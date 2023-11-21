/**
 * 存储对象
 * @param key 
 * @param value
 */
export const setStorage = function (key,value) {
    localStorage.setItem(key, JSON.stringify(value))
}

/**
 * 获取对象 
 * @param key
 */
export const getStorage = function (key) {
    return JSON.parse(localStorage.getItem(key))
}

/**
 * 删除对象
 * @param key 
 */
export const delStorage = function (key) {
    localStorage.removeItem(key)
}