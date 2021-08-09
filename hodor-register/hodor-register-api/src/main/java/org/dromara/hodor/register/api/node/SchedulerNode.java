package org.dromara.hodor.register.api.node;

import org.dromara.hodor.common.utils.StringUtils;

/**
 * scheduler node
 *
 * @author tomgs
 * @since 2020/7/6
 */
public class SchedulerNode {

    public static final String METADATA_PATH = "/scheduler/metadata";

    public static final String NODES_PATH = "/scheduler/nodes";

    public static final String LATCH_PATH = "/scheduler/latch";

    public static final String MASTER_ACTIVE_PATH = "/scheduler/master/active";

    public static final String JOB_EVENT = "/scheduler/job-event";

    public static String getServerNodePath(String serverId) {
        return String.format("%s/%s", NODES_PATH, serverId);
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

}
