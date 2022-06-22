<template>
  <div class='breadcrumb_container'>
    <el-row>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item
          v-for='(item,index) in breadcrumb.breadcrumbItem'
          :key='index'
          :to="{ path: item.path }"
        >{{item.title}}</el-breadcrumb-item>
        <el-breadcrumb-item ><span v-if='groupName'>{{groupName}}-{{title}}</span> <span v-else>{{title}}</span></el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>

  </div>

</template>
<script>
export default {
  name: "Breadcrumb",
  created() {},
  data() {
    return {};
  },
  props: {
    breadcrumb: {
      type: Object,
      default: () => {
        return {};
      }
    },
    title: {
      type: String,
      default: () => {
        return "";
      }
    },
    groupName: {
      type: String,
      default: () => {
        return "";
      }
    }
  },
  components: {},
  methods: {},
  destroyed() {}
};
</script>
<style>
</style>

