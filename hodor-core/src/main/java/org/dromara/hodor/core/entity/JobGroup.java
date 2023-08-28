package org.dromara.hodor.core.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * (JobGroup)实体类
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@TableName("hodor_job_group")
public class JobGroup implements Serializable {

    private static final long serialVersionUID = 660916649852568693L;

    private Long id;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 执行集群
     */
    private String clusterName;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}

