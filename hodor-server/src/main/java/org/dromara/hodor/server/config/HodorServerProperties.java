package org.dromara.hodor.server.config;

import lombok.Data;
import org.dromara.hodor.cache.api.CacheSourceConfig;
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

    private String clusterName;

    private int clusterNodes;

    private NetServerProperties netServer;

    private RegistryProperties registry;

    private CacheSourceConfig cacheSource;

    public String getRegistryServers() {
        return registry.getServers();
    }

    public String getRegistryNamespace() {
        return registry.getNamespace();
    }

    public String getRegistryEndpoint() {
        return registry.getEndpoint();
    }

    public String getRegistryDataPath() {
        return registry.getDataPath();
    }

    public String getNetServerHost() {
        return netServer.getHost();
    }

    public Integer getNetServerPort() {
        return netServer.getPort();
    }

    public String getClusterServerName() {
        return this.clusterName;
    }

    @Data
    static class NetServerProperties {
        private String host;
        private Integer port;
    }

    @Data
    static class RegistryProperties {
        private String type;
        private String servers;
        private String namespace;
        private String endpoint;
        private String dataPath;
    }

}
