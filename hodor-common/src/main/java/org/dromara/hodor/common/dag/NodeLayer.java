package org.dromara.hodor.common.dag;

import java.util.List;

/**
 * 节点层
 *
 * @author tomgs
 * @since 2021/3/31
 */
public class NodeLayer {

  /** 当前层数 */
  private int layer;

  /** 当前层节点 */
  private List<Node> nodes;

  /** 当前层状态：ready、running、success、failure */
  private Status status = Status.READY;

  /** 当前层正在运行的节点 */
  private int runningNodes;

  public int getLayer() {
    return layer;
  }

  public void setLayer(int layer) {
    this.layer = layer;
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public void setNodes(List<Node> nodes) {
    this.nodes = nodes;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public int getRunningNodes() {
    return runningNodes;
  }

  public void setRunningNodes(int runningNodes) {
    this.runningNodes = runningNodes;
  }

}
