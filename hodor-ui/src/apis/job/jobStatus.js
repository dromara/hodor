import httpInstance from "@/utils/http"

export const queryJobStatusListPagingAPI=({ pageNo, pageSize},jobStatusStr)=>{
    return httpInstance({
        url: `/jobExecDetail?pageNo=${pageNo}&pageSize=${pageSize}${jobStatusStr}`,
        method: 'GET',
    })
}

export const killRunningJobAPI=(params)=>{
    return httpInstance({
        url: `/jobExecDetail/kill`,
        method: 'POST',
        data:params,
    })
}

export const queryExecuteLogAPI=(paramsStr)=>{
    return httpInstance({
        url: `/jobExecDetail/logs?${paramsStr}`,
        method: 'GET',
    })
}
