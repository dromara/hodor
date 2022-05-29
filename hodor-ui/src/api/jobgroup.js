import instance from "./index"
const preUrlPath = "/app/service"

const jobgroupApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/job/group/listPage`,
        params
      )

    }
  },
  search: {
    r: params => {
      return instance.post(`${preUrlPath}/selectByKey`,
        params
      )

    }
  },
  add: {
    r: params => {
      return instance.post(`${preUrlPath}/job/group/add`,
        params
      )

    }
  },
  remove: {
    r: params => {
      return instance.post(`${preUrlPath}/job/group/delete`,
        params
      )

    }
  }

}

export {
  jobgroupApi
}
