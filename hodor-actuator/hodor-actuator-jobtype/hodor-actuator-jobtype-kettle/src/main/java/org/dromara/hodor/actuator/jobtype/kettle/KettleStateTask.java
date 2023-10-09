package org.dromara.hodor.actuator.jobtype.kettle;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.api.core.ExecutableJobContext;
import org.dromara.hodor.common.utils.Props;
import org.dromara.hodor.actuator.jobtype.api.queue.AbstractAsyncTask;
import org.dromara.hodor.actuator.jobtype.api.queue.AsyncTask;
import org.pentaho.di.job.Job;

/**
 * @author tomgs
 * @version 1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class KettleStateTask extends AbstractAsyncTask {

    private Job job;

    private Logger log;

    private Long requestId;

    private String logChannelId;

    private int startLineIndex;

    private ExecutableJobContext jobContext;

    private Props props;

    @Override
    protected AsyncTask runTask() {
        // 无论有没有错都要获取日志
        // 是否已经完成
        boolean isFinished = job.isFinished() || job.isStopped();
        if (isFinished) {
            return null;
        }

        if (job.getErrors() > 0) {
            return null;
        }

        return this;
    }

}
