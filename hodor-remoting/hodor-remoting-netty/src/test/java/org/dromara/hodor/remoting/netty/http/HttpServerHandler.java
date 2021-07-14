package org.dromara.hodor.remoting.netty.http;

import java.nio.charset.StandardCharsets;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;

public class HttpServerHandler implements HodorChannelHandler {

    @Override
    public void received(HodorChannel channel, Object message) throws Exception {
        HodorHttpRequest httpRequest = (HodorHttpRequest) message;
        System.out.println(httpRequest);
        byte[] content = httpRequest.getContent();
        String uri = httpRequest.getUri();
        String method = httpRequest.getMethod();

        // do handle
        System.out.println("do something...");

        HodorHttpResponse hodorHttpResponse = new HodorHttpResponse();
        hodorHttpResponse.setUri(uri);
        hodorHttpResponse.setStatusCode(200);
        hodorHttpResponse.setBody("success".getBytes(StandardCharsets.UTF_8));
        hodorHttpResponse.setContentType("Content-Type: application/json; charset=UTF-8");
        channel.send(hodorHttpResponse);
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        cause.printStackTrace();
    }

}
