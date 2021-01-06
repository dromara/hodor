package org.dromara.hodor.client;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.executor.ExecutorServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * hodor client init
 *
 * @author tomgs
 * @since 2021/1/6
 */
@Slf4j
public class HodorClientInit implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // start executor server
        log.info("HodorClient starting executor server...");
        startExecutorServer();
        // start heartbeat sender server
        log.info("HodorClient starting heartbeat sender server...");
        startHeartbeatSender();
        // start register jobs after executor server start success
        log.info("HodorClient starting register jobs...");
        registerJobs();
    }

    private void startExecutorServer() {
        Thread executorServerThread = new Thread(new ExecutorServer(), "hodor-scheduler-executor-server");
        executorServerThread.setDaemon(true);
        executorServerThread.start();
        // wait
    }

    private void startHeartbeatSender() {

    }

    private void registerJobs() {
        JobRegistrar jobRegistrar = ServiceProvider.getInstance().getBean(JobRegistrar.class);
        jobRegistrar.registerJobs();
    }

}
