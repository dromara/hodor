import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { queryJobInfoListPagingAPI, createJobAPI, deleteGroupAPI, stopJobAPI, resumeJobAPI,updateJobAPI,executeJobAPI } from '@/apis/job/jobInfo'
import { message } from 'ant-design-vue';

export const useJobInfoStore = defineStore('jobInfo', () => {
    // state
    const jobInfoList = ref([]);
    const paginationOpt = reactive({
        defaultCurrent: 1, // 默认当前页数
        defaultPageSize: 10, // 默认当前页显示数据的大小
        total: 0, // 总数
        // 改变每页数量时更新显示
        onChange: (current, size) => {
            paginationOpt.defaultCurrent = current;
            paginationOpt.defaultPageSize = size;
            const { defaultCurrent, defaultPageSize } = paginationOpt
            getJobInfoList({ pageNo: defaultCurrent, pageSize: defaultPageSize });
        },
    });
    const totalPageOpt=ref();
    // action
    const getJobInfoList = async ({ pageNo, pageSize }, jobInfo) => {
        // 动态添加jobInfo参数
        let queryString = "";
        if (jobInfo) {
            const entries = Object.entries(jobInfo);
            const filteredEntries = entries.filter(([key, value]) => value !== ""&&value!==undefined);
            if (filteredEntries.length > 0) {
                queryString = '&' + filteredEntries.map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
                    .join('&');
            }
        }
        const res = await queryJobInfoListPagingAPI({ pageNo, pageSize }, queryString)
        jobInfoList.value = res.data.rows
        const { total,totalPage } = res.data;
        Object.assign(paginationOpt, {
            defaultCurrent: pageNo,
            defaultPageSize: pageSize,
            total,
        });
        totalPageOpt.value=totalPage
    }
    const createJob = async (jobInfo) => {
        const res = await createJobAPI(jobInfo)
        const newJobInfo=res.data;
        if (res.successful === true) {
            const{defaultCurrent}=paginationOpt;
            // 如果当前显示的是最后一页则重新分页查询
            if(defaultCurrent===totalPageOpt.value){
                const {defaultCurrent,defaultPageSize}=paginationOpt;
                getJobInfoList({ pageNo:defaultCurrent, pageSize:defaultPageSize });
            }
        }
        else{
            message.error(res.msg);
        }
    }
    const deleteJob = async (id) => {
        const res = await deleteGroupAPI(id);
        if(res.successful!==true){
            message.error(res.msg);
        }
    }
    const stopJob = async (id) => {
        const res = await stopJobAPI(id)
        if (res.successful === true) {
            let job = jobInfoList.value.find((jobInfo) => jobInfo.id === id)
            job.jobStatus = "STOP";
        }
        else{
            message.error(res.msg);
        }
    }
    const resumeJob = async (id) => {
        const res = await resumeJobAPI(id)
        if (res.successful === true) {
            let job = jobInfoList.value.find((jobInfo) => jobInfo.id === id)
            job.jobStatus = "RUNNING";
        }
        else{
            message.error(res.msg);
        }
    }
    const updateJob=async (jobInfo)=>{
        const res=await updateJobAPI(jobInfo);
        if(res.successful===true){
            // let job = jobInfoList.value.find((job) => job.id === id)
            // Object.assign(job,jobInfo);
            const { defaultCurrent, defaultPageSize } = paginationOpt
            getJobInfoList({ pageNo: defaultCurrent, pageSize: defaultPageSize });
        }
        else{
            message.error(res.msg);
        }
    }
    const executeJob=async (jobId)=>{
        const res=await executeJobAPI(jobId);
        console.log("execute",res);
    }

    return {
        jobInfoList,
        paginationOpt,
        getJobInfoList,
        createJob,
        deleteJob,
        stopJob,
        resumeJob,
        updateJob,
        executeJob,
    }
})
