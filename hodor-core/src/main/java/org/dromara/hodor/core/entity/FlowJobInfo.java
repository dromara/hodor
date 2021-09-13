package org.dromara.hodor.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * flow job info
 *
 * @author tomgs
 * @since 2021/9/13
 */
@Data
@TableName("hodor_flow_job_info")
public class FlowJobInfo {

    private Long id;

    private String groupName;

    private String jobName;

    private Date createTime;

    private Date updateTime;

    private Integer encType;

    private byte[] flowData;

}
