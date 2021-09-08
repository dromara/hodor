package org.dromara.hodor.server.executor.handler;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.DagService;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.NodeLayer;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.response.KillRunningJobResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.JobDispatcher;
import org.dromara.hodor.server.manager.JobExecuteManager;
import org.springframework.stereotype.Service;

/**
 * dag service implement
 *
 * @author tomgs
 * @since 2021/8/30
 */
@Slf4j
@Service
public class DagServiceHandler implements DagService {

    private final JobDispatcher jobDispatcher;

    public DagServiceHandler() {
        this.jobDispatcher = new JobDispatcher(new HodorFlowJobRequestHandler());
    }

    @Override
    public void markNodeRunning(Node node) {
        log.info("node {} state READY to RUNNING", node);
        node.changeStatus(Status.RUNNING);
        HodorJobExecutionContext hodorJobExecutionContext = getHodorJobExecutionContext(node);
        jobDispatcher.dispatch(hodorJobExecutionContext);
    }

    @Override
    public void markNodeSuccess(Node node) {
        node.markSuccess();
        log.info("Node {} state RUNNING to SUCCESS", node);
        // persist dag instance
    }

    @Override
    public void markNodeKilling(Node node) {
        String groupName = node.getGroupName();
        String nodeName = node.getNodeName();
        // TODO: 实现任务的kill
        JobExecDetail jobExecDetail = JobExecuteManager.getInstance().queryJobExecDetail(JobKey.of(groupName, nodeName));
        if (!node.getNodeId().equals(jobExecDetail.getId())) {
            throw new IllegalArgumentException("job running");
        }
        if (JobExecuteStatus.isRunning(jobExecDetail.getExecuteStatus())) {
            KillRunningJobResponse killRunningJobResponse = JobExecuteManager.getInstance().killRunningJob(jobExecDetail);
        }

    }

    @Override
    public void markNodeKilled(Node node) {
        node.markKilled();
        log.info("Node {} is KILLED", node);
    }

    @Override
    public void markNodeFailed(Node node) {
        node.markFailed();
        log.info("Node {} is FAILURE", node);
    }

    @Override
    public void killDag(Dag dag) {
        for (NodeLayer nodeLayer : dag.getNodeLayers()) {
            if (nodeLayer.getStatus().isRunning()) {
                if (nodeLayer.getRunningNodes() == 0) {
                    dag.setStatus(Status.KILLED);
                }
                for (Node node : nodeLayer.getNodes()) {
                    node.kill();
                }
            }
            if (nodeLayer.getStatus().isPreRunState()) {
                for (Node node : nodeLayer.getNodes()) {
                    node.cancel();
                }
            }
        }
    }

    @Override
    public void shutdownAndAwaitTermination() throws InterruptedException {

    }

    private HodorJobExecutionContext getHodorJobExecutionContext(Node node) {
        return new HodorJobExecutionContext(node.getNodeId(), (JobDesc) node.getRawData(),
            node.getDag().getSchedulerName(), new Date());
    }

}
