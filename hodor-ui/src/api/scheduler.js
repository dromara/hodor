// 调度器接口函数
import http from "@/tools/http"

export const getSchedulerInfoAPI = (name) => {
    return http.request({
        url: `/scheduler/info?name=${name}`,
        method: 'GET',
    })
}

export const getSchedulerListAPI = () => {
    return http.request({
        url: `/scheduler/list`,
        method: 'GET',
    })
}

export const getSchedulerMetadataAPI = (endpoint) => {
    return http.request({
        url: `/scheduler/metadata/endpoint=${endpoint}`,
        method: 'GET',
    })
}
