package org.dromara.hodor.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * hodor server configuration
 * @author tomgs
 * @since 2020/6/28
 */
@Slf4j
@Configuration
public class HodorServerConfig {

    private final HodorServerProperties properties;

    public HodorServerConfig(final HodorServerProperties properties) {
        this.properties = properties;
    }

}
