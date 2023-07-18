// 执行器api模拟数据
import Mock from "mockjs"
import { getQuery } from "./getQuery"

const actuatorListDataNodes = []
for (let i = 0; i < 3; i++) {
    actuatorListDataNodes.push({
        "ip": Mock.mock("@ip"),
        "port": Mock.mock("@natural"),
        "pid": Mock.mock("@natural"),
        "version": 1,
        "hostname": Mock.mock("@string(5)"),
        "cpuRatio": 0,
        "memoryRatio": 0,
        "loadAverageRatio": 0,
        "queueSize": 0,
        "waitingQueueSize": 0,
        "executeCount": 0,
        "endpoint": 0
    })
}

const actuatorList = []
for (let i = 0; i < 10; i++) {
    actuatorList.push({
        "appName": `Actuator${i}`,
        "appKey": i,
        "nodes": actuatorListDataNodes,
    })
}

const actuatorsInfo = {
    "successful": true,
    "code": 200,
    "msg": "success",
    "data": actuatorList
}

const actuatorInfo = {
    "successful": true,
    "code": 200,
    "msg": "success",
    "data": {},
}

export default [
    {
        url: "/hodor/app/actuator/list",
        method: "get",
        response: () => {
            return actuatorsInfo
        }
    },
    {
        url: "/hodor/app/actuator/info",
        method: "get",
        response: (req) => {
            const appName = getQuery(req.url, 'appName')
            const index = actuatorList.findIndex((item) => item.appName === appName)
            if (index > -1) {
                actuatorInfo.data = actuatorList[index]
            } else {
                actuatorInfo.data = {}
            }
            return actuatorInfo
        }
    }
]