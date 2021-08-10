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

    void init(RegistryConfig config);

    void close();

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

    /**
     * 传入多个节点路径，组成一个全路径
     */
    String makePath(String parent, String firstChild, String... restChildren);

    /**
     * 创建一个全路径的所有节点
     * @param path path
     */
    void makeDirs(String path);

    void update(String key, String value);

    void remove(String key);

    void addDataCacheListener(String path, DataChangeListener listener);

    void executeInLeader(String latchPath, LeaderExecutionCallback callback);

}
