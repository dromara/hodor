import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { queryGroupListPagingAPI, getBindListAPI } from '@/apis/job/jobGroup'

export const useJobGroupStore = defineStore('jobGroup', () => {
    const allGroupList=ref([]);
    const bindingList = ref({});
    const paginationOpt=reactive({
        pageNo:1,
        pageSize:10,
        total:0,
    })
    const getGroupListPaging=async()=>{
        const res=await queryGroupListPagingAPI({pageNo:1,pageSize:10});
        const {total}=res.data;
        Object.assign(paginationOpt,{
            ...paginationOpt,
            total,
        })
    }
    const getAllGroupList=async()=>{
        await getGroupListPaging();
        const{total}=paginationOpt;
        const res=await queryGroupListPagingAPI({pageNo:1,pageSize:total});
        allGroupList.value=res.data.rows;
    }

    const getBindingList = async () => {
        const res = await getBindListAPI()
        for (let bind of res.data) {
            bindingList.value[bind['groupName']] = bind['clusterName']
        }
    }

    return {
        allGroupList,
        bindingList,
        getAllGroupList,
        getBindingList,
    }
})
