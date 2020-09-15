package org.dromara.hodor.remoting.netty.rpc;

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
public class Header {

    /**
     * 消息校验码
     */
    private int crcCode;

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 协议版本，目前是1
     */
    private int version;

    /**
     * 消息体长度
     */
    private int length;

}
