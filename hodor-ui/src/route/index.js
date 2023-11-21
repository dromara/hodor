import {createRouter, createWebHashHistory} from 'vue-router'
import routes from './module/base-routes'
import NProgress from "nprogress";
import "nprogress/nprogress.css";
import { permissionController } from "@/route/permission";

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach(permissionController)

router.afterEach((to, from) => {
  NProgress.done();
})

export default router