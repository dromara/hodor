import http from '@/tools/http'

const Api = {
  login: '/login',
  logout: '/logout',
  menuList: '/getUserMenusArray',
  menuTree: '/getUserMenusTree',
  save: '/user',
  edit: '/user',
  list: '/user',
  remove: '/user',
  give: '/user/give',
  role: '/user/role',
  power: '/user/power',
  removeBatch: '/user/removeBatch',
  resetPassword: '/user/password/reset',
}

const FakerRes = {
  success: false,
  msg: '暂不支持该操作'
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

/** 用户角色 */
export const role = data => {
  return http.request({
    url: Api.role,
    params: data,
    method: 'GET'
  })
}

/** 用户权限 */
export const power = data => {
  return http.request({
    url: Api.power,
    params: data,
    method: 'GET'
  })
}

/** 用户列表 */
export const list = data => {
  return http.request({
    url: Api.list,
    params: data,
    method: 'GET'
  })
}

/** 用户新增 */
export const save = data => {
  return http.request({
    url: Api.save,
    data: data,
    method: 'POST'
  })
}

/** 修改用户 */
export const edit = data => {
  return http.request({
    url: Api.edit,
    data: data,
    method: 'PUT'
  })
}

/** 分配权限 */
export const give = data => {
  return http.request({
    url: Api.give,
    data: data,
    method: 'POST'
  })
}

/** 用户删除 */
export const remove = data => {
  return http.request({
    url: Api.remove,
    params: data,
    method: 'DELETE'
  })
}

/** 批量删除 */
export const removeBatch = data => {
  return Promise.resolve(FakerRes);
  // return http.request({
  //   url: Api.removeBatch,
  //   params: data,
  //   method: 'DELETE'
  // })
}

/** 重置密码 */
export const resetPassword = data => {
  return Promise.resolve(FakerRes);
  // return http.request({
  //   url: Api.resetPassword,
  //   params: data,
  //   method: 'PUT'
  // })
}
