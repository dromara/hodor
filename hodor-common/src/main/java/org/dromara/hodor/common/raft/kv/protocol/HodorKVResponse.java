package org.dromara.hodor.common.raft.kv.protocol;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * RatisKVResponse
 *
 * @author tomgs
 * @since 2022/3/24
 */
@Data
@Builder
public class HodorKVResponse implements Serializable {

    private static final long serialVersionUID = 2097808839313181860L;

    private CmdType cmdType;

    private Long requestId;

    private Long traceId;

    private Boolean success;

    private String message;

    // 详细状态，状态码
    // private Status status;

    private GetResponse getResponse;

    private PutResponse putResponse;

    private ContainsKeyResponse containsKeyResponse;

}
