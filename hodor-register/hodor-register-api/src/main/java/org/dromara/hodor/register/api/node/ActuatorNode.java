package org.dromara.hodor.register.api.node;

import org.dromara.hodor.common.utils.StringUtils;

/**
 * actuator node
 *
 * @author tomgs
 * @since 1.0
 */
public class ActuatorNode {

    /**
     * /actuator/clusters/{cluster}/{endpoint}  -> nodeInfo
     */
    public static final String ACTUATOR_CLUSTERS_PATH = "/actuator/clusters";

    /**
     * /actuator/binding/{cluster}/{groupName}  -> bindingTime
     */
    public static final String ACTUATOR_BINDING_PATH = "/actuator/binding";

    public static boolean isClusterPath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(ACTUATOR_CLUSTERS_PATH + StringUtils.PATH_SEPARATOR);
    }

    public static boolean isBindingPath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(ACTUATOR_BINDING_PATH + StringUtils.PATH_SEPARATOR);
    }

    public static String createClusterPath(String clusterName, String endpoint) {
        return StringUtils.format("{}/{}/{}", ACTUATOR_CLUSTERS_PATH, clusterName, endpoint);
    }

    public static String createBindingPath(String clusterName, String groupName) {
        return StringUtils.format("{}/{}/{}", ACTUATOR_BINDING_PATH, clusterName, groupName);
    }

    public static String getClusterPath(String clusterName) {
        return StringUtils.format("{}/{}", ACTUATOR_CLUSTERS_PATH, clusterName);
    }
}
