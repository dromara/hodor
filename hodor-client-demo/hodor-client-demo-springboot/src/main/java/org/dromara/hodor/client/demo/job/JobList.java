package org.dromara.hodor.client.demo.job;

import org.dromara.hodor.client.JobParameter;
import org.dromara.hodor.client.annotation.Job;
import org.springframework.stereotype.Component;

/**
 * job list demo
 *
 * @author tomgs
 * @since 2021/1/4
 */
@Component
public class JobList {

    @Job(group = "testGroup", jobName = "test1", cron = "0/5 * * * * ?")
    public void test1(JobParameter jobParameter) {
        System.out.println(jobParameter);
    }

    @Job(group = "testGroup", jobName = "test2", cron = "0/5 * * * * ?")
    public void test2(JobParameter jobParameter) {
        System.out.println(jobParameter);
    }

}
