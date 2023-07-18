// 执行器api模拟数据
import Mock from "mockjs"
import { getQuery } from "./getQuery"

const schedulerList = []
for (let i = 0; i < 30; i++) {
    schedulerList.push({
        "name": `scheduler${i}`,
        "role": Mock.mock("@name"),
        "reportTime": 0,
        "ip": Mock.mock("@ip"),
        "port": Mock.mock("@natural"),
        "pid": Mock.mock("@natural"),
        "version": 1,
        "hostname": Mock.mock("@string(5)"),
        "cpuUsage": 0,
        "memoryUsage": 0,
        "loadAverage": 0,
    })
}

const schedulersInfo = {
    "successful": true,
    "code": 200,
    "msg": "success",
    "data": schedulerList
}

const schedulerInfo = {
    "successful": true,
    "code": 200,
    "msg": "success",
    "data": {},
}

export default [
    {
        url: "/hodor/app/scheduler/list",
        method: "get",
        response: () => {
            return schedulersInfo
        }
    },
    {
        url: "/hodor/app/scheduler/info",
        method: "get",
        response: (req) => {
            const name = getQuery(req.url, 'name')
            const index = schedulerList.findIndex((item) => item.name === name)
            if (index > -1) {
                schedulerInfo.data = schedulerList[index]
            } else {
                schedulerInfo.data = {}
            }
            return schedulerInfo
        }
    }
]