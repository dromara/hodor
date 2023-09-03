<script setup>
import { onBeforeMount, onMounted, reactive, ref, onUpdated, watch, shallowRef, nextTick, onBeforeUnmount, computed } from 'vue';
import { useJobStatusStore } from "@/stores/job/jobStatus";
import { useJobInfoStore } from "@/stores/job/jobInfo";
import { storeToRefs } from "pinia";
import { onBeforeRouteUpdate, useRoute } from 'vue-router';
// 代码编辑器
import { Codemirror } from 'vue-codemirror'
import { javascript } from '@codemirror/lang-javascript'
import { oneDark } from '@codemirror/theme-one-dark'

import RefreshButton from '@/components/RefreshButton.vue';

const jobStatusStore = useJobStatusStore();
const { jobStatusList, paginationOpt, logOpt, } = storeToRefs(jobStatusStore);
const { getJobStatusList, getQueryParams, killRunningJob, getExecuteLog } = jobStatusStore;

const jobInfoStore = useJobInfoStore();
const { jobInfoList } = storeToRefs(jobInfoStore);

const route = useRoute();

const routes = ref([
    {
        path: 'job-info',
        breadcrumbName: '任务信息管理',
    },
    {
        path: 'job-status',
        breadcrumbName: '任务执行状态',
    }
]);
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
// 面包屑
const showBread = computed(() => {
    return route.query.groupName !== undefined;
})


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
    const { executeStatus } = jobStatusInfo;
    // 每次发送查看日志请求时,如果任务未处于执行状态且日志后面无内容且存在定时器,则停止定时器
    if (executeStatus !== 'RUNNING' && logOpt.value.logData === ('' || null) && timer) {
        clearInterval(timer)
    }
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
const handleClickKillRunningJob = (job) => {
    const { id, groupName, jobName, actuatorEndpoint } = job;
    const jobInfo = {
        requestId: id,
        groupName,
        jobName,
        actuatorEndpoint,
        timeout: 3000,
    }
    killJob(jobInfo);
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

const refreshTable=()=>{
    const queryParam = route.query;
    getQueryParams(queryParam);
    queryJobStatusListPaging(paginationOpt, queryParam);
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
    <!-- <a-card>
        <a-row :gutter="24">
            <a-col :span="16">
                <a-input-search v-model:value="searchInfo" placeholder="请输入内容" enter-button @search="onSearch" />
            </a-col>
        </a-row>
    </a-card>
    <br /> -->
    <a-card>
        <h3 class="title">任务执行状态管理</h3>
        <span>展示任务执行状态信息，管理所有任务执行状态，具有查看任务状态的执行日志，杀死正在执行的任务等功能</span>
    </a-card>
    <br/>
    <a-card>
        <a-breadcrumb :routes="routes" v-show="showBread">
            <template #itemRender="{ route, paths }">
                <span v-if="routes.indexOf(route) === routes.length - 1">
                    {{ route.breadcrumbName }}
                </span>
                <router-link v-else :to="`${paths.join('/')}`">
                    {{ route.breadcrumbName }}
                </router-link>
            </template>
        </a-breadcrumb>
        <br/>
        <a-row type="flex" justify="end">
            <RefreshButton :onClick="refreshTable"/>
        </a-row>
        <br/>
        <!-- <a-table :columns="jobStatusColumns" :data-source="jobStatusList" bordered :scroll="{ x: true }"
            :pagination="paginationOpt">
            <template #bodyCell="{ column, text, record }">
                <template v-if="column.dataIndex === 'action'">
                    <a-tooltip title="查看执行日志">
                        <a-button type="text" @click="handleClickGetExecuteLog(record)"
                            class="iconfont icon-rizhi"></a-button>
                    </a-tooltip>
                    <a-popconfirm title="确定杀死当前任务?" ok-text="确定" cancel-text="取消" placement="bottom"
                        @confirm="handleClickKillRunningJob(record)" @cancel="cancel">
                        <a-tooltip title="杀死正在执行的任务">
                            <a-button type="text" class="iconfont icon-kill"></a-button>
                        </a-tooltip>
                    </a-popconfirm>
                </template>
            </template>
        </a-table> -->
        <Table :columns="jobStatusColumns" :data-source="jobStatusList" :pagination="paginationOpt">
            <template v-slot="{record}">
                <a-tooltip title="查看执行日志">
                        <a-button type="text" @click="handleClickGetExecuteLog(record)"
                            class="iconfont icon-rizhi"></a-button>
                    </a-tooltip>
                    <a-popconfirm title="确定杀死当前任务?" ok-text="确定" cancel-text="取消" placement="bottom"
                        @confirm="handleClickKillRunningJob(record)" @cancel="cancel">
                        <a-tooltip title="杀死正在执行的任务">
                            <a-button type="text" class="iconfont icon-kill"></a-button>
                        </a-tooltip>
                    </a-popconfirm>
            </template>
        </Table>
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
