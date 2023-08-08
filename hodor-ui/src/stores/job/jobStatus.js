import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import { queryJobStatusListPagingAPI } from '@/apis/job/jobStatus'
import { message } from 'ant-design-vue';
import router from '@/router/router';

export const useJobStatusStore = defineStore('jobStatus', () => {
    const jobStatusList=ref([]);
    const queryParams=reactive({
        groupName:'',
        jobName:'',
    });
    const paginationOpt = reactive({
        defaultCurrent: 1, // 默认当前页数
        defaultPageSize: 50, // 默认当前页显示数据的大小
        total: 0, // 总数
        // 改变每页数量时更新显示
        onChange: (current, size) => {
            paginationOpt.defaultCurrent = current;
            paginationOpt.defaultPageSize = size;
            const { defaultCurrent, defaultPageSize } = paginationOpt
            getJobStatusList({ pageNo: defaultCurrent, pageSize: defaultPageSize },queryParams);
        },
    });
    const getJobStatusList=async ({ pageNo, pageSize }, jobStatus)=>{
        let queryString = "";
        if (jobStatus) {
            const entries = Object.entries(jobStatus);
            const filteredEntries = entries.filter(([key, value]) => value !== ""&&value!==undefined);
            if (filteredEntries.length > 0) {
                queryString = '&' + filteredEntries.map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
                    .join('&');
            }
        }
        const res=await queryJobStatusListPagingAPI({ pageNo, pageSize }, queryString)
        jobStatusList.value = res.data.rows
        const { total } = res.data;
        Object.assign(paginationOpt, {
            defaultCurrent: pageNo,
            defaultPageSize: pageSize,
            total,
        });
    }
    const getQueryParams=(params)=>{
        Object.assign(queryParams,params);
    }

    return {
        jobStatusList,
        paginationOpt,
        getJobStatusList,
        getQueryParams,
    }
})
