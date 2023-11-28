#  HodorScheduler

## 介绍
Hodor是一个专注于**任务调度**以及**任务编排**的**一站式**分布式任务调度系统。

通过提供任务编程API和接口协议， 方便支持用户使用不同的编程语言实现任务执行以及自定义任务类型的扩展，以满足各种业务场景下的任务执行。

支持多种接入方式，支持Java SDK包方式、Java Agent独立进程方式接入，以及兼容XXLJob框架、Spring Task框架任务无缝接入。

多语言执行器实现，计划支持Go或者Rust语言执行器（正在设计与开发中）的接入，满足在资源有限的执行器节点执行，比如物联网场景下面的定时任务执行。

支持丰富的任务类型，除了支持普通的Java、脚本任务的执行，也支持了大数据任务Hadoop、Spark、Flink、Kettle等主流任务的执行，满足用户的不同场景。

## 主要特性
1. 支持Cron、FixedRate、FixedDelay、OnceTime方式配置定时任务，可以方便地实现各种定时任务需求。
2. 支持DAG方式进行任务编排，使得任务之间的关系更加清晰，同时提高了系统的可扩展性和灵活性。
3. 支持Java任务和大数据任务调度，兼容XXLJob、SpringTask调度任务，满足不同场景下的任务调度需求。
4. 支持可扩展执行器，用户可以根据自己的需求自定义实现任务类型，从而更好地适应不同的业务场景。
5. 支持任务静态分片、动态分片、广播、WorkFlow等多种任务执行模式。
6. 支持任务自动创建与手动创建以及批量创建等方式，任务的暂停、恢复、Kill等动作，以及实时日志查看等。
7. 基于CopySet算法实现任务副本分配，提高了任务的可靠性和容错性，从而保障了任务的高可用性。

详细文档请查阅：https://www.yuque.com/tomgs/hodor

## 架构设计

![hodor架构设计图](docs/assets/img/hodor%E6%9E%B6%E6%9E%84%E8%AE%BE%E8%AE%A1%E5%9B%BE.png)

## 快速开始

项目结构：

```
hodor-scheduler
├─docs                                          # 文档存放位置
├─hodor-actuator                                # hodor任务执行器
│  ├─hodor-actuator-api                         # 执行器扩展接口
│  ├─hodor-actuator-java                        # jar包方式任务执行器
│  ├─hodor-actuator-agent                       # 独立进程方式任务执行器
│  ├─hodor-actuator-xxljob                      # 支持xxl-job执行器
│  └─hodor-actuator-springtask                  # 支持spring task执行器
├─hodor-admin                                   # hodor管理控制台（TODO）
├─hodor-client                                  # 提供与hodor server的交互sdk
├─hodor-common                                  # hodor公共依赖
├─hodor-core                                    # hodor核心业务
├─hodor-examples                                # hodor测试用例
├─hodor-cache                                   # hodor扩展
│  ├─hodor-cache-local                          # 基于本地内存的缓存
│  ├─hodor-cache-embedded                       # 基于embedded的分布式缓存
│  └─hodor-cache-redis                          # 基于redis的分布式缓存
├─hodor-model                                   # hodor公共数据模型
├─hodor-storage                                 # hodor storage模块，任务资源管理
├─hodor-register                                # hodor注册中心
│  ├─hodor-register-api
│  ├─hodor-register-embedded                    # 基于Raft实现注册中心
│  └─hodor-register-zookeeper                   # 基于zk实现注册中心
├─hodor-remoting                                # hodor通信框架
│  ├─hodor-remoting-api
│  └─hodor-remoting-netty
├─hodor-scheduler                               # hodor调度器实现
│  ├─hodor-scheduler-api
│  └─hodor-scheduler-quartz
└─hodor-server                                  # hodor调度服务

```
## 本地源码运行方式
1. 在mysql下执行docs目录下面的hodor_ddl.sql。
2. 在项目根目录下通过mvn package -DskipTests=true命令编译整个项目，因为涉及到一些代码的自动生成。
3. 进入到hodor-server，启动HodorServer调度器。
4. 进入到examples下面的hodor-actuator-java-example，启动java任务执行器程序
5. java执行器启动完成后会自动将任务注册到调度器里面来

## 任务使用说明
详细参考 [执行器操作指南](https://www.yuque.com/tomgs/hodor/iqrsqxh2i2qt5ixi)

### java任务接入demo
详细demo代码参考 _hodor-examples/hodor-actuator-java-example_ 模块

> 普通java任务
```java
@Job(group = "testGroup", jobName = "test1", cron = "0/30 * * * * ?")
public String test1(JobExecutionContext context) {
    log.info("Job [testGroup#test1] execute, context: {}", context);
    JobLogger logger = context.getJobLogger();
    logger.info("start executor job test1");
    logger.info("job argument: {}", context.getJobParameter());
    logger.info("executing......");
    logger.info("executed");
    return "a=123";
}
```

> 动态分片任务
```java
// 任务动态切分
@Job(command = "splitStage")
public List<ShardData> split(JobExecutionContext context) {
    ShardData shardData = ShardData.builder().id(0).params("hello").build();
    ShardData shardData1 = ShardData.builder().id(1).params("world").build();
    return Lists.newArrayList(shardData, shardData1);
}

// 任务并行执行
@Job(command = "parallelJob")
public String parallelJob(JobExecutionContext context) {
    log.info("Job [parallelJob] execute, context: {}", context);
    JobLogger logger = context.getJobLogger();
    final ShardData parentJobData = context.getParentJobData(ShardData.class);
    logger.info("start executor job parallelJob, parentJobData: {}", parentJobData);
    log.info("start executor job parallelJob, parentJobData: {}", parentJobData);
    return "a=123";
}

// 任务汇总执行
@Job(command = "reduceJob2")
public void reduceJob(JobExecutionContext context) {
    log.info("Job [reduceJob] execute, context: {}", context);
    JobLogger logger = context.getJobLogger();
    logger.info("job argument: {}", context.getJobParameter());
    logger.info("executed");
    logger.info("Job [reduceJob] execute, context: {}", context);
    logger.info("Job [reduceJob] execute, results {}", context.getParentJobExecuteResults()
    );
    logger.info("Job [reduceJob] execute, statues {}", context.getParentJobExecuteStatuses());
}
```

### xxljob任务接入demo
详细demo代码参考 _hodor-examples/hodor-actuator-xxljob-example_ 模块
```java
/**
 * 1、简单任务示例（Bean模式）
 */
@XxlJob("demoJobHandler")
public void demoJobHandler() throws Exception {
    XxlJobHelper.log("XXL-JOB, Hello World.");

    for (int i = 0; i < 5; i++) {
        XxlJobHelper.log("beat at:" + i);
        TimeUnit.SECONDS.sleep(2);
    }
    // default success
}

/**
 * 2、分片广播任务
 */
@XxlJob("shardingJobHandler")
public void shardingJobHandler() throws Exception {
    // 分片参数
    int shardIndex = XxlJobHelper.getShardIndex();
    int shardTotal = XxlJobHelper.getShardTotal();

    XxlJobHelper.log("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);

    // 业务逻辑
    for (int i = 0; i < shardTotal; i++) {
        if (i == shardIndex) {
            XxlJobHelper.log("第 {} 片, 命中分片开始处理", i);
        } else {
            XxlJobHelper.log("第 {} 片, 忽略", i);
        }
    }
}
```

### spring task任务接入demo
详细demo代码参考 _hodor-examples/hodor-actuator-springtask-example_ 模块
```java
@Scheduled(fixedRate = 3000)
public void task1() throws InterruptedException {
    TimeUnit.SECONDS.sleep(2);
    System.out.println("执行 fixedRate 任务的时间：" + new Date(System.currentTimeMillis()));
}

@Scheduled(fixedDelay = 4000)
public void task2() throws InterruptedException {
    TimeUnit.SECONDS.sleep(2);
    System.out.println("执行 fixedDelay 任务的时间：" + new Date(System.currentTimeMillis()));
}

@Scheduled(cron = "0/5 * * * * ?")
public void task3() {
    System.out.println("执行 cron 任务的时间：" + new Date(System.currentTimeMillis()));
}
```

### agent 方式任务接入demo
详细参考 [执行器操作指南](https://www.yuque.com/tomgs/hodor/iqrsqxh2i2qt5ixi) 中的agent方式的使用。

## hodor打包部署说明
详细参考 [安装部署操作指南](https://www.yuque.com/tomgs/hodor/giukp9y4plqb55ng)

## 交流学习

目前，Hodor仍处于建设的过程中，团队成员只能抽出空闲时间投入其中，主要精力还是放在日常工作中。
如果有兴趣的同学想要参与Hodor的实现或者一起学习，欢迎通过微信联系我们，我们非常欢迎新的成员加入！

Hodor的未来发展方向包括以下几个方面：
1. 前端界面和控制台开发，为用户提供更加友好、直观的任务管理界面。
2. 执行器开发工作，包括执行编程框架和大数据任务执行器的优化，以提高系统的性能和稳定性。
3. 通信协议改造，计划将目前自定义的协议改造为grpc的方式，以提高系统的可扩展性和兼容性。
4. 优化CopySet算法的实现，以支持机架感知，更好地分配任务副本。
5. 开发多语言执行器，主要放在go和rust语言，以满足用户不同的需求和偏好。

Wechat：(备注：Hodor)

![image-20220529141555032](docs/assets/img/wechat.png)



