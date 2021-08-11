package org.dromara.hodor.server;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.server.service.HodorService;
import org.dromara.hodor.server.service.RegisterService;
import org.dromara.hodor.server.service.RestServerService;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
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

    private final RestServerService restServerService;
    private final RegisterService registerService;
    private final HodorService hodorService;
    private final ServiceProvider serviceProvider;
    private ApplicationContext applicationContext;

    public HodorServerInit(final RestServerService restServerService, final RegisterService registerService, final HodorService hodorService) {
        this.restServerService = restServerService;
        this.registerService = registerService;
        this.hodorService = hodorService;
        this.serviceProvider = ServiceProvider.getInstance();
    }

    @Override
    public void run(ApplicationArguments args) {
        serviceProvider.setApplicationContext(applicationContext);
        // start hodor server
        // start remoting server
        restServerService.start();
        registerService.start();
        hodorService.start();
        // register service
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // log something in here.
            // stop service
            try {
                hodorService.stop();
                registerService.stop();
                restServerService.stop();
            } catch (Exception e) {
                log.error("Error where shutting down remote service.", e);
            }
        }));

        log.info("hodor server staring success.");
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
