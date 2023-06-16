package org.dromara.hodor.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * (UserGroups)实体类
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@TableName("hodor_user_groups")
public class UserGroups implements Serializable {

    private static final long serialVersionUID = 834864089345973312L;

        private Long id;
        /**
         * 用户id
         */
        private Long userId;
        /**
         * 租户id
         */
        private Long groupId;

}

