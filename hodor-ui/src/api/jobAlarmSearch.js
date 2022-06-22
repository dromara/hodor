import instance from "./index"
const preUrlPath = "/app/service"

const jobAlarmSearchApi = {
  init: {
    r: params => {
      return instance.get(`${preUrlPath}/monitor/alarm`, {
        params
      })

    }
  },
  remove: {
    r: params => {
      return instance.delete(`${preUrlPath}/monitor/alarm`,
        {params}
      )

    }
  },



}

export {
  jobAlarmSearchApi
}
