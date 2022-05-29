import instance from "./index"
const preUrlPath = "/app/service"

const jobAlarmApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/job/alarm/list`,
        params
      )

    }
  },
  update: {
    r: params => {
      return instance.post(`${preUrlPath}/job/alarm/update`,
        params
      )

    }
  },

  remove: {
    r: params => {
      return instance.post(`${preUrlPath}/job/alarm/delete`,
        params
      )

    }
  }


}

export {
  jobAlarmApi
}
