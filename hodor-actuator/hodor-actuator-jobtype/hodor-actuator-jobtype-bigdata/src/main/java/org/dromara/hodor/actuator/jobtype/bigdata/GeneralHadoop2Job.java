package org.dromara.hodor.actuator.jobtype.bigdata;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.api.utils.Props;
import org.dromara.hodor.actuator.jobtype.bigdata.javautils.AbstractHadoop2Job;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Compatible with hadoop2.x
 *
 * @author tomgs
 * @since 1.0
 **/
public class GeneralHadoop2Job extends AbstractHadoop2Job {

    private static final Logger logger = getLogger(GeneralHadoop2Job.class);

    private final static Set<String> MARKED_KEY = new HashSet<>();

    private static final String JOB_CLASS = "job.class";

    private static final String MAPREDUCE_OUTPUT_KEY_CLASS = "mapreduce.job.output.key.class";
    private static final String MAPREDUCE_OUTPUT_VALUE_CLASS = "mapreduce.job.output.value.class";

    private static final String MAPREDUCE_MAPPER_CLASS = "mapreduce.job.map.class";
    private static final String MAPREDUCE_REDUCER_CLASS = "mapreduce.job.reduce.class";
    private static final String MAPREDUCE_PARTION_CLASS = "mapreduce.job.partitioner.class";
    private static final String MAPREDUCE_COMBINE_CLASS = "mapreduce.job.combine.class";
    private static final String MAPREDUCE_REDUCER_NUMS = "mapreduce.job.reduces";

    private static final String MAPREDUCE_INPUT_FORMAT_CLASS = "mapreduce.job.inputformat.class";
    private static final String MAPREDUCE_OUTPUT_FORMAT_CLASS = "mapreduce.job.outputformat.class";

    private static final String INPUT_PATH = "input.path";
    private static final String OUTPUT_PATH = "output.path";

    private static final String FORCE_OUTPUT_OVERWRITE = "force.output.overwrite";

    static {
        MARKED_KEY.add(JOB_CLASS);
        MARKED_KEY.add(MAPREDUCE_OUTPUT_KEY_CLASS);
        MARKED_KEY.add(MAPREDUCE_OUTPUT_VALUE_CLASS);
        MARKED_KEY.add(MAPREDUCE_MAPPER_CLASS);
        MARKED_KEY.add(MAPREDUCE_REDUCER_CLASS);
        MARKED_KEY.add(MAPREDUCE_INPUT_FORMAT_CLASS);
        MARKED_KEY.add(MAPREDUCE_OUTPUT_FORMAT_CLASS);
        MARKED_KEY.add(INPUT_PATH);
        MARKED_KEY.add(OUTPUT_PATH);
        MARKED_KEY.add(FORCE_OUTPUT_OVERWRITE);
    }

    public GeneralHadoop2Job(String name, Props props) throws IOException {
        super(name, props);
    }

    public void run() throws Exception {
        Props props = this.getProps();

        Job jobConf = getJob();

        jobConf.setJarByClass(props.getClass(JOB_CLASS));
        jobConf.setOutputKeyClass(props.getClass(MAPREDUCE_OUTPUT_KEY_CLASS));
        jobConf.setOutputValueClass(props.getClass(MAPREDUCE_OUTPUT_VALUE_CLASS));

        jobConf.setMapperClass((Class<? extends Mapper>) props.getClass(MAPREDUCE_MAPPER_CLASS));
        logger.info("Mapper class:" + props.getClass(MAPREDUCE_MAPPER_CLASS));

        jobConf.setReducerClass((Class<? extends Reducer>) props.getClass(MAPREDUCE_REDUCER_CLASS));
        logger.info("Reducer class:" + props.getClass(MAPREDUCE_REDUCER_CLASS));

        Class<?> partionerClass = props.getClass(MAPREDUCE_PARTION_CLASS, null);
        if (partionerClass != null) {
            jobConf.setPartitionerClass((Class<? extends Partitioner>) partionerClass);
            logger.info("Partitioner class:" + props.getClass(MAPREDUCE_PARTION_CLASS));
        }

        Class<?> combineClass = props.getClass(MAPREDUCE_COMBINE_CLASS, null);
        if (combineClass != null) {
            jobConf.setCombinerClass((Class<? extends Reducer>) combineClass);
            logger.info("Combiner class:" + props.getClass(MAPREDUCE_COMBINE_CLASS));
        }

        int reduceNums = props.getInt(MAPREDUCE_REDUCER_NUMS, 0);
        if (reduceNums > 0) {
            jobConf.setNumReduceTasks(reduceNums);
        }

        /*Class<?> inputFormatClass = props.getClass(MAPREDUCE_INPUT_FORMAT_CLASS, null);
        if (inputFormatClass != null) {
            jobConf.setInputFormatClass((Class<? extends InputFormat>) inputFormatClass);
        }

        Class<?> outputClass = props.getClass(MAPREDUCE_OUTPUT_FORMAT_CLASS, null);
        if (outputClass != null) {
            jobConf.setOutputFormatClass((Class<? extends OutputFormat>) outputClass);
        }*/

        FileInputFormat.addInputPath(jobConf, new Path(props.getString(INPUT_PATH)));
        logger.info("Input path:" + props.getString(INPUT_PATH));

        FileOutputFormat.setOutputPath(jobConf, new Path(props.getString(OUTPUT_PATH)));
        logger.info("Output path:" + props.getString(OUTPUT_PATH));

        if (props.getBoolean(FORCE_OUTPUT_OVERWRITE, true)) {
            FileSystem fs = FileOutputFormat.getOutputPath(jobConf).getFileSystem(jobConf.getConfiguration());
            fs.delete(FileOutputFormat.getOutputPath(jobConf), true);
        }

        //set other map-reduce job config parameters
        for (Map.Entry<Object, Object> entry : props.toProperties().entrySet()) {
            if (!MARKED_KEY.contains(entry.getKey())) {
                Configuration configuration = jobConf.getConfiguration();
                configuration.set((String) entry.getKey(), (String) entry.getValue());
            }
        }

        super.run();
    }

}
