import instance from "./index"
const preUrlPath = "/app/service"

const onceSearceApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/oncejob/info/noceJobList`,
        params
      )

    }
  },
  checkRegister: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/isLoadedToQuartz`,
        params
      )

    }
  },
  batchHandle: {
    r: params => {
      return instance.post(`${preUrlPath}/oncejob/info/batchHandle`,
        params
      )

    }
  }


}

export {
  onceSearceApi
}
