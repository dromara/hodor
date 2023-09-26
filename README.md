#  HodorScheduler
## 介绍

Hodor是一个专注于任务编排和高可用性的一站式的分布式任务调度系统。通过提供任务编程API和接口协议，使用户可以使用不同的编程语言自定义任务类型，以支持各种业务场景下的任务执行。
目前已经实现了Java任务执行器和大数据任务执行器。

并且Hodor是弱依赖于第三方中间件的系统，能在运行时不依赖于数据库，从而降低第三方组件对系统高可用性的影响。
因为它实现了自定义的分布式存储和注册中心，来实现其高可用和任务持久化，进一步增强了系统的可靠性。

## 主要特性：
1. 支持DAG方式进行任务编排，使得任务之间的关系更加清晰，同时提高了系统的可扩展性和灵活性。
2. 支持Cron表达式配置定时任务，可以方便地实现各种定时任务需求。
3. 同时支持Java任务和大数据任务调度，满足不同场景下的任务调度需求。
4. 可扩展执行器，用户可以根据自己的需求自定义实现任务类型，从而更好地适应不同的业务场景。
5. 基于CopySet算法实现任务副本分配，提高了任务的可靠性和容错性，从而保障了任务的高可用性。

## 架构

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
│  └─hodor-actuator-schedulerx                  # 支持scheduleX2.0执行器（TODO）
├─hodor-admin                                   # hodor管理控制台（TODO）
├─hodor-client                                  # 提供与hodor server的交互sdk
├─hodor-common                                  # hodor公共依赖
├─hodor-core                                    # hodor核心业务
├─hodor-examples                                # hodor测试用例
├─hodor-extension                               # hodor扩展
│  ├─hodor-extension-cache-redis-impl
│  ├─hodor-extension-cache-register-impl
│  └─hodor-extension-cache-zk-impl
├─hodor-model                                   # hodor公共数据模型
├─hodor-register                                # hodor注册中心
│  ├─hodor-register-api
│  ├─hodor-register-embedded                    # 基于Raft实现注册中心
│  └─hodor-register-zookeeper                   # 基于zk实现注册中心
├─hodor-remoting                                # hodor通信框架
│  ├─hodor-remoting-api
│  └─hodor-remoting-netty
├─hodor-scheduler                               # hodor调度核心
│  ├─hodor-scheduler-api
│  └─hodor-scheduler-quartz
└─hodor-server                                  # hodor入口

```

运行方式：

1、在mysql下执行docs目录下面的`hodor_ddl.sql`

2、进入到`hodor-server`，启动调度器。

3、进入到`examples`下面的`hodor-actuator-java-example`，启动java任务执行器程序

## hodor打包部署说明

### hodor-admin打包
```shell
mvn package -pl hodor-dist/hodor-admin-dist -am -DskipTests=true
# 解压运行
nohup ./bin/start.sh > admin.log 2>&1 &
```

### hodor-scheduler-server打包
```shell
mvn package -pl hodor-dist/hodor-scheduler-server-dist -am -DskipTests=true
```

### hodor-actuator-agent打包
```shell
mvn package -pl hodor-dist/hodor-actuator-agent-dist -am -DskipTests=true
```

### hodor-actuator-java打包
```shell
mvn package -pl hodor-dist/hodor-actuator-java-dist -am -DskipTests=true
```

### hodor-actuator-java-example打包
```shell
mvn package -pl hodor-dist/hodor-actuator-examples-dist -am -DskipTests=true
```

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



