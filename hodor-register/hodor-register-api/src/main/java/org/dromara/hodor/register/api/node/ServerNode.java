package org.dromara.hodor.register.api.node;

import org.dromara.hodor.common.utils.StringUtils;

/**
 * server node
 *
 * @author tomgs
 * @since 2020/7/6
 */
public class ServerNode {

    public static final String METADATA_PATH = "/scheduler/metadata";
    public static final String NODES_PATH = "/scheduler/nodes";
    public static final String COPY_SETS_PATH = "/scheduler/copysets";
    public static final String MASTER_PATH = "/scheduler/master";
    public static final String WORKER_PATH = "/worker";

    public static String getServerNodePath(String serverId) {
        return String.format("%s/%s", NODES_PATH, serverId);
    }

    public static boolean isWorkerPath(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }
        return path.startsWith(WORKER_PATH);
    }

}
