<template>
  <a-form
    style="max-width: 500px; margin: 40px auto 0"
    ref="formRef"
    :rules="formRules"
    :model="formState"
  >
    <a-form-item label="付款账户" :labelCol="labelCol" :wrapperCol="wrapperCol">
      <a-select
        placeholder="pearadmin@com"
        v-model:value="formState.paymentUser"
        allowClear
      >
        <a-select-option value="1">pearadmin.com</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="收款账户" :labelCol="labelCol" :wrapperCol="wrapperCol">
      <a-input-group
        style="display: inline-block; vertical-align: middle"
        :compact="true"
      >
        <a-select defaultValue="alipay" style="width: 100px">
          <a-select-option value="alipay">支付宝</a-select-option>
          <a-select-option value="wexinpay">微信</a-select-option>
        </a-select>
        <a-input
          v-model:value="formState.payType"
          :style="{ width: 'calc(100% - 100px)' }"
        />
      </a-input-group>
    </a-form-item>
    <a-form-item
      label="收款人姓名"
      :labelCol="labelCol"
      :wrapperCol="wrapperCol"
    >
      <a-input v-model:value="formState.name" />
    </a-form-item>
    <a-form-item label="转账金额" :labelCol="labelCol" :wrapperCol="wrapperCol">
      <a-input v-model:value="formState.money" prefix="￥" />
    </a-form-item>
    <a-form-item :wrapperCol="{ span: 19, offset: 5 }">
      <a-button type="primary" @click="nextStep">下一步</a-button>
    </a-form-item>
  </a-form>
  <a-divider />
  <div class="step-form-style-desc">
    <h3>说明</h3>
    <h4>转账到支付宝账户</h4>
    <p>
      如果需要，这里可以放一些关于产品的常见问题说明。如果需要，这里可以放一些关于产品的常见问题说明。如果需要，这里可以放一些关于产品的常见问题说明。
    </p>
  </div>
</template>

<script>
/**
 * 注意: step 内容是使用的动态组件component。动态组件上有绑定子组件需要回调的emit，
 * 而动态组件是在调用的组件之上有包裹一层的，所以，回调的方法如 next 之类的是绑定在根元素上的。
 * 这样的话emit出去是无法指向正确的父级组件的
 * 所以需要切换绑定的属性是为 子组件本身之上。故设置
 * inheritAttrs = false 可以将绑定的事件切回到组件本身
 *
 * 如果不习惯这种写法，或用法。可在stepForm.vue中，用v-if代替换<component> 可实现同等效果
 *
 */
import { defineComponent, markRaw, reactive, ref, toRefs } from "vue";

export default defineComponent({
  name: "step1",
  inheritAttrs: false,
  emits: ["next"],
  setup(props, { emit }) {
    const labelCol = markRaw({ lg: { span: 5 }, sm: { span: 5 } });
    const wrapperCol = markRaw({ lg: { span: 19 }, sm: { span: 19 } });

    const formRef = ref("formRef");

    const formState = reactive({
      paymentUser: "",
      payType: "test@example.com",
      name: "Alex",
      money: "5000",
    });

    const formRules = {
      paymentUser: [{ required: true, message: "付款账户必须填写" }],
      payType: [{ required: true, message: "收款账户必须填写" }],
      name: [{ required: true, message: "收款人名称必须核对" }],
      money: [{ required: true, message: "转账金额必须填写" }],
    };

    const nextStep = async (e) => {
      formRef.value
        .validate()
        .then(async () => {
          emit("next", formState);
        })
        .catch((error) => {
          console.log(error);
        });
    };

    return {
      labelCol,
      wrapperCol,
      formRules,
      formState,
      formRef,
      nextStep,
    };
  },
});
</script>

<style scoped lang="less">
.step-form-style-desc {
  padding: 0 56px;
  color: rgba(0, 0, 0, 0.45);

  h3 {
    margin: 0 0 12px;
    color: rgba(0, 0, 0, 0.45);
    font-size: 16px;
    line-height: 32px;
  }

  h4 {
    margin: 0 0 4px;
    color: rgba(0, 0, 0, 0.45);
    font-size: 14px;
    line-height: 22px;
  }

  p {
    margin-top: 0;
    margin-bottom: 12px;
    line-height: 22px;
  }
}
</style>