package org.dromara.hodor.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import org.dromara.hodor.common.dag.Status;

/**
 * flow job execute detail
 *
 * @author tomgs
 * @since 2021/9/13
 */
@Data
@TableName("hodor_flow_job_exec_detail")
public class FlowJobExecDetail {

    private Long id;

    private Long requestId;

    private String groupName;

    private String jobName;

    private String schedulerName;

    private Status status;

    private Date executeStart;

    private Date executeEnd;

    private Long elapsedTime;

    private Integer encType;

    private byte[] flowExecData;

}
