package org.dromara.hodor.common.raft.kv.protocol;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * GetResponse
 *
 * @author tomgs
 * @since 2022/3/24
 */
@Data
@Builder
public class GetResponse implements Serializable {

    private static final long serialVersionUID = -5587523163116261227L;

    private byte[] value;

}
