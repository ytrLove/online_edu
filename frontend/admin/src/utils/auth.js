import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'
const UserInfoKey = 'Admin-UserInfo'

// Token管理
export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

// 用户信息管理
export function getUserInfo() {
  const userInfo = localStorage.getItem(UserInfoKey)
  return userInfo ? JSON.parse(userInfo) : null
}

export function setUserInfo(userInfo) {
  if (userInfo) {
    localStorage.setItem(UserInfoKey, JSON.stringify(userInfo))
  } else {
    localStorage.removeItem(UserInfoKey)
  }
}

// 权限检查
export function hasPermission(permission) {
  const userInfo = getUserInfo()
  if (!userInfo || !userInfo.permissions) {
    return false
  }
  
  // admin角色拥有所有权限
  if (userInfo.roles && (userInfo.roles.includes('admin') || userInfo.permissions.includes('*:*:*'))) {
    return true
  }
  
  return userInfo.permissions.includes(permission)
}

// 判断是否拥有角色
export function hasRole(role) {
  const userInfo = getUserInfo()
  if (!userInfo || !userInfo.roles) {
    return false
  }
  return userInfo.roles.includes(role)
}

// 清除用户信息
export function clearUserInfo() {
  removeToken()
  localStorage.removeItem(UserInfoKey)
}

// 判断是否已登录
export function isAuthenticated() {
  return !!getToken() && !!getUserInfo()
} 