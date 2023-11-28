<template>
  <div id="table-dome">
    <page-layout>

      <a-card>
        <a-form
          ref="formRef"
          name="advanced_search"
          class="ant-advanced-search-form"
          :model="formState"
          @finish="onFinish"
        >
          <a-row :gutter="24">
            <a-col :xs="24" :sm="12" :md="8" lg="6">
              <a-form-item
                :name="`field-1`"
                :label="`field-1`"
                :rules="[{ required: true, message: 'input something' }]"
              >
                <a-input v-model:value="formState[`field-1`]" placeholder="placeholder"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" lg="6">
              <a-form-item
                :name="`field-1`"
                :label="`field-1`"
                :rules="[{ required: true, message: 'input something' }]"
              >
                <a-input v-model:value="formState[`field-1`]" placeholder="placeholder"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" lg="6">
              <a-form-item
                :name="`field-1`"
                :label="`field-1`"
                :rules="[{ required: true, message: 'input something' }]"
              >
                <a-input v-model:value="formState[`field-1`]" placeholder="placeholder"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" lg="6">
              <a-form-item
                :name="`field-1`"
                :label="`field-1`"
                :rules="[{ required: true, message: 'input something' }]"
              >
                <a-input v-model:value="formState[`field-1`]" placeholder="placeholder"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" lg="6">
              <a-form-item
                :name="`field-1`"
                :label="`field-1`"
                :rules="[{ required: true, message: 'input something' }]"
              >
                <a-input v-model:value="formState[`field-1`]" placeholder="placeholder"></a-input>
              </a-form-item>
            </a-col>
            <a-col v-show="expand" :xs="24" :sm="12" :md="8" lg="6">
              <a-form-item
                :name="`field-1`"
                :label="`field-1`"
                :rules="[{ required: true, message: 'input something' }]"
              >
                <a-input v-model:value="formState[`field-1`]" placeholder="placeholder"></a-input>
              </a-form-item>
            </a-col>
            <a-col v-show="expand" :xs="24" :sm="12" :md="8" lg="6">
              <a-form-item
                :name="`field-1`"
                :label="`field-1`"
                :rules="[{ required: true, message: 'input something' }]"
              >
                <a-input v-model:value="formState[`field-1`]" placeholder="placeholder"></a-input>
              </a-form-item>
            </a-col>
            <a-col v-show="expand" :xs="24" :sm="12" :md="8" lg="6">
              <a-form-item
                :name="`field-1`"
                :label="`field-1`"
                :rules="[{ required: true, message: 'input something' }]"
              >
                <a-input v-model:value="formState[`field-1`]" placeholder="placeholder"></a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" lg="6">
              <a-button type="primary" html-type="submit">
                <template #icon>
                  <SearchOutlined/>
                </template>
                查询
              </a-button>
              <a-button style="margin: 0 8px" @click="() => formRef.resetFields()">
                <template #icon>
                  <ClearOutlined/>
                </template>
                清除
              </a-button>
              <a-button type="dashed" style="font-size: 12px" @click="expand = !expand">
                <template v-if="expand">
                  <UpOutlined/>
                </template>
                <template v-else>
                  <DownOutlined/>
                </template>
                更多
              </a-button>
            </a-col>
          </a-row>
        </a-form>
        <p-table
          :fetch="fetch"
          :value="obj"
          :columns="columns"
          :toolbar="toolbar"
          :operate="operate"
          :pagination="pagination"
          :bordered="true"
        >
          <!-- 继承至 a-table 的默认插槽 -->
          <template #name="{ record }">
            {{ record.name }}
          </template>
        </p-table>
      </a-card>
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>
<script>
import {DownOutlined, UpOutlined} from "@ant-design/icons-vue";
import {reactive, ref, watch} from "vue";

const dataItem = {
  key: "1",
  name: "Joe Black",
  sex: "boy",
  age: 32,
  createTime: "2020-02-09 00:00:00",
  address: "Sidney No. 1 Lake Park Sidney No. 1 ",
  tags: ["cool", "teacher"],
};

export default {
  setup() {
    /// 数据来源 [模拟]
    const fetch = async (param) => {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            total: 100,
            data: new Array(param.pageSize).fill(dataItem),
          });
        }, 900);
      });
    };

    /// 工具栏
    const toolbar = [
      {
        label: "新增",
        event: function (keys) {
          alert("新增操作:" + JSON.stringify(keys));
        },
      },
      {
        label: "删除",
        event: function (keys) {
          alert("批量删除:" + JSON.stringify(keys));
        },
      },
      {
        label: "更多操作",
        children: [
          {
            label: "批量导入",
            event(keys) {
              alert("批量导入");
            },
          },
          {
            label: "批量导出",
            event(keys) {
              alert("批量导出");
            },
          },
        ],
      },
    ];

    /// 字段
    const columns = [
      {
        title: "姓名",
        dataIndex: "name",
        key: "name",
        slots: {customRender: "name"},
      },
      {title: "性别", dataIndex: "sex", key: "sex"},
      {title: "年龄", dataIndex: "age", key: "age"},
      {title: "地址", dataIndex: "address", key: "address"},
    ];

    /// 行操作
    const operate = [
      {
        label: "查看",
        event: function (record) {
          alert("查看详情:" + JSON.stringify(record));
        },
      },
      {
        label: "修改",
        event: function (record) {
          alert("修改事件:" + JSON.stringify(record));
        },
      },
      {
        label: "删除",
        event: function (record) {
          alert("删除事件:" + JSON.stringify(record));
        },
      },
      {
        label: "更多",
        children: [
          {
            label: "导出",
            event: function (record) {
              alert("导出");
            },
          },
          {
            label: "下载",
            event: function (record) {
              alert("下载");
            },
          },
        ],
      },
    ];

    /// 表单
    const expand = ref(false);
    const formRef = ref();
    const formState = reactive({});
    const onFinish = values => {
      console.log('Received values of form: ', values);
      console.log('formState: ', formState);
    };
    /// 声明抛出
    return {
      pagination: {current: 1, pageSize: 10}, // 分页配置
      fetch: fetch, // 数据回调
      toolbar: toolbar, // 工具栏
      columns: columns, // 列配置
      operate: operate, // 行操作
      //表单配置
      formRef,
      formState,
      expand,
      onFinish,
    };
  },
  components: {
    DownOutlined,
    UpOutlined,
  }
};
</script>
