package org.dromara.hodor.actuator.api.executor;

import cn.hutool.core.collection.CollectionUtil;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.core.RetryableMessage;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.event.AbstractAsyncEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.storage.db.DBOperator;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * failure request handler manager
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class FailureRequestHandleManager extends AbstractAsyncEventPublisher<RetryableMessage> {

    private static final String MESSAGE_INSERT_EVENT = "MESSAGE_INSERT_EVENT";

    private static final String MESSAGE_UPDATE_EVENT = "MESSAGE_UPDATE_EVENT";

    private static final String MESSAGE_DELETE_EVENT = "MESSAGE_DELETE_EVENT";

    private final ClientChannelManager clientChannelManager;

    private final ExecutorManager executorManager;

    private final ScheduledThreadPoolExecutor failureRequestCheckService;

    private final DBOperator dbOperator;

    public FailureRequestHandleManager(final ClientChannelManager clientChannelManager,
                                       final ExecutorManager executorManager,
                                       final DBOperator dbOperator) {
        this.clientChannelManager = clientChannelManager;
        this.executorManager = executorManager;
        this.dbOperator = dbOperator;
        this.failureRequestCheckService = new ScheduledThreadPoolExecutor(1,
            HodorThreadFactory.create("hodor-failure-request-checker", true),
            new ThreadPoolExecutor.DiscardOldestPolicy());
        startCheck();
    }

    private void startCheck() {
        this.failureRequestCheckService.scheduleWithFixedDelay(this::fireFailureRequestHandler,
            10_000, 10_000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void registryListener() {
        registerAddFailureMessageListener();
        registerUpdateFailureMessageListener();
        registerDeleteFailureMessageListener();
    }

    private void registerAddFailureMessageListener() {
        this.addListener(e -> {
            RetryableMessage retryableMessage = e.getValue();
            try {
                dbOperator.update(insertSql, retryableMessage.getRequestId(), retryableMessage.getRemoteIp(), retryableMessage.getRawMessage(),
                    retryableMessage.getCreateTime(), retryableMessage.getStatus());
            } catch (SQLException ex) {
                log.error("insert retry message exception, {}", ex.getMessage(), ex);
            }
        }, MESSAGE_INSERT_EVENT);
    }

    private void registerUpdateFailureMessageListener() {
        this.addListener(e -> {
            RetryableMessage retryableMessage = e.getValue();
            try {
                dbOperator.update(updateSql, retryableMessage.getStatus(), new Date(), retryableMessage.getId());
            } catch (SQLException ex) {
                log.error("update retry message exception, {}", ex.getMessage(), ex);
            }
        }, MESSAGE_UPDATE_EVENT);
    }

    private void registerDeleteFailureMessageListener() {
        this.addListener(e -> {
            RetryableMessage retryableMessage = e.getValue();
            try {
                dbOperator.update(deleteSql, retryableMessage.getId());
            } catch (SQLException ex) {
                log.error("insert retry message exception, {}", ex.getMessage(), ex);
            }
        }, MESSAGE_DELETE_EVENT);
    }

    public void fireFailureRequestHandler() {
        executorManager.commonExecute(new HodorRunnable() {
            @Override
            public void execute() {
                List<RetryableMessage> retryableMessages = null;
                try {
                    retryableMessages = dbOperator.queryList(querySql, RetryableMessage.class);
                } catch (SQLException ex) {
                    log.error("select retry message exception, {}", ex.getMessage(), ex);
                }
                if (CollectionUtil.isEmpty(retryableMessages)) {
                    return;
                }
                retryableMessages.forEach(retryableMessage -> {
                    String remoteIp = retryableMessage.getRemoteIp();
                    RemotingMessage remotingMessage = SerializeUtils.deserialize(retryableMessage.getRawMessage(), RemotingMessage.class);
                    HodorChannel activeChannel = clientChannelManager.getActiveOrBackupChannel(remoteIp);
                    activeChannel.send(remotingMessage)
                        .operationComplete(e -> {
                            if (e.cause() == null && e.isSuccess()) {
                                publish(Event.create(retryableMessage, MESSAGE_DELETE_EVENT));
                            } else {
                                retryableMessage.setStatus(false);
                                retryableMessage.setUpdateTime(new Date());
                                publish(Event.create(retryableMessage, MESSAGE_UPDATE_EVENT));
                            }
                        });
                });

            }
        });
    }

    public void addFailureRequest(String remoteIp, RemotingMessage message) {
        RetryableMessage retryableMessage = RetryableMessage.createRetryableMessage(remoteIp, message);
        publish(Event.create(retryableMessage, MESSAGE_INSERT_EVENT)); // RESEND_EVENT
    }

    private final String insertSql = "INSERT INTO hodor_retryable_message (request_id, remote_ip, raw_message, create_time, status) VALUES (?, ?, ?, ?, ?)";
    private final String updateSql = "UPDATE hodor_retryable_message SET status = ?, update_time = ?, retry_count = retry_count + 1 WHERE id = ?";
    private final String querySql = "SELECT * FROM hodor_retryable_message";
    private final String deleteSql = "DELETE FROM hodor_retryable_message WHERE id = ?";
}
