package org.dromara.hodor.model.job;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * job instance
 *
 * @author tomgs
 * @since 2021/1/5
 */
@Builder
@Getter
@ToString
public class JobInstance {

    private String groupName;

    private String jobName;

    private String cron;

    private boolean fireNow;

    private Integer timeout;

    private boolean broadcast;

}
