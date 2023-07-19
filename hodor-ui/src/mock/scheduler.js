// 调度器api模拟数据
import Mock from "mockjs"
import { getQuery } from "./getQuery"

const preUrlPath = "/hodor/admin/scheduler"

const schedulerList = []
for (let i = 0; i < 30; i++) {
    schedulerList.push({
        "name": `Scheduler${i}`,
        "role": Mock.mock("@name"),
        "reportTime": Mock.mock("@time"),
        "ip": Mock.mock("@ip"),
        "port": Mock.mock("@natural(1, 65535)"),
        "pid": Mock.mock("@natural"),
        "version": 1,
        "hostname": Mock.mock("@string(5)"),
        "cpuUsage": 0,
        "memoryUsage": 0,
        "loadAverage": 0,
    })
}

// 调度器列表
const schedulersInfo = {
    "successful": true,
    "code": 200,
    "msg": "success",
    "data": schedulerList
}

// 单个调度器信息
const schedulerInfo = {
    "successful": true,
    "code": 200,
    "msg": "success",
    "data": {},
}

// metadata信息
const metadataInfo = {
    "successful": true,
    "code": 200,
    "msg": "success",
    "data": {
        "intervalOffsets": [0, 1000000000],
        "copySets": [
            {
                "id": 0,
                "dataInterval": {
                    "startInterval": 0,
                    "endInterval": 0
                },
                "leader": "127.0.0.1:8081",
                "servers": ["127.0.0.1:8081", "127.0.0.1:8082", "127.0.0.1:8083"]
            }
        ]
    },
}

export default [
    {
        url: `${preUrlPath}/list`,
        method: "get",
        response: () => {
            return schedulersInfo
        }
    },
    {
        url: `${preUrlPath}/info`,
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
    },
    {
        url: `${preUrlPath}/metadata`,
        method: "get",
        response: () => {
            return metadataInfo
        }
    }
]