package org.dromara.hodor.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.cron.CronUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.mapper.JobInfoMapper;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * job info service
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobInfoServiceImpl implements JobInfoService {

    private final @NonNull JobInfoMapper jobInfoMapper;

    @Override
    public JobInfo addJob(JobInfo jobInfo) {
        jobInfoMapper.insert(jobInfo);
        return jobInfo;
    }

    @Override
    public void addJobIfAbsent(JobInfo jobInfo) {
        if (!isExists(jobInfo)) {
            addJob(jobInfo);
        }
    }

    @Override
    public boolean isExists(JobInfo jobInfo) {
        return jobInfoMapper.selectCount(Wrappers.<JobInfo>lambdaQuery()
            .eq(JobInfo::getGroupName, jobInfo.getGroupName())
            .eq(JobInfo::getJobName, jobInfo.getJobName())) > 0;
    }

    @Override
    public JobInfo queryJobByKey(String groupName, String jobName) {
        return jobInfoMapper.selectOne(Wrappers.<JobInfo>lambdaQuery()
                .eq(JobInfo::getGroupName, groupName)
                .eq(JobInfo::getJobName, jobName));
    }

    @Override
    public Integer queryAssignableJobCount() {
        return jobInfoMapper.selectCount(Wrappers.<JobInfo>lambdaQuery()
            .eq(JobInfo::getJobStatus, JobStatus.READY)
            .or()
            .eq(JobInfo::getJobStatus, JobStatus.RUNNING))
            .intValue();
    }

    @Override
    public Long queryJobHashIdByOffset(Integer offset) {
        // select hash_id from hodor_job_info order by hash_id limit ${offset}, 1;
        QueryWrapper<JobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("hash_id").orderByAsc("hash_id").last(String.format("limit %s, 1", offset));
        JobInfo hodorJobInfo = jobInfoMapper.selectOne(queryWrapper);
        return hodorJobInfo == null ? -1 : hodorJobInfo.getHashId();
    }

    @Override
    public Long queryJobIdByOffset(Integer offset) {
        // select id from hodor_job_info order by id limit ${offset}, 1;
        QueryWrapper<JobInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id").orderByAsc("id").last(String.format("limit %s, 1", offset));
        JobInfo hodorJobInfo = jobInfoMapper.selectOne(queryWrapper);
        return hodorJobInfo == null ? -1 : hodorJobInfo.getId();
    }

    @Override
    public List<JobInfo> queryJobInfoByDataInterval(DataInterval dataInterval, JobStatus jobStatus) {
        return jobInfoMapper.selectList(Wrappers.<JobInfo>lambdaQuery()
            .eq(JobInfo::getJobStatus, jobStatus)
            .ne(JobInfo::getCron, CronUtils.CRON_DISABLED) // cron expression is not null
            .ge(JobInfo::getHashId, dataInterval.getStartInterval())
            .lt(JobInfo::getHashId, dataInterval.getEndInterval()));
    }

    @Override
    public List<JobInfo> queryRunningJobInfoByDataInterval(DataInterval dataInterval) {
        return queryJobInfoByDataInterval(dataInterval, JobStatus.RUNNING);
    }

    @Override
    public List<JobInfo> queryReadyJobInfoByDataInterval(DataInterval dataInterval) {
        return queryJobInfoByDataInterval(dataInterval, JobStatus.READY);
    }

    @Override
    public void updateJobStatus(JobInfo jobInfo, JobStatus jobStatus) {
        JobInfo update = new JobInfo();
        update.setJobStatus(jobStatus);
        jobInfoMapper.update(update, Wrappers.<JobInfo>lambdaUpdate()
            .eq(JobInfo::getGroupName, jobInfo.getGroupName())
            .eq(JobInfo::getJobName, jobInfo.getJobName())
            .eq(JobInfo::getJobStatus, jobInfo.getJobStatus()));
    }

    @Override
    public boolean isRunningJob(JobInfo jobInfo) {
        return jobInfoMapper.selectCount(Wrappers.<JobInfo>lambdaQuery()
            .eq(JobInfo::getGroupName, jobInfo.getGroupName())
            .eq(JobInfo::getJobName, jobInfo.getJobName())
            .eq(JobInfo::getJobStatus, JobStatus.RUNNING)) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return jobInfoMapper.deleteById(id) > 0;
    }

    @Override
    public JobInfo updateById(JobInfo jobInfo) {
        final int result = jobInfoMapper.updateById(jobInfo);
        if (result > 0) {
            return queryById(jobInfo.getId());
        }
        return null;
    }

    @Override
    public JobInfo queryById(Long id) {
        return jobInfoMapper.selectById(id);
    }

    @Override
    public PageInfo<JobInfo> queryByPage(JobInfo jobInfo, Integer pageNo, Integer pageSize) {
        IPage<JobInfo> page = new Page<>(pageNo, pageSize);
        jobInfoMapper.selectPage(page, Wrappers.<JobInfo>lambdaQuery()
            .eq(StringUtils.isNotBlank(jobInfo.getGroupName()), JobInfo::getGroupName, jobInfo.getGroupName())
            .eq(StringUtils.isNotBlank(jobInfo.getJobName()), JobInfo::getJobName, jobInfo.getJobName())
            .eq(jobInfo.getJobStatus() != null, JobInfo::getJobStatus, jobInfo.getJobStatus())
            .eq(jobInfo.getJobType() != null, JobInfo::getJobType, jobInfo.getJobType())
            .eq(jobInfo.getJobCommandType() != null, JobInfo::getJobCommandType, jobInfo.getJobCommandType())
            .eq(jobInfo.getJobCategory() != null, JobInfo::getJobCategory, jobInfo.getJobCategory())
        );
        PageInfo<JobInfo> pageInfo = new PageInfo<>();
        return pageInfo.setRows(page.getRecords())
            .setTotal(page.getTotal())
            .setTotalPage((int) page.getPages())
            .setCurrentPage((int) page.getCurrent())
            .setPageNo(pageNo)
            .setPageSize(pageSize);
    }

}
