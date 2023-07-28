import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { queryJobInfoListPagingAPI, createJobAPI,deleteGroupAPI,stopJobAPI,resumeJobAPI } from '@/apis/job/jobInfo'

export const useJobInfoStore = defineStore('jobInfo', () => {
    // state
    const jobInfoList = ref([]);
    const paginationOpt = reactive({
        defaultCurrent: 1, // 默认当前页数
        defaultPageSize: 5, // 默认当前页显示数据的大小
        total: 0, // 总数
        // 改变每页数量时更新显示
        onChange: (current, size) => {
            paginationOpt.defaultCurrent = current;
            paginationOpt.defaultPageSize = size;
            const { defaultCurrent, defaultPageSize } = paginationOpt
            getJobInfoList({pageNo:defaultCurrent,pageSize:defaultPageSize});
        },
    });
    // action
    const getJobInfoList = async ({ pageNo, pageSize }) => {
        const res = await queryJobInfoListPagingAPI({ pageNo, pageSize },{ cacheTime: 0 })
        jobInfoList.value = res.data.rows
        const { total } = res.data;
        Object.assign(paginationOpt, {
            defaultCurrent: pageNo,
            defaultPageSize: pageSize,
            total,
        })
    }
    const createJob = async (jobInfo) => {
        const res = await createJobAPI(jobInfo)
        console.log(res);
        if(res.status===200){
            jobInfoList.push(jobInfo);
        }
    }
    const deleteJob = async (id) => {
        const res =await deleteGroupAPI(id);
    }
    const stopJob = async (id) => {
        const res = await stopJobAPI(id)
        if(res.successful===true){
            let job=jobInfoList.value.find((jobInfo)=>jobInfo.id===id)
            job.jobStatus="STOP";
        }
    }
    const resumeJob = async (id) => {
        const res = await resumeJobAPI(id)
        if(res.successful===true){
            let job=jobInfoList.value.find((jobInfo)=>jobInfo.id===id)
            job.jobStatus="RUNNING";
        }
    }

    return {
        jobInfoList,
        paginationOpt,
        getJobInfoList,
        createJob,
        deleteJob,
        stopJob,
        resumeJob,
    }
})
