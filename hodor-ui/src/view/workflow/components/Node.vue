<script setup>
import { inject, onMounted, reactive, ref } from 'vue'
const getNode = inject('getNode')
let info = reactive()

info = getNode()
console.log("info", info)
</script>

<template>
    <a-collapse v-model:activeKey="activeKey" :border="false" style="background: rgba(252, 250, 250, 0.89)">
        <a-collapse-panel key="1" header="This is panel header 1">
            <template #header>
                <p>{info.id}</p>
            </template>
            <a-list item-layout="horizontal" size="small">
                <!-- <template #renderItem="{ item }"> -->
                <a-list-item>
                    <a-descriptions-item label="UserName">Zhou Maomao</a-descriptions-item>
                </a-list-item>
                <a-list-item>
                    <a-descriptions-item label="Telephone">1810000000</a-descriptions-item>
                </a-list-item>
                <!-- </template> -->
            </a-list>
        </a-collapse-panel>
    </a-collapse>
</template>
