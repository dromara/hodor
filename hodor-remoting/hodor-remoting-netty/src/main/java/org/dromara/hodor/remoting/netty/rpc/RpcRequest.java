package org.dromara.hodor.remoting.netty.rpc;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rpc request
 *
 * @author tomgs
 * @since 2020/9/10
 */
@Data
@Builder
@NoArgsConstructor
public class RpcRequest {

    private Header header;

    private RequestBody body;

}
