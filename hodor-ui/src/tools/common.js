
import { onUnmounted } from "vue";

/**
 * 开发环境
 */
export const isNotProduction = () => {
  return import.meta.env.NODE_ENV !== 'production'
}

/**
 * 定时时间
 * 
 * @param timeout   超时事件
 * @param callback  回调事件
 */
export const isTimeout = (timeout, callback) => {
  setTimeout(() => {
    callback();
  }, timeout.value * 1000);
  const Interval = setInterval(() => {
    timeout.value--;
  }, 1000);
  onUnmounted(() => {
    clearInterval(Interval);
  });
}

/**
 * 根据 当前路径 查询 所有父级 (包括当前)
 * 
 * @param arr 菜单列表
 * @param id 指定路由
 */
 export const findParentAll = (arr, id) => {
  var temp = []
  var forFn = function (list, path) {
    for (var i = 0; i < list.length; i++) {
      var item = list[i]
      if (item.path === path) {
        temp.push(findPathById(arr, item.id))
        forFn(list, item.parent);
        break
      } else {
        if (item.children) {
          forFn(item.children, path)
        }
      }
    }
  }
  forFn(arr, id)
  return temp
}

/**
 * 根据 当前路径 查询 所有父级 
 * 
 * @param arr 菜单列表
 * @param id  当前路由
 */
export const findParent = (arr, id) => {
  var temp = []
  var forFn = function (list, path) {
    for (var i = 0; i < list.length; i++) {
      var item = list[i]
      if (item.path === path) {
        let path = findPathById(arr, item.parent);
        if (path) {
          temp.push(path)
          forFn(arr, path);
        }
        break
      } else {
        if (item.children) {
          forFn(item.children, path)
        }
      }
    }
  }
  forFn(arr, id)
  return temp
}

/**
 * 根据 当前路径 查询 菜单编号
 * 
 * @param arr 菜单列表
 * @param id  当前路由
 */
export const findPathById = (arr, key) => {
  let path = null;
  var forFn = function (list, id) {
    for (var i = 0; i < list.length; i++) {
      var item = list[i]
      if (item.id === id) {
        path = item.path;
      } else {
        if (item.children) {
          forFn(item.children, id)
        }
      }
    }
    return path;
  }
  return forFn(arr, key);
}