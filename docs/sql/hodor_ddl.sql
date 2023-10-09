SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hodor_job_info
-- ----------------------------
DROP TABLE IF EXISTS `hodor_job_info`;
DROP TABLE IF EXISTS `hodor_job_exec_detail`;

CREATE TABLE `hodor_job_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `hash_id` bigint(32) NOT NULL DEFAULT -1 COMMENT '任务hash id',
  `group_name` varchar(64) CHARACTER SET utf8 NOT NULL COMMENT '任务组名称',
  `job_name` varchar(64) CHARACTER SET utf8 NOT NULL COMMENT '任务名称',
  `job_category` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT 'default' COMMENT '任务分类',
  `job_type` int(11) NOT NULL DEFAULT 0 COMMENT '任务命令类型， 0：普通类型 2：工作流类型 3：分片任务 3: 广播任务, 4：mr任务，默认0',
  `job_path` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务调用路径，Java任务即类全路径',
  `job_command_type` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务指令类型，java,shell,python',
  `job_command` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务执行的命令，Java任务即执行的方法',
  `job_status` int(11) NOT NULL DEFAULT 0 COMMENT '任务状态0未激活 1可运行 2正在运行 3暂停',
  `schedule_strategy` int(11) CHARACTER SET utf8 NOT NULL DEFAULT 0 COMMENT '调度策略, 0: random, 1: round_robin, 2: lowest_load, 4: specify_actuator',
  `schedule_exp` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '-' COMMENT '调度策略表达式',
  `time_type` int(11) CHARACTER SET utf8 NOT NULL DEFAULT 0 COMMENT '任务运行时间类型, 0: none, 1: cron, 2: fixed_delay, 3: fixed_rate, 4: once_time',
  `time_exp` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '-' COMMENT '任务运行时间表达式',
  `time_zone` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务时区表达式',
  `job_parameters` varchar(512) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '分片参数',
  `extensible_parameters` varchar(512) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '可扩展任务参数，用于针对不是常规任务的扩展',
  `failover` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否开启故障转移 1:开启 0：不开启',
  `misfire` tinyint(1) NOT NULL DEFAULT 0 COMMENT '错过执行是否马上调度 1：错过马上调度 0 ：否',
  `fire_now` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否马上开始执行（比如设置时间是5分钟，那么5分钟后才执行），如果设置为true，则现在执行一次，1：马上执行，0：否',
  `retry_count` tinyint(1) NOT NULL DEFAULT 0 COMMENT '重试次数，不超过10次',
  `timeout` int(11) NOT NULL DEFAULT 300 COMMENT '超时时间，默认5分钟，单位s',
  `next_execute_time` datetime DEFAULT NULL COMMENT '下一次执行时间',
  `prev_execute_time` datetime DEFAULT NULL COMMENT '最近一次执行时间',
  `create_time` datetime DEFAULT NULL COMMENT '任务创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '任务更新时间',
  `start_time` datetime DEFAULT NULL COMMENT '任务开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '任务结束时间',
  `priority` int(11) NOT NULL DEFAULT 0 COMMENT '任务优先级，0：低，1：中，2：高；默认0',
  `job_data_path` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务的jar，sql文件，sh文件信息等',
  `job_desc` varchar(1024) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务描述',
  `version` bigint(32) NOT NULL DEFAULT -1 COMMENT '任务版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_group` (`job_name`, `group_name`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4;

-- ----------------------------
-- Table structure for hodor_job_exec_detail
-- ----------------------------
CREATE TABLE `hodor_job_exec_detail` (
    `id` bigint NOT NULL COMMENT '任务唯一标识',
    `group_name` varchar(100) NOT NULL COMMENT '任务组名称',
    `job_name` varchar(100) NOT NULL COMMENT '任务名称',
    `sharding_count` int NOT NULL DEFAULT '1' COMMENT '分片数量',
    `sharding_id` int NOT NULL DEFAULT '0' COMMENT '分片id',
    `sharding_param` int NOT NULL DEFAULT '0' COMMENT '分片参数',
    `scheduler_endpoint` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '调度服务器IP',
    `actuator_endpoint` char(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '业务执行服务器IP',
    `schedule_start` timestamp NULL DEFAULT NULL COMMENT '调度开始时间',
    `schedule_end` timestamp NULL DEFAULT NULL COMMENT '调度结束时间',
    `execute_start` timestamp NULL DEFAULT NULL COMMENT '执行开始时间',
    `execute_end` timestamp NULL DEFAULT NULL COMMENT '执行结束时间',
    `elapsed_time` int NOT NULL DEFAULT '0' COMMENT '任务执行耗时',
    `execute_status` char(1) DEFAULT '1' COMMENT '任务执行状态，见JobExecuteStatus',
    `comments` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '明细描述',
    `is_timeout` int NOT NULL DEFAULT '0' COMMENT '是否超时,0:否,1:是',
    `enc_type` int DEFAULT NULL COMMENT '1:rar 2:zip 3:tar 4:json  数据保存类型',
    `detailed_log` blob COMMENT '存储详细日志',
    `job_exec_data` blob COMMENT 'job执行信息',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `hodor_flow_job_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_name` varchar(100) NOT NULL DEFAULT '' COMMENT 'job所属组名',
  `job_name` varchar(100) NOT NULL DEFAULT '' COMMENT 'job名称',
  `create_time` timestamp DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp DEFAULT NULL COMMENT '更新时间',
  `enc_type` int(4) NOT NULL DEFAULT 0 COMMENT '压缩类型  0: plain, 1：zip',
  `flow_data` blob COMMENT '依赖关系数据',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_job_group` (`job_name`, `group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `hodor_flow_job_exec_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `request_id` int(20) NOT NULL COMMENT '任务执行id',
  `group_name` varchar(100) NOT NULL DEFAULT '' COMMENT 'job所属组名',
  `job_name` varchar(100) NOT NULL DEFAULT '' COMMENT 'job名称',
  `scheduler_name` varchar(100) NOT NULL DEFAULT '' COMMENT '调度节点名称',
  `msgCode` varchar(100) NOT NULL DEFAULT 'READY' COMMENT 'Flow状态',
  `execute_start` timestamp DEFAULT NULL COMMENT '创建时间',
  `execute_end` timestamp DEFAULT NULL COMMENT '更新时间',
  `elapsed_time` int(4) NOT NULL DEFAULT 0 COMMENT '执行耗时',
  `enc_type` int(4) NOT NULL DEFAULT 0 COMMENT '压缩类型  0: plain, 1：zip',
  `flow_exec_data` blob COMMENT 'flow任务执行明细关系数据',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE INDEX index_job_key_status USING BTREE ON hodor.hodor_flow_job_exec_detail (group_name, job_name, msgCode);

CREATE TABLE `hodor_actuator_binding` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `cluster_name` varchar(100) NOT NULL COMMENT '执行器集群名称',
  `group_name` varchar(100) NOT NULL COMMENT '绑定任务分组名称',
  `create_time` timestamp DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_group_cluster` (`group_name`, `cluster_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `hodor_tenant` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `tenant_name` varchar(32) NOT NULL COMMENT '租户名称',
    `corp_name` varchar(32) NOT NULL COMMENT '公司名称',
    `email` varchar(32) NOT NULL COMMENT '联系邮箱',
    `description` varchar(256) DEFAULT '' COMMENT '租户描述',
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `hodor_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(32) NOT NULL COMMENT '密码',
  `email` varchar(32) DEFAULT '' COMMENT '联系邮箱',
  `phone` varchar(32) DEFAULT '' COMMENT '联系电话',
  `tenant_id` bigint NOT NULL COMMENT '租户id',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `hodor_job_group` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `group_name` varchar(32) NOT NULL COMMENT '分组名称',
   `cluster_name` varchar(32) NOT NULL COMMENT '执行集群名称',
   `create_user` varchar(32) NOT NULL COMMENT '创建人',
   `user_id` bigint NOT NULL COMMENT '用户id',
   `tenant_id` bigint NOT NULL COMMENT '租户id',
   `remark` varchar(128) DEFAULT '' COMMENT '备注',
   `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`),
   UNIQUE KEY `idx_group_name` (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `hodor_user_groups` (
 `id` bigint NOT NULL AUTO_INCREMENT,
 `user_id` bigint NOT NULL COMMENT '用户id',
 `group_id` bigint NOT NULL COMMENT '租户id',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
