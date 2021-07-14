package org.dromara.hodor.remoting.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;
import org.dromara.hodor.remoting.netty.NettyChannelHandler;
import org.junit.Before;
import org.junit.Test;

public class HttpServerHandlerTest {

    private static String CRLF = "\r\n";

    private EmbeddedChannel embeddedChannel;

    @Before
    public void before() {
        // The same Handlers in order as the ChannelPipeline in HttpServerInitializer
        HttpServerCodec httpServerCodec = new HttpServerCodec();
        WebSocketServerCompressionHandler webSocketServerCompressionHandler = new WebSocketServerCompressionHandler();
        HttpObjectAggregator httpObjectAggregator = new HttpObjectAggregator(1024 * 1024 * 64);
        ChunkedWriteHandler chunkedWriteHandler = new ChunkedWriteHandler();

        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HTTP_PROTOCOL, true);
        HttpServerHandler httpServerHandler = new HttpServerHandler();
        NettyChannelHandler handler = new NettyChannelHandler(attribute, httpServerHandler);

        embeddedChannel = new EmbeddedChannel(httpServerCodec, webSocketServerCompressionHandler, httpObjectAggregator, chunkedWriteHandler, handler);
    }

    @Test
    public void testGetHello() {
        String requestStr = "GET / HTTP/1.1" + CRLF
            + "Host: localhost:8719" + CRLF
            + CRLF;
        String response = processResponse(requestStr);
        System.out.println(response);
    }

    @Test
    public void testPostHello() {
        String requestStr = "POST / HTTP/1.1" + CRLF
            + "Host: localhost:8719" + CRLF
            + "Content-Length: 5" + CRLF
            + CRLF
            + "hello";
        String response = processResponse(requestStr);
        System.out.println(response);
    }

    private String processResponse(String httpRequestStr) {
        embeddedChannel.writeInbound(Unpooled.wrappedBuffer(httpRequestStr.getBytes(StandardCharsets.UTF_8)));
        HodorHttpResponse response = embeddedChannel.readOutbound();
        return response.toString();
    }

    @Test
    public void testQueryParameter() {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder("/a?p1=a&p2=b&p3=c;p2=6#;p4=1&p6=7");
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        System.out.println(parameters);
    }

}
