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

package org.dromara.hodor.server.executor.handler;

import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.core.dag.NodeBean;
import org.dromara.hodor.model.job.JobKey;

/**
 * Thread safe and non blocking service for DAG processing.
 *
 * <p>Allow external inputs to be given to a dag or node to allow the dag to transition states
 * . Since only one thread is used to progress the DAG, thread synchronization is avoided.
 */
public interface DagService {

  /**
   * Transitions the node to the running state.
   */
  void markNodeRunning(Node node);

  /**
   * Transitions the node to the success state.
   */
  void markNodeSuccess(final Node node);

  /**
   * Transitions the node from the running state to the killing state.
   */
  void markNodeKilling(final Node node);

  /**
   * Transitions the node from the killing state to the killed state.
   */
  void markNodeKilled(final Node node);

  /**
   * Transitions the node to the failure state.
   */
  void markNodeFailed(final Node node);

  /**
   * Kills a DAG.
   */
  //void killDag(final Dag dag);

  void updateDagStatus(Dag dag);

  /**
   * Shuts down the service and waits for the tasks to finish.
   */
  void shutdownAndAwaitTermination() throws InterruptedException;

  void markNodeCanceled(Node node);

  void createDagInstance(JobKey jobKey, Dag dagInstance);

  Dag getDagInstance(JobKey jobKey);

  void putFlowNodeBean(JobKey jobKey, NodeBean nodeBean);

  NodeBean getFlowNodeBean(JobKey jobKey);

}
