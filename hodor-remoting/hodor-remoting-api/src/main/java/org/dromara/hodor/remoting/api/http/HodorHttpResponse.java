package org.dromara.hodor.remoting.api.http;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * hodor http response
 *
 * @author tomgs
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HodorHttpResponse  implements Serializable {

    private static final long serialVersionUID = 8813650489151635556L;

    private String protocolVersion = "HTTP/1.1";

    private int statusCode = 200;

    private HodorHttpHeaders headers = new HodorHttpHeaders();

    private byte[] body;

    public void addHeader(String headerName, String headerValue) {
        headers.addHeader(headerName, headerValue);
    }

    public void addHeader(Map.Entry<String, String> entry) {
        addHeader(entry.getKey(), entry.getValue());
    }

}
