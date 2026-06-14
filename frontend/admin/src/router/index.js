import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
// 导入路由模块
import examRoutes from './modules/exam.js'
import statRoutes from './modules/stat.js'

// 标记路由是否已经生成
let isRouteGenerated = false

// 重置路由生成状态
export function resetRouteGeneratedState() {
  isRouteGenerated = false
}

// 讨论区路由
export const discussionRoutes = {
  path: '/discussion',
  component: () => import('@/layout/index.vue'),
  hidden: true,
  children: [
    {
      path: 'detail/:id',
      name: 'DiscussionDetail',
      component: () => import('@/views/discussion/detail.vue'),
      meta: { title: '讨论区详情', icon: 'message' }
    }
  ]
};


// 常量路由 - 不需要权限即可访问的路由
export const constantRoutes = [
  discussionRoutes,
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/403',
    name: 'noPermission',
    component: () => import('@/views/error/403.vue'),
    meta: { title: '无权限', hidden: true }
  },
  {
    path: '/404',
    name: 'notFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在', hidden: true }
  }
]

// 导入个人信息页面
const ProfileView = () => import('@/views/profile/index.vue')

// 基础布局路由 - 所有需要菜单的路由的父路由
export const layoutRoute = {
  path: '/',
  name: 'Layout',
  component: () => import('@/layout/index.vue'),
  redirect: '/dashboard',
  children: [
    {
      path: 'dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index.vue'),
      meta: { title: '首页', icon: 'HomeFilled', affix: true }
    },{
      path: 'profile',
      name: 'Profile',
      component: ProfileView,
      meta: { title: '个人信息', icon: 'User' }
    },
    {
      path: "test",
      name: 'Test',
      component: () => import('@/views/test/index.vue'),
    }
    // 个人信息页面已作为独立路由添加
  ]
}

// 异步路由 - 需要根据用户权限动态加载的路由
export const asyncRoutes = [
  examRoutes,
  statRoutes
  // 其他异步路由
]

// 初始化路由
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...constantRoutes, layoutRoute]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  console.log('导航到:', to.path);

  // 登录页和错误页不需要检查权限
  if (constantRoutes.some(route => route.path === to.path)) {
    next()
    return
  }
  
  // 检查是否已登录
  const userStore = useUserStore()
  if (!userStore.token) {
    next('/login')
    return
  }
  
  // 如果没有用户信息，尝试获取并生成路由
  if (Object.keys(userStore.userInfo).length === 0 || !isRouteGenerated) {
    try {
      // 获取用户信息
      await userStore.getUserInfoAction()
      // 获取用户菜单
      await userStore.getUserMenusAction()
      
      // 根据用户权限过滤路由
      const accessRoutes = await userStore.generateRoutes()
      
      // 清除所有动态路由(保留constantRoutes和layoutRoute)
      const savedRoutes = [...constantRoutes]
      
      router.getRoutes().forEach(route => {
        // 不清除常量路由
        if (!savedRoutes.some(r => r.path === route.path)) {
          if (route.name) {
            router.removeRoute(route.name)
          }
        }
      })
      
      // 动态添加路由
      accessRoutes.forEach(route => {
        router.addRoute(route)
      })
      
      // 延迟添加404兜底路由，确保所有有效路由先添加完成
      router.addRoute({
        path: '/:pathMatch(.*)*',
        redirect: '/404'
      })
      
      console.log('所有路由:', router.getRoutes().map(r => ({path: r.path, name: r.name})));
      
      // 标记路由已生成
      isRouteGenerated = true
      
      // 重新访问当前路由，确保动态添加的路由生效
      // replace: true 会重写history记录
      next({ ...to, replace: true })
      return
    } catch (error) {
      console.error('获取用户信息失败:', error)
      await userStore.logoutAction()
      next('/login')
      return
    }
  }
  
  // 检查路由是否存在
  if (to.matched.length === 0) {
    console.warn(`路由未匹配: ${to.path}, 重定向到404`);
    next('/404')
    return
  }
  
  // 检查是否有权限访问该路由
  if (to.meta && to.meta.permissions && to.meta.permissions.length > 0) {
    const hasPermission = to.meta.permissions.some(permission => 
      userStore.hasPermission(permission)
    )
    
    if (!hasPermission) {
      ElMessage.error('您没有权限访问该页面')
      next('/403')
      return
    }
  }
  
  next()
})

export default router

