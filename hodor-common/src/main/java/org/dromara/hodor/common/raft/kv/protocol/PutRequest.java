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
public class PutRequest implements Serializable {

    private static final long serialVersionUID = -7668460719756952944L;

    private byte[] key;

    private byte[] value;

}
