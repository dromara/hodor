package org.dromara.hodor.core;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * @author tomgs
 * @since 2020/7/7
 */
@Data
@Builder
public class HodorMetadata {

    private List<String> nodes;
    private List<Integer> interval;
    private List<CopySet> copySets;

}
