package org.dromara.hodor.client.config;

import lombok.Data;

/**
 * heartbeat msg
 *
 * @author tomgs
 * @since 2021/1/6
 */
@Data
public class HeartbeatMsg {

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

}
