<template>
  <div class="table-container">

    <el-table
      ref="singleTable"
      border
      :data="list"
      highlight-current-row
      style="width: 100%"
      row-key="id"
      tooltip-effect="dark"
      @selection-change="handleSelectionChange"
      :cell-class-name="tableRowClassName"
      @cell-dblclick='dblclickHandle'
    >
      <el-table-column
        type="selection"
        width="55"
        v-if='tableData.selection'
        align='center'
      >
      </el-table-column>
      <el-table-column
        v-for='(item,index) in tableData.tableTitle'
        :key='index'
        :prop="item.val"
        :label="item.label"
        :min-width="item.label=='id'?40:200"
      >
        <template slot-scope="scope">
          <div>
            <el-tooltip
              v-if='scope.row[item.val]&&scope.row[item.val].length>24'
              class="item"
              effect="light"
              :content="scope.row[item.val]"
              placement="top-end"
            >
              <span class='tableSpan'>{{formatter(scope.row[item.val],item.val)}}</span>
            </el-tooltip>
            <span v-else>{{formatter(scope.row[item.val],item.val)}}</span>
          </div>

        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        label="操作"
        align='center'
        min-width="150"
        v-if='tableData.operation&&tableData.operation.length>0'
      >
        <template slot-scope="scope">
          <div>
            <el-dropdown
              v-if='tableData.btnType=="dropdown"'
              size="mini"
              split-button
              type="primary"
              @command="(command)=>{tableData.fn(command,scope.row)}"
            >
              操作
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  :command="item.key"
                  v-for='(item,index) in tableData.operation'
                  v-if='item.flag?checkPause(item,scope.row):true'
                >{{item.label}}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <span
              v-else
              v-for='(item,index) in tableData.operation'
              :key='index'
            >
              <el-button
                size="mini"
                :type='item.label=="删除"||(item.status&&scope.row.status == "enable")?"danger":"primary"'
                @click="bindEvent(item.fn,scope.row)"
              >{{formatterBtn(scope.row,item,item.key)}}</el-button>&nbsp;
            </span>

          </div>
        </template>
      </el-table-column>
    </el-table>

  </div>
</template>

<script>
import { jobMessageApi } from "@/api/jobMessage";
import { successMsg, apiFn, getkeys, session } from "@/assets/util";
const myMixin = {
  data: function() {
    return {
      jobMessageApi
    };
  }
};
export default {
  mixins: [myMixin],
  name: "Table",
  props: {
    tableData: {
      type: Object,
      default: () => {
        return {};
      }
    },
    list: {
      type: Array,
      default: () => {
        return [];
      }
    }
  },
  data() {
    return {
      status: ["未激活", "正在运行", "正在运行", "暂停"]
    };
  },
  created() {},
  updated() {},
  mounted() {},
  components: {},
  methods: {
    formatter(cellValue, key) {
      if (key == "jobStatus") {
        return this.status[cellValue];
      }

      if (key == "isTest" || key == "killprocess" || key == "available") {
        return cellValue == 1 ? "是" : "否";
      }
      if (key == "currentStatus") {
        return this.setCurrentStatus(cellValue);
      }
      if (key == "executeStart") {
        if (cellValue == "1970-01-01 23:59:59.0") {
          return "";
        }
      }
      if (key == "executeEnd") {
        if (cellValue == "1970-01-01 23:59:59.0") {
          return "";
        }
      }
      return typeof cellValue === "boolean"
        ? cellValue
          ? "是"
          : "否"
        : cellValue == null || cellValue == ""
        ? typeof cellValue == "number"
          ? cellValue
          : "-"
        : cellValue;
    },
    checkPause(item, row) {
      if (row.uuid) {
        /*if ((row.executeEnd == null || row.executeEnd == "1970-01-01 23:59:59.0") && row.currentStatus == 2) {*/
        if (row.currentStatus == 2 || row.currentStatus == 4 || row.currentStatus == 7) {
          if (
            item.key == "syncState" ||
            item.key == "killJob" ||
            item.key == "pauseExecute"
          ) {
            return true;
          } else {
            return false;
          }
        } else {
          return row.currentStatus != 2&&item.key=='rerun'?true:false
        }
      } else {
        if (
          (row.jobStatus > 2 && item.key == "resumeJob") ||
          (row.jobStatus < 3 && item.key == "pauseJob")
        ) {
          return true;
        } else {
          return false;
        }
      }
    },
    setCurrentStatus(val) {
      switch (val) {
        case "7":
          return "超时";
          break;
        case "6":
          return "PAUSED";
          break;
        case "5":
          return "DISABLE";
          break;
        case "4":
          return "就绪";
          break;
        case "3":
          return "KILLED";
          break;
        case "2":
          return "执行中";
          break;
        case "1":
          return "成功";
          break;
        case "0":
          return "失败";
          break;
        default:
          return "未知";
          break;
      }
    },
    formatterBtn(row, item) {
      if (item.status == "status") {
        return row.status == "disable" ? "启动" : "禁用";
      } else {
        return item.label;
      }
    },
    bindEvent(cb, param) {
      return cb(param);
    },
    handleSelectionChange(val) {
      this.$emit("selection", val);
    },
    tableRowClassName({ row, column, rowIndex, columnIndex }) {
      if (
        (row.jobStatus == 3 && column.property == "jobStatus") ||
        (row.currentStatus == 0 && column.property == "currentStatus")
      ) {
        return "faild-row";
      }
      return "";
    },
    dblclickHandle(row, column, cell, event) {
      if (this.tableData.dblclick) {
        apiFn()(
          jobMessageApi,
          "getJobInfo",
          { groupName: row.groupName, jobName: row.jobName },
          this
        ).then(res => {
          if (res && res.successful) {
            this.$router.push({
              name: "addJob",
              params: {
                breadcrumbItem: [
                  {
                    path: this.$route.fullPath,
                    title: "job信息管理",
                    data: res.data
                  }
                ]
              }
            });
          }
        });
      }
    }
  },
  destroyed() {}
};
</script>
<style>
.table-container .tableSpan {
  display: inline-block;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.table-container .el-button + .el-button {
  margin-left: 0px;
}
.table-container .faild-row {
  background-color: #f56c6c;
}
</style>
