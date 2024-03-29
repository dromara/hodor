<script setup>
import {getSchedulerInfoAPI, getSchedulerListAPI} from '@/api/scheduler';
import {h, onMounted, ref} from 'vue';
import {Modal} from 'ant-design-vue';
import {timestampToTime} from '@/tools/timeUtil';

// ############ 列表查询 #############
// 表的列名
const columns = [{
  title: 'Name',  // 列名
  dataIndex: 'name',  // 数据名
  key: 'name',  // 列的key
}, {
  title: 'Role',
  dataIndex: 'role',
  key: 'role',
}, {
  title: 'ReportTime',
  dataIndex: 'reportTime',
  key: 'reportTime',
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
const schedulerList = ref([])
const getSchedulerList = async () => {
  const res = await getSchedulerListAPI()
  schedulerList.value = res.data.map((item) => {
    return {
      ...item,
      reportTime: timestampToTime(item.reportTime),
    }
  })

}
onMounted(() => {
  getSchedulerList()
})

// ############ 搜索 #############
const name = ref('')
const onSearch = async () => {
  if (name.value.length !== 0) {
    const res = await getSchedulerInfoAPI(name.value)
    schedulerList.value = []
    // 响应对象非空
    if (res.data && JSON.stringify(res.data) !== '{}') {
      schedulerList.value.push(res.data)
    }
  } else {  // 搜索框没内容
    getSchedulerList()
  }
}

// ############ 查看详情 #############
const onDetail = (scheduler) => {
  Modal.info({
    title: 'Scheduler Details',
    content: h('div', {}, [
      h('p', 'pid: ' + scheduler.pid),
      h('p', 'version: ' + scheduler.version),
      h('p', 'hostname: ' + scheduler.hostname)
    ])
  })
}

// ############ 监控信息 #############
const onMonitor = (scheduler) => {
  Modal.info({
    title: 'Scheduler Monitor Information',
    content: h('div', {}, [
      h('p', 'cpuUsage: ' + scheduler.cpuUsage),
      h('p', 'memoryUsage: ' + scheduler.memoryUsage),
      h('p', 'loadAverage: ' + scheduler.loadAverage)
    ])
  })
}
</script>

<template>
  <a-card>
    <a-row type="flex" justify="space-between">
      <a-col :span="6">
        <a-input-search
          v-model:value="name"
          class="search-container"
          placeholder="请输入你要搜索的节点"
          @search="onSearch"
        />
      </a-col>
    </a-row>
  </a-card>
  <a-card>
    <!-- 调度结点列表 -->
    <a-table :columns="columns" :data-source="schedulerList">
      <!-- 表内数据 -->
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
