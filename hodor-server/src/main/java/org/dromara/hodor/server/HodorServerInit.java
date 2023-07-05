package org.dromara.hodor.server;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.server.service.HodorService;
import org.dromara.hodor.server.service.RegistryService;
import org.dromara.hodor.server.service.RestServerService;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * hodor server init
 *
 * @author tomgs
 * @version 2020/6/29 1.0
 */
@Slf4j
@Component
public class HodorServerInit implements ApplicationRunner, ApplicationContextAware {

    private final RestServerService restServerService;

    private final RegistryService registryService;

    private final HodorService hodorService;

    private final ServiceProvider serviceProvider;

    public HodorServerInit(final RestServerService restServerService,
                           final RegistryService registryService,
                           final HodorService hodorService) {
        this.restServerService = restServerService;
        this.registryService = registryService;
        this.hodorService = hodorService;
        this.serviceProvider = ServiceProvider.getInstance();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // register service
        registryService.start();
        // start hodor server
        hodorService.start();
        // start remoting server
        restServerService.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // log something in here.
            log.info("Hodor server shutting down ...");
            // stop service
            try {
                restServerService.stop();
                hodorService.stop();
                registryService.stop();
                log.info("Hodor server shutdown complete ...");
            } catch (Exception e) {
                log.error("Error where shutting down remote service.", e);
            }
        }, "HodorServerShutdownHook"));

        log.info("Hodor server starting success.");
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        serviceProvider.setApplicationContext(applicationContext);
    }

}
