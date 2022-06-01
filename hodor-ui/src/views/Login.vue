<template>
  <div class="content bg">
    <div class='logo'>
<!--      <div class="word">Hodor Scheduler</div>-->
      <div></div>
    </div>
    <div @keyup.enter="onSubmit">
      <el-form
        class="form"
        :model="form"
      >
        <el-form-item prop="name">
          <el-input
            placeholder="用户名"
            :autofocus="true"
            prefix-icon="el-icon-edit"
            v-model="form.name"
          >
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            placeholder="密码"
            type="password"
            prefix-icon="el-icon-edit"
            v-model="form.password"
          >
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            class="login-btn"
            @click="onSubmit"
          >登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import "../assets/css/reset.css";
import "../assets/sass/common.css";
import "../assets/sass/login.css";
import { successMsg, apiFn, session } from "../assets/util.js";
import * as loginApi from "../api/login.js";
const myMixin = {
  data: function() {
    return {
      loginApi
    };
  }
};
export default {
  mixins: [myMixin],
  data() {
    return {
      form: {}
    };
  },
  created() {},
  methods: {
    onSubmit() {
      if (!this.checkForm()) {
        return;
      }
      let param = {
        username: this.form.name.trim(),
        password: this.form.password.trim()
      };
      apiFn()(loginApi, "login", param, this).then(res => {
        if (res && res.successful) {
          this.$router.replace("/");
          session("userInfo", res.data);
          session("activeIndex", "0_0");
        }
      });
    },
    checkForm() {
      if (this.form.name == "") {
        this.$message({
          title: "提示",
          message: "请输入用户名！",
          type: "warning",
          duration: 5000
        });
        return false;
      }
      if (this.form.password == "") {
        this.$message({
          title: "提示",
          message: "请输入密码！",
          type: "warning",
          duration: 5000
        });
        return false;
      }
      return true;
    }
  }
};
</script>
