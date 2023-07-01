import {
    createRouter,
    createWebHashHistory
} from 'vue-router'

import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Welcome from '@/components/Welcome.vue'
import Scheduler from '@/components/Scheduler.vue'
import Actuator from '@/components/Actuator.vue'
import DAGWorkflow from '@/components/DAGWorkflow.vue'
import JobAlarmFilter from '@/components/job/JobAlarmFilter.vue'
import JobAlarmSearch from '@/components/job/JobAlarmSearch.vue'
import JobGroup from '@/components/job/JobGroup.vue'
import JobInfo from '@/components/job/JobInfo.vue'
import JobStatus from '@/components/job/JobStatus.vue'
import OneTimeJob from '@/components/job/OneTimeJob.vue'

const router = createRouter({
    history: createWebHashHistory(),
    routes: [{
        path: '',
        redirect: '/home'
    },
    {
        path: '/',
        redirect: '/home'
    },
    {
        path: '/login',
        name: 'login',
        component: Login,
    },
    {
        path: '/home',
        name: 'home',
        component: Home,
        redirect: "/welcome",
        // 嵌套路由：实现菜单切换
        children: [
            {
                path: '/welcome',
                name: 'welcome',
                component: Welcome,
            },
            {
                path: '/scheduler',
                name: 'scheduler',
                component: Scheduler,
            },
            {
                path: '/actuator',
                name: 'actuator',
                component: Actuator,
            },
            {
                path: '/workflow',
                name: 'workflow',
                component: DAGWorkflow,
            },
            {
                path: '/job-alarm-filter',
                name: 'job-alarm-filter',
                component: JobAlarmFilter,
            },
            {
                path: '/job-alarm-search',
                name: 'job-alarm-search',
                component: JobAlarmSearch,
            },
            {
                path: '/job-group',
                name: 'job-group',
                component: JobGroup,
            },
            {
                path: '/job-info',
                name: 'job-info',
                component: JobInfo,
            },
            {
                path: '/job-status',
                name: 'job-status',
                component: JobStatus,
            },
            {
                path: '/one-time-job',
                name: 'one-time-job',
                component: OneTimeJob,
            },
        ]
    },
    ],
});

export default router;