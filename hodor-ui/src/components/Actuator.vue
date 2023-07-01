<template>
    <span>执行节点列表</span>
    <span>
        <a-input-search class="search-container" placeholder="请输入你要搜索的节点" @search="onSearch" v-model="searchVal" />
    </span>
    <br />
    <div>
        <!-- 调度结点列表 -->
        <a-table :columns="columns" :data-source="data">
            <!-- 表内数据 -->
            <template #bodyCell="{ column }">
                <template v-if="column.key === 'actions'">
                    <a-space>
                        <a-button type="primary">查看详情</a-button>
                        <a-button type="primary">监控信息</a-button>
                    </a-space>
                </template>
            </template>
        </a-table>
    </div>
</template>

<script>
// 表的列名
const columns = [{
    title: 'Id',  // 列名
    dataIndex: 'id',  // 数据名
    key: 'id',  // 列的key
}, {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
}, {
    title: 'Host',
    dataIndex: 'host',
    key: 'host',
}, {
    title: 'Status',
    dataIndex: 'status',
    key: 'status',
}, {
    title: 'Actions',
    key: 'actions',
}];
// 测试数据
const data = [{
    key: '1',
    id: '1',
    name: 'actuator01',
    host: '127.0.0.1',
    status: 'Online',
}, {
    key: '2',
    id: '2',
    name: 'actuator02',
    host: '127.0.0.1',
    status: 'Online',
}, {
    key: '3',
    id: '3',
    name: 'actuator03',
    host: '127.0.0.1',
    status: 'Offline',
}];
export default {
    setup() {
        return {
            data,
            columns,
        };
    },
    props: {
        searchval: {
            type: String,
            default: () => {
                return ''
            }
        }
    },
    data() {
        return {
            searchVal: this.searchval,
        };
    },

    methods: {
        onSearch() {

        },
    }
};
</script>

<style scoped>
.search-container {
    width: 200px;
    float: right;
};
</style>