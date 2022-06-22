<template>
  <div class="pagnation-container">
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[10, 20, 30, 50]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
    >
    </el-pagination>
  </div>
</template>

<script>
export default {
  name: "Search",
  props: {
    total: {
      type: Number,
      default: () => {
        return 0;
      }
    },
    currentPage: {
      type: Number,
      default: () => {
        return 0;
      }
    },
    pageSize: {
      type: Number,
      default: () => {
        return 0;
      }
    }
  },
  data() {
    return {
      searchVal: ""
    };
  },
  created() {},
  updated() {},
  mounted() {},
  components: {},
  methods: {
    handleCurrentChange(val) {
      this.$emit("update:currentPage", val);
      this.$emit("init");
    },
    handleSizeChange(val) {
      this.$emit("update:pageSize", val);
      this.$emit("update:currentPage", 1);
      this.$emit("init");
    }
  },
  destroyed() {}
};
</script>
<style>
</style>
