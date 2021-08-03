package org.dromara.hodor.model.scheduler;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * hodor metadata
 *
 * @author tomgs
 * @since 2020/7/7
 */
@Data
@Builder
public class HodorMetadata {

    private List<String> nodes;

    private List<Long> interval;

    private List<CopySet> copySets;

}
