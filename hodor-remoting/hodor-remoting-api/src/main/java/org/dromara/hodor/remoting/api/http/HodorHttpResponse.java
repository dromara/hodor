package org.dromara.hodor.remoting.api.http;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// @see com.sun.deploy.net.HttpResponse
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HodorHttpResponse {

    private String protocolVersion = "HTTP/1.1";

    private int statusCode = 200;

    private Map<String, String> responseHeader = new HashMap<>();

    private byte[] body;

    public void addHeader(String headerName, String headerValue) {
        responseHeader.put(headerName, headerValue);
    }

}
