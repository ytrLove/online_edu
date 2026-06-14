import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getUserInfo } from '@/api/auth'
import { getToken, setToken, removeToken, setUserInfo, getUserInfo as getStoredUserInfo, clearLoginInfo } from '@/utils/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userId = ref('')
  const username = ref('')
  const nickname = ref('')
  const email = ref('')
  const phone = ref('')
  const avatar = ref('')
  const roles = ref([])
  const permissions = ref([])
  
  // 用户登录
  async function userLogin(userInfo) {
    try {
      const { username: user, password } = userInfo
      const response = await login({ username: user.trim(), password })
      
      const { data } = response
      setToken(data.token)
      token.value = data.token
      
      // 保存基本用户信息
      setUserInfo(data.user)
      
      return data
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  // 获取用户信息
  async function getInfo() {
    try {
      // 如果已经有存储的用户信息，先使用它
      const storedUser = getStoredUserInfo()
      if (storedUser) {
        setUserState(storedUser)
      }
      
      // 然后再请求最新信息
      const { data } = await getUserInfo()
      
      if (!data) {
        return Promise.reject('验证失败，请重新登录')
      }
      
      // 设置用户状态
      setUserState(data.user)
      
      // 设置角色和权限
      roles.value = data.roles || []
      permissions.value = data.permissions || []
      
      return data
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  // 设置用户状态
  function setUserState(user) {
    if (user) {
      userId.value = user.userId
      username.value = user.username
      nickname.value = user.nickname
      email.value = user.email
      phone.value = user.phone
      avatar.value = user.avatar || ''
    }
  }
  
  // 用户登出
  async function userLogout() {
    try {
      await logout()
      resetState()
      router.push('/login')
    } catch (error) {
      console.log(error)
    }
  }
  
  // 重置状态
  function resetState() {
    clearLoginInfo()
    token.value = ''
    userId.value = ''
    username.value = ''
    nickname.value = ''
    email.value = ''
    phone.value = ''
    avatar.value = ''
    roles.value = []
    permissions.value = []
  }
  
  return {
    token,
    userId,
    username,
    nickname,
    email,
    phone,
    avatar,
    roles,
    permissions,
    userLogin,
    getInfo,
    userLogout,
    resetState
  }
})