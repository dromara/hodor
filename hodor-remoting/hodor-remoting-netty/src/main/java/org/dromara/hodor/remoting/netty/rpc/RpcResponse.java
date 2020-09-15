package org.dromara.hodor.remoting.netty.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rpc response
 *
 * @author tomgs
 * @since 2020/9/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T extends ResponseBody> {

    private Header header;

    private T body;

}
