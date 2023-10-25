package org.dromara.hodor.actuator.jobtype.flink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.api.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.jobtype.api.executor.AbstractJob;
import org.dromara.hodor.actuator.jobtype.api.queue.AsyncTaskStateChecker;
import org.dromara.hodor.actuator.jobtype.bigdata.HadoopJobUtils;
import org.dromara.hodor.actuator.jobtype.bigdata.javautils.AsyncJobStateTask;
import org.dromara.hodor.actuator.jobtype.bigdata.javautils.YarnSubmitArguments;
import org.dromara.hodor.common.utils.Props;

/**
 * FlinkJob
 *
 * @author tomgs
 * @version 1.0
 */
public class FlinkJob extends AbstractJob {

    private final FlinkOnYarn flinkOnYarn;

    private final String jobId;

    private final Props sysProps;

    private final Props jobProps;

    private final Logger logger;

    protected FlinkJob(String jobId, Props sysProps, Props jobProps, Logger logger) {
        super(jobId, sysProps, jobProps, logger);
        this.jobId = jobId;
        this.sysProps = sysProps;
        this.jobProps = jobProps;
        this.logger = logger;
        this.flinkOnYarn = new FlinkOnYarn(logger);
    }

    @Override
    public void run() throws Exception {
        YarnSubmitArguments arguments = new YarnSubmitArguments();
        //set application config
        arguments.setJobName(jobProps.getString("job.name", jobId));
        arguments.setMainClass(jobProps.getString("job.class"));
        arguments.setApplicationJar(jobProps.getString("job.executor.jar"));

        List<String> args = new ArrayList<>();
        if (jobProps.containsKey("job.executor.args")) {
            String[] applicationArgs = jobProps.getString("job.executor.args").split(",");
            args.addAll(Arrays.asList(applicationArgs));
            arguments.setAppArgs(args);
        }
        if (jobProps.containsKey("job.execution.dependJars")) {
            arguments.setDependJars(jobProps.getString("job.execution.dependJars").split(","));
        }
        if (jobProps.containsKey("job.files")) {
            String files = jobProps.getString("job.files");
            arguments.setFiles(files.split(","));
        }

        //set yarn config
        arguments.setQueue(jobProps.getString("yarn.queue", "default"));
        arguments.setYarnJars(jobProps.getString("yarn.jars", sysProps.getString("yarn.jars")));
        String resourceManagerAddress = jobProps.getString("resourceManagerAddress",
            sysProps.getString("yarn.resource.manager.address"));
        arguments.setYarnResourcemanagerAddress(resourceManagerAddress);

        //set hdfs config
        arguments.setDefaultFS(jobProps.getString("hdfs.defalutFS", sysProps.getString("hdfs.defalutFS")));
        arguments.setNameservices(jobProps.getString("hdfs.nameservices", sysProps.getString("hdfs.nameservices")));
        arguments.setNamenodes(jobProps.getString("hdfs.namenodes", sysProps.getString("hdfs.namenodes")));
        arguments.setNamenodeRpcAddress(jobProps.getString("hdfs.rpc-address", sysProps.getString("hdfs.rpc-address")));

        Properties properties = new Properties();
        properties.put("flink.job.manager.memory", jobProps.getString("flink.job.manager.memory", "512m"));
        properties.put("flink.task.manager.memory", jobProps.getString("flink.task.manager.memory", "512m"));
        arguments.setProperties(properties);

        ApplicationId applicationId = flinkOnYarn.submitFlinkJob(arguments);
        logger.info("jobId:" + jobId + " submit result : applicationId: " + applicationId);

        if (applicationId == null) {
            throw new JobExecutionException("applicationId is null, failed to run job " + jobId);
        }

        ApplicationReport report = HadoopJobUtils.fetchJobStateOnCluster(applicationId.toString());
        String trackingUrl = report.getTrackingUrl();
        logger.info("详细情况请查看tracking url :" + trackingUrl);

        Long requestId = jobProps.getLong("requestId");
        AsyncJobStateTask task = new AsyncJobStateTask(applicationId.toString(), requestId, jobProps, logger);
        AsyncTaskStateChecker stateChecker = AsyncTaskStateChecker.getInstance();
        stateChecker.addTask(task);

        logger.info("current queue size : " + stateChecker.queueSize());
    }

}
