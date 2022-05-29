import instance from "./index"
const preUrlPath = "/app/service"

const jobRunDetailsApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/monitor/jobDetailQuery`,
        params
      )

    }
  },
  rerun: {
    r: params => {
      return instance.post(`${preUrlPath}/monitor/rerun`,
        params
      )

    }
  },
  getLogs: {
    r: params => {
      return instance.post(`${preUrlPath}/monitor/getJobExeLogs`,
        params
      )

    }
  },
  flowDetails: {
    r: params => {
      return instance.post(`${preUrlPath}/job/stat/getJobRunState`,
        params
      )

    }
  },
  syncData: {
    r: params => {
      return instance.post(`${preUrlPath}/monitor/syncData`,
        params
      )

    }
  },
  killJob: {
    r: params => {
      return instance.post(`${preUrlPath}/monitor/killJobNow`,
        params
      )

    }
  },
  PauseJobExecute: {
    r: params => {
      return instance.post(`${preUrlPath}/monitor/stopJobExecute`,
        params
      )

    }
  },



}

export {
  jobRunDetailsApi
}
