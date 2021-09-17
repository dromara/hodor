package org.dromara.hodor.core.service.impl;

import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.compress.Compress;
import org.dromara.hodor.common.compress.CompressFactory;
import org.dromara.hodor.core.dag.FlowData;
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

    private final TypeReference<FlowData> flowDataTypeReference = new TypeReference<FlowData>() {};

    @Override
    public FlowData getFlowData(JobKey jobKey) {
        FlowJobInfo flowJobInfo = flowJobInfoMapper.selectOne(Wrappers.<FlowJobInfo>lambdaQuery()
            .eq(FlowJobInfo::getGroupName, jobKey.getGroupName())
            .eq(FlowJobInfo::getJobName, jobKey.getJobName()));
        if (flowJobInfo == null) {
            return null;
        }
        byte[] flowDataByte = flowJobInfo.getFlowData();
        Integer encType = flowJobInfo.getEncType();
        Compress factory = CompressFactory.getCompress(encType);
        return factory.uncompress(flowDataByte, flowDataTypeReference.getType());
    }

    @Override
    public void createFlowJobInfo(FlowJobInfo flowJobInfo) {
        flowJobInfoMapper.insert(flowJobInfo);
    }

}
