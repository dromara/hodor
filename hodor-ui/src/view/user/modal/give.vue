<template>
    <a-modal :visible="visible" title="分配角色" cancelText="取消" okText="提交" @ok="submit" @cancel="cancel">
        <p-table
            :fetch="fetch"
            :columns ="columns"
            :row-selection="{ selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }">
        </p-table>
    </a-modal>
</template>
<script>
import { message } from 'ant-design-vue';
import { list } from "@/api/module/role";
import { role, give } from "@/api/module/user";
import { defineComponent, reactive, ref, toRaw, watch } from "vue";
export default defineComponent({
  props: {
      visible: {
        type: Boolean
      },
      record: {
        type: Object
      }
  },
  emit: ['close'],
  setup(props, context) {

    const state = reactive({
      selectedRowKeys: []
    });

    /// 加载用户角色
    watch(props,async (value) => {
      state.selectedRowKeys = [];
      var response = await role({"userId":value.record.id})
      response.data.forEach(element => {
        state.selectedRowKeys.push(element.id);
      });
    })

    /// 加载角色列表
    const fetch = async (param) => {
      var response = await list(param);
      return {
        data: response.data,
      };
    };

    const columns = [
      { dataIndex: "name", key: "name", title: "名称" },
      { dataIndex: "remark", key: "remark", title: "描述" }
    ];

    const submit = e => {
          give({"userId":props.record.id,"roleIds":state.selectedRowKeys}).then((response)=>{
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
    };

    const cancel = e => {
      context.emit('close', false);
    }

    const onSelectChange = selectedRowKeys => {
      state.selectedRowKeys = selectedRowKeys;
    };

    return {
      state,
      fetch,
      submit,
      cancel,
      columns,
      onSelectChange
    };
  },
});
</script>
