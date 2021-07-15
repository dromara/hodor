package org.dromara.hodor.server.restservice;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;

/**
 * rest service handler
 *
 * @author tomgs
 * @since 2021/1/27
 */
@Slf4j
public class RestServiceRequestHandler implements HodorChannelHandler {

    public static final Map<String, HandlerMethod> REST_SERVICES = new HashMap<>();

    private final RemotingMessageSerializer serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();

    @Override
    public void received(HodorChannel channel, Object message) {
        try {
            HodorHttpRequest httpRequest = (HodorHttpRequest) message;
            // hodor/{serviceName}/{methodName}
            String uri = httpRequest.getUri();
            // {hodor, {serviceName}/{methodName}}
            String[] paths = uri.split("/", 2);
            String appName = paths[0];
            String serviceFullName = paths[1];
            if (!"hodor".equals(appName)) {
                responseError(400, "Request appName invalid -> " + appName, channel);
                return;
            }
            // do handle
            HandlerMethod handlerMethod = REST_SERVICES.get(serviceFullName);
            if (handlerMethod == null) {
                responseError(404, "Not found service -> " + serviceFullName, channel);
                return;
            }

            byte[] content = httpRequest.getContent();
            Map<String, List<String>> queryParameters = httpRequest.getQueryParameters();
            serializer.deserialize(content, null);
            Object result = handlerMethod.invoke();

            responseSuccess(content, channel);
        } catch (Exception e) {
            responseError(500, "Hodor rest server error", channel);
        }
    }

    private void responseSuccess(byte[] body, HodorChannel channel) {
        response(200, body, "application/json; charset=UTF-8", channel);
    }

    private void responseError(int statusCode, String msg, HodorChannel channel) {
        response(statusCode, msg.getBytes(StandardCharsets.UTF_8), "text/plain; charset=UTF-8", channel);
    }

    private void response(int statusCode, byte[] body, String contentType, HodorChannel channel) {
        HodorHttpResponse hodorHttpResponse = new HodorHttpResponse();
        hodorHttpResponse.setStatusCode(statusCode);
        hodorHttpResponse.setBody(body);
        hodorHttpResponse.addHeader("Content-Type", contentType);
        channel.send(hodorHttpResponse).operationComplete(future -> {
            if (!future.isSuccess()) {
                log.error("response error, {}", future.cause().getMessage(), future.cause());
            }
        });
    }

    @Override
    public void exceptionCaught(HodorChannel channel, Throwable cause) {
        log.error(cause.getMessage(), cause);
    }

}
