import httpInstance from "@/utils/http"


export const queryGroupListPagingAPI = ({ pageNo, pageSize },groupName="") => {
    return httpInstance({
        url: `/group?pageNo=${pageNo}&pageSize=${pageSize}&groupName=${groupName}`,
        method: 'GET',
    })
};

export const createGroupAPI = (params) => {
    return httpInstance({
        url: `/group`,
        method: 'POST',
        data:params,
    })
};

export const updateGroupAPI = (params) => {
    return httpInstance({
        url: `/group`,
        method: 'PUT',
        data:params,
    })
};

export const deleteGroupAPI = (id) => {
    return httpInstance({
        url: `/group?id=${id}`,
        method: 'DELETE',
    })
};

export const queryGroupListByIdAPI = (id) => {
    return httpInstance({
        url: `/group/${id}`,
        method: 'GET',
    })
};

export const bindGroupActuatorAPI = (params) => {
    return httpInstance({
        url: `/group/bindActuator`,
        method: 'POST',
        data:params,
    })
};

export const getBindListAPI = () => {
    return httpInstance({
        url: `/group/listBinding`,
        method: 'GET',
    })
};
