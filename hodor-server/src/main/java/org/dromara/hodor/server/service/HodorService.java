package org.dromara.hodor.server.service;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.hodor.common.exception.HodorException;
import org.dromara.hodor.common.utils.CopySets;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.core.CopySet;
import org.dromara.hodor.core.HodorMetadata;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.core.manager.CopySetManager;
import org.dromara.hodor.core.manager.NodeServerManager;
import org.dromara.hodor.core.manager.WorkerNodeManager;
import org.dromara.hodor.core.service.JobInfoService;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.SchedulerManager;
import org.dromara.hodor.scheduler.api.common.SchedulerConfig;
import org.dromara.hodor.server.component.Constants;
import org.dromara.hodor.server.component.LifecycleComponent;
import org.dromara.hodor.server.executor.JobExecutorTypeManager;
import org.dromara.hodor.server.listener.LeaderElectChangeListener;
import org.dromara.hodor.server.listener.MetadataChangeListener;
import org.dromara.hodor.server.listener.ServerNodeChangeListener;
import org.dromara.hodor.server.listener.WorkerNodeChangeListener;
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

    private final NodeServerManager nodeServerManager;

    private final WorkerNodeManager workerNodeManager;

    private final CopySetManager copySetManager;

    private final SchedulerManager schedulerManager;

    public HodorService(final LeaderService leaderService, final RegisterService registerService, final JobInfoService jobInfoService) {
        this.leaderService = leaderService;
        this.registerService = registerService;
        this.jobInfoService = jobInfoService;
        this.nodeServerManager = NodeServerManager.getInstance();
        this.copySetManager = CopySetManager.getInstance();
        this.schedulerManager = SchedulerManager.getInstance();
        this.workerNodeManager = WorkerNodeManager.getInstance();
    }

    @Override
    public void start() {
        // TODO: 待优化
        Integer currRunningNodeCount = registerService.getRunningNodeCount();
        while (currRunningNodeCount < registerService.getLeastNodeCount()) {

            log.warn("waiting for the node to join the cluster ...");

            ThreadUtils.sleep(TimeUnit.MILLISECONDS, 1000);
            currRunningNodeCount = registerService.getRunningNodeCount();
        }

        //init data
        registerService.registryServerNodeListener(new ServerNodeChangeListener(nodeServerManager));
        registerService.registryWorkerNodeListener(new WorkerNodeChangeListener(workerNodeManager));
        registerService.registryMetadataListener(new MetadataChangeListener(this));
        registerService.registryElectLeaderListener(new LeaderElectChangeListener(this));

        //select leader
        electLeader();
    }

    @Override
    public void stop() {
        registerService.stop();
        copySetManager.clearCopySet();
        nodeServerManager.clearNodeServer();
        workerNodeManager.clearWorkerNodes();
    }

    public void electLeader() {
        leaderService.electLeader(() -> {
            log.info("{} to be leader.", registerService.getServerId());
            // after to be leader write here
            List<String> currRunningNodes = registerService.getRunningNodes();
            if (CollectionUtils.isEmpty(currRunningNodes)) {
                throw new HodorException("running node count is 0.");
            }

            // 至少3个节点才可以使用copy set
            List<List<String>> copySetNodes = CopySets.buildCopySets(currRunningNodes, Constants.REPLICA_COUNT, Constants.SCATTER_WIDTH);
            int setsNum = Math.max(copySetNodes.size(), currRunningNodes.size());
            // distribution copySet
            List<CopySet> copySets = Lists.newArrayList();
            for (int i = 0; i < setsNum; i++) {
                int setsIndex = setsNum % copySetNodes.size();
                List<String> copySetNode = copySetNodes.get(setsIndex);
                CopySet copySet = new CopySet();
                copySet.setId(setsIndex);
                copySet.setServers(copySetNode);
                copySets.add(copySet);
            }

            // get metadata and update
            int jobCount = jobInfoService.queryAssignableJobCount();
            int offset = (int) Math.ceil((double) jobCount / setsNum);
            List<Long> interval = Lists.newArrayList();
            for (int i = 0; i < setsNum; i++) {
                Long hashId = jobInfoService.queryJobHashIdByOffset(offset * i);
                interval.add(hashId);
            }
            for (int i = 0; i < interval.size(); i++) {
                CopySet copySet = copySets.get(i);
                if (i == interval.size() - 1) { // last one
                    copySet.setDataInterval(Lists.newArrayList(interval.get(i), Long.MAX_VALUE));
                } else {
                    copySet.setDataInterval(Lists.newArrayList(interval.get(i), interval.get(i + 1)));
                }
                copySet.setLeader(copySetManager.selectLeaderCopySet(copySet));
            }

            final HodorMetadata metadata = HodorMetadata.builder()
                .nodes(currRunningNodes)
                .interval(interval)
                .copySets(copySets)
                .build();
            registerService.createMetadata(metadata);
        });
    }

    public void createActiveScheduler(String serverId, List<Long> dataInterval) {
        HodorScheduler activeScheduler = buildScheduler(serverId, dataInterval);
        schedulerManager.addActiveScheduler(activeScheduler);
        schedulerManager.addSchedulerDataInterval(activeScheduler.getSchedulerName(), dataInterval);
    }

    public void createStandbyScheduler(String serverId, List<Long> standbyDataInterval) {
        HodorScheduler standbyScheduler = buildScheduler(serverId, standbyDataInterval);
        schedulerManager.addStandByScheduler(standbyScheduler);
        schedulerManager.addSchedulerDataInterval(standbyScheduler.getSchedulerName(), standbyDataInterval);
    }

    public HodorScheduler buildScheduler(String serverId, List<Long> dataInterval) {
        final SchedulerConfig config = SchedulerConfig.builder()
            .schedulerName("HodorScheduler_" + serverId)
            .threadCount(ThreadUtils.availableProcessors() * 2)
            .misfireThreshold(3000)
            .build();
        final HodorScheduler scheduler = schedulerManager.getOrCreateScheduler(config);
        final List<Long> schedulerDataInterval = schedulerManager.getSchedulerDataInterval(config.getSchedulerName());
        if (CollectionUtils.isEmpty(schedulerDataInterval) || !CollectionUtils.isEqualCollection(schedulerDataInterval, dataInterval)) {
            List<JobInfo> jobInfoList = jobInfoService.queryJobInfoByHashIdOffset(dataInterval.get(0), dataInterval.get(1));
            jobInfoList.forEach(job -> scheduler.addJob(job, JobExecutorTypeManager.getInstance().getJobExecutor(job.getJobType())));
        }
        return scheduler;
    }

    public String getServerId() {
        return registerService.getServerId();
    }

}
