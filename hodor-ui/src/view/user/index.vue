<template>
    <page-layout>
      <!-- 列表页面 -->
      <a-row :gutter="[10, 10]">
        <!-- 查询条件 -->
        <a-col :span="24">
          <a-card>
            <p-query
              :searchParam="searchParam"
              @on-search ="search"
            >
              <template #test="{ data }">
                <a-input v-model:value="data.test" type="text" />
              </template>
            </p-query>
          </a-card>
        </a-col>
        <!-- 用户列表 -->
        <a-col :span="24">
          <a-card>
            <p-table
               ref="tableRef"
              :fetch="fetch"
              :columns="columns"
              :toolbar="toolbar"
              :operate="operate"
              :param="state.param"
              :pagination="pagination"
              :row-selection="{ selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }">
            </p-table>
          </a-card>
        </a-col>
      </a-row>
      <!-- 新增页面 -->
      <save :visible="state.visibleSave" @close="closeSave" :record="state.record"></save>
      <!-- 修改页面 -->
      <edit :visible="state.visibleEdit" @close="closeEdit" :record="state.recordEdit" ></edit>
      <!-- 分配页面 -->
      <give :visible="state.visibleGive" @close="closeGive" :record="state.recordGive"></give>
      <!-- 详情页面 -->
      <info :visible="state.visibleInfo" @close="closeInfo" :record="state.recordInfo"></info>
    </page-layout>
</template>

<script setup>
import save from './modal/save.vue';
import edit from './modal/edit.vue';
import give from './modal/give.vue';
import info from './modal/info.vue';
import { message , Modal} from 'ant-design-vue';
import { ExclamationCircleOutlined } from '@ant-design/icons-vue';
import { list, remove, removeBatch, resetPassword } from "@/api/module/user";
import { reactive, createVNode, ref } from 'vue';

const removeKey = "remove";
const removeBatchKey = "removeBatch";

const tableRef = ref();

/// 查询用户
const fetch = async (param) => {
  const response = await list(param);
  return {
    total: response.data.total,
    data: response.data.rows,
  };
};

const resetPasswordMethod = (record) => {
  resetPassword({"id":record.id}).then((response) => {
    if(response.success){
      message.success({content: "重置成功", key: removeKey, duration: 1})
    }else{
      message.error({content: "重置失败", key: removeKey, duration: 1})
    }
  })
}

/// 删除用户
const removeMethod = (record) => {
  Modal.confirm({
    title: '您是否确定要删除此用户?',
    icon: createVNode(ExclamationCircleOutlined),
    okText: '确定',
    cancelText: '取消',
    onOk() {
      message.loading({ content: "提交中...", key: removeKey });
      remove({"id":record.id}).then((response) => {
        if(response.success){
          message.success({content: "删除成功", key: removeKey, duration: 1}).then(() =>
            tableRef.value.reload()
          )
        }else{
          message.error({content: "删除失败", key: removeKey, duration: 1})
        }
      })
    }
  });
}

/// 批量删除
const removeBatchMethod = (ids) => {
  Modal.confirm({
    title: '您是否确定要删除选择用户?',
    icon: createVNode(ExclamationCircleOutlined),
    okText: '确定',
    cancelText: '取消',
    onOk() {
      message.loading({ content: "提交中...", key: removeBatchKey });
      removeBatch({"ids":ids}).then((response) => {
        if(response.success){
          message.success({content: "删除成功", key: removeBatchKey, duration: 1}).then(() =>
            tableRef.value.reload()
          )
        }else{
          message.error({content: "删除失败", key: removeBatchKey, duration: 1})
        }
      })
    }
  });
}

/// 工具栏
const toolbar = [
  { label: "新增", event: function () { state.visibleSave = true }},
  { label: "删除", event: function () { removeBatchMethod(state.selectedRowKeys) }}
];

/// 行操作
const operate = [
  { label: "查看", event: function (record) { state.visibleInfo = true , state.recordInfo = record }},
  { label: "修改", event: function (record) { state.visibleEdit = true , state.recordEdit = record }},
  { label: "分配", event: function (record) { state.visibleGive = true , state.recordGive = record }},
  { label: "删除", event: function (record) { removeMethod(record) }},
  //{ label: '重置', event: function (record) { resetPasswordMethod(record) }}
];

/// 文本
const converFormat = [{label:"男", value:"0"},{label:"女", value:"1"}];

/// 开关
const switchFormat = { yes: true, no: false, event: function(value,record){
    record.enable = !record.enable;
    return value;
  } };

/// 头像
const avatarFormat = { size: 36, shape: "square" };

/// 配置
const columns = [
  //{ dataIndex: "deptName", key: "deptName", title: "部门" },
  //{ dataIndex: "avatar", key: "avatar", title: "头像", avatar: avatarFormat },
  //{ dataIndex: "nickname", key: "nickname", title: "名称" },
  { dataIndex: "username", key: "username", title: "账号" },
  //{ dataIndex: "gender", key: "gender", title: "性别", conver: converFormat},
  { dataIndex: "enable", key: "enable", title: "状态", switch: switchFormat},
  { dataIndex: "email", key: "email", title: "邮箱" },
  { dataIndex: "phone", key: "phone", title: "电话" },
  { dataIndex: "createdAt", key: "createdAt", title: "注册时间" }
];

/// 分页参数
const pagination = { pageNo: 1, pageSize: 10}

/// 外置参数
const state = reactive({
  selectedRowKeys: [],
  param: {},
  record: {},
  visibleSave: false,
  visibleEdit: false,
  visibleGive: false,
  visibleInfo: false,
  recordEdit: {},
  recordGive: {},
  recordInfo: {},
})

/// 查询参数
const searchParam = [
  { key: "username", type: "input", label: "账号"},
  { key: "phone", type: "input", label: "电话"},
  //{ key: "code", type: "input", label: "描述"},
  //{ key: "test", type: "custom", label: "插槽", customRender: "test" }
]

/// 选择操作
const onSelectChange = selectedRowKeys => {
  state.selectedRowKeys = selectedRowKeys;
};

/// 查询操作
const search = function(value) {
  state.param = value;
  tableRef.value.reload();
}

/// 关闭新增
const closeSave = function(){
  state.visibleSave = false;
  tableRef.value.reload();
}

/// 关闭修改
const closeEdit = function(){
  state.visibleEdit = false;
  tableRef.value.reload();
}

/// 关闭分配
const closeGive = function(){
  state.visibleGive = false;
  tableRef.value.reload();
}

/// 关闭详情
const closeInfo = function(){
  state.visibleInfo = false;
}

</script>
