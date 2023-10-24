package org.dromara.hodor.actuator.jobtype.bigdata.javautils;

import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class JobUtils {

    private static final String SPLIT_COMMA = ",";

    public static Logger initJobLogger() {
        // Create configuration builder
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        // Create layout builder
        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
            .addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} %p %C{1.} [%t] %m%n");
        // Create console appender builder
        AppenderComponentBuilder consoleAppenderBuilder = builder.newAppender("console", "Console")
            .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
            .add(layoutBuilder);
        // Add console appender to root logger
        RootLoggerComponentBuilder rootLoggerBuilder = builder.newRootLogger(Level.INFO)
            .add(builder.newAppenderRef("console"));
        // Add appenders and loggers to configuration
        builder.add(consoleAppenderBuilder);
        builder.add(rootLoggerBuilder);
        // Build configuration and start logger context
        BuiltConfiguration config = builder.build();
        LoggerContext context = Configurator.initialize(config);
        // Get logger and log messages
        return context.getRootLogger();
    }

    public static Configuration getHadoopConfiguration(YarnSubmitArguments arguments) {
        Configuration conf = new Configuration();
        String os = System.getProperty("os.name");
        boolean cross_platform = os.contains("Windows");
        conf.setBoolean("mapreduce.app-submission.cross-platform", cross_platform);// 配置使用跨平台提交任务
        // 启用yarn的高可用，默认关闭
        conf.set("mapreduce.framework.name", "yarn");
        conf.setBoolean("yarn.resourcemanager.ha.enabled", true);
        conf.set("yarn.resourcemanager.ha.rm-ids", "rm1,rm2");
        // 设置yarn资源，不然会使用localhost:8032
        String[] hostPort = RegexUtil.getResourceHostPort(arguments.getYarnResourcemanagerAddress());
        assert hostPort != null;
        conf.set("yarn.resourcemanager.address.rm1", hostPort[0]);
        // 设置yarn资源，不然会使用localhost:8032
        if (hostPort.length >= 2) {
            conf.set("yarn.resourcemanager.address.rm2", hostPort[1]);
        }

        // 设置namenode的地址，不然jar包会分发，非常恶心
        String namenodes = arguments.getNamenodes();
        String rpcAddress = arguments.getNamenodeRpcAddress();
        String nameservices = arguments.getNameservices();

        conf.set("fs.defaultFS", arguments.getDefaultFS());
        conf.set("dfs.nameservices", nameservices);
        conf.set("dfs.ha.namenodes." + nameservices, namenodes);

        String[] namenodesStr = namenodes.split(SPLIT_COMMA);
        String[] rpcAddressStr = rpcAddress.split(SPLIT_COMMA);
        if (namenodesStr.length != rpcAddressStr.length) {
            throw new IllegalArgumentException("namenodes and rpcAddress length is not match.");
        }
        for (int i = 0; i < namenodesStr.length; i++) {
            conf.set("dfs.namenode.rpc-address." + nameservices + "." + namenodesStr[i], rpcAddressStr[i]);
        }

        conf.set("dfs.client.failover.proxy.provider." + nameservices,
            "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");

        //设置实现类，因为会出现类覆盖的问题
        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        return conf;
    }

}
