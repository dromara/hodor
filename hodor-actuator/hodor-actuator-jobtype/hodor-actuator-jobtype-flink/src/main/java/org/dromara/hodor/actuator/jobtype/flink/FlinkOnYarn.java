package org.dromara.hodor.actuator.jobtype.flink;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterInformationRetriever;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.jobtype.bigdata.javautils.JobUtils;
import org.dromara.hodor.actuator.jobtype.bigdata.javautils.YarnSubmitArguments;
import org.dromara.hodor.common.utils.StringUtils;

import static org.apache.flink.configuration.MemorySize.MemoryUnit.MEGA_BYTES;

/**
 * @author tomgs
 * @version 1.0
 */
public class FlinkOnYarn {

    private final Logger logger;

    public FlinkOnYarn(Logger logger) {
        this.logger = logger;
    }

    public ApplicationId submitFlinkJob(YarnSubmitArguments arguments) throws ClusterDeploymentException {
        logger.info(StringUtils.format("请求参数: {}", arguments));

        // identify that you will be using Flink as YARN mode
        System.setProperty("FLINK_YARN_MODE", "true");

        String confDir = arguments.getLocalConfDir();
        String jobName = arguments.getJobName();
        String mainClass = arguments.getMainClass();
        String applicationJar = arguments.getApplicationJar();
        String[] libs = arguments.getLibs();
        String flinkYarnJars = arguments.getYarnJars();
        List<String> otherArgs = arguments.getAppArgs();
        Properties properties = arguments.getProperties();

        // 初始化yarn客户端
        logger.info("初始化 yarn 客户端");
        YarnClient yarnClient = YarnClient.createYarnClient();
        org.apache.hadoop.conf.Configuration hadoopConfiguration = JobUtils.getHadoopConfiguration(arguments);
        YarnConfiguration yarnConfiguration = new YarnConfiguration(hadoopConfiguration);
        yarnClient.init(yarnConfiguration);
        yarnClient.start();

        YarnClusterInformationRetriever clusterInformationRetriever = YarnClientYarnClusterInformationRetriever
            .create(yarnClient);

        logger.info("初始化 flink 客户端");

        //获取flink的配置
        Configuration flinkConfiguration = GlobalConfiguration.loadConfiguration(confDir);
        flinkConfiguration.set(CheckpointingOptions.INCREMENTAL_CHECKPOINTS, true);
        flinkConfiguration.set(PipelineOptions.JARS, Collections.singletonList(applicationJar));
        flinkConfiguration.set(YarnConfigOptions.PROVIDED_LIB_DIRS, Lists.newArrayList(libs));
        flinkConfiguration.set(YarnConfigOptions.FLINK_DIST_JAR, flinkYarnJars);
        //设置为application模式
        flinkConfiguration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());
        //yarn application name
        flinkConfiguration.set(YarnConfigOptions.APPLICATION_NAME, jobName);
        flinkConfiguration.set(JobManagerOptions.TOTAL_PROCESS_MEMORY,
            MemorySize.parse(properties.getProperty("flink.job.manager.memory"), MEGA_BYTES));
        flinkConfiguration.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY,
            MemorySize.parse(properties.getProperty("flink.task.manager.memory"), MEGA_BYTES));

        ClusterSpecification clusterSpecification = new ClusterSpecification.ClusterSpecificationBuilder()
            .createClusterSpecification();

        logger.info("开始提交任务");
        // 设置用户jar的参数和主类
        ApplicationConfiguration appConfig = new ApplicationConfiguration(otherArgs.toArray(new String[0]), mainClass);
        YarnClusterDescriptor yarnClusterDescriptor = new YarnClusterDescriptor(
            flinkConfiguration,
            yarnConfiguration,
            yarnClient,
            clusterInformationRetriever,
            true);
        ClusterClientProvider<ApplicationId> clusterClientProvider = yarnClusterDescriptor.deployApplicationCluster(
            clusterSpecification,
            appConfig);
        logger.info("提交任务成功");
        ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
        ApplicationId applicationId = clusterClient.getClusterId();
        String webInterfaceURL = clusterClient.getWebInterfaceURL();

        logger.info("ApplicationId {}", applicationId);
        logger.info("WebInterfaceURL {}", webInterfaceURL);

        return applicationId;
    }

}
