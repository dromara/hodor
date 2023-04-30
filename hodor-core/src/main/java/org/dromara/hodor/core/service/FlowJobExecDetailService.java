package org.dromara.hodor.core.service;

import org.dromara.hodor.core.entity.FlowJobExecDetail;
import org.dromara.hodor.model.job.JobKey;

/**
 * FlowJobExecDetailService
 *
 * @author tomgs
 * @since 1.0
 */
public interface FlowJobExecDetailService {

    void createFlowJobExecDetail(FlowJobExecDetail flowJobExecDetail);

    FlowJobExecDetail getRunningFlowJobExecDetail(JobKey jobKey);

    void updateFlowJobExecDetail(FlowJobExecDetail flowJobExecDetail);
}
