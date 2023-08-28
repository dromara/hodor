package org.dromara.hodor.admin.dto.job;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import org.dromara.hodor.admin.enums.WorkNodeStatus;

@Data
public class WorkNode implements Serializable {

    private static final long serialVersionUID = -7650183109401282102L;

    /**
     * 当前执行结果  0:未完成 2：执行成功 3: 执行完成
     */
    private WorkNodeStatus currentStatus;

    /**
     * 任务标识  groupName#jobName
     */
    private String nodeName;

    /**
     * 前置任务列表
     */
    private List<String> preNodes;

    /**
     * 后置任务
     */
    private List<String> postNodes;

    /**
     * 是否可执行，如果是false,则跳过不执行
     */
    private Boolean enabled;
}
