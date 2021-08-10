package org.dromara.hodor.common.log;

import java.io.File;
import java.util.function.Supplier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * 日志工具
 *
 * @author tomgs
 * @since 2021/2/19
 */
public class LogUtil {

    private static final LogUtil INSTANCE = new LogUtil();

    public static final String DEFAULT_LAYOUT = "[%d{yyyy-MM-dd HH:mm:ss}] [%thread] %-5level %class{40}:%L %msg%n";

    private final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    private final Configuration config = ctx.getConfiguration();

    private LogUtil() {

    }

    public static LogUtil getInstance() {
        return INSTANCE;
    }

    public Logger createLogger(String loggerName, File logFile) {
        return this.createLogger(loggerName, logFile, DEFAULT_LAYOUT);
    }

    public Logger createLogger(String loggerName, File logFile, String layoutPattern) {
        return createLogger(loggerName, () -> buildFileAppender(loggerName, logFile, layoutPattern));
    }

    public Logger createRollingLogger(String loggerName, File logFile, String layoutPattern, int interval) {
        return createLogger(loggerName, () -> buildRollingFileAppender(loggerName, logFile, layoutPattern, interval));
    }

    public Logger createLogger(String loggerName, Supplier<Appender> appenderSupplier) {
        Logger logger = LogManager.getLogger(loggerName);
        Appender appender = appenderSupplier.get();
        appender.start();
        attachFileAppend(logger, appender);
        return logger;
    }

    public void attachFileAppend(Logger logger, Appender appender) {
        String loggerName = logger.getName();
        AppenderRef ref = AppenderRef.createAppenderRef(loggerName, null, null);
        AppenderRef[] refs = new AppenderRef[] { ref };

        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.ALL, loggerName, "true", refs, null, config, null);
        loggerConfig.addAppender(appender, null, null);

        config.addAppender(appender);
        config.addLogger(loggerName, loggerConfig);

        ctx.updateLoggers();
    }

    public Appender buildFileAppender(String loggerName, File logFile, String layoutPattern) {
        Layout<String> layout = PatternLayout.newBuilder().withPattern(layoutPattern).build();
        return FileAppender.newBuilder()
            .setConfiguration(config)
            .withAppend(true)
            .withName(loggerName)
            .withFileName(logFile.getAbsolutePath())
            .withLayout(layout)
            .build();
    }

    public Appender buildRollingFileAppender(String loggerName, File logFile, String layoutPattern, int interval) {
        Layout<String> layout = PatternLayout.newBuilder().withPattern(layoutPattern).build();
        String fileName = logFile.getAbsolutePath();
        return RollingFileAppender.newBuilder()
            .setConfiguration(config)
            .withAppend(true)
            .withName(loggerName)
            .withFileName(fileName)
            .withFilePattern(fileName + "_%d{yyyy-MM-dd-HH-mm-ss}_%i")
            .withLayout(layout)
            .withPolicy(TimeBasedTriggeringPolicy.newBuilder()
                .withInterval(interval) // unit seconds
                .withModulate(true)
                .build())
            .withStrategy(DefaultRolloverStrategy.newBuilder()
                .withMax("10")
                .build())
            .build();
    }

    public void stopLogger(String loggerName) {
        config.getAppender(loggerName).stop();
        config.getLoggerConfig(loggerName).removeAppender(loggerName);
        config.removeLogger(loggerName);
        ctx.updateLoggers();
    }

}
