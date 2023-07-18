<script setup>
import { getActuatorListAPI, getActuatorInfoAPI } from '@/apis/actuator';
import { onMounted, ref, h } from 'vue';
import { Modal } from 'ant-design-vue';

// ############ 列表查询 #############
// 表的列名
const columns = [{
    title: 'AppKey',  // 列名
    dataIndex: 'appKey',  // 数据名
    key: 'appKey',  // 列的key
}, {
    title: 'AppName',
    dataIndex: 'appName',
    key: 'appName',
}, {
    title: 'IP',
    dataIndex: 'ip',
    key: 'ip',
}, {
    title: 'Port',
    dataIndex: 'port',
    key: 'port',
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

// 提取响应数据中的节点，一个执行器有多个节点
const getActuatorNodes = (data) => {
    const res = []
    data.forEach(actuator => {
        actuator.nodes.forEach(node => {
            res.push({
                "appName": actuator.appName,
                "appKey": actuator.appKey,
                "ip": node.ip,
                "port": node.port,
                "pid": node.pid,
                "version": node.version,
                "hostname": node.hostname,
                "cpuRatio": node.cpuRatio,
                "memoryRatio": node.memoryRatio,
                "loadAverageRatio": node.loadAverageRatio,
                "queueSize": node.queueSize,
                "waitingQueueSize": node.waitingQueueSize,
                "executeCount": node.executeCount,
                "endpoint": node.endpoint,
            })
        })
    })
    return res
}

onMounted(() => {
    getActuatorList()
})

// ############ 搜索 #############
// 搜索执行节点
const appName = ref('')
const onSearch = async () => {
    if (appName.value.length !== 0) {
        const res = await getActuatorInfoAPI(appName.value)
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
            h('p', 'cpuRatio: ' + actuator.cpuRatio),
            h('p', 'memoryRatio: ' + actuator.memoryRatio),
            h('p', 'loadAverageRatio: ' + actuator.loadAverageRatio),
            h('p', 'queueSize: ' + actuator.queueSize),
            h('p', 'waitingQueueSize: ' + actuator.waitingQueueSize),
            h('p', 'executeCount: ' + actuator.executeCount),
            h('p', 'endpoint: ' + actuator.endpoint),
        ]),
    })
}

</script>

<template>
    <span>执行节点列表</span>
    <span>
        <a-input-search class="search-container" placeholder="请输入你要搜索的节点" v-model:value="appName" @search="onSearch" />
    </span>
    <br />
    <div>
        <!-- 调度结点列表 -->
        <a-table :columns="columns" :data-source="actuatorList">
            <!-- 行数据 -->
            <template #bodyCell="{ column, text, record }">
                <template v-if="column.key === 'actions'">
                    <a-space>
                        <a-button type="primary" @click="onDetail(record)">查看详情</a-button>
                        <a-button type="primary" @click="onMonitor(record)">监控信息</a-button>
                    </a-space>
                </template>
            </template>
        </a-table>
    </div>
</template>

<style scoped>
.search-container {
    width: 200px;
    float: right;
}

;
</style>