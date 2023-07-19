// 调度器接口函数
import httpInstance from "@/utils/http"

export const getSchedulerInfoAPI = (name) => {
    return httpInstance({
        url: `/scheduler/info?name=${name}`,
        method: 'GET',
    })
}

export const getActuatorListAPI = () => {
    return httpInstance({
        url: `/scheduler/list`,
        method: 'GET',
    })
}

export const getSchedulerMetadataAPI = (endpoint) => {
    return httpInstance({
        url: `/scheduler/metadata/endpoint=${endpoint}`,
        method: 'GET',
    })
}