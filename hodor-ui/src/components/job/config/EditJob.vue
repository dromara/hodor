<template>
  <div class="editJob-container">

    <el-row class="formWrap">
      <Forms
        ref='formComponent'
        :form-options='formOptions'
        :rules='rules'
        :edit-type='editType'
        :form-val='formVal'
        :file.sync='file'
        :select-opt='{groupName:groupList,bin:jobTypeList,postjobs:jobList,checkFire:checkLabel,priority:priorityList}'
      />
      <UserConfig
        :values='userForm'
        ref='userComponent'
      />
    </el-row>
    <div class="edit-footerDiv">
      <span
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="close">取 消</el-button>
        <el-button
          type="primary"
          @click="save('formComponent')"
        >保 存</el-button>
      </span>
    </div>

  </div>
</template>

<script>
import Forms from "../../common/Forms";
import UserConfig from "./UserConfig";
import { addJobApi } from "@/api/addJob";
import { successMsg, apiFn, sNameRule } from "@/assets/util";

const myMixin = {
  data: function() {
    return {
      addJobApi
    };
  }
};
export default {
  name: "EditJob",
  mixins: [myMixin],
  props: {
    rows: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  data() {
    return {
      title: "作业详细配置",
      routeItem: [],
      breadcrumb: {},
      jobTypeList: [],
      groupList: [],
      jobList: [],
      editType: "new",
      file: null,
      userForm: {},
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
        targetIps: "",
        misfire: false,
        ifBroadcast: false,
        fireNow: false
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
            disabled:true,
            info: "注意:只支持字母、数字、下划线"
          },
          {
            label: "任务组",
            key: "groupName",
            type: "select",
            groupName: "",
            disabled:true,
            spans: 10
          },
          {
            label: "任务分类",
            key: "category",
            type: "input",
            category: "",
            disabled:false,
            spans: 10,
            info: "分类便于您管理任务"
          },
          {
            label: "文件路径",
            key: "filePath",
            type: "input",
            filePath: "",
            disabled:false,
            spans: 10
          },
          {
            label: "cron时间表达式",
            key: "cronExpression",
            type: "input",
            cronExpression: "",
            disabled:false,
            spans: 10
          },
          {
            label: "类名",
            key: "className",
            type: "input",
            className: "",
            disabled:"isReadOnly",
            spans: 10
          },
          {
            label: "方法名",
            key: "methodName",
            type: "input",
            methodName: "",
            disabled:"isReadOnly",
            spans: 10
          },
          /*{
            label: "任务调用路径",
            key: "jobPath",
            type: "input",
            jobPath: "",
            disabled:"",
            spans: 10
          },*/
          {
            label: "超时时间(秒)",
            key: "timeOut",
            type: "input",
            timeOut: "",
            disabled:"",
            spans: 10,
            placeholder: "请根据任务具体情况设置超时时间"
          },
          {
            label: "任务结束时间",
            key: "endTime",
            type: "time",
            endTime: "",
            disabled:"",
            spans: 10
          },
          {
            label: "任务调用次数",
            key: "count",
            type: "input",
            count: "",
            spans: 10,
            disabled:"",
            info: "如果设置了执行任务结束时间，则以任务结束时间为准!"
          },
          {
            label: "任务类型",
            key: "bin",
            type: "select",
            bin: "",
            disabled:"",
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
            disabled:"",
            value: "value"
          },
          {
            label: "指定执行节点",
            key: "targetIps",
            type: "input",
            targetIps: "",
            spans: 10,
            disabled:"",
            placeholder: "格式：ip:port,多个可用逗号分隔"
          },
          {
            label: "",
            key: "checkFire",
            type: "checkbox",
            checkFire: "",
            spans: 20,
            disabled:"",
            valueType: "array"
          },

          {
            label: "上传文件",
            key: "file",
            type: "upload",
            file: "",
            spans: 20,
            disabled:"",
            url: ""
          },

          {
            label: "后置任务",
            key: "postjobs",
            type: "select",
            postjobs: "",
            spans: 20,
            show: "jobName",
            valuePre: "groupName",
            value: "jobName",
            disabled:"",
            multiple: true
          },
          {
            label: "任务参数",
            key: "parameters",
            type: "textarea",
            parameters: "",
            disabled:"",
            spans: 20
          },
          {
            label: "任务描述",
            key: "desc",
            type: "textarea",
            desc: "",
            disabled:"",
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
        ]
      }
    };
  },
  created() {
    this.getJobTypeList();
    this.getGroupList();
    this.getJobListByGroupName();
    // this.$nextTick(() => {
    //   this.formVal = this.setFormVal(this.rows);
    //   this.setUserForm();
    // });
  },
  watch: {
    rows: {
      handler(newVal, oldVal) {
        this.formVal = this.setFormVal(newVal);
        this.setUserForm();
      },
      deep: true
    }
  },
  updated() {},
  mounted() {},
  components: { Forms, UserConfig },
  methods: {
    setUserForm() {
      this.userForm = this.rows.taskReport
        ? JSON.parse(this.rows.taskReport)
        : null;
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
      return obj;
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
    close(param) {
      this.$emit("close", param);
    },
    setFalse(form) {
      this.checkLabel.forEach(ele => {
        form[ele.value] = false;
      });
    },

    resetCheckFire(form, data) {
      this.setFalse(form);
      if (form.checkFire.length > 0) {
        data.forEach(ele => {
          form[ele] = true;
        });
      }
      return form;
    },
    save() {
      let param = {};

      let formFlag = this.$refs.formComponent.validate("forms");
      let userFormFlag = this.$refs.userComponent.validateUserforms();
      if (!formFlag || !userFormFlag) {
        return;
      }
      param = this.setSaveParam(null);
      apiFn()(addJobApi, "update", param, this).then(res => {
        if (res.successful) {
          successMsg("job修改成功", this);
          this.close(res.successful);
        }
      });
    },
    setSaveParam(cornForm) {
      let param = {};
      let file = this.file;
      let forms = this.$refs.formComponent.forms;
      let userForm = this.$refs.userComponent.userForm;
      let userFormfy = JSON.stringify(userForm);
      forms.checkFire = forms.checkFire ? forms.checkFire : [];
      forms = this.resetCheckFire(forms, forms.checkFire);
      delete forms.checkFire;
      file ? delete forms.file : "";
      param = new FormData();
      param = this.addFormParam(param, forms);
      file ? param.append("file", file.raw) : param.append("file", undefined);
      // param.append("job", JSON.stringify(forms));
      cornForm ? param.append("cron", JSON.stringify(cornForm)) : "";
      param.append("taskReport", userFormfy);
      return param;
    },
    addFormParam(param, form) {
      for (let key in form) {
        param.append(key, form[key]);
      }
      return param;
    }
  },

  destroyed() {}
};
</script>
<style>
.edit-footerDiv {
  text-align: center;
}
</style>
