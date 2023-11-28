<!-- 任务组管理 -->
<script setup>
    import JobGroup from './components/JobGroup.vue';
</script>

<template>
  <div>
    <page-header
      title="任务组管理"
      describe="展示任务组信息，管理所有任务组，具有新增、编辑、删除、搜索等功能"
    />
    <page-layout>
      <JobGroup />
    </page-layout>
    <page-footer />
  </div>
</template>
