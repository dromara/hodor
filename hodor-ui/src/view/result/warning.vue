<template>
  <div id="success">
    <page-layout>
      <a-card>
        <a-result
          status="warning"
          title="警告"
          sub-title="Order number: 2017182818828182881 Cloud server configuration takes 1-5 minutes, please wait."
        >
          <div class="desc">
            <p style="font-size: 16px">
              <strong
                >The content you submitted has the following error:</strong
              >
            </p>
            <p>
              <CloseCircleOutlined :style="{ color: 'red' }" /> Your account has
              been frozen
              <a>Thaw immediately &gt;</a>
            </p>
            <p>
              <CloseCircleOutlined :style="{ color: 'red' }" /> Your account is
              not yet eligible to apply <a>Apply Unlock &gt;</a>
            </p>
          </div>
          <template #extra>
            <a-button key="console" type="primary"> 回到首页 </a-button>
          </template>
        </a-result>
      </a-card>
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>
<script>
import { CloseCircleOutlined } from "@ant-design/icons-vue";
export default {
  setup() {
    return {
      CloseCircleOutlined,
    };
  },
};
</script>
