import http from "@/tools/http"

export const queryJobStatusListPagingAPI = ({pageNo, pageSize}, jobStatusStr) => {
  return http.request({
    url: `/jobExecDetail?pageNo=${pageNo}&pageSize=${pageSize}${jobStatusStr}`,
    method: 'GET',
  })
}

export const killRunningJobAPI = (params) => {
  return http.request({
    url: `/jobExecDetail/kill`,
    method: 'POST',
    data: params,
  })
}

export const queryExecuteLogAPI = (paramsStr) => {
  return http.request({
    url: `/jobExecDetail/logs?${paramsStr}`,
    method: 'GET',
  })
}
