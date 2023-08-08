<script setup>
import { onMounted, reactive, ref, onUpdated, watch } from 'vue';
import { useJobStatusStore } from "@/stores/job/jobStatus";
import { storeToRefs } from "pinia";
import { onBeforeRouteUpdate,useRoute } from 'vue-router';

const jobStatusStore = useJobStatusStore();
const { jobStatusList, paginationOpt } = storeToRefs(jobStatusStore);
const { getJobStatusList, getQueryParams } = jobStatusStore;

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
        title: 'id',
        dataIndex: 'id',
        key: 'id',
    },
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
        dataIndex: 'comment',
        key: 'comment',
    },
]);
const jobStatus = ref();

// 分页查询任务列表
const queryJobStatusListPaging = (paginationOpt, jobStatus) => {
    const { defaultCurrent, defaultPageSize } = paginationOpt.value;
    getJobStatusList({ pageNo: defaultCurrent, pageSize: defaultPageSize }, jobStatus);
};

// 监听路由参数变化
onBeforeRouteUpdate(to => {
    queryJobStatusListPaging(paginationOpt, to.query)
})

onMounted(() => {
    const queryParam = route.query;
    getQueryParams(queryParam);
    queryJobStatusListPaging(paginationOpt, queryParam);
})

</script>

<template>
    <a-space direction="vertical" style="width: 100%;">
        <a-row :gutter="24">
            <a-col :span="4">
                <a-button type="primary" @click="visible = true">新增</a-button>
            </a-col>
            <a-col :span="16">
                <a-input-search v-model:value="searchInfo" placeholder="请输入内容" enter-button @search="onSearch" />
            </a-col>
        </a-row>
        <a-row>
            <a-table :columns="jobStatusColumns" :data-source="jobStatusList" bordered style="width: 100%;"
                :pagination="paginationOpt">
            </a-table>
        </a-row>
    </a-space>
</template>
