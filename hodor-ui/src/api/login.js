import instance from "./index"

const preUrlPath = "app"

//登录
const login = {
  r: params => {
    return instance.post(`${preUrlPath}/login`, params)
  },
}
//退出
const logOut = {
  r: params => {
    return instance.post(`${preUrlPath}/logout`, params)

    }

}


export {
  login,
  logOut,
}
