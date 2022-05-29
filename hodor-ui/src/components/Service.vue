<template>
  <div class="container">
    <el-row>
      <Search @search='onSearch' />
    </el-row><br>
    <el-row>
      <el-col :span="6">
        <el-button
          type="primary"
          @click='divideJob'
          size="small"
        >均分任务到集群</el-button>
        <el-button
          type="primary"
          size="small"
          @click='currentRoot'
        >当前集群的根节点：/hodor</el-button>
      </el-col>
      <el-col :span="2">
        <el-button
          type="warning"
          size="small"
          @click='resetTimeout'
        >重置调度过期时间</el-button>
      </el-col>
      <el-col :span="3">
        <el-input
          placeholder="请输入过期时间，单位秒"
          size='small'
          v-model="timeOut"
        >
        </el-input>

      </el-col>
      <el-col :span="1">
        <span class="secSpan">秒</span>

      </el-col>

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
      :title="activeIp+'-任务信息列表'"
      :visible.sync="dialogTableVisible"
      width='50%'
    >
      <el-table
        :data="infoList"
        border
      >
        <el-table-column
          property="group"
          label="组名"
        ></el-table-column>
        <el-table-column
          property="name"
          label="任务名"
        ></el-table-column>

      </el-table>
      <el-row>
        <Pagination
          :pageSize.sync='dialogPageSize'
          :currentPage.sync='dialogCurrentPage'
          :total='dialogTotal'
          @init='getInfoData'
        />
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import Table from "./common/Table";
import Search from "./common/Search";
import Pagination from "./common/Pagination";
import { serviceApi } from "@/api/service";
import { service } from "@/assets/js/tableTitle.js";
import { successMsg, apiFn } from "@/assets/util";
const myMixin = {
  data: function() {
    return {
      serviceApi
    };
  }
};
export default {
  name: "Service",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      tableData: { tableTitle: service },
      tableList: [],
      pageSize: 10,
      currentPage: 1,
      total: 0,
      dialogPageSize: 10,
      dialogCurrentPage: 1,
      dialogTotal: 0,
      searchVal: "",
      activeIp: "",
      timeOut: "",
      dialogTableVisible: false,
      infoList: []
    };
  },
  created() {
    this.init();
    this.addOptions();
  },
  updated() {},
  mounted() {
    this.getSechedulerTimeOut();
  },
  components: { Table, Search, Pagination },
  methods: {
    getSechedulerTimeOut() {
      apiFn()(serviceApi, "getSechedulerTimeOut", null, this).then(res => {
        if (res && res.successful) {
          this.timeOut = res.data;
        }
      });
    },
    resetTimeout() {
      let time = this.timeOut.trim();
      time = Number(time);
      if (isNaN(time) || this.timeOut == "") {
        this.$message.warning("过期时间需要填入数字");
        return;
      }

      apiFn()(
        serviceApi,
        "saveSechedulerTimeOut",
        { timeOut: time + "" },
        this
      ).then(res => {
        if (res && res.successful) {
          successMsg(res.msg, this);
        }
      });
    },
    init(name) {
      let param = {
        name: name ? name : "",
        limit: this.pageSize,
        offset: (this.currentPage - 1) * this.pageSize
      };

      let type = "init";
      apiFn()(serviceApi, type, param, this).then(res => {
        if (res) {
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
      this.tableData.operation.push({ label: "查看详情", fn: this.checkInfo });
    },
    divideJob() {
      apiFn()(serviceApi, "divideJob", null, this).then(res => {
        if (res) {
          successMsg(res.msg, this);
        }
      });
    },
    currentRoot() {},
    checkInfo(rows) {
      this.activeIp = rows.ip;
      this.dialogTableVisible = true;
      this.getInfoData();
    },
    getInfoData() {
      let param = {
        ip: this.activeIp,
        limit: this.dialogPageSize,
        offset: (this.dialogCurrentPage - 1) * this.dialogPageSize
      };
      apiFn()(serviceApi, "checkInfo", param, this).then(res => {
        if (res) {
          this.infoList = res.rows;
          this.dialogTotal = res.total;
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
<style scoped>
.secSpan {
  line-height: 32px;
  padding: 5px;
}
</style>
