package org.dromara.hodor.actuator.jobtype.kettle;

import java.nio.file.Path;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.actuator.api.core.JobLogger;
import org.dromara.hodor.actuator.api.core.JobLoggerManager;
import org.dromara.hodor.actuator.api.utils.JobPathUtils;
import org.dromara.hodor.actuator.jobtype.api.executor.Job;
import org.dromara.hodor.common.utils.Props;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.lang.NonNull;

import static org.dromara.hodor.actuator.jobtype.api.executor.CommonJobProperties.JOB_CONTEXT;

/**
 * @author tomgs
 * @version 2022/3/14 1.0
 */
public class KettleJobTest {

    @Test
    public void testJobExecute() throws Exception {
        RequestWrapper requestWrapper = getRequest(123L);
        Props jobProps = new Props();
        Props sysProps = new Props();

        String jobPath = this.getClass().getResource("/test_kettle_job.kjb").getPath();

        jobProps.put(JOB_CONTEXT, requestWrapper.context);
        jobProps.put(KettleConstant.REPOSITORY_PATH, "");
        jobProps.put(KettleConstant.PATH, jobPath);
        jobProps.put(KettleConstant.PLUGINS, "E:\\app\\data-integration\\plugins");
        jobProps.put(KettleConstant.LOG_LEVEL, "Detailed");

        Job job = new KettleJob(requestWrapper.jobKey.getKeyName(), sysProps, jobProps, requestWrapper.jobLogger.getLogger());
        job.run();

        requestWrapper.jobLoggerManager.stopJobLogger();
    }

    @Test
    public void testTransExecute() throws Exception {
        RequestWrapper requestWrapper = getRequest(125L);
        Props jobProps = new Props();
        Props sysProps = new Props();

        String jobPath = this.getClass().getResource("/test_kettle_trans.ktr").getPath();

        jobProps.put(JOB_CONTEXT, requestWrapper.context);
        jobProps.put(KettleConstant.REPOSITORY_PATH, "");
        jobProps.put(KettleConstant.PATH, jobPath);
        jobProps.put(KettleConstant.PLUGINS, "E:\\app\\data-integration\\plugins");
        jobProps.put(KettleConstant.LOG_LEVEL, "Detailed");

        Job job = new KettleTrans(requestWrapper.jobKey.getKeyName(), sysProps, jobProps, requestWrapper.jobLogger.getLogger());
        job.run();

        requestWrapper.jobLoggerManager.stopJobLogger();
    }

    @NonNull
    private static RequestWrapper getRequest(Long requestId) {
        String dataPath = "./hodor-actuator";
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
            .requestContext(requestContext)
            .executeRequest(jobExecuteRequest)
            .jobLogger(jobLogger)
            .build();
        return new RequestWrapper(jobKey, jobLoggerManager, jobLogger, context);
    }

    private static class RequestWrapper {
        public final JobKey jobKey;

        public final JobLoggerManager jobLoggerManager;

        public final JobLogger jobLogger;

        public final ExecutableJobContext context;

        public RequestWrapper(JobKey jobKey, JobLoggerManager jobLoggerManager, JobLogger jobLogger, ExecutableJobContext context) {
            this.jobKey = jobKey;
            this.jobLoggerManager = jobLoggerManager;
            this.jobLogger = jobLogger;
            this.context = context;
        }
    }

}
