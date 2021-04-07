package org.dromara.hodor.remoting.api.message;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  rpc header
 *
 * @author tomgs
 * @version 2020/9/13 1.0 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Header {

    /**
     * 消息id
     */
    private long id;

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 协议版本，目前是1
     */
    private byte version;

    /**
     * 消息体长度
     */
    private int length;

    /**
     * 扩展参数
     */
    private Map<String, Object> attachment;

}
