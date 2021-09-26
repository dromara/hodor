package org.dromara.hodor.register.api;

import lombok.Builder;
import lombok.Getter;

/**
 * 注册中心配置
 *
 * @author tomgs
 * @since 2020/06/28
 */
@Getter
@Builder
public class RegistryConfig {
    /**
     * 注册中心节点列表，如zk:127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
     */
    private final String servers;

    /**
     * 应用在注册中心的命名空间
     */
    private final String namespace;
}
