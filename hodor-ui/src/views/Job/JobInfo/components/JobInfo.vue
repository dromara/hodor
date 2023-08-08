<script setup>
import { onMounted, reactive, ref, toRefs } from "vue";
import { useJobInfoStore } from "@/stores/job/jobInfo";
import { useJobGroupStore } from "@/stores/job/jobGroup";
import { storeToRefs } from "pinia";
import { SearchOutlined } from "@ant-design/icons-vue";
import useClipboard from 'vue-clipboard3'
import { message } from 'ant-design-vue';
import yaml from 'js-yaml';
import { cloneDeep } from 'lodash-es';
import router from "@/router/router";

// store
const jobInfoStore = useJobInfoStore();
const { jobInfoList, paginationOpt } = storeToRefs(jobInfoStore);
const { getJobInfoList, createJob, deleteJob, stopJob, resumeJob, updateJob } = jobInfoStore;

const jobGroupStore = useJobGroupStore();
const { allGroupList } = storeToRefs(jobGroupStore);
const { getAllGroupList } = jobGroupStore;

import { useJobStatusStore } from "@/stores/job/jobStatus";
const jobStatusStore = useJobStatusStore();
const { jobStatusList} = storeToRefs(jobStatusStore);
const { getJobStatusList } = jobStatusStore;

const { toClipboard } = useClipboard()

// 暂停、恢复、删除任务modal
const visibleModal = ref(false);
const eventType = ref('');
const typeOpt = {
    'stop': '暂停',
    'resume': '恢复',
    'delete': '删除'
}
// modal表格
const selectedJobInfosColumns = ref([
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
])
// 新建任务Modal
const openCreateModal = ref(false);
const plainOptions = ['错过马上执行', '马上调度', '广播模式'];
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
    jobCategory: '',
    jobDesc: '',
    jobCommandType: '',
    jobDataType: '',
    jobDataPath: '',
    jobParameters: '',
    timeOut: '',
    retryCount: '',
    checkValue: [],
});
// 新建任务表单验证规则
const rulesCreateJob = {
    groupName: [{ required: true, message: 'Please select groupName!', trigger: 'blur' }],
    jobName: [{ required: true, message: 'Please input jobName!', trigger: 'blur' }],
    jobType: [{ required: true, message: 'Please select jobType!', trigger: 'blur' }],
    priority: [{ required: true, message: 'Please select priority!', trigger: 'blur' }],
}
const groupNameOptions = ref([]);

// 搜索表单
const formRefSearch = ref();
const formStateSearch = reactive({
    groupName: "",
    jobName: "",
    jobStatus: undefined,
    jobType: undefined,
    createTime: "",
});

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
        title: "任务命令类型",
        dataIndex: "jobCommandType",
        key: "jobCommandType",
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
        dataIndex: "timeout",
        key: "timeout",
    },
    {
        title: "创建时间",
        dataIndex: "createTime",
        key: "createTime",
    },
    {
        title: "更新时间",
        dataIndex: "activeTime",
        key: "activeTime",
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
        fixed: 'right', //固定在表格右边
    },
]);
// 多选框
const selectedJobIds = ref([]);
const selectedJobInfos = ref([])
const rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
        selectedJobIds.value = selectedRowKeys;
        selectedJobInfos.value = selectedRows;
    },
};
// 数据编辑器
const visibleEditor = ref(false);
const formEditor = reactive({
    content: '',
})
const editorMode = ref('YAML');
// 文本编辑框
const totalLines = ref(32); // 总行数
// 编辑数据
const editableData = reactive({});


// 暂停任务
const stopSelectedJobs = () => {
    selectedJobIds.value.forEach((jobId) => {
        stopJob(jobId);
    });
}
// 恢复任务
const resumeSelectedJobs = () => {
    selectedJobIds.value.forEach((jobId) => {
        resumeJob(jobId);
    });
}
// 删除任务
const deleteSelectedJobs = () => {
    jobInfoList.value = jobInfoList.value.filter((job) => {
        return !selectedJobIds.value.includes(job.id);
    })
    selectedJobIds.value.forEach((jobId) => {
        deleteJob(jobId);
    })
}
// 分页查询任务列表
const queryJobInfoListPaging = (paginationOpt, jobInfo = {}) => {
    const { defaultCurrent, defaultPageSize } = paginationOpt.value;
    // console.log("jobInfo",jobInfo);
    getJobInfoList({ pageNo: defaultCurrent, pageSize: defaultPageSize }, jobInfo);
};
// 复制到剪贴板
const copy = async (msg) => {
    try {
        await toClipboard(msg);
        message.success('文字已复制成功！');
    }
    catch (e) {
        console.error(e);
        message.error('文字复制失败！');
    }
}
// 编辑行
const editJobInfo = id => {
    editableData[id] = cloneDeep(...jobInfoList.value.filter(item => id === item.id));
}
// 保存编辑后的任务信息
const saveJobInfo = id => {
    const updateJobInfo = editableData[id];
    updateJob(updateJobInfo);
    delete editableData[id];
};
// 获取所有分组名称
const getGroupOptions = async () => {
    await getAllGroupList();
    groupNameOptions.value = allGroupList.value.map(group => {
        const { groupName } = group;
        return {
            value: groupName,
            label: groupName
        }
    })
}
// 查看任务执行明细
const getJobStatusDetail=({groupName,jobName})=>{
    getJobStatusList({pageNo:1,pageSize:100},{groupName,jobName});
}

// 暂停、恢复、删除点击事件
const onClickBtn = (type) => {
    eventType.value = type;
    visibleModal.value = true;
}
const handleOk = () => {
    switch (eventType.value) {
        case 'stop':
            stopSelectedJobs();
            break;
        case 'resume':
            resumeSelectedJobs();
            break;
        case 'delete':
            deleteSelectedJobs();
            break;
    }
    visibleModal.value = false;
}

// 新建任务Modal
const onOk = () => {
    openCreateModal.value = false;
    formRef.value.validateFields().then(values => {
        const { groupName, jobName, jobType, jobStatus, cron, priority, jobCategory, jobDesc, jobCommandType, jobDataType, jobDataPath, jobParameters, timeOut, retryCount, checkValue } = values;
        createJob({ groupName, jobName, jobType, jobStatus, cron, priority, jobCategory, jobDesc, jobCommandType, jobDataType, jobDataPath, jobParameters, timeOut, retryCount });
        queryJobInfoListPaging(paginationOpt);
    })
}

const onFinish = (values) => {
    console.log("Success:", values);
};
const onFinishFailed = (errorInfo) => {
    console.log("Failed:", errorInfo);
};
// 搜索事件
const onClickSearch = () => {
    formRefSearch.value.validateFields().then(values => {
        const { jobStatus, jobType, createTime } = values;
        if (jobStatus === 'ALL') {
            Object.assign(values, {
                ...values,
                jobStatus: undefined,
            });
        }
        if (jobType === 'ALL') {
            Object.assign(values, {
                ...values,
                jobType: undefined,
            });
        }
        queryJobInfoListPaging(paginationOpt, values);
    })
}

// 操作选择器
const handleChangeAction = (value, jobId,jobName,groupName) => {
    switch (value) {
        case 'stopJob':
            stopJob(jobId);
            break;
        case 'resumeJob':
            resumeJob(jobId);
            break;
        case 'deleteJob':
            deleteJob(jobId);
            break;
        case 'editJob':
            editJobInfo(jobId);
            break;
        case 'jobInstance':
            break;
        case 'jobDetail':
            router.push({ path: 'job-status', query: { groupName,jobName }});
            break;
        case 'saveJob':
            saveJobInfo(jobId);
            break;
    }
};
// 关闭数字编辑器
const onCloseEditor = () => {
    visibleEditor.value = false;
}
// 复制文字到剪贴板
const copyText = (value) => {
    copy(value);
}

// 监听文本内容的变化，更新总行数
const handleInput = () => {
    const lines = formEditor.content.split('\n');
    if (lines.length > totalLines.value) {
        totalLines.value = lines.length;
    }
};
// 提交编辑器内容
const onSubmit = () => {
    let jsonData = formEditor.content;
    if (editorMode.value === 'YAML') {
        jsonData = yaml.load(jsonData);
    }
    console.log("jsonData", jsonData)
}


onMounted(() => {
    queryJobInfoListPaging(paginationOpt);
    getGroupOptions();
});
</script>

<template>
    <a-space direction="vertical" style="width: 100%">
        <a-row>
            <a-space>
                <a-button type="primary" @click="{ openCreateModal = true; }">创建任务</a-button>
                <a-modal v-model:open="openCreateModal" title="新建任务" ok-text="确定" cancel-text="取消" @ok="onOk" width="80%">
                    <a-form ref="formRef" :model="formStateCreateJob" name="form_in_modal" :rules="rulesCreateJob">
                        <a-row :gutter="24">
                            <a-col :span="12">
                                <a-form-item label="任务组名称:" name="groupName">
                                    <a-select ref="select" v-model:value="formStateCreateJob.groupName"
                                        :options="groupNameOptions">
                                    </a-select>
                                </a-form-item>
                            </a-col>
                            <a-col :span="12">
                                <a-form-item label="任务名称:" name="jobName">
                                    <a-input v-model:value="formStateCreateJob.jobName" />
                                </a-form-item>
                            </a-col>
                        </a-row>
                        <a-row :gutter="24">
                            <a-col :span="12">
                                <a-form-item label="自定义分类:" name="jobCategory">
                                    <a-input v-model:value="formStateCreateJob.jobCategory" />
                                </a-form-item>
                            </a-col>
                        </a-row>
                        <a-row :gutter="24">
                            <a-col :span="24">
                                <a-form-item label="任务描述:" name="jobDesc">
                                    <a-textarea v-model:value="formStateCreateJob.jobDesc" placeholder="请输入任务描述"
                                        :rows="3" />
                                </a-form-item>
                            </a-col>
                        </a-row>
                        <a-row :gutter="24">
                            <a-col :span="12">
                                <a-form-item label="任务类型:" name="jobType">
                                    <a-select ref="select" v-model:value="formStateCreateJob.jobType">
                                        <a-select-option value="COMMON_JOB"></a-select-option>
                                        <a-select-option value="TIME_JOB"></a-select-option>
                                        <a-select-option value="WORKFLOW_JOB"></a-select-option>
                                    </a-select>
                                </a-form-item>
                            </a-col>
                            <a-col :span="8">
                                <a-form-item label="时间表达式:" name="cron">
                                    <a-input v-model:value="formStateCreateJob.cron" />
                                </a-form-item>
                            </a-col>
                            <a-col :span="4">
                                <a-button @click="onClickValidCron">验证表达式</a-button>
                            </a-col>
                        </a-row>
                        <a-row :gutter="24">
                            <a-col :span="12">
                                <a-form-item label="任务命令类型:" name="jobCommandType" placeholder="任务命令类型">
                                    <a-select ref="select" v-model:value="formStateCreateJob.jobCommandType">
                                        <a-select-option value="java"></a-select-option>
                                    </a-select>
                                </a-form-item>
                            </a-col>
                        </a-row>
                        <a-row :gutter="24">
                            <a-col :span="12">
                                <a-form-item label="任务资源类型:" name="jobDataType">
                                    <a-input v-model:value="formStateCreateJob.jobDataType" />
                                </a-form-item>
                            </a-col>
                            <a-col :span="12">
                                <a-form-item label="任务资源路径:" name="jobDataPath">
                                    <a-input v-model:value="formStateCreateJob.jobDataPath" />
                                </a-form-item>
                            </a-col>
                        </a-row>
                        <a-row :gutter="24">
                            <a-col :span="24">
                                <a-form-item label="任务参数:" name="jobParameters">
                                    <a-textarea v-model:value="formStateCreateJob.jobParameters" placeholder="请输入任务参数"
                                        :rows="3" />
                                </a-form-item>
                            </a-col>
                        </a-row>
                        <a-row :gutter="24">
                            <a-col :span="12">
                                <a-form-item label="超时时间（秒）:" name="timeOut">
                                    <a-input v-model:value="formStateCreateJob.jobDataPath" />
                                </a-form-item>
                            </a-col>
                        </a-row>
                        <a-row :gutter="24">
                            <a-col :span="5">
                                <a-time-range-picker />
                            </a-col>
                            <a-col :span="5">
                                <a-form-item label="优先级:" name="priority"
                                    :rules="[{ required: true, message: 'Please select priority!' }]">
                                    <a-select ref="select" v-model:value="formStateCreateJob.priority">
                                        <a-select-option value="DEFAULT"></a-select-option>
                                        <a-select-option value="MEDIUM"></a-select-option>
                                        <a-select-option value="HIGHER"></a-select-option>
                                    </a-select>
                                </a-form-item>
                            </a-col>
                            <a-col :span="5">
                                <a-form-item label="任务重试次数:" name="retryCount">
                                    <a-input v-model:value="formStateCreateJob.retryCount" />
                                </a-form-item>
                            </a-col>
                            <a-col :span="9">
                                <a-checkbox-group v-model:value="formStateCreateJob.checkValue" name="checkboxgroup"
                                    :options="plainOptions" />
                            </a-col>
                        </a-row>
                    </a-form>
                </a-modal>
                <a-button type="primary" @click="{ visibleEditor = true; }">批量编辑任务</a-button>
                <a-drawer title="数据编辑器" :width="500" :visible="visibleEditor" :body-style="{ paddingBottom: '80px' }"
                    @close="onCloseEditor">
                    <a-space direction="vertical" style="width: 100%;">
                        <a-row type="flex" justify="end">
                            <a-space>
                                <a-select ref="select" v-model:value="editorMode" @change="handleChange">
                                    <a-select-option value="YAML"></a-select-option>
                                    <a-select-option value="JSON"></a-select-option>
                                </a-select>
                                <a-button type="primary" @click="copyText(formEditor.content)">复制</a-button>
                            </a-space>
                        </a-row>
                        <a-form :model="formEditor" layout="vertical">
                            <a-form-item name="content">
                                <div class="editor-container">
                                    <div class="line-number">
                                        <div v-for="lineNumber in totalLines" :key="lineNumber" class="line-number-item">
                                            {{ lineNumber }}
                                        </div>
                                    </div>
                                    <a-textarea v-model:value="formEditor.content" placeholder="请输入内容" :rows="totalLines.value"
                                        class="editor" @change="handleInput" />
                                </div>
                            </a-form-item>
                        </a-form>
                        <a-row type="flex" justify="space-between">
                            <a-button style="margin-right: 8px" @click="onCloseEditor">取消</a-button>
                            <a-button type="primary" @click="onSubmit">提交</a-button>
                        </a-row>
                    </a-space>
                </a-drawer>
                <a-button type="primary">导出任务</a-button>
                <a-button type="primary" danger @click="onClickBtn('stop')">暂停任务</a-button>
                <a-button type="primary" danger @click="onClickBtn('resume')">恢复任务</a-button>
                <a-button type="primary" danger @click="onClickBtn('delete')">删除任务</a-button>
                <a-modal v-model:visible="visibleModal" :title="`${typeOpt[eventType]}任务`" @ok="handleOk">
                    <p>确认{{ typeOpt[eventType] }}下列任务吗？</p>
                    <a-table v-show="selectedJobInfos.length > 0" :columns="selectedJobInfosColumns"
                        :data-source="selectedJobInfos" :pagination="false"></a-table>
                </a-modal>
            </a-space>
        </a-row>
        <a-row>
            <a-form ref="formRefSearch" :model="formStateSearch" name="basic" layout="inline" autocomplete="off"
                @finish="onFinish" @finishFailed="onFinishFailed">
                <a-space style="width: 100%">
                    <a-form-item name="groupName">
                        <a-input v-model:value="formStateSearch.groupName" placeholder="任务组" />
                    </a-form-item>
                    <a-form-item name="jobName">
                        <a-input v-model:value="formStateSearch.jobName" placeholder="任务名称" />
                    </a-form-item>
                    <a-form-item name="jobStatus">
                        <a-select ref="select" v-model:value="formStateSearch.jobStatus" placeholder="任务状态">
                            <a-select-option value="ALL"></a-select-option>
                            <a-select-option value="READY"></a-select-option>
                            <a-select-option value="RUNNING"></a-select-option>
                            <a-select-option value="STOP"></a-select-option>
                        </a-select>
                    </a-form-item>
                    <a-form-item name="jobType">
                        <a-select ref="select" v-model:value="formStateSearch.jobType" placeholder="任务类型">
                            <a-select-option value="ALL"></a-select-option>
                            <a-select-option value="COMMON_JOB"></a-select-option>
                            <a-select-option value="TIME_JOB"></a-select-option>
                            <a-select-option value="WORKFLOW_JOB"></a-select-option>
                        </a-select>
                    </a-form-item>
                    <a-form-item name="createTime">
                        <a-range-picker v-model:value="formStateSearch.createTime" />
                    </a-form-item>
                    <a-button @click="onClickSearch">
                        <SearchOutlined />
                    </a-button>
                </a-space>
            </a-form>
        </a-row>
        <a-row>
            <a-table :columns="jobInfoColumns" :data-source="jobInfoList" bordered :scroll="{ x: 1500 }"
                :row-selection="rowSelection" :rowKey="row => row.id" :pagination="paginationOpt">
                <template #bodyCell="{ column, text, record }">
                    <template
                        v-if="['groupName', 'jobName', 'cron', 'timeOut', 'jobCategory', 'createTime', 'activeTime'].includes(column.dataIndex)">
                        <div>
                            <a-input v-if="editableData[record.id]"
                                v-model:value="editableData[record.id][column.dataIndex]" style="margin: -5px 0" />
                            <template v-else>
                                {{ text }}
                            </template>
                        </div>
                    </template>
                    <template v-if="column.dataIndex === 'jobStatus'">
                        <div>
                            <a-select v-if="editableData[record.id]" ref="select"
                                v-model:value="editableData[record.id][column.dataIndex]" placeholder="任务状态">
                                <a-select-option value="READY"></a-select-option>
                                <a-select-option value="RUNNING"></a-select-option>
                                <a-select-option value="STOP"></a-select-option>
                            </a-select>
                            <template v-else>
                                {{ text }}
                            </template>
                        </div>
                    </template>
                    <template v-if="column.dataIndex === 'jobType'">
                        <div>
                            <a-select v-if="editableData[record.id]" ref="select"
                                v-model:value="editableData[record.id][column.dataIndex]" placeholder="任务状态">
                                <a-select-option value="COMMON_JOB"></a-select-option>
                                <a-select-option value="TIME_JOB"></a-select-option>
                                <a-select-option value="WORKFLOW_JOB"></a-select-option>
                            </a-select>
                            <template v-else>
                                {{ text }}
                            </template>
                        </div>
                    </template>
                    <template v-if="column.dataIndex === 'action'">
                        <a-select ref="select" v-model="record.action" @change="handleChangeAction($event, record.id,record.jobName,record.groupName)" style="width: 100%;"
                            placeholder="操作">
                            <a-select-option value="jobDetail">任务详情</a-select-option>
                            <a-select-option value="stopJob">停止</a-select-option>
                            <a-select-option value="resumeJob">恢复</a-select-option>
                            <a-select-option value="editJob">编辑</a-select-option>
                            <a-select-option value="saveJob">保存</a-select-option>
                        </a-select>
                    </template>
                </template>
            </a-table>
        </a-row>
    </a-space>
</template>

<style scoped>
.greenBtn {
    background-color: #7ac756;
}

.editor-drawer {
    display: flex;
    flex-direction: column;
}

.editor-content {
    flex: 1;
}

.editor-container {
    display: flex;
}

.line-number {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-right: 8px;
    color: #999;
}

.line-number-item {
    min-width: 24px;
}

.editor {
    flex: 1;
    resize: none;
    padding: 4px;
    font-size: 14px;
    line-height: 1.5;
}
</style>
