<template>
  <a-drawer
    placement="right"
    :closable="false"
    :visible="visible"
    width="340"
    @after-visible-change="afterVisibleChange"
    @close="onChangeVisible()"
    :class="[color]"
  >
    <div>
      <div class="pearone-color">
        <div class="color-title">菜单风格</div>
        <div class="color-content">
          <ul>
            <p-demo
              @click="updateTheme('theme-dark')"
              :class="[theme === 'theme-dark' ? 'layui-this' : '']"
              :color="['#2e3549', 'white', '#2e3549', '#f4f5f7']"
            ></p-demo>
            <p-demo
              @click="updateTheme('theme-light')"
              :class="[theme === 'theme-light' ? 'layui-this' : '']"
              :color="['white', 'white', 'white', '#f4f5f7']"
            ></p-demo>
          </ul>
        </div>
      </div>
      <div class="pearone-color">
        <div class="color-title">布局方式</div>
        <div class="color-content">
          <ul>
            <p-demo
              @click="updateLayout('layout-side')"
              :class="[layout === 'layout-side' ? 'layui-this' : '']"
              :color="['#2e3549', 'white', '#2e3549', '#f4f5f7']"
            ></p-demo>
            <p-demo
              @click="updateLayout('layout-head')"
              :class="[layout === 'layout-head' ? 'layui-this' : '']"
              :color="['#2e3549', '#2e3549', '#f4f5f7', '#f4f5f7']"
            ></p-demo>
            <p-demo
              @click="updateLayout('layout-comp')"
              :class="[layout === 'layout-comp' ? 'layui-this' : '']"
              :color="['#2e3549', '#2e3549', 'white', '#f4f5f7']"
            ></p-demo>
          </ul>
        </div>
      </div>
      <div class="select-color">
        <div class="select-color-title">主题配色</div>
        <div class="select-color-content">
          <span
            v-for="(colorItem, index) in colorList"
            class="select-color-item"
            :key="index"
            @click="updateColor(colorItem)"
            :style="{ 'background-color': colorItem.color }"
            ><CheckOutlined v-if="color.scopeName === colorItem.scopeName"
          /></span>
        </div>
      </div>
      <br />
      <div class="pearone-color">
        <div class="color-title">更多设置</div>
        <div class="color-content">
          <br />
          <a-switch v-model:checked="logo" @change="onChangeLogo" />
          <div class="setting-title-right">菜单头部</div>
          <br />
          <br />
          <a-switch v-model:checked="tab" @change="onChangeTab" />
          <div class="setting-title-right">多选项卡</div>
          <br />
          <br />
          <a-switch v-model:checked="keepAlive" @change="toggleKeepAlive" />
          <div class="setting-title-right">视图缓存</div>
          <br />
          <br />
          <a-switch
            v-model:checked="fixedHeader"
            @change="onChangeFixedHeader"
          />
          <div class="setting-title-right">固定头部</div>
          <br />
          <br />
          <a-switch v-model:checked="fixedSide" @change="onChangeFixedSide" />
          <div class="setting-title-right">固定侧边</div>
          <br />
          <br />
          <a-select
            :value="tabType"
            style="width: 100px"
            @change="handleChange"
          >
            <a-select-option value="pear-tab-1"> 样式一 </a-select-option>
            <a-select-option value="pear-tab-2"> 样式二 </a-select-option>
          </a-select>
          <div class="setting-title-right">卡片样式</div>
          <br />
          <br />
          <a-select
            :value="routerAnimate"
            style="width: 100px"
            @change="updateRouterAnimate"
          >
            <a-select-option value="null"> 无 </a-select-option>
            <a-select-option value="fade-right"> 渐入 </a-select-option>
            <a-select-option value="fade-top"> 上滑 </a-select-option>
          </a-select>
          <div class="setting-title-right">路由动画</div>
        </div>
      </div>
    </div>
    <br />
    <br />
  </a-drawer>
</template>
<script>
import { toggleTheme } from "@zougt/vite-plugin-theme-preprocessor/dist/browser-utils.js";
import { CheckOutlined } from "@ant-design/icons-vue";
import { computed } from "vue";
import { useStore } from "vuex";
export default {
  components: {
    CheckOutlined,
  },
  setup() {
    const { getters, commit } = useStore();
    const visible = computed(() => getters.settingVisible);
    const logo = computed(() => getters.logo);
    const keepAlive = computed(() => getters.keepAlive);
    const tab = computed(() => getters.tab);
    const theme = computed(() => getters.theme);
    const fixedSide = computed(() => getters.fixedSide);
    const fixedHeader = computed(() => getters.fixedHeader);
    const layout = computed(() => getters.layout);
    const color = computed(() => getters.color);
    const colorList = computed(() => getters.colorList);
    const routerAnimate = computed(() => getters.routerAnimate);
    const tabType = computed(() => getters.tabType);

    const updateLayout = function (layout) {
      commit("app/UPDATE_LAYOUT", layout);
    };
    const updateTheme = function (theme) {
      commit("app/UPDATE_THEME", theme);
    };
    const updateRouterAnimate = function (animate) {
      commit("app/UPDATE_ROUTER_ANIMATE", animate);
    };
    const toggleKeepAlive = function () {
      commit("app/TOGGLE_KEEP_ALIVE");
    };
    const updateColor = function (color) {
      toggleTheme({ scopeName: color.scopeName });
      commit("app/UPDATE_COLOR", color);
    };
    const handleChange = function (value) {
      commit("app/UPDATE_TAB_TYPE", value);
    };

    return {
      tabType,
      routerAnimate,
      toggleKeepAlive,
      updateRouterAnimate,
      handleChange,
      updateLayout,
      updateTheme,
      updateColor,
      colorList,
      fixedSide,
      fixedHeader,
      visible,
      logo,
      tab,
      layout,
      keepAlive,
      theme,
      color,
      afterVisibleChange: () => {},
      onChangeVisible: () => commit("app/TOGGLE_SETTING"),
      onChangeLogo: () => commit("app/TOGGLE_LOGO"),
      onChangeTab: () => commit("app/UPDATE_TAB"),
      onChangeTheme: () => commit("app/TOGGLE_THEME"),
      onChangeFixedSide: () => commit("app/TOGGLE_FIXEDSIDE"),
      onChangeFixedHeader: () => commit("app/TOGGLE_FIXEDHEADER"),
      onChangeLayout: () => commit("app/TOGGLE_LAYOUT"),
      changeLanguage: (e) => commit("app/setLanguage", e.target.value),
    };
  },
};
</script>
<style>
.setting-title-right {
  float: right;
  margin-right: 15px;
}
.ant-drawer-body {
  padding: 10px !important;
}
</style>
