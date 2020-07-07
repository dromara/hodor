package org.dromara.hodor.server.service;

import org.apache.commons.collections4.CollectionUtils;
import org.dromara.hodor.common.utils.CopySets;
import org.dromara.hodor.server.component.LifecycleComponent;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.exception.HodorException;
import org.dromara.hodor.common.utils.SleepUtil;
import org.dromara.hodor.core.service.JobInfoService;
import org.springframework.stereotype.Service;

/**
 * hodor service
 *
 * @author tomgs
 * @since 2020/6/29
 */
@Slf4j
@Service
public class HodorService implements LifecycleComponent {

    private final LeaderService leaderService;
    private final RegisterService registerService;
    private final JobInfoService jobInfoService;
    private final Integer leastNodeCount;
    private final Integer replicaCount;
    private final Integer scatterWidth;

    public HodorService(final LeaderService leaderService, final RegisterService registerService, final JobInfoService jobInfoService) {
        this.leaderService = leaderService;
        this.registerService = registerService;
        this.jobInfoService = jobInfoService;
        this.replicaCount = 3;
        this.leastNodeCount = 3;
        this.scatterWidth = 2;
    }

    @Override
    public void start() {
        //init data

        //select leader
        Integer currRunningNodeCount = registerService.getRunningNodeCount();
        while (currRunningNodeCount < leastNodeCount) {
            SleepUtil.sleep(1000L);
            currRunningNodeCount = registerService.getRunningNodeCount();
        }
        leaderService.electLeader(() -> {
            log.info("to be leader.");
            // after to be leader write here
            List<String> currRunningNodes = registerService.getRunningNodes();
            if (CollectionUtils.isEmpty(currRunningNodes)) {
                throw new HodorException("running node count is 0.");
            }
            List<List<String>> copySets = CopySets.buildCopySets(currRunningNodes, replicaCount, scatterWidth);
            int setsNum = Math.max(copySets.size(), currRunningNodes.size());
            // distribution copySet
            for (int i = 0; i < setsNum; i++) {
                int setsIndex = setsNum % copySets.size();
                List<String> copySet = copySets.get(setsIndex);
                registerService.createCopySet(i, copySet);
            }

            // get metadata and update
            int jobCount = jobInfoService.queryAssignableJobCount();
            int offset = (int) Math.ceil((double) jobCount / setsNum);
            List<Integer> interval = Lists.newArrayList();
            for (int i = 0; i < setsNum; i++) {
                Integer index = jobInfoService.queryJobHashIdByOffset(offset * i);
                interval.add(index);
            }

        });
        //job assign
    }

    @Override
    public void stop() {

    }

}
