package org.dromara.hodor.remoting.netty.rpc;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  request body
 *
 * @author tomgs
 * @version 2020/9/13 1.0 
 */
@Data
@Builder
@NoArgsConstructor
public class SchedulerRequestBody implements RequestBody {

    private static final long serialVersionUID = -3703044901986185064L;

    private String requestId;

    private String jobName;

    private String groupName;

    private String jobPath;

    private String jobCommand;

    private String jobCommandType;

    private String jobParameters;

    private String extensibleParameters;

    private Integer timeout;

}
