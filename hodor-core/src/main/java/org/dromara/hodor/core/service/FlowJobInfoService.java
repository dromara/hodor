package org.dromara.hodor.core.service;

import org.dromara.hodor.core.dag.NodeBean;
import org.dromara.hodor.model.job.JobKey;

/**
 * FlowJobInfoService
 *
 * @author tomgs
 * @since 2021/9/10
 */
public interface FlowJobInfoService {

    NodeBean getFlowJobInfo(JobKey jobKey);

    void createFlowJobInfo(NodeBean nodeBean);
}
