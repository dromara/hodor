<template>
    <a-space direction="vertical" style="width: 100%;">
        <a-row>
            <a-form :model="formState" name="basic" layout="inline" :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }"
                autocomplete="off" @finish="onFinish" @finishFailed="onFinishFailed">
                <a-space direction="vertical">
                    <a-row>
                        <a-form-item name="batchExe">
                            <a-button type="primary">批量执行</a-button>
                        </a-form-item>
                        <a-form-item name="timeType">
                            <a-select ref="select" v-model:value="formState.timeType" style="width: 120px"
                                :options="timeType" @focus="focus" @change="handleChange">
                            </a-select>
                        </a-form-item>
                        <a-form-item name="time">
                            <a-range-picker v-model:value="formState.time" />
                        </a-form-item>
                        <a-form-item name="type">
                            <a-select ref="select" v-model:value="formState.type" style="width: 120px" :options="type"
                                @focus="focus" @change="handleChange">
                            </a-select>
                        </a-form-item>
                    </a-row>
                    <a-row>
                        <a-form-item name="group">
                            <a-input v-model:value="formState.group" placeholder="组查询" />
                        </a-form-item>
                        <a-form-item name="likeParam">
                            <a-input v-model:value="formState.likeParam" placeholder="参数查询" />
                        </a-form-item>
                        <a-form-item>
                            <a-button type="primary">搜索</a-button>
                        </a-form-item>
                    </a-row>
                </a-space>
            </a-form>
        </a-row>
        <a-row>
            <a-table :row-selection="rowSelection" :columns="onceSearchColumns" :data-source="onceSearch"
                style="width: 100%;">
            </a-table>
        </a-row>
    </a-space>
</template>

<script>
import { defineComponent, reactive, ref } from 'vue';
export default defineComponent({
    setup() {
        // 表单
        const formState = reactive({
            timeType: '',
            time: '',
            type: '',
            group: '',
            likeParam: '',
        });
        const onFinish = values => {
            console.log('Success:', values);
        };
        const onFinishFailed = errorInfo => {
            console.log('Failed:', errorInfo);
        };
        // 时间类型选择器
        const timeType = ref([
            { label: "执行时间", val: "0" },
            { label: "创建时间", val: "1" }
        ]);
        // 类型选择器
        const type = ref([
            { label: "全部", val: "1" },
            { label: "未正常调用的Job", val: "0" }
        ]);
        // 表格
        const rowSelection = {
            onChange: (selectedRowKeys, selectedRows) => {
                console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
            },
            getCheckboxProps: record => ({
                disabled: record.name === 'Disabled User',
                // Column configuration not to be checked
                name: record.name,
            }),
        };
        const onceSearchColumns = ref([
            {
                title: '任务名称',
                dataIndex: 'jobName',
            },
            {
                title: '任务组',
                dataIndex: 'groupName',
            },
            {
                title: 'corn时间表达式',
                dataIndex: 'cronExpression',
            },
            {
                title: 'job执行时间',
                dataIndex: 'executeTime',
            },
            {
                title: 'job创建时间',
                dataIndex: 'createTime',
            },
            {
                title: 'slaveIp',
                dataIndex: 'slaveIp',
            },
            {
                title: '任务参数',
                dataIndex: 'parameters',
            },
            {
                title: '任务描述',
                dataIndex: 'desc',
            },
            {
                title: '上次任务执行时间',
                dataIndex: 'lastFireTime',
            },
        ]);
        const onceSearch = ref('');

        return {
            // 表单
            formState,
            onFinish,
            onFinishFailed,
            // 时间类型选择器
            timeType,
            // 类型选择器
            type,
            // 表格
            rowSelection,
            onceSearchColumns,
            onceSearch,
        };
    },
});
</script>