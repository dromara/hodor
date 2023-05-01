package org.dromara.hodor.server.listener;

import com.google.common.collect.Lists;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.junit.Test;

/**
 * @author tomgs
 * @since 1.0
 */
public class SchedulerNodeChangeListenerTest {

    @Test
    public void testNodeRemove() {
        CopySet copySet = new CopySet();
        copySet.setId(0);
        copySet.setLeader("123");
        copySet.setServers(Lists.newArrayList("123", "231"));

        CopySet copySet1 = new CopySet();
        copySet1.setId(1);
        copySet1.setLeader("231");
        copySet1.setServers(Lists.newArrayList("231", "321"));

        CopySet copySet2 = new CopySet();
        copySet2.setId(2);
        copySet2.setLeader("321");
        copySet2.setServers(Lists.newArrayList("321", "123"));

        HodorMetadata metadata = HodorMetadata.builder().copySets(Lists.newArrayList(copySet, copySet1, copySet2)).build();
        System.out.println(metadata);

        String nodeIp = "123";
        for (int i = 0; i < 3; i++) {
            CopySet metadataCopySet = metadata.getCopySets().get(i);
            if (copySet.getLeader().equals(nodeIp)) {
                //String newLeader = copySetManager.selectLeaderCopySet(copySet);
                String newLeader = "231";
                metadataCopySet.setLeader(newLeader);
            }
            metadataCopySet.getServers().removeIf(server -> server.equals(nodeIp));
        }
        System.out.println(metadata);
    }

}
