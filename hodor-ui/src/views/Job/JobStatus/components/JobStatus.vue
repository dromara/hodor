<script setup>
import { onMounted, reactive, ref, onUpdated, watch, shallowRef, nextTick, onBeforeUnmount } from 'vue';
import { useJobStatusStore } from "@/stores/job/jobStatus";
import { useJobInfoStore } from "@/stores/job/jobInfo";
import { storeToRefs } from "pinia";
import { onBeforeRouteUpdate, useRoute } from 'vue-router';
import { EditorState } from '@codemirror/state'
import { Codemirror } from 'vue-codemirror'
import { javascript } from '@codemirror/lang-javascript'
import { oneDark } from '@codemirror/theme-one-dark'

const jobStatusStore = useJobStatusStore();
const { jobStatusList, paginationOpt, logOpt } = storeToRefs(jobStatusStore);
const { getJobStatusList, getQueryParams, killRunningJob, getExecuteLog } = jobStatusStore;

const jobInfoStore = useJobInfoStore();
const { jobInfoList } = storeToRefs(jobInfoStore);

const route = useRoute();

// 搜索框
const searchInfo = ref('');
const onSearch = searchValue => {
    console.log('use value', searchValue);
    console.log('or use this.value', searchInfo.value);
};
// 表格
const jobStatusColumns = ref([
    {
        title: '任务组名称',
        dataIndex: 'groupName',
        key: 'groupName',
    },
    {
        title: '任务名称',
        dataIndex: 'jobName',
        key: 'jobName',
    },
    {
        title: 'executeStatus',
        dataIndex: 'executeStatus',
        key: 'executeStatus',
    },
    {
        title: 'scheduleStart',
        dataIndex: 'scheduleStart',
        key: 'scheduleStart',
    },
    {
        title: 'scheduleEnd',
        dataIndex: 'scheduleEnd',
        key: 'scheduleEnd',
    },
    {
        title: 'executeStart',
        dataIndex: 'executeStart',
        key: 'executeStart',
    },
    {
        title: 'executeEnd',
        dataIndex: 'executeEnd',
        key: 'executeEnd',
    },
    {
        title: 'elapsedTime',
        dataIndex: 'elapsedTime',
        key: 'elapsedTime',
    },
    {
        title: 'isTimeout',
        dataIndex: 'isTimeout',
        key: 'isTimeout',
    },
    {
        title: '备注',
        dataIndex: 'comments',
        key: 'comments',
        width: "20%",
        ellipsis: true,
    },
    {
        title: "操作",
        dataIndex: "action",
        key: "action",
        fixed: 'right', //固定在表格右边
    },
]);
const jobStatus = ref();
// 多选
const selectedJobIds = ref([]);
const selectedJobInfos = ref([])
const rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
        selectedJobIds.value = selectedRowKeys;
        selectedJobInfos.value = selectedRows;
    },
};
// 执行日志
const visibleLog = ref(false);
// codemirror
const logData = ref('');
const extensions = [javascript(), oneDark];
const codeMirrorRef = ref(null);


const getLogData = async (jobStatusInfo) => {
    const { id, groupName, jobName, actuatorEndpoint } = jobStatusInfo;
    // console.log(id)
    const { offset, length } = logOpt.value;
    // console.log({ offset, length })
    // console.log(jobInfoList)
    const params = {
        requestId: id,
        groupName,
        jobName,
        actuatorEndpoint,
        timeout: 3000,
        offset,
        length,
    }
    // console.log("调用了getLogData", params)
    await getExecuteLog(params);
    const {executeStatus}=jobStatusInfo;
    console.log(executeStatus,logOpt.value.logData,timer)
    // 每次发送查看日志请求时,如果任务未处于执行状态且日志后面无内容且存在定时器,则停止定时器
    if(executeStatus!=='RUNNING'&&logOpt.value.logData===(''||null)&&timer){
        console.log("停止定时器")
        clearInterval(timer)
    }
    // console.log("logOpt.logData",logOpt.value.logData)
    if (logOpt.value.logData !== null) {
        logData.value += logOpt.value.logData;
    }
}
// 滚动条自动滚动到底部
const autoScroll = () => {
    let codemirrorScroll = document.getElementsByClassName('cm-scroller')[0];
    codemirrorScroll.scrollTop = codemirrorScroll.scrollHeight;
}

// 分页查询任务列表
const queryJobStatusListPaging = (paginationOpt, jobStatus) => {
    const { defaultCurrent, defaultPageSize } = paginationOpt.value;
    getJobStatusList({ pageNo: defaultCurrent, pageSize: defaultPageSize }, jobStatus);
};
// 杀死正在执行的任务
const killJob = async (jobInfo) => {
    await killRunningJob(jobInfo);
}
const handleClickKillRunningJob = async () => {
    selectedJobInfos.value.forEach((job) => {
        const { id, groupName, jobName, actuatorEndpoint } = job;
        const jobInfo = {
            requestId: id,
            groupName,
            jobName,
            actuatorEndpoint,
            timeout: 3000,
        }
        killJob(jobInfo);
    })

}
// 查看执行日志
let timer = null;
const handleClickGetExecuteLog = (jobStatusInfo) => {
    visibleLog.value = true;
    logData.value = '';
    logOpt.value.offset = 0;
    logOpt.value.length = 1000;
    if (timer) clearInterval(timer);
    timer = setInterval(() => {
        console.log("定时器")
        getLogData(jobStatusInfo);
    }, 1000);
}
const view = shallowRef()
const handleReady = (payload) => {
    view.value = payload.view
}
const handleCloseLog = () => {
    if (timer) clearInterval(timer)
}

// 监听路由参数变化
onBeforeRouteUpdate(to => {
    queryJobStatusListPaging(paginationOpt, to.query)
})

watch(
    () => logData.value,
    () => {
        nextTick(() => {
            autoScroll();
        });
    }
);



onMounted(() => {
    const queryParam = route.query;
    getQueryParams(queryParam);
    queryJobStatusListPaging(paginationOpt, queryParam);
})
onBeforeUnmount(() => {
    if (timer) {
        clearInterval(timer);
    }
})
</script>

<template>
    <a-card>
        <a-row :gutter="24">
            <a-col :span="4">
                <a-button type="primary" @click="handleClickKillRunningJob">杀死正在执行的任务</a-button>
            </a-col>
            <!-- <a-col :span="16">
                <a-input-search v-model:value="searchInfo" placeholder="请输入内容" enter-button @search="onSearch" />
            </a-col> -->
        </a-row>
    </a-card>
    <br />
    <a-card>
        <a-table :columns="jobStatusColumns" :data-source="jobStatusList" bordered :scroll="{ x: true }"
            :row-selection="rowSelection" :rowKey="row => row.id" :pagination="paginationOpt">
            <template #bodyCell="{ column, text, record }">
                <template v-if="column.dataIndex === 'action'">
                    <a-button type="primary" @click="handleClickGetExecuteLog(record)">查看执行日志</a-button>
                </template>
            </template>
        </a-table>
    </a-card>
    <a-drawer v-model:open="visibleLog" class="custom-class" root-class-name="root-class-name" size="large" title="查看执行日志"
        placement="right" closable @close="handleCloseLog">
        <codemirror ref="codeMirrorRef" v-model="logData" :style="{ height: '400px' }" :autofocus="true"
            :indent-with-tab="true" :tab-size="2" :extensions="extensions" @ready="handleReady" />
    </a-drawer>
</template>

<style scoped>
:deep(.ant-table) {
    white-space: nowrap;
}
</style>
