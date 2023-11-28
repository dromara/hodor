/**
 * 路径拼接
 * 
 * @param 根路径
 * @param 子路径 
 */
 export const resolve = (root, path) => {
    if (path.startsWith('/')) {
        return root + path;
    } else {
        return root + "/" + path;
    }
}

/**
 * 模块化导入 
 * 
 * @param context 
 */
export const module = context => {
    return Object.keys(context).reduce((modules, key) => {
        return {
            ...modules,
            ...context[key].default
        }
    }, {})
}