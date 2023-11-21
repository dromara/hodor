<template>
  <div id="tab" :class="[tabType]">
    <a-tabs
      hide-add
      v-model:activeKey="active"
      @change="onChange"
      @edit="onEdit"
      class="tab"
      type="editable-card"
    >
      <a-tab-pane
        v-for="pane in list"
        :key="pane.path"
        :closable="list.length > 1"
      >
        <template #tab>
          <span class="tab-dot"></span>
          {{ i18nTitle(pane.i18n) }}
        </template>
      </a-tab-pane>
      <template #rightExtra>
        <a-dropdown class="tab-tool">
          <a-button>
            <template v-slot:icon>
              <DownOutlined />
            </template>
          </a-button>
          <template v-slot:overlay>
            <a-menu>
              <a-menu-item @click="closeOther()"> 关 闭 其 他 </a-menu-item>
              <a-menu-item @click="closeCurrent()"> 关 闭 当 前 </a-menu-item>
              <a-menu-item @click="closeAll()"> 关 闭 全 部 </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </template>
    </a-tabs>
  </div>
</template>
<script>
import { computed } from "vue";
import { useStore } from "vuex";
import { DownOutlined } from "@ant-design/icons-vue";
import { useTab } from "@/composable/useTab";
import { useI18n } from "vue-i18n";

export default {
  components: {
    DownOutlined,
  },
  setup() {
    const { t } = useI18n();
    const { getters } = useStore();
    const { to, list, active, close, closeOther, closeCurrent, closeAll } = useTab();
    const tabType = computed(() => getters.tabType);
    
    const onEdit = function (path, action) {
      if (action === "remove") {
        close(path);
      }
    };

    const onChange = function (path) {
      to({ path });
    };

    const i18nTitle = function (content) {
      return t(content);
    };

    return {
      list,
      active,
      tabType,
      close,
      closeAll,
      closeOther,
      closeCurrent,
      onEdit,
      onChange,
      i18nTitle,
    };
  },
};
</script>

<style lang="less">
.pear-tab-1,
.pear-tab-2,
.pear-tab-3 {
  height: 44px;
  .ant-tabs-nav {
    height: 44px;
  }
  .tab-tool {
    margin-left: 6px;
    margin-right: 6px;
  }
}

.pear-tab-1 .ant-tabs-tab {
  height: 34px;
  margin: 5px 0px 0px 5px !important;
  background: #fff !important;
  border-radius: 3px !important;
  color: #808695 !important;
  .tab-dot {
    width: 8px;
    height: 8px;
    background: #f5f5f5;
    border-radius: 8px;
    display: inline-block;
    position: relative;
    margin-right: 5px;
    top: -1px;
  }
  &.ant-tabs-tab-active {
    .tab-dot {
      background: @primary-color;
    }
    .ant-tabs-tab-btn {
      color: #808695 !important;
    }
    border-bottom: none;
  }
}

.pear-tab-2 .ant-tabs {
  border-top: white solid 1px !important;
}

.pear-tab-2 .ant-tabs-nav {
  background: white;
  border-bottom: 1px solid whitesmoke;
  border-top: 1px solid whitesmoke;
}

.pear-tab-2 .ant-tabs-tab {
  height: 40px !important;
  background: #fff !important;
  border-radius: 0px !important;
  color: #808695 !important;
  border: none !important;
  border-right: 1px solid whitesmoke !important;
  .tab-dot {
    width: 8px;
    height: 8px;
    background: #f5f5f5;
    border-radius: 8px;
    display: inline-block;
    position: relative;
    margin-right: 5px;
    top: -1px;
  }
  &.ant-tabs-tab-active {
    .tab-dot {
      background: @primary-color;
    }
    .ant-tabs-tab-btn {
      color: #808695 !important;
    }
    border-bottom: none;
  }
}
</style>