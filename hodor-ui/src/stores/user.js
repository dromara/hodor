// 管理用户数据相关
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginAPI } from '@/apis/login'

export const useUserStore = defineStore('user', () => {
    // 定义管理用户数据的state
    const userInfo = ref({})
    // 定义获取接口数据的action函数
    const getUserInfo = async ({ username, password }) => {
      const res = await loginAPI({ username, password })
      // console.log(res)
      // userInfo.value={username,password}
      userInfo.value = res.data
    }
    // 清除用户数据
    const clearUserInfo = () => {
      userInfo.value = {}
    }
    return {
      userInfo,
      getUserInfo,
      clearUserInfo
    }
  },
  {
    persist: true,
  })
