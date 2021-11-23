package org.dromara.hodor.actuator.java;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.common.HodorActuatorManager;
import org.dromara.hodor.actuator.java.core.JobRegistrar;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * hodor client init
 *
 * @author tomgs
 * @since 2021/1/6
 */
@Slf4j
public class HodorJavaActuatorInit implements ApplicationRunner {

    private final JobRegistrar jobRegistrar;

    private final HodorActuatorManager actuatorManager;

    public HodorJavaActuatorInit(final JobRegistrar jobRegistrar, final HodorActuatorManager actuatorManager) {
        this.jobRegistrar = jobRegistrar;
        this.actuatorManager = actuatorManager;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        actuatorManager.start();
        // start register jobs after executor server start success
        log.info("HodorClient starting register jobs...");
        jobRegistrar.registerJobs();

        // add close shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public void close() {
        log.info("Shutdown server ...");
        actuatorManager.close();
        jobRegistrar.clear();
    }

}
