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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dromara.hodor.common.utils.StringUtils;

import static java.util.Objects.requireNonNull;

/**
 * A builder to build DAGs.
 *
 * <p>Use the {@link DagBuilder#createNode} method to create NodeBuilder instances. Call
 * methods on NodeBuilder to add dependencies among them. Call the {@link DagBuilder#build()} method
 * to build a Dag.
 */
public class DagBuilder {

  private final Dag dag;

  private final Map<String, Node> nameToNodeMap = new HashMap<>();

  // The builder can only be used to build a DAG once to prevent modifying an existing DAG after it
  // is built.
  private boolean isBuilt = false;

  /**
   * A builder for building a DAG.
   *
   * @param name name of the DAG
   */
  public DagBuilder(final String name) {
    requireNonNull(name, "The name of the DagBuilder can't be null");
    this.dag = new Dag(name);
  }

  public DagBuilder(final String groupName, final String jobName) {
    requireNonNull(groupName, "The groupName of the DagBuilder can't be null");
    requireNonNull(jobName, "The jobName of the DagBuilder can't be null");
    this.dag = new Dag(StringUtils.format("{}#{}", groupName, jobName));
  }

  /**
   * Creates a new node and adds it to the DagBuilder.
   *
   * @param name name of the node
   * @return a new node
   * @throws DagException if the name is not unique in the DAG.
   */
  public Node createNode(final String groupName, final String name, final Object rawData) {
    checkIsBuilt();

    if (this.nameToNodeMap.get(name) != null) {
      throw new DagException(String.format("Node names in %s need to be unique. The name "
          + "(%s) already exists.", this, name));
    }
    final Node node = new Node(groupName, name, rawData, this.dag);
    this.nameToNodeMap.put(name, node);

    return node;
  }

  /**
   * Throws an exception if the {@link DagBuilder#build()} method has been called.
   */
  private void checkIsBuilt() {
    if (this.isBuilt) {
      final String msg = String
          .format("The DAG (%s) is built already. Can't create new nodes.", this);
      throw new DagException(msg);
    }
  }

  /**
   * Add a parent node to a child node. All the names should have been registered with this builder
   * with the {@link DagBuilder#createNode(String, String, Object)} call.
   *
   * @param childNodeKeyName name of the child node key
   * @param parentNodeKeyName name of the parent node key
   */
  public void addParentNode(final String childNodeKeyName, final String parentNodeKeyName) {
    checkIsBuilt();

    final Node child = this.nameToNodeMap.get(childNodeKeyName);
    if (child == null) {
      throw new DagException(String.format("Unknown child node (%s). Did you create the node?",
              childNodeKeyName));
    }

    final Node parent = this.nameToNodeMap.get(parentNodeKeyName);
    if (parent == null) {
      throw new DagException(
          String.format("Unknown parent node (%s). Did you create the node?", parentNodeKeyName));
    }

    if (child.getLayer() < parent.getLayer() + 1) {
      child.setLayer(parent.getLayer() + 1);
      adjustChildNodeLayer(child);
    }

    child.addParent(parent);
  }

  /**
   * 调整子节点对应分层
   *
   * @param child 子节点
   */
  private void adjustChildNodeLayer(Node child) {
    for (Node childChild : child.getChildren()) {
      if (child.getLayer() >= childChild.getLayer()) {
        childChild.setLayer(child.getLayer() + 1);
      }
      adjustChildNodeLayer(childChild);
    }
  }


  /**
   * Builds the dag.
   *
   * <p>Once this method is called, subsequent calls via NodeBuilder to modify the nodes's
   * relationships in the dag will have no effect on the returned Dag object.
   * </p>
   *
   * @return the Dag reflecting the current state of the DagBuilder
   */
  public Dag build() {
    checkIsBuilt();
    checkCircularDependencies();
    this.dag.buildNodeLayers();
    this.isBuilt = true;
    return this.dag;
  }

  /**
   * Checks if the builder contains nodes that form a circular dependency ring.
   *
   * <p>The depth first algorithm is described in this article
   * <a href="https://en.wikipedia.org/wiki/Topological_sorting">https://en.wikipedia.org/wiki/Topological_sorting</a>
   * </p>
   *
   * @throws DagException if true
   */
  private void checkCircularDependencies() {
    class CircularDependencyChecker {

      // The nodes that need to be visited
      private final Set<Node> toVisit = new HashSet<>(DagBuilder.this.nameToNodeMap.values());

      // The nodes that have finished traversing all their parent nodes
      private final Set<Node> finished = new HashSet<>();

      // The nodes that are waiting for their parent nodes to finish visit.
      private final Set<Node> ongoing = new HashSet<>();

      // One sample of nodes that form a circular dependency
      private final List<Node> sampleCircularNodes = new ArrayList<>();

      /**
       * Checks if the builder contains nodes that form a circular dependency ring.
       *
       * @throws DagException if true
       */
      private void check() {
        while (!this.toVisit.isEmpty()) {
          final Node node = removeOneNodeFromToVisitSet();
          if (checkNode(node)) {
            final String msg = String.format("Circular dependency detected. Sample: %s",
                this.sampleCircularNodes);
            throw new DagException(msg);
          }
        }
      }

      /**
       * Removes one node from the toVisit set and returns that node.
       *
       * @return a node
       */
      private Node removeOneNodeFromToVisitSet() {
        final Iterator<Node> iterator = this.toVisit.iterator();
        final Node node = iterator.next();
        iterator.remove();
        return node;
      }

      /**
       * Checks if the node is part of a group of nodes that form a circular dependency ring.
       *
       * <p>If true, the node will be added to the sampleCircularNodes list</p>
       *
       * @param node node to check
       * @return true if it is
       */
      private boolean checkNode(final Node node) {
        if (this.finished.contains(node)) {
          return false;
        }
        if (this.ongoing.contains(node)) {
          this.sampleCircularNodes.add(node);
          return true;
        }
        this.toVisit.remove(node);
        this.ongoing.add(node);
        for (final Node parent : node.getParents()) {
          if (checkNode(parent)) {
            this.sampleCircularNodes.add(node);
            return true;
          }
        }
        this.ongoing.remove(node);
        this.finished.add(node);
        return false;
      }
    }

    final CircularDependencyChecker checker = new CircularDependencyChecker();
    checker.check();
  }

  @Override
  public String toString() {
    return String.format("DagBuilder (%s)", this.dag.getName());
  }

}
