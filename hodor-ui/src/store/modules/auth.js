import Vue from 'vue'
import { CHECK_AUTH, GET_LOGIN_USER_INFO, LOG_OUT, LOGIN } from '@/store/actions.type'
import { SET_ERROR, SET_USER_INFO } from '@/store/mutation.types'
import { ajaxPost } from '@/store/config'

const state = {
  userInfo: {
    domains: '',
  },
}

const getters = {
  userInfo(state) {
    return state.userInfo
  },
}

const actions = {
  [LOGIN](context, credentials) {
    return ajaxPost({
      url: '/sys/login.action',
      data: credentials,
    })
  },
  [LOG_OUT]() {
    return ajaxPost({
      url: '/sys/logout.action',
    })
  },
  [GET_LOGIN_USER_INFO](context) {
    return ajaxPost({
      url: '/sys/getLoginUserInfo.action',
      success: (data) => {
        context.commit(SET_USER_INFO, data)
      },
    })
  },
}

const mutations = {
  [SET_USER_INFO](state, data = {}) {
    Vue.set(state, 'userInfo', data)
  },
}

const LoginSettings = {
  state,
  getters,
  actions,
  mutations,
}

export default LoginSettings
