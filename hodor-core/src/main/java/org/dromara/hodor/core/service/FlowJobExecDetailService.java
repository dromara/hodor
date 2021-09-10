package org.dromara.hodor.core.service;

import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.model.job.JobKey;

/**
 * FlowJobExecDetailService
 *
 * @author tomgs
 * @since 2021/9/10
 */
public interface FlowJobExecDetailService {

    void createFlowJobExecDetail(Dag dagInstance);

    Dag getFlowJobExecDetail(JobKey jobKey);

    void updateFlowJobExecDetail(Dag dag);
}
