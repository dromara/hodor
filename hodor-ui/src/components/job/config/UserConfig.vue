<template>
  <div class="userConfig-container">

    <el-row>
      <el-form
        :model="userForm"
        :rules="userFormRules"
        ref="userForm"
        label-width="100px"
        class="demo-ruleForm"
      >
        <div class="borderBox">
          <el-row>
            <span class="titleName">job负责人</span>

            <el-col>
              <el-form-item
                label="用户名"
                prop="name"
              >
                <el-input
                  v-model="userForm.name"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col>
              <el-switch
                v-model="userForm.yxhfEnabled"
                active-color="#13ce66"
                inactive-color="#ff4949"
                :active-value="true"
                :inactive-value="false"
              >
              </el-switch>
              <el-form-item
                label="邮箱"
                prop="yxhf"
              >
                <el-input
                  v-model="userForm.yxhf"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col>
              <el-switch
                v-model="userForm.vvEnabled"
                active-color="#13ce66"
                inactive-color="#ff4949"
                :active-value="true"
                :inactive-value="false"
              >
              </el-switch>
              <el-form-item
                label="VV"
                prop="vv"
              >
                <el-input
                  v-model="userForm.vv"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col>
              <el-switch
                v-model="userForm.weixinEnabled"
                active-color="#13ce66"
                inactive-color="#ff4949"
                :active-value="true"
                :inactive-value="false"
              >
              </el-switch>

              <el-form-item
                label="微信"
                prop="weixin"
              >
                <el-input
                  v-model="userForm.weixin"
                  size="small"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col>
              <el-switch
                v-model="userForm.failureEnabled"
                active-color="#13ce66"
                inactive-color="#ff4949"
                :active-value="true"
                :inactive-value="false"
              >
              </el-switch>
              <el-form-item
                    label=""
                    prop="onlyFailure"
                  >
                <span>仅对失败进行通知</span>
              </el-form-item>
            </el-col>
            <el-col>
              <span class="warningInfo"><i class="el-icon-warning"></i>用户名，邮箱，VV，微信都可以填写多个用逗号隔开。 【左边滑块】，<span class="turnOn">绿色</span>代表<span class="turnOn">开启</span>通知，将会通过该方式发送通知，<span class="turnOff">红色</span>代表<span class="turnOff">关闭</span>，将不会发送通知。</span>
            </el-col>
          </el-row>
        </div>

      </el-form>
    </el-row>

  </div>
</template>

<script>
import { emailRule } from "@/assets/util";
export default {
  name: "UserConfig",
  props: {
    values: {
      type: Object,
      defaultL: () => {
        return null;
      }
    }
  },
  data() {
    let numRule = function(rule, value, callback) {
      if(value==null){
         callback();
         return;
      }
      try {
        let reg = /^[\d,]*$/;
        if (!reg.test(value)) {
          callback(new Error("这里需填写数字"));
        } else {
          callback();
        }
      } catch (ex) {
        callback(new Error("这里需填写数字"));
      }
    };
    return {
      userForm: {},
      userFormRules: {
        name: [
          {
            required: true,
            message: "请填写用户名称",
            trigger: "blur"
          }
        ],
        vv: [
          {
            validator: numRule,
            message: "VV号码是一串数字",
            trigger: ["blur", "change"]
          }
        ],
        yxhf: [
          {
            validator: emailRule,
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ]
      }
    };
  },
  watch: {
    values: {
      handler(val) {
        this.userForm = val ? Object.assign({}, val) : {};
      },
      deep: true,
      immediate: true
    }
  },
  created() {},
  updated() {},
  mounted() {},
  components: {},
  methods: {
    validateUserforms() {
      let flag = true;
      this.$refs.userForm.validate(valid => {
        if (valid) {
          let vv = this.checkUserFormsVal(
            this.userForm.vvEnabled,
            this.userForm.vv
          );
          let weixin = this.checkUserFormsVal(
            this.userForm.weixinEnabled,
            this.userForm.weixin
          );
          let email = this.checkUserFormsVal(
            this.userForm.yxhfEnabled,
            this.userForm.yxhf
          );
          let failureEnabled = this.checkUserFormsVal(
            this.userForm.failureEnabled,
          );
          if (vv == "error" || weixin == "error" || email == "error") {
            this.$message.warning("选择开启，就需要填写对应的通知方式");
            flag = false;
            return;
          }
        } else {
          flag = false;
        }
      });
      return flag;
    },
    checkUserFormsVal(flag, val) {
      return !flag ? false : val ? true : "error";
    }
  },
  destroyed() {}
};
</script>
<style >
.userConfig-container .borderBox .el-form-item {
  margin-bottom: 0px;
}
.userConfig-container .borderBox {
  border: 1px solid #7b7e8c;
  border-radius: 10px;
  padding: 16px 10px 0px 10px;
  margin-top: 8px;
  margin-bottom: 8px;
}
.userConfig-container .borderBox:nth-child(1) {
  margin-bottom: 20px;
}
.userConfig-container .titleName {
  position: absolute;
  top: -25px;
  left: 2px;
  background: white;
  padding: 0px 5px;
  font-size: 10px;
  font-weight: 700;
}
.userConfig-container .warningInfo {
  font-size: 10px;
  color: #e6a23c;
  display: inline-block;
  margin-top: 30px;
}
.userConfig-container .el-switch__core {
  height: 16px;
}
.userConfig-container .el-switch__core:after {
  width: 12px;
  height: 12px;
}
.userConfig-container .el-switch__label span {
  font-size: 10px;
}
.userConfig-container .el-switch {
  position: relative;
  top: 30px;
}
.userConfig-container .turnOn {
  color: rgb(19, 206, 102);
  font-weight: 700;
}
.userConfig-container .turnOff {
  color: rgb(255, 73, 73);
  font-weight: 700;
}
.userConfig-container .userForms .el-form-item {
  margin-bottom: 0px;
}
</style>
