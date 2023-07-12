// 用户相关接口函数
import httpInstance from "@/utils/http"

export const loginAPI = ({ username, password }) => {
    return httpInstance({
        url: `/hodor/app/login/?username=${username}&password=${password}`,
        method: 'POST',
    })
}