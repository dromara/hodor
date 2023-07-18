<script setup>
import { ref, computed } from "vue";
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { useUserStore } from "@/stores/user";
import { useRouter } from 'vue-router'

const formState = ref({
    username: 'admin',
    password: 'admin',
});
const userStore = useUserStore()
const router = useRouter()
const onFinish = async values => {
    // 提交表单且数据验证成功后回调事件
    // values为返回的表单数据
    // 登录请求和校验
    const {username, password} = formState.value
    await userStore.getUserInfo({username, password})
    message.success('登录成功')
    router.push({ path: '/' })
};
const onFinishFailed = errorInfo => {
    // 提交表单且数据验证失败后回调事件
    message.error('输入内容有误！')
};
const disabled = computed(() => {
    // 表单不为空则按钮可点击
    return (formState.username && formState.password);
});
// 表单校验规则
const rules = {
    username: [
        {
            type: "string",
            required: true,
            message: "用户名不可以为空",
            trigger: "blur",
        },
        {
            type: "string",
            min: 2,
            max: 20,
            message: "用户名长度在2-20位",
            trigger: "blur",
        },
    ],
    password: [
        {
            type: "string",
            required: true,
            message: "密码不可以为空",
            trigger: "blur",
        },
        {
            type: "string",
            min: 5,
            max: 20,
            message: "密码长度在5-20位",
            trigger: "blur",
        },
    ],
};
</script>

<template>
    <div class="bg">
        <div class="logo"></div>
        <a-form :model="formState" name="normal_login" class="form" @finish="onFinish" @finishFailed="onFinishFailed">
            <a-form-item label="Username" name="username" :rules="rules.username">
                <a-input v-model:value="formState.username">
                    <template #prefix>
                        <UserOutlined class="site-form-item-icon" />
                    </template>
                </a-input>
            </a-form-item>

            <a-form-item label="Password" name="password" :rules="rules.password">
                <a-input-password v-model:value="formState.password">
                    <template #prefix>
                        <LockOutlined class="site-form-item-icon" />
                    </template>
                </a-input-password>
            </a-form-item>

            <a-form-item>
                <a-button :disabled="disabled" type="primary" html-type="submit" class="login-button">
                    Login
                </a-button>
                Or
                <a href="#">register now!</a>
            </a-form-item>
        </a-form>
    </div>
</template>

<style scoped>
.bg {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    background-size: cover;
}

.logo {
    padding: 65px;
    width: 164px;
    background-image: url('@/assets/images/logo.png');
    background-color: #ffffff;
    background-repeat: no-repeat;
}

.form {
    /* background-color: #1d7fcc; */
    padding: 50px;
    width: 370px;
    /* border-bottom: 8px solid #8ceece; */
    border-radius: 30px 30px 50px 50px;
}

.login-button {
    background: rgba(79, 195, 160, 1);
    border-radius: 2px;
    color: white;
    width: 100%;
}
</style>
