<script setup>
import { createApp, onMounted, reactive, ref } from 'vue';
import { cloneDeep } from 'lodash-es';
import { queryGroupListPagingAPI, createGroupAPI, updateGroupAPI, deleteGroupAPI, queryGroupListByIdAPI, bindGroupActuatorAPI, getBindListAPI } from '@/apis/job/jobGroup'
import { useActuatorStore } from '@/stores/actuator'
import { storeToRefs } from 'pinia'
import { timeTransfer } from '@/utils/timeTransfer'
import { message } from 'ant-design-vue';

// 新增任务分组表单
const formRef = ref();
const visible = ref(false);
const formState = reactive({
    groupName: '',
    notes: '',
    clusterName: undefined,
});
// modal布局
const fomLayout = {
    labelCol: {
        span: 4,
    },
    wrapperCol: {
        span: 16,
    },
};
// 选择器
const actuatorStore = useActuatorStore();
const { actuatorClusterList } = storeToRefs(actuatorStore);
const { getAllClusters } = actuatorStore;
// 选择器选项列表
const options = ref([]);
// 搜索框
const searchInfo = ref('');

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
        title: '执行集群',
        dataIndex: 'clusterName',
        key: 'clusterName',
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


const getClusterOptions = async() => {
    // 获取执行节点列表
    await getAllClusters();
    options.value = actuatorClusterList.value.map(element => {
        return {
            value: element,
            label: element,
        }
    });
}


// 新建任务分组
const createJobGroup = async (groupInfo) => {
    const res = await createGroupAPI(groupInfo);
    return res
}
// 绑定执行集群
const bindGroupActuator = async ({ clusterName, groupName }) => {
    const res = await bindGroupActuatorAPI({ clusterName, groupName });
    return res;
}
// 创建分组事件
const onOk = () => {
    formRef.value.validateFields().then(values => {
        const { groupName, clusterName } = values;
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
        // 发送请求创建分组
        createJobGroup(groupInfo);
        // 绑定执行集群
        bindGroupActuator({ groupName, clusterName })
        // 隐藏modal
        visible.value = false;
        // 重置文本
        formRef.value.resetFields();
        // 重新分页查询
        const { defaultCurrent, defaultPageSize } = paginationOpt
        queryJobGroupListPaging({ pageNo: defaultCurrent, pageSize: defaultPageSize });
    }).catch(info => {
        console.log('Validate Failed:', info);
    });
};
//
const onSelectChange = (value) => {
    // console.log(value)
}

// 搜索事件
const onSearch = (searchValue) => {
    // 如果搜索内容为空，则分页查询
    if (searchValue === "") {
        const { defaultCurrent, defaultPageSize } = paginationOpt
        queryJobGroupListPaging({ pageNo: defaultCurrent, pageSize: defaultPageSize });
    }
    // 如果搜索内容不为空，则搜索所有信息中包含searchValue的任务分组
    else {
        // 搜索id
        handleQueryJobGroupListById(Number(searchValue));
        // 搜索groupName
        const { defaultCurrent, defaultPageSize } = paginationOpt
        queryJobGroupListPaging({ pageNo: defaultCurrent, pageSize: defaultPageSize }, searchValue);
        // 搜索createUser
        // 搜索userId
        // 搜索tenantId
        // remark
        // createdAt
        // updatedAt
    }
};
const onSearchChange = (searchValue) => {
    if (searchValue === "") {
        const { defaultCurrent, defaultPageSize } = paginationOpt
        queryJobGroupListPaging({ pageNo: defaultCurrent, pageSize: defaultPageSize });
    }
}
// 按id搜索任务信息
const handleQueryJobGroupListById = async (id) => {
    await queryGroupListByIdAPI(id).then((res) => {
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


// 分页查询分组信息
const queryJobGroupListPaging = async ({ pageNo, pageSize }, groupName = "") => {
    await queryGroupListPagingAPI({ pageNo, pageSize }, groupName).then((res) => {
        const data = res.data;
        groupList.value = data.rows;
        groupList.value = groupList.value.map((item) => {
            const { createdAt } = item;
            // console.log("createdAt", createdAt)
            if (createdAt) {
                const localTimeString = timeTransfer(createdAt);
                // console.log("localTimeString", typeof localTimeString)
                return Object.assign(item, {
                    ...item,
                    createdAt: localTimeString,
                })
            }
        })
        const { total, pageNo, pageSize } = data;
        Object.assign(paginationOpt, { total, defaultCurrent: pageNo, defaultPageSize: pageSize });
    })
    getBindList();

}
//获取执行集群与任务分组的绑定关系
const getBindList = async () => {
    const res = await getBindListAPI();
    return res;
}

// 编辑行
const editableData = reactive({});
const handleUpdateGroupInfo = async (updateInfo) => {
    await updateGroupAPI(updateInfo).then((res) => {
        console.log("更新" + res.msg);
        // 接口测试数据库并没有修改
    })
}
const editGroupInfo = name => {
    editableData[name] = cloneDeep(...groupList.value.filter(item => name === item.groupName));
}
const saveGroupInfo = name => {
    const updateInfo = editableData[name];
    Object.assign(groupList.value.filter(item => name === item.groupName)[0], updateInfo);
    handleUpdateGroupInfo(updateInfo);
    delete editableData[name];
};
const cancelEditGroup = name => {
    delete editableData[name];
}

// 删除行
const handleDeleteGroupInfo = async (groupName) => {
    await deleteGroupAPI(groupName).then((res) => {
        if (res.success === true) {
            groupList.value = groupList.value.filter(item => item.groupName !== groupName);
            message.success(res.msg)
        }
        else {
            message.error(res.msg)
        }
    }).catch(error => {
        message.error(error.msg)
    })
}
const onDelete = async groupName => {
    await handleDeleteGroupInfo(groupName);
    // 发送请求删除
    // 接口测试："服务端异常: group暂不支持删除"
};

const onClickCreate = () => {
    visible.value = true;
}


onMounted(() => {
    // 分页查询任务分组信息
    const { defaultCurrent, defaultPageSize } = paginationOpt
    queryJobGroupListPaging({ pageNo: defaultCurrent, pageSize: defaultPageSize });
    // 获取执行集群选择器选项
    getClusterOptions();
})

</script>

<template>
    <a-card>
        <a-row>
            <a-col :span="2">
                <a-button type="primary" @click="onClickCreate">新增</a-button>
                <a-modal v-model:visible="visible" title="Create a new collection" ok-text="Create" cancel-text="Cancel"
                    width="400px" @ok="onOk">
                    <a-form ref="formRef" :model="formState" name="form_in_modal" layout="horizontal" v-bind="fomLayout">
                        <a-form-item name="groupName" label="分组名：">
                            <a-input v-model:value="formState.groupName" />
                        </a-form-item>
                        <a-form-item name="notes" label="备注：">
                            <a-input v-model:value="formState.notes" />
                        </a-form-item>
                        <a-form-item name="clusterName" label="执行器：">
                            <a-select ref="select" v-model:value="formState.clusterName" :options="options"
                                placeholder="执行器选择" @change="onSelectChange"></a-select>
                        </a-form-item>
                    </a-form>
                </a-modal>
            </a-col>
            <a-col :span="6">
                <a-input-search v-model:value="searchInfo" placeholder="请输入你需要搜索的节点" @search="onSearch"
                    @change="onSearchChange(searchInfo)" />
            </a-col>
        </a-row>
    </a-card>
    <br />
    <a-card>
        <a-table :columns="columns" :data-source="groupList" bordered :pagination="paginationOpt" :scroll="{ x: true }">
            <template #bodyCell="{ column, text, record }">
                <template v-if="['clusterName'].includes(column.dataIndex)">
                    <div>
                        <a-select v-if="editableData[record.groupName]" ref="select"
                            v-model:value="editableData[record.groupName][column.dataIndex]" :options="options"
                            placeholder="执行器选择" @change="onSelectChange"></a-select>
                        <template v-else>
                            {{ text }}
                        </template>
                    </div>
                </template>
                <template v-if="column.dataIndex === 'action'">
                    <div v-show="!editableData[record.groupName]">
                        <a-space>
                            <a-button type="primary" style="background-color: #F26161;"
                                @click="onDelete(record.groupName)">删除</a-button>
                            <a-button type="primary" style="background-color: #3894FF;"
                                @click="editGroupInfo(record.groupName)">编辑</a-button>
                            <a-button type="primary" style="background-color: #7AC756;">查看节点</a-button>
                        </a-space>
                    </div>
                    <div v-show="editableData[record.groupName]">
                        <a-space>
                            <a-button @click="cancelEditGroup(record.groupName)">取消</a-button>
                            <a-button type="primary" style="background-color: #3894FF;"
                                @click="saveGroupInfo(record.groupName)">保存</a-button>
                        </a-space>
                    </div>
                </template>
            </template>
        </a-table>
    </a-card>
</template>

<style scoped>
:deep(.ant-table) {
    white-space: nowrap;
}
</style>