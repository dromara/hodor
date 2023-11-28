<template>
  <a-form>
    <a-result title="操作成功" :is-success="true" sub-title="预计两小时内到账" style="max-width: 560px; margin: 40px auto 0;">
      <div class="information">
        <a-row>
          <a-col :sm="8" :xs="24">付款账户：</a-col>
          <a-col :sm="16" :xs="24">ant-design@alipay.com</a-col>
        </a-row>
        <a-row>
          <a-col :sm="8" :xs="24">收款账户：</a-col>
          <a-col :sm="16" :xs="24">test@example.com</a-col>
        </a-row>
        <a-row>
          <a-col :sm="8" :xs="24">收款人姓名：</a-col>
          <a-col :sm="16" :xs="24">辉夜</a-col>
        </a-row>
        <a-row>
          <a-col :sm="8" :xs="24">转账金额：</a-col>
          <a-col :sm="16" :xs="24"><span class="money">500</span> 元</a-col>
        </a-row>
      </div>
      <template #extra>
        <a-button type="primary" @click="finish">再转一笔</a-button>
        <a-button style="margin-left: 8px" @click="toOrderList">查看账单</a-button>
      </template>
    </a-result>
  </a-form>
</template>

<script>
import {defineComponent} from "vue";
import {useRouter} from "vue-router";

export default defineComponent({
  inheritAttrs: false,
  name: 'step3',
  emits: ['finish'],
  setup (_, { emit }) {
    const router = useRouter()
    const finish = e => {
      emit('finish')
    }
    const toOrderList = e => {
      router.push({ name: 'base-list' })
    }
    return {
      finish,
      toOrderList
    }
  }
})
</script>

<style scoped lang="less">

</style>
