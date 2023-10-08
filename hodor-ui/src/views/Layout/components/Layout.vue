<script setup>
import {
    LogoutOutlined,
    UserOutlined,
    LoginOutlined
} from '@ant-design/icons-vue';
import { ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router'
import Menu from '@/components/Menu/Menu.vue'
import Tabs from '@/components/Tabs.vue'
import BreadCrumb from '@/components/BreadCrumb.vue';

const userStore = useUserStore()
const router = useRouter()
// 退出登录
const confirm = () => {
    // 1. 清除用户信息
    userStore.clearUserInfo()
    // 2.跳转到登录页
    router.replace('/login')
}
// 取消退出登录
const cancel = () => {

}
// 登录
const doLogin = () => {
    router.replace('/login')
}

// 左侧菜单
const collapsed = ref(false)

</script>

<template>
    <a-layout style="min-height: 97vh">
        <!-- 侧边栏 -->
        <a-layout-sider v-model:collapsed="collapsed" collapsible class="sider">
            <Menu />
        </a-layout-sider>
        <a-layout :style="{ marginLeft: '200px' }">
            <!-- 页头 -->
            <a-layout-header class="header">
                <div class="headerContent">
                    <BreadCrumb />
                    <ul>
                        <li>
                            <a href="#">
                                <i><UserOutlined /></i>
                                {{ userStore.userInfo ? userStore.userInfo.username : '' }}
                            </a>
                        </li>

                        <li v-if="userStore.userInfo && JSON.stringify(userStore.userInfo) !== '{}'">
                            <a-popconfirm title="确认退出吗？" ok-text="确认" cancel-text="取消" @confirm="confirm" @cancel="cancel">
                                <a href="#">
                                    <i><LogoutOutlined /></i>
                                    退出登录
                                </a>
                            </a-popconfirm>
                        </li>
                        <li v-else>
                            <a href="#" @click="doLogin">
                                <i><LoginOutlined /></i>
                                点击登录
                            </a>
                        </li>
                    </ul>
                </div>
            </a-layout-header>
            <!-- 内容 -->
            <a-layout-content :style="{ margin: '54px 0px 0', overflow: 'initial' }">
                <div :style="{ padding: '24px 20px 24px 15px', minHeight: '360px' }">
                    <Tabs />
                    <router-view v-slot="{ Component, route }">
                        <!-- <KeepAlive> -->
                            <component :is="Component" :key="route.name"></component>
                        <!-- </KeepAlive> -->
                    </router-view>
                </div>
            </a-layout-content>
            <!-- 页尾 -->
            <a-layout-footer :style="{ textAlign: 'center' }">
                HodorScheduler v1.0.0
            </a-layout-footer>
        </a-layout>
    </a-layout>
</template>

<style scoped lang="scss">
.sider {
    overflow: auto;
    height: 100vh;
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 4;
}
.header {
    text-align: right;
    font-size: large;
    background: #24292E;
    padding: 0% 2%;
    color: #fff;
    width: 100%;
    position: fixed;
    right: 0;
    top: 0;
    z-index: 3;

    .headerContent {
        margin-left: 200px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    ul {
        list-style: none;
        display: flex;
        height: 35px;
        justify-content: flex-end;
        align-items: center;

        li {
            a {
                padding: 0 15px;
                color: #cdcdcd;
                line-height: 1;
                display: inline-block;

                i {
                    font-size: 16px;
                    margin-right: 2px;
                }
            }

            ~li {
                a {
                    border-left: 2px solid #666;
                }
            }
        }
    }
}
</style>
