package org.dromara.hodor.server.executor.handler;

import cn.hutool.core.lang.Assert;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.concurrent.LockUtil;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.storage.cache.CacheSource;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;
import org.dromara.hodor.core.dag.NodeBean;
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

    private final CacheSource<JobKey, NodeBean> flowNodeBeanCacheSource;

    private final FlowJobInfoService flowJobInfoService;

    private final FlowJobExecDetailService flowJobExecDetailService;

    private final ReentrantLock flowNodeBeanLock = new ReentrantLock();

    private final ReentrantLock dagInstanceLock = new ReentrantLock();

    public DagServiceImpl(final HodorCacheSource hodorCacheSource,
                          final FlowJobInfoService flowJobInfoService,
                          final FlowJobExecDetailService flowJobExecDetailService) {
        this.flowJobInfoService = flowJobInfoService;
        this.flowJobExecDetailService = flowJobExecDetailService;
        Assert.notNull(hodorCacheSource, "hodorCacheSource must be not null.");
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
        //dagCacheSource.put();
    }

    @Override
    public void shutdownAndAwaitTermination() throws InterruptedException {

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
            flowJobExecDetailService.createFlowJobExecDetail(dagInstance);
            return null;
        }, jobKey, dagInstance);
    }

    @Override
    public Dag getDagInstance(JobKey jobKey) {
        return LockUtil.lockMethod(dagInstanceLock, key -> {
            Dag dag = dagCacheSource.get(jobKey);
            if (dag == null) {
                dag = flowJobExecDetailService.getFlowJobExecDetail(jobKey);
                dagCacheSource.put(jobKey, dag);
            }
            return dag;
        }, jobKey);
    }

    @Override
    public void putFlowNodeBean(JobKey jobKey, NodeBean nodeBean) {
        flowNodeBeanCacheSource.put(jobKey, nodeBean);
    }

    @Override
    public NodeBean getFlowNodeBean(JobKey jobKey) {
        return LockUtil.lockMethod(flowNodeBeanLock, key -> {
            NodeBean nodeBean = flowNodeBeanCacheSource.get(key);
            if (nodeBean == null) {
                nodeBean = flowJobInfoService.getFlowJobInfo(jobKey);
                flowNodeBeanCacheSource.put(key, nodeBean);
            }
            return nodeBean;
        }, jobKey);
    }

    public void persistDagInstance(Dag dag) {
        LockUtil.lockMethod(dagInstanceLock, d -> {
            dagCacheSource.put(JobKey.of(d.getName()), d);
            flowJobExecDetailService.updateFlowJobExecDetail(d);
            return null;
        }, dag);
    }

}
