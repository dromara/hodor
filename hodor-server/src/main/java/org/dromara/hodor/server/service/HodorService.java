package org.dromara.hodor.server.service;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.hodor.common.HodorLifecycle;
import org.dromara.hodor.common.exception.HodorException;
import org.dromara.hodor.common.utils.CopySets;
import org.dromara.hodor.core.Constants.CopySetConstants;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.DataInterval;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.SchedulerManager;
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
        // waiting node ready
        registryService.waitServerStarted();
        // init data
        registryService.registrySchedulerNodeListener(new SchedulerNodeChangeListener(schedulerNodeManager, this));
        registryService.registryActuatorNodeListener(new ActuatorNodeChangeListener(actuatorNodeManager));
        registryService.registryMetadataListener(new MetadataChangeListener(this));
        registryService.registryElectLeaderListener(new LeaderElectChangeListener(this));
        //registerService.registryJobEventListener(new JobEventDispatchListener(this));

        // select leader
        electLeader();
    }

    @Override
    public void stop() throws Exception {
        registryService.stop();
        copySetManager.clearCopySet();
        schedulerNodeManager.clearNodeServer();
        actuatorNodeManager.clearActuatorNodes();
        actuatorNodeManager.stopOfflineActuatorClean();
    }

    public void electLeader() {
        leaderService.electLeader(() -> {
            actuatorNodeManager.startOfflineActuatorClean();
            this.createNewHodorMetadata();
        });
    }

    public void createNewHodorMetadata() {
        log.info("server {} to be leader.", registryService.getServerEndpoint());
        // after to be leader write here
        List<String> currRunningNodes = registryService.getRunningNodes();
        if (CollectionUtils.isEmpty(currRunningNodes)) {
            throw new HodorException("running node count is 0.");
        }
        // 至少3个节点才可以使用copy set
        List<List<String>> copySetNodes = CopySets.buildCopySets(currRunningNodes, CopySetConstants.REPLICA_COUNT, CopySetConstants.SCATTER_WIDTH);
        int setsNum = Math.max(copySetNodes.size(), currRunningNodes.size());
        // distribution copySet
        List<CopySet> copySets = Lists.newArrayList();
        for (int i = 0; i < setsNum; i++) {
            int setsIndex = i % copySetNodes.size();
            List<String> copySetNode = copySetNodes.get(setsIndex);
            CopySet copySet = new CopySet();
            // FixBug: 这个地方CopySet的数量可能会小于setsNum的大小，所以可能会重用同一个CopySet节点，所以这里的id不能使用setsIndex，而应该使用setsNum下标
            copySet.setId(i);
            copySet.setServers(copySetNode);
            copySets.add(copySet);
        }

        // get metadata and update
        int jobCount = jobInfoService.queryAssignableJobCount();
        int offset = (int) Math.ceil((double) jobCount / setsNum);
        List<Long> intervalOffsets = Lists.newArrayListWithCapacity(setsNum);
        for (int i = 0; i < setsNum; i++) {
            Long hashId = jobInfoService.queryJobHashIdByOffset(offset * i);
            // hashId == -1 -> end
            if (hashId <= -1 && i > 0) {
                Long preMin = intervalOffsets.get(i - 1);
                hashId = preMin + ((Long.MAX_VALUE - preMin) >> 1);
            }
            intervalOffsets.add(Math.max(Math.abs(hashId), 0));
        }
        // add last one interval offset
        intervalOffsets.add(Long.MAX_VALUE);
        copySetManager.clearLeaderCopySet();
        for (int i = 0; i < intervalOffsets.size() - 1; i++) {
            CopySet copySet = copySets.get(i);
            copySet.setDataInterval(DataInterval.create(intervalOffsets.get(i), intervalOffsets.get(i + 1)));
            copySet.setLeader(copySetManager.selectLeaderCopySet(copySet));
        }
        final HodorMetadata metadata = HodorMetadata.builder()
            .intervalOffsets(intervalOffsets)
            .copySets(copySets)
            .build();

        log.info("Create new metadata: {}", metadata);

        registryService.createMetadata(metadata);
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

    public boolean isMasterNode() {
        if (!leaderService.hasLeader()) {
            log.info("not exist leader node.");
            return false;
        }
        // 节点下线，由主节点通知进行CopySet的主从切换
        return leaderService.isLeader();
    }

    public void updateMetadata(HodorMetadata metadata) {
        registryService.createMetadata(metadata);
    }

}
