<template>
  <div class="user-action-container">
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
  </div>

</template>
<script>
import Table from "../common/Table";
import Search from "../common/Search";
import Pagination from "../common/Pagination";
import { userAction } from "@/assets/js/tableTitle.js";
import { successMsg, apiFn } from "@/assets/util";
import { userActionApi } from "@/api/userAction";
const myMixin = {
  data: function() {
    return {
      userActionApi
    };
  }
};
export default {
  name: "Action",
  mixins: [myMixin],
  data() {
    return {
      tableData: { tableTitle: userAction },
      multipleSelection: [],
      tableList: [],
      pageSize: 10,
      currentPage: 1,
      total: 0
    };
  },
  components: { Table, Search, Pagination },
  created() {
    this.init();
  },
  mounted() {},
  methods: {
    init(val) {
      let param = {
        search: val,
        order: "asc",
        limit: this.pageSize,
        offset: (this.currentPage-1)*this.pageSize
      };
      apiFn()(userActionApi, "init", param, this).then(res => {
        if (res.successful) {
          this.tableList = res.rows;
          this.total = res.total;
        } else {
          this.tableList = [];
          this.total = 0;
        }
      });
    },
    onSearch(val) {
      this.init(val);
    },
    tableSelectionChange(val) {
      this.multipleSelection = val;
    }
  }
};
</script>
<style scoped>
</style>

