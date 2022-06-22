import instance from "./index"
const preUrlPath = "/app/service"

const nodeInfoApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/job/groupNode/groupNodeList`,
        params
      )

    }
  },
  jobInit: {
    r: params => {
      return instance.post(`${preUrlPath}/job/jobNode/jobNodeList`,
        params
      )

    }
  },
  changeWeight: {
    r: params => {
      return instance.post(`${preUrlPath}/job/groupNode/changeWeight`,
        params
      )

    }
  },
  changeStatus: {
    r: params => {
      return instance.post(`${preUrlPath}/job/groupNode/changeStatus`,
        params
      )

    }
  },
  jobNodeList: {
    r: params => {
      return instance.post(`${preUrlPath}/job/jobNode/jobNodeList`,
        params
      )

    }
  },
  updateJobLoadBalance: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/updateJobLoadBalance`,
        params
      )

    }
  },
  jobChangeWeight: {
    r: params => {
      return instance.post(`${preUrlPath}/job/jobNode/changeWeight`,
        params
      )

    }
  },
  jobChangeStatus: {
    r: params => {
      return instance.post(`${preUrlPath}/job/jobNode/changeStatus`,
        params
      )

    }
  },
  getBalanceState: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/getJobLoadBalance`,
        params
      )

    }
  }

}

export {
  nodeInfoApi
}
