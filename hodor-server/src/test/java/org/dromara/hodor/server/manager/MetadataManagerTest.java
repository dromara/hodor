package org.dromara.hodor.server.manager;

import com.google.common.collect.Lists;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.junit.Test;

/**
 * @author tomgs
 * @since 1.0
 */
public class MetadataManagerTest {

    @Test
    public void testMetadataCopy() {
        CopySet copySet = new CopySet();
        copySet.setId(0);
        List<CopySet> copySets = Lists.newArrayList(copySet);
        List<Long> interval = Lists.newArrayList(0L, 1L, 2L);
        HodorMetadata metadata = HodorMetadata.builder()
            .copySets(copySets)
            .intervalOffsets(interval)
            .build();
        System.out.println(metadata);

        byte[] serialize = SerializeUtils.serialize(metadata);
        System.out.println(new String(serialize, StandardCharsets.UTF_8));
        HodorMetadata deserialize = SerializeUtils.deserialize(serialize, HodorMetadata.class);
        System.out.println(deserialize);

        //HodorMetadata hodorMetadata = BeanUtil.copyProperties(metadata, HodorMetadata.class);
        //System.out.println(hodorMetadata);
        //hodorMetadata.getIntervalOffsets().add(3L);

        //System.out.println(hodorMetadata.getCopySets() == copySets);
        //System.out.println(metadata);
        //System.out.println(hodorMetadata);
    }

}
