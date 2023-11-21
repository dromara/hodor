import axios from "axios";
// import {useStore} from "vuex";
import {message as Msg, notification} from "ant-design-vue";
import store from "@/store";
import JSONBIG from 'json-bigint'

// 设置响应数据的转换函数,使用JSONBIG解析响应数据,解决精度丢失问题
axios.defaults.transformResponse = [
  function (data) {
    const json = JSONBIG({
      storeAsString: true
    })
    return json.parse(data)
  }
]

class Http {

  constructor(config) {
    const contextPath = import.meta.env.VITE_CONTEXT_PATH
    let baseUrl = import.meta.env.VITE_API_DOMAIN
    if (contextPath) {
      baseUrl = baseUrl + contextPath
    }
    this.config = config || {
      timeout: 6000,
      withCredentials: true,
      baseURL: baseUrl,
      headers: {
        "Content-Type": "application/json; charset=utf-8"
      }
    };
  }

  interceptors(instance) {
    /**
     * 请求拦截器
     */
    instance.interceptors.request.use(config => {
        // 请求头添加API-KEY
        config.headers['API-KEY'] = import.meta.env.VITE_API_KEY

        const token = localStorage.getItem("USER_TOKEN");
        if (token) {
          config.headers["Access-Token"] = token;
        }
        // 请求时缓存该请求，路由跳转时取消, 如果timeout值过大，可能在上一个次请求还没完成时，切换了页面。
        config.cancelToken = new axios.CancelToken(async cancel => {
          await store.dispatch("app/execCancelToken", {cancelToken: cancel});
        });
        return config;
      },
      error => {
        return Promise.reject(error);
      }
    );

    /** 响应拦截 */
    instance.interceptors.response.use(
      response => {
        return response.data;
      },
      error => {
        if (error.response) {
          const data = error.response.data;
          if (error.response.status === 403) {
            notification.error({
              message: "无权限访问",
              description: data.message
            });
          }
          if (error.response.status === 401) {
            store.dispatch("app/logout").then(() => {
              setTimeout(() => {
                window.location.reload();
              }, 2000);
            });
          }
        } else {
          let {message} = error;
          if (message === "Network Error") {
            message = "连接异常";
          }
          if (message.includes("timeout")) {
            message = "请求超时";
          }
          if (message.includes("Request failed with status code")) {
            const code = message.substr(message.length - 3);
            message = "接口" + code + "异常";
          }
          Msg.error(message);
        }
        return Promise.reject(error);
      }
    );
  }

  request(options) {
    const instance = axios.create();
    const requestOptions = Object.assign({}, this.config, options);
    this.interceptors(instance);
    return instance(requestOptions);
  }
}

const http = new Http();
export default http;
