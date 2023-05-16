package org.dromara.hodor.core.service;

import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobExecDetail;

/**
 * job execute detail service
 *
 * @author tomgs
 * @since 1.0
 */
public interface JobExecDetailService {

    void create(JobExecDetail jobExecDetail);

    void update(JobExecDetail jobExecDetail);

    void createIfAbsent(JobExecDetail jobExecDetail);

    Boolean deleteById(Long id);

    PageInfo<JobExecDetail> queryByPage(JobExecDetail jobExecDetail, Integer pageNo, Integer pageSize);

    JobExecDetail queryById(Long id);
}
