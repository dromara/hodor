<template>
  <div>
    <page-header
      title="高级表单"
      describe="高级表单常见于一次性输入和提交大批量数据的场景"
    ></page-header>
    <page-layout>
      <a-card class="card" title="仓库管理" :bordered="false">
        <repository-form ref="repository" :showSubmit="false"/>
      </a-card>
      <a-card class="card" title="任务管理" :bordered="false">
        <task-form ref="task" :showSubmit="false"/>
      </a-card>

      <!-- table -->
      <a-card>
        <a-table
          :columns="columns"
          :dataSource="data"
          :pagination="false"
          :loading="memberLoading"
        >
          <template v-for="(col, i) in ['name', 'workId', 'department']" v-slot:[col]="{text, record}">
            <a-input
              :key="col"
              v-if="record.editable"
              style="margin: -5px 0"
              :value="text"
              :placeholder="columns[i].title"
              @change="e => handleChange(e.target.value, record.key, col)"
            />
            <template v-else>{{ text }}</template>
          </template>
          <template #operation="{record}">
            <template v-if="record.editable">
            <span v-if="record.isNew">
              <a @click="saveRow(record)">添加</a>
              <a-divider type="vertical"/>
              <a-popconfirm title="是否要删除此行？" @confirm="remove(record.key)">
                <a>删除</a>
              </a-popconfirm>
            </span>
              <span v-else>
              <a @click="saveRow(record)">保存</a>
              <a-divider type="vertical"/>
              <a @click="cancel(record.key)">取消</a>
            </span>
            </template>
            <span v-else>
            <a @click="toggle(record.key)">编辑</a>
            <a-divider type="vertical"/>
            <a-popconfirm title="是否要删除此行？" @confirm="remove(record.key)">
              <a>删除</a>
            </a-popconfirm>
          </span>
          </template>
        </a-table>
        <a-button style="width: 100%; margin-top: 16px; margin-bottom: 24px" type="dashed" @click="newMember">
          <template #icon>
            <PlusOutlined/>
          </template>
          新增成员
        </a-button>
        <a-button @click="resetForm">重置</a-button>
        <a-button type="primary" @click="validate" :loading="loading" style="margin-left: 16px;">提交</a-button>
      </a-card>
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>

<script>
import {defineComponent, reactive, ref, toRefs} from "vue";
import RepositoryForm from "@/view/form/components/RepositoryForm.vue";
import TaskForm from "@/view/form/components/TaskForm.vue";
import {message, notification} from 'ant-design-vue'
import {PlusOutlined} from "@ant-design/icons-vue";

const fieldLabels = {
  name: '仓库名',
  url: '仓库域名',
  owner: '仓库管理员',
  approver: '审批人',
  dateRange: '生效日期',
  type: '仓库类型',
  name2: '任务名',
  url2: '任务描述',
  owner2: '执行人',
  approver2: '责任人',
  dateRange2: '生效日期',
  type2: '任务类型'
}

export default defineComponent({
  name: 'advancedForm',
  components: {
    'repository-form': RepositoryForm,
    'task-form': TaskForm,
    PlusOutlined
  },
  setup() {
    const repository = ref(null)
    const task = ref(null)
    const state = reactive({
      loading: false,
      memberLoading: false,
      // table
      columns: [
        {
          title: '成员姓名',
          dataIndex: 'name',
          key: 'name',
          width: '20%',
          slots: {customRender: 'name'}
        },
        {
          title: '工号',
          dataIndex: 'workId',
          key: 'workId',
          width: '20%',
          slots: {customRender: 'workId'}
        },
        {
          title: '所属部门',
          dataIndex: 'department',
          key: 'department',
          width: '40%',
          slots: {customRender: 'department'}
        },
        {
          title: '操作',
          key: 'action',
          slots: {customRender: 'operation'}
        }
      ],
      data: [
        {
          key: '1',
          name: '小明',
          workId: '001',
          editable: false,
          department: '行政部'
        },
        {
          key: '2',
          name: '李莉',
          workId: '002',
          editable: false,
          department: 'IT部'
        },
        {
          key: '3',
          name: '王小帅',
          workId: '003',
          editable: false,
          department: '财务部'
        }
      ],
      errors: []
    })

    const newMember = () => {
      const length = state.data.length
      state.data.push({
        key: length === 0 ? '1' : (parseInt(state.data[length - 1].key) + 1).toString(),
        name: '',
        workId: '',
        department: '',
        editable: true,
        isNew: true
      })
    }
    const remove = (key) => {
      const newData = state.data.filter(item => item.key !== key)
      state.data = newData
    }

    const saveRow = (record) => {
      state.memberLoading = true
      const {key, name, workId, department} = record
      if (!name || !workId || !department) {
        state.memberLoading = false
        message.error('请填写完整成员信息。')
        return
      }
      // 模拟网络请求、卡顿 800ms
      new Promise((resolve) => {
        setTimeout(() => {
          resolve({loop: false})
        }, 800)
      }).then(() => {
        const target = state.data.find(item => item.key === key)
        target.editable = false
        target.isNew = false
        state.memberLoading = false
      })
    }
    const toggle = (key) => {
      const target = state.data.find(item => item.key === key)
      target._originalData = {...target}
      target.editable = !target.editable
    }

    const getRowByKey = (key, newData) => {
      const data = state.data
      return (newData || data).find(item => item.key === key)
    }
    const cancel = (key) => {
      const target = state.data.find(item => item.key === key)
      Object.keys(target).forEach(key => {
        target[key] = target._originalData[key]
      })
      target._originalData = undefined
    }

    const handleChange = (value, key, column) => {
      const newData = [...state.data]
      const target = newData.find(item => key === item.key)
      if (target) {
        target[column] = value
        state.data = newData
      }
    }
    // 全页面提交
    const validate = async () => {
      const promises = [repository.value.validate(), task.value.validate()]
      try{
        const validates = await Promise.all(promises)
        console.log(validates)
      } catch (e) {
        console.log(e)
      }
    }

    const resetForm = e => {
      repository.value.resetFields()
      task.value.resetFields()
    }

    return {
      repository,
      task,
      ...toRefs(state),
      newMember,
      remove,
      saveRow,
      toggle,
      getRowByKey,
      cancel,
      handleChange,
      validate,
      resetForm
    }
  }
})
</script>

<style scoped lang="less">
.card {
  margin-bottom: 24px;
}
</style>
