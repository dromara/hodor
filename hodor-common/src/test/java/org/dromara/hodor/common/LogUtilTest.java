package org.dromara.hodor.common;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.common.log.LogUtil;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.junit.Test;

/**
 * log util test
 *
 * @author tomgs
 * @since 1.0
 */
public class LogUtilTest {

    @Test
    public void testCreateLogFile() {
        File logFile = new File(System.getProperty("user.dir") + "/log/test.log");
        Logger logger = LogUtil.getInstance().createLogger("log-test", logFile);
        logger.info("12312312");
        logger.info("12312312");
    }

    @Test
    public void testCreateRollingLogFile() {
        File logFile = new File(System.getProperty("user.dir") + "/log/test-rolling.log");
        Logger logger = LogUtil.getInstance().createRollingLogger("log-test", logFile, "%msg%n", 3);
        for (int i = 0; i < 9; i++) {
            logger.info("12312312");
            ThreadUtils.sleep(TimeUnit.SECONDS, 1);
        }
    }

    @Test
    public void testWriteByteData() {
        File logFile = new File(System.getProperty("user.dir") + "/log/test-byte.log");
        Logger logger = LogUtil.getInstance().createLogger("log-test", logFile);
        byte[] bytes = "1231312312".getBytes(StandardCharsets.UTF_8);
        logger.info(bytes);
    }
}
