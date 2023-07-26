package org.dromara.hodor.actuator.api.core;

import java.nio.file.Path;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

/**
 * job logger manager
 *
 * @author tomgs
 * @since 1.0
 */
public class JobLoggerManager {

    private static final String DEFAULT_LAYOUT = "%d{yyyy-MM-dd HH:mm:ss} %p %msg%n";

    private final String loggerName;

    private final String layout;

    private final Path logPath;

    private LoggerContext context;

    public JobLoggerManager(String loggerName, String layout, Path logPath) {
        this.loggerName = loggerName;
        this.layout = layout;
        this.logPath = logPath;
    }

    public JobLogger createJobLogger() {
        this.context = createLogContext(loggerName, logPath.toAbsolutePath().toString(), layout == null ? DEFAULT_LAYOUT : layout);
        // Get logger and log messages
        Logger logger = context.getLogger(loggerName);
        //Logger logger = LogUtil.getInstance().createLogger(loggerName, logFile);
        return new JobLogger(loggerName, logPath, logger);
    }

    private LoggerContext createLogContext(String loggerName, String logPath, String layout) {
        System.setProperty("log4j2.disable.jmx", "true");
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
            .addAttribute("pattern", layout);
        final AppenderComponentBuilder appenderComponentBuilder = builder.newAppender("jobLogFile", "RandomAccessFile")
            .addAttribute("fileName", logPath)
            .add(layoutBuilder);
        final RootLoggerComponentBuilder loggerComponentBuilder = builder.newRootLogger(Level.INFO)
            .add(builder.newAppenderRef("jobLogFile"));
        builder.add(appenderComponentBuilder);
        builder.add(loggerComponentBuilder);
        // Build configuration and start logger context
        Configuration config = builder.build();
        //return Configurator.initialize(config);
        LoggerContext context = new LoggerContext(loggerName);
        context.setConfiguration(config);
        return context;
    }

    public void stopJobLogger() {
        //LogUtil.getInstance().stopLogger(loggerName);
        Configurator.shutdown(context);
    }

}
