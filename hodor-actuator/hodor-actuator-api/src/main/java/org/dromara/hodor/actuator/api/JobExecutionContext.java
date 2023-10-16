package org.dromara.hodor.actuator.api;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dromara.hodor.actuator.api.core.JobLogger;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.model.enums.JobExecuteStatus;

/**
 * job execution context
 *
 * @author tomgs
 * @since 1.0
 */
@Getter
@RequiredArgsConstructor
public class JobExecutionContext {

    private final JobLogger jobLogger;

    private final JobParameter jobParameter;

    // 上游任务结果数据
    private final Object parentJobData;

    //上游任务的结果 Map<requestId, result>
    private final Map<Long, Object> parentJobExecuteResults;

    //上游任务的状态 Map<requestId, executeStatus>
    private final Map<Long, JobExecuteStatus> parentJobExecuteStatuses;

    public <T> T getParentJobData(Class<T> tClass) {
        return Utils.Jsons.toBean(Utils.Jsons.toJson(parentJobData), tClass);
    }

    @Override
    public String toString() {
        return "JobExecutionContext{" +
            "jobLogger=" + jobLogger +
            ", jobParameter=" + jobParameter +
            '}';
    }
}
