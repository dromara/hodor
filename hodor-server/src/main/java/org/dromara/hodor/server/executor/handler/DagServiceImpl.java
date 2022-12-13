package org.dromara.hodor.server.executor.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.TypeReference;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.compress.EncType;
import org.dromara.hodor.common.concurrent.LockUtil;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.storage.cache.CacheClient;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.common.utils.TypedMapWrapper;
import org.dromara.hodor.core.Constants.FlowNodeConstants;
import org.dromara.hodor.core.dag.DagCreator;
import org.dromara.hodor.core.dag.FlowData;
import org.dromara.hodor.core.entity.FlowJobExecDetail;
import org.dromara.hodor.core.service.FlowJobExecDetailService;
import org.dromara.hodor.core.service.FlowJobInfoService;
import org.dromara.hodor.model.job.JobKey;
import org.springframework.stereotype.Service;

/**
 * dag service implement
 *
 * @author tomgs
 * @since 2021/8/30
 */
@Slf4j
@Service
public class DagServiceImpl implements DagService {

    // dagName -> Dag instance
    private final CacheClient<JobKey, Dag> dagCacheClient;

    private final CacheClient<JobKey, FlowData> flowNodeBeanCacheClient;

    private final FlowJobInfoService flowJobInfoService;

    private final FlowJobExecDetailService flowJobExecDetailService;

    private final ReentrantLock flowNodeBeanLock = new ReentrantLock();

    private final ReentrantLock dagInstanceLock = new ReentrantLock();

    private final TypeReference<Map<String, Map<String, Object>>> flowExecDataTypeReference =
        new TypeReference<Map<String, Map<String, Object>>>() {};

    public DagServiceImpl(final HodorCacheSource hodorCacheSource,
                          final FlowJobInfoService flowJobInfoService,
                          final FlowJobExecDetailService flowJobExecDetailService) {
        Assert.notNull(hodorCacheSource, "hodorCacheSource must be not null.");
        this.flowJobInfoService = flowJobInfoService;
        this.flowJobExecDetailService = flowJobExecDetailService;
        this.dagCacheClient = hodorCacheSource.getCacheClient("dag_instance");
        this.flowNodeBeanCacheClient = hodorCacheSource.getCacheClient("flow_node");
    }

    @Override
    public void markNodeRunning(Node node) {
        log.info("node {} state READY to RUNNING", node);
        node.runIfAllowed();
        // persist dag instance
        persistDagInstance(node.getDag());
    }

    @Override
    public void markNodeSuccess(Node node) {
        log.info("Node {} state RUNNING to SUCCESS", node);
        node.markSuccess();
        // persist dag instance
        persistDagInstance(node.getDag());
    }

    @Override
    public void markNodeKilling(Node node) {
        log.info("Node {} state RUNNING to KILLING", node);
        node.kill();
        persistDagInstance(node.getDag());
    }

    @Override
    public void markNodeKilled(Node node) {
        log.info("Node {} is KILLED", node);
        node.markKilled();
        persistDagInstance(node.getDag());
    }

    @Override
    public void markNodeFailed(Node node) {
        log.info("Node {} is FAILURE", node);
        node.markFailed();
        persistDagInstance(node.getDag());
    }

    @Override
    public void markNodeCanceled(Node node) {
        log.info("Node {} is CANCELED", node);
        node.cancel();
        persistDagInstance(node.getDag());
    }

    @Override
    public void updateDagStatus(Dag dag) {
        log.info("Dag {} Status update.", dag);
        dag.updateDagStatus();
        persistDagInstance(dag);
    }

    @Override
    public void createDagInstance(JobKey jobKey, Dag dagInstance) {
        LockUtil.lockMethod(dagInstanceLock, (key, dag) -> {
            FlowJobExecDetail flowJobExecDetail = buildFlowJobExecDetail(dagInstance);
            flowJobExecDetail.setExecuteStart(new Date());
            flowJobExecDetailService.createFlowJobExecDetail(flowJobExecDetail);
            dagCacheClient.put(jobKey, dagInstance);
            return null;
        }, jobKey, dagInstance);
    }

    @Override
    public Dag getDagInstance(JobKey jobKey) {
        return LockUtil.lockMethod(dagInstanceLock, key -> {
            Dag dag = dagCacheClient.get(jobKey);
            if (dag == null) {
                dag = getRunningDagInstance(jobKey);
                dagCacheClient.put(jobKey, dag);
            }
            return dag;
        }, jobKey);
    }

    @Override
    public void putFlowData(JobKey jobKey, FlowData flowData) {
        flowNodeBeanCacheClient.put(jobKey, flowData);
    }

    @Override
    public FlowData getFlowData(JobKey jobKey) {
        return LockUtil.lockMethod(flowNodeBeanLock, key -> {
            FlowData flowData = flowNodeBeanCacheClient.get(key);
            if (flowData == null) {
                flowData = flowJobInfoService.getFlowData(jobKey);
                flowNodeBeanCacheClient.put(key, flowData);
            }
            return flowData;
        }, jobKey);
    }

    private void persistDagInstance(Dag dag) {
        LockUtil.lockMethod(dagInstanceLock, d -> {
            dagCacheClient.put(JobKey.of(d.getName()), d);
            FlowJobExecDetail flowJobExecDetail = buildFlowJobExecDetail(d);
            if (dag.getStatus().isTerminal()) {
                flowJobExecDetail.setExecuteEnd(new Date());
                // dag is finished, remove dag instance from cache
                dagCacheClient.delete(JobKey.of(d.getName()));
            }
            flowJobExecDetailService.updateFlowJobExecDetail(flowJobExecDetail);
            return null;
        }, dag);
    }

    private Dag getRunningDagInstance(JobKey jobKey) {
        FlowJobExecDetail flowJobExecDetail = flowJobExecDetailService.getRunningFlowJobExecDetail(jobKey);
        if (flowJobExecDetail == null) {
            return null;
        }
        Map<String, Map<String, Object>> runningFlowExecDataMaps = SerializeUtils.deserialize(flowJobExecDetail.getFlowExecData(), flowExecDataTypeReference.getType());

        FlowData flowData = getFlowData(jobKey);
        Assert.notNull(flowData, "not found flowData by key {}.", jobKey);
        DagCreator dagCreator = new DagCreator(flowData);
        Dag dag = dagCreator.create();

        dag.setDagId(flowJobExecDetail.getRequestId());
        dag.setStatus(flowJobExecDetail.getStatus());
        dag.setSchedulerName(flowJobExecDetail.getSchedulerName());
        List<Node> nodes = dag.getNodes();
        for (Node node : nodes) {
            JobKey nodeJobKey = JobKey.of(node.getGroupName(), node.getNodeName());
            TypedMapWrapper<String, Object> wrapper = new TypedMapWrapper<>(runningFlowExecDataMaps.get(nodeJobKey.getKeyName()));
            node.setNodeId(wrapper.getLong(FlowNodeConstants.NODE_ID));
            node.setStatus(Status.valueOf(wrapper.getString(FlowNodeConstants.NODE_STATUS)));
        }
        return dag;
    }

    private FlowJobExecDetail buildFlowJobExecDetail(Dag dagInstance) {
        Long dagId = dagInstance.getDagId();
        JobKey jobKey = JobKey.of(dagInstance.getName());
        String schedulerName = dagInstance.getSchedulerName();
        Status status = dagInstance.getStatus();
        List<Node> nodes = dagInstance.getNodes();

        Map<String, Map<String, Object>> nodeMaps = Maps.newHashMap();
        for (Node node : nodes) {
            Map<String, Object> nodeMap = Maps.newHashMap();
            Long nodeId = node.getNodeId();
            String groupName = node.getGroupName();
            String nodeName = node.getNodeName();
            Status nodeStatus = node.getStatus();
            nodeMap.put(FlowNodeConstants.NODE_ID, nodeId);
            nodeMap.put(FlowNodeConstants.GROUP_NAME, groupName);
            nodeMap.put(FlowNodeConstants.NODE_NAME, nodeName);
            nodeMap.put(FlowNodeConstants.NODE_STATUS, nodeStatus);
            nodeMaps.put(JobKey.of(groupName, nodeName).getKeyName(), nodeMap);
        }

        byte[] flowExecDataBytes = SerializeUtils.serialize(nodeMaps);

        return getFlowJobExecDetail(dagId, jobKey, schedulerName, status, flowExecDataBytes);
    }

    private FlowJobExecDetail getFlowJobExecDetail(Long dagId, JobKey jobKey, String schedulerName, Status status, byte[] flowExecDataBytes) {
        FlowJobExecDetail flowJobExecDetail = new FlowJobExecDetail();
        flowJobExecDetail.setRequestId(dagId);
        flowJobExecDetail.setGroupName(jobKey.getGroupName());
        flowJobExecDetail.setJobName(jobKey.getJobName());
        flowJobExecDetail.setSchedulerName(schedulerName);
        flowJobExecDetail.setStatus(status);
        flowJobExecDetail.setEncType(EncType.PLAIN.getType());
        flowJobExecDetail.setFlowExecData(flowExecDataBytes);
        return flowJobExecDetail;
    }

}
