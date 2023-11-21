<template>
  <div id="403">
    <page-layout>
      <a-card>
        <a-result status="403" title="403" sub-title="您没有权限访问！">
          <template #extra>
            <div style="margin-bottom:20px">{{ overTime }} 秒后返回首页</div>
            <router-link to="/">
              <a-button type="primary"> 返回首页 </a-button>
            </router-link>
          </template>
        </a-result>
      </a-card>
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>

<script>
import { ref } from "vue";
import { isTimeout } from "@/tools/common";
import { useRouter } from 'vue-router';
export default {
  setup() {
    const router = useRouter();
    const overTime = ref(10);
     isTimeout(overTime,function(){
      router.push("/");
    })
    return { overTime };
  }
};
</script>
