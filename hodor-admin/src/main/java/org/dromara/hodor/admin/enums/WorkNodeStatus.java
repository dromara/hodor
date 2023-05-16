package org.dromara.hodor.admin.enums;

public enum WorkNodeStatus {

    INIT(0),//待激活
    SUCCESS(2),//运行
    FINISHED(3);//暂停

    int value;

    WorkNodeStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static WorkNodeStatus to(int value) {
        WorkNodeStatus status = null;
        switch (value) {
            case 0:
                status = INIT;
                break;
            case 2:
                status = SUCCESS;
                break;
            case 3:
                status = FINISHED;
                break;
            default:
                break;
        }
        return status;
    }

    public static boolean isInit(int status) {
        if (INIT.getValue() == status) {
            return true;
        }
        return false;
    }

    public static boolean isInit(WorkNodeStatus status) {
        if (INIT == status) {
            return true;
        }
        return false;
    }

    public static boolean isSuccess(int status) {
        if (SUCCESS.getValue() == status) {
            return true;
        }
        return false;
    }

    public static boolean isSuccess(WorkNodeStatus status) {
        if (SUCCESS == status) {
            return true;
        }
        return false;
    }

    public static boolean isFinished(int status) {
        if (FINISHED.getValue() == status) {
            return true;
        }
        return false;
    }

    public static boolean isFinished(WorkNodeStatus status) {
        if (FINISHED == status) {
            return true;
        }
        return false;
    }

}
