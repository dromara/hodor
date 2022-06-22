import instance from "./index"
const preUrlPath = "/app/service"


const monitorApi = {
  init: {
    r: params => {
      return instance.post(
        `${preUrlPath}/quickConfiguration/getAllRmsErrorCodes`,
        params
      )

    }
  },
  addSave: {
    r: params => {
      return instance.post(
        `${preUrlPath}/quickConfiguration/saveRmsErrorCode`,
        params
      )

    }
  },

  update: {
    r: params => {
      return instance.post(
        `${preUrlPath}/quickConfiguration/updateErrorCode`,
        params
      )

    }
  }



}
const configMonitorApi = {
  getRmsGroupConfigInfoPage: {
    r: params => {
      return instance.post(
        `${preUrlPath}/quickConfiguration/getRmsGroupConfigInfoPage`,
        params
      )

    }
  },
  getAllRmsErrorCodes: {
    r: params => {
      return instance.post(
        `${preUrlPath}/quickConfiguration/getAllRmsErrorCodes`,
        params
      )

    }
  },
  getGroup: {
    r: params => {
      return instance.post(
        `${preUrlPath}/job/group/getGroupList`,
        params
      )

    }
  },
  save: {
    r: params => {
      return instance.post(
        `${preUrlPath}/quickConfiguration/saveAlarmConfiguration`,
        params
      )

    }
  }

}

const cronTransferApi = {
  generateCron: {
    r: params => {
      return instance.post(
        `${preUrlPath}/ser/basic/generateCron`,
        params
      )

    }
  },
  transferBatch: {
    r: params => {
      return instance.post(
        `${preUrlPath}/ser/basic/transferBatch`,
        params
      )

    }
  }
}

export {
  monitorApi,
  configMonitorApi,
  cronTransferApi
}
