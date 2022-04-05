package org.dromara.hodor.actuator.bigdata.jobtype.asyncSpark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.log4j.Logger;
import org.dromara.hodor.actuator.bigdata.core.JobExecutorStateChecker;
import org.dromara.hodor.actuator.bigdata.executor.JavaProcessJob;
import org.dromara.hodor.actuator.bigdata.jobtype.HadoopJobUtils;
import org.dromara.hodor.actuator.bigdata.jobtype.javautils.AsyncJobStateTask;
import org.dromara.hodor.actuator.common.exceptions.JobExecutionException;
import org.dromara.hodor.actuator.common.utils.Props;

/**
 * 异步提交spark任务入口
 * <br/>
 * <p>
 * 请求参数：<br/>
 * job.name -- 任务名称，非必填<br/>
 * job.class -- 任务运行主类名，必填<br/>
 * job.execution.jar -- 任务运行jar包，必填<br/>
 * queue -- yarn队列设置，默认default<br/>
 * job.executon.args -- 任务运行参数，非必填<br/>
 *
 * </p>
 *
 * @author tangzhongyuan
 * @create 2019-03-07 19:02
 **/
public class AsyncSparkJob extends JavaProcessJob {

    private final Logger logger;

    private final String jobid;

    private final Props jobProps;

    public AsyncSparkJob(String jobid, Props sysProps, Props jobProps, Logger logger) {
        super(jobid, sysProps, jobProps, logger);
        this.logger = logger;
        this.jobid = jobid;
        this.jobProps = jobProps;
    }

    @Override
    public void run() throws Exception {
        try {
            resolveProps();
            resolverJars();
        } catch (final Exception e) {
            handleError("Bad property definition! " + e.getMessage(), e);
        }

        SparkOnYarn instance = SparkOnYarn.getInstance();
        YarnSubmitConditions conditions = YarnSubmitConditions.builder().build();

        //set application config
        conditions.setJobName(jobProps.getString("job.name", jobid));
        conditions.setMainClass(jobProps.getString("job.class"));
        conditions.setApplicationJar(jobProps.getString("job.executor.jar"));
        conditions.setQueue(jobProps.getString("queue", "default"));
        List<String> args = new ArrayList<>();

        if (jobProps.containsKey("job.executor.args")) {
            String[] applicationArgs = jobProps.getString("job.executor.args").split(",");
            for (String applicationArg : applicationArgs) {
                args.add(applicationArg);
            }
            conditions.setOtherArgs(args);
        }

        //set yarn config
        conditions.setYarnJars(jobProps.getString("yarn.jars", sysProps.getString("yarn.jars")));
        String resourceManagerAddress = jobProps.getString("resourceManagerAddress",
            sysProps.getString("yarn.resource.manager.address"));
        conditions.setYarnResourcemanagerAddress(resourceManagerAddress);

        if (jobProps.containsKey("job.execution.dependJars")) {
            conditions.setDependJars(jobProps.getString("job.execution.dependJars").split(","));
        }
        if (jobProps.containsKey("spark.files")) {
            String files = jobProps.getString("spark.files");
            conditions.setFiles(files.split(","));
        }

        Properties properties = new Properties();
        properties.put("spark.executor.memory", jobProps.getString("spark.executor.memory", "512m"));
        properties.put("spark.driver.memory", jobProps.getString("spark.driver.memory", "512m"));
        properties.put("spark.executor.cores", jobProps.getString("spark.executor.cores", "1"));

        //spark.executor.extraJavaOptions=-Dhdp.version=2.6.5.0-292
        properties.put("spark.executor.extraJavaOptions", sysProps.getString("spark.executor.extraJavaOptions"));
        properties.put("spark.driver.extraJavaOptions", sysProps.getString("spark.driver.extraJavaOptions"));
        properties.put("spark.am.extraJavaOptions", sysProps.getString("spark.am.extraJavaOptions"));
        //开启spark历史任务追踪
        properties.put("spark.eventLog.enabled", sysProps.getString("spark.eventLog.enabled"));
        properties.put("spark.yarn.historyServer.address", sysProps.getString("spark.yarn.historyServer.address"));
        properties.put("spark.eventLog.dir", sysProps.getString("spark.eventLog.dir"));
        properties.put("spark.history.fs.logDirectory", sysProps.getString("spark.history.fs.logDirectory"));

        conditions.setProperties(properties);
        //set hdfs config
        conditions.setDefaultFS(jobProps.getString("hdfs.defalutFS", sysProps.getString("hdfs.defalutFS")));
        conditions.setNameservices(jobProps.getString("hdfs.nameservices", sysProps.getString("hdfs.nameservices")));
        conditions.setNamenodes(jobProps.getString("hdfs.namenodes", sysProps.getString("hdfs.namenodes")));
        conditions.setNamenodeRpcAddress(jobProps.getString("hdfs.rpc-address", sysProps.getString("hdfs.rpc-address")));

        String applicationId = instance.submitSpark(conditions);
        logger.info("jobid:" + jobid + " submit result : applicationId: " + applicationId);

        if (applicationId == null) {
            throw new JobExecutionException("applicationId is null, failed to run job " + jobid);
        }
        ApplicationReport report = HadoopJobUtils.fetchJobStateOnCluster(applicationId);
        String trackingUrl = report.getTrackingUrl();
        logger.info("详细情况请查看tracking url :" + trackingUrl);

        Long requestId = jobProps.getLong("requestId");
        AsyncJobStateTask task = AsyncJobStateTask.builder()
            .appId(applicationId)
            .requestId(requestId)
            .props(jobProps)
            .build();
        JobExecutorStateChecker stateCheckHandler = JobExecutorStateChecker.getInstance();
        int queueSize = stateCheckHandler.addTask(task);

        logger.info("current queue size : " + queueSize);
    }

    private void resolverJars() throws FileNotFoundException {
        String appJar = jobProps.getString("job.executor.jar");
        String lib = jobProps.getString("job.execution.dependJars", "");
        String conf = jobProps.getString("spark.files", "");

        String workingDir = getWorkingDirectory();
        File workingFiles = new File(workingDir);
        if (!workingFiles.exists()) {
            throw new FileNotFoundException(workingDir + "## this file is not found!");
        }
        File[] files = workingFiles.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".jar") || "lib".equalsIgnoreCase(name)
                    || "conf".equalsIgnoreCase(name)) {
                    return true;
                }
                return false;
            }
        });
        StringBuilder dependJarSb = new StringBuilder();
        StringBuilder filesSb = new StringBuilder();
        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                if (file.getName().equalsIgnoreCase(appJar)) {
                    jobProps.put("job.executor.jar", file.getAbsolutePath());
                } else {
                    dependJarSb.append(file.getAbsolutePath()).append(",");
                }
            }
            if (file.getName().equalsIgnoreCase("lib")) {
                for (File libFile : file.listFiles()) {
                    dependJarSb.append(libFile.getAbsolutePath()).append(",");
                }
            }
            if (file.getName().equalsIgnoreCase("conf")) {
                for (File confFile : file.listFiles()) {
                    filesSb.append(confFile.getAbsoluteFile()).append(",");
                }
            }
        }
        dependJarSb.append(lib);
        filesSb.append(conf);

        jobProps.put("job.execution.dependJars", dependJarSb.toString());
        jobProps.put("spark.flies", filesSb.toString());
    }

}
