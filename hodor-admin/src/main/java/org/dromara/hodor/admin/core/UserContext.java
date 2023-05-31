package org.dromara.hodor.admin.core;


import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.domain.User;
import org.dromara.hodor.admin.exception.ServiceException;

/**
 * 用户上下文
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class UserContext {
    
    private static final ThreadLocal<User> LOCAL_REQUEST_USER = new ThreadLocal<>();

    public static final String USER_KEY = "userInfo";


    /**
     * 将用户信息存到本地线程变量
     *
     * @param userInfo the user info
     */
    public static void setUser(User userInfo) {
        LOCAL_REQUEST_USER.set(userInfo);
    }

    /**
     * 获取当前线程的用户信息
     *
     * @return the user
     */
    public static User getUser() {
        User user = LOCAL_REQUEST_USER.get();
        if (user == null) {
            throw new ServiceException(MsgCode.USER_NOT_LOGIN);
        }
        // 避免修改当前用户信息
        return BeanUtil.toBean(user, User.class);
    }

    /**
     * 移除当前线程的用户
     */
    public static void removeUser() {
        LOCAL_REQUEST_USER.remove();
    }

}
