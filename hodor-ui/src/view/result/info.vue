<template>
  <div id="failure">
    <page-layout>
      <a-card>
        <a-result title="详情">
          <template #extra>
            <a-button key="console" type="primary"> 查看详情 </a-button>
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