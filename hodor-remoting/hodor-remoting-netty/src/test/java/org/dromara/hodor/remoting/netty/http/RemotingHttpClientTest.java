package org.dromara.hodor.remoting.netty.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.model.node.NodeInfo;
import org.dromara.hodor.remoting.api.Attribute;
import org.dromara.hodor.remoting.api.NetClient;
import org.dromara.hodor.remoting.api.NetClientTransport;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;
import org.junit.Test;

/**
 * remoting http client test
 *
 * @author tomgs
 * @date 2021/7/26 16:26
 * @since 1.0
 */
public class RemotingHttpClientTest {

    @Test
    public void testHttpClient() throws IOException, ExecutionException, InterruptedException {
        Attribute attribute = new Attribute();
        attribute.put(RemotingConst.HOST_KEY, "127.0.0.1");
        attribute.put(RemotingConst.PORT_KEY, 8081);
        attribute.put(RemotingConst.HTTP_PROTOCOL, true);
        // handle request
        NodeInfo nodeInfo = new NodeInfo();
        HodorHttpRequest request = new HodorHttpRequest();
        request.setUri("/hodor/scheduler/isAlive");
        request.setMethod("GET");
        request.setContent(GsonUtils.getInstance().toJson(nodeInfo).getBytes(StandardCharsets.UTF_8));
        request.addHeader("Content-Type", "application/json;charset=utf-8");

        CompletableFuture<HodorHttpResponse> responseFuture = new CompletableFuture<>();
        HttpClientHandler handler = new HttpClientHandler(request, responseFuture);

        // execute
        NetClientTransport clientTransport = ExtensionLoader.getExtensionLoader(NetClientTransport.class).getDefaultJoin();
        NetClient client = clientTransport.build(attribute, handler);
        client.connect();

        // wait response
        HodorHttpResponse hodorHttpResponse = responseFuture.get();
        System.out.println(hodorHttpResponse);
        if (hodorHttpResponse.getStatusCode() == 200) {
            String string = new String(hodorHttpResponse.getBody(), StandardCharsets.UTF_8);
            System.out.println(string);
        }
    }

}
