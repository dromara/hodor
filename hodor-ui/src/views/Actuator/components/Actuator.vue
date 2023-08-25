<script setup>
import { getActuatorListAPI, getActuatorInfoAPI } from '@/apis/actuator';
import { onMounted, ref, h } from 'vue';
import { Modal } from 'ant-design-vue';
import { timestampToTime } from '@/utils/timeUtil'

// ############ 列表查询 #############
// 表的列名
const columns = [{
    title: 'Name',  // 列名
    dataIndex: 'name',  // 数据名
    key: 'name',  // 列的key
}, {
    title: 'IP',
    dataIndex: 'ip',
    key: 'ip',
}, {
    title: 'Port',
    dataIndex: 'port',
    key: 'port',
}, {
    title: 'GroupNames',
    dataIndex: 'groupNames',
    key: 'groupNames',
}, {
    title: 'LastHeartbeat',
    dataIndex: 'lastHeartbeat',
    key: 'lastHeartbeat',
}, {
    title: 'Actions',
    key: 'actions',
}];

// 获取执行节点列表数据
const actuatorList = ref([])
const getActuatorList = async () => {
    const res = await getActuatorListAPI()
    actuatorList.value = getActuatorNodes(res.data)
}

// 处理响应数据中执行器节点信息
const getActuatorNodes = (data) => {
    const res = []
    if (data === null) {
        return res;
    }
    data.forEach(actuator => {
        res.push({
            "name": actuator.name,
            "ip": actuator.nodeInfo.ip,
            "port": actuator.nodeInfo.port,
            "pid": actuator.nodeInfo.pid,
            "version": actuator.nodeInfo.version,
            "hostname": actuator.nodeInfo.hostname,
            "cpuUsage": actuator.nodeInfo.cpuUsage,
            "memoryUsage": actuator.nodeInfo.memoryUsage,
            "loadAverage": actuator.nodeInfo.loadAverage,
            "queueSize": actuator.nodeInfo.queueSize,
            "waitingQueueSize": actuator.nodeInfo.waitingQueueSize,
            "executeCount": actuator.nodeInfo.executeCount,
            "endpoint": actuator.nodeInfo.endpoint,
            "nodeEndpoint": actuator.nodeEndpoint,
            "groupNames": actuator.groupNames.toString(),
            "lastHeartbeat": timestampToTime(actuator.lastHeartbeat),
        })
    })
    return res
}

onMounted(() => {
    getActuatorList()
})

// ############ 搜索 #############
// 搜索执行节点
const name = ref('')
const onSearch = async () => {
    if (name.value.length !== 0) {
        const res = await getActuatorInfoAPI(name.value)
        actuatorList.value = []
        // 响应对象非空
        if (res.data && JSON.stringify(res.data) !== '{}') {
            actuatorList.value = getActuatorNodes([res.data])
        }
    } else {
        getActuatorList()
    }
}

// ############ 查看详情 #############
const onDetail = (actuator) => {
    Modal.info({
        title: 'Actuator Details',
        content: h('div', {}, [
            h('p', 'pid: ' + actuator.pid),
            h('p', 'version: ' + actuator.version),
            h('p', 'hostname: ' + actuator.hostname),
        ]),
    })
}

// ############ 监控信息 #############
const onMonitor = (actuator) => {
    Modal.info({
        title: 'Actuator Monitor Information',
        content: h('div', {}, [
            h('p', 'cpuUsage: ' + actuator.cpuUsage),
            h('p', 'memoryUsage: ' + actuator.memoryUsage),
            h('p', 'loadAverage: ' + actuator.loadAverage),
            h('p', 'queueSize: ' + actuator.queueSize),
            h('p', 'waitingQueueSize: ' + actuator.waitingQueueSize),
            h('p', 'executeCount: ' + actuator.executeCount),
            h('p', 'endpoint: ' + actuator.endpoint),
        ]),
    })
}
</script>

<template>
    <a-card>
        <h3 class="title">执行节点管理</h3>
        <span>展示执行节点信息，管理所有执行节点，具有查看详情、监控信息等功能</span>
    </a-card>
    <br/>
    <a-card>
        <span>执行节点列表</span>
        <span>
            <a-input-search class="search-container" placeholder="请输入你要搜索的节点" v-model:value="name" @search="onSearch" />
        </span>
    </a-card>
    <br />
    <a-card>
        <!-- 调度结点列表 -->
        <a-table :columns="columns" :data-source="actuatorList">
            <!-- 行数据 -->
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'actions'">
                    <a-space>
                        <a-button type="primary" @click="onDetail(record)">查看详情</a-button>
                        <a-button type="primary" @click="onMonitor(record)">监控信息</a-button>
                    </a-space>
                </template>
            </template>
        </a-table>
    </a-card>
</template>

<style scoped>
.search-container {
    width: 200px;
    float: right;
}
</style>
