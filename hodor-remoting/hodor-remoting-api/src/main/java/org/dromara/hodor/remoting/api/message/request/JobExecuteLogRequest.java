package org.dromara.hodor.remoting.api.message.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.hodor.remoting.api.message.RequestBody;

/**
 *  job execute log request
 *
 * @author tomgs
 * @version 2021/3/3 1.0
 */
@Data
public class JobExecuteLogRequest implements RequestBody {

    private static final long serialVersionUID = -3561119764522144234L;

    private Long requestId;

    private String groupName;

    private String jobName;

    private Integer offset;

    private Integer length;

}
