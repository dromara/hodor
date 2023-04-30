package org.dromara.hodor.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.exception.KeyAlreadyExistsException;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.core.entity.FlowJobExecDetail;
import org.dromara.hodor.core.mapper.FlowJobExecDetailMapper;
import org.dromara.hodor.core.service.FlowJobExecDetailService;
import org.dromara.hodor.model.job.JobKey;
import org.springframework.stereotype.Service;

/**
 * flow job exec detail service impl
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowJobExecDetailServiceImpl implements FlowJobExecDetailService {

    private final FlowJobExecDetailMapper flowJobExecDetailMapper;

    @Override
    public void createFlowJobExecDetail(FlowJobExecDetail flowJobExecDetail) {
        Integer count = flowJobExecDetailMapper.selectCount(Wrappers.<FlowJobExecDetail>lambdaQuery()
            .eq(FlowJobExecDetail::getGroupName, flowJobExecDetail.getGroupName())
            .eq(FlowJobExecDetail::getJobName, flowJobExecDetail.getJobName())
            .eq(FlowJobExecDetail::getStatus, Status.RUNNING));
        if (count > 0) {
            throw new KeyAlreadyExistsException(StringUtils.format("create job instance exception, job {} has running already.",
                JobKey.of(flowJobExecDetail.getGroupName(), flowJobExecDetail.getJobName())));
        }

        flowJobExecDetailMapper.insert(flowJobExecDetail);
    }

    @Override
    public FlowJobExecDetail getRunningFlowJobExecDetail(JobKey jobKey) {
        return flowJobExecDetailMapper.selectOne(Wrappers.<FlowJobExecDetail>lambdaQuery()
            .eq(FlowJobExecDetail::getGroupName, jobKey.getGroupName())
            .eq(FlowJobExecDetail::getJobName, jobKey.getJobName())
            .eq(FlowJobExecDetail::getStatus, Status.RUNNING));
    }

    @Override
    public void updateFlowJobExecDetail(FlowJobExecDetail flowJobExecDetail) {
        flowJobExecDetailMapper.update(flowJobExecDetail, Wrappers.<FlowJobExecDetail>lambdaUpdate()
            .eq(FlowJobExecDetail::getRequestId, flowJobExecDetail.getRequestId()));
    }

}
