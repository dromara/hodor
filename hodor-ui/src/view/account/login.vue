<template>
  <div id="login">
    <div class="login-form">
      <a-form ref="formRef" :label-col="labelCol" :model="formState" :rules="formRules" :wrapper-col="wrapperCol">
        <a-form-item>
          <img class="logo" src="@/assets/image/logo.png" />
          <!--<div class="head">Hodor Admin</div>-->
          <div class="desc">一 站 式 分 布 式 任 务 调 度 平 台</div>
        </a-form-item>
        <a-form-item>
          <a-input placeholder="请 输 入 账 户 " v-model:value="formState.username" />
        </a-form-item>
        <a-form-item>
          <a-input
            placeholder="请 输 入 密 码 "
            v-model:value="formState.password"
            type="password"
            @keyup.enter="onSubmit"
          />
        </a-form-item>
        <a-form-item>
          <a-checkbox :checked="true"> 记住我 </a-checkbox>
          <a class="forgot" href=""> 忘记密码 </a>
        </a-form-item>
        <a-form-item :wrapper-col="{ span: 24 }">
          <a-button :loading="load" type="primary" @click="onSubmit">
            登录
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>
<script>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { message } from "ant-design-vue";
export default {
  setup() {
    const router = useRouter();
    const store = useStore();
    const formState = reactive({
      username: "admin",
      password: "admin",
    });

    const formRules = {
      username: [{ required: true, message: "请输入账户", trigger: "blur" }],
      password: [{ required: true, message: "请输入密码", trigger: "blur" }],
    };

    const formRef = ref();

    const load = ref(false);

    const onSubmit = async (e) => {
      formRef.value.validate().then(async () => {
          load.value = true;
          await store.dispatch("user/login", formState);
          await router.push("/");
        })
        .catch((error) => {
          console.log("error", e);
          message.error(e);
        });
    };
    return {
      labelCol: { span: 6 },
      wrapperCol: { span: 24 },
      formRules,
      formState,
      onSubmit,
      formRef,
      load,
    };
  },
};
</script>
<style lang="less">
#login {
  width: 100%;
  height: 100%;
  background: url(../../assets/image/background.svg);
  background-size: cover;
  .login-form {
    margin: auto;
    width: 340px;
    min-height: 20px;
    padding-top: 150px;
    .ant-input {
      border-radius: 4px;
      line-height: 42px;
      height: 42px;
    }
    .ant-btn {
      width: 100%;
      height: 42px;
    }
  }
  .logo {
    width: 260px !important;
    margin-top: 10px !important;
    margin-bottom: 10px !important;
    margin-left: 50px !important;
    border: none;
    background-color: transparent;
  }
  .head {
    width: 300px;
    font-size: 30px !important;
    font-weight: 700 !important;
    margin-left: 20px !important;
    line-height: 60px !important;
    margin-top: 10px !important;
    position: absolute !important;
    display: inline-block !important;
    height: 60px !important;
    color: #36b368;
  }
  .desc {
    width: 100% !important;
    text-align: center !important;
    color: gray !important;
    height: 60px !important;
    line-height: 60px !important;
  }
  .forgot {
    float: right;
  }
}
</style>
