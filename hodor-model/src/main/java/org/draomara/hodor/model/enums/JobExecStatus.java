package org.draomara.hodor.model.enums;

/**
 * job executing status
 *
 * @author tomgs
 * @since 2020/8/26
 */
public enum JobExecStatus {

    /**
     * 任务提交成功，等待运行
     */
    PENDING(0),

    /**
     * 任务正在运行
     */
    RUNNING(1),

    /**
     * 任务运行成功
     */
    SUCCEEDED(2),

    /**
     * 任务运行失败
     */
    FAILED(3),

    /**
     * 任务被手动停止
     */
    KILLED(4),

    /**
     * 任务运行超时
     */
    TIMEOUT(5),

    /**
     * 任务在提交的过程出现错误
     */
    ERROR(6);

    private int status;

    JobExecStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static boolean isFailed(int status) {
        return FAILED.getStatus() == status;
    }

    public static boolean isSucceed(int status) {
        return SUCCEEDED.getStatus() == status;
    }

    public static boolean isRunning(int status) {
        return RUNNING.getStatus() == status;
    }

    public static boolean isPending(int status) {
        return PENDING.getStatus() == status;
    }

    public static boolean isKilled(int status) {
        return KILLED.getStatus() == status;
    }

    public static boolean isTimeout(int status) {
        return TIMEOUT.getStatus() == status;
    }

    public static boolean isError(int status) {
        return ERROR.getStatus() == status;
    }

}
