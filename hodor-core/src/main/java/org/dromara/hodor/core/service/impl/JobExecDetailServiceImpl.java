package org.dromara.hodor.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.core.entity.JobGroup;
import org.dromara.hodor.core.mapper.JobExecDetailMapper;
import org.dromara.hodor.core.service.JobExecDetailService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author tomgs
 * @since 1.0
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

    @Override
    public Boolean deleteById(Long id) {
        return jobExecDetailMapper.deleteById(id) > 0;
    }

    @Override
    public PageInfo<JobExecDetail> queryByPage(JobExecDetail jobExecDetail, Integer pageNo, Integer pageSize) {
        IPage<JobExecDetail> page = new Page<>(pageNo, pageSize);
        jobExecDetailMapper.selectPage(page, Wrappers.<JobExecDetail>lambdaQuery()
            .eq(JobExecDetail::getGroupName, jobExecDetail.getGroupName())
            .eq(JobExecDetail::getJobName, jobExecDetail.getJobName()));
        PageInfo<JobExecDetail> pageInfo = new PageInfo<>();
        return pageInfo.setRows(page.getRecords())
            .setTotal(page.getTotal())
            .setTotalPage((int) page.getPages())
            .setCurrentPage((int) page.getCurrent())
            .setPageNo(pageNo)
            .setPageSize(pageSize);
    }

    @Override
    public JobExecDetail queryById(Long id) {
        return jobExecDetailMapper.selectById(id);
    }

}
