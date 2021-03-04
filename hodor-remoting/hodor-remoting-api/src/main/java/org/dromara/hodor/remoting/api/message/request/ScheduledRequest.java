package org.dromara.hodor.remoting.api.message.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.hodor.remoting.api.message.RequestBody;

/**
 *  request body
 *
 * @author tomgs
 * @version 2020/9/13 1.0 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledRequest implements RequestBody {

    private static final long serialVersionUID = -3703044901986185064L;

    private Long requestId;

    private String jobName;

    private String groupName;

    private String jobPath;

    private String jobCommand;

    private String jobCommandType;

    private String jobParameters;

    private String extensibleParameters;

    private Integer timeout;

    private Integer shardId;

    private String shardName;

}
