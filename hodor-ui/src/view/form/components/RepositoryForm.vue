<template>
  <a-form class="form" @submit="onSubmit" :scrollToFirstError="true">
    <a-row class="form-row" :gutter="16">
      <a-col :lg="6" :md="12" :sm="24">
        <a-form-item label="仓库名" v-bind="validateInfos.name">
          <a-input
            placeholder="请输入仓库名称"
            v-model:value="formState.name"
          />
        </a-form-item>
      </a-col>
      <a-col :xl="{span: 7, offset: 1}" :lg="{span: 8}" :md="{span: 12}" :sm="24">
        <a-form-item
          label="仓库域名"
          v-bind="validateInfos.url"
        >
          <a-input
            addonBefore="http://"
            addonAfter=".com"
            placeholder="请输入"
            v-model:value="formState.url"
          />
        </a-form-item>
      </a-col>
      <a-col :xl="{span: 9, offset: 1}" :lg="{span: 10}" :md="{span: 24}" :sm="24">
        <a-form-item
          label="仓库管理员"
          v-bind="validateInfos.owner"
        >
          <a-select placeholder="请选择管理员" v-model:value="formState.owner">
            <a-select-option value="王同学">王同学</a-select-option>
            <a-select-option value="李同学">李同学</a-select-option>
            <a-select-option value="黄同学">黄同学</a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
    </a-row>
    <a-row class="form-row" :gutter="16">
      <a-col :lg="6" :md="12" :sm="24">
        <a-form-item
          v-bind="validateInfos.approver"
          label="审批人">
          <a-select placeholder="请选择审批员" v-model:value="formState.approver">
            <a-select-option value="王晓丽">王晓丽</a-select-option>
            <a-select-option value="李军">李军</a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
      <a-col :xl="{span: 7, offset: 1}" :lg="{span: 8}" :md="{span: 12}" :sm="24">
        <a-form-item
          v-bind="validateInfos.dateRange"
          label="生效日期">
          <a-range-picker
            style="width: 100%"
            v-model:value="formState.dateRange"
          />
        </a-form-item>
      </a-col>
      <a-col :xl="{span: 9, offset: 1}" :lg="{span: 10}" :md="{span: 24}" :sm="24">
        <a-form-item
          v-bind="validateInfos.type"
          label="仓库类型">
          <a-select
            placeholder="请选择仓库类型"
            v-model:value="formState.type"
          >
            <a-select-option value="公开">公开</a-select-option>
            <a-select-option value="私密">私密</a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
    </a-row>
    <a-form-item v-if="showSubmit">
      <a-button htmlType="submit">Submit</a-button>
    </a-form-item>
  </a-form>
</template>

<script>
import { defineComponent, reactive } from "vue";
import { useForm } from "@ant-design-vue/use";

export default defineComponent({
  name: 'RepositoryForm',
  props: {
    showSubmit: {
      type: Boolean,
      default: false
    }
  },
  setup() {
    const formState = reactive({
      name: "",
      url: "",
      owner: undefined,
      approver: undefined,
      dateRange: [],
      type: undefined
    });
    const validateUrl = (rule, value, callback) => {
      const regex = /^user-(.*)$/
      if (!regex.test(value)) {
        callback(new Error('需要以 user- 开头'))
      }
      callback()
    }
    const {resetFields, validate, validateInfos} = useForm(
      formState,
      reactive({
        name: [{required: true, message: '请输入仓库名称'}],
        url: [{required: true, message: '请输入仓库域名'}, {validator: validateUrl}],
        owner: [{required: true, message: '请选择管理员'}],
        approver: [{required: true, message: '请选择审批员'}],
        dateRange: [{required: true, type: 'array', message: '请选择生效日期'}],
        type: [{required: true, message: '请选择仓库类型'}],
      })
    );

    const onSubmit = async e => {
      e.preventDefault()
      const v = await validate()
      console.log(v)
    }

    return {
      validateInfos,
      onSubmit,
      formState,
      validate,
      resetFields
    }
  }
})
</script>

<style scoped lang="less">
@import "./form";
</style>
