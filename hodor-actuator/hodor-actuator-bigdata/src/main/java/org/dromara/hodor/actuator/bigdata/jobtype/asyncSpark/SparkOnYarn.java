package org.dromara.hodor.actuator.bigdata.jobtype.asyncSpark;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.dromara.hodor.actuator.bigdata.exception.JobExecutionException;
import org.dromara.hodor.actuator.bigdata.jobtype.javautils.JobUtils;
import org.dromara.hodor.actuator.bigdata.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 用于spark 提交到yarn
 *
 * @author tangzhongyuan
 * @create 2019-03-06 15:49
 **/
public class SparkOnYarn {

    private static final Logger logger = LoggerFactory.getLogger(SparkOnYarn.class);

    private static volatile SparkOnYarn sparkOnYarn = null;

    private SparkOnYarn() {

    }

    public static synchronized SparkOnYarn getInstance() {
        if (sparkOnYarn == null) {
            sparkOnYarn = new SparkOnYarn();
        }
        return sparkOnYarn;
    }

    public String submitSpark(YarnSubmitConditions conditions) {
        logger.info("请求参数:{}.", conditions);

        // 初始化yarn客户端
        logger.info("初始化spark on yarn客户端");
        List<String> args = Lists.newArrayList("--jar", conditions.getApplicationJar(), "--class",
                conditions.getMainClass());
        if (conditions.getOtherArgs() != null && conditions.getOtherArgs().size() > 0) {
            for (String arg : conditions.getOtherArgs()) {
                args.add("--arg");
                args.add(StringUtils.join(new String[] { arg }, ","));
            }
        }

        // identify that you will be using Spark as YARN mode
        System.setProperty("SPARK_YARN_MODE", "true");
        // 初始化 spark的配置
        SparkConf sparkConf = JobUtils.getSparkConf(conditions);
        // 初始化 yarn的配置
        Configuration conf = JobUtils.getHadoopConfiguration(conditions);

        ClientArguments cArgs = new ClientArguments(args.toArray(new String[0]));
        Client client = new Client(cArgs, conf, sparkConf);
        logger.info("提交任务，HadoopJavaJobRunnerMain任务名称：" + conditions.getJobName());

        try {
            ApplicationId appId = client.submitApplication();
            return appId.toString();
        } catch (Exception e) {
            logger.error("提交spark任务失败", e);
            throw new JobExecutionException("提交spark任务失败", e);
        } finally {
            client.stop();
        }
    }

    /**
     * 停止spark任务
     *
     * @param yarnResourcemanagerAddress
     *            yarn资源管理器地址， 例如：master:8032，查看yarn集群获取具体地址
     * @param appIdStr
     *            需要取消的任务id
     */
    public void killJob(String yarnResourcemanagerAddress, String appIdStr) {
        logger.info("取消spark任务,任务id：{}", appIdStr);
        // 初始化 yarn的配置
        Configuration cf = new Configuration();
        String os = System.getProperty("os.name");
        boolean cross_platform = false;
        if (os.contains("Windows")) {
            cross_platform = true;
        }
        cf.setBoolean("mapreduce.app-submission.cross-platform", cross_platform);// 配置使用跨平台提交任务
        // 设置yarn资源，不然会使用localhost:8032
        // 启用yarn的高可用，默认关闭
        cf.setBoolean("yarn.resourcemanager.ha.enabled", true);
        cf.set("yarn.resourcemanager.ha.rm-ids", "rm1,rm2");
        // 设置yarn资源，不然会使用localhost:8032
        String[] hostPort = RegexUtil.getResourceHostPort(yarnResourcemanagerAddress);
        cf.set("yarn.resourcemanager.address.rm1", hostPort[0]);
        // 设置yarn资源，不然会使用localhost:8032
        if (hostPort.length >= 2) {
            cf.set("yarn.resourcemanager.address.rm2", hostPort[1]);
        }
        // 创建yarn的客户端，此类中有杀死任务的方法
        YarnClient yarnClient = YarnClient.createYarnClient();
        // 初始化yarn的客户端
        yarnClient.init(cf);
        // yarn客户端启动
        yarnClient.start();
        try {
            // 根据应用id，杀死应用
            yarnClient.killApplication(getAppId(appIdStr));
        } catch (Exception e) {
            logger.error("取消spark任务失败", e);
        }
        // 关闭yarn客户端
        yarnClient.stop();

    }

    /**
     * 获取spark任务状态
     *
     *
     * @param yarnResourcemanagerAddress
     *            yarn资源管理器地址， 例如：master:8032，查看yarn集群获取具体地址
     * @param appIdStr
     *            需要取消的任务id
     */
    public SparkTaskState getStatus(String yarnResourcemanagerAddress, String appIdStr) {
        logger.info("获取任务状态启动，任务id：{}", appIdStr);
        // 初始化 yarn的配置
        Configuration cf = new Configuration();
        String os = System.getProperty("os.name");
        boolean cross_platform = false;
        if (os.contains("Windows")) {
            cross_platform = true;
        }
        cf.setBoolean("mapreduce.app-submission.cross-platform", cross_platform);// 配置使用跨平台提交任务
        // 设置yarn资源，不然会使用localhost:8032
        // 启用yarn的高可用，默认关闭
        cf.setBoolean("yarn.resourcemanager.ha.enabled", true);
        cf.set("yarn.resourcemanager.ha.rm-ids", "rm1,rm2");
        // 设置yarn资源，不然会使用localhost:8032
        String[] hostPort = RegexUtil.getResourceHostPort(yarnResourcemanagerAddress);
        cf.set("yarn.resourcemanager.address.rm1", hostPort[0]);
        // 设置yarn资源，不然会使用localhost:8032
        if (hostPort.length >= 2) {
            cf.set("yarn.resourcemanager.address.rm2", hostPort[1]);
        }
        logger.info("获取任务状态，任务id: {}", appIdStr);

        SparkTaskState taskState = SparkTaskState.builder().build();
        // 设置任务id
        taskState.setAppId(appIdStr);
        YarnClient yarnClient = YarnClient.createYarnClient();
        // 初始化yarn的客户端
        yarnClient.init(cf);
        // yarn客户端启动
        yarnClient.start();
        ApplicationReport report = null;
        try {
            report = yarnClient.getApplicationReport(getAppId(appIdStr));
        } catch (Exception e) {
            logger.error("获取spark任务状态失败", e);
        }

        if (report != null) {
            YarnApplicationState state = report.getYarnApplicationState();
            taskState.setState(state);
            // 任务执行进度
            float progress = report.getProgress();
            taskState.setProgress(progress);
            // 最终状态
            FinalApplicationStatus status = report.getFinalApplicationStatus();
            taskState.setFinalStatus(status);
        } else {
            taskState.setState(YarnApplicationState.FAILED);
            taskState.setProgress(0.0f);
            taskState.setFinalStatus(FinalApplicationStatus.FAILED);
        }

        // 关闭yarn客户端
        yarnClient.stop();
        logger.info("获取任务状态结束，任务状态： {}", JSONUtils.toJSON(taskState));
        return taskState;
    }

    /**
     * 根据spark的任务id字符串获取对象
     *
     * @param appIdStr
     *            String类型的id
     * @return 应用id的对象
     */
    public ApplicationId getAppId(String appIdStr) {
        return ConverterUtils.toApplicationId(appIdStr);
    }

}
