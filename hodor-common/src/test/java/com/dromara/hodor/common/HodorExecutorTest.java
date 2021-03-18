package com.dromara.hodor.common;

import java.io.IOException;
import org.dromara.hodor.common.exception.HodorExecutorException;
import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.junit.Test;

/**
 * @author tomgs
 * @since 2021/1/22
 */
public class HodorExecutorTest {

    @Test
    public void testJobExecutor() throws IOException {

        HodorExecutor executor = HodorExecutorFactory.createDefaultExecutor("test", 4, false);

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executor.serialExecute(new HodorRunnable() {
                @Override
                public void execute() throws Exception {
                    System.out.println("" + finalI);
                    //Thread.sleep(new Random().nextInt(2000));
                    //System.out.println("---------");
                    throw new HodorExecutorException("error ...");
                }
            });
        }

        System.in.read();
    }

}
