package org.dromara.hodor.actuator.agent;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.HodorActuatorManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * HodorBigdataActuatorInit
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorAgentActuatorInit implements ApplicationRunner {

    private final HodorActuatorManager actuatorManager;

    public HodorAgentActuatorInit(final HodorActuatorManager actuatorManager) {
        this.actuatorManager = actuatorManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("HodorBigdataActuator starting");
        actuatorManager.start();
        log.info("HodorBigdataActuator starting success");
        // add close shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("HodorBigdataActuator closed");
            actuatorManager.close();
        }));
    }

}
