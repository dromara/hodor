package org.dromara.hodor.remoting.api.http;

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

    private String uri;

    private int statusCode;

    private String contentType;

    private Map<String, String> responseHeader;

    private byte[] body;

}
