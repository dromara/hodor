package org.dromara.hodor.core.service;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.BaseTest;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class JobExecDetailServiceTest extends BaseTest {

    @Resource
    private JobExecDetailService jobExecDetailService;

    @Test
    void queryById() {
        final JobExecDetail jobExecDetail = jobExecDetailService.queryById(932997738017243136L);
        Assertions.assertNotNull(jobExecDetail);
    }

    @Test
    void deleteById() {
        final Boolean result = jobExecDetailService.deleteById(932997738017243136L);
        Assertions.assertTrue(result);
    }

    @Test
    void queryByPage() {
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setGroupName("testGroup");
        jobExecDetail.setJobName("test1");
        final PageInfo<JobExecDetail> jobExecDetailPageInfo = jobExecDetailService.queryByPage(jobExecDetail, 1, 2);
        Assertions.assertNotNull(jobExecDetailPageInfo);
        log.info("queryByPageInfo: {}", jobExecDetailPageInfo);
    }

}
