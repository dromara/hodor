// 用户相关接口函数
import http from "@/tools/http"

export const loginAPI = ({ username, password }) => {
    return http.request({
        url: `/login/?username=${username}&password=${password}`,
        method: 'POST',
    })
}
