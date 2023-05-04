package org.dromara.hodor.admin.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * @author tomgs
 * 
 * @since 1.0
 **/
@Data
public class SimpleJobDTO implements Serializable {
    private String groupName;
    private String jobName;
}
