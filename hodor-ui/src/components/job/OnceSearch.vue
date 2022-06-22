<template>
  <div class="container">
    <el-row class="searchWrap">
      <el-form
        :inline="true"
        :model="searchForm"
        class="demo-form-inline"
      >
        <el-form-item>
          <el-button
            type="primary"
            size="small"
            @click='batchFire'
            :disabled="multipleSelection.length<1"
          >批量执行</el-button>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.timeType"
            placeholder="请选择"
            size="small"
            @change="onSearch"
          >
            <el-option
              v-for="item in timeType"
              :key="item.val"
              :label="item.label"
              :value="item.val"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            size="small"
            v-model="searchForm.time"
            :picker-options="pickerOptions"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format='yyyy-MM-dd HH:mm:ss'
            @blur="onSearch"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.type"
            placeholder="请选择"
            size="small"
            @change="onSearch"
          >
            <el-option
              v-for="item in type"
              :key="item.val"
              :label="item.label"
              :value="item.val"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            size="small"
            v-model="searchForm.group"
            placeholder="组查询"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-input
            size="small"
            v-model="searchForm.likeParam"
            placeholder="参数查询"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            icon="el-icon-search"
            size="small"
            @click="onSearch"
          >搜索</el-button>
        </el-form-item>
      </el-form>
    </el-row><br>
    <el-row>
      注：<span class="mark">每行</span>是一个job信息
    </el-row>
    <br>
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
    </el-row>

  </div>
</template>

<script>
import Table from "../common/Table";
import Pagination from "../common/Pagination";
import { onceSearceApi } from "@/api/onceSearch";
import { onceSearch } from "@/assets/js/tableTitle.js";
import { successMsg, apiFn, dateFormat } from "@/assets/util";
const myMixin = {
  data: function() {
    return {
      onceSearceApi
    };
  }
};
export default {
  name: "OnceSearch",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      tableData: { tableTitle: onceSearch, selection: true },
      tableList: [],
      multipleSelection: [],
      pageSize: 10,
      currentPage: 1,
      total: 0,
      searchVal: "",
      searchForm: {
        timeType: "0",
        type: "1"
      },
      timeType: [
        { label: "执行时间", val: "0" },
        { label: "创建时间", val: "1" }
      ],
      type: [
        { label: "全部", val: "1" },
        { label: "未正常调用的Job", val: "0" }
      ],
      pickerOptions: {
        /*disabledDate(time) {
          return time.getTime() > Date.now();
        },*/
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
    this.init();
    this.addOptions();
    this.searchForm.time = this.setDefaultTime();
  },
  updated() {},
  mounted() {},
  components: { Table, Pagination },
  methods: {
    init() {
      let param = {
        limit: this.pageSize,
        offset: (this.currentPage-1)*this.pageSize,
        sortName: "execute_time",
        sortOrder: "asc",
        group: "",
        likeParam: ""
      };
      if (this.searchForm.time == null || this.searchForm.time.length < 1) {
        this.searchForm.time = this.setDefaultTime();
      }
      param.startTime = this.searchForm.time[0];
      param.endTime = this.searchForm.time[1];
      delete this.searchForm.time;
      let params = Object.assign(param, this.searchForm);
      let type = "init";
      apiFn()(onceSearceApi, type, params, this).then(res => {
        if (res.successful) {
          this.tableList = res.rows;
          this.total = res.total;
        } else {
          this.tableList = [];
          this.total = 0;
        }
        this.searchForm.time = [param.startTime, param.endTime];
      });
    },
    setDefaultTime() {
      let now = new Date().getTime();
      return [dateFormat(now - 1000 * 60 * 60 * 24), dateFormat(now)];
    },
    addOptions() {
      this.tableData.operation = [];
      this.tableData.operation.push({
        label: "查看job的注册情况",
        fn: this.checkRegister
      });
    },
    checkRegister(row) {
      let param = {
        jobName: row.jobName,
        groupName: row.groupName
      };
      let errMsg = "未装载到相应quartz";
      let sucMsg = "已装载到相应quartz";

      apiFn()(onceSearceApi, "checkRegister", param, this).then(res => {
        if (res.successful) {
          successMsg(sucMsg, this);
        } else {
          this.$message.error(errMsg);
        }
      });
    },
    setBatchFireParam() {
      return this.multipleSelection.map(ele => {
        return ele.groupName + "@" + ele.jobName;
      });
    },
    batchFire() {
      let param = {
        params: this.setBatchFireParam().join(',')
      };
      apiFn()(onceSearceApi, "batchHandle", param, this).then(res => {
        if (res.successful) {
          successMsg("操作成功", this);
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    onSearch() {
      this.init();
    },
    tableSelectionChange(val) {
      this.multipleSelection = val;
    }
  },
  destroyed() {}
};
</script>
<style>
</style>
