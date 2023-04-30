package org.dromara.hodor.actuator.java;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.HodorActuatorManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * hodor client init
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorJavaActuatorInit implements ApplicationRunner {

    private final HodorActuatorManager actuatorManager;

    public HodorJavaActuatorInit(final HodorActuatorManager actuatorManager) {
        this.actuatorManager = actuatorManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        actuatorManager.start();
    }

}
