<template>
  <a-config-provider :locale="antdLocal">
    <router-view />
  </a-config-provider>
</template>
<script>
//test
import {computed, defineComponent} from "vue";
import {useStore} from "vuex";
import {useI18n} from 'vue-i18n';
import {toggleTheme} from "@zougt/vite-plugin-theme-preprocessor/dist/browser-utils.js";

export default defineComponent({
  name: 'App',
  setup() {
    const store = useStore()
    const color = computed(() => store.getters.color);
    const antdLocal = computed(() => {
      const { getLocaleMessage } = useI18n({ useScope: 'global' })
      return  getLocaleMessage(store.getters.language).antLocal
    });
    toggleTheme({
      scopeName: color.value.scopeName,
    });
    return {
      antdLocal
    }
  }
})
</script>
<style>
#app, body, html {
  height: 100%;
}
</style>
