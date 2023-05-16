package org.dromara.hodor.admin.domain;

import lombok.Builder;
import lombok.Data;

/**
 * 用户信息反馈
 *
 * @author tomgs
 * @since 1.0
 **/
@Data
@Builder
public class UserFeedback {

    private int id;

    private String userName;

    private String content;

    private String createTime;
}
