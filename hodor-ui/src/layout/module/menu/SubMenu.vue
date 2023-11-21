<template>
  <a-menu-item :key="item.path" v-if="!hasChildren(item)">
    <template #icon>
      <p-icon :type="item.meta.icon"/>
    </template>
    {{ t(item.meta.i18n) }}
  </a-menu-item>
  <a-menu-item :key="item.children[0].path"
               v-else-if="item.children && item.children.length === 1 && item.parent === '0'">
    <template #icon>
      <p-icon :type="item.children[0].meta.icon"/>
    </template>
    {{ t(item.children[0].meta.i18n) }}
  </a-menu-item>
  <a-sub-menu :key="item.path" v-else>
    <template #icon>
      <p-icon :type="item.meta.icon"/>
    </template>
    <template #title>
      {{ t(item.meta.i18n) }}
    </template>
    <template v-for="child in item.children" :key="child.path">
      <sub-menu v-if="!child.hidden" :item="child"></sub-menu>
    </template>
  </a-sub-menu>
</template>
<script>
import {useI18n} from "vue-i18n";

export default {
  name: "SubMenu",
  props: {
    item: {
      type: Object,
      required: true,
    },
  },
  setup() {
    const hasChildren = function (item) {
      return item.children != undefined;
    };

    const {t} = useI18n();

    return {
      t,
      hasChildren,
    };
  },
};
</script>
