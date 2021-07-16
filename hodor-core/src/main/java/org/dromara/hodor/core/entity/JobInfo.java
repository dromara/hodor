package org.dromara.hodor.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.hodor.model.enums.JobStatus;
import org.dromara.hodor.core.JobDesc;

/**
 * hodor job info
 *
 * @author tomgs
 * @version 2020/8/2 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hodor_job_info")
public class JobInfo extends JobDesc {

    private JobStatus jobStatus;

    private Date activeTime;

    private Date nextExecuteTime;

    private Date prevExecuteTime;

    private Date createTime;

    private String jobDataPath;

    private String jobDesc;

}
