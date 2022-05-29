import instance from "./index"
const preUrlPath = "/app/service"

const userRoleApi = {
    init  : {
    r: params => {
      return instance.post(`${preUrlPath}/role/getRoleList`,
        params
      )

    }
  },
  getRolePermitInfo: {
    r: params => {
      return instance.post(`${preUrlPath}/permit/getRolePermitInfo`,
        params
      )

    }
  },
  remove: {
    r: params => {
      return instance.post(`${preUrlPath}/role/deleteRole`,
        params
      )

    }
  },
  addRole: {
    r: params => {
      return instance.post(`${preUrlPath}/role/addRole`,
        params
      )

    }
  },
  extName:{
    r: params => {
        return instance.post(`${preUrlPath}/role/checkRoleExisted `,
          params
        )
  
      }
  }



}

export {
    userRoleApi
}
