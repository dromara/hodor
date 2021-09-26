package org.dromara.hodor.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.core.mapper.JobExecDetailMapper;
import org.dromara.hodor.core.service.JobExecDetailService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author tomgs
 * @since 2020/8/27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecDetailServiceImpl implements JobExecDetailService {

    private final @NonNull JobExecDetailMapper jobExecDetailMapper;

    @Override
    public void create(JobExecDetail jobExecDetail) {
        jobExecDetailMapper.insert(jobExecDetail);
    }

    @Override
    public void update(JobExecDetail jobExecDetail) {
        jobExecDetailMapper.updateById(jobExecDetail);
    }

    @Override
    public void createIfAbsent(JobExecDetail jobExecDetail) {
        jobExecDetailMapper.insertIgnore(jobExecDetail);
    }

}
