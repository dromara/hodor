package org.dromara.hodor.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.dag.CompressDataFactory;
import org.dromara.hodor.core.dag.NodeBean;
import org.dromara.hodor.core.entity.FlowJobInfo;
import org.dromara.hodor.core.mapper.FlowJobInfoMapper;
import org.dromara.hodor.core.service.FlowJobInfoService;
import org.dromara.hodor.model.job.JobKey;
import org.springframework.stereotype.Service;

/**
 * flow job info service impl
 *
 * @author tomgs
 * @since 2021/9/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowJobInfoServiceImpl implements FlowJobInfoService {

    private final FlowJobInfoMapper flowJobInfoMapper;

    @Override
    public NodeBean getFlowJobInfo(JobKey jobKey) {
        FlowJobInfo flowJobInfo = flowJobInfoMapper.selectOne(Wrappers.<FlowJobInfo>lambdaQuery()
            .eq(FlowJobInfo::getGroupName, jobKey.getGroupName())
            .eq(FlowJobInfo::getJobName, jobKey.getJobName()));
        if (flowJobInfo == null) {
            return null;
        }
        byte[] flowData = flowJobInfo.getFlowData();
        Integer encType = flowJobInfo.getEncType();
        CompressDataFactory factory = CompressDataFactory.getFactory(encType);
        NodeBean nodeBean = factory.uncompress(flowData);
        // nodeBean.setNodes();
        return nodeBean;
    }

    @Override
    public void createFlowJobInfo(NodeBean nodeBean) {

    }

}
