package org.dromara.hodor.admin.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.admin.domain.UserGroups;
import org.dromara.hodor.admin.mapper.UserGroupsMapper;
import org.dromara.hodor.admin.service.UserGroupsService;
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
}
