package org.dromara.hodor.actuator.bigdata.jobtype.javautils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.spark.SparkConf;
import org.dromara.hodor.actuator.bigdata.exception.ConfigException;
import org.dromara.hodor.actuator.bigdata.jobtype.asyncSpark.RegexUtil;
import org.dromara.hodor.actuator.bigdata.jobtype.asyncSpark.YarnSubmitConditions;

public class JobUtils {

    private static final String SPLIT_COMMA = ",";

    public static Logger initJobLogger() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.removeAllAppenders();
        ConsoleAppender appender = new ConsoleAppender();
        appender.setLayout(new PatternLayout("%p %m\n"));
        appender.activateOptions();
        rootLogger.addAppender(appender);
        return rootLogger;
    }

    public static SparkConf getSparkConf(YarnSubmitConditions conditions) {
        SparkConf sparkConf = new SparkConf();
        if (StringUtils.isNotEmpty(conditions.getJobName())) {
            sparkConf.setAppName(conditions.getJobName());
        }

        sparkConf.set("spark.yarn.jars", conditions.getYarnJars());
        //sparkConf.set("spark.driver.host", conditions.getClientHost());
        if (conditions.getDependJars() != null && conditions.getDependJars().length > 0) {
            sparkConf.set("spark.jars", StringUtils.join(conditions.getDependJars(), ","));
        }

        if (conditions.getFiles() != null && conditions.getFiles().length > 0) {
            sparkConf.set("spark.files", StringUtils.join(conditions.getFiles(), ","));
        }

        if (conditions.getProperties() != null) {
            for (Map.Entry<Object, Object> e : conditions.getProperties().entrySet()) {
                sparkConf.set(e.getKey().toString(), e.getValue().toString());
            }
        }

        //sparkConf.set("yarn.resourcemanager.ha.rm-ids", "rm1,rm2");

        // 添加这个参数，不然spark会一直请求0.0.0.0:8030,一直重试
        /*List<String> host = RegexUtil.getResourceHost(conditions.getYarnResourcemanagerAddress());
        sparkConf.set("yarn.resourcemanager.hostname.rm1", host.get(0));
        if (host.size() >= 2) {
            sparkConf.set("yarn.resourcemanager.hostname.rm2", host.get(1));
        }*/
        // 添加这个参数，不然spark会一直请求0.0.0.0:8030,一直重试
        List<String> host = RegexUtil.getResourceHost(conditions.getYarnResourcemanagerAddress());
        if (host.size() < 2) {
            throw new IllegalArgumentException("resourcemanagerAddress is illegal argument...");
        }
        Socket socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(host.get(1), 8030);
        try {
            socket.connect(socketAddress, 3000);// 超时3秒
            sparkConf.set("yarn.resourcemanager.hostname", host.get(1));
        } catch (IOException e) {
            sparkConf.set("yarn.resourcemanager.hostname", host.get(0));
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 设置为true，不删除缓存的jar包，因为现在提交yarn任务是使用的代码配置，没有配置文件，删除缓存的jar包有问题，
        sparkConf.set("spark.yarn.preserve.staging.files", "true");
        sparkConf.set("spark.sql.session.timeZone", "Asia/Shanghai");
        sparkConf.set("spark.yarn.queue", conditions.getQueue());
        sparkConf.set("spark.submit.deployMode", "cluster");
        return sparkConf;
    }

    public static Configuration getHadoopConfiguration(YarnSubmitConditions conditions) {
        Configuration conf = new Configuration();
        String os = System.getProperty("os.name");
        boolean cross_platform = false;
        if (os.contains("Windows")) {
            cross_platform = true;
        }
        conf.setBoolean("mapreduce.app-submission.cross-platform", cross_platform);// 配置使用跨平台提交任务
        // 启用yarn的高可用，默认关闭
        conf.set("mapreduce.framework.name", "yarn");
        conf.setBoolean("yarn.resourcemanager.ha.enabled", true);
        conf.set("yarn.resourcemanager.ha.rm-ids", "rm1,rm2");
        // 设置yarn资源，不然会使用localhost:8032
        String[] hostPort = RegexUtil.getResourceHostPort(conditions.getYarnResourcemanagerAddress());
        assert hostPort != null;
        conf.set("yarn.resourcemanager.address.rm1", hostPort[0]);
        // 设置yarn资源，不然会使用localhost:8032
        if (hostPort.length >= 2) {
            conf.set("yarn.resourcemanager.address.rm2", hostPort[1]);
        }

        // 设置namenode的地址，不然jar包会分发，非常恶心
        String namenodes = conditions.getNamenodes();
        String rpcAddress = conditions.getNamenodeRpcAddress();
        String nameservices = conditions.getNameservices();

        conf.set("fs.defaultFS", conditions.getDefaultFS());
        conf.set("dfs.nameservices", nameservices);
        conf.set("dfs.ha.namenodes." + nameservices, namenodes);

        String[] namenodesStr = namenodes.split(SPLIT_COMMA);
        String[] rpcAddressStr = rpcAddress.split(SPLIT_COMMA);
        if (namenodesStr.length != rpcAddressStr.length) {
            throw new ConfigException("namenodes and rpcAddress length is not match.");
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
