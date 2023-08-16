<script setup>
import { onMounted, reactive, ref, toRefs, watch, nextTick } from "vue";
import { useJobInfoStore } from "@/stores/job/jobInfo";
import { useJobGroupStore } from "@/stores/job/jobGroup";
import { storeToRefs } from "pinia";
import { SearchOutlined } from "@ant-design/icons-vue";
import useClipboard from 'vue-clipboard3'
import { message } from 'ant-design-vue';
import yaml from 'js-yaml';
import { cloneDeep } from 'lodash-es';
import router from "@/router/router";
// 代码编辑器
import { Codemirror } from 'vue-codemirror'
import { javascript } from '@codemirror/lang-javascript'
import { oneDark } from '@codemirror/theme-one-dark'
import '@/assets/iconfont/iconfont.css'

// store
const jobInfoStore = useJobInfoStore();
const { jobInfoList, paginationOpt } = storeToRefs(jobInfoStore);
const { getJobInfoList, createJob, deleteJob, stopJob, resumeJob, updateJob, executeJob } = jobInfoStore;

const jobGroupStore = useJobGroupStore();
const { allGroupList } = storeToRefs(jobGroupStore);
const { getAllGroupList } = jobGroupStore;

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
const checkboxOptions = [{
    label: '错过马上执行',
    value: 'misfire',
}, {
    label: '马上调度',
    value: 'fireNow',
}, {
    label: '广播模式',
    value: 'isBroadcast',
}];
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
    executeTime: [],
});
// 新建任务表单验证规则
const rulesCreateJob = {
    groupName: [{ required: true, message: 'Please select groupName!', trigger: 'blur' }],
    jobName: [{ required: true, message: 'Please input jobName!', trigger: 'blur' }],
    jobType: [{ required: true, message: 'Please select jobType!', trigger: 'blur' }],
    priority: [{ required: true, message: 'Please select priority!', trigger: 'blur' }],
}
const groupNameOptions = ref([]);
// tabs
const activeKey = ref('1');
const modalCreateProps = ref('')

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
        title: "结束时间",
        dataIndex: "endTime",
        key: "endTime",
    },
    {
        title: "重试次数",
        dataIndex: "retryCount",
        key: "retryCount",
    },
    {
        title: "版本",
        dataIndex: "version",
        key: "version",
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
// 日期时间选择器
const onRangeChange = (date, dateStr) => {
    // console.log("dateStr", dateStr);
    formStateCreateJob.executeTime = dateStr;
}
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
    content:
        `groupName: ''
jobName: ''
config: {}
dependsOn: []
nodes:
  - groupName: ''
    jobName: ''
    config: {}
    dependsOn: []
    nodes: []`,
})
const editorMode = ref('YAML');
// 文本编辑框
const totalLines = ref(32); // 总行数
// 编辑数据
const editableData = ref({});
//codemirror
const extensions = [javascript(), oneDark];
const codeMirrorRef = ref(null);
// 编辑任务表单
const formUpdateRef = ref()
const formStateUpdateJob = reactive({
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
    executeTime: [],
});


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
        message.error('文字复制失败！');
    }
}
const openUpdateModal = ref(false)
// 编辑行
const editJobInfo = id => {
    openUpdateModal.value = true;
    editableData.value = cloneDeep(...jobInfoList.value.filter(item => id === item.id));
    Object.assign(formStateUpdateJob, editableData.value);
}
// 保存编辑后的任务信息
const saveJobInfo = () => {
    const { id } = editableData.value;
    formUpdateRef.value.validateFields().then(values => {
        const {
            groupName,
            jobName,
            jobType,
            jobStatus,
            cron,
            priority,
            jobCategory,
            jobDesc,
            jobCommandType,
            jobDataType,
            jobDataPath,
            jobParameters,
            timeOut,
            retryCount,
            checkValue,
            executeTime } = formStateUpdateJob;
        const misfire = checkValue.includes('misfire');
        const fireNow = checkValue.includes('fireNow');
        const isBroadcast = checkValue.includes('isBroadcast');
        const prevExecuteTime = executeTime[0];
        const nextExecuteTime = executeTime[1];
        updateJob({ id, groupName, jobName, jobType, jobStatus, cron, priority, jobCategory, jobDesc, jobCommandType, jobDataType, jobDataPath, jobParameters, timeOut, retryCount, misfire, fireNow, isBroadcast,/* prevExecuteTime,nextExecuteTime */ });
    })
    editableData.value = {};
    openUpdateModal.value = false;
};
// 取消编辑
const cancelEdit = (jobId) => {
    delete editableData[jobId]
}
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
const getJobStatusDetail = (groupName, jobName) => {
    // getJobStatusList({ pageNo: 1, pageSize: 100 }, { groupName, jobName });
    router.push({ path: 'job-status', query: { groupName, jobName } });
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
        const {
            groupName,
            jobName,
            jobType,
            jobStatus,
            cron,
            priority,
            jobCategory,
            jobDesc,
            jobCommandType,
            jobDataType,
            jobDataPath,
            jobParameters,
            timeOut,
            retryCount,
            checkValue,
            executeTime } = formStateCreateJob;
        const misfire = checkValue.includes('misfire');
        const fireNow = checkValue.includes('fireNow');
        const isBroadcast = checkValue.includes('isBroadcast');
        const prevExecuteTime = executeTime[0];
        const nextExecuteTime = executeTime[1];
        // console.log({ groupName, jobName, jobType, jobStatus, cron, priority, jobCategory, jobDesc, jobCommandType, jobDataType, jobDataPath, jobParameters, timeOut, retryCount,misfire,fireNow,isBroadcast,prevExecuteTime,nextExecuteTime })
        createJob({ groupName, jobName, jobType, jobStatus, cron: '-', priority, jobCategory, jobDesc, jobCommandType, jobDataType, jobDataPath, jobParameters, timeOut, retryCount, misfire, fireNow, isBroadcast,/* prevExecuteTime,nextExecuteTime */ });
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

// 关闭数字编辑器
const onCloseEditor = () => {
    visibleEditor.value = false;
}
// 复制文字到剪贴板
const copyText = (value) => {
    copy(value);
}

const handleClickDrawer = () => {
    visibleEditor.value = true;
}
// 提交编辑器内容
const onSubmit = () => {
    let jsonData = formEditor.content;
    if (editorMode.value === 'YAML') {
        jsonData = yaml.load(jsonData);
    }
}

watch(
    () => editorMode.value,
    () => {
        if (editorMode.value === 'YAML') {
            formEditor.content =
                `groupName: ''
jobName: ''
config: {}
dependsOn: []
nodes:
  - groupName: ''
    jobName: ''
    config: {}
    dependsOn: []
    nodes: []`
        }
        else {
            formEditor.content =
                `{
    "groupName": "",
    "jobName": "",
    "config": {},
    "dependsOn": [],
    "nodes": [
        {
            "groupName": "",
            "jobName": "",
            "config": {},
            "dependsOn": [],
            "nodes": []
        }
    ]
}`;
        }
    }
);


onMounted(() => {
    queryJobInfoListPaging(paginationOpt);
    getGroupOptions();
});
</script>

<template>
    <a-card>
        <a-space direction="vertical">
            <a-row>
                <a-space>
                    <a-button type="primary" @click="{ openCreateModal = true; }">创建任务</a-button>
                    <a-modal v-model:open="openCreateModal" ok-text="确定" cancel-text="取消" @ok="onOk" width="80%"
                        :ok-button-props="{ style: { display: 'none' } }"
                        :cancel-button-props="{ style: { display: 'none' } }">
                        <a-tabs v-model:activeKey="activeKey">
                            <a-tab-pane key="1" tab="新建任务">
                                <a-form ref="formRef" :model="formStateCreateJob" name="form_in_modal"
                                    :rules="rulesCreateJob">
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
                                        <div class="timeExpression" v-show="formStateCreateJob.jobType === 'TIME_JOB'">
                                            <a-col :span="8" style="flex: 1;max-width: 100%;">
                                                <a-form-item label="时间表达式:" name="cron">
                                                    <a-input v-model:value="formStateCreateJob.cron" />
                                                </a-form-item>
                                            </a-col>
                                            <a-col :span="4">
                                                <a-button @click="onClickValidCron">验证表达式</a-button>
                                            </a-col>
                                        </div>
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
                                                <!-- <a-textarea v-model:value="formStateCreateJob.jobParameters"
                                                    placeholder="请输入任务参数" :rows="3" /> -->
                                                <codemirror ref="codeMirrorRef" v-model="formStateCreateJob.jobParameters"
                                                    :style="{ height: '100px' }" :tab-size="2" @ready="handleReady" />
                                            </a-form-item>
                                        </a-col>
                                    </a-row>
                                    <a-row :gutter="24">
                                        <a-col :span="12">
                                            <a-form-item label="超时时间（秒）:" name="timeOut">
                                                <a-input v-model:value="formStateCreateJob.jobDataPath" />
                                            </a-form-item>
                                        </a-col>
                                        <a-col :span="12">
                                            <a-form-item label="执行时间:" name="executeTime">
                                                <a-range-picker v-model="formStateCreateJob.executeTime"
                                                    :show-time="{ format: 'HH:mm:ss.SSSX' }"
                                                    format="YYYY-MM-DD'T'HH:mm:ss.SSSX" allowClear showNow
                                                    :placeholder="['Start Time', 'End Time']" @change="onRangeChange"
                                                    @ok="onRangeOk" />
                                            </a-form-item>
                                        </a-col>
                                    </a-row>
                                    <a-row :gutter="24">
                                        <a-col :span="7">
                                            <a-form-item label="优先级:" name="priority"
                                                :rules="[{ required: true, message: 'Please select priority!' }]">
                                                <a-select ref="select" v-model:value="formStateCreateJob.priority">
                                                    <a-select-option value="DEFAULT"></a-select-option>
                                                    <a-select-option value="MEDIUM"></a-select-option>
                                                    <a-select-option value="HIGHER"></a-select-option>
                                                </a-select>
                                            </a-form-item>
                                        </a-col>
                                        <a-col :span="8">
                                            <a-form-item label="任务重试次数:" name="retryCount">
                                                <a-input v-model:value="formStateCreateJob.retryCount" />
                                            </a-form-item>
                                        </a-col>
                                        <a-col :span="9">
                                            <a-form-item name="checkValue">
                                                <a-checkbox-group v-model:value="formStateCreateJob.checkValue"
                                                    name="checkboxgroup" :options="checkboxOptions" />
                                            </a-form-item>
                                        </a-col>
                                    </a-row>
                                    <a-row type="flex" justify="end">
                                        <a-space>
                                            <a-button @click="openCreateModal = false">取消</a-button>
                                            <a-button type="primary" @click="onOk">确定</a-button>
                                        </a-space>
                                    </a-row>
                                </a-form>
                            </a-tab-pane>
                            <a-tab-pane key="2" tab="数据编辑">
                                <a-space direction="vertical"
                                    style="width: 100%;height: 100%;display: flex;flex-direction: column;">
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
                                            <codemirror ref="codeMirrorRef" v-model="formEditor.content"
                                                :style="{ height: '700px', flex: 1 }" :autofocus="true"
                                                :indent-with-tab="true" :tab-size="2" :extensions="extensions" />
                                        </a-form-item>
                                    </a-form>
                                    <a-row type="flex" justify="space-between">
                                        <a-button style="margin-right: 8px" @click="onCloseEditor">取消</a-button>
                                        <a-button type="primary" @click="onSubmit">提交</a-button>
                                    </a-row>
                                </a-space>
                            </a-tab-pane>
                        </a-tabs>
                    </a-modal>
                    <a-button type="primary" @click="handleClickDrawer">批量编辑任务</a-button>
                    <a-drawer title="数据编辑器" :width="500" :visible="visibleEditor" :body-style="{ paddingBottom: '80px' }"
                        @close="onCloseEditor">
                        <a-space direction="vertical"
                            style="width: 100%;height: 100%;display: flex;flex-direction: column;">
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
                                    <codemirror ref="codeMirrorRef" v-model="formEditor.content"
                                        :style="{ height: '700px', flex: 1 }" :autofocus="true" :indent-with-tab="true"
                                        :tab-size="2" :extensions="extensions" />
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
        </a-space>
    </a-card>
    <br />
    <a-card>
        <a-table :columns="jobInfoColumns" :data-source="jobInfoList" bordered :scroll="{ x: true }"
            :row-selection="rowSelection" :rowKey="row => row.id" :pagination="paginationOpt">
            <template #bodyCell="{ column, text, record }">
                <template v-if="column.dataIndex === 'action'">
                    <a-space>
                        <a-tooltip title="编辑">
                            <a-button type="text" class="iconfont icon-edit-square"
                                @click="editJobInfo(record.id)"></a-button>
                        </a-tooltip>
                        <a-tooltip title="立即执行">
                            <a-button type="text" class="iconfont icon-play" @click="executeJob(record.id)"></a-button>
                        </a-tooltip>
                        <a-tooltip title="恢复">
                            <a-button type="text" class="iconfont icon-Restart" @click="resumeJob(record.id)"></a-button>
                        </a-tooltip>
                        <a-tooltip title="停止">
                            <a-button type="text" class="iconfont icon-stop" @click="stopJob(record.id)"></a-button>
                        </a-tooltip>
                        <a-tooltip title="任务详情">
                            <a-button type="text" class="iconfont icon-detail"
                                @click="getJobStatusDetail(record.groupName, record.jobName)"></a-button>
                        </a-tooltip>
                    </a-space>
                </template>
            </template>
        </a-table>
    </a-card>
    <a-modal v-model:open="openUpdateModal" ok-text="确定" cancel-text="取消" @ok="saveJobInfo" width="80%">
        <a-form ref="formUpdateRef" :model="formStateUpdateJob" name="form_in_modal" :rules="rulesCreateJob">
            <a-row :gutter="24">
                <a-col :span="12">
                    <a-form-item label="任务组名称:" name="groupName">
                        <a-select ref="select" v-model:value="formStateUpdateJob.groupName" :options="groupNameOptions">
                        </a-select>
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="任务名称:" name="jobName">
                        <a-input v-model:value="formStateUpdateJob.jobName" />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row :gutter="24">
                <a-col :span="12">
                    <a-form-item label="自定义分类:" name="jobCategory">
                        <a-input v-model:value="formStateUpdateJob.jobCategory" />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row :gutter="24">
                <a-col :span="24">
                    <a-form-item label="任务描述:" name="jobDesc">
                        <a-textarea v-model:value="formStateUpdateJob.jobDesc" placeholder="请输入任务描述" :rows="3" />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row :gutter="24">
                <a-col :span="12">
                    <a-form-item label="任务类型:" name="jobType">
                        <a-select ref="select" v-model:value="formStateUpdateJob.jobType">
                            <a-select-option value="COMMON_JOB"></a-select-option>
                            <a-select-option value="TIME_JOB"></a-select-option>
                            <a-select-option value="WORKFLOW_JOB"></a-select-option>
                        </a-select>
                    </a-form-item>
                </a-col>
                <div class="timeExpression" v-show="formStateUpdateJob.jobType === 'TIME_JOB'">
                    <a-col :span="8" style="flex: 1;max-width: 100%;">
                        <a-form-item label="时间表达式:" name="cron">
                            <a-input v-model:value="formStateUpdateJob.cron" />
                        </a-form-item>
                    </a-col>
                    <a-col :span="4">
                        <a-button @click="onClickValidCron">验证表达式</a-button>
                    </a-col>
                </div>
            </a-row>
            <a-row :gutter="24">
                <a-col :span="12">
                    <a-form-item label="任务命令类型:" name="jobCommandType" placeholder="任务命令类型">
                        <a-select ref="select" v-model:value="formStateUpdateJob.jobCommandType">
                            <a-select-option value="java"></a-select-option>
                        </a-select>
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row :gutter="24">
                <a-col :span="12">
                    <a-form-item label="任务资源类型:" name="jobDataType">
                        <a-input v-model:value="formStateUpdateJob.jobDataType" />
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
                        <codemirror ref="codeMirrorRef" v-model="formStateUpdateJob.jobParameters"
                            :style="{ height: '100px' }" :tab-size="2" @ready="handleReady" />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row :gutter="24">
                <a-col :span="12">
                    <a-form-item label="超时时间（秒）:" name="timeOut">
                        <a-input v-model:value="formStateUpdateJob.jobDataPath" />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="执行时间:" name="executeTime">
                        <a-range-picker v-model="formStateUpdateJob.executeTime" :show-time="{ format: 'HH:mm:ss.SSSX' }"
                            format="YYYY-MM-DD'T'HH:mm:ss.SSSX" allowClear showNow :placeholder="['Start Time', 'End Time']"
                            @change="onRangeChange" @ok="onRangeOk" />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row :gutter="24">
                <a-col :span="7">
                    <a-form-item label="优先级:" name="priority"
                        :rules="[{ required: true, message: 'Please select priority!' }]">
                        <a-select ref="select" v-model:value="formStateUpdateJob.priority">
                            <a-select-option value="DEFAULT"></a-select-option>
                            <a-select-option value="MEDIUM"></a-select-option>
                            <a-select-option value="HIGHER"></a-select-option>
                        </a-select>
                    </a-form-item>
                </a-col>
                <a-col :span="8">
                    <a-form-item label="任务重试次数:" name="retryCount">
                        <a-input v-model:value="formStateUpdateJob.retryCount" />
                    </a-form-item>
                </a-col>
                <a-col :span="9">
                    <a-form-item name="checkValue">
                        <a-checkbox-group v-model:value="formStateUpdateJob.checkValue" name="checkboxgroup"
                            :options="checkboxOptions" />
                    </a-form-item>
                </a-col>
            </a-row>
        </a-form>
    </a-modal>
</template>

<style scoped lang="less">
:deep(.ant-table) {
    white-space: nowrap;
}
.timeExpression {
    display: flex;
}
button.css-dev-only-do-not-override-j6gjt1.ant-btn.ant-btn-text.iconfont {
    padding: 4px;
}
</style>
