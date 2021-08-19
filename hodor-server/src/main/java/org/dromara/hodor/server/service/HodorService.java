package org.dromara.hodor.server.service;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.hodor.common.exception.HodorException;
import org.dromara.hodor.common.utils.CopySets;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.server.common.Constants;
import org.dromara.hodor.server.common.HodorLifecycle;
import org.dromara.hodor.server.executor.JobExecutorTypeManager;
import org.dromara.hodor.server.listener.ActuatorNodeChangeListener;
import org.dromara.hodor.server.listener.LeaderElectChangeListener;
import org.dromara.hodor.server.listener.MetadataChangeListener;
import org.dromara.hodor.server.listener.SchedulerNodeChangeListener;
import org.dromara.hodor.server.manager.ActuatorNodeManager;
import org.dromara.hodor.server.manager.CopySetManager;
import org.dromara.hodor.server.manager.SchedulerNodeManager;
import org.springframework.stereotype.Service;

/**
 * hodor service
 *
 * @author tomgs
 * @since 2020/6/29
 */
@Slf4j
@Service
public class HodorService implements HodorLifecycle {

    private final LeaderService leaderService;

    private final RegistryService registryService;

    private final JobInfoService jobInfoService;

    private final SchedulerNodeManager schedulerNodeManager;

    private final ActuatorNodeManager actuatorNodeManager;

    private final CopySetManager copySetManager;

    private final SchedulerManager schedulerManager;

    public HodorService(final LeaderService leaderService, final RegistryService registryService, final JobInfoService jobInfoService) {
        this.leaderService = leaderService;
        this.registryService = registryService;
        this.jobInfoService = jobInfoService;
        this.schedulerNodeManager = SchedulerNodeManager.getInstance();
        this.copySetManager = CopySetManager.getInstance();
        this.schedulerManager = SchedulerManager.getInstance();
        this.actuatorNodeManager = ActuatorNodeManager.getInstance();
    }

    @Override
    public void start() {
        // TODO: 待优化
        Integer currRunningNodeCount = registryService.getRunningNodeCount();
        while (currRunningNodeCount < registryService.getLeastNodeCount()) {

            log.warn("waiting for the node to join the cluster ...");

            ThreadUtils.sleep(TimeUnit.MILLISECONDS, 1000);
            currRunningNodeCount = registryService.getRunningNodeCount();
        }

        //init data
        registryService.registrySchedulerNodeListener(new SchedulerNodeChangeListener(schedulerNodeManager, leaderService));
        registryService.registryActuatorNodeListener(new ActuatorNodeChangeListener(actuatorNodeManager, registryService));
        registryService.registryMetadataListener(new MetadataChangeListener(this));
        registryService.registryElectLeaderListener(new LeaderElectChangeListener(this));
        //registerService.registryJobEventListener(new JobEventDispatchListener(this));

        //select leader
        electLeader();
    }

    @Override
    public void stop() {
        registryService.stop();
        copySetManager.clearCopySet();
        schedulerNodeManager.clearNodeServer();
        actuatorNodeManager.clearActuatorNodes();
        actuatorNodeManager.stopOfflineActuatorClean();
    }

    public void electLeader() {
        leaderService.electLeader(() -> {
            log.info("{} to be leader.", registryService.getServerEndpoint());
            actuatorNodeManager.startOfflineActuatorClean();
            // after to be leader write here
            List<String> currRunningNodes = registryService.getRunningNodes();
            if (CollectionUtils.isEmpty(currRunningNodes)) {
                throw new HodorException("running node count is 0.");
            }
            // 至少3个节点才可以使用copy set
            List<List<String>> copySetNodes = CopySets.buildCopySets(currRunningNodes, Constants.REPLICA_COUNT, Constants.SCATTER_WIDTH);
            int setsNum = Math.max(copySetNodes.size(), currRunningNodes.size());
            // distribution copySet
            List<CopySet> copySets = Lists.newArrayList();
            for (int i = 0; i < setsNum; i++) {
                int setsIndex = i % copySetNodes.size();
                List<String> copySetNode = copySetNodes.get(setsIndex);
                CopySet copySet = new CopySet();
                copySet.setId(setsIndex);
                copySet.setServers(copySetNode);
                copySets.add(copySet);
            }

            // get metadata and update
            int jobCount = jobInfoService.queryAssignableJobCount();
            int offset = (int) Math.ceil((double) jobCount / setsNum);
            List<Long> intervalOffsets = Lists.newArrayListWithCapacity(setsNum);
            for (int i = 0; i < setsNum; i++) {
                Long hashId = jobInfoService.queryJobHashIdByOffset(offset * i);
                if (hashId < 0 && i > 0) {
                    Long preMin = intervalOffsets.get(i - 1);
                    hashId = preMin + ((Long.MAX_VALUE - preMin) >> 1);
                }
                intervalOffsets.add(Math.max(Math.abs(hashId), 0));
            }
            // add last one interval offset
            intervalOffsets.add(Long.MAX_VALUE);
            for (int i = 0; i < intervalOffsets.size() - 1; i++) {
                CopySet copySet = copySets.get(i);
                copySet.setDataInterval(DataInterval.create(intervalOffsets.get(i), intervalOffsets.get(i + 1)));
                copySet.setLeader(copySetManager.selectLeaderCopySet(copySet));
            }

            final HodorMetadata metadata = HodorMetadata.builder()
                .nodes(currRunningNodes)
                .intervalOffsets(intervalOffsets)
                .copySets(copySets)
                .build();

            log.info("HodorMetadata: {}", metadata);

            registryService.createMetadata(metadata);
        });
    }

    public void addRunningJob(final HodorScheduler scheduler, DataInterval dataInterval) {
        final DataInterval schedulerDataInterval = schedulerManager.getSchedulerDataInterval(scheduler.getSchedulerName());
        if (dataInterval.equals(schedulerDataInterval)) { // 如果区间不相等表示数据有交叉，会产生重复
            List<JobInfo> jobInfoList = jobInfoService.queryRunningJobInfoByDataInterval(dataInterval);
            jobInfoList.forEach(job -> scheduler.addJob(job, JobExecutorTypeManager.getInstance().getJobExecutor(job.getJobType())));
        }
    }

    public String getServerEndpoint() {
        return registryService.getServerEndpoint();
    }

}
