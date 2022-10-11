package org.dromara.hodor.register.api;

import java.util.List;
import org.dromara.hodor.common.extension.SPI;

/**
 * 注册中心
 *
 * @author tomgs
 * @version 2020/6/26 1.0
 */
@SPI("register")
public interface RegistryCenter {

    void init(RegistryConfig config) throws Exception;

    void close() throws Exception;

    boolean checkExists(String key);

    String get(String key);

    List<String> getChildren(String key);

    void createPersistent(String key, String value);

    /**
     * 创建持久化有序节点
     * @param path 路径
     * @param value 值
     */
    void createPersistentSequential(String path, String value);

    void createEphemeral(String key, String value);

    void createEphemeralSequential(String key, String value);

    void update(String key, String value);

    void remove(String key);

    void addDataCacheListener(String path, DataChangeListener listener);

    void addConnectionStateListener(ConnectionStateChangeListener listener);

    void executeInLeader(String latchPath, LeaderExecutionCallback callback);

}
