package org.dromara.hodor.server;

import org.dromara.hodor.server.service.HodorService;
import org.dromara.hodor.server.service.RegisterService;
import org.dromara.hodor.server.service.HttpServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *  hodor server init
 *
 * @author tomgs
 * @version 2020/6/29 1.0 
 */
@Slf4j
@Component
public class HodorServerInit implements ApplicationRunner, ApplicationContextAware {

    private final HttpServerService httpServerService;
    private final RegisterService registerService;
    private final HodorService hodorService;
    private final ContextProvider contextProvider;
    private ApplicationContext applicationContext;

    public HodorServerInit(final HttpServerService httpServerService, final RegisterService registerService, final HodorService hodorService) {
        this.httpServerService = httpServerService;
        this.registerService = registerService;
        this.hodorService = hodorService;
        this.contextProvider = ContextProvider.getInstance();
    }

    @Override
    public void run(ApplicationArguments args) {
        contextProvider.setApplicationContext(applicationContext);
        // start hodor server
        // start remoting server
        httpServerService.start();
        registerService.start();
        hodorService.start();
        // register service
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // log something in here.
            // stop service
            try {
                hodorService.stop();
                registerService.stop();
                httpServerService.stop();
            } catch (Exception e) {
                log.error("Error where shutting down remote service.", e);
            }
        }));

        log.info("hodor server staring success.");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
