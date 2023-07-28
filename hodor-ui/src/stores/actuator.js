// 管理用户数据相关
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAllClustersAPI } from '@/apis/actuator'

export const useActuatorStore = defineStore('actuator', () => {
    // state
    const actuatorClusterList = ref([]);
    // action
    const getAllClusters = async () => {
        const res = await getAllClustersAPI()
        actuatorClusterList.value = res.data
    }

    return {
        actuatorClusterList,
        getAllClusters,
    }
})
