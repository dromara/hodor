<template>
  <a-form @submit="onSubmit" class="form">
    <a-row class="form-row" :gutter="16">
      <a-col :lg="6" :md="12" :sm="24">
        <a-form-item
          v-bind="validateInfos.name2"
          label="任务名">
          <a-input placeholder="请输入任务名称" v-model:value="formState.name2" />
        </a-form-item>
      </a-col>
      <a-col :xl="{span: 7, offset: 1}" :lg="{span: 8}" :md="{span: 12}" :sm="24">
        <a-form-item
          v-bind="validateInfos.url2"
          label="任务描述">
          <a-input placeholder="请输入任务描述" v-model:value="formState.url2" />
        </a-form-item>
      </a-col>
      <a-col :xl="{span: 9, offset: 1}" :lg="{span: 10}" :md="{span: 24}" :sm="24">
        <a-form-item
          v-bind="validateInfos.owner2"
          label="执行人">
          <a-select
            placeholder="请选择执行人"
            v-model:value="formState.owner2"
            >
            <a-select-option value="黄丽丽">黄丽丽</a-select-option>
            <a-select-option value="李大刀">李大刀</a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
    </a-row>
    <a-row class="form-row" :gutter="16">
      <a-col :lg="6" :md="12" :sm="24">
        <a-form-item
          v-bind="validateInfos.approver2"
          label="责任人">
          <a-select
            placeholder="请选择责任人"
            v-model:value="formState.approver2"
          >
            <a-select-option value="王伟">王伟</a-select-option>
            <a-select-option value="李红军">李红军</a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
      <a-col :xl="{span: 7, offset: 1}" :lg="{span: 8}" :md="{span: 12}" :sm="24">
        <a-form-item
          v-bind="validateInfos.dateRange2"
          label="提醒时间">
          <a-time-picker
            style="width: 100%"
            v-model:value="formState.dateRange2"
          />
        </a-form-item>
      </a-col>
      <a-col :xl="{span: 9, offset: 1}" :lg="{span: 10}" :md="{span: 24}" :sm="24">
        <a-form-item
          v-bind="validateInfos.type2"
          label="任务类型">
          <a-select
            placeholder="请选择任务类型"
            v-model:value="formState.type2">
            <a-select-option value="定时执行">定时执行</a-select-option>
            <a-select-option value="周期执行">周期执行</a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
    </a-row>
    <a-form-item v-if="showSubmit">
      <a-button htmlType="submit" >Submit</a-button>
    </a-form-item>
  </a-form>
</template>

<script>
import { defineComponent, reactive } from "vue";
import { useForm } from "@ant-design-vue/use";

export default defineComponent({
  name: 'TaskForm',
  props: {
    showSubmit: {
      type: Boolean,
      default: false
    }
  },
  setup () {
    const formState = reactive({
      name2: "",
      url2: "",
      owner2: undefined,
      approver2: undefined,
      dateRange2: "",
      type2: undefined
    });
    const {resetFields, validate, validateInfos } = useForm(
      formState,
      reactive({
        name2: [{required: true, message: '请输入任务名称'}],
        url2: [{required: true, message: '请输入任务描述'}],
        owner2: [{required: true, message: '请选择执行人'}],
        approver2: [{required: true, message: '请选择责任人'}],
        dateRange2: [{required: true, message: '请选择提醒时间'}],
        type2: [{required: true, message: '请选择任务类型'}],
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
