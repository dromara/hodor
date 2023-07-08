package org.dromara.hodor.remoting.api.http;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * hodor http request
 *
 * @author tomgs
 */
@Data
public class HodorHttpRequest implements Serializable {

    private static final long serialVersionUID = 4466331260180550160L;

    private String protocolVersion = "HTTP/1.1";

    private String uri;

    private String path;

    private String method;

    private byte[] content = new byte[0];

    private HodorHttpHeaders headers = new HodorHttpHeaders();

    private Map<String, List<String>> queryParameters;

    public void addHeader(String key, String value) {
        headers.addHeader(key, value);
    }

}
