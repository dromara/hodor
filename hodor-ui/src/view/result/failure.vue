<template>
  <div id="failure">
    <page-layout>
      <a-card>
        <a-result
          status="error"
          title="提交失败"
          sub-title="Please check and modify the following information before resubmitting."
        >
          <template #extra>
            <a-button key="console" type="primary"> 前往控制台 </a-button>
            <a-button key="buy"> 再次购买 </a-button>
          </template>

          <div class="desc">
            <p style="font-size: 16px">
              <strong
                >The content you submitted has the following error:</strong
              >
            </p>
            <p>
              <close-circle-outlined :style="{ color: 'red' }" /> Your account
              has been frozen
              <a>Thaw immediately &gt;</a>
            </p>
            <p>
              <close-circle-outlined :style="{ color: 'red' }" /> Your account
              is not yet eligible to apply <a>Apply Unlock &gt;</a>
            </p>
          </div>
        </a-result>
      </a-card>
    </page-layout>
    <page-footer></page-footer>
  </div>
</template>