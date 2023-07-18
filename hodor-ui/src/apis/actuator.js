// 执行器接口函数
import httpInstance from "@/utils/http"

export const getActuatorInfoAPI = (appName) => {
    return httpInstance({
        url: `/hodor/app/actuator/info?appName=${appName}`,
        method: 'GET',
    })
}

export const getActuatorListAPI = () => {
    return httpInstance({
        url: `/hodor/app/actuator/list`,
        method: 'GET',
    })
}