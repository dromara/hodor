<template>
  <div class="container">
    <el-row>
      <Search @search='onSearch' />
    </el-row><br>
    <el-row>
      <el-button
        type="primary"
        @click='addNew'
        size='small'
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
      title="新增"
      :visible.sync="dialogTableVisible"
    >
      <Forms
        :form-options='formOptions'
        :rules='rules'
        :show='showFooter'
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
import { jobgroupApi } from "@/api/jobgroup";
import { jobGroup } from "@/assets/js/tableTitle.js";
import {
  successMsg,
  apiFn
} from "@/assets/util";
const myMixin = {
  data: function() {
    return {
      jobgroupApi
    };
  }
};
export default {
  name: "Service",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      tableData: { tableTitle: jobGroup },
      tableList: [],
      pageSize: 10,
      currentPage: 1,
      total: 0,
      searchVal: "",
      dialogTableVisible: false,
      showFooter: true,
      formOptions: {
        options: [
          { label: "组名", key: "groupName", type: "input", groupName: "" },
          { label: "备注", key: "remark", type: "input", remark: "" }
        ]
      },
      rules: {
        groupName: [
          {
            required: true,
            message: "请输入任务组名称",
            trigger: "blur"
          }
        ]
      },
      options: [
        { key: "remove", label: "删除" },
        { key: "checkInfo", label: "查看节点信息" }
      ]
    };
  },
  created() {
    this.init();
    this.addOptions();
  },
  watch: {},
  updated() {},
  mounted() {},
  components: { Table, Search, Pagination, Forms },
  methods: {
    init(name) {
      let param = {
        groupName: name ? name : "",
        limit: this.pageSize,
        offset: (this.currentPage-1)*this.pageSize
      };

      let type = "init";
      apiFn()(jobgroupApi, type, param, this).then(res => {
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
    addNew() {
      this.dialogTableVisible = true;
    },
    handleDialog(val) {
      this.dialogTableVisible = false;

      if (val) {
        apiFn()(jobgroupApi, "add", val, this).then(res => {
          if (res.successful) {
            successMsg("任务组 " + val.groupName + " 新增成功", this);
            this.init();
          }
        });
      }
    },
    remove(rows) {
      apiFn()(jobgroupApi, "remove", { groupName: rows.groupName }, this).then(
        res => {
          if (res.successful) {
            successMsg("删除成功", this);
            this.init();
          }
        }
      );
    },
    checkInfo(rows) {
      this.changeRoute("nodeInfo", rows);
    },
    changeRoute(name, data) {
      this.$router.push({
        name: name,
        params: {
          breadcrumbItem: [
            { path: this.$route.fullPath, title: "job组管理", data: data }
          ]
        }
      });
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
