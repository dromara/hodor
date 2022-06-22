import Vue from 'vue'
import Vuex from 'vuex'
import auth from './modules/auth'
import topProject from './modules/common/topProject'

Vue.use(Vuex)

export const LoginStore = new Vuex.Store(auth)
export const IndexStore = new Vuex.Store({
  modules: {
    topProject
  },
})
