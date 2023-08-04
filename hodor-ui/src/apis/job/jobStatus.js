import httpInstance from "@/utils/http"

export const queryJobStatusListPagingAPI=({ pageNo, pageSize},jobStatusStr)=>{
    return httpInstance({
        url: `/jobExecDetail?pageNo=${pageNo}&pageSize=${pageSize}${jobStatusStr}`,
        method: 'GET',
    })
}