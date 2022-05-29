import instance from "./index"
const preUrlPath = "/app/service/job"

const jobBindApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/bind/getJobGroupList`,
        params
      )

    }
  },
  getBindSession: {
    r: params => {
      return instance.post(`${preUrlPath}/bind/getBindSession`,
        params
      )

    }
  },
  bind: {
    r: params => {
      return instance.post(`${preUrlPath}/bind/doBind`,
        params
      )

    }
  },
  reBind: {
    r: params => {
      return instance.post(`${preUrlPath}/bind/doReBind`,
        params
      )

    }
  }

}

export {
  jobBindApi
}
