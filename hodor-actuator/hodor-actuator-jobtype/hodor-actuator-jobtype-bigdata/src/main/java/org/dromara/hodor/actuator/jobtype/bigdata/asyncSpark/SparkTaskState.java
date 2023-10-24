package org.dromara.hodor.actuator.jobtype.bigdata.asyncSpark;

import lombok.Data;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;

/**
 * SparkTaskState
 *
 * @author tomgs
 * @since 1.0
 **/
@Data
public class SparkTaskState {

    private String appId;

    private float progress;

    private YarnApplicationState state;

    private FinalApplicationStatus finalStatus;
}
