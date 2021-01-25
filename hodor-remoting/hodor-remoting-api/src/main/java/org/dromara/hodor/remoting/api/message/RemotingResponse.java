package org.dromara.hodor.remoting.api.message;

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
public class RemotingResponse<T extends ResponseBody> {

    private Header header;

    private T body;

}
