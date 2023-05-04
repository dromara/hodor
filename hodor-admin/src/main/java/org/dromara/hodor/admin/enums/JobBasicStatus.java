package org.dromara.hodor.admin.enums;

/**
 * job明细执行状态枚举类
 * @author tomgs
 **/
public enum JobBasicStatus {
    //失败、成功、执行中
    FAIL('0'),
    SUCCESS('1'),
    RUNNING('2'),
    KILLED('3'),
    READY('4'),
    DISABLED('5'),
    PAUSED('6'),
    TIME_OUT('7');

    private char status;

    JobBasicStatus(char status) {
        this.status = status;
    }

    public char get() {
        return status;
    }

    public static char value(char status) {
        if (FAIL.get() == status)
            return FAIL.get();
        return SUCCESS.get();
    }

    public static boolean isFail(char status) {
        return FAIL.get() == status;
    }

    public static boolean isSuccess(char status) {
        return SUCCESS.get() == status;
    }

    public static boolean isRunning(char status) {
        return RUNNING.get() == status;
    }

    public static boolean isReady(char status) {
        return READY.get() == status;
    }

    public static boolean isKilled(char status) {
        return KILLED.get() == status;
    }
}