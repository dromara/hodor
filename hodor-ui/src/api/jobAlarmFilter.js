import instance from "./index"
const preUrlPath = "/app/service/job"

const jobAlarmFilterApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/alarmFilter/list`,
        params
      )

    }
  },
  batchDelete: {
    r: params => {
      return instance.post(`${preUrlPath}/alarmFilter/batchDelete`,
        params
      )

    }
  },
  add: {
    r: params => {
      return instance.post(`${preUrlPath}/alarmFilter/add`,
        params
      )

    }
  },
  getGroupList:{
    r: params => {
      return instance.post(`${preUrlPath}/group/getGroupList`,
        params
      )

    }
  }


}

export {
  jobAlarmFilterApi
}
