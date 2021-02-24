package org.dromara.hodor.remoting.api.message;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class RemotingRequest {

    private Header header;

    private Object body;

}
