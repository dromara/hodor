package org.dromara.hodor.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  hodor server properties
 *
 * @author tomgs
 * @version 2020/6/29 1.0 
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "hodor")
public class HodorServerProperties {

    private String logDir;

    private NetServerProperties netServer;

    private RegistryProperties registry;

    public String getRegistryServers() {
        return registry.getServers();
    }

    public String getRegistryNamespace() {
        return registry.getNamespace();
    }

    public String getNetServerHost() {
        return netServer.getHost();
    }

    public Integer getNetServerPort() {
        return netServer.getPort();
    }

    public String getNetServerName() {
        return netServer.getAppName();
    }

    @Data
    static class NetServerProperties {
        private String appName;
        private String host;
        private Integer port;
    }

    @Data
    static class RegistryProperties {
        private String servers;
        private String namespace;
    }

}
