<template>
  <div class="container">
    <div v-if='breadcrumb.breadcrumbItem'>
      <Breadcrumb
        :breadcrumb='breadcrumb'
        :title='title'
      />
    </div>

    <br />
    <el-row v-if='!routeQuery'>
      <el-form
        :inline="true"
        :model="formSearch"
        class="demo-form-inline"
      >
        <el-form-item>
          <el-input
            v-model="formSearch.uuid"
            size="small"
            placeholder="请输入查询的任务UUID"
          ></el-input>
        </el-form-item>
        <el-form-item label="执行时间">
          <el-date-picker
            size="small"
            v-model="formSearch.time"
            :picker-options="pickerOptions"
            type="datetimerange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format='yyyy-MM-dd HH:mm:ss'
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button
            size="small"
            type="primary"
            @click="onSearch"
          >查询</el-button>
        </el-form-item>
      </el-form>
    </el-row>
    <el-row>
      注：<span class="mark">每行</span>是任务的一次执行
    </el-row>
    <br>
    <el-row>
      <Table
        :tableData='tableData'
        :list='tableList'
      />
    </el-row>
    <el-row>
      <Pagination
        :pageSize.sync='pageSize'
        :currentPage.sync='currentPage'
        :total='total'
        @init='init'
      />
    </el-row>
    <el-dialog
      class="logPre"
      :title="dialogTitle"
      :visible.sync="dialogTableVisible"
      width="80%"
    >
      <div>
        <pre>{{logInfo}}</pre>
      </div>
    </el-dialog>
    <el-dialog
      class="logPre"
      title="依赖执行状态"
      :visible.sync="dialogFlowVisible"
      width="80%"
    >
      <div class="flowMapDiv">
        <FlowMap
          :flow-data='flowData'
          :group-name='groupName'
          :job-name='jobName'
          show-request-id='true'
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
      </div>

    </el-dialog>

  </div>
</template>

<script>
import Table from "../common/Table";
import Pagination from "../common/Pagination";
import Breadcrumb from "../common/Breadcrumb";
import { jobRunDetailsApi } from "@/api/jobRunDetails";
import { jobRunDetails } from "@/assets/js/tableTitle.js";
import { saveBreadcrumb, getBreadcrumb } from "@/utils/breadcrumb";
import { successMsg, apiFn } from "@/assets/util";
import FlowMap from "../common/FlowMap";
const myMixin = {
  data: function() {
    return {
      jobRunDetailsApi
    };
  }
};
export default {
  name: "JobRunDetatils",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      tableData: {
        tableTitle: jobRunDetails,
        btnType: "dropdown",
        fn: this.handleCommand
      },
      title: "执行明细",
      tableList: [],
      routeItem: [],
      formSearch: {},
      breadcrumb: {},
      flowData: {},
      pageSize: 10,
      currentPage: 1,
      routeQuery: "",
      total: 0,
      dialogTitle: "",
      searchVal: "",
      groupName: "",
      jobName: "",
      uuid: "",
      logInfo: "",
      colorWaring: [
        {
          name: "READY",
          val: "4",
          color: "#b9f6ca"
        },
        {
          name: "RUNNING",
          val: "2",
          color: "#66bfff"
        },
        {
          name: "SUCCESS",
          val: "1",
          color: "#1ab394"
        },
        {
          name: "KILLED",
          val: "3",
          color: "#f57f17"
        },
        {
          name: "FAIL",
          val: "0",
          color: "#d9534f"
        },
        {
          name: "WAIT",
          val: "-1",
          color: "white"
        },
        {
          name: "Disabled",
          val: "disabled",
          color: "#9e9e9e"
        }
      ],
      dialogTableVisible: false,
      dialogFlowVisible: false,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
        shortcuts: [
          {
            text: "最近一周",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            }
          },
          {
            text: "最近一个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            }
          },
          {
            text: "最近三个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            }
          }
        ]
      }
    };
  },
  created() {
    this.saveBreadcrumb();
    this.addOptions();
  },
  updated() {},
  mounted() {},
  components: { Table, Pagination, Breadcrumb, FlowMap },
  methods: {
    saveBreadcrumb() {
      if (this.$route.params.breadcrumbItem) {
        this.breadcrumb = this.$route.params;
        saveBreadcrumb(this.$route.params);
      } else {
        let breadcrumb = getBreadcrumb(this.routeItem);
        this.breadcrumb = typeof breadcrumb == "object" ? breadcrumb : {};
      }
      let len = this.breadcrumb.breadcrumbItem
        ? this.breadcrumb.breadcrumbItem.length - 1
        : 0;
      this.groupName = this.breadcrumb.breadcrumbItem
        ? this.breadcrumb.breadcrumbItem[len].data.groupName
        : "";
      this.jobName = this.breadcrumb.breadcrumbItem
        ? this.breadcrumb.breadcrumbItem[len].data.jobName
        : "";
      this.routeQuery = this.$route.query.requestId
        ? this.$route.query.requestId
        : "";
      this.jobName = this.$route.query.jobName
        ? this.$route.query.jobName
        : this.jobName;
      this.groupName = this.$route.query.groupName
        ? this.$route.query.groupName
        : this.groupName;
      this.init();
    },
    init(obj) {
      let param = {
        groupName: this.groupName,
        limit: this.pageSize,
        offset: (this.currentPage - 1) * this.pageSize,
        uuid: this.routeQuery
          ? this.routeQuery
          : this.formSearch.uuid
          ? this.formSearch.uuid
          : "",
        start:
          this.formSearch.time && this.formSearch.time.length > 0
            ? this.formSearch.time[0]
            : "",
        end:
          this.formSearch.time && this.formSearch.time.length > 0
            ? this.formSearch.time[1]
            : "",
        jobName: this.jobName
      };
      if (obj) {
        param.uuid = obj.uuid ? obj.uuid : "";
        if (obj.time && obj.time.length > 0) {
          param.start = obj.time[0];
          param.end = obj.time[1];
        }
      }
      let type = "init";
      apiFn()(jobRunDetailsApi, type, param, this).then(res => {
        if (res.successful) {
          this.tableList = res.rows;
          this.total = res.total;
        } else {
          this.tableList = [];
          this.total = 0;
        }
      });
    },
    addOptions() {
      let opts = [
        { label: "同步状态", key: "syncState", flag: true },
        { label: "查看执行日志", key: "checkLog" },
        { label: "kill任务", key: "killJob", flag: true },
        // { label: "终止执行", key: "pauseExecute",flag:true },
        { label: "重新执行", key: "rerun", flag: true },
        { label: "依赖执行状态", key: "flowMapFn" }
      ];
      this.tableData.operation = opts;
    },

    syncState(row) {
      apiFn()(jobRunDetailsApi, "syncData", { uuid: row.uuid }, this).then(
        result => {
          if (result && result.successful) {
            successMsg(
              "操作结果：\n状态:" +
                result.data.taskStatus +
                " 返回信息:" +
                result.data.exception +
                "操作成功",
              this
            );
          } else {
            if (result) {
              this.$message.error(result.msg);
            }
          }
        }
      );
    },
    killJob(row) {
      apiFn()(jobRunDetailsApi, "killJob", { uuid: row.uuid }, this).then(
        res => {
          if (res && res.successful) {
            successMsg(res.msg, this);
          } else {
            if (res) {
              this.$message.error(res.msg);
            }
          }
        }
      );
    },
    pauseExecute(row) {
      apiFn()(
        jobRunDetailsApi,
        "PauseJobExecute",
        { uuid: row.uuid },
        this
      ).then(res => {
        if (res && res.successful) {
          successMsg("终止执行成功", this);
        } else {
          if (res) {
            this.$message.error(res.msg);
          }
        }
      });
    },
    checkLog(row) {
      this.uuid = row.uuid;
      this.dialogTitle = row.uuid + "任务执行日志详情";
      apiFn()(jobRunDetailsApi, "getLogs", { uuid: row.uuid }, this).then(
        res => {
          if (res.successful) {
            this.openDialog(res.data);
          }
        }
      );
    },
    rerun(row) {
      apiFn()(jobRunDetailsApi, "rerun", { requestId: row.uuid }, this).then(
        res => {
          if (res.successful) {
            successMsg(res.msg, this);
            this.init();
          }
        }
      );
    },
    flowMapFn(row) {
      this.dialogFlowVisible = true;
      this.flowData = {};
      apiFn()(
        jobRunDetailsApi,
        "flowDetails",
        { requestId: row.uuid },
        this
      ).then(res => {
        if (res && res.successful) {
          this.flowData = res.data;
        }
      });
    },
    onSearch() {
      this.init(this.formSearch);
    },
    handleCommand(command, row) {
      this[command](row);
    },
    openDialog(data) {
      this.dialogTableVisible = true;
      this.logInfo = data;
    }
  }
};
</script>
<style>
.mark {
  color: #f56c6c;
}
.logPre .el-dialog {
  background: #4f6b8a;
}
.logPre .el-dialog .el-dialog__title {
  color: #ece5e5;
}
.logPre pre {
  color: #d6d0d0;
  overflow: auto;
}
.flowMapDiv {
  min-height: 500px;
  position: relative;
}
.flowMapDiv .colorWaring {
  color: white;
  position: absolute;
  right: 0;
  top: -36px;
}

.flowMapDiv .colorSpan {
  display: inline-block;
  width: 20px;
  height: 5px;
  border: 1px solid #e7eaec;
  border-radius: 5px;
}
</style>
