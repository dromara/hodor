/// 动态路由
import Layout from "@/layout/index.vue";

export default {
  Layout,
  'account-center': () => import('@/view/account/center.vue'),
  'dashboard-console': () => import('@/view/dashboard/console.vue'),
  'dashboard-workspace': () => import('@/view/dashboard/workspace.vue'),
  'more-form': () => import('@/view/form/moreForm.vue'),
  'base-form': () => import('@/view/form/baseForm.vue'),
  'step-form': () => import('@/view/form/stepForm.vue'),
  'base-list': () => import('@/view/list/baseList.vue'),
  'card-list': () => import('@/view/list/cardList.vue'),
  'news-list': () => import('@/view/list/newsList.vue'),
  'table-list': () => import('@/view/list/tableList.vue'),
  'result-success': () => import('@/view/result/success.vue'),
  'result-failure': () => import('@/view/result/failure.vue'),
  'result-warning': () => import('@/view/result/warning.vue'),
  'result-info': () => import('@/view/result/info.vue'),
  'error-403': () => import('@/view/error/403.vue'),
  'error-404': () => import('@/view/error/404.vue'),
  'error-500': () => import('@/view/error/500.vue'),
  'permission-test': () => import('@/view/permission/permission.vue'),
  'userInfo': () => import('@/view/systemManager/userInfo.vue'),
  'actuator-list': () => import('@/view/actuator/index.vue'),
  'scheduler-list': () => import('@/view/scheduler/index.vue'),
  'group-list': () => import('@/view/group/index.vue'),
  'jobInfo': () => import('@/view/job/jobInfo/jobInfo.vue'),
  'jobStatus': () => import('@/view/job/jobStatus/index.vue')
}
