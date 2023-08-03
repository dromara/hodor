package org.dromara.hodor.actuator.api.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.api.utils.JobPathUtils;
import org.dromara.hodor.model.job.JobKey;
import org.junit.Test;

/**
 * @author tomgs
 * @version 2022/3/14 1.0
 */
public class JobLoggerManagerTest {

    @Test
    public void testLog4j2ConvertLog4j() {
        final Path path = Paths.get("./test.log");
        final JobLoggerManager jobLoggerManager = new JobLoggerManager("test", null, path);
        JobLogger jobLogger = jobLoggerManager.createJobLogger();
        Logger log4jLogger = jobLogger.getLogger();
        log4jLogger.info("1232");

        jobLoggerManager.stopJobLogger();
    }

    @Test
    public void testJobLogger() {
        String dataPath = "./test1.log";
        Long requestId = 123L;
        JobKey jobKey = JobKey.of("testGroup", "testJob");
        final String loggerName = JobPathUtils.createLoggerName(jobKey.getGroupName(), jobKey.getJobName(), requestId);
        final Path jobLogPath = JobPathUtils.getJobLogPath(dataPath, jobKey, requestId);
        final JobLoggerManager jobLoggerManager = new JobLoggerManager(loggerName, null, jobLogPath);
        final JobLogger jobLogger = jobLoggerManager.createJobLogger();
        jobLogger.info("test job logger print.");
        jobLoggerManager.stopJobLogger();
    }

}
