package org.dromara.hodor.admin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 执行中的状态信息
 *
 * @author tomgs
 * @since 1.0
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobExecutingInfo {

    private int id;
    private String requestId;
    private String groupName;
    private String jobName;
    private String postGroupName;
    private String postJobName;
    private Date createTime;
    private Date updateTime;
    private long timeout;
}
