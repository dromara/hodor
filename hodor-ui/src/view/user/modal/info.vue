<template>
  <a-modal
    :visible="visible"
    title="查看详情"
    cancelText="取消"
    okText="确定"
    @ok="submit"
    @cancel="cancel"
  >
    <a-form
       ref="formRef"
      :model="formState"
      :rules="formRules"
      :label-col="labelCol"
      :wrapper-col="wrapperCol">
      <a-form-item ref="nickname" label="昵称" name="nickname">
        <a-input v-model:value="formState.nickname" />
      </a-form-item>
      <a-form-item ref="username" label="账户" name="username">
        <a-input v-model:value="formState.username" />
      </a-form-item>
      <a-form-item ref="email" label="邮箱" name="email">
        <a-input v-model:value="formState.email" />
      </a-form-item>
      <a-form-item ref="phone" label="电话" name="phone">
        <a-input v-model:value="formState.phone" />
      </a-form-item>
      <a-form-item label="性别" name="gender">
        <a-radio-group v-model:value="formState.gender">
          <a-radio value="0">男</a-radio>
          <a-radio value="1">女</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="状态" name="enable">
        <a-switch v-model:checked="formState.enable" />
      </a-form-item>
      <a-form-item label="岗位" name="postId">
          <a-select v-model:value="formState.postId">
            <a-select-option :value="post.id" v-bind:key="index" v-for="(post,index) in state.posts">{{post.name}}</a-select-option>
          </a-select>
      </a-form-item>
      <a-form-item label="部门" name="deptId">
          <a-tree-select
            v-model:value="formState.deptId"
            style="width: 100%"
            :tree-data="state.depts"
            placeholder="所属部门"
            replace
            tree-default-expand-all
            :replaceFields="replaceFields"
          >
          </a-tree-select>
      </a-form-item>
      <a-form-item label="备注" name="remark">
        <a-textarea v-model:value="formState.remark" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script>
import { defineComponent, reactive, ref, watch } from "vue";
export default defineComponent({
  props: {
    visible: {
      type: Boolean,
    },
    record: {
      type: Object,
    }
  },
  emit: ["close"],
  setup(props, context) {

    const formRef = ref();

    const state = reactive({
      posts: [],
      depts: [],
    })

    const formState = reactive({
      enable: true,
      gender:  "0",
    });

    const formRules = {};

    const submit = (e) => {
      formRef.value.resetFields();
      context.emit("close", false);
    };

    const cancel = (e) => {
      formRef.value.resetFields();
      context.emit("close", false);
    };

    watch(props,(props) => {
      formState.id = props.record.id;
      formState.username = props.record.username;
      formState.nickname = props.record.nickname;
      formState.password = props.record.password;
      formState.email = props.record.email;
      formState.phone = props.record.phone;
      formState.postId = props.record.postId;
      formState.deptId = props.record.deptId;
      formState.enable = props.record.enable;
      formState.remark = props.record.remark;
    })

    return {
      state,
      submit,
      cancel,
      formRef,
      formState,
      formRules,

      labelCol: { span: 6 },
      wrapperCol: { span: 18 },

      replaceFields: {children:'children', title:'name', key:'id', value: 'id' }
    };
  },
});
</script>
