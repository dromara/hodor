package org.draomara.hodor.model.executor;

/**
 * JobExecuteStatus
 *
 * @author tomgs
 * @since 2021/3/4
 */
public interface JobExecuteStatus {

    /**
     * execute SUCCESS 没有任何消息 表示执行正常
     */
    int SUCCESS = 0;

    /**
     *  execute fail
     */
    int FAILED = 1;

    /**
     * job running
     */
    int RUNNING = 2;

    /**
     *  任务已完成（成功或者失败）
     */
    int FINISHED = 3;

    /**
     *  任务被kill
     */
    int KILLED = 4;

}
