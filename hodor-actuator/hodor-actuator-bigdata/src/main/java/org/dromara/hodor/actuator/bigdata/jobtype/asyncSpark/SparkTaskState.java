package org.dromara.hodor.actuator.bigdata.jobtype.asyncSpark;

import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;

/**
 * SparkTaskState
 *
 * @author tomgs
 * @since 1.0
 **/
public class SparkTaskState {

    private String appId;

    private YarnApplicationState state;

    private float progress;

    private FinalApplicationStatus finalStatus;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public YarnApplicationState getState() {
        return state;
    }

    public void setState(YarnApplicationState state) {
        this.state = state;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public FinalApplicationStatus getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(FinalApplicationStatus finalStatus) {
        this.finalStatus = finalStatus;
    }
}
