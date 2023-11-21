<!-- 调度器节点管理入口 -->
<script setup>
import Scheduler from './components/Scheduler.vue';
</script>

<template>
  <div>
    <page-header
      title="调度结点管理"
      describe="展示调度结点信息，管理所有调度结点，具有查看详情、监控信息等功能"
    />
    <page-layout>
      <Scheduler/>
    </page-layout>
    <page-footer/>
  </div>
</template>
