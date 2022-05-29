import instance from "."
const preUrlPath = "/app/service"
const targetUrl='/user/recommend'

const feedbackApi = {
  init: {
    r: params => {
      return instance.get(`${preUrlPath}${targetUrl}`,
        {params}
      )

    }
  },
  remove: {
    r: params => {
      return instance.delete(`${preUrlPath}${targetUrl}`,
        {params}
      )

    }
  },
  save: {
    r: params => {
      return instance.post(`${preUrlPath}${targetUrl}`,
        params
      )

    }
  },


}

export {
  feedbackApi
}
