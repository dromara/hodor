<script setup>
import {
    HomeOutlined,
    BlockOutlined,
    BuildOutlined,
    ProfileOutlined,
    ApartmentOutlined,
    LogoutOutlined,
    UserOutlined,
    LoginOutlined
} from '@ant-design/icons-vue';
import { ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router'

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
const selectedKeys = ref(['1'])
const menu = [
    {
        key: "1",
        router: "/home",
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
            // {
            //     key: "7",
            //     router: "/job-alarm-search",
            //     text: "任务报警查询",
            // },
            // {
            //     key: "8",
            //     router: "/job-alarm-filter",
            //     text: "任务报警过滤管理",
            // },
            // {
            //     key: "9",
            //     router: "/one-time-job",
            //     text: "一次性任务查询",
            // },
        ]
    },
    // {
    //     key: "10",
    //     router: "/workflow",
    //     text: "DAG任务流",
    //     icon: ApartmentOutlined,
    // },
]

// tabs
// const panes = ref([
//     {
//         title: '主页',
//         key: '/home',
//         closable: false,
//         // content:,
//     }
// ]);
// const activeKey = ref(panes.value[0].key);
// const add = () => {
//     activeKey.value = router.currentRoute.value.path;
//     const menuItem = menu.find(item => {
//         if (item.children) {
//             const res = item.children.find(subItem => {
//                 return subItem.router === activeKey.value
//             })
//             return res;
//         }
//         else {
//             return item.router == activeKey.value;
//         }
//     });
//     panes.value.push({
//         title: (menuItem.children && menuItem.children.find(item => item.router === activeKey.value).text) || menuItem.text,
//         key: activeKey.value,
//     });
// };

// router.afterEach(async (to, from) => {
//     const toPage = to.path;
//     // 在tabPanes中寻找，如果已有标签页则显示相应标签页，如果没有则新建标签页
//     const res = panes.value.find(item => item.key === toPage)
//     if (res) {
//         activeKey.value = toPage;
//     }
//     else {
//         add();
//     }
// })
// const remove = targetKey => {
//     let lastIndex = 0;
//     panes.value.forEach((pane, i) => {
//         if (pane.key === targetKey) {
//             lastIndex = i - 1;
//         }
//     });
//     panes.value = panes.value.filter(pane => pane.key !== targetKey);
//     if (panes.value.length && activeKey.value === targetKey) {
//         if (lastIndex >= 0) {
//             activeKey.value = panes.value[lastIndex].key;
//         } else {
//             activeKey.value = panes.value[0].key;
//         }
//     }
//     router.push(activeKey.value)
// };
// const onEdit = targetKey => {
//     remove(targetKey);
// };

// watch(
//     () => activeKey.value,
//     () => {
//         // tab标签页选择与侧边栏同步
//         let curMenuItem=menu.find(item=>{
//             if(item.children){
//                 return item.children.find(subItem=>subItem.router===activeKey.value)
//             }
//             else{
//                 return item.router===activeKey.value
//             }
//         });
//         if(curMenuItem){
//             if(curMenuItem.children){
//                 curMenuItem=curMenuItem.children.find(item=>item.router===activeKey.value)
//             }
//             selectedKeys.value.length=0
//             selectedKeys.value.push(curMenuItem.key)
//         }
//         router.push(activeKey.value)
//     },
//     {
//         immediate: true,
//     }
// );

const routes = ref([{
    path: '/home',
    breadcrumbName: '主页',
}])
function findElementRecursive(array, target) {
  for (let item of array) {
    if (item.router === target) {
      return item.text;
    } else if (Array.isArray(item.children)) {
      return findElementRecursive(item.children, target)
    }
  }
  return undefined;
}
router.beforeEach((to, from, next) => {
    const toPath = to.path;
    if (toPath === menu[0].router) {
        if (routes.value.length > 1) routes.value.pop();
    }
    else {
        const hasCurPath = routes.value.find(item => item.path === toPath)
        if (!hasCurPath) {
            const toMenuItem=findElementRecursive(menu,toPath)
            if (toMenuItem) {
                if (routes.value.length > 1) routes.value.pop();
                routes.value.push({
                    path: toPath,
                    breadcrumbName: toMenuItem,
                })
            }
        }
    }
    next();
})
</script>

<template>
    <a-layout style="min-height: 97vh">
        <!-- 侧边栏 -->
        <a-layout-sider v-model:collapsed="collapsed" collapsible class="sider">
            <a-menu v-model:selectedKeys="selectedKeys" theme="dark" mode="inline" id="siderMenu">
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
        <a-layout :style="{ marginLeft: '200px' }">
            <!-- 页头 -->
            <a-layout-header class="header">
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
            </a-layout-header>
            <!-- 内容 -->
            <a-layout-content :style="{ margin: '54px 0px 0', overflow: 'initial' }">
                <div :style="{ padding: '24px 20px 24px 15px', minHeight: '360px' }">
                    <!-- <a-tabs v-model:activeKey="activeKey" hide-add type="editable-card" @edit="onEdit">
                        <a-tab-pane v-for="(pane, index) in panes" :key="pane.key" :tab="pane.title" :closable="pane.closable">
                            <router-view />
                        </a-tab-pane>
                    </a-tabs> -->
                    <a-breadcrumb :routes="routes" class="bread">
                        <template #itemRender="{ route, params, routes, paths }">
                            <span v-if="routes.indexOf(route) === routes.length - 1">{{ route.breadcrumbName }}</span>
                            <RouterLink v-else :to=route.path>{{ route.breadcrumbName }}</RouterLink>
                        </template>
                    </a-breadcrumb>
                <br/>
                    <router-view />
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

    :deep(li.ant-menu-item.ant-menu-item-only-child) {
        margin-bottom: 10px;
    }
}

:deep(:where(.css-dev-only-do-not-override-j6gjt1).ant-menu) {
    font-size: 1rem;
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
.bread {
    padding: 0 10px;
}
</style>
