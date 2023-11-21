<template>
  <div>
    <page-header
      title="系统权限"
      describe="根 据 权 限 提 供 节 点 的 渲 染"
    ></page-header>
    <page-layout>
      <a-row :gutter="[10,10]">
        <a-col :span="24">
          <a-card>
            <h3>[ {{ useInfo.username }} ]</h3>
          </a-card>
        </a-col>
        <a-col :span="24">
          <a-card>
            <a-space>
              <a-button type="primary" v-permission:add>增</a-button>
              <a-button type="primary" v-permission:del>删</a-button>
              <a-button type="primary" v-permission:upd>改</a-button>
            </a-space>
          </a-card>
        </a-col>
      </a-row>
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>

<script>
import { computed, defineComponent } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  name: "permission",
  setup() {
    const store = useStore();
    const useInfo = computed(() => store.state.user.userInfo);
    return {
      useInfo,
    };
  },
});
</script>

<style scoped lang="less">
</style>
