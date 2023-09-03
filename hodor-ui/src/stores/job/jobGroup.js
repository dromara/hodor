import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { queryGroupListPagingAPI } from '@/apis/job/jobGroup'

export const useJobGroupStore = defineStore('jobGroup', () => {
    const allGroupList=ref([]);
    const groupList=ref([]);
    const paginationOpt=reactive({
        defaultCurrent:1,
        defaultPageSize:10,
        total:0,
        onChange: (current, size) => {
            paginationOpt.defaultCurrent = current;
            paginationOpt.defaultPageSize = size;
            getGroupListPaging();
        },
    })

    const getGroupListPaging=async()=>{
        const {defaultCurrent,defaultPageSize}=paginationOpt;
        const res=await queryGroupListPagingAPI({pageNo:defaultCurrent,pageSize:defaultPageSize});
        groupList.value=res.data.rows;
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
    // 删除分组
    const deleteGroup=async (id)=>{
        await handleDeleteGroupInfo(id).then(res=>{
            groupList.value=groupList.value.filter(item=>item.id!==id)
        });
    }

    return {
        allGroupList,
        groupList,
        paginationOpt,
        getGroupListPaging,
        getAllGroupList,
        deleteGroup,
    }
})
