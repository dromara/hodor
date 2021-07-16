package org.dromara.hodor.client.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.HodorApiClient;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.model.node.NodeInfo;

/**
 * 心跳/下线消息发送服务
 *
 * @author tomgs
 * @since 2021/1/7
 */
@Slf4j
public class MsgSender {

    private final HodorApiClient hodorApiClient = ServiceProvider.getInstance().getBean(HodorApiClient.class);

    public HeartbeatSender getHeartbeatSender() {
        return new HeartbeatSender();
    }

    public NodeOfflineSender getNodeOfflineSender() {
        return new NodeOfflineSender();
    }

    public class HeartbeatSender implements Runnable {
        @Override
        public void run() {
            try {
                NodeInfo msg = new NodeInfo();
                hodorApiClient.sendHeartbeat(msg);
            } catch (Exception e) {
                log.warn("HeartbeatSender send message has exception, msg: {}", e.getMessage(), e);
            }
        }
    }

    public class NodeOfflineSender implements Runnable {
        @Override
        public void run() {
            try {
                NodeInfo msg = new NodeInfo();
                hodorApiClient.sendOfflineMsg(msg);
            } catch (Exception e) {
                log.warn("HeartbeatSender send message has exception, msg: {}", e.getMessage(), e);
            }
        }
    }

}
