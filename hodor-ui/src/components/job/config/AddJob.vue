<template>
  <div class="addJob-container">
    <Breadcrumb
      :breadcrumb='breadcrumb'
      :title='title'
    />

    <el-row class="formWrap">
      <el-card class="box-card">
        <div
          slot="header"
          class="clearfix"
        >
          <span>作业详细信息：</span>
          <span
            @click="upAndDown('showWork')"
            class="upAndDown"
          >
            <el-tooltip
              class="item"
              effect="dark"
              content="收起"
              placement="top-start"
              v-show='showWork'
            >
              <i class="el-icon-arrow-up  cursor-p"></i>
            </el-tooltip>
            <el-tooltip
              class="item"
              effect="dark"
              content="展开"
              v-show='!showWork'
              placement="top-start"
            >
              <i class="el-icon-arrow-down  cursor-p"></i>
            </el-tooltip>
          </span>
        </div>
        <div v-show='showWork'>
          <Forms
            ref='formComponent'
            :form-options='formOptions'
            :rules='rules'
            :edit-type='editType'
            :form-val='formVal'
            :file.sync='file'
            :show-corninput='showCorninput'
            :select-opt='{groupName:groupList,bin:jobTypeList,postjobs:jobList,checkFire:checkLabel,priority:priorityList}'
          />

        </div>

      </el-card>

    </el-row>
    <br />
    <el-row class="userForms">
      <el-card class="box-card">
        <div
          slot="header"
          class="clearfix"
        >
          <span>人员信息：</span>
          <span
            @click="upAndDown('showUser')"
            class="upAndDown"
          >
            <el-tooltip
              class="item"
              effect="dark"
              content="收起"
              placement="top-start"
              v-show='showUser'
            >
              <i class="el-icon-arrow-up  cursor-p"></i>
            </el-tooltip>
            <el-tooltip
              class="item"
              effect="dark"
              content="展开"
              v-show='!showUser'
              placement="top-start"
            >
              <i class="el-icon-arrow-down cursor-p"></i>
            </el-tooltip>
          </span>
        </div>
        <div v-show='showUser'>
          <UserConfig
            ref='userComponent'
            :values='userForm'
          />
        </div>

      </el-card>

    </el-row>
    <br />
    <el-row
      class="cornCard"
      v-if='!breadcrumb.jobMessage&&showCornTime'
    >
      <el-card class="box-card">
        <div
          slot="header"
          class="clearfix"
        >
          <span>执行时间：</span>
          <span
            @click="upAndDown('showCorn')"
            class="upAndDown"
          >
            <el-tooltip
              class="item"
              effect="dark"
              content="收起"
              placement="top-start"
              v-show='showCorn'
            >
              <i class="el-icon-arrow-up  cursor-p"></i>
            </el-tooltip>
            <el-tooltip
              class="item"
              effect="dark"
              content="展开"
              v-show='!showCorn'
              placement="top-start"
            >
              <i class="el-icon-arrow-down cursor-p"></i>
            </el-tooltip>
          </span>
        </div>
        <div v-show='showCorn'>
          <CornConfig
            ref='cornComponent'
            :corn-value='cronExpressionVal'
          />
        </div>

      </el-card>

    </el-row>
    <div class="saveDiv">
      <el-button
        type="primary"
        size="small"
        @click="save"
      >保存</el-button>
    </div>

  </div>
</template>

<script>
import Forms from "../../common/Forms";
import CornConfig from "./CornConfig";
import Breadcrumb from "../../common/Breadcrumb";
import UserConfig from "./UserConfig";
import { addJobApi } from "@/api/addJob";
import { saveBreadcrumb, getBreadcrumb } from "@/utils/breadcrumb";
import { successMsg, apiFn, sNameRule } from "@/assets/util";
const myMixin = {
  data: function() {
    return {
      addJobApi
    };
  }
};
export default {
  name: "AddJob",
  mixins: [myMixin],
  props: {},
  data() {
    return {
      title: "作业详细配置",
      routeItem: [],
      breadcrumb: {},
      jobTypeList: [],
      groupList: [],
      jobList: [],
      editType: "new",
      userForm: null,
      file: null,
      turnweixin: false,
      showCornTime: true,
      showWork: true,
      showCorn: true,
      showUser: true,
      typeEdit: false,
      showCorninput: true,
      cronExpressionVal: {},
      formVal: {
        jobName: "",
        groupName: "",
        bin: "php",
        timeOut: "",
        count: "0",
        desc: "",
        category: "",
        filePath: "",
        className: "",
        methodName: "",
        cronExpression: "",
        slaveIp: "",
        jobPath: "",
        parameters: "",
        endTime: "",
        postjobs: [],
        priority: 5,
        targetIps: "",
        misfire:false,
        ifBroadcast:false,
        fireNow:false
      },
      priorityList: [
        { label: "默认", value: 5 },
        { label: "中级", value: 10 },
        { label: "高级", value: 50 },
        { label: "最高级", value: 100 }
      ],
      checkLabel: [
        { label: "错过马上执行", value: "misfire" },
        { label: "马上调度", value: "fireNow" },
        { label: "广播模式", value: "ifBroadcast" }
      ],
      formOptions: {
        options: [
          {
            label: "任务名称",
            key: "jobName",
            type: "input",
            jobName: "",
            spans: 10,
            info: "注意:只支持字母、数字、下划线"
          },
          {
            label: "任务组",
            key: "groupName",
            type: "select",
            groupName: "",
            spans: 10
          },
          {
            label: "任务分类",
            key: "category",
            type: "input",
            category: "",
            spans: 10,
            info: "分类便于您管理任务"
          },
          {
            label: "文件路径",
            key: "filePath",
            type: "input",
            filePath: "",
            spans: 10
          },
          {
            label: "cron时间表达式",
            key: "cronExpression",
            type: "input",
            cronExpression: "",
            spans: 10,
            ifShow: true
          },
          {
            label: "类名",
            key: "className",
            type: "input",
            className: "",
            spans: 10
          },
          {
            label: "方法名",
            key: "methodName",
            type: "input",
            methodName: "",
            spans: 10
          },
          /*{
            label: "任务调用路径",
            key: "jobPath",
            type: "input",
            jobPath: "",
            spans: 10
          },*/
          {
            label: "指定执行节点",
            key: "targetIps",
            type: "input",
            targetIps: "",
            spans: 10,
            placeholder: "填写格式：ip:port,多个可用逗号分隔"
          },

          {
            label: "超时时间(秒)",
            key: "timeOut",
            type: "input",
            timeOut: "",
            spans: 10,
            placeholder: "请根据任务具体执行情况设置超时时间"
          },
          {
            label: "任务结束时间",
            key: "endTime",
            type: "time",
            endTime: "",
            spans: 10
          },
          {
            label: "任务调用次数",
            key: "count",
            type: "input",
            count: "",
            spans: 10,
            info: "如果设置了执行任务结束时间，则以任务结束时间为准!"
          },
          {
            label: "后置任务",
            key: "postjobs",
            type: "select",
            postjobs: "",
            spans: 10,
            show: "jobName",
            valuePre: "groupName",
            value: "jobName",
            multiple: true
          },

          {
            label: "任务类型",
            key: "bin",
            type: "autocomplete",
            bin: "",
            spans: 10
          },
          {
            label: "任务优先级",
            key: "priority",
            type: "select",
            priority: "",
            spans: 10,
            url: "",
            show: "label",
            value: "value"
          },
           {
            label: "",
            key: "checkFire",
            type: "checkbox",
            checkFire: "",
            spans: 10,
            valueType: "array"
          },
          {
            label: "上传文件",
            key: "file",
            type: "upload",
            file: "",
            spans: 10,
            url: ""
          },

          {
            label: "任务参数",
            key: "parameters",
            type: "textarea",
            parameters: "",
            spans: 20
          },
          {
            label: "任务描述",
            key: "desc",
            type: "textarea",
            desc: "",
            spans: 20
          }
        ]
      },
      forms: {},
      rules: {
        jobName: [
          {
            required: true,
            message: "请填写任务名称",
            trigger: "blur"
          },
          { validator: sNameRule, trigger: "blur" }
        ],
        groupName: [
          {
            required: true,
            message: "请选择任务组名称",
            trigger: "change"
          }
        ],
        bin: [
          {
            required: true,
            message: "请选择任务类型",
            trigger: "change"
          }
        ],
        timeOut: [
          {
            required: true,
            message: "请填写任务超时时间",
            trigger: "blur"
          }
        ]
      }
    };
  },
  created() {
    this.saveBreadcrumb();
    this.getJobListByGroupName();
    this.getJobTypeList();
    this.getGroupList();
  },
  watch: {},
  updated() {},
  mounted() {},
  components: { Forms, CornConfig, Breadcrumb, UserConfig },
  methods: {
    saveBreadcrumb() {
      if (this.$route.params.breadcrumbItem) {
        this.breadcrumb = this.$route.params;
        saveBreadcrumb(this.$route.params);
      } else {
        this.breadcrumb = getBreadcrumb(this.routeItem);
        this.breadcrumb = this.breadcrumb
          ? this.breadcrumb
          : { breadcrumbItem: [] };
      }
      let data =
        this.breadcrumb.breadcrumbItem.length > 0
          ? this.breadcrumb.breadcrumbItem[0].data
          : null;
      if (data) {
        this.typeEdit = true;
        this.showCorninput = false;
        this.formVal = this.setFormVal(data);
        data.taskReport
          ? (this.userForm = JSON.parse(data.taskReport))
          : (this.userForm = null);
      }
      this.formVal.checkFire = [];
      this.formVal.misfire ? this.formVal.checkFire.push("misfire") : "";
      this.formVal.ifBroadcast
        ? this.formVal.checkFire.push("ifBroadcast")
        : "";
    },
    setFormVal(data) {
      let obj = {};
      for (let key in this.formVal) {
        obj[key] = data[key];
      }
      obj.file = [];
      data.filePath
        ? obj.file.push({ name: data.filePath, url: data.filePath })
        : "";
      obj.priority = data.priority ? data.priority : 5;
      data.cronExpression
        ? this.transferFromCron(data.cronExpression)
        : (this.cronExpressionVal = { chooseSpecial: "default" });
      return obj;
    },
    transferFromCron(corn) {
      apiFn()(
        addJobApi,
        "transferFromCron",
        { cronExpression: corn },
        this
      ).then(res => {
        if (res && res.successful) {
          this.cronExpressionVal = res.data;
          this.showCornTime = true;
        } else {
          this.showCornTime = false;
        }
        // if (res && Object.prototype.toString.call(res) == "[object Object]") {
        //   this.cronExpressionVal = res;
        // }
      });
    },
    getJobTypeList() {
      apiFn()(addJobApi, "getJobTypeList", null, this).then(res => {
        if (res.successful) {
          this.jobTypeList = res.data;
        }
      });
    },
    getGroupList() {
      apiFn()(addJobApi, "getGroupList", null, this).then(res => {
        if (Object.prototype.toString.call(res) == "[object Array]") {
          this.groupList = res;
        }
      });
    },
    getJobListByGroupName() {
      apiFn()(addJobApi, "getJobListByGroupName", null, this).then(res => {
        if (res.successful) {
          this.jobList = res.data;
        }
      });
    },
    cornValidate(cornForm) {
      let flag = true;
      if (
        cornForm.days &&
        cornForm.days !== "false" &&
        cornForm.days.length > 0 &&
        cornForm.weekdays &&
        cornForm.weekdays.length > 0
      ) {
        this.$message.warning("不可以同时指定天和周");
        flag = false;
      }
      return flag;
    },
    setFalse(form) {
      this.checkLabel.forEach(ele => {
        form[ele.value] = false;
      });
    },
    resetCheckFire(form, data) {
      //先设置为默认值
      //misfire:false,
      //ifBroadcast:false,
      //fireNow:false

      //form["misfire"] = false;
      //form["ifBroadcast"] = false;
      //form["fireNow"] = false;

      this.setFalse(form);
      if (form.checkFire.length > 0) {
        data.forEach(ele => {
          form[ele] = true;
        });
      }

      return form;
    },
    removeFalse(obj) {
      for (let key in obj) {
        if (obj[key] == "false") {
          delete obj[key];
        }
      }
      return obj;
    },
    save() {
      let param = {};
      let self = this;
      let formsFlag = this.$refs.formComponent.validate("forms");
      let userFormFlag = this.$refs.userComponent.validateUserforms();
      if (this.breadcrumb.jobMessage) {
        param = this.setSaveParam(null);
        if (!formsFlag || !userFormFlag) {
          return;
        }
      } else {
        let cornForm = this.$refs.cornComponent
          ? this.$refs.cornComponent.cornForm
          : null;
        if (
          cornForm &&
          cornForm.chooseSpecial == "customize" &&
          cornForm.customizeVal == null
        ) {
          this.$message.warning("未输入cron时间表达式");
          return;
        }
        param = this.setSaveParam(cornForm);
        let corns = cornForm ? this.removeFalse(cornForm) : "";
        let cornFlag = cornForm ? this.cornValidate(corns) : true;
        this.showTips(formsFlag, "showWork");
        this.showTips(cornFlag, "showCorn");
        this.showTips(userFormFlag, "showUser");
        if (!formsFlag || !cornFlag || !userFormFlag) {
          return;
        }
      }
      apiFn()(addJobApi, "addSave", param, this).then(res => {
        if (res.successful) {
          successMsg("job配置成功", this);
          self.$router.replace("/jobmessage");
        }
      });
    },
    showTips(flag, type) {
      flag ? "" : (this[type] = true);
    },
    setSaveParam(cornForm) {
      let param = {};
      let file = this.file;
      let forms = this.$refs.formComponent.forms;
      let userForm = this.$refs.userComponent.userForm;
      forms.checkFire = forms.checkFire ? forms.checkFire : [];
      forms = this.resetCheckFire(forms, forms.checkFire);
      delete forms.checkFire;
      let method = this.typeEdit
        ? this.breadcrumb.clone
          ? "addNew"
          : "edit"
        : "addNew";
      /*let cornFormfy = null;
      if (cornForm) {
        cornFormfy =
          cornForm.chooseSpecial == "default" ? null : JSON.stringify(cornForm);
      }*/

      let cornFormfy = JSON.stringify(cornForm);
      let formsfy = JSON.stringify(forms);
      let userFormfy = JSON.stringify(userForm);
      param = new FormData();
      file ? param.append("file", file.raw) : param.append("file", undefined);
      param.append("job", formsfy);
      cornForm ? param.append("cron", cornFormfy) : "";
      param.append("method", method);
      param.append("taskReportDto", userFormfy);
      return param;
    },

    upAndDown(type) {
      this[type] = !this[type];
    }
  },
  destroyed() {
    localStorage.removeItem("$routeParams");
  }
};
</script>
<style>
.addJob-container .formWrap {
  margin-top: 25px;
}
.addJob-container .saveDiv {
  text-align: center;
}
.addJob-container .cornCard {
  margin: 6px 0px;
}
.addJob-container .upAndDown {
  float: right;
}
.addJob-container .upAndDown i {
  color: #42b983;
  font-weight: 700;
  font-size: 20px;
}
</style>
