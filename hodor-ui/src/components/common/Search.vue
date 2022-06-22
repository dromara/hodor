<template>
  <div
    class="search-container"
    @keyup.enter="onSearch"
  >
    <el-autocomplete
      v-if='autocomplete'
      class="inline-input autocompleteInput"
      v-model="searchVal"
      custom-item="my-remote"
      :fetch-suggestions="querySearch"
      placeholder="请输入内容"
      @select="handleSelect"
    >
      <template slot-scope="{ item }">
        <div class="name">{{ item.groupName }}</div>
      </template>
      <el-button
        slot="append"
        icon="el-icon-search"
        @click="onSearch"
        type="primary"
      >搜索</el-button>
    </el-autocomplete>

    <el-input
      v-else
      placeholder="请输入内容"
      v-model="searchVal"
      class="input-with-select"
      size="small"
    >
      <el-button
        slot="append"
        icon="el-icon-search"
        @click="onSearch"
        type="primary"
      >搜索</el-button>
    </el-input>

  </div>
</template>

<script>
import { session } from "@/assets/util";
export default {
  name: "Search",
  props: {
    autocomplete: {
      type: Boolean,
      default: () => {
        return false;
      }
    },
    list: {
      type: Array,
      default: () => {
        return [];
      }
    },
    searchval:{
      type:String,
      default:()=>{
        return ''
      }
    }
  },
  data() {
    return {
      searchVal:this.searchval?this.searchval:''
    };
  },
  created() {},
  updated() {},
  mounted() {},
  components: {},
  methods: {
    onSearch() {
      this.$emit("search", this.searchVal);
    },
    handleSelect(item) {
      this.searchVal = item.groupName;
      this.onSearch();
    },
    querySearch(queryString, cb) {
      var list = this.list;
      var results = queryString
        ? list.filter(this.createFilter(queryString))
        : list;
      // 调用 callback 返回建议列表的数据
      cb(results);
    },
    createFilter(queryString) {
      return item => {
        return (
          item.groupName.toLowerCase().indexOf(queryString.toLowerCase()) === 0
        );
      };
    }
  },
  destroyed() {}
};
</script>
<style>
.search-container .el-input-group__append {
  background: #42b983;
  color: white;
}
.search-container .autocompleteInput {
  width: 100%;
}
</style>
