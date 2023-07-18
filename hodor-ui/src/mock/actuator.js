// 执行器api模拟数据
import Mock from "mockjs"
import { getQuery } from "./getQuery"

const actuatorList = []
for (let i = 0; i < 30; i++) {
    actuatorList.push({
        "name": `Actuator${i}`,
        "nodeInfo": {
            "ip": Mock.mock("@ip"),
            "port": Mock.mock("@natural"),
            "pid": Mock.mock("@natural"),
            "version": "1",
            "hostname": Mock.mock("@string(5)"),
            "cpuUsage": 0,
            "memoryUsage": 0,
            "loadAverage": 0,
            "queueSize": 0,
            "waitingQueueSize": 0,
            "executeCount": 0,
            "endpoint": 0,
        },
        "nodeEndpoint": "",
        "groupNames": [Mock.mock("@name"), Mock.mock("@name")],
        "lastHeartbeat": 0
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
            const name = getQuery(req.url, 'name')
            const index = actuatorList.findIndex((item) => item.name === name)
            if (index > -1) {
                actuatorInfo.data = actuatorList[index]
            } else {
                actuatorInfo.data = {}
            }
            return actuatorInfo
        }
    }
]