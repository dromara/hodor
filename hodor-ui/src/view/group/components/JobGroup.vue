<script setup>
import {onMounted, reactive, ref} from 'vue';
import {cloneDeep} from 'lodash-es';
import {bindGroupActuatorAPI, createGroupAPI, queryGroupListByIdAPI, updateGroupAPI} from '@/api/job/jobGroup'
import {jobGroupStoreRef, useJobGroupStore} from '@/stores/job/jobGroup';
import {actuatorStoreRef, useActuatorStore} from '@/stores/actuator'
import {message} from 'ant-design-vue';

const jobGroupStore = useJobGroupStore();
const {paginationOpt, groupList} = jobGroupStoreRef;
const {getGroupListPaging, deleteGroup} = jobGroupStore;

const {actuatorClusterList} = actuatorStoreRef;
const {getAllClusters} = useActuatorStore();

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


const getClusterOptions = async () => {
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
const bindGroupActuator = async ({clusterName, groupName}) => {
  const res = await bindGroupActuatorAPI({clusterName, groupName});
  return res;
}
// 创建分组事件
const onOk = () => {
  formRef.value.validateFields().then(values => {
    const {groupName, clusterName} = values;
    const groupInfo = {
      id: 0,
      groupName: groupName,
      createUser: "",
      userId: 0,
      tenantId: 0,
      remark: "",
      createdAt: "",
      updatedAt: "",
      clusterName: clusterName,  // 新增字段
    }
    // 发送请求创建分组
    createJobGroup(groupInfo);
    // 绑定执行集群
    bindGroupActuator({groupName, clusterName})
    // 隐藏modal
    visible.value = false;
    // 重置文本
    formRef.value.resetFields();
    // 重新分页查询
    getGroupListPaging();
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
    getGroupListPaging();
  }
  // 如果搜索内容不为空，则搜索所有信息中包含searchValue的任务分组
  else {
    // 搜索id
    handleQueryJobGroupListById(Number(searchValue));
    // 搜索groupName
    getGroupListPaging();
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
    getGroupListPaging();
  }
}
// 按id搜索任务信息
const handleQueryJobGroupListById = async (id) => {
  await queryGroupListByIdAPI(id).then((res) => {
    if (res.data === null) {
      groupList.value = null;
      paginationOpt.total = 0;
    } else {
      groupList.value = [res.data];
      paginationOpt.total = groupList.value.length;
    }
  })
}


// 编辑行
const editableData = reactive({});
const handleUpdateGroupInfo = async (updateInfo) => {
  await updateGroupAPI(updateInfo).then((res) => {
    console.log("更新" + res.msg);
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

const onDelete = async id => {
  deleteGroup(id).then((res) => {
    if (res.successful === true) {
      groupList.value = groupList.value.filter(item => item.id !== id);
      message.success(res.msg)
    }
  });
};

const onClickCreate = () => {
  visible.value = true;
}

const refreshTable = () => {
  getGroupListPaging();
}

onMounted(() => {
  // 分页查询任务分组信息
  getGroupListPaging();
  // 获取执行集群选择器选项
  getClusterOptions();
})

</script>

<template>
  <a-card>
    <a-row type="flex" justify="space-between">
      <a-col :span="2">
        <a-button type="primary" @click="onClickCreate">新增</a-button>
        <a-modal v-model:visible="visible" title="Create a new collection" ok-text="Create" cancel-text="Cancel"
                 width="400px" @ok="onOk">
          <a-form ref="formRef" :model="formState" name="form_in_modal" layout="horizontal" v-bind="fomLayout">
            <a-form-item name="groupName" label="分组名：">
              <a-input v-model:value="formState.groupName"/>
            </a-form-item>
            <a-form-item name="notes" label="备注：">
              <a-input v-model:value="formState.notes"/>
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
                        @change="onSearchChange(searchInfo)"/>
      </a-col>
    </a-row>
  </a-card>
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
                        @click="onDelete(record.id)">删除
              </a-button>
              <a-button type="primary" style="background-color: #3894FF;"
                        @click="editGroupInfo(record.groupName)">编辑
              </a-button>
              <a-button type="primary" style="background-color: #7AC756;">查看节点</a-button>
            </a-space>
          </div>
          <div v-show="editableData[record.groupName]">
            <a-space>
              <a-button @click="cancelEditGroup(record.groupName)">取消</a-button>
              <a-button type="primary" style="background-color: #3894FF;"
                        @click="saveGroupInfo(record.groupName)">保存
              </a-button>
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
