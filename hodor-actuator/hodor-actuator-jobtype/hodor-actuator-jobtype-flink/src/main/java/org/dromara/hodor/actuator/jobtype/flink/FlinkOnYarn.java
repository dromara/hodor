package org.dromara.hodor.actuator.jobtype.flink;

import com.google.common.collect.Lists;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.*;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterInformationRetriever;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.common.utils.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author tomgs
 * @version 1.0
 */
public class FlinkOnYarn {

    private static final Logger logger = LogManager.getLogger(FlinkOnYarn.class);

    public ApplicationId submitFlinkJob(FlinkSubmitArguments arguments) throws ClusterDeploymentException {
        logger.info(StringUtils.format("请求参数:{}.", arguments));

        // identify that you will be using Flink as YARN mode
        System.setProperty("FLINK_YARN_MODE", "true");

        String confDir = arguments.getLocalConfDir();
        String jobName = arguments.getJobName();
        String mainClass = arguments.getMainClass();
        String applicationJar = arguments.getApplicationJar();
        String[] dependJars = arguments.getDependJars();
        String flinkYarnJars = arguments.getYarnJars();

        // 初始化yarn客户端
        logger.info("初始化spark on yarn客户端");
        List<String> args = Lists.newArrayList("--jar", arguments.getApplicationJar(), "--class",
            arguments.getMainClass());
        if (arguments.getOtherArgs() != null && !arguments.getOtherArgs().isEmpty()) {
            for (String arg : arguments.getOtherArgs()) {
                args.add("--arg");
                args.add(StringUtils.join(new String[]{arg}, ","));
            }
        }

        YarnClient yarnClient = YarnClient.createYarnClient();
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnClient.init(yarnConfiguration);
        yarnClient.start();

        YarnClusterInformationRetriever clusterInformationRetriever = YarnClientYarnClusterInformationRetriever
            .create(yarnClient);

        //获取flink的配置
        Configuration flinkConfiguration = GlobalConfiguration.loadConfiguration(confDir);
        flinkConfiguration.set(CheckpointingOptions.INCREMENTAL_CHECKPOINTS, true);
        flinkConfiguration.set(PipelineOptions.JARS, Collections.singletonList(applicationJar));
        flinkConfiguration.set(YarnConfigOptions.PROVIDED_LIB_DIRS, Lists.newArrayList(dependJars));
        flinkConfiguration.set(YarnConfigOptions.FLINK_DIST_JAR, flinkYarnJars);
        //设置为application模式
        flinkConfiguration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());
        //yarn application name
        flinkConfiguration.set(YarnConfigOptions.APPLICATION_NAME, jobName);


        ClusterSpecification clusterSpecification = new ClusterSpecification.ClusterSpecificationBuilder()
            .createClusterSpecification();

        // 设置用户jar的参数和主类
        ApplicationConfiguration appConfig = new ApplicationConfiguration(args.toArray(new String[0]), mainClass);

        YarnClusterDescriptor yarnClusterDescriptor = new YarnClusterDescriptor(
            flinkConfiguration,
            yarnConfiguration,
            yarnClient,
            clusterInformationRetriever,
            true);
        ClusterClientProvider<ApplicationId> clusterClientProvider = yarnClusterDescriptor.deployApplicationCluster(
            clusterSpecification,
            appConfig);
        ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
        return clusterClient.getClusterId();
    }

}
