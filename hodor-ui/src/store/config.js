import qs from 'qs'
import axios from 'axios'

let baseSettings = {
  responseType: 'json',
  headers: {
    'content-type': 'application/x-www-form-urlencoded; charset=utf-8',
    'X-Requested-With': 'XMLHttpRequest',
  },
}

const goToLoginPage = () => {
  let basePath = window.location.origin
  location.href = basePath + '/login.html'
}

export const ajaxPost = (options) => {
  let axiosOptions = Object.assign(baseSettings, {
    method: 'POST',
    url: options.url,
  })
  if (options.data) {
    axiosOptions['data'] = qs.stringify(options.data, { arrayFormat: 'brackets' })
  }
  return new Promise((resolve, reject) => {
    axios(axiosOptions).then((response) => {
      if (options.success) {
        options.success(response.data)
      }
      if (response.data && response.data.isExpired == '1') {
        goToLoginPage()
      }
      resolve(response.data)
    }, (err) => {
      reject(err)
    })
  })
}

export const ajaxGet = (options) => {
  let axiosOptions = Object.assign(baseSettings, {
    method: 'GET',
    url: options.url,
  })
  return new Promise((resolve, reject) => {
    axios(axiosOptions).then((response) => {
      if (options.success) {
        options.success(response.data)
      }
      if (response.data && response.data.isExpired == '1') {
        goToLoginPage()
      }
      resolve(response.data)
    }, (err) => {
      reject(err)
    })
  })
}
