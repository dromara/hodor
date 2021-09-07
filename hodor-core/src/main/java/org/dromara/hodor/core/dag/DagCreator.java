package org.dromara.hodor.core.dag;

import java.util.List;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.DagBuilder;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.model.job.JobDesc;

/**
 * dag creator
 *
 * @author tomgs
 * @since 2021/9/2
 */
public class DagCreator {

    private final NodeBean flowNode;

    private final DagBuilder dagBuilder;

    public DagCreator(final NodeBean flowNode) {
        final String groupName = flowNode.getGroupName();
        final String nodeName = flowNode.getNodeName();
        this.flowNode = flowNode;
        this.dagBuilder = new DagBuilder(Node.createNodeKey(groupName, nodeName));
    }

    public Dag create() {
        createNodes();
        linkNodes();
        return this.dagBuilder.build();
    }

    private void createNodes() {
        for (final NodeBean node : this.flowNode.getNodes()) {
            createNode(node);
        }
    }

    private void createNode(final NodeBean node) {
        JobDesc jobDesc = buildJobDesc(node);
        this.dagBuilder.createNode(node.getGroupName(), node.getNodeName(), jobDesc);
    }

    private JobDesc buildJobDesc(NodeBean node) {
        JobDesc jobDesc = new JobDesc();
        jobDesc.setJobName(node.getNodeName());
        return jobDesc;
    }

    private void linkNodes() {
        for (final NodeBean node : this.flowNode.getNodes()) {
            linkNode(node);
        }
    }

    private void linkNode(final NodeBean node) {
        final String groupName = node.getGroupName();
        final String nodeName = node.getNodeName();
        final List<String> parents = node.getDependsOn();
        if (parents == null) {
            return;
        }
        for (final String parentNodeKeyName : parents) {
            this.dagBuilder.addParentNode(Node.createNodeKey(groupName, nodeName), parentNodeKeyName);
        }
    }

}
