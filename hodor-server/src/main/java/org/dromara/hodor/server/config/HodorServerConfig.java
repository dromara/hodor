package org.dromara.hodor.server.config;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ContextPathCompositeHandler;
import org.springframework.http.server.reactive.HttpHandler;

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

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory webServerFactory = new NettyReactiveWebServerFactory() {
            @Override
            public WebServer getWebServer(HttpHandler httpHandler) {
                Map<String, HttpHandler> handlerMap = new HashMap<>();
                handlerMap.put("/api", httpHandler);
                return super.getWebServer(new ContextPathCompositeHandler(handlerMap));
            }
        };
        webServerFactory.addServerCustomizers(serverCustomizer());
        return webServerFactory;
    }

    public NettyServerCustomizer serverCustomizer() {
        log.info("hodor sever endpoint: {}:{}", properties.getNetServerHost(), properties.getNetServerPort());
        return httpServer -> httpServer.host(properties.getNetServerHost()).port(properties.getNetServerPort());
    }

}
