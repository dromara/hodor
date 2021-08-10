package org.dromara.hodor.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * job executor detail info
 * @author tomgs
 * @since 2020/8/27
 */
@Data
@TableName("hodor_job_exec_detail")
public class JobExecDetail {

    private long id;

}
