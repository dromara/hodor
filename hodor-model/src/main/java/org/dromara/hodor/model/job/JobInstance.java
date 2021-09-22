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

    private final String groupName;

    private final String jobName;

    private final String commandType;

    private final String cron;

    private final boolean fireNow;

    private final Integer timeout;

    private final boolean broadcast;

}
