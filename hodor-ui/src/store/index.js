import {createLogger, createStore} from 'vuex'

import app from "./modules/app.js"
import user from "./modules/user.js"
import getters from './getters.js'

const debug = false;

export default createStore({
  modules: {
     app,
     user
  },
  getters,
  strict: debug,
  plugins: debug ? [createLogger()] : []
})
