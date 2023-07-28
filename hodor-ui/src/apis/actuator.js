// 执行器接口函数
import httpInstance from "@/utils/http"

export const getActuatorInfoAPI = (name) => {
    return httpInstance({
        url: `/actuator/info?name=${name}`,
        method: 'GET',
    })
}

export const getActuatorListAPI = () => {
    return httpInstance({
        url: `/actuator/list`,
        method: 'GET',
    })
}

export const getAllClustersAPI = () => {
    return httpInstance({
        url: `/actuator/clusterNames`,
        method: 'GET',
    })
}
