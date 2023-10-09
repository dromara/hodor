package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;

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
        hodorExecutor.serialExecute(new HodorRunnable() {
            @Override
            public void execute() {
                try {
                    preProcess(context);
                    process(context);
                } catch (Exception e) {
                    exceptionProcess(context, e);
                }
            }
        });

    }

    public abstract void preProcess(HodorJobExecutionContext context);

    public abstract void process(HodorJobExecutionContext context);

    public abstract void exceptionProcess(HodorJobExecutionContext context, Exception e);
}
