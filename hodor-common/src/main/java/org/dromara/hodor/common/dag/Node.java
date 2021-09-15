/*
 * Copyright 2018 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.dromara.hodor.common.dag;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import org.dromara.hodor.common.IdGenerator;
import org.dromara.hodor.common.utils.StringUtils;

import static java.util.Objects.requireNonNull;

/**
 * Node in a DAG: Directed acyclic graph.
 */
public class Node {

    private static final String NODE_KEY_FORMAT = "{}#{}";

    private final Long id;

    private final String groupName;

    private final String nodeName;

    private final Object rawData;

    // The nodes that this node depends on.
    private final List<Node> parents = new ArrayList<>();

    // The nodes that depend on this node.
    private final List<Node> children = new ArrayList<>();

    private Status status = Status.READY;

    private final Dag dag;

    private int layer = 0;

    public Node(final String groupName, final String nodeName, Object rawData, final Dag dag) {
        requireNonNull(groupName, "The groupName of the node can't be null");
        this.groupName = groupName;
        requireNonNull(nodeName, "The nodeName of the node can't be null");
        this.nodeName = nodeName;
        requireNonNull(dag, "The dag of the node can't be null");
        this.dag = dag;
        requireNonNull(rawData, "The rawData of the node can't be null");
        this.rawData = rawData;
        this.id = IdGenerator.defaultGenerator().nextId();
        dag.addNode(this);
    }

    public static String createNodeKey(String groupName, String nodeName) {
        return StringUtils.format(NODE_KEY_FORMAT, groupName, nodeName);
    }

    public Dag getDag() {
        return this.dag;
    }

    public Long getNodeId() {
        return this.id;
    }

    public Object getRawData() {
        return rawData;
    }

    /**
     * Adds the node as the current node's parent i.e. the current node depends on the given node.
     *
     * <p>It's important NOT to expose this method as public. The design relies on this to ensure
     * correctness. The DAG's structure shouldn't change after it is created.
     */
    void addParent(final Node node) {
        this.parents.add(node);
        node.addChild(this);
    }

    private void addChild(final Node node) {
        this.children.add(node);
    }

    boolean hasParent() {
        return !this.parents.isEmpty();
    }

    /**
     * Checks if the node is ready to run.
     *
     * @return true if the node is ready to run
     */
    public boolean isReady() {
        if (this.status != Status.READY) {
            // e.g. if the node is disabled, it is not ready to run.
            return false;
        }
        for (final Node parent : this.parents) {
            if (!parent.status.isSuccessEffectively()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Transitions the node to the success state.
     */
    public void markSuccess() {
        // It's possible that the dag is killed before this method is called.
        assertRunningOrKilling();
        changeStatus(Status.SUCCESS);
        NodeLayer layer = this.dag.getLayer(this.getLayer());
        if (layer.getRunningNodes() == 1) {
            layer.setStatus(Status.SUCCESS);
        }
        layer.setRunningNodes(layer.getRunningNodes() - 1);
    }

    /**
     * Checks if all the dependencies are met and run if they are.
     */
    public void runIfAllowed() {
        if (isReady()) {
            changeStatus(Status.RUNNING);
        }
    }

    /**
     * Transitions the node to the failure state.
     */
    public void markFailed() {
        // It's possible that the dag is killed before this method is called.
        assertRunningOrKilling();
        changeStatus(Status.FAILURE);
        for (final Node child : this.children) {
            child.cancel();
        }
        this.dag.updateDagStatus();
    }

    public void cancel() {
        // The node shouldn't have started.
        assert (this.status.isPreRunState());
        if (this.status != Status.DISABLED) {
            changeStatus(Status.CANCELED);
        }
        for (final Node node : this.children) {
            node.cancel();
        }
    }

    /**
     * Asserts that the state is running or killing.
     */
    private void assertRunningOrKilling() {
        assert (this.status == Status.RUNNING || this.status == Status.KILLING);
    }

    public void changeStatus(final Status status) {
        this.status = status;
    }

    /**
     * Kills a node.
     *
     * <p>A node is not designed to be killed individually. This method expects {@link Dag#kill()}
     * method to kill all nodes. Thus this method itself doesn't need to propagate the kill signal to
     * the node's children nodes.
     */
    public void kill() {
        assert (this.dag.getStatus() == Status.KILLING);
        if (this.status == Status.READY || this.status == Status.BLOCKED) {
            // If the node is disabled, keep the status as disabled.
            changeStatus(Status.CANCELED);
        } else if (this.status == Status.RUNNING) {
            changeStatus(Status.KILLING);
        }
        // If the node has finished, leave the status intact.
    }

    /**
     * Transition the node from the killing state to the killed state.
     */
    public void markKilled() {
        assert (this.status == Status.KILLING);
        changeStatus(Status.KILLED);
        this.dag.updateDagStatus();
    }

    @Override
    public String toString() {
        return String.format("Node (%s_%s) id (%s) status (%s) layer (%s) in [%s]", this.groupName, this.nodeName, this.id, this.status, this.layer, this.dag);
    }

    public Status getStatus() {
        return this.status;
    }

    @VisibleForTesting
    public void setStatus(final Status status) {
        this.status = status;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeKeyName() {
        return StringUtils.format(NODE_KEY_FORMAT, groupName, nodeName);
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public NodeLayer getCurrentNodeLayer() {
        return this.dag.getLayer(layer);
    }

    @VisibleForTesting
    public List<Node> getChildren() {
        return this.children;
    }

    @VisibleForTesting
    public List<Node> getParents() {
        return this.parents;
    }

    public NodeLayer getNodeLayer() {
        return this.getDag().getLayer(this.layer);
    }

}
