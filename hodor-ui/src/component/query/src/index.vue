<template>
  <div class="p-query">
    <!-- 表单内容 -->
    <a-form 
     layout="inline" 
    :model="formState"  
    :label-col="labelCol"
    :wrapper-col="wrapperCol">
      <a-form-item v-show="!param.hidden || !hidden" :label="param.label" :key="index" v-for="(param, index) in searchParam">
        <!-- 输入框 -->
        <a-input v-model:value="formState[param.key]" v-show="!param.hidden || !hidden" v-if="param.type == 'input'" type="text">
        </a-input>
        <!-- 选择框 -->
        <a-select v-if="param.type == 'select'" v-model:value="formState[param.key]" style="width: 160px" class="p-query-select">
          <a-select-option :key="index" v-for="(option,index) in param.options" :value="option.value">{{option.text}}</a-select-option>
        </a-select>
        <!-- 日期选择 -->
        <a-time-picker v-if="param.type == 'timePicker'" v-model:value="formState[param.key]" />
        <a-date-picker v-if="param.type == 'datePicker'" v-model:value="formState[param.key]" />
        <a-week-picker v-if="param.type == 'weekPicker'" v-model:value="formState[param.key]" />
        <!-- 内容插槽 -->
        <template v-if="param.type == 'custom'">
          <slot :name="param.customRender" :data="formState"></slot>
        </template>
      </a-form-item>
      <!-- 按钮组 -->
      <a-button type="primary" class="p-query-button" @click="search"> 查询 </a-button>
      <a-button html-type="submit" class="p-query-button" @click="reset"> 重置 </a-button>
      <!-- 收起 隐藏 -->
      <a class="p-query-hidden" @click="hiddenHandle" v-show="hidden && more">展开 &nbsp;<DownOutlined/></a>
      <a class="p-query-hidden" @click="hiddenHandle" v-show="!hidden && more">收起 &nbsp;<UpOutlined/></a>
    </a-form>
  </div>
</template>
<script>
import "./index.less";
import { defineComponent, reactive, ref } from "vue";
import { DownOutlined , UpOutlined } from "@ant-design/icons-vue"
import { useForm } from "@ant-design-vue/use";

export default defineComponent({
  name: "p-query",
  props: {
    searchParam: {
      type: Array,
    },
    labelCol: {
      type: Object,
      default: function() { return { span: 10 }} 
    },
    wrapperCol: {
      type: Object,
      default: function() { return { span: 14 }} 
    }
  },
  components: {
      DownOutlined,
      UpOutlined
  },
  emits: [
    'on-search',
    'on-reset'
  ],
  setup(props, {emit}) {
    
    const hidden = ref(true);
    const more = ref(false);

    const formState = reactive({})
    const formRules = reactive({})

    const generateFormState = array => {
      array.length > 0 && array.forEach(it => {
        const {key, rules = [], value} = it
        formState[key] = value == undefined ? "":value
        formRules[key] = rules
      })
    }

    generateFormState(props.searchParam);
    const { validate, resetFields } = useForm(formState, formRules)
    
    /// 是否存在隐藏表单
    props.searchParam.forEach(element => {
        if(element.hidden){
           return more.value = true;
        }
    });

    /// 更多操作
    const hiddenHandle = function() {
        hidden.value = !hidden.value;
    }

    /// 表单查询
    const search = async function(){
      var o = await validate();
      emit('on-search',o)
    }

    /// 重置表单
    const reset = function(){
      resetFields();
      emit('on-reset')
    }

    return {
        more: more,
        hidden: hidden,
        hiddenHandle: hiddenHandle,

        /// EMIT
        search: search,
        reset: reset,

        /// 状态
        formState,
        formRules
    }
  }
});
</script>

<style>
.p-query-hidden {
  line-height: 30px;
}
</style>