<template>
  <div class="container">
    <el-row>
      <Search
        @search='onSearch'
        :autocomplete='true'
        :list='searchList'
        :searchval='searchVal'
      />
    </el-row><br>
    <el-row>
      <el-form
        label-width="0px"
        :model="searchForm"
        :inline="true"
      >

        <el-form-item class="itemPosition">
          <el-input
            size="small"
            v-model="searchForm.search"
            placeholder="--------自定义检索--------"
            @change="filterList"
          ></el-input>
        </el-form-item>
        <el-form-item class="itemPosition">
          <el-select
            v-model="searchForm.category"
            size="small"
            @change='filterList'
          >
            <el-option
              label="自定义任务分类--全部"
              value="all"
            ></el-option>
            <el-option
              :label="item"
              :value="item"
              v-for='(item,i) in categoryList'
              :key='i'
            ></el-option>

          </el-select>
        </el-form-item>
        <el-form-item class="itemPosition">
          <el-select
            v-model="searchForm.exeStatus"
            @change='filterList'
            size="small"
          >
            <el-option
              v-for='(item,index) in exeStatusList'
              :key='index'
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="itemPosition">
          <el-select
            v-model="searchForm.jobStatus"
            @change='filterList'
            size="small"
          >
            <el-option
              v-for='(item,index) in jobStatusList'
              :key='index'
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="itemPosition">
          <div class="labelsPosition">job执行时间</div>
          <el-date-picker
            size="small"
            v-model="searchForm.time"
            :picker-options="pickerOptions"
            type="datetimerange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format='yyyy-MM-dd HH:mm:ss'
            @change='filterList'
            @input="filterList"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item class="itemPosition">
          <el-button
            type="primary"
            size="small"
            @click="addNew"
          >新增</el-button>
          <el-button
            type="primary"
            size="small"
            @click="batchUpload"
          >批量上传</el-button>
        </el-form-item>
      </el-form>

    </el-row>
    <el-row>
      <el-form :inline="true">
        <el-form-item>
          <el-button-group>
            <el-button
              size="small"
              type="primary"
              :disabled="multipleSelection.length<1"
              @click='batchHandle("1")'
            >批量恢复</el-button>
            <el-button
              size="small"
              type="danger"
              :disabled="multipleSelection.length<1"
              @click='batchHandle("0")'
            >批量暂停</el-button>
            <el-button
              size="small"
              type="danger"
              :disabled="multipleSelection.length<1"
              @click='batchHandle("2")'
            >批量删除</el-button>
            <el-button
              size="small"
              type="primary"
              :disabled="multipleSelection.length<1"
              @click='batchHandle("3")'
            >批量导出</el-button>
          </el-button-group>
        </el-form-item>
      </el-form>

    </el-row>
    <el-row>
      <Table
        :tableData='tableData'
        :list='tableList'
        @selection='tableSelectionChange'
      />
    </el-row>
    <el-row>
      <Pagination
        :pageSize.sync='pageSize'
        :currentPage.sync='currentPage'
        :total='total'
        @init='init'
      />
      <el-col>
        <span class="warningInfo">
          <i class="el-icon-warning"></i>
            <span class="turnOff">自定义检索</span>目前支持
            <span class="turnOff">任务名称</span>、
            <span class="turnOff">文件路径</span>、
            <span class="turnOff">任务参数</span>、
            <span class="turnOff">任务描述</span>的模糊查询，当输入信息之后鼠标点击空白页即会展示搜索结果!
        </span>
      </el-col>
    </el-row>
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogTableVisible"
    >
      <div v-if='showType=="edit"'>
        <EditJob
          :rows='editData'
          @close='closeDialog'
        />
      </div>
      <div v-if='showType=="statistics"'>
        <el-row>
          <div class="radigroupBtn">
            <el-radio-group
              v-model="queryCondition"
              size="small"
              @change="changeQueryCondition"
            >
              <el-radio-button
                :label="item.val"
                v-for='(item,index) in conditions'
              >{{item.label}}</el-radio-button>
            </el-radio-group>
          </div>

          <chart
            width='100%'
            ref='linecharts'
            :options="lineOption"
          ></chart>
        </el-row>
      </div>

    </el-dialog>

  </div>
</template>

<script>
import Table from "../common/Table";
import Search from "../common/Search";
import EditJob from "./config/EditJob";
import Pagination from "../common/Pagination";
import { jobMessageApi } from "@/api/jobMessage";
import { jobMessage } from "@/assets/js/tableTitle.js";
import { successMsg, apiFn, getkeys, session } from "@/assets/util";
import topProject from "../../store/modules/common/topProject";
const myMixin = {
  data: function() {
    return {
      jobMessageApi
    };
  }
};
export default {
  name: "Service",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      tableData: {
        tableTitle: jobMessage,
        selection: true,
        btnType: "dropdown",
        fn: this.handleCommand,
        dblclick: true
      },
      searchList: [],
      tableList: [],
      multipleSelection: [],
      lineOption: {},
      pageSize: 10,
      currentPage: 1,
      total: 0,
      searchVal: "",
      dialogTitle: "",
      dialogTableVisible: false,
      showType: "",
      categoryList: [],
      queryCondition: "0",
      editData: null,
      statisticsRow: null,
      searchForm: { category: "all", exeStatus: "all", jobStatus: "all" },
      conditions: [
        { label: "今天", val: "0" },
        { label: "昨天", val: "1" },
        { label: "近七天", val: "2" },
        { label: "本月", val: "3" },
        { label: "上个月", val: "4" },
        { label: "今年", val: "5" }
      ],
      exeStatusList: [
        { value: "all", label: "job执行状态--全部" },
        { value: "unExe", label: "job执行状态--上次未正常执行的job" }
      ],
      jobStatusList: [
        { value: "all", label: "job状态--全部" },
        { value: 0, label: "job状态--未激活" },
        { value: 1, label: "job状态--正在运行" },
        { value: 3, label: "job状态--暂停" }
      ],
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
    this.searchVal = session("searchVal");
    this.initSearchList();
    this.searchVal ? this.init() : "";
    this.addOptions();
  },
  updated() {},
  mounted() {},
  components: { Table, Search, EditJob, Pagination },
  methods: {
    init(obj) {
      if (obj == null) {
        obj = session("historyParam");
        this.searchForm.time = (obj.startTime === "" || obj.startTime === undefined || obj.startTime === null? undefined
          : [obj.startTime, obj.endTime]);
      }
      let param = {
        groupName: this.searchVal,
        category: this.searchForm.category = obj ? obj.category : this.searchForm.category,
        exeStatus: this.searchForm.exeStatus = obj ? obj.exeStatus : this.searchForm.exeStatus,
        jobStatus: this.searchForm.jobStatus = obj ? obj.jobStatus : this.searchForm.jobStatus,
        startTime: obj ? obj.startTime : this.searchForm.time && this.searchForm.time.length > 0 ? this.searchForm.time[0] : "",
        endTime: obj ? obj.endTime : this.searchForm.time && this.searchForm.time.length > 0 ? this.searchForm.time[1] : "",
        //timetmp: this.searchForm.time = (obj.startTime === "" ? undefined : [obj.startTime, obj.endTime]),
        search: this.searchForm.search = obj ? obj.search : this.searchForm.search,
        limit: this.pageSize,
        offset: (this.currentPage - 1) * this.pageSize
      };
      let params = obj ? Object.assign(param, obj) : param;
      let type = "init";
      apiFn()(jobMessageApi, type, params, this, true).then(res => {
        if (res.successful) {
          if (Object.prototype.toString.call(res) === "[object Array]") {
            this.tableList = res;
            this.total = res.length;
            this.categoryList = res.categories;
          } else {
            this.tableList = res.rows;
            this.total = res.total;
            this.categoryList = res.categories;
          }
        } else {
          this.tableList = [];
          this.total = 0;
          this.categoryList = [];
        }
      });
    },
    deepCopyObj() {},
    filterList() {
      let obj = {
        startTime:
          this.searchForm.time && this.searchForm.time.length > 0
            ? this.searchForm.time[0]
            : "",
        endTime:
          this.searchForm.time && this.searchForm.time.length > 0
            ? this.searchForm.time[1]
            : ""
      };
      let param = Object.assign(obj, this.searchForm);
      param = JSON.parse(JSON.stringify(param));
      delete param.time;
      session("historyParam", param);
      this.init(param);
    },

    initSearchList() {
      apiFn()(jobMessageApi, "getGroupList", null, this).then(res => {
        if (Object.prototype.toString.call(res) === "[object Array]") {
          this.searchList = res;
        } else {
          this.searchList = [];
        }
      });
    },
    addOptions() {
      let opts = [
        { label: "统计", key: "statistics" },
        { label: "执行明细", key: "progressDetail" },
        { label: "查看节点信息", key: "nodeInfo" },
        { label: "编辑", key: "edit" },
        { label: "立即执行", key: "firenow" },
        { label: "job是否已注册", key: "register" },
        // { label: "重新加载job", key: "restartJob" },
        { label: "clone此job", key: "clone" },
        { label: "下载", key: "downLoad" },
        { label: "调度恢复", key: "resumeJob", flag: true },
        { label: "调度暂停", key: "pauseJob", flag: true },
        { label: "查看依赖", key: "checkFlow" }
      ];

      this.tableData.operation = opts;
    },
    batchUpload() {
      this.$router.push({
        name: "batchUpload",
        params: {
          breadcrumbItem: [{ path: this.$route.fullPath, title: "job信息管理" }]
        }
      });
    },
    addNew() {
      this.$router.push({
        name: "addJob",
        params: {
          breadcrumbItem: [{ path: this.$route.fullPath, title: "job信息管理" }]
        }
      });
    },
    batchHandle(type) {
      let param = {
        groupName: this.searchVal,
        jobNames: getkeys(this.multipleSelection, "jobName").join()
      };

      if (+type < 2) {
        param.type = +type;
        let msg = type == "0" ? "批量暂停成功" : "批量恢复成功";
        if (type == "0") {
          this.batchPause(param).then(res => {
            param.opReason = '暂停原因：' + res.value;
            debugger;
            this.batchApi("batch", param, msg);
          }).catch(() => {});
          return;
        }
        this.batchApi("batch", param, msg);
        return;
      }
      if (+type < 3) {
        param.type = +type;
        let msg = "批量删除成功";
        let validateVal = this.removeValidate();
        if (validateVal == null || validateVal == "") {
          this.batchRmove(param)
            .then(res => {
              param.opReason = '删除原因：' + res.value;
              debugger;
              this.batchApi("batch", param, msg);
            })
            .catch(() => {});
        } else {
          this.$message({
            message: "任务 " + validateVal + " 不能执行此操作",
            type: "warning"
          });
        }
        return;
      }

      this.batchExport(param);
    },
    removeValidate() {
      let extArr = this.multipleSelection.filter(ele => {
        return ele.jobStatus == 2 || ele.jobStatus == 1;
      });
      if (extArr.length > 0) {
        return getkeys(extArr, "jobName").join();
      }
    },
    batchApi(type, param, msg) {
      apiFn()(jobMessageApi, type, param, this).then(res => {
        if (res.successful) {
          successMsg(msg, this);
          //this.init(this.searchVal);
          this.init();
        }
      });
    },
    batchPause(param) {
      return this.$prompt("谨慎操作：确认要暂停任务：" + param.jobNames + "?，请输入暂停原因:", '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      });
    },
    batchRmove(param) {
      /*return this.$confirm("确认要删除job：" + param.jobNames + "?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      });*/
      return this.$prompt("谨慎操作：确认要删除任务：" + param.jobNames + "?，请输入删除原因:", '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      });
    },
    batchExport(param) {
      apiFn()(jobMessageApi, "batchExport", param, this).then(res => {
        if (res.data) {
          let url = window.URL.createObjectURL(new Blob([res.data]));
          let link = document.createElement("a");
          link.style.display = "none";
          link.href = url;
          link.setAttribute("download", "admin_setting.ini.txt");
          document.body.appendChild(link);
          link.click();
          window.URL.revokeObjectURL(url);
        }
      });
    },
    onSearch(val) {
      session("searchVal", val);
      this.searchVal = val;
      this.filterList();
    },
    handleCommand(command, row) {
      this[command](row);
    },
    tableSelectionChange(val) {
      this.multipleSelection = val;
    },
    // 操作事件
    directOpration(type, param, sucMsg, errMsg, reload) {
      apiFn()(jobMessageApi, type, param, this).then(res => {
        if (res.successful) {
          successMsg(res.msg ? res.msg : sucMsg, this);
          reload ? this.init() : "";
        } else {
          errMsg ? this.$message.error(errMsg) : "";
          this.init();
        }
      });
    },
    firenow(row) {
      let param = {
        jobName: row.jobName,
        jobGroup: row.groupName
      };
      this.directOpration("firenow", param, "立即执行成功", null, true);
    },
    register(row) {
      let param = {
        jobName: row.jobName,
        groupName: row.groupName
      };
      let errMsg = "未装载到相应quartz";
      let sucMsg = "已装载到相应quartz";

      this.directOpration("register", param, sucMsg, errMsg);
    },
    restartJob(row) {
      let param = {
        groupName: row.groupName,
        jobName: row.jobName,
        jobGroup: row.groupName
      };
      this.directOpration("restartJob", param, "重新加载成功", null, true);
    },
    /*pauseJob(row) {
      this.$confirm("暂停调度？请谨慎操作", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
      .then(() => {
        this.directOpration("pause", row, "暂停任务成功", null, true);
      })
      .catch(() => {});
    },*/
    pauseJob(row) {
      this.$prompt('谨慎操作：请输入暂停原因', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (value != null && value.length > 128) {
          throw new Error("长度大于规定值128!");
        }
        //往row插入新的元素
        row.opReason = '暂停原因：' + value;
        this.directOpration("pause", row, "暂停任务成功", null, true);
        /*this.$message({
          type: 'success',
          message: '你输入的是: ' + value
        });*/
      }).catch((e) => {
        this.$message({
          type: 'error',
          message: '提交失败：' + e
        });
      });
    },
    resumeJob(row) {
      this.$confirm("恢复调度？请谨慎操作", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
      .then(() => {
        this.directOpration("resume", row, "恢复任务成功", null, true);
      })
      .catch(() => {});
    },
    downLoad(row) {
      let param = {
        groupName: this.searchVal,
        jobNames: row.jobName
      };
      this.batchExport(param);
    },
    progressDetail(row) {
      this.$router.push({
        name: "job_run_details",
        params: {
          breadcrumbItem: [
            { path: this.$route.fullPath, title: "job信息管理", data: row }
          ],
          jobMessage: true
        }
      });
    },
    nodeInfo(row) {
      this.$router.push({
        name: "nodeInfo",
        params: {
          breadcrumbItem: [
            { path: this.$route.fullPath, title: "job信息管理", data: row }
          ],
          jobMessage: true
        }
      });
    },
    clone(row) {
      this.$router.push({
        name: "addJob",
        params: {
          breadcrumbItem: [
            { path: this.$route.fullPath, title: "job信息管理", data: row }
          ],
          clone: true
        }
      });
    },
    statistics(row) {
      this.dialogTitle = "组" + row.groupName + "(任务" + row.jobName + ")";
      this.showType = "statistics";
      this.queryCondition = "0";
      this.lineOption = {};
      this.statisticsRow = row;
      this.openDialog();
      this.jobStatisticsApi(row);
    },
    jobStatisticsApi(row) {
      let param = {
        groupName: row.groupName,
        jobName: row.jobName,
        id: this.queryCondition
      };
      this.showLoading();
      apiFn()(jobMessageApi, "jobStatistics", param, this).then(res => {
        this.hideLoading();
        if (res && res.successful) {
          this.lineOption = JSON.parse(res.data);
        }
      });
    },
    changeQueryCondition(val) {
      this.jobStatisticsApi(this.statisticsRow);
    },
    showLoading() {
      this.$nextTick(() => {
        this.$refs.linecharts.showLoading({
          text: "正在加载数据"
        });
      });
    },
    hideLoading() {
      this.$nextTick(() => {
        this.$refs.linecharts.hideLoading();
      });
    },
    edit(row) {
      this.dialogTitle = "job修改";
      this.showType = "edit";
      this.openDialog();
      // this.editData = row;
      let param = { groupName: row.groupName, jobName: row.jobName };
      this.getJobInfo(param, "editData");
    },
    getJobInfo(param, key) {
      apiFn()(jobMessageApi, "getJobInfo", param, this).then(res => {
        if (res && res.successful) {
          this[key] = res.data;
        }
      });
    },
    checkFlow(row) {
      this.$router.push({
        name: "workFlow",
        params: {
          breadcrumbItem: [
            { path: this.$route.fullPath, title: "job信息管理", data: row }
          ],
          jobMessage: true
        }
      });
    },
    openDialog() {
      this.dialogTableVisible = true;
    },
    closeDialog(flag) {
      this.dialogTableVisible = false;
      if (flag) {
        this.onSearch(this.searchVal);
      }
    }
  },
  destroyed() {}
};
</script>
<style scopd>
.itemPosition {
  position: relative;
  margin-bottom: 5px;
}
.labelsPosition {
  position: absolute;
  top: -2px;
  font-size: 10px;
  color: #3c8dbc;
  z-index: 5;
  left: 3px;
  background: white;
  padding: 0px 5px;
  line-height: 10px;
}

.warningInfo {
  font-size: 10px;
  color: #e6a23c;
  display: inline-block;
  margin-top: 30px;
}

.turnOn {
  color: rgb(19, 206, 102);
  font-weight: 700;
}
.turnOff {
  color: rgb(255, 73, 73);
  font-weight: 700;
}

</style>
