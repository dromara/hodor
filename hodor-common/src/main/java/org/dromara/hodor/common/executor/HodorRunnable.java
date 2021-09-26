package org.dromara.hodor.common.executor;

import org.dromara.hodor.common.exception.HodorExecutorException;

/**
 * hodor runnable
 *
 * @author tomgs
 * @since 2021/1/21
 */
public abstract class HodorRunnable implements Runnable {

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            exceptionCaught(e);
        } finally {
            afterProcess();
        }

    }

    /**
     * 具体任务执行逻辑
     *
     * @throws Exception 任务执行异常
     */
    public abstract void execute() throws Exception;

    /**
     * 自定义任务异常处理
     *
     * @param e 任务执行异常
     */
    public void exceptionCaught(Exception e) {
        //默认不处理,交由子类处理处理任务出现异常时的逻辑
        throw new HodorExecutorException(e);
    }

    /**
     * 任务执行完成处理
     */
    public void afterProcess() {
        // 默认不处理，交由子类处理
    }

}
