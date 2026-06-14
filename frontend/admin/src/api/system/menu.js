import request from '@/utils/request'

/**
 * 获取菜单列表
 * @param {Object} query 查询参数
 * @returns {Promise} 返回Promise对象
 */
export function getMenuList(query) {
  // 确保status是数字类型
  if (query && query.status !== undefined && query.status !== null && query.status !== '') {
    query.status = parseInt(query.status, 10);
  }
  return request({
    url: '/api/system/menu/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取菜单详情
 * @param {String} menuId 菜单ID
 * @returns {Promise} 返回Promise对象
 */
export function getMenu(menuId) {
  return request({
    url: `/api/system/menu/${menuId}`,
    method: 'get'
  })
}

/**
 * 获取菜单树形结构
 * @returns {Promise} 返回Promise对象
 */
export function getMenuTree() {
  return request({
    url: '/api/system/menu/tree',
    method: 'get'
  })
}

/**
 * 获取角色菜单树形结构
 * @param {String} roleId 角色ID
 * @returns {Promise} 返回Promise对象
 */
export function getRoleMenus(roleId) {
  return request({
    url: `/api/system/menu/roleMenuTreeSelect/${roleId}`,
    method: 'get'
  })
}

/**
 * 获取角色菜单ID列表
 * @param {String} roleId 角色ID
 * @returns {Promise} 返回Promise对象
 */
export function getRoleMenuIds(roleId) {
  return request({
    url: `/api/system/menu/role/${roleId}`,
    method: 'get'
  }).then(response => {
    // 确保返回数组格式
    return Array.isArray(response) ? response : []
  })
}

/**
 * 更新角色菜单
 * @param {Object} data 角色菜单数据
 * @returns {Promise} 返回Promise对象
 */
export function updateRoleMenu(data) {
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
    data: data
  })
}

/**
 * 新增菜单
 * @param {Object} data 菜单数据
 * @returns {Promise} 返回Promise对象
 */
export function addMenu(data) {
  // 数据类型转换
  prepareMenuData(data);
  return request({
    url: '/api/system/menu',
    method: 'post',
    data
  })
}

/**
 * 修改菜单
 * @param {Object} data 菜单数据
 * @returns {Promise} 返回Promise对象
 */
export function updateMenu(data) {
  // 数据类型转换
  prepareMenuData(data);
  return request({
    url: '/api/system/menu',
    method: 'put',
    data
  })
}

/**
 * 准备菜单数据，确保数据类型正确
 * @param {Object} data 菜单数据
 */
function prepareMenuData(data) {
  // 确保menuId是数字类型
  if (data.menuId !== undefined && typeof data.menuId === 'string') {
    data.menuId = parseInt(data.menuId, 10);
  }
  // 确保parentId是数字类型
  if (data.parentId !== undefined && typeof data.parentId === 'string') {
    data.parentId = parseInt(data.parentId, 10);
  }
  // 确保orderNum是数字类型
  if (data.orderNum !== undefined && typeof data.orderNum === 'string') {
    data.orderNum = parseInt(data.orderNum, 10);
  }
  // 确保status是数字类型
  if (data.status !== undefined && typeof data.status === 'string') {
    data.status = parseInt(data.status, 10);
  }
}

/**
 * 删除菜单
 * @param {String} menuId 菜单ID
 * @returns {Promise} 返回Promise对象
 */
export function delMenu(menuId) {
  return request({
    url: `/api/system/menu/${menuId}`,
    method: 'delete'
  })
}

/**
 * 获取用户菜单
 * @returns {Promise} 返回Promise对象
 */
export function getUserMenu() {
  return request({
    url: '/api/system/menu/user',
    method: 'get'
  })
}

/**
 * 获取路由信息
 * @returns {Promise} 返回Promise对象
 */
export function getRoutes() {
  return request({
    url: '/api/system/menu/routes',
    method: 'get'
  }).then(response => {
    return response;
  }).catch(error => {
    console.warn('获取路由信息失败:', error);
    return { code: 200, data: [] }; // 返回空数组作为默认值
  });
}

/**
 * 获取当前用户的菜单树结构
 * @returns {Promise} 返回Promise对象
 */
export function getUserMenuTree() {
  return request({
    url: '/api/system/menu/user/tree',
    method: 'get'
  }).then(response => {
    return response;
  }).catch(error => {
    console.warn('获取用户菜单树失败:', error);
    return { code: 200, data: [] }; // 返回空数组作为默认值
  });
} 