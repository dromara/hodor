package org.dromara.hodor.core.service;

import org.dromara.hodor.core.dag.FlowData;
import org.dromara.hodor.core.entity.FlowJobInfo;
import org.dromara.hodor.model.job.JobKey;

/**
 * FlowJobInfoService
 *
 * @author tomgs
 * @since 1.0
 */
public interface FlowJobInfoService {

    FlowData getFlowData(JobKey jobKey);

    void createFlowJobInfo(FlowJobInfo flowJobInfo);

    void addFlowJob(FlowData flowData);

}
