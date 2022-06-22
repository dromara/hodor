<template>
  <div class='nodeInfo_container'>
    <Breadcrumb
      :breadcrumb='breadcrumb'
      :group-name='groupName'
      :title='title'
    />
    <br />
    <el-row v-if='breadcrumb.jobMessage'>
      <div class="jobMessageOpt">
        <el-button
          type="primary"
          size="small"
        >负载均衡</el-button>
        <el-select
          v-model="selectVal"
          placeholder="请选择"
          size='small'
          class="selectStyle"
          @change='updateBalance'
        >
          <el-option
            v-for="item in selectOpt"
            :key="item.val"
            :label="item.label"
            :value="item.val"
          >
          </el-option>
        </el-select>

      </div>

    </el-row>
    <br />
    <Table
      :tableData='tableData'
      :list='tableList'
    />
    <el-row>
      <Pagination
        :pageSize.sync='pageSize'
        :currentPage.sync='currentPage'
        :total='total'
        @init='init'
      />
    </el-row>
  </div>

</template>
<script>
import Table from "../common/Table";
import Pagination from "../common/Pagination";
import Breadcrumb from "../common/Breadcrumb";
import { nodeInfo } from "@/assets/js/tableTitle.js";
import { saveBreadcrumb, getBreadcrumb } from "@/utils/breadcrumb";
import { successMsg, apiFn } from "@/assets/util";
import { nodeInfoApi } from "@/api/nodeInfo";
const myMixin = {
  data: function() {
    return {
      nodeInfoApi
    };
  }
};

export default {
  name: "NodeInfo",
  mixins: [myMixin],
  props: {},
  created() {
    this.saveBreadcrumb();
    this.addOptions();
  },
  data() {
    return {
      tableData: { tableTitle: nodeInfo },
      tableList: [],
      routeItem: [],
      breadcrumb: {},
      title: "节点信息",
      groupName: "",
      jobName: "",
      pageSize: 10,
      currentPage: 1,
      total: 0,
      options: [
        { key: "rightTimes", label: "倍权" },
        { key: "halfRight", label: "半权" },
        { key: "updateStatus", label: "禁用", status: "status" }
      ],
      selectOpt: [
        { label: "轮询", val: "roundRobin" },
        { label: "随机", val: "random" }
      ],
      selectVal: "roundRobin"
    };
  },
  components: { Table, Pagination, Breadcrumb },
  methods: {
    saveBreadcrumb() {
      if (this.$route.params.breadcrumbItem) {
        this.breadcrumb = this.$route.params;
        saveBreadcrumb(this.$route.params);
      } else {
        this.breadcrumb = getBreadcrumb(this.routeItem);
      }
      this.groupName =
        this.breadcrumb && this.breadcrumb.breadcrumbItem
          ? this.breadcrumb.breadcrumbItem[0].data.groupName
          : "";
      this.jobName =
        this.breadcrumb && this.breadcrumb.breadcrumbItem
          ? this.breadcrumb.breadcrumbItem[0].data.jobName
          : "";
      this.jobName ? this.getBalanceState() : "";
      this.init();
    },
    init(name) {
      let param = {
        groupName: this.groupName,
        jobName: this.jobName,
        limit: this.pageSize,
        offset: (this.currentPage - 1) * this.pageSize
      };
      let type = this.jobName ? "jobInit" : "init";
      this.getTableApi(type, param);
    },
    getTableApi(type, param) {
      apiFn()(nodeInfoApi, type, param, this).then(res => {
        if (res.successful) {
          this.tableList = res.rows;
          this.total = res.total;
        } else {
          this.tableList = [];
          this.total = 0;
        }
      });
    },
    getJobNodeList(name) {
      let param = {
        groupName: this.groupName,
        jobName: this.jobName,
        limit: this.pageSize,
        offset: (this.currentPage - 1) * this.pageSize
      };
      this.getTableApi("jobNodeList", param);
    },
    addOptions() {
      this.tableData.operation = [];
      this.options.forEach(ele => {
        let obj = { label: ele.label, fn: this[ele.key], status: ele.status };
        this.tableData.operation.push(obj);
      });
    },
    getBalanceState() {
      apiFn()(
        nodeInfoApi,
        "getBalanceState",
        { groupName: this.groupName, jobName: this.jobName },
        this
      ).then(res => {
        if (res && res.successful) {
          this.selectVal = res.data;
        }
      });
    },
    updateBalance(val) {
      let param = {
        groupName: this.groupName,
        jobName: this.jobName,
        lb: val
      };
      apiFn()(nodeInfoApi, "updateJobLoadBalance", param, this).then(res => {
        if (res.successful) {
          this.getJobNodeList(this.groupName);
        }
      });
    },
    setParam(row) {
      return {
        groupName: this.groupName,
        sessionName: row.sessionName ? row.sessionName : undefined,
        nodeName: row.nodeName,
        jobName: this.jobName
      };
    },
    rightTimes(row) {
      let param = this.setParam(row);
      param.type = "double";
      let type = this.jobName ? "jobChangeWeight" : "changeWeight";
      this.changeApi(type, param);
    },
    halfRight(row) {
      let param = this.setParam(row);
      param.type = "halve";
      let type = this.jobName ? "jobChangeWeight" : "changeWeight";
      this.changeApi(type, param);
    },
    updateStatus(row) {
      let param = this.setParam(row);
      let type = this.jobName ? "jobChangeStatus" : "changeStatus";
      this.changeApi(type, param);
    },
    changeApi(type, param) {
      apiFn()(nodeInfoApi, type, param, this).then(res => {
        if (res.successful) {
          this.init(this.groupName);
        }
      });
    }
  },
  destroyed() {
    localStorage.removeItem("$routeParams");
  }
};
</script>
<style>
.nodeInfo_container .jobMessageOpt {
  width: 30%;
  float: right;
}
.nodeInfo_container .jobMessageOpt .selectStyle {
  width: 80%;
}
</style>

