package org.dromara.hodor.actuator.api.core;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.dromara.hodor.actuator.api.ExecutableJob;
import org.dromara.hodor.model.job.JobDesc;

/**
 * job instance
 *
 * @author tomgs
 * @since 1.0
 */
@Builder
@Getter
@ToString
public class JobInstance {

    /**
     * 任务描述信息
     */
    private final JobDesc jobDesc;

    /**
     * 可执行任务
     */
    private final ExecutableJob jobRunnable;

}
