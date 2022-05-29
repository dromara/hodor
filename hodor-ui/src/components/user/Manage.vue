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
        >添加用户</el-button>
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
        :select-opt='{roleId:roleList}'
      />

    </el-dialog>
  </div>

</template>
<script>
import Table from "../common/Table";
import Search from "../common/Search";
import Forms from "../common/Forms";
import Pagination from "../common/Pagination";
import { userManage } from "@/assets/js/tableTitle.js";
import { successMsg, apiFn } from "@/assets/util";
import { userManageApi } from "@/api/userAction";
import { userRoleApi } from "@/api/userRole";
const myMixin = {
  data: function() {
    return {
      userManageApi,
      userRoleApi
    };
  }
};
export default {
  name: "Manage",
  mixins: [myMixin],
  data() {
    return {
      tableData: { tableTitle: userManage },
      tableList: [],
      roleList: [],
      pageSize: 10,
      currentPage: 1,
      total: 0,
      editType: "",
      dialogTableVisible: false,
      formVal: {},
      formOptions: {
        options: [
          { label: "用户名", key: "username", type: "input" },
          { label: "密码", key: "password", type: "password" },
          {
            label: "角色",
            key: "roleId",
            type: "select",
            show: "roleName",
            value: "id"
          }
        ]
      },
      rules: {
        username: [
          {
            required: true,
            message: "请输入用户名",
            trigger: "blur"
          },
          { validator: this.checkUserName, trigger: "blur" }
        ],
        password: [
          {
            required: true,
            message: "请输入密码",
            trigger: "blur"
          }
        ],
        roleId: [
          {
            required: true,
            message: "请选择角色",
            trigger: "change"
          }
        ]
      }
    };
  },
  components: { Table, Search, Forms, Pagination },
  created() {
    this.init();
    this.addOptions();
    this.getRoleList();
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
      apiFn()(userManageApi, "init", param, this).then(res => {
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
    getRoleList() {
      apiFn()(userRoleApi, "init", null, this).then(res => {
        if (res && res.successful) {
          this.roleList = res.rows;
        }
      });
    },
    addOptions() {
      this.tableData.operation = [];
      this.tableData.operation.push({
        label: "编辑",
        fn: this.edit
      });
    },
    checkUserName(rule, value, callback) {
      this.editType == "edit"
        ? callback()
        : apiFn()(
            userManageApi,
            "checkUserName",
            { username: value },
            this
          ).then(res => {
            if (res) {
              if (res.successful) {
                callback(new Error(value + "已经存在了！该字段必须唯一！！！"));
              } else {
                callback();
              }
            }
          });
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
    handleDialog(val) {
      this.dialogTableVisible = false;
      if (val) {
        let type = this.editType == "add" ? "addSave" : "update";
        apiFn()(userManageApi, type, { user: JSON.stringify(val) }, this).then(
          res => {
            let msg =
              this.editType == "add" ? val.username + "添加成功" : "修改成功";
            if (res.successful) {
              successMsg(msg, this);
              this.init();
            }
          }
        );
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

