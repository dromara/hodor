package org.dromara.hodor.actuator.bigdata.jobtype.asyncHadoop;

import java.util.Set;
import org.apache.log4j.Logger;
import org.dromara.hodor.actuator.bigdata.core.JobExecutorStateChecker;
import org.dromara.hodor.actuator.bigdata.jobtype.HadoopJavaJob;
import org.dromara.hodor.actuator.bigdata.jobtype.HadoopJobUtils;
import org.dromara.hodor.actuator.bigdata.jobtype.javautils.AsyncJobStateTask;
import org.dromara.hodor.actuator.common.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.common.utils.Props;

import static org.dromara.hodor.actuator.bigdata.executor.Constants.JobProperties.JOB_LOG_PATH;


/**
 * 异步提交hadoop任务
 *
 * @author tangzhongyuan
 * @date 2019-03-07 18:34
 **/
public class AsyncHadoopJavaJob extends HadoopJavaJob {

    private final Logger logger;

    private final Props props;

    private final String jobid;

    public AsyncHadoopJavaJob(String jobid, Props sysProps, Props jobProps, Logger logger) throws RuntimeException {
        super(jobid, sysProps, jobProps, logger);
        getJobProps().put("execute.as.async", "true");
        this.logger = logger;
        this.props = jobProps;
        this.jobid = jobid;
    }

    @Override
    public void run() throws Exception {
        super.run();
        String logPath = jobProps.getString(JOB_LOG_PATH);
        Set<String> applicationIds = HadoopJobUtils.findApplicationIdFromLog(logPath, logger);
        logger.info("hadoop submit applicationIds:" + applicationIds);
        if (applicationIds.size() <= 0) {
            throw new JobExecutionException("applicationId is null, failed to run job " + jobid);
        }
        //这里只处理一个任务的情况
        String applicationId = applicationIds.iterator().next();
        Long requestId = jobProps.getLong("requestId");

        AsyncJobStateTask task = AsyncJobStateTask.builder()
            .appId(applicationId)
            .requestId(requestId)
            .props(props)
            .build();
        JobExecutorStateChecker stateCheckHandler = JobExecutorStateChecker.getInstance();
        int queueSize = stateCheckHandler.addTask(task);

        logger.info("current queue size : " + queueSize);
    }

}
