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

package org.dromara.hodor.admin.service;

import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobInfo;

/**
 * JobOperatorService
 *
 * @author tomgs
 * @since 1.0
 */
public interface JobOperatorService {

    /**
     * 查询任务列表
     *
     * @param jobInfo 任务信息
     * @param pageNo 当前分页数
     * @param pageSize 分页大小
     * @return 任务信息列表
     */
    PageInfo<JobInfo> queryByPage(JobInfo jobInfo, Integer pageNo, Integer pageSize);

    /**
     * 查询指定任务
     * @param id 任务id
     * @return 任务信息
     */
    JobInfo queryById(Long id);

    /**
     * 新增任务
     * @param jobInfo 任务信息
     * @return 新增任务信息
     */
    JobInfo addJob(JobInfo jobInfo);

    /**
     * 更新任务
     * @param jobInfo 任务信息
     * @return 更新后任务信息
     */
    JobInfo updateById(JobInfo jobInfo);

    /**
     * 删除任务
     * @param id 任务id
     * @return 删除结果
     */
    Boolean deleteById(Long id);

    /**
     * 停止指定任务，会将任务从调度器删除
     * @param id 任务id
     * @return 操作结果
     */
    Boolean stopById(Long id);

    /**
     * 恢复指定任务，会将任务从调度器删除
     * @param id 任务id
     * @return 操作结果
     */
    Boolean resumeById(Long id);

    /**
     * 执行指定任务，会将任务从调度器删除
     * @param id 任务id
     * @return 操作结果
     */
    Boolean executeById(Long id);
}
