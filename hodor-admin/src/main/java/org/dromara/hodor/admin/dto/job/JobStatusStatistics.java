package org.dromara.hodor.admin.dto.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tomgs
 * @since 1.0
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobStatusStatistics {

    /**
     * 任务类型 0:未激活，1/2:正在运行，3:暂停
     */
    private int status;

    /**
     * 任务数量
     */
    private int nums;
}
