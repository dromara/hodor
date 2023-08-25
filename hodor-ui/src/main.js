import { createApp } from 'vue'
import App from './App.vue'
import Antd from 'ant-design-vue';
import router from './router/router.js'
import { createPinia } from 'pinia'  // 导入pinia
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'  // pinia持久化

import '@/assets/style/global.scss'

const app = createApp(App)
// pinia持久化
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

app.use(Antd)
app.use(router)
app.mount('#app')
