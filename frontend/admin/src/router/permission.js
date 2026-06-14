import { getToken } from '@/utils/auth'
import { getRoutes } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// NProgress配置
NProgress.configure({ showSpinner: false })

// 不需要重定向的白名单
const whiteList = ['/login', '/register', '/forget-password']

/**
 * 设置路由守卫
 * @param {Router} router - Vue Router实例
 */
export function setupRouterGuard(router) {
  router.beforeEach(async (to, from, next) => {
    NProgress.start()
    
    // 获取store
    const userStore = useUserStore()
    const hasToken = getToken()
    
    if (hasToken) {
      if (to.path === '/login') {
        // 已登录，重定向到首页
        next({ path: '/' })
        NProgress.done()
      } else {
        // 检查用户是否已获取用户信息
        const hasUserInfo = Object.keys(userStore.userInfo).length > 0
        if (hasUserInfo) {
          next()
        } else {
          try {
            // 获取用户信息
            await userStore.getUserInfoAction()
            // 获取用户菜单
            await userStore.getUserMenusAction()
            
            // 确认菜单已获取
            if (userStore.menus.length === 0) {
              // 没有菜单权限，退出登录
              await userStore.logoutAction()
              ElMessage.error('您没有访问权限，请联系管理员')
              next('/login')
            } else {
              // 有菜单权限，继续访问
              next({ ...to, replace: true })
            }
          } catch (error) {
            // 出错，清空令牌并重定向到登录页
            await userStore.logoutAction()
            ElMessage.error(error.message || '验证失败，请重新登录')
            next('/login')
            NProgress.done()
          }
        }
      }
    } else {
      if (whiteList.includes(to.path)) {
        // 白名单路由，直接访问
        next()
      } else {
        // 未登录，重定向到登录页
        next(`/login?redirect=${to.path}`)
        NProgress.done()
      }
    }
  })
  
  router.afterEach(() => {
    NProgress.done()
  })
} 