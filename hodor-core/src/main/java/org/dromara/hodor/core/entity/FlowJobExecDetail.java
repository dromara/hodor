package org.dromara.hodor.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

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

    private Date createTime;

    private Date updateTime;

    private Integer encType;

    private byte[] flowExecData;

}
