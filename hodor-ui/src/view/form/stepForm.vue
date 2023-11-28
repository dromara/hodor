<template>
  <div>
    <page-header
      title="分步表单"
      describe="将一个冗长或用户不熟悉的表单任务分成多个步骤，指导用户完成."
    ></page-header>
    <page-layout>
      <a-card>
        <a-steps :current="current">
          <a-step v-for="(item, key) in steps" :key="key" :title="item.title"/>
        </a-steps>
        <div class="steps-content">
          <!-- {{ steps[current].content }} -->
          <!-- 使用keep-alive 保存执行上一步的时候，原来的数据还在 -->
          <!-- <keep-alive> -->
          <component :is="steps[current].content" @next="next" @prev="prev" @finish="finish"></component>
          <!-- </keep-alive> -->
        </div>
      </a-card>
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>
<script>
import {defineComponent, markRaw, reactive, toRefs} from "vue";
import step1 from "@/view/form/components/step1.vue";
import step2 from "@/view/form/components/step2.vue";
import step3 from "@/view/form/components/step3.vue";

export default defineComponent({
  name: 'stepForm',
  components: {
    step1,
    step2,
    step3
  },
  setup() {
    const state = reactive({
      current: 0,
      steps: [
        {
          title: "填写转账信息",
          content: markRaw(step1),
        },
        {
          title: "确认转账信息",
          content: markRaw(step2),
        },
        {
          title: "完成",
          content: markRaw(step3),
        },
      ],
    })
    const next = e => {
      state.current++;
    }
    const prev = e => {
      state.current--;
    }
    const finish = e => {
      state.current = 0
    }
    return {
      ...toRefs(state),
      next,
      prev,
      finish
    }
  }
})
</script>
<style scoped>
</style>
