package org.dromara.hodor.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        // select hash_id from hodor_job_info order by hash_id limit ${offset}, 1;
        QueryWrapper<HodorJobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("hash_id").orderByAsc("hash_id").last(String.format("limit %s, 1", offset));
        HodorJobInfo hodorJobInfo = jobInfoMapper.selectOne(queryWrapper);
        return hodorJobInfo == null ? -1 : hodorJobInfo.getHashId();
    }

    @Override
    public Integer queryJobIdByOffset(Integer offset) {
        // select id from hodor_job_info order by id limit ${offset}, 1;
        QueryWrapper<HodorJobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").orderByAsc("id").last(String.format("limit %s, 1", offset));
        HodorJobInfo hodorJobInfo = jobInfoMapper.selectOne(queryWrapper);
        return hodorJobInfo == null ? -1 : hodorJobInfo.getId();
    }

    @Override
    public List<HodorJobInfo> queryJobInfoByHashIdOffset(Integer startHashId, Integer endHashId) {
        return jobInfoMapper.selectList(Wrappers.<HodorJobInfo>lambdaQuery()
            .ge(HodorJobInfo::getHashId, startHashId)
            .lt(HodorJobInfo::getHashId, endHashId));
    }

}
