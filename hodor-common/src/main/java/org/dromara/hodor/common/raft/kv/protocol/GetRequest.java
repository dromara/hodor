package org.dromara.hodor.common.raft.kv.protocol;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * GetRequest
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@Builder
public class GetRequest implements Serializable {

    private static final long serialVersionUID = -4010735230624230530L;

    private byte[] key;

}
