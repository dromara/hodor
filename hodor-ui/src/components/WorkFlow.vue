<template>
  <div class="workFlow-container">
    <div
      class="breadcrumbDiv"
      v-if='breadcrumb.breadcrumbItem'
    >
      <Breadcrumb
        :breadcrumb='breadcrumb'
        :title='title'
      />
    </div>

    <el-row>
      <el-col :span="18">
        <el-select
          v-model="groupName"
          filterable
          placeholder="请选择"
          class="width100"
          size="small"
          @change="onSearch"
        >
          <el-option
            v-for="item in groupList"
            :key="item.groupName"
            :label="item.groupName"
            :value="item.groupName"
          >
          </el-option>
        </el-select>
      </el-col>
      <el-col :span="6">
        &nbsp;
        <el-button
          type="primary"
          size='small'
          @click="onSearch"
        >搜索</el-button>
        <el-button
          type="primary"
          size='small'
          @click="addNew"
        >新增job</el-button>
        <el-button
          type="primary"
          size='small'
          @click='fireDependence'
        >依赖执行</el-button>
      </el-col>
    </el-row>
    <el-row class="content">
      <el-col
        :span="4"
        class="leftCol"
      >
        <div class="leftWrap">
          <el-input
            v-model="filterVal"
            placeholder="查询job"
            class='inputMargin'
          ></el-input>
          <el-radio-group
            v-model="jobName"
            class="radioGroup"
          >
            <div
              v-for='(item,index) in jobList'
              class="item"
              @click="clickRadio($event)"
            >
              <el-radio-button :label="item.jobName"></el-radio-button>
            </div>
          </el-radio-group>
        </div>

      </el-col>
      <el-col
        :span="20"
        class="flowMap-content"
      >
        <FlowMap
          :flow-data='flowData'
          :group-name='groupName'
          :job-name='jobName'
          :breadcrumb='breadcrumb'
          :show-right-menu='showRightMenu'
        />
        <div class="colorWaring">
          <ul>
            <li
              v-for='item in colorWaring'
              :key='item.val'
            >
              <span
                class="colorSpan"
                :style="{ background: item.color}"
              ></span>&nbsp;<span>{{item.name}}</span>
            </li>
          </ul>

        </div>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import Search from "./common/Search";
import FlowMap from "./common/FlowMap";
import Breadcrumb from "./common/Breadcrumb";
import { workFlowApi } from "@/api/workFlow";
import { successMsg, apiFn, session } from "@/assets/util";
import { saveBreadcrumb, getBreadcrumb } from "@/utils/breadcrumb";
const myMixin = {
  data: function() {
    return {
      workFlowApi
    };
  }
};
export default {
  name: "WorkFlow",
  mixins: [myMixin],
  data() {
    let groupName = session("groupName");
    let jobName = session("jobName");
    return {
      groupList: [],
      jobList: [],
      baseJobList: [],
      groupName: groupName ? groupName : "",
      jobName: jobName ? jobName : "",
      filterVal: "",
      showRightMenu: true,
      flowData: {},
      breadcrumb: {},
      title: "job依赖",
      colorWaring: [
        {
          name: "未运行",
          val: "init",
          color: "white"
        },
        {
          name: "执行中",
          val: "process",
          color: "#66bfff"
        },
        {
          name: "成功",
          val: "success",
          color: "#1ab394"
        },
        {
          name: "暂停",
          val: "pause",
          color: "#d9534f"
        },
        {
          name: "禁用",
          val: "disbaled",
          color: "#9e9e9e"
        }
      ]
    };
  },
  created() {
    this.getGroupList();
    this.saveBreadcrumb();
  },
  watch: {
    filterVal: {
      handler(val) {
        if (val == null || val == "") {
          this.jobList = [].concat(this.baseJobList);
        } else {
          this.jobList = this.baseJobList.filter(ele => {
            return ele.jobName.toLowerCase().indexOf(val.toLowerCase()) > -1;
          });
        }
      }
    }
  },
  updated() {},
  mounted() {},
  components: { Search, FlowMap, Breadcrumb },
  methods: {
    saveBreadcrumb() {
      if (this.$route.params.breadcrumbItem) {
        this.breadcrumb = this.$route.params;
        saveBreadcrumb(this.$route.params);
      } else {
        let breadcrumb = getBreadcrumb(["/work_flow"]);
        this.breadcrumb =
          typeof breadcrumb == "object"
            ? breadcrumb.breadcrumbItem.length < 1
              ? {}
              : breadcrumb
            : {};
      }

      this.breadcrumb.breadcrumbItem && this.breadcrumb.breadcrumbItem[0].data
        ? session("groupName", this.breadcrumb.breadcrumbItem[0].data.groupName)
        : "";
      this.breadcrumb.breadcrumbItem && this.breadcrumb.breadcrumbItem[0].data
        ? session("jobName", this.breadcrumb.breadcrumbItem[0].data.jobName)
        : "";
    },
    getGroupList() {
      apiFn()(workFlowApi, "getGroupList", null, this).then(res => {
        if (Object.prototype.toString.call(res) == "[object Array]") {
          this.groupList = res;
          let groupName = res.length > 0 ? session("groupName") : "";
          this.groupName = "";
          if (res.length > 0) {
            this.groupName = groupName ? groupName : res[0].groupName;
            this.getJobList();
          }
        }
      });
    },
    getJobList() {
      let param = { groupName: this.groupName };
      apiFn()(workFlowApi, "getJobList", param, this).then(res => {
        if (res.successful) {
          this.jobList = res.data;
          this.baseJobList = [].concat(res.data);
          if (res.data && res.data.length > 0) {
            let jobName = session("jobName");
            this.jobName = jobName ? jobName : this.jobList[0].jobName;
            this.getFlowData();
          }
        }
      });
    },
    saveNames() {
      session("groupName", this.groupName);
      session("jobName", this.jobName);
    },
    onSearch(val) {
      this.getJobList();
      session("jobName", "");
    },
    addNew() {
      this.$router.push({
        name: "addJob",
        params: {
          breadcrumbItem: [
            { path: this.$route.fullPath, title: "workFlow管理" }
          ]
        }
      });
    },
    fireDependence() {
      let param = { jobGroup: this.groupName, jobName:this.jobName };
      apiFn()(workFlowApi, "fireDependence", param, this).then(res => {
        if (res.successful) {
          successMsg(res.msg, this);
        }
      });
    },
    clickRadio(e) {
      if (e.target.nodeName == "SPAN") {
        this.jobName = e.target.textContent;
        this.getFlowData();
      }
    },
    getFlowData() {
      this.saveNames();
      let param = {
        groupName: this.groupName,
        jobName: this.jobName
      };
      apiFn()(workFlowApi, "getJobWorkFlow", param, this, true).then(res => {
        if (res.successful) {
          this.flowData = JSON.parse(res.data);
        }
      });
    }
  },
  destroyed() {}
};
</script>
<style scoped>
.workFlow-container .content {
  padding-top: 10px;
  height: 80vh;
}

.workFlow-container .breadcrumbDiv {
  margin-bottom: 10px;
}
.workFlow-container .leftCol {
  height: 100%;
}
.workFlow-container .leftWrap {
  height: 100%;
  border: 1px solid #e7eaec;
  border-radius: 5px;
  padding: 5px;
  overflow: auto;
  box-sizing: border-box;
}
.workFlow-container .radioGroup {
  width: 100%;
}
.workFlow-container .radioGroup .item .el-radio-button {
  width: 100%;
}

.workFlow-container
  >>> .el-radio-button:first-child:last-child
  .el-radio-button__inner {
  width: 100%;
  border-bottom: none;
  text-overflow: ellipsis;
  overflow: hidden;
}

.workFlow-container .item:last-child {
  border-bottom: 1px solid #dde0e7;
}
.workFlow-container .inputMargin {
  margin-bottom: 8px;
}
/* 设置滚动条的样式 */
::-webkit-scrollbar {
  width: 6px;
}
/* 滚动槽 */
::-webkit-scrollbar-track {
  -webkit-box-shadow: inset006pxrgba(0, 0, 0, 0.3);
  border-radius: 10px;
}
/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: #999999;
  -webkit-box-shadow: inset006pxrgba(0, 0, 0, 0.5);
}
.workFlow-container .flowMap-content {
  height: 100%;
}
.workFlow-container .colorSpan {
  display: inline-block;
  width: 20px;
  height: 5px;
  border: 1px solid #e7eaec;
  border-radius: 5px;
}
.workFlow-container .colorWaring {
  position: absolute;
  top: 0px;
  right: 0px;
  padding: 5px;
  font-size: 10px;
}
</style>
