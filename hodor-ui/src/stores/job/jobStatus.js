import {defineStore} from 'pinia'
import {reactive, ref} from 'vue'
import {killRunningJobAPI, queryExecuteLogAPI, queryJobStatusListPagingAPI} from '@/api/job/jobStatus'
import {message} from 'ant-design-vue';
import {timeTransfer} from '@/tools/timeUtil';

export const useJobStatusStore = defineStore('jobStatus', () => {
  const jobStatusList = ref([]);
  const queryParams = reactive({
    groupName: '',
    jobName: '',
  });
  const logOpt = reactive({
    logData: '',
    offset: 0,
    length: 1000,
  });
  const paginationOpt = reactive({
    defaultCurrent: 1, // 默认当前页数
    defaultPageSize: 10, // 默认当前页显示数据的大小
    total: 0, // 总数
    // 改变每页数量时更新显示
    onChange: (current, size) => {
      paginationOpt.defaultCurrent = current;
      paginationOpt.defaultPageSize = size;
      const {defaultCurrent, defaultPageSize} = paginationOpt
      getJobStatusList({pageNo: defaultCurrent, pageSize: defaultPageSize}, queryParams);
    },
  });
  const getJobStatusList = async ({pageNo, pageSize}, jobStatus) => {
    let queryString = "";
    if (jobStatus) {
      const entries = Object.entries(jobStatus);
      const filteredEntries = entries.filter(([key, value]) => value !== "" && value !== undefined);
      if (filteredEntries.length > 0) {
        queryString = '&' + filteredEntries.map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
          .join('&');
      }
    }
    const res = await queryJobStatusListPagingAPI({pageNo, pageSize}, queryString)
    jobStatusList.value = res.data.rows
    jobStatusList.value = jobStatusList.value.map(item => {
      return {
        ...item,
        scheduleStart: timeTransfer(item.scheduleStart),
        scheduleEnd: timeTransfer(item.scheduleEnd),
        executeStart: timeTransfer(item.executeStart),
        executeEnd: timeTransfer(item.executeEnd),
        isTimeout: item.isTimeout.toString(),
      }
    })
    const {total} = res.data;
    Object.assign(paginationOpt, {
      defaultCurrent: pageNo,
      defaultPageSize: pageSize,
      total,
    });
  }
  const killRunningJob = async (params) => {
    const res = await killRunningJobAPI(params);
    // console.log("killRunningJob",res);
    if (res && res.successful === true) {
      message.success("成功杀死正在执行的任务")
    } else {
      message.error("杀死任务失败")
    }
  }
  const getExecuteLog = async (params) => {
    let queryString = "";
    if (params) {
      const entries = Object.entries(params);
      const filteredEntries = entries.filter(([key, value]) => value !== "" && value !== undefined);
      if (filteredEntries.length > 0) {
        queryString = '&' + filteredEntries.map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
          .join('&');
      }
    }
    const res = await queryExecuteLogAPI(queryString);
    const {offset, length, logData} = res.data;
    if (res.successful === true) {
      // console.log("logOpt",res)
      logOpt.logData = logData;
      logOpt.offset += length;
      // logOpt.length=res.data.offset+res.data.length;
    } else {
      message.error("获取执行日志失败")
    }
  }

  const getQueryParams = (params) => {
    // 判断query参数是否为空
    if (JSON.stringify(params) === '{}') {
      queryParams.groupName = '';
      queryParams.jobName = '';
    } else {
      Object.assign(queryParams, params);
    }
  }

  return {
    jobStatusList,
    logOpt,
    paginationOpt,
    getJobStatusList,
    getQueryParams,
    killRunningJob,
    getExecuteLog,
  }
})
