package org.dromara.hodor.remoting.api;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * default response future
 *
 * @author tomgs
 * @since 2021/4/7
 */
@Slf4j
public class DefaultResponseFuture implements ResponseFuture {

    private static final long DEFAULT_TIMEOUT = 10_000; // 10s

    private static final Map<Long, ResponseFuture> RESPONSE_FUTURE_MAP = new ConcurrentHashMap<>();

    private final CountDownLatch latch = new CountDownLatch(1);

    private final long messageId;

    private final RemotingMessage message;

    private RemotingMessage responseMessage;

    private boolean isDone;

    private Throwable cause;

    private boolean success;

    public DefaultResponseFuture(final RemotingMessage message) {
        this.messageId = message.getHeader().getId();
        this.message = message;
        RESPONSE_FUTURE_MAP.put(messageId, this);
    }

    @Override
    public RemotingMessage get() throws RemoteException {
        return get(DEFAULT_TIMEOUT);
    }

    @Override
    public RemotingMessage get(long timeout) throws RemoteException {
        if (!isDone()) {
            try {
                this.latch.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error("get response timeout, request message: {}", message);
                throw new RemoteException("request " + messageId + " timeout.");
            }
        }
        if (!isSuccess()) {
            log.error("get response exception, request message: {}", message, cause());
            throw new RemoteException("request " + messageId + " exception.", cause());
        }
        return this.responseMessage;
    }

    @Override
    public boolean isDone() {
        return this.isDone;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public Throwable cause() {
        return this.cause;
    }

    @Override
    public void receive(RemotingMessage responseMessage) {
        this.responseMessage = responseMessage;
        this.isDone = true;
        this.latch.countDown();
        RESPONSE_FUTURE_MAP.remove(responseMessage.getHeader().getId());
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static ResponseFuture getResponseFuture(long messageId) {
        return RESPONSE_FUTURE_MAP.get(messageId);
    }

}
