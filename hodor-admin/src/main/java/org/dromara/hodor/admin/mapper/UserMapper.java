package org.dromara.hodor.admin.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.hodor.admin.domain.User;

public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询用户是否存在用户
     *
     * @param userName 用户名
     * @param password 密码
     * @return 返回用户信息
     */
    User findUser(@Param("uname") String userName, @Param("pwd") String password);

    /**
     * 不包含参数中的username
     *
     * @param list
     * @return
     */
    List<String> getAllUsername(List<String> list);

    /**
     * 查询用户
     * TODO.
     *
     * @return
     */
    List<User> queryUser(@Param("username") String userName);

    /**
     * 添加用户
     * TODO.
     *
     * @param user
     */
    void saveUser(User user);

    /**
     * 更新用户
     * TODO.
     *
     * @param user
     */
    void updateUser(User user);

    /**
     * 校验用户名是否存在
     * TODO.
     *
     * @param userName
     * @return
     */
    User checkUserName(@Param("username") String userName);


}
