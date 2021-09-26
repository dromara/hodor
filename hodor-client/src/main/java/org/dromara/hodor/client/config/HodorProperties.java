package org.dromara.hodor.client.config;

import lombok.Data;
import org.dromara.hodor.common.storage.db.DataSourceConfig;
import org.dromara.hodor.common.utils.HostUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * hodor client config
 *
 * @author tomgs
 * @since 2021/1/5
 */
@Data
@ConfigurationProperties(prefix = "hodor")
public class HodorProperties {

    /**
     * 应用名称，这个用于和用户进行关联，需要先在管理平台进行注册分配
     */
    private String appName;

    /**
     * hodor分配的客户端密钥key，需要先在管理平台进行注册分配
     */
    private String appKey;

    /**
     * 任务注册地址
     */
    private String registryAddress;

    /**
     * 客户端host，如果存在多网卡的时候设置此值，默认自动获取本机ip
     */
    private String host = HostUtils.getLocalIp();

    /**
     * 客户端暴露端口，默认46367
     */
    private Integer port = 46367;

    /**
     * 任务执行队列大小，默认5000，短时任务队列为其1/2
     */
    private Integer queueSize;

    /**
     * 任务运行线程池大小
     */
    private Integer poolSize;

    /**
     * 任务堆积策略，默认0，0：丢弃最老、1：继续等待（阻塞）、2：自适应调整队列大小、3：增大执行线程数量、4：转移任务到别的机器
     */
    private Integer taskStackingStrategy = 0;

    /**
     * 长时任务临界值单位秒，默认60s为长时任务，统计上一次任务执行的时长，然后判断下一次执行是放在长时队列还是短时队列
     */
    private Integer longTaskTime = 60;

    /**
     * 任务日志路径
     */
    private String rootJobLogPath;

    /**
     * 数据库配置
     */
    private DataSourceConfig dataSourceConfig;

}
