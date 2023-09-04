package org.dromara.hodor.model.actuator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author superlit
 * @create 2023/8/31 14:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobTypeInfo {
    /**
     * 执行集群名
     */
    private String clusterName;

    /**
     * 任务类型名称
     */
    private List<String> jobTypeNames;
}
