SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hodor_job_info
-- ----------------------------
DROP TABLE IF EXISTS `hodor_job_info`;
CREATE TABLE `hodor_job_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `hash_id` int(11) NOT NULL DEFAULT -1 COMMENT '任务hash id',
  `group_name` varchar(64) CHARACTER SET utf8 NOT NULL COMMENT '任务组名称',
  `job_name` varchar(64) CHARACTER SET utf8 NOT NULL COMMENT '任务名称',
  `job_category` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT 'default' COMMENT '任务分类',
  `job_type` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务的类型',
  `job_path` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务调用路径，Java任务即类全路径',
  `job_command` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务执行的命令，Java任务即执行的方法',
  `job_status` int(11) NOT NULL DEFAULT 0 COMMENT '任务状态0未激活 1可运行 2正在运行 3暂停',
  `is_dependence` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0:表示改job没有依赖；1：有相关依赖；',
  `cron_expression` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务运行时间表达式',
  `sharding_count` int(11) NOT NULL DEFAULT 0 COMMENT '任务分片数',
  `job_parameters` varchar(512) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '分片参数',
  `extensible_parameters` varchar(512) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '可扩展任务参数，用于针对不是常规任务的扩展',
  `failover` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否开启故障转移 1:开启 0：不开启',
  `misfire` tinyint(1) NOT NULL DEFAULT 0 COMMENT '错过执行是否马上调度 1：错过马上调度 0 ：否',
  `fire_now` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否马上开始执行（比如设置时间是5分钟，那么5分钟后才执行），如果设置为true，则现在执行一次，1：马上执行，0：否',
  `is_once` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为一次性任务，0：否，1：是；默认0',
  `is_broadcast` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否广播模式 0：否，1：是；默认0',
  `slave_ip` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '该job被分配到slave的IP',
  `timeout` int(11) NOT NULL DEFAULT 300 COMMENT '超时时间，默认5分钟，单位s',
  `active_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '激活的时间',
  `next_execute_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下一次执行时间',
  `prev_execute_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近一次执行时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
  `end_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务结束时间',
  `priority` int(11) CHARACTER SET utf8 NOT NULL DEFAULT 0 COMMENT '任务优先级，0：低，1：中，2：高；默认0',
  `job_data_path` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务的jar，sql文件，sh文件信息等',
  `job_desc` varchar(1024) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务描述 ',
  UNIQUE INDEX `job_group_idx`(`job_name`, `group_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8;

-- ----------------------------
-- Table structure for hodor_job_exec_detail
-- ----------------------------
DROP TABLE IF EXISTS `hodor_job_exec_detail`;
CREATE TABLE `hodor_job_exec_detail` (
  `id` int(11) NOT NULL COMMENT '任务唯一标识',
  `group_name` varchar(100) NOT NULL COMMENT '任务组名称',
  `job_name` varchar(100) NOT NULL COMMENT '任务名称',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT NULL COMMENT '最后一次更新时间',
  `schedule_sip` char(20) DEFAULT NULL COMMENT '调度服务器IP',
  `business_sip` char(20) DEFAULT NULL COMMENT '业务执行服务器IP',
  `schedule_start` timestamp NULL DEFAULT NULL COMMENT '调度开始时间',
  `schedule_end` timestamp NULL DEFAULT NULL COMMENT '调度结束时间',
  `execute_start` timestamp NULL DEFAULT NULL COMMENT '执行开始时间',
  `execute_end` timestamp NULL DEFAULT NULL COMMENT '执行结束时间',
  `time_consuming` int(11) NOT NULL DEFAULT '0' COMMENT '任务执行耗时',
  `current_status` char(1) DEFAULT '1' COMMENT '当前状态,1表成功,0表失败',
  `error_location` varchar(128) DEFAULT NULL COMMENT '失败时异常位置',
  `error_type` char(6) DEFAULT NULL COMMENT '失败时异常类型：-1：调度失败，1：任务文件不存在，2：任务执行命令不存在，3：任务执行失败，4：任务执行中途被killed',
  `error_reason` text COMMENT '失败原因',
  `is_timeout` int(1) NOT NULL DEFAULT '0' COMMENT '是否超时,0:否,1:是',
  `recv_time` varchar(32) DEFAULT NULL COMMENT 'php接收请求的时间',
  `kill_process` int(1) NOT NULL DEFAULT '0' COMMENT '是否是强制杀死任务,0:否,1:是',
  `sharding_request_id` varchar(32) DEFAULT NULL,
  `instance_id` varchar(100) DEFAULT NULL COMMENT '实例ID',
  `enc_type` int(4) DEFAULT NULL COMMENT '1:rar 2:zip 3:tar 4:json  数据保存类型',
  `detailed_log` blob COMMENT '存储详细日志',
  `job_exe_data` blob COMMENT 'job执行信息',
  `parent_request_id` varchar(32) NOT NULL DEFAULT '' COMMENT 'flow上一任务的requestid'
  PRIMARY KEY (`id`,`ix_created_at`),
  KEY `i_ix_group_name` (`ix_group_name`) USING HASH,
  KEY `i_ix_job_name` (`ix_job_name`) USING HASH,
  KEY `ix_created_at` (`ix_created_at`),
  KEY `index_job_group_create` (`ix_group_name`,`ix_job_name`,`ix_created_at`),
  KEY `index_job_group` (`ix_group_name`,`ix_job_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
