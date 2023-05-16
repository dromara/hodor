package org.dromara.hodor.admin.dto;

import lombok.Data;

/**
 * @author tomgs
 * 
 * @since 1.0
 **/
@Data
public class JobNodeDTO {

    private String jobNodeName;

    private Integer jobStatus;
    private String createTime;
    private String nextExcuteTime;
    private String jobType;
    private String cronExpression;
    private String requestId;
}
