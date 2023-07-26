package org.dromara.hodor.actuator.java.examples.job;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.JobExecutionContext;
import org.dromara.hodor.actuator.api.core.JobLogger;
import org.dromara.hodor.actuator.java.annotation.Job;
import org.springframework.stereotype.Component;

/**
 * job list demo
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Component
public class JobList {

    @Job(group = "testGroup", jobName = "test1", cron = "0/30 * * * * ?")
    public String test1(JobExecutionContext context) {
        log.info("Job [testGroup#test1] execute, context: {}", context);
        JobLogger logger = context.getJobLogger();
        logger.info("start executor job test1");
        logger.info("job argument: {}", context.getJobParameter());
        logger.info("executing......");
        logger.info("executed");
        return "a=123";
    }

    @Job(group = "testGroup", jobName = "test2", cron = "0/15 * * * * ?")
    public void test2() {
        log.info("Job [testGroup#test2] execute, no arguments");
    }

}
