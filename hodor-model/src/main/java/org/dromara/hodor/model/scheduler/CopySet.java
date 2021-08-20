package org.dromara.hodor.model.scheduler;

import java.util.List;
import lombok.Data;

/**
 * copy set
 *
 * @author tomgs
 * @since 2020/7/8
 */
@Data
public class CopySet {
    /**
     * copy set id
     */
    private Integer id;

    /**
     * copy set 数据区间
     */
    private DataInterval dataInterval;

    /**
     * copy set 主节点
     */
    private String leader;

    /**
     * copy set 节点列表
     */
    private List<String> servers;

}
