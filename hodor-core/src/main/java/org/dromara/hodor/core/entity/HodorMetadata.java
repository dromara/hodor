package org.dromara.hodor.core.entity;

import java.util.List;
import lombok.Data;

/**
 * @author tomgs
 * @since 2020/7/7
 */
@Data
public class HodorMetadata {

    private List<String> nodes;
    private List<Integer> interval;
    private List<CopySet> copySets;

}
