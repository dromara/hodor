<script setup>
import { HomeOutlined, BlockOutlined, BuildOutlined, ProfileOutlined, ApartmentOutlined, LogoutOutlined } from '@ant-design/icons-vue';
import { ref } from 'vue';

const collapsed = ref(false)
const selectedKeys = ref(['1'])
const menu = [
    {
        key: "1",
        router: "/",
        text: "HodorScheduler",
        icon: HomeOutlined,
    },
    {
        key: "2",
        router: "/scheduler",
        text: "调度节点管理",
        icon: BlockOutlined,
    },
    {
        key: "3",
        router: "/actuator",
        text: "执行节点管理",
        icon: BuildOutlined,
    },
    {
        key: "4",
        router: "/job-group",
        text: "任务组管理",
        icon: ProfileOutlined,
    },
    {
        key: "sub1",
        text: "任务管理",
        icon: ProfileOutlined,
        children: [
            {
                key: "5",
                router: "/job-info",
                text: "任务信息管理",
            },
            {
                key: "6",
                router: "/job-status",
                text: "任务执行状态",
            },
            {
                key: "7",
                router: "/job-alarm-search",
                text: "任务报警查询",
            },
            {
                key: "8",
                router: "/job-alarm-filter",
                text: "任务报警过滤管理",
            },
            {
                key: "9",
                router: "/one-time-job",
                text: "一次性任务查询",
            },
        ]
    },
    {
        key: "10",
        router: "/workflow",
        text: "DAG任务流",
        icon: ApartmentOutlined,
    },
]
</script>

<template>
    <a-layout style="min-height: 97vh">
        <!-- 侧边栏 -->
        <a-layout-sider v-model:collapsed="collapsed" collapsible>
            <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="inline">
                <template v-for="item in menu">
                    <template v-if="item.children">
                        <a-sub-menu :key=item.key>
                            <template #title>
                                <span>
                                    <component :is="item.icon" />
                                    <span>{{ item.text }}</span>
                                </span>
                            </template>
                            <a-menu-item v-for="subItem in item.children" :key="subItem.key">
                                <RouterLink :to=subItem.router>
                                    {{ subItem.text }}
                                </RouterLink>
                            </a-menu-item>
                        </a-sub-menu>
                    </template>
                    <template v-else>
                        <a-menu-item :key=item.key>
                            <RouterLink :to=item.router>
                                <component :is="item.icon" />
                                <span>{{ item.text }}</span>
                            </RouterLink>
                        </a-menu-item>
                    </template>
                </template>
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

<style scoped>
.header {
    text-align: right;
    font-size: large;
    background: #24292E;
    padding: 0% 2%;
    color: #fff;
}
</style>