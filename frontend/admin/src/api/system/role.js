import request from '@/utils/request'
import { getRoleMenus } from './menu'

// 重新导出getRoleMenus函数，让调用者可以直接从这个文件导入
export { getRoleMenus }

/**
 * 获取角色列表
 * @param {Object} params 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getRoleList(params) {
    return request({
        url: '/api/system/role/list',
        method: 'get',
        params
    })
}

/**
 * 获取角色详情
 * @param {String} roleId 角色ID
 * @returns {Promise} 返回Promise对象
 */
export function getRoleDetail(roleId) {
    return request({
        url: `/api/system/role/${roleId}`,
        method: 'get'
    })
}

/**
 * 添加角色
 * @param {Object} data 角色数据
 * @returns {Promise} 返回Promise对象
 */
export function addRole(data) {
    // 确保status是数字类型
    if (data.status !== undefined && typeof data.status === 'string') {
        data.status = parseInt(data.status, 10);
    }
    // 确保roleSort是数字类型
    if (data.roleSort !== undefined && typeof data.roleSort === 'string') {
        data.roleSort = parseInt(data.roleSort, 10);
    }
    return request({
        url: '/api/system/role',
        method: 'post',
        data
    })
}

/**
 * 更新角色
 * @param {Object} data 角色数据
 * @returns {Promise} 返回Promise对象
 */
export function updateRole(data) {
    // 确保status是数字类型
    if (data.status !== undefined && typeof data.status === 'string') {
        data.status = parseInt(data.status, 10);
    }
    // 确保roleSort是数字类型
    if (data.roleSort !== undefined && typeof data.roleSort === 'string') {
        data.roleSort = parseInt(data.roleSort, 10);
    }
    // 确保roleId是数字类型
    if (data.roleId !== undefined && typeof data.roleId === 'string') {
        data.roleId = parseInt(data.roleId, 10);
    }
    return request({
        url: '/api/system/role',
        method: 'put',
        data
    })
}

/**
 * 删除角色
 * @param {String} roleId 角色ID
 * @returns {Promise} 返回Promise对象
 */
export function delRole(roleId) {
    return request({
        url: `/api/system/role/${roleId}`,
        method: 'delete'
    })
}

/***
 * 分配角色菜单
 * @param {Object} data 包含roleId和menuIds的对象
 * @returns {Promise} 返回Promise对象
 */
export function assignMenus(data) {
    // 确保roleId是数字类型
    if (data.roleId !== undefined && typeof data.roleId === 'string') {
        data.roleId = parseInt(data.roleId, 10);
    }
    // 确保menuIds中的每个ID是数字类型
    if (data.menuIds && Array.isArray(data.menuIds)) {
        data.menuIds = data.menuIds.map(id => 
            typeof id === 'string' ? parseInt(id, 10) : id
        );
    }
    return request({
        url: '/api/system/menu/role',
        method: 'put',
        data
    })
}

/**
 * 获取所有可用角色列表（不分页）
 * @returns {Promise} 返回Promise对象
 */
export function getAllRoles() {
    return request({
        url: '/api/system/role/all',
        method: 'get'
    })
}