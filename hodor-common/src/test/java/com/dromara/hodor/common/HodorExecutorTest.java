package com.dromara.hodor.common;

import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.queue.CircleQueue;
import org.dromara.hodor.common.queue.ResizeQueuePolicy;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author tomgs
 * @since 2021/1/22
 */
public class HodorExecutorTest {

    @Test
    public void testJobExecutor() throws IOException {

        ExecutorService demoExecutor = Executors.newFixedThreadPool(10);

        CircleQueue<HodorRunnable> queue = new CircleQueue<>(16, new ResizeQueuePolicy<>());
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(10);
        HodorExecutor executor = new HodorExecutor();
        executor.setCircleQueue(queue);
        executor.setExecutor(threadPoolExecutor);

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executor.serialExecute(new HodorRunnable() {
                @Override
                public void execute() throws Exception {
                    System.out.println("" + finalI);
                    //Thread.sleep(new Random().nextInt(2000));
                    //System.out.println("---------");
                }
            });
        }

        System.in.read();
    }

}
