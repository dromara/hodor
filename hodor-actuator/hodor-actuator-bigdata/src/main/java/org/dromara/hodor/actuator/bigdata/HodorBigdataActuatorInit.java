package org.dromara.hodor.actuator.bigdata;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.common.HodorActuatorManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * HodorBigdataActuatorInit
 *
 * @author tomgs
 * @since 2021/1/6
 */
@Slf4j
public class HodorBigdataActuatorInit implements ApplicationRunner {

    private final HodorActuatorManager actuatorManager;

    public HodorBigdataActuatorInit(final HodorActuatorManager actuatorManager) {
        this.actuatorManager = actuatorManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        actuatorManager.start();
    }

}
