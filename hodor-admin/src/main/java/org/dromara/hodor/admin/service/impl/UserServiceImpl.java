package org.dromara.hodor.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.core.PageInfo;
import org.dromara.hodor.admin.entity.User;
import org.dromara.hodor.admin.mapper.UserMapper;
import org.dromara.hodor.admin.service.UserService;
import org.springframework.stereotype.Service;

/**
 * (User)表服务实现类
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User queryById(Long id) {
        return userMapper.queryById(id);
    }

    @Override
    public PageInfo<User> queryByPage(User user, Integer pageNo, Integer pageSize) {
        long total = userMapper.count(user);
        int current = (pageNo - 1) * pageSize;
        List<User> result = userMapper.queryAllByLimit(user, current, pageSize);
        PageInfo<User> pageInfo = new PageInfo<>();
        return pageInfo.setRows(result)
            .setTotal(total)
            .setPageNo(pageNo)
            .setPageSize(pageSize);
    }

    @Override
    public User insert(User user) {
        user.setTenantId(0L);
        userMapper.insert(user);
        return user;
    }

    @Override
    public User update(User user) {
        user.setUpdatedAt(Utils.Dates.date());
        userMapper.updateById(user);
        return queryById(user.getId());
    }

    @Override
    public boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public User findUser(String username, String password) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        return userMapper.selectOne(Wrappers.<User>lambdaQuery()
            .eq(User::getUsername, username)
            .eq(User::getPassword, password));
    }

    @Override
    public User findUser(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        return userMapper.selectOne(Wrappers.<User>lambdaQuery()
            .eq(User::getUsername, username));
    }
}
