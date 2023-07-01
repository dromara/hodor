<template>
    <a-layout style="min-height: 97vh">
        <!-- 侧边栏 -->
        <a-layout-sider v-model:collapsed="collapsed" collapsible>
            <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="inline">
                <a-menu-item key="1" @click="changeMenu('welcome')">
                    <HomeOutlined />
                    <span>HodorScheduler</span>
                </a-menu-item>
                <a-menu-item key="2" @click="changeMenu('scheduler')">
                    <BlockOutlined />
                    <span>调度节点管理</span>
                </a-menu-item>
                <a-menu-item key="3" @click="changeMenu('actuator')">
                    <BuildOutlined />
                    <span>执行节点管理</span>
                </a-menu-item>
                <a-sub-menu key="sub1">
                    <template #title>
                        <span>
                            <ProfileOutlined />
                            <span>任务管理</span>
                        </span>
                    </template>
                    <a-menu-item key="4" @click="changeMenu('job-group')">任务组管理</a-menu-item>
                    <a-menu-item key="5" @click="changeMenu('job-info')">任务信息管理</a-menu-item>
                    <a-menu-item key="6" @click="changeMenu('job-status')">任务执行状态</a-menu-item>
                    <a-menu-item key="7" @click="changeMenu('job-alarm-search')">任务报警查询</a-menu-item>
                    <a-menu-item key="8" @click="changeMenu('job-alarm-filter')">任务报警过滤管理</a-menu-item>
                    <a-menu-item key="9" @click="changeMenu('one-time-job')">一次性任务查询</a-menu-item>
                </a-sub-menu>
                <a-menu-item key="10" @click="changeMenu('workflow')">
                    <ApartmentOutlined />
                    <span>DAG任务流</span>
                </a-menu-item>
            </a-menu>
        </a-layout-sider>
        <a-layout>
            <!-- 页头 -->
            <a-layout-header class="header">
                <span @click="logout" style="cursor: pointer">
                    <LogoutOutlined />
                    <span> 退出</span>
                </span>
            </a-layout-header>
            <!-- 内容 -->
            <a-layout-content style="margin: 0 16px">
                <div :style="{ padding: '24px', background: '#fff', minHeight: '360px' }">
                    <router-view />
                </div>
            </a-layout-content>
            <!-- 页尾 -->
            <a-layout-footer style="text-align: center">
                HodorScheduler v1.0.0
            </a-layout-footer>
        </a-layout>
    </a-layout>
</template>
<script>
import { HomeOutlined, BlockOutlined, BuildOutlined, ProfileOutlined, ApartmentOutlined, LogoutOutlined } from '@ant-design/icons-vue';
import { defineComponent, ref } from 'vue';
export default defineComponent({
    components: {
        HomeOutlined,
        BlockOutlined,
        BuildOutlined,
        ProfileOutlined,
        ApartmentOutlined,
        LogoutOutlined,
    },

    data() {
        return {
            collapsed: ref(false),
            selectedKeys: ref(['1']),
        };
    },

    methods: {
        changeMenu(route) {
            if (route !== this.$route.name) {
                this.$router.push(route)
            }
        },

        logout() {
            
        }
    },
});
</script>
<style scoped>
#components-layout-demo-side {
    height: 32px;
    margin: 16px;
    background: rgba(255, 255, 255, 0.3);
}

.site-layout .site-layout-background {
    background: #fff;
}

[data-theme='dark'] .site-layout .site-layout-background {
    background: #141414;
}

.header {
    text-align: right;
    font-size: large;
    background: #24292E;
    padding: 0% 2%;
    color: #fff;
}
</style>