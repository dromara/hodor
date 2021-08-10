package org.dromara.hodor.server.executor.handler;

import java.io.File;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.common.log.LogUtil;
import org.dromara.hodor.core.entity.JobExecDetail;

/**
 * job execute record
 *
 * @author tomgs
 * @since 2021/8/10
 */
public class JobExecuteRecord {

    private final Logger logger;

    public JobExecuteRecord() {
        String loggerName = "hodor-job-execute-record.log";
        File logfile = new File(System.getProperty("user.dir"));
        String logLayout = "%msg%n";
        this.logger = LogUtil.getInstance().createRollingLogger(loggerName, logfile, logLayout, 3);
    }

    public void record(String msg, Object... params) {
        logger.info(msg, params);
    }

    public void recordRunningJob(JobExecDetail jobExecDetail) {

    }

}
