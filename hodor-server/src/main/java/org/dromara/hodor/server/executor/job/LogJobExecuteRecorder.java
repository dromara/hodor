package org.dromara.hodor.server.executor.job;

import java.io.File;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.common.log.LogUtil;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.server.config.HodorServerProperties;
import org.dromara.hodor.server.service.RegisterService;
import org.springframework.stereotype.Service;

/**
 * LogJobExecuteRecord
 *
 * @author tomgs
 * @since 2021/8/11
 */
@Service
public class LogJobExecuteRecorder implements JobExecuteRecorder {

    private final Logger logger;

    private final RegisterService registerService;

    public LogJobExecuteRecorder(final HodorServerProperties properties, final RegisterService registerService) {
        String logLayout = "%msg%n";
        String logDir = properties.getLogDir();
        String loggerName = "hodor-job-execute-record.log";
        File logfile = new File((StringUtils.isBlank(logDir) ? System.getProperty("user.dir") : logDir) + "/logs/" + loggerName);
        this.logger = LogUtil.getInstance().createRollingLogger(loggerName, logfile, logLayout, 3);
        this.registerService = registerService;
    }

    public void recordJobExecDetail(String op, JobExecDetail jobExecDetail) {
        logger.info(toDetailString(op, jobExecDetail));
    }

    @Override
    public JobExecDetail getJobExecDetail(JobKey jobKey) {
        return null;
    }

    @Override
    public void removeRunningJob(JobKey jobKey) {

    }

    @Override
    public void addSchedulerRunningJob(JobExecDetail jobExecDetail) {

    }

}
