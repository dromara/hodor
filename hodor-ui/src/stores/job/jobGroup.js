import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { queryGroupListPagingAPI } from '@/apis/job/jobGroup'

export const useJobGroupStore = defineStore('jobGroup', () => {
    const allGroupList=ref([]);
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

    return {
        allGroupList,
        getAllGroupList,
    }
})
