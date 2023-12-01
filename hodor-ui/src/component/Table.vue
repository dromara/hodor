<script setup>
import { ref, reactive } from 'vue';
const props = defineProps(['columns', 'dataSource', 'pagination', 'rowSelection'])

const staticPagination = reactive({
    // pageSize切换器
    showSizeChanger: true,
    //显示数据总量和当前数据顺序
    showTotal: (total) => `共有 ${total} 条数据`,
})
const paginationOpt = reactive(
    Object.assign(staticPagination, {
        ...props.pagination,
    })
)

</script>

<template>
    <a-table :columns="props.columns" :data-source="props.dataSource" bordered :scroll="{ x: true }"
        :pagination="props.pagination" :row-selection="props.rowSelection" :rowKey="row => row.id">
        <template #bodyCell="{ column, text, record }">
            <template v-if="column.dataIndex === 'action'">
                <slot :column="column" :record="record"></slot>
            </template>
        </template>
    </a-table>
</template>
