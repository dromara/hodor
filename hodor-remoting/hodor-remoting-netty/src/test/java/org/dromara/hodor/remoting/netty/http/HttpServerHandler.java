package org.dromara.hodor.remoting.netty.http;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;

@Slf4j
public class HttpServerHandler implements HodorChannelHandler {

    @Override
    public void received(HodorChannel channel, Object message) throws Exception {
        HodorHttpRequest httpRequest = (HodorHttpRequest) message;
        System.out.println(httpRequest);

        byte[] content = httpRequest.getContent();
        String uri = httpRequest.getUri();
        String method = httpRequest.getMethod();

        log.info("uri: {}, method: {}, content: {}.", uri, method, new String(content, StandardCharsets.UTF_8));

        // do handle
        System.out.println("do something...");

        HodorHttpResponse hodorHttpResponse = new HodorHttpResponse();
        hodorHttpResponse.setStatusCode(200);
        hodorHttpResponse.setBody("success".getBytes(StandardCharsets.UTF_8));
        hodorHttpResponse.addHeader("Content-Type", "application/json; charset=UTF-8");
        channel.send(hodorHttpResponse).operationComplete(future -> {
            if (future.isSuccess()) {
                log.info("response success.");
            } else {
                log.error("response error, {}", future.cause().getMessage(), future.cause());
            }
        });
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        cause.printStackTrace();
    }

}
