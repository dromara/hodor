package org.dromara.hodor.remoting.netty.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  
 *
 * @author tomgs
 * @version 2020/9/13 1.0 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestBody {

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
