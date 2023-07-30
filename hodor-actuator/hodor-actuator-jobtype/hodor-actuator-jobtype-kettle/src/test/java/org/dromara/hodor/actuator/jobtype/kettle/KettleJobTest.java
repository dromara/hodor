package org.dromara.hodor.actuator.jobtype.kettle;

import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.core.JobLogger;
import org.dromara.hodor.actuator.api.core.JobLoggerManager;
import org.dromara.hodor.actuator.api.utils.JobPathUtils;
import org.dromara.hodor.actuator.api.utils.Props;
import org.dromara.hodor.actuator.jobtype.api.executor.Job;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.Path;

import static org.dromara.hodor.actuator.jobtype.api.executor.CommonJobProperties.JOB_CONTEXT;

/**
 * @author tomgs
 * @version 2022/3/14 1.0
 */
public class KettleJobTest {

    @Test
    public void testJobExecute() throws Exception {
        String dataPath = "./hodor-actuator";
        Long requestId = 123L;
        JobKey jobKey = JobKey.of("testGroup", "testJob");
        final String loggerName = JobPathUtils.createLoggerName(jobKey.getGroupName(), jobKey.getJobName(), requestId);
        final Path jobLogPath = JobPathUtils.getJobLogPath(dataPath, jobKey, requestId);
        final JobLoggerManager jobLoggerManager = new JobLoggerManager(loggerName, null, jobLogPath);
        final JobLogger jobLogger = jobLoggerManager.createJobLogger();
        //String jobPath = this.getClass().getResource("/sample_flow_01.zip").getPath();

        RequestContext requestContext = Mockito.mock(RequestContext.class);
        JobExecuteRequest jobExecuteRequest = Mockito.mock(JobExecuteRequest.class);
        //Mockito.when(jobExecuteRequest.getJobPath()).thenReturn(jobPath);
        Mockito.when(jobExecuteRequest.getVersion()).thenReturn(1);
        Mockito.when(jobExecuteRequest.getGroupName()).thenReturn(jobKey.getGroupName());
        Mockito.when(jobExecuteRequest.getJobName()).thenReturn(jobKey.getJobName());

        ExecutableJobContext context = ExecutableJobContext.builder()
            .requestId(requestId)
            .jobKey(jobKey)
            .jobCommandType("kettleJob")
            .dataPath(dataPath)
            .requestContext(requestContext)
            .executeRequest(jobExecuteRequest)
            .jobLogger(jobLogger)
            .build();
        Props jobProps = new Props();
        Props sysProps = new Props();

        jobProps.put(JOB_CONTEXT, context);
        jobProps.put(KettleConstant.REPOSITORY_PATH, "");
        jobProps.put(KettleConstant.PATH, "F:\\workspace_exer\\source_code\\hodor\\hodor-actuator\\hodor-actuator-jobtype\\hodor-actuator-jobtype-kettle\\src\\test\\resources\\testJob.kjb");
        jobProps.put(KettleConstant.PLUGINS, "");
        jobProps.put(KettleConstant.LOG_LEVEL, "Detailed");

        Job job = new KettleJob(jobKey.getKeyName(), sysProps, jobProps, jobLogger.getLogger());
        job.run();

        jobLoggerManager.stopJobLogger();
    }

}
