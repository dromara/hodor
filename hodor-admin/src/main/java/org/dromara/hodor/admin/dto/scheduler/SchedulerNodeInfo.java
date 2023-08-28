/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.admin.dto.scheduler;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * SchedulerNodeInfo
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class SchedulerNodeInfo {

    /**
     * 集群名称
     */
    private String name;

    /**
     * 节点角色
     */
    private String role;

    /**
     * 上报时间戳
     */
    private Long reportTime;

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
    private Long pid;

    /**
     * 版本号
     */
    private String version;

    /**
     * 执行端节点hostname
     */
    private String hostname;

    /**
     * cpu使用
     */
    private Double cpuUsage;

    /**
     * 内存使用
     */
    private Double memoryUsage;

    /**
     * 机器负载
     */
    private Double loadAverage;

}
