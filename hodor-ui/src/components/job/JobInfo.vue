<template>
    <a-space direction="vertical" style="width: 100%;">
        <a-row>
            <a-input-search v-model:value="searchInfo" placeholder="请输入内容" enter-button @search="onSearch" />
        </a-row>
        <a-row>
            <a-form :model="formState" name="basic" layout="inline" :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }"
                autocomplete="off" @finish="onFinish" @finishFailed="onFinishFailed">
                <a-space direction="vertical">
                    <a-row>
                        <!-- 自定义检索 -->
                        <a-form-item name="customSearchInfo">
                            <a-input v-model:value="formState.customSearchInfo" placeholder="---自定义检索---" />
                        </a-form-item>
                        <!-- 自定义任务分类 -->
                        <a-form-item name="customJobCategory">
                            <a-select ref="select" v-model:value="formState.category" style="width: 120px" @focus="focus"
                                @change="handleChange">
                                <a-select-option value="自定义任务分类--全部"></a-select-option>
                            </a-select>
                        </a-form-item>
                        <!-- job执行状态 -->
                        <a-form-item name="exeStatus">
                            <a-select ref="select" v-model:value="formState.exeStatus" style="width: 120px"
                                :options="exeStatusList" @focus="focus" @change="handleChange">
                            </a-select>
                        </a-form-item>
                        <!-- job状态 -->
                        <a-form-item name="jobStatus">
                            <a-select ref="select" v-model:value="formState.jobStatus" style="width: 120px"
                                :options="jobStatusList" @focus="focus" @change="handleChange">
                            </a-select>
                        </a-form-item>
                    </a-row>
                    <a-row>
                        <!-- job执行时间 -->
                        <a-form-item name="time">
                            <a-range-picker v-model:value="formState.time" />
                        </a-form-item>
                        <a-form-item name="btn">
                            <a-space>
                                <a-button type="primary" html-type="submit">新增</a-button>
                                <a-button type="primary" html-type="submit">批量上传</a-button>
                            </a-space>
                        </a-form-item>
                    </a-row>
                </a-space>
            </a-form>
        </a-row>
        <a-row>
            <a-space>
                <a-button type="primary">批量恢复</a-button>
                <a-button type="primary" danger>批量暂停</a-button>
                <a-button type="primary" danger>批量删除</a-button>
                <a-button type="primary">批量导出</a-button>
            </a-space>
        </a-row>
        <a-row>
            <a-table :columns="jobMessageColumns" :data-source="jobMessage" bordered :scroll="{ x: 1500, y: 300 }">
            </a-table>
        </a-row>
    </a-space>
</template>

<script>
import { defineComponent, reactive, ref } from 'vue';
export default defineComponent({
    setup() {
        // 搜索框
        const searchInfo = ref('');
        const onSearch = searchValue => {
            console.log('use value', searchValue);
            console.log('or use this.value', searchInfo.value);
        };
        // 表单
        const formState = reactive({
            customSearchInfo: '',
            customJobCategory: '',
            category: '',
            exeStatus: '',
            jobStatus: '',
            time: ref(''),

        });
        const onFinish = values => {
            console.log('Success:', values);
        };
        const onFinishFailed = errorInfo => {
            console.log('Failed:', errorInfo);
        };
        // 选择器
        const focus = () => {
            console.log('focus');
        };
        const handleChange = value => {
            console.log(`selected ${value}`);
        };
        const exeStatusList = ref([
            { value: "job执行状态--全部", label: "job执行状态--全部" },
            { value: "job执行状态--上次未正常执行的job", label: "job执行状态--上次未正常执行的job" }
        ]);
        const jobStatusList = ref([
            { value: "all", label: "job状态--全部" },
            { value: 0, label: "job状态--未激活" },
            { value: 1, label: "job状态--正在运行" },
            { value: 3, label: "job状态--暂停" }
        ]);
        // 表格
        const jobMessageColumns = ref([
            {
                title: '任务名称',
                dataIndex: 'jobName',
                key: 'jobName',
            },
            {
                title: '文件路径',
                dataIndex: 'filePath',
                key: 'filePath',
            },
            {
                title: 'corn时间表达式',
                dataIndex: 'cronExpression',
                key: 'cronExpression',
            },
            {
                title: '任务参数',
                dataIndex: 'parameters',
                key: 'parameters',
            },
            {
                title: '超时时间（秒）',
                dataIndex: 'timeOut',
                key: 'timeOut',
            },
            {
                title: '任务类型',
                dataIndex: 'bin',
                val: 'bin',
            },
            {
                title: '任务状态',
                dataIndex: 'jobStatus',
                key: 'jobStatus',
            },
            {
                title: '上次执行时间',
                dataIndex: 'lastFireTime',
                key: 'lastFireTime',
            },
            {
                title: '任务结束时间',
                dataIndex: 'endTime',
                key: 'endTime',
            },
            {
                title: '创建时间',
                dataIndex: 'createTime',
                key: 'createTime',
            },
            {
                title: '故障转义',
                dataIndex: 'failover',
                key: 'failover',
            },
            {
                title: '错过马上执行',
                dataIndex: 'misfire',
                key: 'misfire',
            },
            {
                title: '马上调度',
                dataIndex: 'fireNow',
                key: 'fireNow',
            },
            {
                title: '并行执行',
                dataIndex: 'ifParallel',
                key: 'ifParallel',
            },
            {
                title: '依赖任务',
                dataIndex: 'isDependence',
                key: 'isDependence',
            },
            {
                title: '任务描述',
                dataIndex: 'desc',
                key: 'desc',
            },
            {
                title: '子节点Ip',
                dataIndex: 'slaveIp',
                key: 'slaveIp',
            },
        ]);
        const jobMessage = ref('');

        return {
            // 搜索框
            searchInfo,
            onSearch,
            // 表单
            formState,
            onFinish,
            onFinishFailed,
            // 选择器
            focus,
            handleChange,
            exeStatusList,
            jobStatusList,
            // 表格
            jobMessageColumns,
            jobMessage,
        };
    },
});
</script>