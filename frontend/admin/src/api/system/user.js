import request from '@/utils/request'

/**
 * 获取用户列表
 * @param {Object} params - 符合UserQueryRequest格式的查询参数
 * @returns {Promise} 返回UserListResponse格式的响应
 */
export function getUserList(params) {
    return request({
        url: '/api/system/user/list',
        method: 'get',
        params
    })
}

/**
 * 获取用户详情
 * @param {String} userId 用户ID
 * @returns {Promise} 返回UserInfoResponse格式的响应
 */
export function getUserDetail(userId) {
    return request({
        url: `/api/system/user/${userId}`,
        method: 'get'
    })
}

/**
 * 获取用户角色列表
 * @param {String} userId 用户ID
 * @returns {Promise} 返回角色列表
 */
export function getUserRoles(userId) {
    return request({
        url: `/api/system/user/${userId}/roles`,
        method: 'get'
    })
}

/**
 * 更新用户角色
 * @param {String} userId 用户ID
 * @param {Array} roleIds 角色ID数组
 * @returns {Promise} 返回Promise对象
 */
export function updateUserRoles(userId, roleIds) {
    return request({
        url: `/api/system/user/${userId}/roles`,
        method: 'put',
        data: { roleIds }
    })
}

/**
 * 添加用户
 * @param {Object} data - 符合UserCreateRequest格式的用户数据
 * @returns {Promise} 返回创建结果
 */
export function addUser(data) {
    return request({
        url: '/api/system/user',
        method: 'post',
        data
    })
}

/**
 * 更新用户
 * @param {Object} data - 符合UserUpdateRequest格式的用户数据
 * @returns {Promise} 返回更新结果
 */
export function updateUser(data) {
    return request({
        url: '/api/system/user',
        method: 'put',
        data
    })
}

/**
 * 删除用户
 * @param {String} userId 用户ID
 * @returns {Promise} 返回删除结果
 */
export function deleteUser(userId) {
    return request({
        url: `/api/system/user/${userId}`,
        method: 'delete'
    })
}

/**
 * 分配用户角色
 * @param {String} userId 用户ID
 * @param {Array} roleIds 角色ID数组
 * @returns {Promise} 返回分配结果
 */
export function assignRoles(userId, roleIds) {
    return request({
        url: '/api/system/user/assignRoles',
        method: 'put',
        data: {
            userId,
            roleIds
        }
    })
}

/**
 * 重置用户密码
 * @param {Object} data - 符合ResetPasswordRequest格式的数据，包含userId和password
 * @returns {Promise} 返回重置结果
 */
export function resetPassword(data) {
    return request({
        url: '/api/system/user/resetPwd',
        method: 'put',
        data
    })
}

/**
 * 更新用户状态
 * @param {String} userId 用户ID
 * @param {Number} status 状态值 0:禁用, 1:启用
 * @returns {Promise} 返回更新结果
 */
export function updateUserStatus(userId, status) {
    // 创建一个符合UserUpdateRequest的对象
    const data = {
        userId,
        status
    };
    
    return request({
        url: '/api/system/user',
        method: 'put',
        data
    })
} 