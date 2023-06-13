package org.dromara.hodor.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * (Tenant)实体类
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@TableName("hodor_tenant")
public class Tenant implements Serializable {

    private static final long serialVersionUID = -86082780926539643L;

    private Long id;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 公司名称
     */
    private String corpName;

    /**
     * 联系邮箱
     */
    private String email;

    /**
     * 租户描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}

