package org.dromara.hodor.server.component;

/**
 * constants
 *
 * @author tomgs
 */
public interface Constants {

    Integer LEAST_NODE_COUNT = 3;

    /**
     * 副本数量，不超过节点数量
     */
    Integer REPLICA_COUNT = 2;

    /**
     * 副本散布宽度，小于节点数量
     */
    Integer SCATTER_WIDTH = 2;

}
