import request from './request'

// 用户登录
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/api/auth/userInfo',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/api/auth/updateUser',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/api/auth/changePassword',
    method: 'put',
    data
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
} 