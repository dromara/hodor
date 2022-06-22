<template>
  <div class="container">
    <el-row>
      <Search @search='onSearch' />
    </el-row><br>
    <el-row>
      <el-button
        type="primary"
        @click="addNew"
        size="small"
      >新增</el-button>
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
      :title="editType=='add'?'新增':'修改'"
      :visible.sync="dialogTableVisible"
    >
      <Forms
        :form-options='formOptions'
        :rules='rules'
        :show='showFooter'
        :select-opt='{groupName:groupList}'
        :edit-type='editType'
        :form-val='formVal'
        @handledialog='handleDialog'
      />

    </el-dialog>
  </div>
</template>

<script>
import Table from "../common/Table";
import Forms from "../common/Forms";
import Search from "../common/Search";
import Pagination from "../common/Pagination";
import { jobStatusApi } from "@/api/jobStatus";
import { jobStatus } from "@/assets/js/tableTitle.js";
import { successMsg, apiFn } from "@/assets/util";
const myMixin = {
  data: function() {
    return {
      jobStatusApi
    };
  }
};
export default {
  name: "Jobstatus",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      tableData: { tableTitle: jobStatus },
      tableList: [],
      groupList: [],
      pageSize: 10,
      currentPage: 1,
      total: 0,
      searchVal: "",
      options: [
        { key: "edit", label: "编辑" },
        { key: "remove", label: "删除" }
      ],
      formOptions: {
        options: [
          {
            label: "任务组",
            key: "groupName",
            type: "select",
            groupName: "",
            value: ""
          },
          {
            label: "阀值：(单位%)",
            key: "threshold",
            type: "input",
            threshold: "",
            value: ""
          },
          {
            label: "是否开启",
            key: "enabled",
            type: "checkbox",
            enabled: false,
            value: ""
          },
          {
            label: "备注信息",
            key: "comment",
            type: "input",
            comment: "",
            value: ""
          }
        ]
      },
      dialogTableVisible: false,
      rules: {
        groupName: [
          {
            required: true,
            message: "请选择任务组名称",
            trigger: "change"
          }
        ]
      },
      showFooter: true,
      editType: "",
      formVal: {}
    };
  },
  created() {
    this.init();
    this.initGroupList();
    this.addOptions();
  },
  updated() {},
  mounted() {},
  components: { Table, Search, Forms, Pagination },
  methods: {
    init(name) {
      let param = {
        groupName: name ? name : "",
        limit: this.pageSize,
        offset: (this.currentPage - 1) * this.pageSize
      };

      let type = "init";
      apiFn()(jobStatusApi, type, param, this).then(res => {
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
      this.options.forEach(ele => {
        let obj = { label: ele.label, fn: this[ele.key] };
        this.tableData.operation.push(obj);
      });
    },
    initGroupList() {
      apiFn()(jobStatusApi, "getGroupList", null, this).then(res => {
        if (Object.prototype.toString.call(res) == "[object Array]") {
          this.groupList = res;
        }
      });
    },
    addNew() {
      this.dialogTableVisible = true;
      this.editType = "add";
      this.formVal = {};
    },
    handleDialog(val) {
      this.dialogTableVisible = false;
      if (val) {
        apiFn()(jobStatusApi, this.editType, val, this).then(res => {
          if (res.successful) {
            let msg = this.editType == "add" ? "新增" : "修改";
            successMsg(msg + "成功", this);
            this.init();
          }
        });
      }
    },
    edit(rows) {
      this.dialogTableVisible = true;
      this.editType = "update";
      let form = {};
      this.formOptions.options.forEach(ele => {
        form[ele.key] = rows[ele.key];
      });
      this.formVal = form;
    },
    remove(rows) {
      this.$message({
        message: "你没有这个权限！",
        type: "warning"
      });
      // apiFn()(jobStatusApi, "remove", { groupName: rows.groupName }, this).then(
      //   res => {
      //     console.log(res);
      //   }
      // );
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
