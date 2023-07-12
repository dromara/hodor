import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteMockServe } from "vite-plugin-mock"  // 引入mock插件

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // mock插件配置
    viteMockServe({
      mockPath: "./src/mock",
      localEnabled: true,
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
