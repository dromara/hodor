package org.dromara.hodor.actuator.common.core;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * retryable message
 *
 * @author tomgs
 * @since 2021/7/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetryableMessage {

   private Long id;

   private Long requestId;

   private String remoteIp;

   private byte[] rawMessage;

   private Date createTime;

   private Date updateTime;

   private Boolean status;

   private Integer retryCount;

   public static RetryableMessage createRetryableMessage(String remoteIp, RemotingMessage rawMessage) {
       long requestId = rawMessage.getHeader().getId();
       Date currentDate = new Date();
       return RetryableMessage.builder()
           .requestId(requestId)
           .remoteIp(remoteIp)
           .rawMessage(SerializeUtils.serialize(rawMessage))
           .createTime(currentDate)
           .updateTime(currentDate)
           .status(false)
           .retryCount(0)
           .build();
   }

}
