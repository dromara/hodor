package org.dromara.hodor.server.executor.handler;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.storage.cache.CacheSource;
import org.dromara.hodor.common.storage.cache.HodorCacheSource;
import org.dromara.hodor.core.dag.NodeBean;
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

    public DagServiceImpl(final HodorCacheSource hodorCacheSource) {
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
    public void putDagInstance(JobKey jobKey, Dag dagInstance) {
        dagCacheSource.put(jobKey, dagInstance);
    }

    @Override
    public Dag getDagInstance(JobKey jobKey) {
        return dagCacheSource.get(jobKey);
    }

    @Override
    public void putFlowNodeBean(JobKey jobKey, NodeBean nodeBean) {
        flowNodeBeanCacheSource.put(jobKey, nodeBean);
    }

    @Override
    public NodeBean getFlowNodeBean(JobKey jobKey) {
        return flowNodeBeanCacheSource.get(jobKey);
    }

    public void persistDagInstance(Dag dag) {
        putDagInstance(JobKey.of(dag.getName()), dag);
    }

}
