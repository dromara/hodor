import Vue from 'vue'
import { Message } from 'element-ui'
import initLoading from '@/utils/loading'

Vue.component(Message.name, Message)

//sessionStorage
export const session = function(key, value) {
  if (value === void 0) {
    var lsVal = sessionStorage.getItem(key)
    if (lsVal && lsVal.indexOf('autostringify-') === 0) {
      return JSON.parse(lsVal.split('autostringify-')[1])
    } else {
      return lsVal
    }
  } else {
    if (typeof value === 'object' || Array.isArray(value)) {
      value = 'autostringify-' + JSON.stringify(value)
    }

    return sessionStorage.setItem(key, value)
  }
}

//生成随机数
export const getUUID = function(len) {
  len = len || 6
  len = parseInt(len, 10)
  len = isNaN(len) ? 6 : len
  var seed = '0123456789abcdefghijklmnopqrstubwxyzABCEDFGHIJKLMNOPQRSTUVWXYZ'
  var seedLen = seed.length - 1
  var uuid = ''
  while (len--) {
    uuid += seed[Math.round(Math.random() * seedLen)]
  }
  return uuid
}
//深拷贝
export const deepcopy = function(source) {
  if (!source) {
    return source
  }
  let sourceCopy = source instanceof Array ? [] : {}
  for (let item in source) {
    sourceCopy[item] =
      typeof source[item] === 'object' ? deepcopy(source[item]) : source[item]
  }
  return sourceCopy
}
//菜单数据组织
export const buildMenu = function(array, ckey) {
  let menuData = []
  let indexKeys = Array.isArray(array)
    ? array.map(e => {
        return e.id
      })
    : []
  ckey = ckey || 'parent_id'
  array.forEach(function(e, i) {
    //一级菜单
    if (!e[ckey] || e[ckey] === e.id) {
      delete e[ckey]
      menuData.push(deepcopy(e)) //深拷贝
    } else if (Array.isArray(indexKeys)) {
      //检测ckey有效性
      let parentIndex = indexKeys.findIndex(function(id) {
        return id == e[ckey]
      })
      if (parentIndex === -1) {
        menuData.push(e)
      }
    }
  })
  let findChildren = function(parentArr) {
    if (Array.isArray(parentArr) && parentArr.length) {
      parentArr.forEach(function(parentNode) {
        array.forEach(function(node) {
          if (parentNode.id === node[ckey]) {
            if (parentNode.children) {
              parentNode.children.push(node)
            } else {
              parentNode.children = [node]
            }
          }
        })
        if (parentNode.children) {
          findChildren(parentNode.children)
        }
      })
    }
  }
  findChildren(menuData)
  return menuData
}

export const emailRule = function(rule, value, callback) {
  let reg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
  if (value == '' || value == null) {
    callback()
    return
  }
  let data = value.split(',')
  try {
    let error = -1
    data.forEach(ele => {
      if (!reg.test(ele)) {
        error++
      }
    })
    if (error > -1) {
      callback(new Error('这里需填写数字'))
    } else {
      callback()
    }
  } catch (ex) {
    callback(new Error('邮箱格式不正确'))
  }
}
//日期格式化
export const dateFormat = function(value) {
  let da = new Date(value)
  let year = da.getFullYear()
  let month = da.getMonth() + 1
  month = month >= 10 ? month : '0' + month
  let date = da.getDate()
  date = date >= 10 ? date : '0' + date
  let h = da.getHours()
  h = h >= 10 ? h : '0' + h
  let m = da.getMinutes()
  m = m >= 10 ? m : '0' + m
  let s = da.getSeconds()
  s = s >= 10 ? s : '0' + s
  return year + '-' + month + '-' + date + '  ' + h + ':' + m + ':' + s
}
// 过滤数据
// export const filterData = function (val, data) {
//   return data.filter(function (el) {
//     return el.name.indexOf(val) > -1
//   })
// }
//ajax错误处理
export const catchError = function(error) {
  if (error.response) {
    switch (error.response.status) {
      case 400:
        Vue.prototype.$message({
          message: error.response.data.message || '请求参数异常',
          type: 'error',
        })
        break
      case 401:
        sessionStorage.removeItem('user')
        Vue.prototype.$message({
          message: error.response.data.message || '密码错误或账号不存在！',
          type: 'warning',
          onClose: function() {
            location.reload()
          },
        })
        break
      case 403:
        // window.location.href = 'http://user.gw-ec.com/login/index/login/'
        Vue.prototype.$message({
          message:
            error.response.data.message || '无访问权限，请联系企业管理员',
          type: 'warning',
        })
        break
      default:
        Vue.prototype.$message({
          message: error.response.data.message || '服务端异常，请联系技术支持',
          type: 'error',
        })
    }
  }
  return Promise.reject(error)
}

export const sNameRule = function(rule, value, callback) {
  let regExp = /^[0-9A-Za-z_\-]+$/
  if (!regExp.test(value)) {
    callback(new Error('只支持字母、数字、下划线'))
  } else {
    callback()
  }
}

export const dotRule = function(rule, value, callback) {
  let regExp = /^[0-9A-Za-z\-.]{1,50}$/
  if (!regExp.test(value)) {
    callback(new Error('不能含有中文和特殊字符,且最多不可超过50字符'))
  } else {
    callback()
  }
}
export const isNumber = function(rule, value, callback) {
  if (!value || value < 1) {
    return callback(new Error('不能为空，且不能小于1'))
  }

  if (!Number.isInteger(value)) {
    callback(new Error('请输入数字值'))
  }
}
// codeRule
export const codeRule = function(rule, value, callback) {
  let regExp = /[^\u4e00-\u9fa5]+$/
  if (regExp.test(value)) {
    callback(new Error('不能含有中文和特殊字符'))
  } else {
    callback()
  }
}
// 获取用户名
export const getUserName = function(key) {
  let userName = ''
  var name = key + '='
  var ca = document.cookie.split(';')
  ca.forEach(ele => {
    if (ele.indexOf(name) > -1) {
      userName = ele.split('=')[1]
    }
  })
  return userName
}

// 过滤数据
export const filterData = function(val, type, data) {
  if (type) {
    return data.filter(function(ele) {
      return ele[type].indexOf(val) > -1
    })
  } else {
    let flag = []
    for (let key in data) {
      if (data[key].indexOf(val) > -1 || key.indexOf(val) > -1) {
        flag.push(data[key])
      }
    }
    return flag
  }
}

// 返回对象数组中指定的key的值（一个数组）
export const getkeys = function(data, key) {
  return data.map(ele => {
    return ele[key]
  })
}

// 数组去重
export const arrRe = function(data) {
  return data.filter((ele, index, farr) => {
    return farr.indexOf(ele) === index
  })
}
// 数组排序
export const arrSort = function(data) {
  return data.sort(function(a, b) {
    return a - b
  })
}

// 分页高度控制
export const addPaginationHeight = function(classStr, dom) {
  if (classStr.indexOf('showPagination') < 0) {
    dom.classList.add('showPagination')
  }
}

export const removePaginationHeight = function(dom) {
  dom.classList.remove('showPagination')
}

// 确认弹窗页面跳转回指定列表页
export const confirmMsg = function(msg, that, routeName) {
  that
    .$confirm(
      '<strong>' + msg + '</strong> 点击<i> 确认 </i>可回到列表页',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true,
      }
    )
    .then(() => {
      that.$router.replace({
        name: routeName,
      })
    })
    .catch(err => {})
}
// 成功提示
export const successMsg = function(msg, that) {
  that.$message({
    message: msg,
    type: 'success',
  })
}

export const apiFn = function() {
  return async function name(api, type, param, that, isLoading) {
    let loading = isLoading ? initLoading() : ''
    return api[type]
      .r(param)
      .then(res => {
        isLoading ? loading.close() : ''
        if (
          res.headers['content-type'] &&
          res.headers['content-type'].indexOf('application/force-download') > -1
        ) {
          return res
        } else if (
          res.data.successful ||
          Object.prototype.toString.call(res.data) === '[object Array]'
        ) {
          return res.data
        } else {
          res.data.msg ? that.$message.error(res.data.msg) : ''
          if (res.data.msg == '登陆已失效') {
            that.$router.replace('/login')
          }
          return res.data
        }
      })
      .catch(err => {
        that.$message.error(err)
      })
  }
}

export const isJSON = function(str) {
  if (typeof str == 'string') {
    try {
      JSON.parse(str)
      return true
    } catch (e) {
      return false
    }
  }
}

// 判断对象的值是否存在
const checkObjValIsNull = function(obj) {
  let flag = false
  for (let key in obj) {
    if (obj[key] == null || obj[key] == '') {
      flag = true
    }
  }
  return flag
}
export const arrObjValisNull = function(arr) {
  let flag = true
  arr.forEach(ele => {
    if (checkObjValIsNull(ele)) {
      flag = false
    }
  })
  return flag
}

export const isAdmin = function() {
  let userInfo = session('userInfo')
  let flag = false
  if (userInfo && userInfo != '') {
    userInfo = JSON.parse(userInfo)
    flag = userInfo.isAdmin == 1 ? true : false
  }
  return flag
}
