package com.dromara.hodor.server;

import com.dromara.hodor.server.service.RegisterService;
import com.dromara.hodor.server.service.RemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 *  
 *
 * @author tomgs
 * @version 2020/6/29 1.0 
 */
@Slf4j
public class HodorServerInit implements ApplicationRunner {

    private final RemoteService remoteService;
    private final RegisterService registerService;

    public HodorServerInit(final RemoteService remoteService, final RegisterService registerService) {
        this.remoteService = remoteService;
        this.registerService = registerService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // start hodor server
        // start remoting server
        remoteService.start();
        registerService.start();
        // register service
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // log something in here.
            // stop service
            try {
                remoteService.stop();
                registerService.stop();
            } catch (Exception e) {
                log.error("Error where shutting down remote service.", e);
            }
        }));

        log.info("hodor server staring success.");
    }

}
