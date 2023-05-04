package org.dromara.hodor.core.entity;

import lombok.Data;

@Data
public class JobGroup {

    private Long id;

    /**
     * 任务组名称
     */
    private String groupName;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 任务组备注
     */
    private String remark;

}
