<template>
    <a-space direction="vertical" style="width: 100%;">
        <a-row>
            <a-col :span="4">
                <a-button type="primary" @click="visible = true" danger>批量删除</a-button>
            </a-col>
            <a-col :span="4">
                <a-button type="primary" @click="visible = true">添加报警过滤</a-button>
            </a-col>
            <a-col :span="16">
                <a-input-search v-model:value="searchInfo" placeholder="请输入内容" enter-button @search="onSearch" />
            </a-col>
        </a-row>
        <a-row>
            <a-table :row-selection="rowSelection" :columns="jobAlarmFilterColumns" :data-source="jobAlarmFilter"
                style="width: 100%;">
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
        const jobAlarmFilterColumns = reactive([
            {
                title: '组名',
                dataIndex: 'groupName',
            },
            {
                title: '错误编码',
                dataIndex: 'code',
            },
            {
                title: '错误片段',
                dataIndex: 'exception',
            },
            {
                title: '使用范围',
                dataIndex: 'scope',
            },
        ]);
        const jobAlarmFilter = ref('');


        return {
            // 搜索框
            searchInfo,
            onSearch,
            // 表格
            jobAlarmFilter,
            jobAlarmFilterColumns,
            rowSelection,
        };
    },
});
</script>