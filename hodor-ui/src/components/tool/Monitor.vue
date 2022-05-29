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
        >新增监控项</el-button>
      </el-col>

    </el-row><br>
    <el-row>
      <Table
        :tableData='tableData'
        :list='tableList'
      />
    </el-row>

    <el-dialog
      :title="editType=='add'?'新增监控项':'修改监控项'"
      :visible.sync="dialogTableVisible"
    >
      <Forms
        ref='formsComponent'
        :form-options='formOptions'
        :rules='rules'
        :show='true'
        :edit-type='editType'
        :form-val='formVal'
        :select-opt='{available:isAvailable,isTest:isTest}'
        @handledialog='handleDialog'
      />

    </el-dialog>
  </div>

</template>
<script>
import Table from "../common/Table";
import Forms from "../common/Forms";
import { toolMonitor } from "@/assets/js/tableTitle.js";
import { successMsg, apiFn } from "@/assets/util";
import { monitorApi } from "@/api/tool";
const myMixin = {
  data: function() {
    return {
      monitorApi
    };
  }
};
export default {
  name: "Monitor",
  mixins: [myMixin],
  data() {
    var validateLength = function(rule, value, callback) {
      if (value.length < 7) {
        callback(new Error("最少 6 个字"));
      } else {
        callback();
      }
    };
    return {
      tableData: { tableTitle: toolMonitor },
      tableList: [],
      pageSize: 10,
      currentPage: 0,
      total: 0,
      editType: "",
      dialogTableVisible: false,
      isAvailable: [
        { label: "可用", val: true },
        { label: "不可用", val: false }
      ],
      isTest: [{ label: "是", val: 1 }, { label: "否", val: 0 }],
      formVal: {},
      formOptions: {
        options: [
          { label: "错误编码", key: "errorCode", type: "input" },
          { label: "项目编码描述", key: "description", type: "input" },
          { label: "监控点编码", key: "pointCode", type: "input" },
          { label: "触发报警的条件", key: "sendCondition", type: "input" },
          { label: "是否可用", key: "available", type: "radio" },
          { label: "是否自检", key: "isTest", type: "radio" },
          { label: "自检关联的项目", key: "testProjectCode", type: "input" }
        ]
      },
      rules: {
        errorCode: [
          {
            required: true,
            message: "请输入错误编码",
            trigger: "blur"
          }
        ],
        description: [
          {
            required: true,
            message: "请输入项目编码描述",
            trigger: "blur"
          }
        ],
        pointCode: [
          {
            required: true,
            message: "请输入监控点编码",
            trigger: "blur"
          }
        ],
        sendCondition: [
          {
            required: true,
            message: "请输入触发报警的条件",
            trigger: "blur"
          }
        ],
        available: [
          {
            required: true,
            message: "请选择是否可用",
            trigger: "change"
          }
        ],
        isTest: [
          {
            required: true,
            message: "请选择是否自检",
            trigger: "change"
          }
        ],
        testProjectCode: [
          {
            required: true,
            message: "请输入自检关联的项目",
            trigger: "blur"
          },
          { validator: validateLength, trigger: "blur" }
        ]
      }
    };
  },
  components: { Table, Forms },
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
        order: "asc"
      };
      apiFn()(monitorApi, "init", param, this).then(res => {
        if (res.successful) {
          this.tableList = res.data;
        } else {
          this.tableList = [];
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
        apiFn()(
          monitorApi,
          type,
          { dBMonitorErrorInfoDTOStr: JSON.stringify(val) },
          this
        ).then(res => {
          let msg = this.editType == "add" ? "添加成功" : "修改成功";
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

