// 执行器接口函数
import http from "@/tools/http"

export const getActuatorInfoAPI = (name) => {
    return http.request({
        url: `/actuator/info?name=${name}`,
        method: 'GET',
    })
}

export const getActuatorListAPI = () => {
    return http.request({
        url: `/actuator/list`,
        method: 'GET',
    })
}

export const getAllClustersAPI = () => {
    return http.request({
        url: `/actuator/clusterNames`,
        method: 'GET',
    })
}
