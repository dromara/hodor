package org.dromara.hodor.client.executor;

import cn.hutool.core.lang.Tuple;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 *  failure request handler manager
 *
 * @author tomgs
 * @since 2021/3/22
 */
public class FailureRequestHandleManager extends AbstractAsyncEventPublisher<Tuple> {

    private static final FailureRequestHandleManager INSTANCE = new FailureRequestHandleManager();

    private static final String REQUEST_RESEND_EVENT = "REQUEST_RESEND_EVENT";

    private long lastFireTime = System.currentTimeMillis();

    private final ExecutorManager executorManager;

    private final ScheduledExecutorService failureRequestCheckService;

    //TODO: to persistence
    private final Map<String, List<RemotingMessage>> resendMessageMap;

    private FailureRequestHandleManager() {
        this.executorManager = ExecutorManager.getInstance();
        this.resendMessageMap = new HashMap<>();
        this.failureRequestCheckService = new ScheduledThreadPoolExecutor(1,
            HodorThreadFactory.create("hodor-failure-request-checker", true),
            new ThreadPoolExecutor.DiscardOldestPolicy());
        this.failureRequestCheckService.scheduleAtFixedRate(() -> {

        }, 3_000, 30_000, TimeUnit.MILLISECONDS);
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
            Tuple activeChannelTuple = e.getValue();
            executorManager.commonExecute(new HodorRunnable() {
                @Override
                public void execute() {
                    String remoteIp = activeChannelTuple.get(0);
                    HodorChannel activeChannel = activeChannelTuple.get(1);
                    List<RemotingMessage> remotingMessages = resendMessageMap.get(remoteIp);
                    Optional.ofNullable(remotingMessages).ifPresent(msgList -> msgList.forEach(remotingMessage -> {
                        activeChannel.send(remotingMessage).operationComplete(e -> {
                            if (e.cause() == null && e.isSuccess()) {
                                remotingMessages.remove(remotingMessage);
                            }
                        });
                    }));

                }
            });
        }, REQUEST_RESEND_EVENT); // RESEND_EVENT
    }

    public void fireFailureRequestHandler(String remoteIp, HodorChannel activeChannel) {
        // 30s 触发一次
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastFireTime < 30 * 1000) {
            return;
        }
        lastFireTime = currentTimeMillis;
        publish(new Event<>(new Tuple(remoteIp, activeChannel), REQUEST_RESEND_EVENT)); // RESEND_EVENT
    }

    public void addFailureRequest(String remoteIp, HodorChannel activeChannel, RemotingMessage message) {
        List<RemotingMessage> remotingMessages = resendMessageMap.computeIfAbsent(remoteIp, k -> new ArrayList<>());
        remotingMessages.add(message);

        if (activeChannel == null || !activeChannel.isOpen()) {
            return;
        }
        fireFailureRequestHandler(remoteIp, activeChannel);
    }

}
