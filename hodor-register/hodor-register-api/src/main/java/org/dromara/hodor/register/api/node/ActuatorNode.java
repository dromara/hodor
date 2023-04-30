package org.dromara.hodor.register.api.node;

import org.dromara.hodor.common.utils.StringUtils;

/**
 * actuator node
 *
 * @author tomgs
 * @since 1.0
 */
public class ActuatorNode {

    public static final String ACTUATOR_GROUPS_PATH = "/actuator/groups";

    public static final String ACTUATOR_NODES_PATH = "/actuator/nodes";

    public static final String ACTUATOR_CLUSTERS_PATH = "/actuator/clusters";

    public static final String ACTUATOR_BINDING_PATH = "/actuator/binding";

    public static boolean isGroupPath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(ACTUATOR_GROUPS_PATH + StringUtils.PATH_SEPARATOR);
    }

    public static boolean isNodePath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(ACTUATOR_NODES_PATH + StringUtils.PATH_SEPARATOR);
    }

    public static boolean isClusterPath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(ACTUATOR_CLUSTERS_PATH + StringUtils.PATH_SEPARATOR);
    }

    public static boolean isBindingPath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(ACTUATOR_BINDING_PATH + StringUtils.PATH_SEPARATOR);
    }

    public static String createNodePath(String endpoint) {
        return StringUtils.format("{}/{}", ACTUATOR_NODES_PATH, endpoint);
    }

    public static String createGroupPath(String groupName, String endpoint) {
        return StringUtils.format("{}/{}/{}", ACTUATOR_GROUPS_PATH, groupName, endpoint);
    }

    public static String createClusterPath(String clusterName, String endpoint) {
        return StringUtils.format("{}/{}/{}", ACTUATOR_CLUSTERS_PATH, clusterName, endpoint);
    }

    public static String createBindingPath(String clusterName, String groupName) {
        return StringUtils.format("{}/{}/{}", ACTUATOR_BINDING_PATH, clusterName, groupName);
    }

}
