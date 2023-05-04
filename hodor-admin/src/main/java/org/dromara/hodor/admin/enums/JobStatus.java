package org.dromara.hodor.admin.enums;

/**
 *  job当前的状态，注意这个状态不是job执行中的状态，而是这个job任务的状态。
 *  比如说这个job是新增的在还没有运行过的就是JobStatusEnum.INACTIVATED待激活状态，如果这个job运行之后则设置为
 *  JobStatus.RUNNING可运行状态，如果这个job停止或被暂停了那么这个job将被设置为JobStatusEnum.STOP停止状态。
 */
public enum JobStatus {

    INACTIVATED(0),//待激活
    RUNNING(1),//运行
    STOP(3);//暂停

    int value;

    JobStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static JobStatus to(int value) {
        JobStatus status = null;
        switch (value) {
            case 0:
                status = INACTIVATED;
                break;
            case 1:
                status = RUNNING;
                break;
            case 3:
                status = STOP;
                break;
            default:
                break;
        }
        return status;
    }

    public static boolean isInactivated(int status) {
        if (INACTIVATED.getValue() == status) {
            return true;
        }
        return false;
    }

    public static boolean isRunning(int status) {
        if (RUNNING.getValue() == status) {
            return true;
        }
        return false;
    }

    public static boolean isStop(int status) {
        if (STOP.getValue() == status) {
            return true;
        }
        return false;
    }

}

