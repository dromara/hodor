package org.dromara.hodor.common.dag;

import java.util.List;

/**
 * dag creator
 *
 * @author tomgs
 * @since 2021/9/2
 */
public class DagCreator {

    private final NodeBean flowNode;

    private final NodeProcessor nodeProcessor;

    private final DagBuilder dagBuilder;

    public DagCreator(final NodeBean flowNode, final DagProcessor dagProcessor, final NodeProcessor nodeProcessor) {
        final String flowName = flowNode.getName();
        this.flowNode = flowNode;
        this.nodeProcessor = nodeProcessor;
        this.dagBuilder = new DagBuilder(flowName, dagProcessor);
    }

    public Dag create() {
        createNodes();
        linkNodes();
        return this.dagBuilder.build();
    }

    private void createNodes() {
        for (final NodeBean node : this.flowNode.getNodes()) {
            createNode(node, nodeProcessor);
        }
    }

    private void createNode(final NodeBean node, final NodeProcessor nodeProcessor) {
        final String nodeName = node.getName();
        this.dagBuilder.createNode(nodeName, nodeProcessor);
    }

    private void linkNodes() {
        for (final NodeBean node : this.flowNode.getNodes()) {
            linkNode(node);
        }
    }

    private void linkNode(final NodeBean node) {
        final String name = node.getName();
        final List<String> parents = node.getDependsOn();
        if (parents == null) {
            return;
        }
        for (final String parentNodeName : parents) {
            this.dagBuilder.addParentNode(name, parentNodeName);
        }
    }

}
