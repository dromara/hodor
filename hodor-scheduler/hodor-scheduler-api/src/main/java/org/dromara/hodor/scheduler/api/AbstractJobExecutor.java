package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;

/**
 *  abstract job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
public abstract class AbstractJobExecutor implements JobExecutor {

    private final HodorExecutor hodorExecutor;

    public AbstractJobExecutor() {
        final int threadSize = Runtime.getRuntime().availableProcessors() * 2;
        this.hodorExecutor = HodorExecutorFactory.createDefaultExecutor("scheduler-exec", threadSize, false);
    }

    @Override
    public void execute(HodorJobExecutionContext context) {
        try {
            hodorExecutor.serialExecute(new HodorRunnable() {
                @Override
                public void execute() {
                    process(context);
                }
            });
        } catch (Exception e) {
            throw new HodorSchedulerException(e);
        }
    }

    public abstract void process(HodorJobExecutionContext context);

}
