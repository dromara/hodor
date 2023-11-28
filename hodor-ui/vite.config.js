import {defineConfig, loadEnv} from 'vite';
import vue from "@vitejs/plugin-vue";
import visualizer from "rollup-plugin-visualizer";
import path from 'path'
import themePreprocessorPlugin from "@zougt/vite-plugin-theme-preprocessor";
import {fileURLToPath, URL} from 'node:url'

const plugins = [vue(),
themePreprocessorPlugin({
  less: {
    // 各个主题文件的位置
    multipleScopeVars: [
      {
        scopeName: "theme-blue",
        path: path.resolve("src/assets/theme/blue.less")
      },{
        scopeName: "theme-green",
        path: path.resolve("src/assets/theme/green.less")
      }, {
        scopeName: "theme-yellow",
        path: path.resolve("src/assets/theme/yellow.less")
      }, {
        scopeName: "theme-red",
        path: path.resolve("src/assets/theme/red.less")
      }, {
        scopeName: "theme-purple",
        path: path.resolve("src/assets/theme/purple.less")
      }
    ]
  }
})
];

if (process.env.vis) {
  plugins.push(
    visualizer({
      open: true,
      gzipSize: true,
      brotliSize: true
    })
  );
}

export default defineConfig({
  plugins,
  server: {
    port: 8080,
    open: false,  // 自动弹出浏览器
    https: false,  // 是否是https请求
    proxy: {
      '/api/hodor/admin': {
        target: loadEnv('development', process.cwd()).VITE_PROXY,
        changeOrigin: true,  // 允许跨域
        rewrite:path => path.replace(/^\/api/,'')
      }
    }
  },
  base: './',
  // 路径映射
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // 开启less支持
  css: {
    preprocessorOptions: {
      less: {
        javascriptEnabled: true
      }
    }
  },
  hmr: {
    overlay: false
  },
  build: {
    sourcemap: true,
  }
})
