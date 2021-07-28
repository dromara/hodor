package org.dromara.hodor.client.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 *  failure request handler manager
 *
 * @author tomgs
 * @since 2021/3/22
 */
public class FailureRequestHandleManager extends AbstractEventPublisher<HodorChannel> {

    private static final FailureRequestHandleManager INSTANCE = new FailureRequestHandleManager();

    private final ExecutorManager executorManager;

    //TODO: to persistence
    private final Map<String, List<RemotingMessage>> resendMessageMap;

    private FailureRequestHandleManager() {
        this.executorManager = ExecutorManager.getInstance();
        this.resendMessageMap = new HashMap<>();
    }

    public static FailureRequestHandleManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void registerListener() {
        registerFailureHandlerListener();
    }

    private void registerFailureHandlerListener() {
        this.addListener(e -> {
            HodorChannel activeChannel = e.getValue();
            executorManager.commonExecute(new HodorRunnable() {
                @Override
                public void execute() {
                    String remoteIp = HostUtils.getIp(activeChannel.remoteAddress());
                    List<RemotingMessage> remotingMessages = resendMessageMap.get(remoteIp);
                    for (RemotingMessage remotingMessage : remotingMessages) {
                        activeChannel.send(remotingMessage).operationComplete(e -> {
                            if (e.cause() == null && e.isSuccess()) {
                                remotingMessages.remove(remotingMessage);
                            }
                        });
                    }
                }
            });
        }, JobExecuteStatus.FAILED); // RESEND_EVENT
    }

    public void fireFailureRequestHandler(String remoteIp, HodorChannel activeChannel, RemotingMessage message) {
        List<RemotingMessage> remotingMessages = resendMessageMap.computeIfAbsent(remoteIp, k -> new ArrayList<>());
        remotingMessages.add(message);

        if (activeChannel == null || !activeChannel.isOpen()) {
            return;
        }

        publish(new Event<>(activeChannel, JobExecuteStatus.FAILED)); // RESEND_EVENT
    }

}
