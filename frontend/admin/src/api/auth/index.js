import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data 登录信息
 * @returns {Promise} 返回Promise对象
 */
export function login(data) {
    return request({
        url: '/api/auth/login',
        method: 'post',
        data
    })
}

/**
 * 用户登出
 * @returns {Promise} 返回Promise对象
 */
export function logout() {
    return request({
        url: '/api/auth/logout',
        method: 'post'
    })
}

/**
 * 修改密码
 * @param {Object} data 密码信息
 * @returns {Promise} 返回Promise对象
 */
export function changePassword(data) {
    return request({
        url: '/api/auth/changePassword',
        method: 'put',
        data
    })
}

/**
 * 重置密码
 * @param {Object} data 密码信息
 * @returns {Promise} 返回Promise对象
 */
export function resetUserPassword(data) {
    return request({
        url: '/api/system/user/password/reset',
        method: 'put',
        data
    })
} 

// 获取路由
export function getRoutes() {
    return request({
        url: '/api/auth/getRouters',
        method: 'get'
    })
}

// 用户信息
export function getUserInfo() {
    return request({
        url: '/api/auth/userInfo',
        method: 'get'
    })
}

/**
 * 更新用户个人信息
 * @param {Object} data 用户信息
 * @returns {Promise} 返回Promise对象
 */
export function updateUserInfo(data) {
    return request({
        url: '/api/auth/updateUser',
        method: 'put',
        data
    })
}