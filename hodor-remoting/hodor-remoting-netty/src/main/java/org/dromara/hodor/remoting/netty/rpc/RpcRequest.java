package org.dromara.hodor.remoting.netty.rpc;

import lombok.*;

/**
 * rpc request
 *
 * @author tomgs
 * @since 2020/9/10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {

    private Header header;

    private RequestBody body;

}
