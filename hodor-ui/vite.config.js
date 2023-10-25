import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import {viteMockServe} from "vite-plugin-mock"  // 引入mock插件

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // mock插件配置
    // viteMockServe({
    //   mockPath: "./src/mock",
    //   localEnabled: true,
    // }),
  ],
  base: './',
  // 路径映射
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // 代理配置
  server: {
    open: false,  // 自动弹出浏览器
    https: false,  // 是否是https请求
    proxy: {
      '/hodor/admin': {
        // target: 'http://106.55.104.216:8089',  // 代理目标地址
        target: 'http://127.0.0.1:8089',
        changeOrigin: true,  // 允许跨域
        pathRewrite: {
          '^/hodor/admin': '/hodor/admin' //路径的替换规则
        }
      }
    },
  },
})
