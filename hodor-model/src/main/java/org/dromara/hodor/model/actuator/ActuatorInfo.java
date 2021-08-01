package org.dromara.hodor.model.actuator;

import lombok.Data;
import org.dromara.hodor.model.node.NodeInfo;

import java.util.Set;

/**
 *  actuator info
 *
 * @author tomgs
 * @version 2021/8/1 1.0 
 */
@Data
public class ActuatorInfo {

    private NodeInfo nodeInfo;

    private Set<String> groupNames;
    
}
