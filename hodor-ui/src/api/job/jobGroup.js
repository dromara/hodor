import http from "@/tools/http"

export const queryGroupListPagingAPI = ({pageNo, pageSize}, groupName = "") => {
  return http.request({
    url: `/group?pageNo=${pageNo}&pageSize=${pageSize}&groupName=${groupName}`,
    method: 'GET',
  })
};

export const createGroupAPI = (params) => {
  return http.request({
    url: `/group`,
    method: 'POST',
    data: params,
  })
};

export const updateGroupAPI = (params) => {
  return http.request({
    url: `/group`,
    method: 'PUT',
    data: params,
  })
};

export const deleteGroupAPI = (id) => {
  return http.request({
    url: `/group?id=${id}`,
    method: 'DELETE',
  })
};

export const queryGroupListByIdAPI = (id) => {
  return http.request({
    url: `/group/${id}`,
    method: 'GET',
  })
};

export const bindGroupActuatorAPI = (params) => {
  return http.request({
    url: `/group/bindActuator`,
    method: 'POST',
    data: params,
  })
};

export const getBindListAPI = () => {
  return http.request({
    url: `/group/listBinding`,
    method: 'GET',
  })
};
