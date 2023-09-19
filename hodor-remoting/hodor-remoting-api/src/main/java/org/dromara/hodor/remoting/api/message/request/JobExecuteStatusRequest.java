package org.dromara.hodor.remoting.api.message.request;

import lombok.Data;
import org.dromara.hodor.remoting.api.message.RequestBody;

/**
 *  job execute status request
 *
 * @author tomgs
 * @version 2021/3/3 1.0
 */
@Data
public class JobExecuteStatusRequest implements RequestBody {

    private static final long serialVersionUID = -3268050422697931614L;

    private Long requestId;
}
