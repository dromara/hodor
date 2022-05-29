import instance from "./index"
const preUrlPath = "/app/service"

const userActionApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/user/userActionList`,
        params
      )

    }
  },
}
const userManageApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/user/listUser`,
        params
      )

    }
  },
  checkUserName: {
    r: params => {
      return instance.post(`${preUrlPath}/user/checkUserName`,
        params
      )

    }
  },
  addSave: {
    r: params => {
      return instance.post(`${preUrlPath}/user/saveUser`,
        params
      )

    }
  },
  update: {
    r: params => {
      return instance.post(`${preUrlPath}/user/updateUser`,
        params
      )

    }
  }
}
export {
  userActionApi,
  userManageApi
}
