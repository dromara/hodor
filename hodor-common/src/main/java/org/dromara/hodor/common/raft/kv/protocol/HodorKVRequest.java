package org.dromara.hodor.common.raft.kv.protocol;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * 1、这种通过组合的方式把请求头和请求体进行组装，在新增请求类型时需要改动改类（这种适合在通过proto文件的方式）
 * 2、通过继承的方式，BaseRequest中只放请求头，然后请求体通过实现新的类型然后继承BaseRequest来实现（这种在直接代码的组织方式比较方便）
 *
 * @author tomgs
 * @version 2022/3/23 1.0
 */
@Data
@Builder
public class HodorKVRequest implements Serializable {

    private static final long serialVersionUID = 227553172360185673L;

    private CmdType cmdType;

    private Long requestId;

    private Long traceId;

    private GetRequest getRequest;

    private PutRequest putRequest;

    private DeleteRequest deleteRequest;

}
