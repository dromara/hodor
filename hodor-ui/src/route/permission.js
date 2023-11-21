import store from "@/store"
import NProgress from "nprogress"
import router from "@/route/index"
import permissionRoutes from './module/main-routes'
import { toTree, hasRoute } from "@/tools/menu"

/**
 * 根据 菜单树 数据 生成 路由树
 *
 * @param menuList
 */
export const createRouteByTree = (menuList) => {
  const userRoutes = menuList.map(menu => {
    const { id, parent, icon, name, children = [], path, hidden = false, title, i18n } = menu
    const currentMenu = {
      id, path, name, hidden, parent,
      meta: { title, i18n, icon},
      children: children.length === 0 ? [] : createRouteByTree(children)
    }
    if (children.length <= 0) {
      delete currentMenu.children
    }
    return currentMenu
  })
  return userRoutes
}

/**
 * 根据 菜单集合 数据 生成 路由树
 *
 * @param menuList
 */
export const createRouteByList = menuList => {
  const tree = toTree(menuList)
  return createRouteByTree(tree)
}

/**
 * 路由 添加 布局
 *
 * @param routes
 */
export const setUserRouteComponent = routes => {
  routes.forEach(r => {
    r.component = r.parent === "0" ? permissionRoutes.Layout : permissionRoutes[r.name]
    if (r.children && r.children.length > 0) {
      setUserRouteComponent(r.children)
    }
  })
}

/**
 * 网站 设置 标题
 *
 * @param title 标题
 */
const setDocumentTitle = title => {
  document.title = `Hodor-${title}`
}
/**
 * 前置拦截
 *
 * @param to    前往路由
 * @param from  来至路由
 * @param next  放行路由
 */
export const permissionController = async (to, from, next) => {
  NProgress.start();
  const { meta } = to
  setDocumentTitle(meta.title)
  await store.dispatch('app/execCancelToken')
  // 检 测 登 录
  if (!to.fullPath.includes('login') && !localStorage.getItem('USER_TOKEN')) {
    next({path: '/login'})
  } else {
    // 基本路由 是否包括 前往路由
    let paths = router.getRoutes().map(it => it.path)
    if (!paths.includes(to.path)) {

      await store.dispatch('user/addUserRouteForArray')

      const userRoutes = JSON.parse(JSON.stringify(store.getters.menu))
      // 动态路由 是否包括 前往路由
      if (hasRoute(userRoutes, to.path)) {
        // 设置 路由 布局
        setUserRouteComponent(userRoutes)
        userRoutes.forEach(r => {
          router.addRoute(r)
        })
        next(to.fullPath)
      } else {
        // 404
        next('/error/404')
      }
    } else {
      next()
    }
  }
}
