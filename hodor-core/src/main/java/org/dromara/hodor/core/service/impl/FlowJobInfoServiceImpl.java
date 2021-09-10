package org.dromara.hodor.core.service.impl;

import org.dromara.hodor.core.dag.NodeBean;
import org.dromara.hodor.core.service.FlowJobInfoService;
import org.dromara.hodor.model.job.JobKey;
import org.springframework.stereotype.Service;

/**
 * flow job info service impl
 *
 * @author tomgs
 * @since 2021/9/10
 */
@Service
public class FlowJobInfoServiceImpl implements FlowJobInfoService {

    @Override
    public NodeBean getFlowJobInfo(JobKey jobKey) {
        return null;
    }

    @Override
    public void createFlowJobInfo(NodeBean nodeBean) {

    }

}
