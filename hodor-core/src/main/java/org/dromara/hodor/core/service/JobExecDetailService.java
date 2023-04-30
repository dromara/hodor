package org.dromara.hodor.core.service;

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
}
