package org.dromara.hodor.client.demo.job;

import org.apache.logging.log4j.Logger;
import org.dromara.hodor.client.JobExecutionContext;
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
    public String test1(JobExecutionContext context) {
        System.out.println(context);
        Logger logger = context.getJobLogger();
        logger.info("start executor job test1");
        logger.info("job argument: {}", context.getJobParameter());
        logger.info("executing......");
        logger.info("executed");
        return "a=123";
    }

    @Job(group = "testGroup", jobName = "test2", cron = "0/5 * * * * ?")
    public void test2() {
        System.out.println("no arguments ...");
    }

}
