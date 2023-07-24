package org.dromara.hodor.server.common;

import lombok.Data;

/**
 * job command
 *
 * @author tomgs
 * @version 1.0
 */
@Data
public class JobCommand<T> {

    private JobCommandType commandType;

    private T data;

    public JobCommand(JobCommandType jobCommandType, T data) {
        this.commandType = jobCommandType;
        this.data = data;
    }

}
