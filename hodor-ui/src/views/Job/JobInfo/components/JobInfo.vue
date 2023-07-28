<script setup>
import { onMounted, reactive, ref, toRefs } from "vue";
import { useJobInfoStore } from "@/stores/job/jobInfo";
import { storeToRefs } from "pinia";
import { SearchOutlined } from "@ant-design/icons-vue";

const jobInfoStore = useJobInfoStore();
const { jobInfoList, paginationOpt } = toRefs(storeToRefs(jobInfoStore)) ;
const { getJobInfoList, createJob, deleteJob, stopJob, resumeJob } = jobInfoStore;

// 暂停任务
const onClickStop = () => {
    selectedJobIds.value.forEach((jobId) => {
        stopJob(jobId);
    })
    // 立即重新分页查询的话，数据库还未写入，不会返回响应结果，所以不进行分页查询而是直接在前端改变
    // queryJobInfoListPaging(paginationOpt);
}

// 恢复任务
const onClickResume = () => {
    selectedJobIds.value.forEach((jobId) => {
        resumeJob(jobId);
    })
    // queryJobInfoListPaging(paginationOpt);
}

// 删除任务:将所有选中的任务删除
const onClickDelete = () => {
    jobInfoList.value = jobInfoList.value.filter((job) => {
        return !selectedJobIds.value.includes(job.id);
    })
    selectedJobIds.value.forEach((jobId) => {
        deleteJob(jobId);
    })
}

// 新建任务Modal
const open = ref(false);
const onOk = () => {
    open.value = false;
    formRef.value.validateFields().then(values => {
        const { groupName, jobName, jobType, jobStatus, cron, priority } = values;
        createJob({ groupName, jobName, jobType, jobStatus, cron, priority });
        console.log("paginationOpt", paginationOpt);
        queryJobInfoListPaging(paginationOpt);
    })
}
// 新建任务表单
const formRef = ref()
const formStateCreateJob = reactive({
    layout: 'horizontal',
    groupName: '',
    jobName: '',
    jobType: '',
    jobStatus: '',
    cron: '',
    priority: '',
});


// 搜索表单
const formStateSearch = reactive({
    groupName: "",
    jobName: "",
    customJobCategory: "",
    jobStatus: "",
    jobType: "",
    jobStatus: "",
    createTime: "",
});
const onFinish = (values) => {
    console.log("Success:", values);
};
const onFinishFailed = (errorInfo) => {
    console.log("Failed:", errorInfo);
};
// 选择器
const focus = () => {
    console.log("focus");
};
const handleChange = (value) => {
    console.log(`selected ${value}`);
};


// 表格
const jobInfoColumns = ref([
    {
        title: "ID",
        dataIndex: "id",
        key: "id",
    },
    {
        title: "任务组名称",
        dataIndex: "groupName",
        key: "groupName",
    },
    {
        title: "任务名称",
        dataIndex: "jobName",
        key: "jobName",
    },
    {
        title: "任务类型",
        dataIndex: "jobType",
        key: "jobType",
    },
    {
        title: "任务状态",
        dataIndex: "jobStatus",
        key: "jobStatus",
    },
    {
        title: "时间表达式",
        dataIndex: "cron",
        key: "cron",
    },
    {
        title: "超时时间（秒）",
        dataIndex: "timeOut",
        key: "timeOut",
    },
    {
        title: "创建时间",
        dataIndex: "createTime",
        key: "createTime",
    },
    {
        title: "更新时间",
        dataIndex: "updateTime",
        key: "updateTime",
    },
    {
        title: "自定义分类",
        dataIndex: "jobCategory",
        key: "jobCategory",
    },
    {
        title: "操作",
        dataIndex: "action",
        key: "action",
    },
]);
const selectedJobIds = ref([]);
const rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
        selectedJobIds.value = selectedRowKeys;
    },
};

// 分页查询任务列表
const queryJobInfoListPaging = (paginationOpt) => {
    const { defaultCurrent, defaultPageSize } = paginationOpt.value;
    getJobInfoList({ pageNo: defaultCurrent, pageSize: defaultPageSize });
};

onMounted(() => {
    queryJobInfoListPaging(paginationOpt);
});
</script>

<template>
    <a-space direction="vertical" style="width: 100%">
        <a-row>
            <a-space>
                <a-button type="primary" @click="{ open = true; }">创建任务</a-button>
                <a-modal v-model:open="open" title="创建任务" ok-text="Create" cancel-text="Cancel" @ok="onOk">
                    <a-form ref="formRef" :layout="formStateCreateJob.layout" :model="formStateCreateJob"
                        name="form_in_modal">
                        <a-form-item label="任务组名称:" name="groupName">
                            <a-input v-model:value="formStateCreateJob.groupName" />
                        </a-form-item>
                        <a-form-item label="任务名称:" name="jobName">
                            <a-input v-model:value="formStateCreateJob.jobName" />
                        </a-form-item>
                        <a-form-item label="任务类型:" name="jobType">
                            <a-select ref="select" v-model:value="formStateCreateJob.jobType">
                                <a-select-option value="COMMON_JOB"></a-select-option>
                                <a-select-option value="TIME_JOB"></a-select-option>
                                <a-select-option value="WORKFLOW_JOB"></a-select-option>
                            </a-select>
                        </a-form-item>
                        <a-form-item label="任务状态:" name="jobStatus">
                            <a-select ref="select" v-model:value="formStateCreateJob.jobStatus">
                                <a-select-option value="READY"></a-select-option>
                                <a-select-option value="RUNNING"></a-select-option>
                                <a-select-option value="STOP"></a-select-option>
                            </a-select>
                        </a-form-item>
                        <a-form-item label="时间表达式:" name="cron">
                            <a-input v-model:value="formStateCreateJob.cron" />
                        </a-form-item>
                        <a-form-item label="优先级:" name="priority">
                            <a-select ref="select" v-model:value="formStateCreateJob.priority">
                                <a-select-option value="DEFAULT"></a-select-option>
                                <a-select-option value="MEDIUM"></a-select-option>
                                <a-select-option value="HIGHER"></a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-form>
                </a-modal>
                <a-button type="primary">上传任务</a-button>
                <a-button type="primary">导出任务</a-button>
                <a-button type="primary" danger @click="onClickStop">暂停任务</a-button>
                <a-button type="primary" danger @click="onClickResume">恢复任务</a-button>
                <a-button type="primary" danger @click="onClickDelete">删除任务</a-button>
            </a-space>
        </a-row>
        <a-row>
            <a-form :model="formStateSearch" name="basic" layout="inline" autocomplete="off" @finish="onFinish"
                @finishFailed="onFinishFailed">
                <a-space style="width: 100%">
                    <a-col :span="6">
                        <a-form-item name="groupName">
                            <a-input v-model:value="formStateSearch.groupName" placeholder="任务组" style="width: 120px" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-item name="jobName">
                            <a-input v-model:value="formStateSearch.jobName" placeholder="任务名称" style="width: 120px" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-item name="jobStatus">
                            <a-select ref="select" v-model:value="formStateSearch.jobStatus" style="width: 120px"
                                @focus="focus" @change="handleChange">
                                <a-select-option value="READY"></a-select-option>
                                <a-select-option value="RUNNING"></a-select-option>
                                <a-select-option value="STOP"></a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-item name="jobType">
                            <a-select ref="select" v-model:value="formStateSearch.jobType" style="width: 240px"
                                @focus="focus" @change="handleChange">
                                <a-select-option value="COMMON_JOB"></a-select-option>
                                <a-select-option value="TIME_JOB"></a-select-option>
                                <a-select-option value="WORKFLOW_JOB"></a-select-option>
                            </a-select>
                        </a-form-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-item name="createTime">
                            <a-range-picker v-model:value="formStateSearch.createTime" style="width: 240px" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="6">
                        <a-form-item name="btn">
                            <a-space>
                                <a-button html-type="submit">
                                    <SearchOutlined />
                                </a-button>
                            </a-space>
                        </a-form-item>
                    </a-col>
                </a-space>
            </a-form>
        </a-row>

        <a-row>
            <a-table :columns="jobInfoColumns" :data-source="jobInfoList" bordered :scroll="{ x: 1500 }"
                :row-selection="rowSelection" :rowKey="row => row.id" :pagination="paginationOpt">
                <template v-if="column.dataIndex === 'action'">
                    <a-select ref="select" v-model:value="actions" @focus="focus" @change="handleChange">
                    </a-select>
                </template>
            </a-table>
        </a-row>
    </a-space>
</template>

<style scoped>
.greenBtn {
    background-color: #7ac756;
}
</style>
