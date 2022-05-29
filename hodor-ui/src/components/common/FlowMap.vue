<template>
  <div
    class="flowMap-container"
    ref='boxDiv'
  >
    <div
      id="diagram"
      ref='diagram'
      style="width: 100%; min-height: 500px"
      @contextmenu="clickHandle"
      @click="hiddenMenu"
    ></div>
    <div
      class="rightList"
      ref='rightList'
      v-show='showRightMenu&&showRight'
    >
      <ul>
        <li
          @click="menuClickHandle($event)"
          v-for='(item,index) in rightList'
          :key='index'
          :id='item.val'
        > {{item.name}}</li>
      </ul>

    </div>
    <el-dialog
      title="job详情"
      :visible.sync="dialogTableVisible"
    >
      <el-card class="box-card">

        <div
          v-for="(item,key) in jobDetails"
          :key="key"
          class="text item list-item"
          v-if='listKey[key]'
        >
          <span>{{listKey[key]+'：' }}</span>
          <span>{{filterStatus(key,item)}}</span>
        </div>
      </el-card>
    </el-dialog>

  </div>
</template>

<script>
import gojs from "gojs";
import { successMsg, apiFn } from "@/assets/util";
import { workFlowApi } from "@/api/workFlow";
let $ = go.GraphObject.make;
const myMixin = {
  data: function() {
    return {
      workFlowApi
    };
  }
};
export default {
  name: "FlowMap",
  mixins: [myMixin],
  data() {
    return {
      nodes: [],
      diagram: null,
      showRight: false,
      showFlag: false,
      dialogTableVisible: false,
      jobDetails: {},
      disabledData: [],
      selectNode: "",
      rightList: [
        {
          name: "查看详情",
          val: "checkDetails"
        },
        {
          name: "禁用",
          val: "disabled"
        },
        {
          name: "启用",
          val: "enable"
        },
        {
          name: "查看执行明细",
          val: "queryExecDetail"
        }
      ],
      colorList: {
        disbaled: "#e3e3e3",
        init: "white",
        process: "#66bfff",
        success: "#1ab394",
        pause: "#d9534f"
      },
      colorWaring: {
        wait: "white",
        ready: "#b9f6ca",
        running: "#66bfff",
        success: "#1ab394",
        killed: "#f57f17",
        fail: "#d9534f"
      },
      listKey: {
        groupName: "任务组",
        jobName: "任务名称",
        cronExpression: "cron时间表达式",
        jobPath: "任务调用路径",
        shardingCount: "shardingCount",
        shardingItemParameters: "shardingItemParameters",
        failover: "failover",
        misfire: "错过马上执行",
        fireNow: "马上调度",
        desc: "任务描述",
        parameters: "任务参数",
        slaveIp: "调度端Ip",
        jobStatus: "任务状态",
        jobClass: "jobClass",
        lastFireTime: "上次执行时间",
        endTime: "任务结束时间",
        count: "任务调用次数",
        ifParallel: "是否并行执行",
        filePath: "文件路径",
        className: "任务类名",
        methodName: "任务方法名",
        ifBroadcast: "广播模式",
        activeTime: "任务激活时间",
        timeOut: "超时时间",
        category: "任务分类",
        createTime: "创建时间",
        host: "执行端host",
        port: "执行端port",
        bin: "任务类型",
        isDependence: "isDependence",
        once: "是否一次性任务",
        executeTime: "一次性任务执行时间",
        jobPackageName: "jobPackageName"
      }
    };
  },
  props: {
    showRequestId: {
      type: Boolean,
      default: () => {
        return false;
      }
    },
    flowData: {
      type: Object,
      default: () => {
        return null;
      }
    },
    groupName: {
      type: String,
      default: () => {
        return "";
      }
    },
    jobName: {
      type: String,
      default: () => {
        return "";
      }
    },
    breadcrumb: {
      type: Object,
      default: () => {
        return {};
      }
    },
    showRightMenu: {
      type: Boolean,
      default: () => {
        return false;
      }
    }
  },
  created() {},
  watch: {
    flowData: {
      handler(val) {
        if (val && val.nodes) {
          this.disabledData = val.disabled ? val.disabled : [];
          this.nodes = this.resetData(val.nodes, "jobNodeName");
          let edges = val.edges ? this.resetLink(val.edges, val.nodes) : [];
          this.updateModel(this.nodes, edges);
          if (this.diagram) {
            this.diagram.scale = this.nodes.length > 50 ? 0.2 : 0.5;
          } else {
            return;
          }
        }
      },
      deep: true,
      immediate: true
    }
  },
  updated() {},
  mounted() {
    this.initMap();
    this.scrollHandle();
  },
  components: {},
  methods: {
    initMap() {
      let self = this;
      var diagram = $(go.Diagram, "diagram", {
        initialContentAlignment: go.Spot.Center, // center Diagram contents
        "undoManager.isEnabled": true, // enable Ctrl-Z to undo and Ctrl-Y to redo
        layout: $(
          go.TreeLayout, // specify a Diagram.layout that arranges trees
          {
            angle: 90,
            layerSpacing: 35,
            alignment: go.TreeLayout.AlignmentCenterSubtrees
          }
        )
      });
      diagram.nodeTemplate = $(
        go.Node,
        "Auto",
        {
          doubleClick: function(e, node) {
            let key = node.part.data.key;
            if (key && key.indexOf("requestId:") > -1) {
              let i=key.indexOf("requestId:")
              let index=key.indexOf("#")
              let groupName=key.slice(0,index)
              let jobName=key.slice(index+1,i)
              let id=key.slice(i+10,key.length)
              let aNode = document.createElement("a");
              aNode.target = "_blank";
              aNode.setAttribute(
                "href",
                window.location.origin +window.location.pathname+ "#/job_run_details?requestId="+id+"&groupName="+groupName+"&jobName="+jobName
              );
              aNode.click();
            }
          }
        },
        new go.Binding("location", "loc", go.Point.parse).makeTwoWay(
          go.Point.stringify
        ),

        $(
          go.Shape,
          {
            figure: "RoundedRectangle",
            fill: "white",
            cursor: "pointer"
          },
          new go.Binding("fill", "jobStatus", function(type) {
            switch (type) {
              case 0:
                return self.showRightMenu
                  ? self.colorList.init
                  : self.colorWaring.fail;
                break;
              case 1:
                return self.showRightMenu
                  ? self.colorList.process
                  : self.colorWaring.success;
                break;
              case 2:
                return self.showRightMenu
                  ? self.colorList.success
                  : self.colorWaring.running;
                break;
              case 3:
                return self.showRightMenu
                  ? self.colorList.pause
                  : self.colorWaring.killed;
                break;
              case 4:
                return self.showRightMenu
                  ? self.colorList.init
                  : self.colorWaring.ready;
                break;
              case "wait":
                return "white";
                break;
              case "disabled":
                return "#9e9e9e";
                break;
            }
          })
        ),
        $(
          go.TextBlock,
          { margin: 5, isMultiline: true },
          new go.Binding("text", "key")
        )
      );
      diagram.addDiagramListener("ChangedSelection", this.rightFn);
      diagram.minScale = 0.1;
      diagram.maxScale = 10;
      diagram.toolManager.mouseWheelBehavior = "go.ToolManager.WheelNone"; //禁止滚动鼠标画图滚动
      this.diagram = diagram;
      this.updateModel();
    },
    updateModel(node, edges) {
      // No GoJS transaction permitted when replacing Diagram.model.
      if (node instanceof go.Model) {
        this.diagram.model = val;
      } else {
        var nodeDataArray = node ? node : [];
        let linkDataArray = edges ? edges : [];

        if (this.diagram) {
          this.diagram.model = new go.GraphLinksModel(
            nodeDataArray,
            linkDataArray
          );
        }
      }
    },
    resetLink(data, nodes) {
      let that = this;
      return data.map(ele => {
        return {
          from: that.showRequestId
            ? ele.source + "\nrequestId:" + this.getRequestId(ele.source, nodes)
            : ele.source,
          to: that.showRequestId
            ? ele.target + "\nrequestId:" + this.getRequestId(ele.target, nodes)
            : ele.target
        };
      });
    },
    getRequestId(name, nodes) {
      let arr = nodes.filter(ele => {
        return ele.jobNodeName == name;
      });
      return arr[0].requestId;
    },
    resetData(data, i) {
      let that = this;
      return data.map(ele => {
        let extIndex = this.disabledData.indexOf(ele[i]);
        return {
          key: that.showRequestId
            ? ele[i] + "\nrequestId:" + ele.requestId
            : ele[i],
          jobStatus:
            extIndex > -1
              ? "disabled"
              : ele.jobStatus == -1
              ? "wait"
              : ele.jobStatus
        };
      });
    },
    copy(text) {
      if (document.body.createTextRange) {
        let range = document.body.createTextRange();
        range.moveToElementText(text);
        range.select();
      } else if (window.getSelection) {
        let selection = window.getSelection();
        let range = document.createRange();
        range.selectNodeContents(text);
        selection.removeAllRanges();
        selection.addRange(range);
        document.execCommand("Copy");
      }
    },
    getSelectNode() {
      this.selectNode = "";
      for (let nit = this.diagram.nodes; nit.next(); ) {
        let node = nit.value;
        if (node.isSelected) {
          this.selectNode += node.data.key;
        }
      }
      if (this.selectNode != "") {
        this.showFlag = true;
      }
    },
    rightFn(e) {
      this.getSelectNode();
    },
    clickHandle(e) {
      if (this.showFlag) {
        this.showRight = true;
        this.showFlag = false;
        this.$refs.rightList.style.left = e.pageX - 270 + "px";
        this.$refs.rightList.style.top = e.pageY - 110 + "px";
      } else {
        this.hiddenMenu();
      }
    },
    hiddenMenu() {
      this.showRight = false;
    },
    menuClickHandle(e) {
      if (e.target.id) {
        let type = e.target.id;
        this[type]();
      }
      this.hiddenMenu();
    },
    disabled() {
      this.setStatusApi("disabled");
    },
    enable() {
      this.setStatusApi("enable");
    },
    setStatusApi(type) {
      let param = {
        nodeInfo: this.selectNode,
        resourceNodeInfo: this.groupName + "#" + this.jobName
      };
      apiFn()(workFlowApi, type, param, this).then(res => {
        if (res.successful) {
          let data = this.diagram.model.findNodeDataForKey(this.selectNode);
          if (type == "disabled") {
            this.diagram.model.setDataProperty(data, "jobStatus", "disabled");
          } else {
            this.getCurrentJobStatus().then(res => {
              if (res.successful) {
                this.diagram.model.setDataProperty(
                  data,
                  "jobStatus",
                  res.data.jobStatus
                );
              }
            });
          }
          this.diagram.clearSelection(); //清除选中节点
        }
      });
    },
    getCurrentJobStatus() {
      let tempObj = this.selectNode.split("#");
      return apiFn()(
        workFlowApi,
        "details",
        { groupName: tempObj[0], jobName: tempObj[1] },
        this
      );
    },
    checkDetails() {
      let tempObj = this.selectNode.split("#");
      this.dialogTableVisible = true;
      apiFn()(
        workFlowApi,
        "details",
        { groupName: tempObj[0], jobName: tempObj[1] },
        this
      ).then(res => {
        this.diagram.clearSelection(); //清除选中节点
        if (res.successful) {
          this.jobDetails = res.data;
        }
      });
    },
    queryExecDetail() {
      let tempObj = this.selectNode.split("#");
      let obj = {
        path: this.$route.fullPath,
        title: "workFlow管理",
        data: { groupName: tempObj[0], jobName: tempObj[1] }
      };
      this.breadcrumb.breadcrumbItem
        ? this.breadcrumb.breadcrumbItem.push(obj)
        : (this.breadcrumb.breadcrumbItem = [obj]);
      this.$router.push({
        name: "job_run_details",
        params: {
          breadcrumbItem: this.breadcrumb.breadcrumbItem,
          jobMessage: true
        }
      });
    },
    filterStatus(key, item) {
      let result = "";
      if (key == "jobStatus") {
        switch (item) {
          case 0:
            result = "未激活";
            break;
          case 1:
            result = "可运行";
            break;
          case 2:
            result = "正在运行";
            break;
          case 3:
            result = "暂停";
            break;
        }
      } else {
        result = item;
      }
      return result;
    },
    scrollHandle(e) {
      let dom = this.$refs.boxDiv;
      dom.addEventListener(
        "mousewheel",
        e => {
          this.diagram.clearSelection(); //清除选中节点
          this.hiddenMenu(); //隐藏右键菜单

          if (e.wheelDelta > 0) {
            //当滑轮向上滚动时
            this.diagram.scale += 0.2;
          }
          if (e.wheelDelta < 0) {
            //当滑轮向下滚动时
            this.diagram.scale -= 0.2;
          }
        },
        true
      );
    }
  },
  destroyed() {}
};
</script>
<style >
.flowMap-container {
  height: 100%;
}
#diagram {
  z-index: 1;
}
#diagram canvas {
  border: 0px;
  outline: none;
}
.flowMap-container .rightList {
  position: absolute;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  background-color: #fff;
  overflow: hidden;
  color: #42b983;
  transition: 0.3s;
  padding: 20px;
  z-index: 2;
  /* right: 0px; */
  /* top: 0px; */
}
.flowMap-container .rightList li {
  cursor: pointer;
}

.list-item {
  background: honeydew;
  padding: 5px;
  margin: 8px 0px;
  border: 1px solid #e7eaec;
}
</style>
