package org.dromara.hodor.client.core;

import java.io.File;
import java.text.MessageFormat;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.common.log.LogUtil;

/**
 * job logger manager
 *
 * @author tomgs
 * @since 2021/3/23
 */
public class JobLoggerManager {

    private static final JobLoggerManager INSTANCE = new JobLoggerManager();

    private JobLoggerManager() {
    }

    public static JobLoggerManager getInstance() {
        return INSTANCE;
    }

    public String getJobLoggerDir(String rootJobLogPath) {
        return rootJobLogPath == null ? System.getProperty("user.dir") : rootJobLogPath;
    }

    public String createLoggerName(String groupName, String jobName, Long requestId) {
        return MessageFormat.format("{0}_{1}_{2}_{3}", System.currentTimeMillis(),
            groupName,
            jobName,
            requestId);
    }

    public String createLogFileName(String groupName, String jobName, Long requestId) {
        return MessageFormat.format("_job.{0}.{1}.{2}.log",
            groupName,
            jobName,
            requestId);
    }

    public File buildJobLoggerFile(String rootJobLogPath, String logFileName) {
        return new File(getJobLoggerDir(rootJobLogPath), logFileName);
    }

    public File buildJobLoggerFile(String rootJobLogPath, String groupName, String jobName, Long requestId) {
        return buildJobLoggerFile(rootJobLogPath, createLogFileName(groupName, jobName, requestId));
    }

    public Logger createJobLogger(String rootJobLogPath, String groupName, String jobName, Long requestId) {
        File jobLoggerFile = buildJobLoggerFile(rootJobLogPath, createLogFileName(groupName, jobName, requestId));
        return LogUtil.getInstance().createLogger(createLoggerName(groupName, jobName, requestId), jobLoggerFile);
    }

    public Logger createJobLogger(String loggerName, File logFile) {
        return LogUtil.getInstance().createLogger(loggerName, logFile);
    }

    public void stopJobLogger(String loggerName) {
        LogUtil.getInstance().stopLogger(loggerName);
    }

}
