package org.dromara.hodor.actuator.bigdata.jobtype.javautils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.*;
import org.apache.log4j.*;
import org.dromara.hodor.actuator.bigdata.jobtype.MapReduceJob2State;
import org.dromara.hodor.actuator.bigdata.jobtype.StatsUtils;
import org.dromara.hodor.actuator.bigdata.utils.JSONUtils;
import org.dromara.hodor.actuator.common.utils.Props;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.apache.hadoop.security.UserGroupInformation.HADOOP_TOKEN_FILE_LOCATION;
import static org.dromara.hodor.actuator.bigdata.executor.CommonJobProperties.JOB_ATTACHMENT_FILE;
import static org.dromara.hodor.actuator.bigdata.security.commons.HadoopSecurityManager.MAPREDUCE_JOB_CREDENTIALS_BINARY;

/**
 * Compatible with hadoop2.x
 *
 * @author tangzhongyuan
 * @create 2018-12-11 14:11
 **/
public abstract class AbstractHadoop2Job {

    private static final Layout DEFAULT_LAYOUT = new PatternLayout("%p %m\n");
    private static final Logger logger = getLogger();

    private final Props props;
    private final String jobName;

    private Job job;
    private Configuration conf;

    private boolean visualizer;
    private MapReduceJob2State mapReduceJobState;
    private String jobStatsFileName;

    protected AbstractHadoop2Job(String name, Props props) throws IOException {
        this.props = props;
        this.jobName = name;

        this.conf = getJobConf();

        setSubmitUser(props);

        logger.info("fs.defaultFS=" + conf.get("fs.defaultFS"));

        this.job = Job.getInstance(conf, name);

        visualizer = props.getBoolean("mr.listener.visualizer", false) == true;
        if (visualizer == true) {
            jobStatsFileName = props.getString(JOB_ATTACHMENT_FILE);
        }
    }

    private void setSubmitUser(Props props) {
        if (props.containsKey("user.to.proxy")) {
            String submitUser = props.getString("user.to.proxy");
            logger.info("submit user is : " + submitUser);
            System.setProperty("HADOOP_USER_NAME", submitUser);
            System.setProperty("HADOOP_PROXY_USER", submitUser);
        }
    }

    private Configuration getJobConf() throws IOException {
        this.conf = new Configuration();
        if (System.getenv(HADOOP_TOKEN_FILE_LOCATION) != null) {
            conf.set(MAPREDUCE_JOB_CREDENTIALS_BINARY,
                    System.getenv(HADOOP_TOKEN_FILE_LOCATION));
        }
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.unset("mapred.input.format.class");
        return conf;
    }

    public Job getJob() {
        return this.job;
    }

    public Configuration getConf() {
        return this.conf;
    }

    public String getJobName() {
        return this.jobName;
    }

    public Props getProps() {
        return props;
    }

    public void run() throws Exception {

        Job runningJob = getJob();

        Configuration conf = runningJob.getConfiguration();
        boolean isNewApi = conf.getBoolean("mapred.mapper.new-api", false);

        conf.unset("mapred.input.format.class");
        conf.unset("mapred.output.format.class");
        conf.unset("mapred.mapper.class");
        conf.unset("mapred.reducer.class");
        conf.unset("mapred.output.key.class");
        conf.unset("mapred.output.value.class");

        String inputClass = conf.get("mapred.input.format.class");
        logger.info("Is new API:" + isNewApi + ", has old API:" + inputClass);

        logger.info("Start submit job!");
        runningJob.submit();
        logger.info("See " + runningJob.getTrackingURL() + " for details.");

        if (props.getBoolean("execute.as.async", false)) {
            logger.info("jobId: " + runningJob.getJobID());
            return;
        }
        runningJob.monitorAndPrintJob();

        if (!runningJob.isSuccessful()) {
            throw new Exception("Hadoop job:" + getJobName() + " failed!");
        }

        // dump all counters
        Counters counters = runningJob.getCounters();
        for (String groupName : counters.getGroupNames()) {
            CounterGroup group = counters.getGroup(groupName);
            logger.info("Group: " + group.getDisplayName());
            for (Counter counter : group) {
                logger.info(counter.getDisplayName() + ":\t" + counter.getValue());
            }
        }
        updateMapReduceJobState(conf, runningJob);
    }

    private void updateMapReduceJobState(Configuration conf, Job runningJob) {
        if (runningJob == null || visualizer == false) {
            return;
        }

        try {
            //JobID jobID = runningJob.getJobID();
            TaskReport[] mapTaskReport = runningJob.getTaskReports(TaskType.MAP);
            TaskReport[] reduceTaskReport = runningJob.getTaskReports(TaskType.REDUCE);

            mapReduceJobState =
                    new MapReduceJob2State(runningJob, mapTaskReport, reduceTaskReport);
            writeMapReduceJobState(conf);
        } catch (IOException | InterruptedException e) {
            logger.error("Cannot update MapReduceJobState");
        }
    }

    private Object statsToJson(Configuration jobConf) {
        List<Object> jsonObj = new ArrayList<Object>();
        Map<String, Object> jobJsonObj = new HashMap<String, Object>();
        Properties conf = StatsUtils.getJobConf(jobConf);
        jobJsonObj.put("state", mapReduceJobState.toJson());
        jobJsonObj.put("conf", StatsUtils.propertiesToJson(conf));
        jsonObj.add(jobJsonObj);
        return jsonObj;
    }

    private void writeMapReduceJobState(Configuration jobConf) {
        File mrStateFile = null;
        try {
            mrStateFile = new File(jobStatsFileName);
            JSONUtils.toJSON(statsToJson(jobConf), mrStateFile);
        } catch (Exception e) {
            logger.error("Cannot write JSON file.");
        }
    }

    protected static Logger getLogger() {
        Logger logger = Logger.getRootLogger();
        logger.removeAllAppenders();
        ConsoleAppender appender = new ConsoleAppender();
        appender.setLayout(DEFAULT_LAYOUT);
        appender.activateOptions();
        logger.addAppender(appender);
        logger.setLevel(Level.INFO); //Explicitly setting level to INFO
        return logger;
    }

}
