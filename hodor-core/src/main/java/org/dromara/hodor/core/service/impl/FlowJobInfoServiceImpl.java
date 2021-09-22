package org.dromara.hodor.core.service.impl;

import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.compress.Compress;
import org.dromara.hodor.common.compress.CompressFactory;
import org.dromara.hodor.common.compress.EncType;
import org.dromara.hodor.common.cron.CronUtils;
import org.dromara.hodor.common.utils.HashUtils;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.common.utils.TypedMapWrapper;
import org.dromara.hodor.core.Constants;
import org.dromara.hodor.core.dag.FlowData;
import org.dromara.hodor.core.entity.FlowJobInfo;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.mapper.FlowJobInfoMapper;
import org.dromara.hodor.core.service.FlowJobInfoService;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.model.job.JobKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final JobInfoServiceImpl jobInfoService;

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

    @Override
    @Transactional
    public void addFlowJob(FlowData flowData) {
        String groupName = flowData.getGroupName();
        String jobName = flowData.getJobName();
        Map<String, Object> config = flowData.getConfig();
        TypedMapWrapper<String, Object> jobConfigWrapper = new TypedMapWrapper<>(config);

        JobInfo jobInfo = new JobInfo();
        jobInfo.setGroupName(groupName);
        jobInfo.setJobName(jobName);
        jobInfo.setJobType(JobType.WORKFLOW_JOB);
        jobInfo.setCron(jobConfigWrapper.getString(Constants.CRON_KEY, CronUtils.CRON_DISABLED));
        jobInfo.setJobStatus(JobStatus.READY);
        jobInfo.setHashId(HashUtils.hash(jobInfo.getGroupName() + jobInfo.getJobName()));
        jobInfoService.addJobIfAbsent(jobInfo);

        FlowJobInfo flowJobInfo = new FlowJobInfo();
        flowJobInfo.setGroupName(groupName);
        flowJobInfo.setJobName(jobName);
        flowJobInfo.setCreateTime(new Date());
        flowJobInfo.setEncType(EncType.PLAIN.getType());
        flowJobInfo.setFlowData(SerializeUtils.serialize(flowData));
        this.createFlowJobInfo(flowJobInfo);
    }

}
