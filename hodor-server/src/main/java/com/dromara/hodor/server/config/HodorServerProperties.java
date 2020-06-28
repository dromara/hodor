package com.dromara.hodor.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  
 *
 * @author tomgs
 * @version 2020/6/29 1.0 
 */
@Configuration
@ConfigurationProperties(prefix = "hodor")
public class HodorServerProperties {

    @Value("${hodor.net-server.host}")
    @Getter
    private String netServerHost;

    @Value("${hodor.net-server.port}")
    @Getter
    private Integer netServerPort;

    @Value("${hodor.reg.servers}")
    @Getter
    private String registryServers;

    @Value("${hodor.reg.namespace}")
    @Getter
    private String registryNamespace;

}
