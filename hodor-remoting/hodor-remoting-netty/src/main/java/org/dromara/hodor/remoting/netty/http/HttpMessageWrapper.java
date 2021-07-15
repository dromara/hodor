package org.dromara.hodor.remoting.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;

/**
 * hodor channel handler wrapper<br/>
 * <br/>
 * only support GET/POST request<br/>
 * and POST request only support application/json
 *
 * @author tomgs
 */
public final class HttpMessageWrapper {

    private static final byte[] EMPTY_BYTE = new byte[0];

    public static HodorHttpRequest requestWrapper(FullHttpRequest httpRequest) {
        HttpVersion httpVersion = httpRequest.protocolVersion();
        HttpMethod method = httpRequest.method();
        HttpHeaders headers = httpRequest.headers();
        ByteBuf content = httpRequest.content();
        String uri = httpRequest.uri();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> queryParameters = queryStringDecoder.parameters();

        HodorHttpRequest hodorHttpRequest = new HodorHttpRequest();
        hodorHttpRequest.setUri(uri);
        hodorHttpRequest.setMethod(method.name());
        hodorHttpRequest.setHeader(headers.entries());
        hodorHttpRequest.setQueryParameters(queryParameters);
        hodorHttpRequest.setProtocolVersion(httpVersion.toString());

        if (content.readableBytes() <= 0) {
            hodorHttpRequest.setContent(EMPTY_BYTE);
        } else {
            byte[] contentBytes = new byte[content.readableBytes()];
            content.getBytes(0, contentBytes);
            hodorHttpRequest.setContent(contentBytes);
        }

        return hodorHttpRequest;
    }

    public static FullHttpResponse responseWrapper(HodorHttpResponse hodorHttpResponse) {
        Map<String, String> responseHeader = hodorHttpResponse.getResponseHeader();
        HttpVersion httpVersion = HttpVersion.valueOf(hodorHttpResponse.getProtocolVersion());
        HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(hodorHttpResponse.getStatusCode());

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(httpVersion, httpResponseStatus, Unpooled.copiedBuffer(hodorHttpResponse.getBody()));
        Optional.of(responseHeader).ifPresent(headers -> headers.forEach((k ,v) -> httpResponse.headers().set(k, v)));

        httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        return httpResponse;
    }

}
