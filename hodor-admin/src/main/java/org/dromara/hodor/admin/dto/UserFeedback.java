package org.dromara.hodor.admin.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户信息反馈
 *
 * @author tomgs
 * @since 1.0
 **/
@Data
@Accessors(chain = true)
public class UserFeedback {

    private int id;

    private String userName;

    private String content;

    private String createTime;
}
