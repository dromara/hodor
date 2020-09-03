package org.dromara.hodor.core.service;

import javax.annotation.Resource;
import org.dromara.hodor.common.utils.HashUtils;
import org.dromara.hodor.core.BaseTest;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.enums.JobStatus;
import org.junit.Test;

/**
 * job info service test
 *
 * @author tomgs
 * @since 2020/8/28
 */
public class JobInfoServiceTest extends BaseTest {

    @Resource
    private JobInfoService jobInfoService;

    @Test
    public void testAddJobInfo() {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setGroupName("test");
        jobInfo.setJobName("1234");
        jobInfo.setCronExpression("*/5 * * * * ?");
        jobInfo.setJobStatus(JobStatus.READY);
        jobInfo.setHashId(HashUtils.hash(jobInfo.getGroupName() + jobInfo.getJobName()));

        jobInfoService.addJob(jobInfo);
    }

}
