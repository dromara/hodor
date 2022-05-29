import axios from "axios"
import * as util from "../assets/util.js";
import qs from 'qs'
const instance = axios.create({
  baseURL: window.location.origin + '/ejob',
  withCredentials: true,
  timeout: 10000,
  timestamp: new Date().getTime(),
  headers: {
    "X-Requested-with": "XMLHttpRequest"
  },
  transformRequest: [function (data) {
    if (data) {
      if (data.__proto__ == FormData.prototype) {
        return data
      }

    }
    return qs.stringify(data)

  }]
})

instance.defaults.headers.post["Content-Type"] =
  'application/x-www-form-urlencoded';


instance.interceptors.request.use(
  config => {
    return config
  });
instance.interceptors.response.use(function (response) {
  return response
}, util.catchError)

export default instance
