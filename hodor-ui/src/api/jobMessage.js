import instance from "./index"
const preUrlPath = "/app/service"

const jobMessageApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/search`,
        params
      )

    }
  },
  getGroupList: {
    r: params => {
      return instance.post(`${preUrlPath}/job/group/list`,
        params
      )

    }
  },
  getJobListByGroupName: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/getJobListByGroupName`,
        params
      )

    }
  },
  getJobTypeList: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/getJobTypeList`,
        params
      )

    }
  },
  batch: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/batchHandle`,
        params
      )

    }
  },
  batchExport: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/batchExport`,
        params
      )

    }
  },
  firenow: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/fireNow`,
        params
      )

    }
  },
  register: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/isLoadedToQuartz`,
        params
      )

    }
  },
  restartJob: {
    r: params => {
      return instance.post(`${preUrlPath}/monitor/restartJob`,
        params
      )

    }
  },
  resume: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/resume`,
        params
      )

    }
  },
  pause: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/pause`,
        params
      )

    }
  },

  jobStatistics: {
    r: params => {
      return instance.post(`${preUrlPath}/monitor/jobStatistics`,
        params
      )

    }
  },
  getJobInfo: {
    r: params => {
      return instance.get(`${preUrlPath}/job/info/getJobInfoByGroupJobName`, {
        params
      })

    }
  }

}

export {
  jobMessageApi
}
