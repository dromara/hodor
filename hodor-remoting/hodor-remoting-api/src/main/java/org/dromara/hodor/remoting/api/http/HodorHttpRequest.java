package org.dromara.hodor.remoting.api.http;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class HodorHttpRequest implements Serializable {

    private static final long serialVersionUID = 4466331260180550160L;

    private String protocolVersion = "HTTP/1.1";

    private String uri;

    private String method;

    private byte[] content;

    private List<Map.Entry<String, String>> header;

    private Map<String, List<String>> queryParameters;

}
