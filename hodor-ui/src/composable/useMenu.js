import { computed, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { useStore } from "vuex";
import { findParent, findParentAll } from '@/tools/common';

export function useMenu() {

    const { getters } = useStore();
    const route = useRoute();
    const selectedKeys = ref([]);
    const menuStore = computed(() => getters.menu);
    const openKeys = ref([]);
    const lastOpenKeys = ref([]);
    const layout = computed(() => getters.layout);
    const menus = ref(menuStore.value);

    watch(() => route.path, (path) => {
        selectedKeys.value = [path];
        let result = findParent(menus.value, path);
        if(layout.value != 'layout-head'){
            openKeys.value = result;
            lastOpenKeys.value = result;
        }
    }, { immediate: true })

    watch(() => layout.value, (layout) => {
        if(layout == "layout-comp") {
            menus.value = getters.menu.find((r) => r.path === route.matched[0].path).children;
        } else {
            menus.value = getters.menu;
        }
    }, { immediate: true})

    function openChange(keys) {
      if (getters.layout == "layout-head" ){
        return;
      }
      const lastPath = keys.find(key => lastOpenKeys.value.indexOf(key) === -1);
      //let result = findParentAll(menus.value, lastPath);
      let oneLevelMenu = [];
      menus.value.filter(s => s.parent == 0).forEach(s => {
        oneLevelMenu.push(s.path);
      })
      if (!oneLevelMenu.includes(lastPath)) {
        openKeys.value = keys;
        lastOpenKeys.value = keys;
      } else {
        let normalizePath = lastPath ? [lastPath] : [];
        openKeys.value = normalizePath
        lastOpenKeys.value = normalizePath
      }
    }

    return {
        selectedKeys, openKeys, openChange, menus
    }
}
