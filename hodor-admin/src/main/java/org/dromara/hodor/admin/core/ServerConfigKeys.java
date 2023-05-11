package org.dromara.hodor.admin.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 全局配置和定义类
 */
public interface ServerConfigKeys {

    /**
     * 用户会话的键
     */
    String USER_SESSION = UserContext.USER_KEY;

    Charset CHARSET = StandardCharsets.UTF_8;

    String ADMINISTRATOR_ROLE = "超级管理员";

    int ADMINISTRATOR_ROLE_ID = 0;
}
