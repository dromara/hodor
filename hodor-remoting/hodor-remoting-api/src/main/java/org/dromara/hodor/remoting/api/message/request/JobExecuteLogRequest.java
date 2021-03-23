package org.dromara.hodor.remoting.api.message.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  job execute log request
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JobExecuteLogRequest extends AbstractRequestBody {

    private static final long serialVersionUID = -3561119764522144234L;

    private String groupName;

    private String jobName;

    private Integer offset;

    private Integer length;

}
