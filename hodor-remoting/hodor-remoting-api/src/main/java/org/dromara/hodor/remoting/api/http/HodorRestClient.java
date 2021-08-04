package org.dromara.hodor.remoting.api.http;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;

/**
 * hodor rest client
 *
 * @author tomgs
 * @since 2021/8/4
 */
public class HodorRestClient {

    private static final HodorRestClient INSTANCE = new HodorRestClient();



    @Slf4j
    private static class HodorRestClientHandler implements HodorChannelHandler {

        private final CompletableFuture<HodorHttpResponse> responseFuture;

        private final HodorHttpRequest request;

        public HodorRestClientHandler(final HodorHttpRequest request, final CompletableFuture<HodorHttpResponse> responseFuture) {
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


}
