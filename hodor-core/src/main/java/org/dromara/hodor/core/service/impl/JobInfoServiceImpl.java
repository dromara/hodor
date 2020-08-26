package org.dromara.hodor.core.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.JobInfo;
import org.dromara.hodor.core.entity.HodorJobInfo;
import org.dromara.hodor.core.enums.JobStatus;
import org.dromara.hodor.core.mapper.JobInfoMapper;
import org.dromara.hodor.core.service.JobInfoService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * job info service
 *
 * @author tomgs
 * @since 2020/6/30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobInfoServiceImpl implements JobInfoService {

    private @NonNull JobInfoMapper jobInfoMapper;

    @Override
    public Integer queryAssignableJobCount() {
        return jobInfoMapper.selectCount(Wrappers.<HodorJobInfo>lambdaQuery()
            .eq(HodorJobInfo::getJobStatus, JobStatus.READY)
            .or()
            .eq(HodorJobInfo::getJobStatus, JobStatus.RUNNING));
    }

    @Override
    public Integer queryJobHashIdByOffset(Integer offset) {
        return null;
    }

    @Override
    public Integer queryJobIdByOffset(Integer offset) {
        return null;
    }

    @Override
    public List<JobInfo> queryJobInfoByOffset(Integer start, Integer end) {
        return null;
    }

}
