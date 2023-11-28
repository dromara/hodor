import http from "@/tools/http"

export const queryJobInfoListPagingAPI = ({pageNo, pageSize}, jobInfoStr) => {
  return http.request({
    url: `/jobOperator?pageNo=${pageNo}&pageSize=${pageSize}${jobInfoStr}`,
    method: 'GET',
  })
};

export const createJobAPI = (params) => {
  return http.request({
    url: `/jobOperator`,
    method: 'POST',
    data: params,
  })
};

export const updateJobAPI = (params) => {
  return http.request({
    url: `/jobOperator`,
    method: 'PUT',
    data: params,
  })
};

export const deleteJobAPI = (id) => {
  return http.request({
    url: `/jobOperator/delete/${id}`,
    method: 'DELETE',
  })
};

export const queryGroupListByIdAPI = (id) => {
  return http.request({
    url: `/jobOperator/${id}`,
    method: 'GET',
  })
};

export const executeJobAPI = (id) => {
  return http.request({
    url: `/jobOperator/execute/${id}`,
    method: 'POST',
  })
};

export const resumeJobAPI = (id) => {
  return http.request({
    url: `/jobOperator/resume/${id}`,
    method: 'POST',
  })
};

export const stopJobAPI = (id) => {
  return http.request({
    url: `/jobOperator/stop/${id}`,
    method: 'POST',
    data: id,
  })
};

export const getJobTypeNamesAPI = (clusterName) => {
  return http.request({
    url: `/actuator/jobTypeNames?clusterName=${clusterName}`,
    method: 'GET',
  })
}
