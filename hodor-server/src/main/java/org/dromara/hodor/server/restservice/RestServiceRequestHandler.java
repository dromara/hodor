package org.dromara.hodor.server.restservice;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.StrSplitter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelHandler;
import org.dromara.hodor.remoting.api.http.HodorHttpRequest;
import org.dromara.hodor.remoting.api.http.HodorHttpResponse;
import org.dromara.hodor.server.config.HodorServerProperties;

/**
 * rest service handler
 *
 * @author tomgs
 * @since 2021/1/27
 */
@Slf4j
public class RestServiceRequestHandler implements HodorChannelHandler {

    public static final Map<String, HandlerMethod> REST_SERVICES = new HashMap<>();

    private final HodorServerProperties properties;

    private final GsonUtils serializer;

    public RestServiceRequestHandler(final HodorServerProperties properties) {
        Assert.notNull(properties, "properties must be not null.");
        this.properties = properties;
        this.serializer = GsonUtils.getInstance();
    }

    @Override
    public void received(HodorChannel channel, Object message) {
        try {
            HodorHttpRequest httpRequest = (HodorHttpRequest) message;
            // /hodor/{serviceName}/{methodName}
            String uri = httpRequest.getUri();
            List<String> requestPath = StrSplitter.splitPath(uri, 2);
            if (requestPath.size() != 2) {
                responseError(404, "not found path -> " + uri, channel);
                return;
            }

            String appName = requestPath.get(0);
            String serviceFullName = requestPath.get(1);
            if (!properties.getClusterServerName().equals(appName)) {
                responseError(400, "Request appName invalid -> " + appName, channel);
                return;
            }

            // do handle
            HandlerMethod handlerMethod = REST_SERVICES.get(serviceFullName);
            if (handlerMethod == null) {
                responseError(404, "Not found service -> " + serviceFullName, channel);
                return;
            }

            HandlerMethod.MethodParameter[] parameters = handlerMethod.getParameter();
            Object[] args;
            if (parameters == null || parameters.length == 0) {
                args = new Object[0];
            } else {
                args = new Object[parameters.length];
                Map<String, List<String>> queryParameters = httpRequest.getQueryParameters();
                if (CollectionUtil.isNotEmpty(queryParameters)) {
                    for (int i = 0; i < parameters.length; i++) {
                        String parameterName = parameters[i].getParameterName();
                        List<String> values = queryParameters.get(parameterName);
                        args[i] =values.size() == 1 ? values.get(0) : values;
                    }
                }
                byte[] content = httpRequest.getContent();
                if (content != null && content.length > 0) {
                    HandlerMethod.MethodParameter parameter = parameters[parameters.length - 1];
                    args[parameters.length - 1] = serializer.fromJson(new String(content, StandardCharsets.UTF_8),
                        parameter.getParameterizedType());
                }
            }
            Object result = handlerMethod.invoke(args);
            byte[] responseByteArray = serializer.toJson(result).getBytes(StandardCharsets.UTF_8);
            responseSuccess(responseByteArray, channel);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseError(500, "Hodor rest server error -> " + e.getMessage(), channel);
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
