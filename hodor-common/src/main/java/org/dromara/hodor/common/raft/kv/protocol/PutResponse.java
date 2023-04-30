package org.dromara.hodor.common.raft.kv.protocol;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * PutResponse
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@Builder
public class PutResponse implements Serializable {

    private static final long serialVersionUID = 2034924846457819834L;
}
