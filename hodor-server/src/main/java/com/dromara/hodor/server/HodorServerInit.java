package com.dromara.hodor.server;

import com.dromara.hodor.server.service.HodorService;
import com.dromara.hodor.server.service.RegisterService;
import com.dromara.hodor.server.service.RemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 *  hodor server init
 *
 * @author tomgs
 * @version 2020/6/29 1.0 
 */
@Slf4j
public class HodorServerInit implements ApplicationRunner {

    private final RemoteService remoteService;
    private final RegisterService registerService;
    private final HodorService hodorService;

    public HodorServerInit(final RemoteService remoteService, final RegisterService registerService, HodorService hodorService) {
        this.remoteService = remoteService;
        this.registerService = registerService;
        this.hodorService = hodorService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // start hodor server
        // start remoting server
        remoteService.start();
        registerService.start();
        hodorService.start();
        // register service
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // log something in here.
            // stop service
            try {
                hodorService.stop();
                registerService.stop();
                remoteService.stop();
            } catch (Exception e) {
                log.error("Error where shutting down remote service.", e);
            }
        }));

        log.info("hodor server staring success.");
    }

}
