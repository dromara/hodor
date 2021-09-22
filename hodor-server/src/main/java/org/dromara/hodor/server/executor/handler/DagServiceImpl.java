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
import org.dromara.hodor.common.storage.cache.CacheSource;
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
    private final CacheSource<JobKey, Dag> dagCacheSource;

    private final CacheSource<JobKey, FlowData> flowNodeBeanCacheSource;

    private final FlowJobInfoService flowJobInfoService;

    private final FlowJobExecDetailService flowJobExecDetailService;

    private final ReentrantLock flowNodeBeanLock = new ReentrantLock();

    private final ReentrantLock dagInstanceLock = new ReentrantLock();

    private final TypeReference<Map<JobKey, Map<String, Object>>> flowExecDataTypeReference =
        new TypeReference<Map<JobKey, Map<String, Object>>>() {};

    public DagServiceImpl(final HodorCacheSource hodorCacheSource,
                          final FlowJobInfoService flowJobInfoService,
                          final FlowJobExecDetailService flowJobExecDetailService) {
        Assert.notNull(hodorCacheSource, "hodorCacheSource must be not null.");
        this.flowJobInfoService = flowJobInfoService;
        this.flowJobExecDetailService = flowJobExecDetailService;
        this.dagCacheSource = hodorCacheSource.getCacheSource("dag_instance");
        this.flowNodeBeanCacheSource = hodorCacheSource.getCacheSource("flow_node");
    }

    @Override
    public void markNodeRunning(Node node) {
        log.info("node {} state READY to RUNNING", node);
        node.changeStatus(Status.RUNNING);
        // persist dag instance
        persistDagInstance(node.getDag());
    }

    @Override
    public void markNodeSuccess(Node node) {
        node.markSuccess();
        log.info("Node {} state RUNNING to SUCCESS", node);
        // persist dag instance
        persistDagInstance(node.getDag());
    }

    @Override
    public void markNodeKilling(Node node) {
        node.kill();
        node.changeStatus(Status.KILLING);
        persistDagInstance(node.getDag());
    }

    @Override
    public void markNodeKilled(Node node) {
        node.markKilled();
        log.info("Node {} is KILLED", node);
        persistDagInstance(node.getDag());
    }

    @Override
    public void markNodeFailed(Node node) {
        node.markFailed();
        log.info("Node {} is FAILURE", node);
        persistDagInstance(node.getDag());
    }

    @Override
    public void updateDagStatus(Dag dag) {
        log.info("Dag {} Status update.", dag);
        dag.updateDagStatus();
        persistDagInstance(dag);
    }

    @Override
    public void shutdownAndAwaitTermination() {

    }

    @Override
    public void markNodeCanceled(Node node) {
        node.cancel();
        // persist dag instance
        persistDagInstance(node.getDag());
    }

    @Override
    public void createDagInstance(JobKey jobKey, Dag dagInstance) {
        LockUtil.lockMethod(dagInstanceLock, (key, dag) -> {
            dagCacheSource.put(jobKey, dagInstance);
            FlowJobExecDetail flowJobExecDetail = buildFlowJobExecDetail(dagInstance);
            flowJobExecDetail.setExecuteStart(new Date());
            flowJobExecDetailService.createFlowJobExecDetail(flowJobExecDetail);
            return null;
        }, jobKey, dagInstance);
    }

    @Override
    public Dag getDagInstance(JobKey jobKey) {
        return LockUtil.lockMethod(dagInstanceLock, key -> {
            Dag dag = dagCacheSource.get(jobKey);
            if (dag == null) {
                dag = getRunningDagInstance(jobKey);
                dagCacheSource.put(jobKey, dag);
            }
            return dag;
        }, jobKey);
    }

    @Override
    public void putFlowData(JobKey jobKey, FlowData flowData) {
        flowNodeBeanCacheSource.put(jobKey, flowData);
    }

    @Override
    public FlowData getFlowData(JobKey jobKey) {
        return LockUtil.lockMethod(flowNodeBeanLock, key -> {
            FlowData flowData = flowNodeBeanCacheSource.get(key);
            if (flowData == null) {
                flowData = flowJobInfoService.getFlowData(jobKey);
                flowNodeBeanCacheSource.put(key, flowData);
            }
            return flowData;
        }, jobKey);
    }

    private void persistDagInstance(Dag dag) {
        LockUtil.lockMethod(dagInstanceLock, d -> {
            dagCacheSource.put(JobKey.of(d.getName()), d);
            FlowJobExecDetail flowJobExecDetail = buildFlowJobExecDetail(d);
            if (dag.getStatus().isTerminal()) {
                flowJobExecDetail.setExecuteEnd(new Date());
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

        FlowData flowData = getFlowData(jobKey);
        Assert.notNull(flowData, "not found flowData by key {}.", jobKey);
        DagCreator dagCreator = new DagCreator(flowData);
        Dag dag = dagCreator.create();
        Map<JobKey, Map<String, Object>> nodeMaps = SerializeUtils.deserialize(flowJobExecDetail.getFlowExecData(), flowExecDataTypeReference.getType());

        dag.setDagId(flowJobExecDetail.getRequestId());
        dag.setStatus(flowJobExecDetail.getStatus());
        dag.setSchedulerName(flowJobExecDetail.getSchedulerName());
        List<Node> nodes = dag.getNodes();
        for (Node node : nodes) {
            TypedMapWrapper<String, Object> wrapper = new TypedMapWrapper<>(nodeMaps.get(JobKey.of(node.getGroupName(), node.getNodeName())));
            node.setNodeId(wrapper.getLong(FlowNodeConstants.NODE_ID));
            node.setStatus(Status.valueOf(wrapper.getString(FlowNodeConstants.NODE_STATUS)));
            node.setRawData(wrapper.getObject(FlowNodeConstants.RAW_DATA));
        }
        return dag;
    }

    private FlowJobExecDetail buildFlowJobExecDetail(Dag dagInstance) {
        Long dagId = dagInstance.getDagId();
        JobKey jobKey = JobKey.of(dagInstance.getName());
        String schedulerName = dagInstance.getSchedulerName();
        Status status = dagInstance.getStatus();
        List<Node> nodes = dagInstance.getNodes();

        Map<JobKey, Map<String, Object>> nodeMaps = Maps.newHashMap();
        for (Node node : nodes) {
            Map<String, Object> nodeMap = Maps.newHashMap();
            Long nodeId = node.getNodeId();
            String groupName = node.getGroupName();
            String nodeName = node.getNodeName();
            Status nodeStatus = node.getStatus();
            Object rawData = node.getRawData();
            nodeMap.put(FlowNodeConstants.NODE_ID, nodeId);
            nodeMap.put(FlowNodeConstants.GROUP_NAME, groupName);
            nodeMap.put(FlowNodeConstants.NODE_NAME, nodeName);
            nodeMap.put(FlowNodeConstants.NODE_STATUS, nodeStatus);
            nodeMap.put(FlowNodeConstants.RAW_DATA, rawData);
            nodeMaps.put(JobKey.of(groupName, nodeName), nodeMap);
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
