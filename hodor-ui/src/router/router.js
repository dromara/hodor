import {createRouter, createWebHashHistory} from 'vue-router'

import Layout from '@/views/Layout/index.vue'
import Home from '@/views/Home/index.vue'
import Login from '@/views/Login/index.vue'
import Scheduler from '@/views/Scheduler/index.vue'
import Actuator from '@/views/Actuator/index.vue'
import DAGWorkflow from '@/views/DAGWorkflow/index.vue'
import JobAlarmFilter from '@/views/Job/JobAlarmFilter/index.vue'
import JobGroup from '@/views/Job/JobGroup/index.vue'
import JobAlarmSearch from '@/views/Job/JobAlarmSearch/index.vue'
import JobInfo from '@/views/Job/JobInfo/index.vue'
import JobStatus from '@/views/Job/JobStatus/index.vue'
import OneTimeJob from '@/views/Job/OneTimeJob/index.vue'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'layout',
      component: Layout,
      redirect:'/login',
      children: [
        {
          path: 'home',
          name: 'home',
          component: Home,
        },
        {
          path: 'scheduler',
          name: 'scheduler',
          component: Scheduler,
        },
        {
          path: 'actuator',
          name: 'actuator',
          component: Actuator,
        },
        {
          path: 'workflow',
          name: 'workflow',
          component: DAGWorkflow,
        },
        {
          path: 'job-alarm-filter',
          name: 'job-alarm-filter',
          component: JobAlarmFilter,
        },
        {
          path: 'job-alarm-search',
          name: 'job-alarm-search',
          component: JobAlarmSearch,
        },
        {
          path: 'job-group',
          name: 'job-group',
          component: JobGroup,
        },
        {
          path: 'job-info',
          name: 'job-info',
          component: JobInfo,
        },
        {
          path: 'job-status',
          name: 'job-status',
          component: JobStatus,
        },
        {
          path: 'one-time-job',
          name: 'one-time-job',
          component: OneTimeJob,
        },
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
  ]
});
// 全局路由守卫
router.beforeEach((to, from, next) => {
  const userInfo = localStorage.getItem('user')
  if (!userInfo && to.name === 'login') {
    next();
  }
  else {
    if (userInfo) {
      next();
    }
    else {
      router.replace('/login')
    }
  }
})

export default router;
