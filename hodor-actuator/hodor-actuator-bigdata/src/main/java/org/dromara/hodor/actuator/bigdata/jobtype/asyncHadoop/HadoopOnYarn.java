package org.dromara.hodor.actuator.bigdata.jobtype.asyncHadoop;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tangzhongyuan
 * @create 2019-03-11 18:24
 **/
public class HadoopOnYarn {

    private static final Logger logger = LoggerFactory.getLogger(HadoopOnYarn.class);
    private static final String SPLIT_COMMA = ",";

    private static volatile HadoopOnYarn hadoopOnYarn = null;

    private HadoopOnYarn() {

    }

    public static synchronized HadoopOnYarn getInstance() {
        if (hadoopOnYarn == null) {
            hadoopOnYarn = new HadoopOnYarn();
        }
        return hadoopOnYarn;
    }

    public String submitJob() {
        Configuration conf = new Configuration();

        return null;
    }

}
