package org.dromara.hodor.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (User)实体类
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@TableName("hodor_user")
public class User implements Serializable {

    private static final long serialVersionUID = 157545728486218301L;

    private Long id;
    /**
     * 用户名
     */
    @TableField("username")
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 联系邮箱
     */
    private String email;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date updatedAt;


}

