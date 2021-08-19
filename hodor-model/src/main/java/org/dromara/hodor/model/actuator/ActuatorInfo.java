package org.dromara.hodor.model.actuator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.hodor.model.node.NodeInfo;

import java.util.Set;

/**
 *  actuator info
 *
 * @author tomgs
 * @version 2021/8/1 1.0 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActuatorInfo {

    private NodeInfo nodeInfo;

    private String nodeEndpoint;

    private Set<String> groupNames;

    private long lastHeartbeat;

}
