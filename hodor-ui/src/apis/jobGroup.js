import httpInstance from "@/utils/http"


export const queryGroupListPaging = ({ groupName = '', pageNo, pageSize }) => {
    return httpInstance({
        url: `/group?groupName=${groupName}&pageNo=${pageNo}&pageSize=${pageSize}`,
        method: 'GET',
    })
};

export const createGroup = (params) => {
    return httpInstance({
        url: `/group`,
        method: 'POST',
        data:params,
    })
};

export const updateGroup = (params) => {
    return httpInstance({
        url: `/group`,
        method: 'PUT',
        data:params,
    })
};

export const deleteGroup = (id) => {
    return httpInstance({
        url: `/group?id=${id}`,
        method: 'DELETE',
    })
};

export const queryGroupListById = (id) => {
    return httpInstance({
        url: `/group/${id}`,
        method: 'GET',
    })
};
