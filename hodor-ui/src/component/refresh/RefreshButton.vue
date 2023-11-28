<script setup>
import { SyncOutlined } from '@ant-design/icons-vue';
const props = defineProps(['onClick'])
</script>

<template>
    <a-tooltip title="刷新">
        <a-button shape="circle" @click="props.onClick">
            <SyncOutlined />
        </a-button>
    </a-tooltip>
</template>