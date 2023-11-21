<template>
  <div id="menu">
    <a-menu
      :mode="menuModel"
      :theme="menuTheme"
      :openKeys="openKeys"
      v-model:selectedKeys="selectedKeys"
      @select="onSelect"
      @openChange="openChange"
    >
      <template v-for="menu in menus" :key="menu.path">
        <sub-menu v-if="!menu.hidden" :item="menu"></sub-menu>
      </template>
    </a-menu>
  </div>
</template>
<script>
import SubMenu from "./SubMenu.vue";
import { computed, watch } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from 'vue-router';
import { useMenu } from '@/composable/useMenu';

export default {
  components: {
    SubMenu
  },
  setup() {
    const layout = computed(() => getters.layout )
    const { getters } = useStore();
    const router = useRouter();
    const route = useRoute();

    const menuModel = computed(() =>
      getters.layout == "layout-head" ? "horizontal" : "inline"
    );

    const menuTheme = computed(() =>
       getters.theme === "theme-dark" || getters.theme === "theme-night" ? "dark" : "light"
    );

    const onSelect = ({ key }) => {
      router.push(key);
    }

    watch(route, () => {
        if(layout.value == "layout-comp"){
            menus.value = getters.menu.find((r) => r.path === route.matched[0].path).children;
        }
    })

    const { selectedKeys, openKeys, openChange, menus } = useMenu();

    return { onSelect, openChange, selectedKeys,  menuModel, menuTheme, openKeys, menus };
  },
};
</script>
<style>
#menu {
  /*overflow: scroll;*/
  /*height: calc(100% - 60px);*/
}

#menu::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 0;
}

#menu::-webkit-scrollbar {
  width: 0px;
  height: 0px;
}
</style>
