package org.dromara.hodor.model.scheduler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 数据区间</br>
 *
 * 左闭右开区间
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataInterval {

    /**
     * 起始区间
     */
    private Long startInterval;

    /**
     * 终止区间
     */
    private Long endInterval;

    public static DataInterval create(Long startInterval, Long endInterval) {
        return new DataInterval(startInterval, endInterval);
    }

    /**
     * 判断元素是否包含在区间内
     *
     * @param element 当前元素
     * @return true: 包含，false：不包含
     */
    public boolean containsInterval(Long element) {
        return startInterval <= element && endInterval > element;
    }

}
