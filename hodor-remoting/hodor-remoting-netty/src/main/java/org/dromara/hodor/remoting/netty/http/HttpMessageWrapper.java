package org.dromara.hodor.remoting.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
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
import org.dromara.hodor.remoting.api.http.HodorHttpHeaders;
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
        hodorHttpRequest.setQueryParameters(queryParameters);
        hodorHttpRequest.setProtocolVersion(httpVersion.toString());

        headers.entries().forEach(entry -> hodorHttpRequest.addHeader(entry.getKey(), entry.getValue()));
        hodorHttpRequest.setContent(asBytes(content));
        return hodorHttpRequest;
    }

    public static FullHttpRequest requestRawWrapper(HodorHttpRequest hodorRequest) {
        byte[] content = Optional.ofNullable(hodorRequest.getContent()).orElse(EMPTY_BYTE);
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.valueOf(hodorRequest.getProtocolVersion()),
            HttpMethod.valueOf(hodorRequest.getMethod()), hodorRequest.getUri(), Unpooled.copiedBuffer(content));
        Optional.ofNullable(hodorRequest.getHeaders()).ifPresent(headers -> {
            for (Map.Entry<String, String> entry : headers.entries()) {
                httpRequest.headers().add(entry.getKey(), entry.getValue());
            }
        });
        return httpRequest;
    }

    public static HodorHttpResponse responseWrapper(FullHttpResponse response) {
        int statusCode = response.status().code();
        String protocolVersion = response.protocolVersion().toString();
        List<Map.Entry<String, String>> headers = response.headers().entries();

        HodorHttpResponse hodorHttpResponse = new HodorHttpResponse();
        hodorHttpResponse.setStatusCode(statusCode);
        hodorHttpResponse.setProtocolVersion(protocolVersion);
        headers.forEach(hodorHttpResponse::addHeader);
        hodorHttpResponse.setBody(asBytes(response.content()));
        return hodorHttpResponse;
    }

    public static FullHttpResponse responseRawWrapper(HodorHttpResponse hodorHttpResponse) {
        HodorHttpHeaders responseHeaders = hodorHttpResponse.getHeaders();
        HttpVersion httpVersion = HttpVersion.valueOf(hodorHttpResponse.getProtocolVersion());
        HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(hodorHttpResponse.getStatusCode());

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(httpVersion, httpResponseStatus, Unpooled.copiedBuffer(hodorHttpResponse.getBody()));
        Optional.ofNullable(responseHeaders).ifPresent(headers -> headers.entries().forEach(entry -> httpResponse.headers().set(entry.getKey(), entry.getValue())));

        httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        return httpResponse;
    }

    private static byte[] asBytes(ByteBuf content) {
        if (content.readableBytes() <= 0) {
            return EMPTY_BYTE;
        } else {
            byte[] contentBytes = new byte[content.readableBytes()];
            content.getBytes(0, contentBytes);
            return contentBytes;
        }
    }

}
