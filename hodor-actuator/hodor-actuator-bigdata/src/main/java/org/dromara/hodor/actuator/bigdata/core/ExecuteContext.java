package org.dromara.hodor.actuator.bigdata.core;

import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteContext implements JobContext {

    private static final long serialVersionUID = 3189116974529264486L;

    private String requestId;
    private String parameters;
    private String sharingRequestId;
    private String appId;
    private int statusCode;
    private String jobCompleteTime;

    private ConcurrentHashMap<String, Object> extraProperties = new ConcurrentHashMap<>();

    public ExecuteContext(String requestId, String parameters,
                          String sharingRequestId) {
        super();
        this.requestId = requestId;
        this.parameters = parameters;
        this.sharingRequestId = sharingRequestId;
    }
}


