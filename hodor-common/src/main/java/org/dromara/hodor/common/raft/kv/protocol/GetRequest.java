package org.dromara.hodor.common.raft.kv.protocol;

import java.io.Serializable;
import lombok.Data;

/**
 * GetRequest
 *
 * @author tomgs
 * @since 2022/3/24
 */
@Data
public class GetRequest implements Serializable {

    private static final long serialVersionUID = -4010735230624230530L;

    private byte[] key;

}
