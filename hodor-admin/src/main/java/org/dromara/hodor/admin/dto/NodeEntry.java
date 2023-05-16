package org.dromara.hodor.admin.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author tomgs
 * 
 * @since 1.0
 **/
@Data
public class NodeEntry implements Serializable {

    private String propSource;

    private String condition;

    private String groupName;

    private String jobName;

    private Integer expectedRuntime;

    private String id;

    private String jobType;

    private List<String> successEmail;

    private List<String> failureEmail;

    private Map<String,Object> layout;
}
