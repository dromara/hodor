import instance from "./index"
const preUrlPath = "/app/service"

const serviceApi = {
  init: {
    r: params => {
      return instance.post(`${preUrlPath}/ser/basic/list`,
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
  divideJob: {
    r: params => {
      return instance.post(`${preUrlPath}/job/info/divideJob`,
        params
      )

    }
  },
  setRoot: {
    r: params => {
      return instance.post(`${preUrlPath}/selectByKey`,
        params
      )

    }
  },
  checkInfo: {
    r: params => {
      return instance.post(`${preUrlPath}/ser/basic/subJob`,
        params
      )

    }
  },
  saveSechedulerTimeOut: {
    r: params => {
      return instance.post(`${preUrlPath}/ser/basic/saveSechedulerTimeOut`,
        params
      )

    }
  },
  getSechedulerTimeOut: {
    r: params => {
      return instance.post(`${preUrlPath}/ser/basic/getSechedulerTimeOut`,
        params
      )

    }
  }

}

export {
  serviceApi
}
