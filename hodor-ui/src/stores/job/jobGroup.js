import {defineStore, storeToRefs} from 'pinia'
import {reactive, ref} from 'vue'
import {deleteGroupAPI, getBindListAPI, queryGroupListPagingAPI} from '@/api/job/jobGroup'

export const useJobGroupStore = defineStore('jobGroup', () => {
  const allGroupList = ref([]);
  const groupList = ref([]);
  const bindingList = ref({});
  const paginationOpt = reactive({
    defaultCurrent: 1,
    defaultPageSize: 10,
    total: 0,
    onChange: (current, size) => {
      paginationOpt.defaultCurrent = current;
      paginationOpt.defaultPageSize = size;
      getGroupListPaging();
    },
  })

  const getGroupListPaging = async () => {
    const {defaultCurrent, defaultPageSize} = paginationOpt;
    const res = await queryGroupListPagingAPI({pageNo: defaultCurrent, pageSize: defaultPageSize});
    groupList.value = res.data.rows;
    const {total} = res.data;
    Object.assign(paginationOpt, {
      ...paginationOpt,
      total,
    })
  }
  const getAllGroupList = async () => {
    await getGroupListPaging();
    const {total} = paginationOpt;
    const res = await queryGroupListPagingAPI({pageNo: 1, pageSize: total});
    allGroupList.value = res.data.rows;
  }
  // 删除分组
  const deleteGroup = async (id) => {
    return handleDeleteGroupInfo(id)
  }

  const getBindingList = async () => {
    const res = await getBindListAPI()
    for (let bind of res.data) {
      bindingList.value[bind['groupName']] = bind['clusterName']
    }
  }

  // 删除行
  const handleDeleteGroupInfo = async (id) => {
    return deleteGroupAPI(id)
  }

  return {
    allGroupList,
    groupList,
    paginationOpt,
    getGroupListPaging,
    getAllGroupList,
    deleteGroup,
    bindingList,
    getBindingList,
  }
})

export const jobGroupStoreRef = storeToRefs(useJobGroupStore())
