package org.dromara.hodor.server.service;

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

    public HodorService(final LeaderService leaderService, final RegisterService registerService, final JobInfoService jobInfoService) {
        this.leaderService = leaderService;
        this.registerService = registerService;
        this.jobInfoService = jobInfoService;
        this.leastNodeCount = 3;
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
            // after to be leader write here
            System.out.println("---------");
            // get metadata and update
            Integer runningNodeCount = registerService.getRunningNodeCount();
            Integer jobCount = jobInfoService.queryAssignableJobCount();
            if (runningNodeCount <= 0) {
                throw new HodorException("running node count is 0.");
            }
            int offset = (int) Math.ceil((double) jobCount / runningNodeCount);
            List<Integer> interval = Lists.newArrayList();
            for (int i = 0; i < runningNodeCount; i++) {
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
