package org.dromara.hodor.actuator.jobtype.flink;

import lombok.Data;

import java.util.List;
import java.util.Properties;

/**
 * 提交到yarn的实体类
 *
 * @since 1.0
 **/
@Data
public class FlinkSubmitArguments {

    /**
     * Flink本地配置目录
     */
    String localConfDir;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务运行主类
     */
    private String mainClass;

    /**
     * 应用jar包
     */
    private String applicationJar;

    /**
     * 应用依赖jar包
     */
    private String[] dependJars;

    /**
     * yarn 依赖jar包
     */
    private String yarnJars;

    /**
     * yarn resource manager address
     */
    private String yarnResourcemanagerAddress;

    /**
     * 任务队列
     */
    private String queue;

    /**
     * namenode 地址
     */
    private String defaultFS;

    /**
     * hdfs nameservices
     */
    private String nameservices;

    /**
     * eg: nn1,nn2
     */
    private String namenodes;

    /**
     * eg: nn1.hadoop:9000,nn2.hadoop:9000
     */
    private String namenodeRpcAddress;

    /**
     * spark或者mr依赖的文件
     */
    private String[] files;

    /**
     * 应用参数
     */
    private List<String> otherArgs;

    /**
     * 其他属性
     */
    private Properties properties;

}
