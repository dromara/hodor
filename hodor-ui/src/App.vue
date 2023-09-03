<script setup>
import { RouterView,useRouter } from 'vue-router';

const router=useRouter();
// 刷新后跳转到首页
if(router.currentRoute.value.path!=='/home'){
  router.replace('/home')
}
</script>

<template>
  <!-- 一级路由 -->
  <RouterView />
</template>
