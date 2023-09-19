package org.dromara.hodor.remoting.api.message.request;

import lombok.Data;
import org.dromara.hodor.remoting.api.message.RequestBody;

/**
 *  kill running job request
 *
 * @author tomgs
 * @version 2021/3/3 1.0
 */
@Data
public class KillRunningJobRequest implements RequestBody {

    private static final long serialVersionUID = -1814909263364679439L;

    private Long requestId;
}
