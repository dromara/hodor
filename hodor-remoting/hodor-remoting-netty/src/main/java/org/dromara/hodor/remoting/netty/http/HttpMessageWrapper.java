package org.dromara.hodor.remoting.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.util.List;
import java.util.Map;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;

/**
 * hodor channel handler wrapper
 * @author tomgs
 */
public final class HttpMessageWrapper {

    public static HodorHttpRequest requestWrapper(Object msg) throws Exception {
        HodorHttpRequest hodorHttpRequest = new HodorHttpRequest();
        FullHttpRequest httpRequest = (FullHttpRequest)msg;

        HttpVersion httpVersion = httpRequest.protocolVersion();
        HttpMethod method = httpRequest.method();
        HttpHeaders headers = httpRequest.headers();
        ByteBuf content = httpRequest.content();
        String uri = httpRequest.uri();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> queryParameters = queryStringDecoder.parameters();

        hodorHttpRequest.setUri(uri);
        hodorHttpRequest.setMethod(method.name());
        hodorHttpRequest.setHeader(headers.entries());
        hodorHttpRequest.setQueryParameters(queryParameters);
        hodorHttpRequest.setProtocolVersion(httpVersion.protocolName());

        if (content.readableBytes() < 0) {
            hodorHttpRequest.setContent(null);
        } else {
            byte[] contentBytes = new byte[content.readableBytes()];
            content.getBytes(0, contentBytes);
            hodorHttpRequest.setContent(contentBytes);
        }

        return hodorHttpRequest;
    }

    public static HodorHttpResponse responseWrapper(Object msg) {
        HodorHttpResponse httpResponse = new HodorHttpResponse();
        return httpResponse;
    }

}
