<template>
  <div class="container">
    <el-row>
      <Search @search='onSearch' />
    </el-row><br>

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
      title="新增"
      :visible.sync="dialogTableVisible"
    >
      <Forms
        :form-options='formOptions'
        :rules='rules'
        :show='showFooter'
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
import { jobAlarmApi } from "@/api/jobAlarm";
import { jobAlarm } from "@/assets/js/tableTitle.js";
import { successMsg, apiFn } from "@/assets/util";
const myMixin = {
  data: function() {
    return {
      jobAlarmApi
    };
  }
};
export default {
  name: "JobAlarm",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      tableData: { tableTitle: jobAlarm },
      tableList: [],
      pageSize: 10,
      currentPage: 1,
      total: 0,
      searchVal: "",
      showFooter: true,
      options: [
        { key: "edit", label: "编辑" },
        { key: "remove", label: "删除" }
      ],
      formOptions: {
        options: [
          {
            label: "任务组",
            key: "groupName",
            type: "input",
            groupName: "",
            value: ""
          },
          {
            label: "是否开启",
            key: "onOff",
            type: "checkbox",
            onOff: false,
            value: ""
          },
          {
            label: "rtx报警接收人列表：(以,分隔)",
            key: "alarmRtx",
            type: "input",
            alarmRtx: "",
            value: ""
          }
        ]
      },
      dialogTableVisible: false,
      rules: {},
      formVal: {},
      editType: ""
    };
  },
  created() {
    this.init();
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
        offset: (this.currentPage-1)*this.pageSize
      };

      let type = "init";
      apiFn()(jobAlarmApi, type, param, this).then(res => {
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
    edit(rows) {
      this.dialogTableVisible = true;
      this.editType = "update";
      let form = {};
      this.formOptions.options.forEach(ele => {
        form[ele.key] = rows[ele.key];
      });
      this.formVal = form;
    },
    handleDialog(val) {
      this.dialogTableVisible = false;
      if (val) {
        apiFn()(jobAlarmApi, "update", val, this).then(res => {
          if (res.successful) {
            successMsg(" 修改成功", this);
            this.init();
          }
        });
      }
    },
    remove(rows) {
      console.log(rows);
      this.$message({
        message: "你没有这个权限！",
        type: "warning"
      });
      // apiFn()(jobAlarmApi, "remove", { groupName: rows.groupName }, this).then(
      //   res => {
      //     successMsg(" 删除成功", this);
      //     this.init();
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
