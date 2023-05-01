package org.dromara.hodor.actuator.api.core;

import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * @author tomgs
 * @version 2022/3/14 1.0
 */
public class JobLoggerManagerTest {

    @Test
    public void testLog4j2ConvertLog4j() {
        JobLogger jobLogger = JobLoggerManager.getInstance().createJobLogger("./",
                "test.log",
                "test",
                123L);
        Logger log4jLogger = jobLogger.getLogger();
        log4jLogger.info("1232");
    }

}
