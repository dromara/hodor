import instance from "./index"
const preUrlPath = "/app/service"

const userAuthApi = {
  getUserList: {
    r: params => {
      return instance.post(`${preUrlPath}/permit/getUserList`,
        params
      )

    }
  },
  getJobgroup: {
    r: params => {
      return instance.post(`${preUrlPath}/auth/getJobgroup`,
        params
      )

    }
  },
  getPermitList: {
    r: params => {
      return instance.post(`${preUrlPath}/permit/getPermitList`,
        params
      )

    }
  },
  saveGroupAuth: {
    r: params => {
      return instance.post(`${preUrlPath}/auth/auth`,
        params
      )

    }
  },
  groupUnauth: {
    r: params => {
      return instance.post(`${preUrlPath}/auth/unAuth`,
        params
      )

    }
  },
  saveOptAuth: {
    r: params => {
      return instance.post(`${preUrlPath}/permit/auth`,
        params
      )

    }
  },
  optUnauth: {
    r: params => {
      return instance.post(`${preUrlPath}/permit/unAuth`,
        params
      )

    }
  },
  getPermitInfo: {
    r: params => {
      return instance.post(`${preUrlPath}/permit/getPermitInfo`,
        params
      )

    }
  },
  getAuthInfos: {
    r: params => {
      return instance.post(`${preUrlPath}/auth/getAuthInfos`,
        params
      )

    }
  },
  updateOrSaveCompetence: {
    r: params => {
      return instance.post(
        `${preUrlPath}/competence/updateOrSaveCompetence`,
        params
      )

    }
  },
}

export {
  userAuthApi
}
