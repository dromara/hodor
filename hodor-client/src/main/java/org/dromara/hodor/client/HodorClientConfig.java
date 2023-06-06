package org.dromara.hodor.client;

import lombok.Data;

/**
 * hodor client config
 *
 * @author tomgs
 * @since 1.0
 */
@Data
public class HodorClientConfig {

    /**
     * 应用名称，这个用于和用户进行关联，需要先在管理平台进行注册分配
     */
    private String appName;

    /**
     * hodor分配的客户端密钥key，需要先在管理平台进行注册分配
     */
    private String appKey;

    /**
     * 任务注册地址
     */
    private String registryAddress;

}
