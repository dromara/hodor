// 根据url获取query参数
export const getQuery = (url, name) => {
    const index = url.indexOf('?')
    if (index !== -1) {
        const queryStrArr = url.substr(index + 1).split('&')
        for (var i = 0; i < queryStrArr.length; i++) {
            const itemArr = queryStrArr[i].split('=')
            if (itemArr[0] === name) {
                return itemArr[1]
            }
        }
    }
    return null
}