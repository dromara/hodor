<template>
    <svg :class="svgClass" aria-hidden="true">
        <use :xlink:href="iconClassName" :fill="color" />
    </svg>
</template>


<script>
import { computed } from 'vue';
import { createFromIconfontCN } from '@ant-design/icons-vue';
import * as iconfontJS from '@/assets/iconfont/iconfont.js'

const MyIcon = createFromIconfontCN({
  scriptUrl: iconfontJS, // 替换为您的图标字体文件链接
});

export default {
name: 'SvgIcon',
props: {
    iconClass: {
    type: String,
    required: true
    },
    className: {
    type: String,
    default: ''
    }
},
computed: {
    iconName() {
    return `#icon-${this.iconClass}`
    },
    svgClass() {
    if (this.className) {
        return 'svg-icon ' + this.className
    } else {
        return 'svg-icon'
    }
    }
  }
}
</script>


<style scoped>
.svg-icon {
    width: 1em;
    height: 1em;
    vertical-align: -0.15em;
    fill: currentColor;
    overflow: hidden;
}
</style>
