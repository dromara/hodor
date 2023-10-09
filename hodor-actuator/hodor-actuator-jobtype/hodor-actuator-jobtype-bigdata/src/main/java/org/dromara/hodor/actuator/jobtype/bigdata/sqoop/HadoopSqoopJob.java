package org.dromara.hodor.actuator.jobtype.bigdata.sqoop;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.common.utils.Props;
import org.dromara.hodor.actuator.jobtype.api.executor.ProcessJob;

/**
 * sqoop任务<br/>
 *
 * 直接开启sqoop子进程进行调用
 *
 * @author tomgs
 * @since 1.0
 **/
public class HadoopSqoopJob extends ProcessJob {

    public static final String SQOOP_ARGS = "sqoop.args";
    private static final String SQOOP_COMMAND = "sqoop";
    public static final String SQOOP_BIN = "sqoop.bin";

    public HadoopSqoopJob(String jobid, Props sysProps, Props jobProps, Logger logger) {
        super(jobid, sysProps, jobProps, logger);
    }

    @Override
    protected List<String> getCommandList() {
        final List<String> commands = new ArrayList<>();
        String sqoopBinPath = this.jobProps.getString(SQOOP_BIN, SQOOP_COMMAND);
        String sqoopArgs = this.jobProps.getString(SQOOP_ARGS, "");

        commands.add(sqoopBinPath + " " + sqoopArgs);
        for (int i = 1; this.jobProps.containsKey(SQOOP_ARGS + "." + i); i++) {
            commands.add(sqoopBinPath + " "
                    + this.jobProps.getString(SQOOP_ARGS + "." + i));
        }

        return commands;
    }
}
