<template>
  <div class="user-action-container">
    <el-row>
      <el-card class="box-card">
        <div
          slot="header"
          class="clearfix"
        >
          <span>报警配置查看</span>
        </div>
        <Table
          :tableData='tableData'
          :list='tableList'
        />
        <Pagination
          :pageSize.sync='pageSize'
          :currentPage.sync='currentPage'
          :total='total'
          @init='init'
        />
      </el-card>
    </el-row><br>
    <el-row>
      <el-card class="box-card">
        <div
          slot="header"
          class="clearfix"
        >
          <span>一键配置报警</span>
        </div>
        <Forms
          ref='formsComponent'
          :form-options='formOptions'
          :rules='rules'
          :edit-type='editType'
          :form-val='formVal'
          :select-opt='{jobGroup:groupList,pointCode:errCodeList}'
        />
        <div class="align-center">
          <el-button
            type="primary"
            size="small"
            @click="save"
          >保存</el-button>
        </div>
      </el-card>
    </el-row><br>

  </div>

</template>
<script>
  import Table from "../common/Table";
  import Forms from "../common/Forms";
  import Pagination from "../common/Pagination";
  import { configMonitorApi } from "@/api/tool";
  import { rmsConfig } from "@/assets/js/tableTitle.js";
  import { successMsg, apiFn } from "@/assets/util";
  const myMixin = {
    data: function() {
      return {
        configMonitorApi
      };
    }
  };
  export default {
    name: "ConfigMonitor",
    mixins: [myMixin],
    data() {
      return {
        tableData: {
          tableTitle: rmsConfig
        },
        tableList: [],
        pageSize: 10,
        currentPage: 1,
        total: 0,
        editType: "",
        showWork: true,
        groupList: [],
        formVal: {},
        errCodeList: [],
        formOptions: {
          options: [
            { label: "项目编码", key: "projectCode", type: "input", spans: 10 },
            { label: "任务组", key: "jobGroup", type: "select", spans: 10 },
            {
              label: "项目编码描述",
              key: "description",
              type: "input",
              spans: 10
            },
            { label: "token值", key: "token", type: "input", spans: 10 },
            {
              label: "监控点编码",
              key: "pointCode",
              type: "select",
              spans: 10,
              show: "pointCode",
              value: "pointCode",
              preShow: "description"
            }
          ]
        },
        rules: {
          projectCode: [
            {
              required: true,
              message: "请输入项目编码",
              trigger: "blur"
            }
          ],
          jobGroup: [
            {
              required: true,
              message: "请选择项目组",
              trigger: "change"
            }
          ],
          description: [
            {
              required: true,
              message: "请输入项目编码描述",
              trigger: "blur"
            }
          ],
          token: [
            {
              required: true,
              message: "请输入token值",
              trigger: "blur"
            }
          ],
          pointCode: [
            {
              required: true,
              message: "请选择监控点编码",
              trigger: "change"
            }
          ]
        }
      };
    },
    components: { Table, Forms, Pagination },
    created() {
      this.initErrCode();
      this.init(null);
      this.getGroupList();
    },
    watch: {},
    mounted() {},
    computed: {},
    methods: {
      init() {
        let param = {
          limit: this.pageSize,
          offset: (this.currentPage - 1) * this.pageSize
        };
        apiFn()(configMonitorApi, "getRmsGroupConfigInfoPage", param, this, true).then(res => {
          if (res.successful) {
            if (Object.prototype.toString.call(res) === "[object Array]") {
              this.tableList = res;
              this.total = res.length;
            } else {
              this.tableList = res.rows;
              this.total = res.total;
            }
          } else {
            this.tableList = [];
            this.total = 0;
          }
        });
      },
      initErrCode(val) {
        apiFn()(configMonitorApi, "getAllRmsErrorCodes", null, this).then(res => {
          if (res.successful) {
            this.errCodeList = res.data;
          } else {
            this.errCodeList = [];
          }
        });
      },
      getGroupList() {
        apiFn()(configMonitorApi, "getGroup", null, this).then(res => {
          if (Object.prototype.toString.call(res) == "[object Array]") {
            this.groupList = res;
          } else {
            this.groupList = [];
          }
        });
      },
      save() {
        if (this.$refs.formsComponent.validate("forms")) {
          let param = this.$refs.formsComponent.forms;
          apiFn()(
            configMonitorApi,
            "save",
            { alarmConfiguration: JSON.stringify(param) },
            this
          ).then(res => {
            if (res.successful) {
              successMsg("添加成功", this);
            }
          });
        }
      },
      upAndDown(type) {
        this[type] = !this[type];
      }
    }
  };
</script>
<style scoped>
.user-action-container .addBtn {
  text-align: center;
}
.user-action-container .align-center {
  text-align: center;
}
</style>

