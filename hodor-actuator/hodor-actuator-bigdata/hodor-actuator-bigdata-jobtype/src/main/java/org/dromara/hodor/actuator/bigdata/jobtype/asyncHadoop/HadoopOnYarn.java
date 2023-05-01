package org.dromara.hodor.actuator.bigdata.jobtype.asyncHadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author tomgs
 * @since 1.0
 **/
public class HadoopOnYarn {

    private static final Logger logger = LogManager.getLogger(HadoopOnYarn.class);

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
