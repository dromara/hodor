import Vue from "vue"
import Router from "vue-router"

const Home = () =>
  import("@/components/Home")
const Login = () =>
  import("@/views/Login")
const Index = () =>
  import("@/views/Index")

Vue.use(Router)

const router = new Router({
  routes: [{
      path: "/",
      redirect: "/index",
    },
    {
      path: "/login",
      name: 'login',
      component: Login,
    },
    {
      path: "/index",
      component: Index,
      redirect: "/home",
      meta: {
        requireAuth: true,
      },
      children: [{
          path: "/home",
          name: 'home',
          redirect: "/service",

        },
        {
          path: "/service",
          name: 'service',
          component: resolve =>
            require(["@/components/Service"], resolve),
        },
        {
          path: "/jobgroup",
          name: 'jobgroup',
          component: resolve =>
            require(["@/components/job/Jobgroup"], resolve),
        },
        {
          path: "/nodeInfo",
          name: 'nodeInfo',
          component: resolve =>
            require(["@/components/job/NodeInfo"], resolve),
        },
        {
          path: "/jobstatus",
          name: 'jobstatus',
          component: resolve =>
            require(["@/components/job/Jobstatus"], resolve),
        },
        {
          path: "/jobalarm",
          name: 'jobalarm',
          component: resolve =>
            require(["@/components/job/Jobalarm"], resolve),
        },
        {
          path: "/jobmessage",
          name: 'jobmessage',
          component: resolve =>
            require(["@/components/job/Jobmessage"], resolve),
        },
        {
          path: "/batch_upload",
          name: 'batchUpload',
          component: resolve =>
            require(["@/components/job/config/BatchUpload"], resolve),
        },
        {
          path: "/add_job",
          name: 'addJob',
          component: resolve =>
            require(["@/components/job/config/AddJob"], resolve),
        },
        {
          path: "/job_alarm_search",
          name: 'job_alarm_search',
          component: resolve =>
            require(["@/components/job/JobAlarmSearch"], resolve),
        },
        {
          path: "/job_run_details",
          name: 'job_run_details',
          component: resolve =>
            require(["@/components/job/JobRunDetatils"], resolve),
        },
        {
          path: "/job_alarm_filter",
          name: 'job_alarm_filter',
          component: resolve =>
            require(["@/components/job/JobAlarmFilter"], resolve),
        },
        {
          path: "/job_bind",
          name: 'job_bind',
          component: resolve =>
            require(["@/components/job/JobBind"], resolve),
        },
        {
          path: "/once_search",
          name: 'once_search',
          component: resolve =>
            require(["@/components/job/OnceSearch"], resolve),
        },
        {
          path: "/user_auth",
          name: 'userAuth',
          component: resolve =>
            require(["@/components/user/Auth"], resolve),
        },
        {
          path: "/user_action",
          name: 'userAction',
          component: resolve =>
            require(["@/components/user/Action"], resolve),
        },
        {
          path: "/user_role",
          name: 'userRole',
          component: resolve =>
            require(["@/components/user/Role"], resolve),
        },
        {
          path: "/user_manage",
          name: 'userManage',
          component: resolve =>
            require(["@/components/user/Manage"], resolve),
        },
        {
          path: "/tool_monitor",
          name: 'toolMonitor',
          component: resolve =>
            require(["@/components/tool/Monitor"], resolve),
        },
        {
          path: "/tool_config_monitor",
          name: 'toolConfigMonitor',
          component: resolve =>
            require(["@/components/tool/ConfigMonitor"], resolve),
        },
        {
          path: "/tool_cron_transfer",
          name: 'toolCronTransfer',
          component: resolve =>
            require(["@/components/tool/CronTransfer"], resolve),
        },
        {
          path: "/chart_statistics",
          name: 'chartStatistics',
          component: resolve =>
            require(["@/components/chart/Statistics"], resolve),
        },
        {
          path: "/chart_monitor",
          name: 'chartMonitor',
          component: resolve =>
            require(["@/components/chart/Monitor"], resolve),
        },
        {
          path: "/work_flow",
          name: 'workFlow',
          component: resolve =>
            require(["@/components/WorkFlow"], resolve),
        },
        {
          path: "/help",
          name: 'help',
          component: resolve =>
          window.location.href=window.location.href
            // require(["@/components/feedback/Use"], resolve),
        },
        {
          path: "/feedback",
          name: 'feedback',
          component: resolve =>
            require(["@/components/feedback/Feedback"], resolve),
        },
        {
          path: "/feedback_list",
          name: 'feedbackList',
          component: resolve =>
            require(["@/components/feedback/FeedbackList"], resolve),
        },
      ]
    },

  ],
})

export default router
