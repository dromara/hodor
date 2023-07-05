package org.dromara.hodor.register.zookeeper;

import com.google.common.base.Charsets;
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
import org.dromara.hodor.register.api.*;
import org.dromara.hodor.register.api.exception.RegistryException;
import org.dromara.hodor.register.api.node.SchedulerNode;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private RegistryConfig config;

    private final Map<String, TreeCache> caches = new ConcurrentHashMap<>();

    private static final AtomicBoolean isLeader = new AtomicBoolean(false);

    @Override
    public void init(final RegistryConfig config) {
        this.config = config;
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
        if (listener == null) {
            return;
        }
        TreeCache cache = caches.computeIfAbsent(path, key -> {
            TreeCache treeCache = TreeCache.newBuilder(client, path).build();
            try {
                treeCache.start();
            } catch (Exception e) {
                throw new RegistryException(e);
            }
            return treeCache;
        });
        //TreeCache cache = TreeCache.newBuilder(client, path).build();
        cache.getListenable().addListener((curatorFramework, event) -> {
            String dataPath = null == event.getData() ? "" : event.getData().getPath();
            if (dataPath.isEmpty()) {
                return;
            }
            DataChangeEvent changeEvent = new DataChangeEvent(event.getType().name(), event.getData().getPath(), event.getData().getData());
            listener.dataChanged(changeEvent);
        });
    }

    @Override
    public void addConnectionStateListener(ConnectionStateChangeListener listener) {
        if (listener == null) {
            return;
        }
        client.getConnectionStateListenable().addListener(((curatorFramework, connectionState) -> {
            ConnectionState state = ConnectionState.valueOf(connectionState.name());
            listener.stateChanged(state);
        }));
    }

    @Override
    public void executeInLeader(final String latchPath, final LeaderExecutionCallback callback) {
        try (LeaderLatch latch = new LeaderLatch(client, latchPath)) {
            latch.start();
            latch.await();
            callback.execute();
            isLeader.set(true);
            createEphemeral(SchedulerNode.MASTER_ACTIVE_PATH, config.getEndpoint());
        } catch (final Exception ex) {
            RegExceptionHandler.handleException(ex);
        }
        addDataCacheListener(SchedulerNode.MASTER_ACTIVE_PATH, event -> {
            log.info("LeaderElect change event, {}", event);
            if (!SchedulerNode.isMasterActivePath(event.getPath())) {
                return;
            }
            if (event.getType() == DataChangeEvent.Type.NODE_REMOVED
                || event.getType() == DataChangeEvent.Type.NODE_UPDATED) {
                this.executeInLeader(latchPath, callback);
            }
        });
    }

    @Override
    public boolean isLeaderNode() {
        return isLeader.get();
    }

    private String makePath(final String parent, final String firstChild, final String... restChildren) {
        return ZKPaths.makePath(parent, firstChild, restChildren);
    }

    private void makeDirs(final String path) {
        try {
            ZooKeeper zkClient = client.getZookeeperClient().getZooKeeper();
            ZKPaths.mkdirs(zkClient, path);
        } catch (Exception e) {
            RegExceptionHandler.handleException(e);
        }
    }

}
