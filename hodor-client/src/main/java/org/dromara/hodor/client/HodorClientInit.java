package org.dromara.hodor.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.executor.ExecutorServer;
import org.dromara.hodor.client.executor.MsgSender;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
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

    private final String interval;

    private final ExecutorServer executorServer;

    private final MsgSender msgSender;

    private final ScheduledExecutorService heartbeatSenderService;

    public HodorClientInit() {
        this.interval = System.getProperty("hodor.heartbeat.interval", "3000");

        this.executorServer = new ExecutorServer();

        this.msgSender = new MsgSender();

        this.heartbeatSenderService = new ScheduledThreadPoolExecutor(2,
            HodorThreadFactory.create("hodor-heartbeat-sender", true),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO: 这里可以改造为SPI的方式加载服务
        // start executor server
        log.info("HodorClient starting executor server...");
        startExecutorServer();

        // start heartbeat sender server
        log.info("HodorClient starting heartbeat sender server...");
        startHeartbeatSender();

        // start register jobs after executor server start success
        log.info("HodorClient starting register jobs...");
        registerJobs();

        // add close shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void startExecutorServer() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread executorServerThread = new Thread(() -> {
            executorServer.start();
            countDownLatch.countDown();
        }, "hodor-scheduler-executor-server");
        executorServerThread.setDaemon(true);
        executorServerThread.start();
        // wait
        countDownLatch.await();
    }

    private void startHeartbeatSender() {
        // 第一次初始化
        MsgSender.HeartbeatSender heartbeatSender = msgSender.getHeartbeatSender();
        heartbeatSender.run();
        heartbeatSenderService.scheduleAtFixedRate(heartbeatSender, 3_000, Integer.parseInt(interval), TimeUnit.MILLISECONDS);
    }

    private void registerJobs() {
        JobRegistrar jobRegistrar = ServiceProvider.getInstance().getBean(JobRegistrar.class);
        jobRegistrar.registerJobs();
    }

    public void close() {
        // 发送下线通知
        msgSender.getNodeOfflineSender().run();
        // 关闭相应服务
        executorServer.close();
        heartbeatSenderService.shutdown();
    }

}
