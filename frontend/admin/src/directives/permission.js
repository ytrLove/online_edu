import { useUserStore } from '@/stores/user'

/**
 * 权限指令
 * 用法：
 * 1. 验证单个权限: v-permission="'system:user:add'"
 * 2. 验证多个权限(或关系): v-permission="['system:user:add', 'system:user:edit']"
 * 3. 验证角色: v-permission="{role: 'admin'}"
 * 4. 验证角色或权限(或关系): v-permission="{role: 'admin', permission: 'system:user:add'}"
 */
export const permission = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const { value } = binding
    
    // 如果是admin角色，直接返回true，无需进一步检查
    if (userStore.roles.includes('admin') || userStore.permissions.includes('*:*:*')) {
      return true
    }
    
    if (value === undefined || value === null) {
      return true
    }
    
    // 单个权限验证
    if (typeof value === 'string') {
      const hasPermission = userStore.hasPermission(value)
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
      return
    }
    
    // 多个权限验证（或关系）
    if (Array.isArray(value)) {
      const hasPermission = value.some(p => userStore.hasPermission(p))
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
      return
    }
    
    // 角色和权限混合验证
    if (typeof value === 'object') {
      const { role, permission } = value
      let hasRole = true
      let hasPermission = true
      
      if (role) {
        hasRole = typeof role === 'string' 
          ? userStore.hasRole(role)
          : role.some(r => userStore.hasRole(r))
      }
      
      if (permission) {
        hasPermission = typeof permission === 'string'
          ? userStore.hasPermission(permission)
          : permission.some(p => userStore.hasPermission(p))
      }
      
      if (!(hasRole || hasPermission)) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}

export default {
  install(app) {
    app.directive('permission', permission)
  }
} 