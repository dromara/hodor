const baseResponse = {
  code: 0,
  result: undefined,
  message: 'ok',
  timestamp: 0,
}

/**
 * 随机生成
 * 
 * @returns {string}
 */
export const generatorToken = () => {
  const tempUrl = URL.createObjectURL(new Blob());
  const token = tempUrl.toString();
  URL.revokeObjectURL(tempUrl);
  return token.substr(token.lastIndexOf("/") + 1);
}

/**
 * 请求响应
 * 
 * @returns {string}
 */
export const generatorResponse = (data = null, message = 'request success', code = 200) => {
  const response = Object.assign({}, baseResponse, {
    result: data,
    message,
    code,
    timestamp: new Date().getTime()
  })
  return response
}

export const getRequestBody = request => {
  const {body} = request
  return JSON.parse(body)
}

export const getRolePermission = (isAdmin = false) => {
  return [
    {
      role: isAdmin ? 'admin' : 'other_user',
      pageId: 'permissionTest',
      actions: isAdmin ? ['add', 'upd', 'del'] : ['add', 'upd']
    },
  ]
}
