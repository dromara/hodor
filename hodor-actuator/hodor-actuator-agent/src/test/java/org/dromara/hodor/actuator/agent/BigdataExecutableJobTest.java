package org.dromara.hodor.actuator.agent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import org.dromara.hodor.actuator.agent.config.HodorActuatorAgentProperties;
import org.dromara.hodor.actuator.agent.job.AgentJobRegister;
import org.dromara.hodor.actuator.api.ExecutableJob;
import org.dromara.hodor.actuator.api.config.HodorProperties;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.core.JobLogger;
import org.dromara.hodor.actuator.api.core.JobLoggerManager;
import org.dromara.hodor.actuator.api.utils.JobPathUtils;
import org.dromara.hodor.common.utils.Props;
import org.dromara.hodor.actuator.jobtype.api.executor.CommonJobProperties;
import org.dromara.hodor.actuator.jobtype.api.executor.Job;
import org.dromara.hodor.actuator.jobtype.api.executor.NoopJob;
import org.dromara.hodor.actuator.jobtype.api.jobtype.JobTypeManager;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author tomgs
 * @version 2022/3/14 1.0
 */
public class BigdataExecutableJobTest {

    @Test
    public void testBase() {
        final JobLoggerManager jobLoggerManager = new JobLoggerManager("test", null, Paths.get("./test.log"));
        JobTypeManager jobtypeManager = new JobTypeManager(null, null, getClass().getClassLoader());
        String jobKey = "testJob";
        Props props = new Props();
        props.put(CommonJobProperties.JOB_TYPE, "noop");
        final JobLogger jobLogger = jobLoggerManager.createJobLogger();
        Job job = jobtypeManager.buildJobExecutor(jobKey, props, jobLogger);
        jobLoggerManager.stopJobLogger();
        Assert.assertTrue(job instanceof NoopJob);
    }

    @Test
    public void testJobExecute() throws Exception {
        String dataPath = "./hodor-actuator";
        Long requestId = 123L;
        JobKey jobKey = JobKey.of("testGroup", "testJob");
        final String loggerName = JobPathUtils.createLoggerName(jobKey.getGroupName(), jobKey.getJobName(), requestId);
        final Path jobLogPath = JobPathUtils.getJobLogPath(dataPath, jobKey, requestId);
        final JobLoggerManager jobLoggerManager = new JobLoggerManager(loggerName, null, jobLogPath);
        final JobLogger jobLogger = jobLoggerManager.createJobLogger();
        String jobPath = this.getClass().getResource("/sample_flow_01.zip").getPath();

        HodorProperties hodorProperties = Mockito.mock(HodorProperties.class);
        HodorActuatorAgentProperties properties = Mockito.mock(HodorActuatorAgentProperties.class);
        Mockito.when(hodorProperties.getDataPath()).thenReturn(dataPath);
        Mockito.when(hodorProperties.getAppName()).thenReturn("test_bigdata_actuator");
        Mockito.when(properties.getAgentConfig()).thenReturn(new HashMap<>());
        Mockito.when(properties.getCommonProperties()).thenReturn(hodorProperties);

        RequestContext requestContext = Mockito.mock(RequestContext.class);
        JobExecuteRequest jobExecuteRequest = Mockito.mock(JobExecuteRequest.class);
        Mockito.when(jobExecuteRequest.getJobCommand()).thenReturn(jobPath);
        Mockito.when(jobExecuteRequest.getVersion()).thenReturn(1);
        Mockito.when(jobExecuteRequest.getGroupName()).thenReturn(jobKey.getGroupName());
        Mockito.when(jobExecuteRequest.getJobName()).thenReturn(jobKey.getJobName());

        ExecutableJobContext context = ExecutableJobContext.builder()
            .requestId(requestId)
            .jobKey(jobKey)
            .jobCommandType("noop")
            .requestContext(requestContext)
            .executeRequest(jobExecuteRequest)
            .jobLogger(jobLogger)
            .build();

        AgentJobRegister register = new AgentJobRegister(properties);
        ExecutableJob executableJob = register.provideExecutableJob(context);
        executableJob.execute(context);
        jobLoggerManager.stopJobLogger();
    }

}
