package org.dromara.hodor.actuator.jobtype.flink;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.api.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.jobtype.api.executor.AbstractJob;
import org.dromara.hodor.actuator.jobtype.api.queue.AsyncTaskStateChecker;
import org.dromara.hodor.actuator.jobtype.bigdata.HadoopJobUtils;
import org.dromara.hodor.actuator.jobtype.bigdata.javautils.AsyncJobStateTask;
import org.dromara.hodor.common.utils.Props;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
        flinkOnYarn = new FlinkOnYarn();
    }

    @Override
    public void run() throws Exception {
        FlinkSubmitArguments arguments = new FlinkSubmitArguments();
        //set application config
        arguments.setJobName(jobProps.getString("job.name", jobId));
        arguments.setMainClass(jobProps.getString("job.class"));
        arguments.setApplicationJar(jobProps.getString("job.executor.jar"));
        arguments.setQueue(jobProps.getString("queue", "default"));
        List<String> args = new ArrayList<>();

        if (jobProps.containsKey("job.executor.args")) {
            String[] applicationArgs = jobProps.getString("job.executor.args").split(",");
            args.addAll(Arrays.asList(applicationArgs));
            arguments.setOtherArgs(args);
        }

        //set yarn config
        arguments.setYarnJars(jobProps.getString("yarn.jars", sysProps.getString("yarn.jars")));
        String resourceManagerAddress = jobProps.getString("resourceManagerAddress",
            sysProps.getString("yarn.resource.manager.address"));
        arguments.setYarnResourcemanagerAddress(resourceManagerAddress);

        if (jobProps.containsKey("job.execution.dependJars")) {
            arguments.setDependJars(jobProps.getString("job.execution.dependJars").split(","));
        }
        if (jobProps.containsKey("flink.files")) {
            String files = jobProps.getString("flink.files");
            arguments.setFiles(files.split(","));
        }

        Properties properties = new Properties();
        properties.put("flink.executor.memory", jobProps.getString("flink.executor.memory", "512m"));
        properties.put("flink.driver.memory", jobProps.getString("flink.driver.memory", "512m"));
        properties.put("flink.executor.cores", jobProps.getString("flink.executor.cores", "1"));

        //flink.executor.extraJavaOptions=-Dhdp.version=2.6.5.0-292
        properties.put("flink.executor.extraJavaOptions", sysProps.getString("flink.executor.extraJavaOptions"));
        properties.put("flink.driver.extraJavaOptions", sysProps.getString("flink.driver.extraJavaOptions"));
        properties.put("flink.am.extraJavaOptions", sysProps.getString("flink.am.extraJavaOptions"));
        //开启flink历史任务追踪
        properties.put("flink.eventLog.enabled", sysProps.getString("flink.eventLog.enabled"));
        properties.put("flink.yarn.historyServer.address", sysProps.getString("flink.yarn.historyServer.address"));
        properties.put("flink.eventLog.dir", sysProps.getString("flink.eventLog.dir"));
        properties.put("flink.history.fs.logDirectory", sysProps.getString("flink.history.fs.logDirectory"));

        arguments.setProperties(properties);
        //set hdfs config
        arguments.setDefaultFS(jobProps.getString("hdfs.defalutFS", sysProps.getString("hdfs.defalutFS")));
        arguments.setNameservices(jobProps.getString("hdfs.nameservices", sysProps.getString("hdfs.nameservices")));
        arguments.setNamenodes(jobProps.getString("hdfs.namenodes", sysProps.getString("hdfs.namenodes")));
        arguments.setNamenodeRpcAddress(jobProps.getString("hdfs.rpc-address", sysProps.getString("hdfs.rpc-address")));

        ApplicationId applicationId = flinkOnYarn.submitFlinkJob(arguments);
        logger.info("jobid:" + jobId + " submit result : applicationId: " + applicationId);

        if (applicationId == null) {
            throw new JobExecutionException("applicationId is null, failed to run job " + jobId);
        }

        ApplicationReport report = HadoopJobUtils.fetchJobStateOnCluster(applicationId.toString());
        String trackingUrl = report.getTrackingUrl();
        logger.info("详细情况请查看tracking url :" + trackingUrl);

        Long requestId = jobProps.getLong("requestId");
        AsyncJobStateTask task = new AsyncJobStateTask();
        task.setAppId(applicationId.toString());
        task.setRequestId(requestId);
        task.setProps(jobProps);
        AsyncTaskStateChecker stateChecker = AsyncTaskStateChecker.getInstance();
        stateChecker.addTask(task);

        logger.info("current queue size : " + stateChecker.queueSize());
    }

}
