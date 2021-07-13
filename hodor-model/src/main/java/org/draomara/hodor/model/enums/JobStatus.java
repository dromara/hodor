package org.draomara.hodor.model.enums;

/**
 * job status
 *
 * @author tomgs
 * @since 2020/8/26
 */
public enum JobStatus {

    /**
     * ready to running
     */
    READY(0),

    /**
     * running
     */
    RUNNING(1),

    /**
     * stop running
     */
    STOP(2);

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
                status = READY;
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

    public static boolean isReady(int status) {
        return READY.getValue() == status;
    }

    public static boolean isRunning(int status) {
        return RUNNING.getValue() == status;
    }

    public static boolean isStop(int status) {
        return STOP.getValue() == status;
    }

}
