package org.dromara.hodor.model.scheduler;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * hodor metadata
 *
 * @author tomgs
 * @since 2020/7/7
 */
@Getter
@Builder
public class HodorMetadata {

    private final List<Long> intervalOffsets;

    private final List<CopySet> copySets;

}
