<!-- 执行节点管理入口 -->
<script setup>
    import Actuator from './components/Actuator.vue';
</script>

<template>
  <div>
    <page-header
      title="执行节点管理"
      describe="展示执行节点信息，管理所有执行节点，具有查看详情、监控信息等功能"
    ></page-header>
    <page-layout>
      <Actuator />
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>
