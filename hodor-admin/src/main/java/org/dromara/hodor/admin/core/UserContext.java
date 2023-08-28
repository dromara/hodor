package org.dromara.hodor.admin.core;


import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.admin.dto.user.UserInfo;
import org.dromara.hodor.admin.exception.ServiceException;
import org.dromara.hodor.common.utils.Utils;

/**
 * 用户上下文
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class UserContext {

    private static final ThreadLocal<UserInfo> LOCAL_REQUEST_USER = new ThreadLocal<>();

    public static final String USER_KEY = "userInfo";


    /**
     * 将用户信息存到本地线程变量
     *
     * @param userInfo the user info
     */
    public static void setUser(UserInfo userInfo) {
        LOCAL_REQUEST_USER.set(userInfo);
    }

    /**
     * 获取当前线程的用户信息
     *
     * @return the user
     */
    public static UserInfo getUser() {
        UserInfo user = LOCAL_REQUEST_USER.get();
        if (user == null) {
            throw new ServiceException(MsgCode.USER_NOT_LOGIN);
        }
        // 避免修改当前用户信息
        return Utils.Beans.toBean(user, UserInfo.class);
    }

    /**
     * 移除当前线程的用户
     */
    public static void removeUser() {
        LOCAL_REQUEST_USER.remove();
    }

}
