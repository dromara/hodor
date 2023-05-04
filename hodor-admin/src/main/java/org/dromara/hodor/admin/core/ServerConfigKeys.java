package org.dromara.hodor.admin.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 全局配置和定义类
 */
public interface ServerConfigKeys {

    /**
     * 系统用户默认密码
     */
    String DEFAULT_USER_PASSWORD = "123456";

    /**
     * 用户会话的键
     */
    String USER_SESSION = "currentUser";

    /**
     * 超级管理员角色的ID
     */
    String SUPER_ROLE_ID = "root";

    String USER_CARD_PASSWORD = "123456";

    Charset CHARSET = StandardCharsets.UTF_8;

    String ADMINISTRATOR_ROLE = "超级管理员";

    int ADMINISTRATOR_ROLE_ID = 0;
}
