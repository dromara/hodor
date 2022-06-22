import instance from "./index"
const preUrlPath = "/app/service"


const statisticsApi = {
  initGroup: {
    r: params => {
      return instance.post(
        `${preUrlPath}/job/group/list`,
        params
      )

    }
  },
  jobExeInfo: {
    r: params => {
      return instance.post(
        `${preUrlPath}/job/stat/jobExeInfo`,
        params
      )

    }
  },
  search: {
    r: params => {
      return instance.post(
        `${preUrlPath}/job/stat/rankConsumerTime`,
        params
      )

    }
  },
  jobStatusStatistic: {
    r: params => {
      return instance.get(
        `${preUrlPath}/job/stat/jobStatusStatistic`,
        {params}
      )

    }
  }

}




export {
  statisticsApi
}
