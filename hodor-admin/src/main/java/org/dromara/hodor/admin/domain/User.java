package org.dromara.hodor.admin.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * User
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 7195750353498840884L;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 创建的时间
     */
    private String createdAt;

    /**
     * 最后更新时间
     */
    private String updatedAt;

    /**
     * 角色id
     */
    private Integer roleId;

    /**
     * 权限集合
     */
    private String[] permitItems;
}
