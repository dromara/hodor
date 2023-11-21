<template>
  <div id="logo">
    <!-- 图片 -->
    <span v-if="collapsed">
      <img
        class="image"
        :src="image"
        alt=""
      >
    </span>
    <!-- 标题 -->
    <div v-else>
      <span class="title">{{ title }}</span>
    </div>
  </div>
</template>
<script>
import { computed } from "vue";
import { useStore } from "vuex";
import config from '@/configure/app.config.js';
export default {
  setup() {

    const { getters } = useStore();
    const collapsed = computed(() => getters.collapsed);

    const image = config.image;
    const title = config.title;

    return {
      collapsed,
      image,
      title
    };
  }
};
</script>
