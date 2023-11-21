import http from '@/tools/http'

const Api = {
  login: '/login',
  logout: '/logout',
  menuList: '/getUserMenusArray',
  menuTree: '/getUserMenusTree',
}

/// 登录
export const login = data => {
  return http.request({
    url: Api.login,
    data: data,
    method: 'post'
  })
}

/// 注销
export const logout = data => {
  return http.request({
    url: Api.logout,
    data: data,
    method: 'post'
  })
}

/// 菜单列表 (集合)
export const menuList = data => {
  return http.request({
    url: Api.menuList,
    data: data,
    method: 'post'
  })
}

/// 菜单列表 (嵌套)
export const menuTree = data => {
  return http.request({
    url: Api.menuTree,
    data: data,
    method: 'post'
  })
}
