package org.dromara.hodor.register.api.node;

import org.dromara.hodor.common.utils.StringUtils;

/**
 * scheduler node
 *
 * @author tomgs
 * @since 1.0
 */
public class SchedulerNode {

    public static final String METADATA_PATH = "/scheduler/metadata";

    public static final String NODES_PATH = "/scheduler/nodes";

    public static final String LATCH_PATH = "/scheduler/latch";

    public static final String MASTER_ACTIVE_PATH = "/scheduler/master/active";

    public static final String METRICS_NODES_PATH = "/scheduler/metrics/nodes";

    public static String getServerNodePath(String serverEndpoint) {
        return String.format("%s/%s", NODES_PATH, serverEndpoint);
    }

    public static boolean isNodePath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(NODES_PATH + StringUtils.PATH_SEPARATOR);
    }

    public static boolean isMasterActivePath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(MASTER_ACTIVE_PATH);
    }

    public static boolean isMetadataPath(String path) {
        return StringUtils.isNotBlank(path) && path.equals(METADATA_PATH);
    }

    public static String getServerMetricsNodePath(String serverEndpoint) {
        return String.format("%s/%s", METRICS_NODES_PATH, serverEndpoint);
    }
}
