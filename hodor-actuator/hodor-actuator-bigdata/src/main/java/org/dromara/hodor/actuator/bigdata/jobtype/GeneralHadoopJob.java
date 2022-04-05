package org.dromara.hodor.actuator.bigdata.jobtype;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.*;
import org.apache.log4j.Logger;
import org.dromara.hodor.actuator.bigdata.jobtype.javautils.AbstractHadoopJob;
import org.dromara.hodor.actuator.common.utils.Props;

/**
 * Compatible with hadoop1.x
 *
 * @author tangzhongyuan
 * @create 2018-12-08 15:25
 **/
public class GeneralHadoopJob extends AbstractHadoopJob {

    private final static Set<String> MARKED_KEY = new HashSet<>();

    private static final Logger logger = getLogger();

    private static final String JOB_CLASS = "job.class";

    private static final String MAPRED_OUTPUT_KEY_CLASS = "mapred.output.key.class";
    private static final String MAPRED_OUTPUT_VALUE_CLASS = "mapred.output.value.class";

    private static final String MAPRED_COMBINE_CLASS = "mapred.combine.class";
    private static final String MAPRED_PARTION_CLASS = "mapred.partitioner.class";
    private static final String MAPRED_REDUCER_NUMS = "mapred.job.reduces";
    private static final String MAPRED_MAPPER_CLASS = "mapred.mapper.class";
    private static final String MAPRED_REDUCER_CLASS = "mapred.reducer.class";

    private static final String MAPRED_INPUT_FORMAT_CLASS = "mapred.input.format.class";
    private static final String MAPRED_OUTPUT_FORMAT_CLASS = "mapred.output.format.class";

    private static final String INPUT_PATH = "input.path";
    private static final String OUTPUT_PATH = "output.path";

    private static final String FORCE_OUTPUT_OVERWRITE = "force.output.overwrite";

    static {
        MARKED_KEY.add(JOB_CLASS);
        MARKED_KEY.add(MAPRED_OUTPUT_KEY_CLASS);
        MARKED_KEY.add(MAPRED_OUTPUT_VALUE_CLASS);
        MARKED_KEY.add(MAPRED_MAPPER_CLASS);
        MARKED_KEY.add(MAPRED_REDUCER_CLASS);
        MARKED_KEY.add(MAPRED_INPUT_FORMAT_CLASS);
        MARKED_KEY.add(MAPRED_OUTPUT_FORMAT_CLASS);
        MARKED_KEY.add(INPUT_PATH);
        MARKED_KEY.add(OUTPUT_PATH);
        MARKED_KEY.add(FORCE_OUTPUT_OVERWRITE);
    }

    public GeneralHadoopJob(String name, Props props) {
        super(name, props);
    }

    public void run() throws Exception {
        Props props = this.getProps();
        //set up conf
        JobConf jobConf = getJobConf();
        jobConf.setJarByClass(props.getClass(JOB_CLASS));
        jobConf.setOutputKeyClass(props.getClass(MAPRED_OUTPUT_KEY_CLASS));
        jobConf.setOutputValueClass(props.getClass(MAPRED_OUTPUT_VALUE_CLASS));

        jobConf.setMapperClass((Class<? extends Mapper>) props.getClass(MAPRED_MAPPER_CLASS));
        logger.info("Mapper class:" + props.getClass(MAPRED_MAPPER_CLASS));

        jobConf.setReducerClass((Class<? extends Reducer>) props.getClass(MAPRED_REDUCER_CLASS));
        logger.info("Reducer class:" + props.getClass(MAPRED_REDUCER_CLASS));

        Class<?> combineClass = props.getClass(MAPRED_COMBINE_CLASS, null);
        if (combineClass != null) {
            jobConf.setCombinerClass((Class<? extends Reducer>) combineClass);
        }

        Class<?> partionerClass = props.getClass(MAPRED_PARTION_CLASS, null);
        if (partionerClass != null) {
            jobConf.setPartitionerClass((Class<? extends Partitioner>) partionerClass);
        }

        int reduceNums = props.getInt(MAPRED_REDUCER_NUMS, 0);
        if (reduceNums > 0) {
            jobConf.setNumReduceTasks(reduceNums);
        }

        jobConf.setInputFormat((Class<? extends InputFormat>) props.getClass(MAPRED_INPUT_FORMAT_CLASS));
        jobConf.setOutputFormat((Class<? extends OutputFormat>) props.getClass(MAPRED_OUTPUT_FORMAT_CLASS));

        FileInputFormat.addInputPath(jobConf, new Path(props.getString(INPUT_PATH)));
        logger.info("Input path:" + props.getString(INPUT_PATH));

        FileOutputFormat.setOutputPath(jobConf, new Path(props.getString(OUTPUT_PATH)));
        logger.info("Output path:" + props.getString(OUTPUT_PATH));

        if (props.getBoolean(FORCE_OUTPUT_OVERWRITE, true)) {
            FileSystem fs = FileOutputFormat.getOutputPath(jobConf).getFileSystem(jobConf);
            fs.delete(FileOutputFormat.getOutputPath(jobConf), true);
        }

        //set other map-reduce job config parameters
        for (Map.Entry<Object, Object> entry : props.toProperties().entrySet()) {
            if (!MARKED_KEY.contains(entry.getKey())) {
                jobConf.set((String) entry.getKey(), (String) entry.getValue());
            }
        }

        super.run();
    }

}
