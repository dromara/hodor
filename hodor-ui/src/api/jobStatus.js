import instance from "./index"
const preUrlPath = "/app/service/job"

const jobStatusApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/check/list`,
        params
      )

    }
  },
  getGroupList: {
    r: params => {
      return instance.post(`${preUrlPath}/check/groupList`,
        params
      )

    }
  },
  add: {
    r: params => {
      return instance.post(`${preUrlPath}/check/add`,
        params
      )

    }
  },
  update: {
    r: params => {
      return instance.post(`${preUrlPath}/check/update`,
        params
      )

    }
  },
  remove:{
    r: params => {
      return instance.post(`${preUrlPath}/check/delete`,
        params
      )

    }
  }


}

export {
  jobStatusApi
}
