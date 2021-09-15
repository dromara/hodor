package org.dromara.hodor.core.service.impl;

import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.core.service.FlowJobExecDetailService;
import org.dromara.hodor.model.job.JobKey;
import org.springframework.stereotype.Service;

/**
 * flow job exec detail service impl
 *
 * @author tomgs
 * @since 2021/9/10
 */
@Service
public class FlowJobExecDetailServiceImpl implements FlowJobExecDetailService {

    @Override
    public void createFlowJobExecDetail(Dag dagInstance) {

    }

    @Override
    public Dag getFlowJobExecDetail(JobKey jobKey) {
        return null;
    }

    @Override
    public void updateFlowJobExecDetail(Dag dagInstance) {

    }

}
