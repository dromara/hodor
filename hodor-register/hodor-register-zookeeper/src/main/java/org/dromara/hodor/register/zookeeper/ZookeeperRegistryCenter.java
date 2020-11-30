package org.dromara.hodor.register.zookeeper;

import com.google.common.base.Charsets;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.LeaderExecutionCallback;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.RegistryConfig;
import org.dromara.hodor.register.api.exception.RegistryException;

/**
 * registry center implements by zookeeper
 *
 * @author tomgs
 * @version 2020/6/26 1.0
 */
@Join
@Slf4j
public class ZookeeperRegistryCenter implements RegistryCenter {

    private CuratorFramework client;
    private final Map<String, TreeCache> caches = new ConcurrentHashMap<>();

    @Override
    public void init(final RegistryConfig config) {
        log.info("zookeeper registry center init, server list is {}.", config.getServers());
        // TODO: 这里配置先简单处理
        client = CuratorFrameworkFactory.builder()
            .connectString(config.getServers())
            .namespace(config.getNamespace())
            .retryPolicy(new ExponentialBackoffRetry(1000, 3, 3000))
            .sessionTimeoutMs(30000)
            .connectionTimeoutMs(5000)
            .build();
        client.start();
        try {
            if (!client.blockUntilConnected(3000 * 3, TimeUnit.MILLISECONDS)) {
                client.close();
                throw new KeeperException.OperationTimeoutException();
            }
        } catch (final Exception e) {
            throw new RegistryException(e);
        }
    }

    @Override
    public void close() {
        for (Map.Entry<String, TreeCache> each : caches.entrySet()) {
            each.getValue().close();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignore) {
        }
        CloseableUtils.closeQuietly(client);
    }

    @Override
    public boolean checkExists(final String key) {
        try {
            return null != client.checkExists().forPath(key);
        } catch (Exception e) {
            RegExceptionHandler.handleException(e);
        }
        return false;
    }

    @Override
    public String get(final String key) {
        try {
            return new String(client.getData().forPath(key), StandardCharsets.UTF_8);
        } catch (Exception e) {
            RegExceptionHandler.handleException(e);
        }
        return null;
    }

    @Override
    public List<String> getChildren(final String key) {
        try {
            return client.getChildren().forPath(key);
        } catch (Exception e) {
            RegExceptionHandler.handleException(e);
        }
        return Collections.emptyList();
    }

    @Override
    public void createPersistent(final String key, final String value) {
        try {
            if (value == null) {
                makeDirs(key);
                return;
            }
            if (!checkExists(key)) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key, value.getBytes(Charsets.UTF_8));
            } else {
                update(key, value);
            }
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
    }

    @Override
    public void createEphemeral(final String key, final String value) {
        try {
            if (checkExists(key)) {
                client.delete().deletingChildrenIfNeeded().forPath(key);
            }
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key, value.getBytes(Charsets.UTF_8));
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
    }

    @Override
    public void createEphemeralSequential(final String key, final String value) {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(key);
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
    }

    @Override
    public String makePath(final String parent, final String firstChild, final String... restChildren) {
        return ZKPaths.makePath(parent, firstChild, restChildren);
    }

    @Override
    public void makeDirs(final String path) {
        try {
            ZooKeeper zkClient = client.getZookeeperClient().getZooKeeper();
            ZKPaths.mkdirs(zkClient, path);
        } catch (Exception e) {
            RegExceptionHandler.handleException(e);
        }
    }

    @Override
    public void update(final String key, final String value) {
        try {
            client.inTransaction().check().forPath(key).and().setData().forPath(key, value.getBytes(Charsets.UTF_8)).and().commit();
        } catch (final Exception e) {
            RegExceptionHandler.handleException(e);
        }
    }

    @Override
    public void remove(final String key) {
        try {
            if (checkExists(key)) {
                client.delete().deletingChildrenIfNeeded().forPath(key);
            }
        } catch (Exception e) {
            RegExceptionHandler.handleException(e);
        }
    }

    @Override
    public void addDataCacheListener(final String path, final DataChangeListener listener) {
        TreeCache cache = TreeCache.newBuilder(client, path).build();
        try {
            cache.start();
        } catch (Exception e) {
            throw new RegistryException(e);
        }
        cache.getListenable().addListener((curatorFramework, event) -> {
            if (listener == null) {
                return;
            }
            String dataPath = null == event.getData() ? "" : event.getData().getPath();
            if (dataPath.isEmpty()) {
                return;
            }
            DataChangeEvent changeEvent = new DataChangeEvent(event.getType().name(), event.getData().getPath(), event.getData().getData());
            listener.dataChanged(changeEvent);
        });
        caches.put(path, cache);
    }

    @Override
    public void executeInLeader(final String latchPath, final LeaderExecutionCallback callback) {
        try (LeaderLatch latch = new LeaderLatch(client, latchPath)) {
            latch.start();
            latch.await();
            callback.execute();
        } catch (final Exception ex) {
            RegExceptionHandler.handleException(ex);
        }
    }

}
