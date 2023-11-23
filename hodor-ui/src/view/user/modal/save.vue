<template>
  <a-modal
    :visible="visible"
    title="新增用户"
    cancelText="取消"
    okText="提交"
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
      <a-form-item ref="password" label="密码" name="password">
        <a-input v-model:value="formState.password" />
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
import { message } from 'ant-design-vue';
//import { list } from "@/api/module/post";
import { save } from "@/api/module/user";
//import { tree } from "@/api/module/dept";
import { defineComponent, reactive, ref, toRaw } from "vue";
export default defineComponent({
  props: {
    visible: {
      type: Boolean,
    },
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

    const formRules = {
      username: [ { required: true, message: '请输入账户', trigger: 'blur'} ],
      nickname: [ { required: true, message: '请输入昵称', trigger: 'blur'} ],
      password: [ { required: true, message: '请输入密码', trigger: 'blur'} ],
      email: [ { required: true, message: '请输入邮箱', trigger: 'blur'} ],
      phone: [ { required: true, message: '请输入电话', trigger: 'blur'} ],
      postId: [ { required: true, message: '请选择岗位', trigger: 'blur'} ],
      deptId: [ { required: true, message: '请选择部门', trigger: 'blur'} ],
      tenantId: [ { required: true, message: '请选择租户', trigger: 'blur'} ]
    };

    /*const loadPost = () => {
      list({}).then((response)=>{
        state.posts = response.data;
      })
    }

    const loadTree = () => {
      tree({}).then((response)=>{
        state.depts = response.data;
      })
    }*/

    const submit = (e) => {
        formRef.value.validate().then(() => {
          save(toRaw(formState)).then((response)=>{
            if(response.success){
                message.success({ content: '保存成功', duration: 1 }).then(()=>{
                  cancel();
                });
            }else{
                message.error({ content: '保存失败', duration: 1 }).then(()=>{
                  cancel();
                });
            }
          })
      }).catch(error => {
          console.log('error', error);
      });
    };

    const cancel = (e) => {
      formRef.value.resetFields();
      context.emit("close", false);
    };

    /// 加载岗位
    //loadPost();
    /// 加载部门
    //loadTree();

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
