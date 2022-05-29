import instance from "./index"
const preUrlPath = "/app/service"

const workFlowApi = {
  getGroupList: {
    r: params => {
      return instance.post(`${preUrlPath}/job/group/list`,
        params
      )

    }
  },
  getJobList: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/getJobListByGroupName`,
        params
      )

    }
  },
  fireDependence: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/fireNowWithDependence`,
        params
      )

    }
  },
  getJobWorkFlow: {
    r: params => {
      return instance.post(`${preUrlPath}/job/workflow/getJobWorkFlow`,
        params
      )

    }
  },
  disabled: {
    r: params => {
      return instance.post(`${preUrlPath}/job/workflow/disabledJob`,
        params
      )

    }
  },
  enable: {
    r: params => {
      return instance.post(`${preUrlPath}/job/workflow/enabledJob`,
        params
      )

    }
  },
  details: {
    r: params => {
      return instance.post(
        `${preUrlPath}/job/info/getJobInfoByGroupJobName`,
        params
      )

    }
  }


}

export {
  workFlowApi
}
