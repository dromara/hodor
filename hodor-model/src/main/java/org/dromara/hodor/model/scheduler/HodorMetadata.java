package org.dromara.hodor.model.scheduler;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * hodor metadata
 *
 * @author tomgs
 * @since 1.0
 */
@Getter
@Builder
@EqualsAndHashCode
public class HodorMetadata {

    private final List<Long> intervalOffsets;

    private final List<CopySet> copySets;

}
