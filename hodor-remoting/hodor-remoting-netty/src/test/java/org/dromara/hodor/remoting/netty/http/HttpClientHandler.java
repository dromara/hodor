package org.dromara.hodor.remoting.netty.http;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;

/**
 * http client
 *
 * @author tomgs
 * @date 2021/7/26 16:32
 * @since 1.0
 */
@Slf4j
public class HttpClientHandler implements HodorChannelHandler {

    private final CompletableFuture<HodorHttpResponse> responseFuture;

    private final HodorHttpRequest request;

    public HttpClientHandler(final HodorHttpRequest request, final CompletableFuture<HodorHttpResponse> responseFuture) {
        this.request = request;
        this.responseFuture = responseFuture;
    }

    @Override
    public void connected(HodorChannel channel) {
        channel.send(request);
    }

    @Override
    public void received(HodorChannel channel, Object message) {
        HodorHttpResponse httpResponse = (HodorHttpResponse) message;
        responseFuture.complete(httpResponse);
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        responseFuture.completeExceptionally(cause);
    }

}
