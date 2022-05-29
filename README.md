#  hodor scheduelr
# 介绍

hodor是一款分布式任务调度系统，我们聚焦于任务可编排、高可用的分布式任务调度器，而对于任务执行我们提供一个编程框架和接口协议自定义去实现（后续改造成grpc的方式），以达到支撑其它任务类型的执行，目前实现已经实现了java任务执行器和大数据任务的执行器、go语言实现的执行器基本上也已经完成主要业务场景用在脚本任务执行和CD场景。

## 特性

- 通过DAG的方式进行任务编排
- 支持Cron表达式配置定时任务
- 同时支持Java任务和大数据任务调度
- 可扩展执行器
- 基于CopySet算法实现任务副本分配
## 架构

![hodor架构设计图](docs/assets/img/hodor%E6%9E%B6%E6%9E%84%E8%AE%BE%E8%AE%A1%E5%9B%BE.png)

# 快速开始

项目结构：

```
hodor-scheduler
├─docs											          # 文档存放位置
├─hodor-actuator								      # hodor任务执行器
│  ├─hodor-actuator-bigdata						# 大数据任务执行器
│  ├─hodor-actuator-common						# 执行器公共依赖
│  └─hodor-actuator-java						  # Java任务执行器
├─hodor-admin									        # hodor管理控制台（TODO）
├─hodor-client									      # 待废弃
├─hodor-common									      # hodor公共依赖
├─hodor-core									        # hodor核心业务
├─hodor-examples								      # hodor测试用例
├─hodor-extension								      # hodor扩展
│  ├─hodor-extension-cache-redis-impl
│  ├─hodor-extension-cache-register-impl
│  └─hodor-extension-cache-zk-impl
├─hodor-model									        # hodor公共数据模型
├─hodor-register								      # hodor注册中心
│  ├─hodor-register-api
│  ├─hodor-register-embedded					# 待完成（TODO）
│  └─hodor-register-zookeeper					# 基于zk实现注册中心
├─hodor-remoting								      # hodor通信框架
│  ├─hodor-remoting-api
│  └─hodor-remoting-netty
├─hodor-scheduler								      # hodor调度核心
│  ├─hodor-scheduler-api
│  └─hodor-scheduler-quartz
├─hodor-server									      # hodor入口
└─hodor-ui										        # hodor前端模块
```

运行方式：

1、在mysql下执行docs目录下面的`hodor_ddl.sql`

2、进入到`hodor-server`，启动调度器。（调度器目前依赖zk）

3、进入到`examples`下面的`hodor-actuator-java-example`，启动java任务执行器程序

# 交流群

目前还是处于建设的过程中，平时只能有额外时间才投入进来，主要还是放在工作当中，如果有兴趣的同学想要参与实现或者一起学习，可以通过联系微信或邮件的方式加入。

未来方向是放在：

- 基于Raft协议实现调度节点高可用和注册中心
- 运行时移除第三方中间件
- 前端界面+控制台

Wechat：(备注：Hodor)

![image-20220529141555032](docs/assets/img/wechat.png)

Email：tincopper@foxmail.com



