package org.dromara.hodor.actuator.bigdata.jobtype.asyncSpark;

import lombok.Builder;
import lombok.Data;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;

/**
 * @author tangzhongyuan
 * @create 2019-03-06 18:57
 **/
@Data
@Builder
public class SparkTaskState {

    private String appId;
    private YarnApplicationState state;
    private float progress;
    private FinalApplicationStatus finalStatus;
}
