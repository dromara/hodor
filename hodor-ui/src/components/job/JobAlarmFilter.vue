<template>
  <div class="container">
    <el-row>
      <el-col :span="4">
        <el-button
          type="danger"
          :disabled="multipleSelection.length<1"
          @click="batchDelete"
          size="small"
        >批量删除</el-button>
        <el-button
          type="primary"
          @click="addNew"
          size="small"
        >添加报警过滤</el-button>
      </el-col>
      <el-col :span='20'>
        <Search @search='onSearch' />
      </el-col>
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
    <el-dialog
      title="新增报警过滤"
      :visible.sync="dialogTableVisible"
    >
      <Forms
        :form-options='formOptions'
        :rules='rules'
        :show='showFooter'
        :edit-type='editType'
        @handledialog='handleDialog'
        :select-opt='{groupName:groupList,scope:scopelist,filter:labels}'
      />

    </el-dialog>
  </div>
</template>

<script>
import Table from "../common/Table";
import Forms from "../common/Forms";
import Search from "../common/Search";
import Pagination from "../common/Pagination";
import { jobAlarmFilterApi } from "@/api/jobAlarmFilter";
import { jobAlarmFilter } from "@/assets/js/tableTitle.js";
import {
  successMsg,
  apiFn
} from "@/assets/util";
const myMixin = {
  data: function() {
    return {
      jobAlarmFilterApi
    };
  }
};
export default {
  name: "Service",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      multipleSelection: [],
      tableData: { tableTitle: jobAlarmFilter, selection: true },
      tableList: [],
      pageSize: 10,
      currentPage:1,
      total: 0,
      searchVal: "",
      groupList: [],
      dialogTableVisible: false,
      rules: {},
      formOptions: {
        options: [
          {
            label: "组名",
            key: "groupName",
            type: "select",
            groupName: ""
          },
          {
            label: "",
            key: "filter",
            type: "select",
            filter: "",
            value: "val",
            show: "label",
            spans: 8
          },
          {
            label: "",
            key: "code",
            type: "input",
            code: "",
            spans: 10,
            formLabelWidth: "10px"
          },
          {
            label: "使用范围",
            key: "scope",
            type: "radio",
            scope: "",
            value: "val",
            show: "label"
          }
        ]
      },
      showFooter: true,
      editType: "",
      labels: [
        { label: "错误编号", val: "code" },
        { label: "错误片段", val: "exception" }
      ],
      scopelist: [
        { label: "适用特定组", val: 0 },
        { label: "适用所有组", val: 1 }
      ]
    };
  },
  created() {
    this.init();
    this.addOptions();
    this.getGroupList();
  },
  updated() {},
  mounted() {},
  components: { Table, Search, Forms, Pagination },
  methods: {
    init(name) {
      let param = {
        search: name ? name : "",
        limit: this.pageSize,
        offset: (this.currentPage-1)*this.pageSize
      };

      let type = "init";
      apiFn()(jobAlarmFilterApi, type, param, this).then(res => {
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
      this.tableData.operation = [];
    },
    addNew() {
      this.dialogTableVisible = true;
    },
    getIds(key) {
      return this.multipleSelection.map(ele => {
        return ele[key];
      });
    },
    batchDelete() {
      let param = { ids: this.getIds("id").join() };
      apiFn()(jobAlarmFilterApi, "batchDelete", param, this).then(res => {
        if (res.successful) {
          successMsg("批量删除成功", this);
          this.init();
        }
      });
    },
    getGroupList() {
      apiFn()(jobAlarmFilterApi, "getGroupList", null, this).then(res => {
        if (res.successful) {
          this.groupList = res;
        }
      });
    },
    tableSelectionChange(val) {
      this.multipleSelection = val;
    },
    handleDialog(val) {
      this.dialogTableVisible = false;
      if (val) {
        let param = {
          groupName: val.groupName,
          scope: val.scope
        };
        param[val.filter] = val.code;
        apiFn()(
          jobAlarmFilterApi,
          "add",
          { alarmFilterJson: JSON.stringify(param) },
          this
        ).then(res => {
          if (res.successful) {
            successMsg("添加成功", this);
            this.init();
          }
        });
      }
    },
    onSearch(val) {
      this.init(val);
    }
  },
  destroyed() {}
};
</script>
<style>
</style>
