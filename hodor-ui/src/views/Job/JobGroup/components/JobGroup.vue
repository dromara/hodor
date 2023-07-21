<script setup>
import { onMounted, reactive, ref } from 'vue';
import { cloneDeep } from 'lodash-es';
import { queryGroupListPaging, createGroup, updateGroup, deleteGroup, queryGroupListById } from '@/apis/jobGroup'

// 新增表单
const formRef = ref();
const visible = ref(false);
const formState = reactive({
    groupName: '',
    notes: '',
    actuator: '',
});
// 新建任务分组
const createJobGroup = async (groupInfo) => {
    await createGroup(groupInfo);
}
// 创建分组事件
const onOk = () => {
    formRef.value.validateFields().then(values => {
        const { groupName } = values;
        const groupInfo = {
            id: 0,
            groupName: groupName,
            createUser: "",
            userId: 0,
            tenantId: 0,
            remark: "",
            createdAt: "",
            updatedAt: ""
        }
        createJobGroup(groupInfo);
        visible.value = false;
        formRef.value.resetFields();
        const { defaultCurrent, defaultPageSize } = paginationOpt
        queryJobGroupListPaging({ pageNo: defaultCurrent, pageSize: defaultPageSize });
    }).catch(info => {
        console.log('Validate Failed:', info);
    });
};

// 搜索框
const searchInfo = ref('');
const onSearch = searchValue => {
    queryJobGroupListById(Number(searchValue));
};
// 按id搜索任务信息
const queryJobGroupListById = async (id) => {
    await queryGroupListById(id).then((res) => {
        if (res.data === null) {
            groupList.value = null;
            paginationOpt.total = 0;
        }
        else {
            groupList.value = [res.data];
            paginationOpt.total = groupList.value.length;
        }
    })
}

// 表格
const columns = [
    {
        title: 'ID',
        dataIndex: 'id',
        key: 'id',
    },
    {
        title: '任务组名称',
        dataIndex: 'groupName',
        key: 'groupName',
    },
    {
        title: '创建人',
        dataIndex: 'createUser',
        key: 'createUser',
    },
    {
        title: '创建时间',
        dataIndex: 'createdAt',
        key: 'createdAt',
    },
    {
        title: '操作',
        dataIndex: 'action',
        key: 'action',
    }
];
// 分页器配置项
const paginationOpt = reactive({
    defaultCurrent: 1, // 默认当前页数
    defaultPageSize: 5, // 默认当前页显示数据的大小
    total: 0, // 总数
    simple: "true",
    // 改变每页数量时更新显示
    onChange: (current, size) => {
        paginationOpt.defaultCurrent = current;
        paginationOpt.defaultPageSize = size;
        const { defaultCurrent, defaultPageSize } = paginationOpt
        queryJobGroupListPaging({ pageNo: defaultCurrent, pageSize: defaultPageSize });
    },
});
const groupList = ref([]);
// 分页查询分组信息
const queryJobGroupListPaging = async ({ pageNo, pageSize }) => {
    await queryGroupListPaging({ pageNo, pageSize }).then((res) => {
        const data = res.data;
        groupList.value = data.rows;
        const { total, pageNo, pageSize } = data;
        Object.assign(paginationOpt, { total, defaultCurrent: pageNo, defaultPageSize: pageSize });
    })
}

// 编辑行
const editableData = reactive({});
const editGroupInfo = name => {
    editableData[name] = cloneDeep(...groupList.value.filter(item => name === item.groupName));
}
const saveGroupInfo = name => {
    const updateInfo = editableData[name];
    Object.assign(groupList.value.filter(item => name === item.groupName)[0], updateInfo);
    handleUpdateGroupInfo(updateInfo);
    delete editableData[name];
};

const handleUpdateGroupInfo = async (updateInfo) => {
    await updateGroup(updateInfo).then((res) => {
        console.log("更新" + res.msg);
        // 接口测试数据库并没有修改
    })
}
// 删除行
const onDelete = name => {
    console.log(name)
    groupList.value = groupList.value.filter(item => item.groupName !== name);
    // 发送请求删除
    // 接口测试："服务端异常: group暂不支持删除"
};

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
        value: 'actuator3',
        label: '执行器集群3',
    }
];


onMounted(() => {
    const { defaultCurrent, defaultPageSize } = paginationOpt
    queryJobGroupListPaging({ pageNo: defaultCurrent, pageSize: defaultPageSize });
})

</script>

<template>
    <a-space direction="vertical" style="width: 100%;">
        <a-row>
            <a-col :span="2">
                <a-button type="primary" @click="visible = true">新增</a-button>
                <a-modal v-model:visible="visible" title="Create a new collection" ok-text="Create" cancel-text="Cancel"
                    @ok="onOk">
                    <a-form ref="formRef" :model="formState" layout="vertical" name="form_in_modal">
                        <a-form-item name="groupName" label="分组名：">
                            <a-input v-model:value="formState.groupName" />
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
        <a-table :columns="columns" :data-source="groupList" bordered :pagination="paginationOpt">
            <template #bodyCell="{ column, text, record }">
                <template v-if="['id', 'groupName', 'createUser', 'createdAt'].includes(column.dataIndex)">
                    <div>
                        <a-input v-if="editableData[record.groupName]"
                            v-model:value="editableData[record.groupName][column.dataIndex]" style="margin: -5px 0" />
                        <template v-else>
                            {{ text }}
                        </template>
                    </div>
                </template>
                <template v-if="column.dataIndex === 'action'">
                    <a-space>
                        <a-button type="primary" :size="size" style="background-color: #F26161;"
                            @click="onDelete(record.groupName)">删除</a-button>
                        <a-button v-if="editableData[record.groupName]" type="primary" :size="size"
                            style="background-color: #3894FF;" @click="saveGroupInfo(record.groupName)">保存</a-button>
                        <a-button v-else type="primary" :size="size" style="background-color: #3894FF;"
                            @click="editGroupInfo(record.groupName)">编辑</a-button>
                        <a-button type="primary" :size="size" style="background-color: #7AC756;">查看节点</a-button>
                    </a-space>
                </template>
            </template>
        </a-table>

    </a-space>
</template>
