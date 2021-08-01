package org.dromara.hodor.model.node;

import lombok.Data;

import java.util.Objects;

/**
 * node info
 *
 * @author tomgs
 * @since 2021/1/6
 */
@Data
public class NodeInfo {

    /**
     * 执行端机器ip
     */
    private String ip;

    /**
     * 执行端端口
     */
    private Integer port;

    /**
     * 执行端进程pid
     */
    private String pid;

    /**
     * 版本号
     */
    private String version;

    /**
     * 执行端节点hostname
     */
    private String hostname;

    /**
     * cpu使用占比
     */
    private Double cpuRatio;

    /**
     * 内存使用占比
     */
    private Double memoryRatio;

    /**
     * 机器负载占比
     */
    private Double loadAverageRatio;

    /**
     * 任务队列大小
     */
    private Integer queueSize;

    /**
     * 在队列等待的任务大小
     */
    private Integer waitingQueueSize;

    /**
     * 任务执行次数
     */
    private Integer executeCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return ip.equals(nodeInfo.ip) &&
                port.equals(nodeInfo.port) &&
                hostname.equals(nodeInfo.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port, hostname);
    }

}
