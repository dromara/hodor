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
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 6189255067417307493L;

    private byte[] key;

}
