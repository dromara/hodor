import httpInstance from "@/utils/http"

export const queryJobInfoListPagingAPI = ({ pageNo, pageSize},jobInfoStr) => {
    return httpInstance({
        url: `/jobOperator?pageNo=${pageNo}&pageSize=${pageSize}${jobInfoStr}`,
        method: 'GET',
    })
};

export const createJobAPI = (params) => {
    return httpInstance({
        url: `/jobOperator`,
        method: 'POST',
        data:params,
    })
};

export const updateJobAPI = (params) => {
    return httpInstance({
        url: `/jobOperator`,
        method: 'PUT',
        data:params,
    })
};

export const deleteGroupAPI = (id) => {
    return httpInstance({
        url: `/jobOperator/delete/${id}`,
        method: 'DELETE',
    })
};

export const queryGroupListByIdAPI = (id) => {
    return httpInstance({
        url: `/jobOperator/${id}`,
        method: 'GET',
    })
};

export const executeJobAPI = (id) => {
    return httpInstance({
        url: `/jobOperator/execute/${id}`,
        method: 'POST',
    })
};

export const resumeJobAPI = (id) => {
    return httpInstance({
        url: `/jobOperator/resume/${id}`,
        method: 'POST',
    })
};

export const stopJobAPI = (id) => {
    return httpInstance({
        url: `/jobOperator/stop/${id}`,
        method: 'POST',
        data:id,
    })
};
