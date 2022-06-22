<template>
  <div class="user-action-container">
    <el-row>
      <el-col
        :span="2"
        class="addBtn"
      >
        <el-button
          type="primary"
          size="small"
          @click='addNew'
        >添加角色</el-button>
      </el-col>
      <el-col :span='22'>
        <Search @search='onSearch' />
      </el-col>

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
      :title="editType=='add'?'添加用户':'修改用户'"
      :visible.sync="dialogTableVisible"
    >
      <Forms
        ref='formsComponent'
        :form-options='formOptions'
        :rules='rules'
        :show='true'
        :edit-type='editType'
        :form-val='formVal'
        @handledialog='handleDialog'
      />

    </el-dialog>
  </div>

</template>
<script>
import Table from "../common/Table";
import Search from "../common/Search";
import Forms from "../common/Forms";
import Pagination from "../common/Pagination";
import { userRole } from "@/assets/js/tableTitle.js";
import { successMsg, apiFn } from "@/assets/util";
import { userRoleApi } from "@/api/userRole";
const myMixin = {
  data: function() {
    return {
      userRoleApi
    };
  }
};
export default {
  name: "Manage",
  mixins: [myMixin],
  data() {
    return {
      tableData: { tableTitle: userRole },
      tableList: [],
      pageSize: 10,
      currentPage: 1,
      total: 0,
      editType: "",
      dialogTableVisible: false,
      formVal: {},
      formOptions: {
        options: [
          { label: "角色名称", key: "roleName", type: "input" },
          { label: "描述", key: "description", type: "textarea" }
        ]
      },
      rules: {
        roleName: [
          {
            required: true,
            message: "请输入角色",
            trigger: "blur"
          },
          { validator: this.checkExtrName, trigger: "blur" }
        ],
        description: [
          {
            required: true,
            message: "请输入描述信息",
            trigger: "blur"
          }
        ]
      }
    };
  },
  components: { Table, Search, Forms, Pagination },
  created() {
    this.init();
    this.addOptions();
  },
  watch: {},
  mounted() {},
  computed: {},
  methods: {
    init(val) {
      let param = {
        search: val,
        limit: this.pageSize,
        offset: (this.currentPage - 1) * this.pageSize
      };
      apiFn()(userRoleApi, "init", param, this).then(res => {
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
    addOptions() {
      this.tableData.operation = [];
      let opts = [
        // {
        //   label: "编辑",
        //   fn: this.edit
        // },
        {
          label: "删除",
          fn: this.remove
        }
      ];
      this.tableData.operation = opts;
    },
    checkExtrName(rule, value, callback) {
      this.editType == "edit"
        ? callback()
        : apiFn()(userRoleApi, "extName", { roleName: value }, this).then(
            res => {
              if (res && typeof res == "boolean") {
                callback(new Error(value + "已经存在了！该字段必须唯一！！！"));
              } else {
                callback();
              }
            }
          );
    },
    addNew() {
      this.editType = "add";
      this.dialogTableVisible = true;
      this.formVal = {};
    },
    edit(row) {
      this.editType = "edit";
      this.dialogTableVisible = true;
      this.formVal = row;
    },
    remove(row) {
      this.$confirm("此操作将删除此条数据, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          apiFn()(userRoleApi, "remove", { id: row.id }).then(res => {
            if (res && res.successful) {
              successMsg("删除成功", this);
              this.init();
            }
          });
        })
        .catch(() => {});
    },
    handleDialog(val) {
      this.dialogTableVisible = false;
      if (val) {
        let type = this.editType == "add" ? "addRole" : "update";
        apiFn()(userRoleApi, type, val, this).then(res => {
          let msg =
            this.editType == "add" ? val.roleName + "添加成功" : "修改成功";
          if (res.successful) {
            successMsg(msg, this);
            this.init();
          }
        });
      }
    }
  }
};
</script>
<style scoped>
.user-action-container .addBtn {
  text-align: center;
}
</style>

