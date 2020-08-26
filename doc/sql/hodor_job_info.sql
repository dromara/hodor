SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hodor_job_info
-- ----------------------------
CREATE TABLE `hodor_job_info` (
  `id` int(11) NOT NULL COMMENT '任务id',
  `hash_id` int(11) NOT NULL COMMENT '任务hash id',
  `group_name` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '任务组名称',
  `job_name` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '任务名称',
  `job_category` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '任务分类',
  `job_type` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务的类型',
  `job_path` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '任务调用路径，Java任务即类全路径',
  `job_command` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '任务执行的命令，Java任务即执行的方法',
  `job_status` int(11) NOT NULL DEFAULT NULL COMMENT '任务状态0未激活 1可运行 2正在运行 3暂停',
  `is_dependence` int(1) NOT NULL DEFAULT 0 COMMENT '0:表示改job没有依赖；1：有相关依赖；',
  `cron_expression` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '任务运行时间表达式',
  `sharding_count` int(11) NOT NULL DEFAULT NULL COMMENT '任务分片数',
  `job_parameters` varchar(512) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '分片参数',
  `extensible_parameters` varchar(512) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '可扩展任务参数，用于针对不是常规任务的扩展',
  `failover` int(11) NOT NULL DEFAULT NULL COMMENT '是否开启故障转移 1:开启 0：不开启',
  `misfire` int(11) NOT NULL DEFAULT NULL COMMENT '错过执行是否马上调度 1：错过马上调度 0 ：否',
  `fire_now` int(11) NOT NULL DEFAULT NULL COMMENT ' 是否马上开始执行（比如设置时间是5分钟，那么5分钟后才执行），如果设置为true，则现在执行一次，1：马上执行，0：否',
  `is_once` int(11) NOT NULL DEFAULT NULL,
  `is_broadcast` int(11) NOT NULL DEFAULT 0 COMMENT '是否广播模式 0：否 1：是，默认否',
  `slave_ip` varchar(32) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '该job被分配到slave的IP',
  `time_out` bigint(20) NOT NULL DEFAULT NULL COMMENT '超时时间，默认5分钟',
  `active_time` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '激活的时间',
  `next_execute_time` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '下一次执行时间',
  `prev_execute_time` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '最近一次执行时间',
  `priority` int(11) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '任务优先级',
  `end_time` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '任务结束时间',
  `create_time` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT NULL,
  `job_data_path` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '任务的jar，sql文件，sh文件信息等',
  `job_desc` varchar(1024) CHARACTER SET utf8 NOT NULL DEFAULT NULL COMMENT '任务描述 ',
  UNIQUE INDEX `job_group_idx`(`group_name`, `job_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8;
DROP TABLE IF EXISTS `hodor_job_info`;

SET FOREIGN_KEY_CHECKS = 1;
