package org.dromara.hodor.client.executor;

import java.util.concurrent.CountDownLatch;

/**
 * 执行器服务
 *
 * @author tomgs
 * @since 2021/1/6
 */
public class ExecutorServer implements Runnable {

    private final CountDownLatch countDownLatch;

    public ExecutorServer(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        // 启动成功之后
        countDownLatch.countDown();
    }

}
