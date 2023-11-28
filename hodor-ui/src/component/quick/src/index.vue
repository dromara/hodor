<template>
  <a-card hoverable class="quick">
    <div :style="{ color: color }" class="quick-icon">
      <component :is="$antIcons[icon]" />
    </div>
    <div class="quick-title">
      {{ title }}
    </div>
  </a-card>
</template>
<script>
import "./index.less";
import * as Icons from "@ant-design/icons-vue";
export default {
  name: "quick",
  props: {
    icon: {
      type: String,
      required: true,
    },
    title: {
      type: String,
      required: true,
    },
    color: {
      type: String,
      required: true,
    }
  },
  setup(props) {
    
    const MenuIcon = Icons[props.icon] || {};

    return {
      MenuIcon,
    };
  },
};
</script>