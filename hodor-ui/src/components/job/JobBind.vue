<template>
  <div class="container">
    <el-row>
      <Search @search='onSearch' />
    </el-row><br>
    <el-row>
      <el-button
        type="primary"
        @click="refresh"
        size="small"
      >刷新</el-button>
      <el-button
        type="primary"
        @click='binding("bind")'
        size="small"
        :disabled="multipleSelection.length<1||sessions.length<1"
      >绑定</el-button>
      <el-button
        type="primary"
        size="small"
        @click='binding("reBind")'
        :disabled="multipleSelection.length<1||sessions.length<1"
      >解绑再绑定</el-button>

      <div class='filterableSelect'>
        <el-select
          class="selectStyle"
          v-model="sessions"
          filterable
          multiple
          placeholder="请选择会话"
        >
          <el-option
            v-for="(item,index) in sessionList"
            :key="index"
            :label="item"
            :value="item"
          >
          </el-option>
        </el-select>
      </div>
    </el-row>
    <br>
    <el-row>
      <Table
        :tableData='tableData'
        :list='tableList'
        @selection='tableSelectionChange'
      />
    </el-row>

  </div>
</template>

<script>
import Table from "../common/Table";
import Search from "../common/Search";
import { jobBindApi } from "@/api/jobBind";
import { jobBind } from "@/assets/js/tableTitle.js";
import { successMsg, apiFn } from "@/assets/util";
const myMixin = {
  data: function() {
    return {
      jobBindApi
    };
  }
};
export default {
  name: "Service",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      tableData: { tableTitle: jobBind, selection: true },
      multipleSelection: [],
      tableList: [],
      pageSize: 10,
      offset: 0,
      searchVal: "",
      sessionList: [],
      sessions: []
    };
  },
  created() {
    this.init();
    this.addOptions();
    this.initSesstion();
  },
  updated() {},
  mounted() {},
  components: { Table, Search },
  methods: {
    init(name) {
      this.sessions=[]
      let param = {
        order: "asc",
        search: name ? name : ""
      };

      let type = "init";
      apiFn()(jobBindApi, type, param, this).then(res => {
        if (Object.prototype.toString.call(res) === "[object Array]") {
          this.tableList = res;
        } else {
          this.tableList = [];
        }
      });
    },
    initSesstion() {
      apiFn()(jobBindApi, "getBindSession", null, this).then(res => {
        if (Object.prototype.toString.call(res) === "[object Array]") {
          this.sessionList = res;
        } else {
          this.sessionList = [];
        }
      });
    },
    addOptions() {
      this.tableData.operation = [];
    },
    binding(type) {
      let param = {
        sessions: JSON.stringify(this.sessions),
        groupNames: JSON.stringify(this.getGroupList())
      };
      this.bindApi(type, param);
    },
    bindApi(type, param) {
      apiFn()(jobBindApi, type, param, this).then(res => {
        if (res.successful) {
          successMsg("绑定成功", this);
          this.refresh();

        }
      });
    },
    getGroupList() {
      return this.multipleSelection.map(ele => {
        return ele.groupName;
      });
    },
    tableSelectionChange(val) {
      this.multipleSelection = val;
    },
    checkInfo(rows) {
      console.log(rows);
    },
    onSearch(val) {
      this.init(val);
    },
    refresh() {
      this.init();
      this.initSesstion();
    }
  },
  destroyed() {}
};
</script>
<style>
.filterableSelect {
  padding-top: 6px;
}
.filterableSelect .selectStyle {
  width: 50%;
}
</style>
