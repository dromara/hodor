import instance from "./index"
const preUrlPath = "/app/service"


const addJobApi = {
  getJobTypeList: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/getJobTypeList`,
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

  getGroupList: {
    r: params => {
      return instance.post(`${preUrlPath}/job/group/getGroupList`,
        params
      )

    }
  },
  addSave: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/saveJobDetail`,
        params
      )

    }
  },

  update: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/update`,
        params
      )

    }
  },
  batchUpload: {
    r: params => {
      return instance.post(`${preUrlPath}/job/group/import/hdpJobs`,
        params
      )

    }
  },
  transferFromCron: {
    r: params => {
      return instance.post(`${preUrlPath}/job/cron/transferFromCron`,
        params
      )

    }
  }


}

export {
  addJobApi
}
