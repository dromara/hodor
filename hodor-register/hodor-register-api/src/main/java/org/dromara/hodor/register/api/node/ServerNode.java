package org.dromara.hodor.register.api.node;

import org.dromara.hodor.common.utils.StringUtils;

/**
 * server node
 *
 * @author tomgs
 * @since 2020/7/6
 */
public class ServerNode {

    public static final String PATH_SEPARATOR = "/";

    public static final String METADATA_PATH = "/scheduler/metadata";

    public static final String COPY_SETS_PATH = "/scheduler/copysets";

    public static final String NODES_PATH = "/scheduler/nodes";

    public static final String MASTER_PATH = "/scheduler/master";

    public static final String LATCH_PATH = "/scheduler/latch";

    public static final String ACTIVE_PATH = "/scheduler/master/active";

    public static final String WORKER_PATH = "/worker";

    public static String getServerNodePath(String serverId) {
        return String.format("%s/%s", NODES_PATH, serverId);
    }

    public static boolean isWorkerPath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(WORKER_PATH);
    }

    public static boolean isNodePath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(NODES_PATH);
    }

    public static boolean isMasterActivePath(String path) {
        return StringUtils.isNotBlank(path) && path.startsWith(ACTIVE_PATH);
    }

    public static boolean isMetadataPath(String path) {
        return StringUtils.isNotBlank(path) && path.equals(METADATA_PATH);
    }
}
