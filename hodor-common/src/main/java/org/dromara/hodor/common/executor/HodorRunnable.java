package org.dromara.hodor.common.executor;

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
        }

    }

    public abstract void execute() throws Exception;

    public void exceptionCaught(Exception e) {
        //默认不处理,交由子类处理处理任务出现异常时的逻辑
    }

}
