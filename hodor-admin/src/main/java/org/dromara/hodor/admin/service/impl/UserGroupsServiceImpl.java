package org.dromara.hodor.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.domain.UserGroups;
import org.dromara.hodor.admin.mapper.UserGroupsMapper;
import org.dromara.hodor.admin.service.UserGroupsService;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.core.entity.JobGroup;
import org.springframework.stereotype.Service;

/**
 * (UserGroups)表服务实现类
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Service("userGroupsService")
@RequiredArgsConstructor
public class UserGroupsServiceImpl implements UserGroupsService {

    private final UserGroupsMapper userGroupsMapper;

    @Override
    public UserGroups queryById(Long id) {
        return userGroupsMapper.queryById(id);
    }

    @Override
    public PageInfo<UserGroups> queryByPage(UserGroups userGroups, Integer pageNo, Integer pageSize) {
        long total = userGroupsMapper.count(userGroups);
        List<UserGroups> result = userGroupsMapper.queryAllByLimit(userGroups, pageNo, pageSize);
		PageInfo<UserGroups> pageInfo = new PageInfo<>();
        return pageInfo.setRows(result)
            .setTotal(total)
            .setPageNo(pageNo)
            .setPageSize(pageSize);
    }

    @Override
    public UserGroups insert(UserGroups userGroups) {
        userGroupsMapper.insert(userGroups);
        return userGroups;
    }

    @Override
    public UserGroups update(UserGroups userGroups) {
        userGroupsMapper.update(userGroups);
        return queryById(userGroups.getId());
    }

    @Override
    public boolean deleteById(Long id) {
        return userGroupsMapper.deleteById(id) > 0;
    }

    @Override
    public PageInfo<JobGroup> queryByUserId(Long userId, Integer pageNo, Integer pageSize) {
        final MPJLambdaWrapper<UserGroups> joinWrapper = JoinWrappers.lambda(UserGroups.class)
            .selectAll(JobGroup.class)
            .leftJoin(JobGroup.class, JobGroup::getId, UserGroups::getGroupId)
            .eq(UserGroups::getUserId, userId);
        Page<JobGroup> listPage = userGroupsMapper.selectJoinPage(new Page<>(pageNo, pageSize), JobGroup.class, joinWrapper);
        PageInfo<JobGroup> pageInfo = new PageInfo<>();
        pageInfo.setCurrentPage(pageNo)
            .setPageSize(pageSize)
            .setTotal(listPage.getTotal())
            .setRows(listPage.getRecords());
        return pageInfo;
    }

    @Override
    public Boolean deleteByUserIdAndGroupId(UserGroups userGroups) {
        final LambdaQueryWrapper<UserGroups> wrapper = Wrappers.<UserGroups>lambdaQuery()
            .eq(UserGroups::getUserId, userGroups.getUserId())
            .eq(UserGroups::getGroupId, userGroups.getGroupId());
        final int delete = userGroupsMapper.delete(wrapper);
        return delete > 0;
    }
}
