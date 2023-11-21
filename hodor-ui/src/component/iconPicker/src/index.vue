<template>
  <div class="p-icon-picker">
    <a-dropdown>
      <a-input v-model:value="selected" :name="name" readonly>
        <template #addonAfter>
          <p-icon :type="selected"></p-icon>
        </template>
      </a-input>
      <template #overlay>
        <a-menu class="p-icon-picker-content">
          <a-menu-item :key="icon" v-for="icon in icons" @click="select(icon)">
            <p-icon :type="icon"></p-icon>
          </a-menu-item>
        </a-menu>
      </template>
    </a-dropdown>
  </div>
</template>
<script>
import icons from "./index.js";
import { watch, ref } from "vue";

export default {
  name: "p-icon-picker",
  props: {
    modelValue: {
      type: String,
      require: false,
    },
    name: {
      type: String,
      require: false
    }
  },
  emits: ['update:modelValue'],
  setup(props, context) {
    const selected = ref(props.modelValue ? props.modelValue : icons[0]);

    const select = function (icon) {
      selected.value = icon;
      context.emit("update:modelValue", icon);
    };

    watch(props, (props) => {
      selected.value = props.modelValue;
    });

    return {
      icons,
      select,
      selected,
    };
  },
};
</script>

<style>
.p-icon-picker-content {
  height: 300px;
  overflow-y: scroll;
  width: 310px;
}

.p-icon-picker-content .ant-dropdown-menu-item {
  display: inline-block;
}

.p-icon-picker-content
  .ant-dropdown-menu-item
  .ant-dropdown-menu-title-content
  span {
  margin-right: 0px !important;
}
</style>