package org.dromara.hodor.actuator.bigdata;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.HodorActuatorManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.CountDownLatch;

/**
 * HodorBigdataActuatorInit
 *
 * @author tomgs
 * @since 2021/1/6
 */
@Slf4j
public class HodorBigdataActuatorInit implements ApplicationRunner {

    private final CountDownLatch aliveLatch = new CountDownLatch(1);

    private final HodorActuatorManager actuatorManager;

    public HodorBigdataActuatorInit(final HodorActuatorManager actuatorManager) {
        this.actuatorManager = actuatorManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        actuatorManager.start();
        // add close shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            actuatorManager.close();
            aliveLatch.countDown();
        }));
        aliveLatch.await();
    }

}
