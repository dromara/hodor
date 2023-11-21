import {menuList, menuTree, login, logout} from "@/api/module/user";
import { createRouteByList, createRouteByTree} from "@/route/permission";
import { message } from "ant-design-vue";

const state = {
  token: localStorage.getItem("USER_TOKEN") != null ? localStorage.getItem("USER_TOKEN") : "",
  userInfo: localStorage.getItem('USER_INFO') != null ? localStorage.getItem('USER_INFO') : null,
  userRoutes: localStorage.getItem("USER_ROUTES") != null ? localStorage.getItem("USER_ROUTES") : [],
  userPowers: localStorage.getItem("USER_POWERS") != null ? localStorage.getItem("USER_POWERS") : []
}

const mutations = {
  SET_USER_TOKEN(state, token) {
    if (token) {
      state.token = token;
      localStorage.setItem('USER_TOKEN', token);
    } else {
      state.token = '';
      localStorage.removeItem('USER_TOKEN')
    }
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    localStorage.setItem('USER_INFO', userInfo)
  },
  SET_USER_MENU(state, menuList) {
    if (menuList && menuList.length === 0) {
      state.userRoutes = []
      localStorage.removeItem('USER_ROUTES')
    } else {
      const finalMenu = menuList
      state.userRoutes = finalMenu
      localStorage.setItem('USER_ROUTES', JSON.stringify(finalMenu))
    }
  }
}

const actions = {
  setUserToken({commit}, token) {
    return new Promise(resolve => {
      commit('SET_USER_TOKEN', token);
      resolve()
    })
  },
  async logout({commit}) {
    await logout()
    message.success("注销成功", 0.5).then(function(){
      commit('SET_USER_TOKEN');
      commit('SET_USER_MENU');
      window.location.reload();
    });
    return Promise.resolve();
  },
  async login({commit}, data) {
    try {
      const response = await login(data)
      const {code, message, result: userInfo} = response
      if (code === 200) {
        const { token } = userInfo
        delete userInfo.menuList
        delete userInfo.token
        commit('SET_USER_TOKEN', token)
        commit('SET_USER_INFO', userInfo)
        return Promise.resolve()
      } else {
        return Promise.reject(message)
      }
    } catch (e) {
      console.log(e)
    }
  },
  async addUserRouteForArray ({ state: { userRoutes }, commit }) {
    const { result: data } = await menuList()
    const dynamicRoutes = createRouteByList(data)
    commit('SET_USER_MENU', dynamicRoutes)
  },
  async addUserRouteForTree ({ state: { userRoutes }, commit }) {
    const { result: data } = await menuTree()
    const dynamicRoutes = createRouteByTree(data)
    commit('SET_USER_MENU', dynamicRoutes)
  }
}

export default {
  namespaced: true,
  mutations,
  actions,
  state
}