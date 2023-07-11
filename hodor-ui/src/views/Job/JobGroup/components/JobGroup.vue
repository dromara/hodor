<template>
    <a-space direction="vertical" style="width: 100%;">
        <a-row>
            <a-col :span="2">
                <a-button type="primary" @click="visible = true">新增</a-button>
                <a-modal v-model:visible="visible" title="Create a new collection" ok-text="Create" cancel-text="Cancel"
                    @ok="onOk">
                    <a-form ref="formRef" :model="formState" layout="vertical" name="form_in_modal">
                        <a-form-item name="jobGroupName" label="分组名：">
                            <a-input v-model:value="formState.jobGroupName" />
                        </a-form-item>
                        <a-form-item name="notes" label="备注：">
                            <a-input v-model:value="formState.notes" />
                        </a-form-item>
                        <a-form-item name="actuator" label="执行器：">
                            <a-cascader v-model:value="actuatorSelect" :options="options" placeholder="执行器选择" />
                        </a-form-item>
                    </a-form>
                </a-modal>
            </a-col>
            <a-col :span="6">
                <a-input-search v-model:value="searchInfo" placeholder="请输入你需要搜索的节点" @search="onSearch" />
            </a-col>
        </a-row>
        <!-- 如果给a-table加a-row就算给a-space添加width:100%也不能使表格100% -->
        <a-table :columns="columns" :data-source="jobInfo" bordered :pagination="false">
            <template #action="{ record }">
                <a-space>
                    <a-button type="primary" :size="size" style="background-color: #F26161;">删除</a-button>
                    <a-button type="primary" :size="size" style="background-color: #3894FF;">编辑</a-button>
                    <a-button type="primary" :size="size" style="background-color: #7AC756;">查看节点</a-button>
                </a-space>
            </template>
        </a-table>

        <a-row justify="end">
            <a-pagination simple v-model:current="current" :total="50" class="pagination">
                <template #itemRender="{ type, originalElement }">
                    <a v-if="type === 'prev'">上一页</a>
                    <a v-else-if="type === 'next'">下一页</a>
                    <component :is="originalElement" v-else></component>
                </template>
            </a-pagination>
        </a-row>

    </a-space>
</template>

<script>
import { defineComponent, reactive, ref, toRaw } from 'vue';
// 级联选择器
const options = [
    {
        value: 'actuator1',
        label: '执行器集群1',
    },
    {
        value: 'actuator2',
        label: '执行器集群2',
    },
    {
        // 如果这里用了actuator2，那么在选择时2和3会一起高亮，且3不显示在value中
        value: 'actuator3',
        label: '执行器集群3',
    }
];
// 表格
const columns = [
    {
        title: 'ID',
        dataIndex: 'id',
        key: 'id',
    },
    {
        title: '任务组名称',
        dataIndex: 'jobGroupName',
        key: 'jobGroupNam',
    },
    {
        title: '创建人',
        dataIndex: 'username',
        key: 'username',
    },
    {
        title: '创建时间',
        dataIndex: 'time',
        key: 'time',
    },
    {
        title: '操作',
        key: 'action',
        slots: {
            customRender: 'action',
        },
    }
];
const jobInfo = [
    {
        id: '1',
        jobGroupName: 'test1',
        username: 'james',
        time: '2023-6-20 10:04:55',
    },
    {
        id: '1',
        jobGroupName: 'test1',
        username: 'james',
        time: '2023-6-20 10:04:55',
    },
    {
        id: '1',
        jobGroupName: 'test1',
        username: 'james',
        time: '2023-6-20 10:04:55',
    },
    {
        id: '1',
        jobGroupName: 'test1',
        username: 'james',
        time: '2023-6-20 10:04:55',
    },
];

export default defineComponent({
    setup() {
        // 弹窗表单
        const formRef = ref();
        const visible = ref(false);
        const formState = reactive({
            jobGroupName: '',
            notes: '',
            actuator: '',
        });
        const onOk = () => {
            formRef.value.validateFields().then(values => {
                console.log('Received values of form: ', values);
                console.log('formState: ', toRaw(formState));
                visible.value = false;
                formRef.value.resetFields();
                console.log('reset formState: ', toRaw(formState));
            }).catch(info => {
                console.log('Validate Failed:', info);
            });
        };

        // 搜索框
        const searchInfo = ref('');
        const onSearch = searchValue => {
            console.log('use value', searchValue);
            console.log('or use this.value', value.value);
        };
        // 分页器
        const current = ref(2);

        return {
            // 弹窗
            formState,
            formRef,
            visible,
            onOk,
            // 搜索框
            searchInfo,
            onSearch,
            // 级联选择
            options,
            actuatorSelect: ref([]),
            // 表格
            columns,
            jobInfo,
            // 分页器
            current,
        };
    },
});
</script>

<style scoped>
</style>