package org.dromara.hodor.admin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.hodor.admin.core.PageInfo;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.domain.UserFeedback;
import org.dromara.hodor.admin.mapper.UserMapper;
import org.dromara.hodor.admin.mapper.UserRecommendMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户信息服务
 */
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserMapper mapper;

    private final UserRecommendMapper userRecommendMapper;

    public User findUser(String userName, String password) {
        if (StringUtils.isEmpty(userName)) {
            return null;
        }
        return mapper.findUser(userName, password);// 查找
    }

    public List<String> getAllUsername() {
        List<String> list = Lists.newArrayList("admin", "visitor");
        return mapper.getAllUsername(list);

    }

    /**
     * 查询用户
     *
     * @param userName
     * @param pageNum
     * @param pageSize
     * @return
     */

    public PageInfo<User> queryUser(String userName, int pageNum, int pageSize) {
        PageInfo<User> pageInfo = new PageInfo<>();
        Page<User> page = PageHelper.startPage(pageNum, pageSize);
        mapper.queryUser(userName);
        List<User> data = page.getResult();
        pageInfo.setTotalList(data);
        pageInfo.setTotalPage((int) page.getTotal());
        //return new PageResult().setRows(data).setTotal(page.getTotal());
        return pageInfo;
    }

    public List<User> findUser(String userName) {
        if (StringUtils.isBlank(userName)) {
            return null;
        }
        return mapper.queryUser(userName);
    }

    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    public boolean saveUser(User user) {
        try {
            mapper.saveUser(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    public boolean updateUser(User user) {
        try {
            mapper.updateUser(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 校验用户名是否存在
     *
     * @param userName
     * @return
     */
    public boolean checkUserName(String userName) {
        User user;
        try {
            user = mapper.checkUserName(userName);
        } catch (Exception e) {
            return false;
        }
        return user != null;
    }

    public boolean addRecommend(String userName, String content) {
        UserFeedback recommend = UserFeedback.builder().userName(userName).content(content).build();
        try {
            userRecommendMapper.insert(recommend);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<UserFeedback> listRecommend(String userName, int pageOffset, int pageSize) {
        return userRecommendMapper.selectByUserName(userName, pageOffset, pageSize);
    }

    public long getRecommendSizeByUserName(String userName) {
        long size = userRecommendMapper.getSize(userName);
        return size;
    }

    public boolean deleteRecommend(int id, String userName) {
        userRecommendMapper.delete(id, userName);
        return true;
    }

    /**
     * 根据用户名查询用户信息.
     *
     * @param userName
     * @return
     */

    public User getUserByName(String userName) {
        User user = mapper.checkUserName(userName);
        return user;
    }
}
