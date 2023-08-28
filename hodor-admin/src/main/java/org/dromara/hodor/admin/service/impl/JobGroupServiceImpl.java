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

package org.dromara.hodor.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.core.MsgCode;
import org.dromara.hodor.admin.dto.user.UserInfo;
import org.dromara.hodor.admin.exception.ServiceException;
import org.dromara.hodor.admin.service.JobGroupService;
import org.dromara.hodor.common.utils.DateUtils;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobGroup;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.mapper.JobGroupMapper;
import org.dromara.hodor.core.mapper.JobInfoMapper;
import org.springframework.stereotype.Service;

/**
 * JobGroupServiceImpl
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobGroupServiceImpl implements JobGroupService {

    private final JobGroupMapper jobGroupMapper;

    private final JobInfoMapper jobInfoMapper;

    @Override
    public PageInfo<JobGroup> queryGroupListPaging(UserInfo user, String groupName, Integer pageNo, Integer pageSize) {
        IPage<JobGroup> page = new Page<>(pageNo, pageSize);
        jobGroupMapper.selectPage(page, Wrappers.<JobGroup>lambdaQuery()
            .like(groupName != null, JobGroup::getGroupName, groupName));
        PageInfo<JobGroup> pageInfo = new PageInfo<>();
        return pageInfo.setRows(page.getRecords())
            .setTotal(page.getTotal())
            .setTotalPage((int) page.getPages())
            .setCurrentPage((int) page.getCurrent())
            .setPageNo(pageNo)
            .setPageSize(pageSize);
    }

    @Override
    public JobGroup createGroup(UserInfo user, JobGroup group) {
        setGroupInfo(user, group);
        group.setCreatedAt(DateUtils.nowDate());
        jobGroupMapper.insert(group);
        return group;
    }

    @Override
    public void updateJobGroup(UserInfo user, JobGroup group) {
        setGroupInfo(user, group);
        jobGroupMapper.updateById(group);
    }

    @Override
    public void deleteJobGroup(UserInfo user, Long id) {
        final JobGroup jobGroup = jobGroupMapper.selectById(id);
        final Long count = jobInfoMapper.selectCount(Wrappers.<JobInfo>lambdaQuery()
            .eq(JobInfo::getGroupName, jobGroup.getGroupName()));
        if (count > 0) {
            throw new ServiceException(MsgCode.DELETE_GROUP_ERROR, StringUtils.format("Current group has {} jobs", count));
        }
        jobGroupMapper.deleteById(id);
    }

    @Override
    public JobGroup queryById(Long id) {
        return jobGroupMapper.selectById(id);
    }

    private static void setGroupInfo(UserInfo user, JobGroup group) {
        group.setCreateUser(user.getUsername());
        group.setUserId(user.getId());
        group.setTenantId(user.getTenantId());
        group.setUpdatedAt(DateUtils.nowDate());
    }

}
