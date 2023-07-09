<template>
    <a-space direction="vertical" style="width: 100%;">
        <a-row>
            <a-input-search v-model:value="searchInfo" placeholder="请输入内容" enter-button @search="onSearch" />
        </a-row>
        <a-row>
            <a-button type="primary" @click="visible = true">新增</a-button>
        </a-row>
        <a-row>
            <a-table :columns="jobStatusColumns" :data-source="jobStatus" bordered style="width: 100%;">
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
        const jobStatusColumns = ref([{
            title: '任务组',
            dataIndex:'groupName',
            key: 'groupName',
        },
        {
            title: '阀值（%）',
            dataIndex:'threshold',
            key: 'threshold',
        },
        {
            title: '是否开启报警',
            dataIndex:'enabled',
            key: 'enabled',
        },
        {
            title: '备注',
            dataIndex:'comment',
            key: 'comment',
        },
        ]);
        const jobStatus=ref();

        return {
            // 搜索框
            searchInfo,
            onSearch,
            // 表格
            jobStatusColumns,
            jobStatus,
        };
    },
});
</script>