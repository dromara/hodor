<!-- 任务状态 -->
<script setup>
    import JobStatus from './components/JobStatus.vue';
</script>

<template>
  <div>
    <page-header
      title="任务执行状态管理"
      describe="展示任务执行状态信息，管理所有任务执行状态，具有查看任务状态的执行日志，杀死正在执行的任务等功能"
    />
    <page-layout>
      <JobStatus />
    </page-layout>
    <page-footer />
  </div>
</template>
