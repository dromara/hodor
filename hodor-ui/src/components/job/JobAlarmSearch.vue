<template>
  <div class="container">
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

  </div>
</template>

<script>
import Table from "../common/Table";
import Pagination from "../common/Pagination";
import { jobAlarmSearchApi } from "@/api/jobAlarmSearch";
import { jobAlarmSearch } from "@/assets/js/tableTitle.js";
import { saveBreadcrumb, getBreadcrumb } from "@/utils/breadcrumb";
import { successMsg, apiFn } from "@/assets/util";
const myMixin = {
  data: function() {
    return {
      jobAlarmSearchApi
    };
  }
};
export default {
  name: "JobAlarmSearch",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      tableData: {
        tableTitle: jobAlarmSearch,
      },
      tableList: [],
      pageSize: 10,
      currentPage: 1,
      total: 0
    };
  },
  created() {
    this.addOptions();
    this.init();
  },
  updated() {},
  mounted() {},
  components: { Table, Pagination },
  methods: {
    init() {
      let param = {
        limit: this.pageSize,
        offset: (this.currentPage - 1) * this.pageSize
      };

      let type = "init";
      apiFn()(jobAlarmSearchApi, type, param, this).then(res => {
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
      this.tableData.operation.push({ label: "删除", fn: this.remove });
    },
    remove(row) {
      this.$confirm("是否删除此条记录?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          apiFn()(jobAlarmSearchApi, "remove", { id: row.id }, this).then(
            res => {
              if (res && res.successful) {
                successMsg(res.msg, this);
                this.init();
              }
            }
          );
        })
        .catch(() => {});
    },


  },
  destroyed() {}
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
</style>
