import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout } from '@/api/auth'
import { getUserInfo } from '@/api/auth/index'
import { getUserMenuTree, getRoutes } from '@/api/system/menu'
import { getToken, setToken, setUserInfo as saveUserInfo, clearUserInfo } from '@/utils/auth'
import router, { asyncRoutes, constantRoutes } from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref({})
  const roles = ref([])
  const permissions = ref([])
  const menus = ref([])
  const routes = ref([])
  const loading = ref(false)

  // 预加载所有视图组件
  const viewModules = import.meta.glob('@/views/**/*.vue')

  // 检查是否有权限
  const hasPermission = (permission) => {
    if (!permission) return true
    
    // admin角色拥有所有权限
    if (roles.value.includes('admin') || permissions.value.includes('*:*:*')) {
      return true
    }
    
    return permissions.value.includes(permission)
  }

  // 检查是否拥有角色
  const hasRole = (role) => {
    if (!role) return true
    return roles.value.includes(role)
  }

  // 登录
  const loginAction = async (loginData) => {
    try {
      loading.value = true
      const res = await login(loginData)
      
      // 适配后端返回的数据格式
      if (res.code === 200 && res.data) {
        const { token: newToken, user, permissions: userPermissions, roles: userRoles } = res.data
        
        // 设置token
      setToken(newToken)
      token.value = newToken

    // 设置用户信息
        userInfo.value = user || {}
        roles.value = userRoles || []
        permissions.value = userPermissions || []
        
        // 保存用户信息
        saveUserInfo({
          ...user,
          roles: userRoles,
          permissions: userPermissions
        })
        
      return true
      } else {
        console.error('登录响应格式异常:', res)
        return false
      }
    } catch (error) {
      console.error('登录失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  // 获取用户信息
  const getUserInfoAction = async () => {
    try {
      loading.value = true
      const res = await getUserInfo()
      console.log(res.data);
      const user = res.data
      userInfo.value = user
      roles.value = user.roles || []
      permissions.value = user.permissions || []
      saveUserInfo(user)
      return user
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return null
    } finally {
      loading.value = false
    }
  }

  // 获取用户菜单
  const getUserMenusAction = async () => {
    try {
      loading.value = true
      const res = await getUserMenuTree()
      menus.value = convertMenuFormat(res.data)
      
      // 同时获取后端路由配置
      try {
        const routesRes = await getRoutes()
        routes.value = routesRes.data || []
      } catch (routeError) {
        console.error('获取路由配置失败:', routeError)
        routes.value = []
      }
      
      return menus.value
    } catch (error) {
      console.error('获取用户菜单失败:', error)
      return []
    } finally {
      loading.value = false
    }
  }

  // 将后端菜单格式转换为前端所需格式
  const convertMenuFormat = (backendMenus) => {
    const iconMap = {
      'User': 'fa-users',
      'Reading': 'fa-book',
      'Folder': 'fa-folder',
      'List': 'fa-tasks',
      'ChatDotRound': 'fa-comments',
      'Star': 'fa-star',
      'DataLine': 'fa-chart-line',
      'system': 'Setting',
      'education': 'Reading',
      'documentation': 'Document',
      'clipboard': 'Document',
      'chart': 'DataLine'
    }

    // 过滤掉path为'/'或'/dashboard'的菜单项（即首页）
    const filteredMenus = backendMenus.filter(menu => 
      menu.path !== '/' && 
      menu.path !== '/dashboard' && 
      menu.path !== 'dashboard'
    );

    // 使用Set记录已处理的菜单ID，避免重复
    const processedMenuIds = new Set();
    const result = [];

    filteredMenus.forEach(menu => {
      // 跳过已处理的菜单
      if (processedMenuIds.has(menu.menuId)) {
        console.warn(`菜单 ${menu.menuName} (ID: ${menu.menuId}) 已存在，跳过处理`);
        return;
      }

      // 确保菜单路径以'/'开头
      const normalizedMenuPath = menu.path.startsWith('/') ? menu.path : `/${menu.path}`;

      const newMenu = {
        id: menu.menuId,
        name: menu.menuName,
        icon: iconMap[menu.icon] || menu.icon,
        path: normalizedMenuPath,
        children: menu.children && menu.children.length > 0 
          ? menu.children.filter(child => 
              child.path !== '/' && 
              child.path !== '/dashboard' && 
              child.path !== 'dashboard' &&
              !processedMenuIds.has(child.menuId) // 过滤掉已处理的子菜单
            ).map(child => {
              processedMenuIds.add(child.menuId); // 记录已处理的子菜单ID
              
              // 确保子菜单路径格式正确
              let childPath = child.path;
              // 如果子菜单路径不是以'/'开头，并且不是相对于父菜单的路径，则视为相对于父菜单的路径
              if (!childPath.startsWith('/') && !childPath.includes('/')) {
                childPath = `${normalizedMenuPath}/${childPath}`;
              } else if (!childPath.startsWith('/')) {
                // 如果不是以'/'开头但包含'/'，可能是相对路径，规范化为以'/'开头
                childPath = `/${childPath}`;
              }
              
              return {
              id: child.menuId,
              name: child.menuName,
                path: childPath,
              perms: child.perms
              };
            })
          : []
      };

      processedMenuIds.add(menu.menuId); // 记录已处理的菜单ID
      result.push(newMenu);
    });

    return result;
  }

  // 根据权限过滤路由
  const filterAsyncRoutes = (routes, permissions) => {
    const res = []
    
    routes.forEach(route => {
      const tmp = { ...route }
      
      // 检查是否有访问权限
      if (hasRoutePermission(tmp, permissions)) {
        // 处理子路由
        if (tmp.children) {
          tmp.children = filterAsyncRoutes(tmp.children, permissions)
        }
        res.push(tmp)
      }
    })
    
    return res
  }
  
  // 检查路由是否有权限
  const hasRoutePermission = (route, permissions) => {
    if (route.meta && route.meta.permissions) {
      return route.meta.permissions.some(permission => permissions.includes(permission))
    }
    return true
  }
  
  // 动态加载组件
  const loadComponent = (component) => {
    // 处理布局组件
    if (component === 'Layout') {
      return () => import('@/layout/index.vue')
    }
    
    // 处理其他组件
    try {
      console.log(`尝试加载组件: ${component}`)
      
      // 检查组件路径是否包含文件扩展名
      const componentPath = component.endsWith('.vue') 
        ? component.replace('.vue', '') 
        : component
        
      // 确保路径格式正确，移除开头的斜杠
      const normalizedPath = componentPath.startsWith('/') 
        ? componentPath.substring(1) 
        : componentPath
      
      console.log(`格式化后的组件路径: ${normalizedPath}`)
      
      // 查找匹配的模块
      const matchingPaths = Object.keys(viewModules).filter(path => {
        // 移除/src前缀和.vue后缀以便于比较
        const normalizedModulePath = path.replace(/^\/src\/views\//, '').replace(/\.vue$/, '')
        return normalizedModulePath === normalizedPath || 
               path.endsWith(`/${normalizedPath}.vue`) || 
               path === `/src/views/${normalizedPath}.vue`
      })
      
      if (matchingPaths.length > 0) {
        // 使用第一个匹配的模块
        console.log(`找到匹配组件: ${matchingPaths[0]}`)
        return viewModules[matchingPaths[0]]
      } else {
        console.warn(`未找到匹配的组件: ${normalizedPath}, 尝试直接导入`)
        return () => import(`@/views/${normalizedPath}.vue`)
      }
    } catch (error) {
      console.error(`无法加载组件: ${component}`, error)
      return () => import('@/views/error/404.vue')
    }
  }

  // 将后端路由转换为前端路由格式
  const convertBackendRoutes = (backendRoutes, parentPath = '') => {
    const result = []
    
    backendRoutes.forEach(route => {
      if (!route.path) {
        console.warn('路由缺少path属性:', route)
        return
      }
      
      const { path, component, name, meta, children, menuName, icon, perms } = route
      
      // 确保路径以"/"开头，并拼接父路径
      let normalizedPath = path.startsWith('/') ? path : `/${path}`
      if (parentPath) {
        // 如果有父路径，需要拼接完整路径
        // 处理可能出现的双斜杠
        normalizedPath = `${parentPath}/${path}`.replace(/\/+/g, '/')
      }
      
      console.log(`处理路由: ${normalizedPath}, 组件: ${component || '未指定组件'}`)
      
      // 创建路由对象
      const routeObj = {
        path: normalizedPath,
        name: name || menuName || '',
        meta: {
          ...(meta || {}),
          title: menuName || name || '',
          icon: icon || '',
          permissions: perms ? [perms] : []
        }
      }
      
      // 处理组件
      if (component) {
        // 有组件的情况，动态导入
        routeObj.component = loadComponent(component)
        console.log(`${normalizedPath} 的组件为: ${routeObj.component}`)
      } else if (!children || children.length === 0) {
        // 无组件且无子路由，视为外部链接或特殊路由
        console.log(`${normalizedPath} 无组件且无子路由，可能是外部链接`)
      } else {
        // 无组件但有子路由，使用Layout组件
        //
      }
      
      // 处理子路由
      if (children && children.length > 0) {
        console.log(`处理[${normalizedPath}]的子路由，数量: ${children.length}`)
        routeObj.children = convertBackendRoutes(children, normalizedPath)
      }
      result.push(routeObj)
      
    })
    return result
  }

  // 动态生成路由
  const generateRoutes = async () => {
    // 根据权限过滤异步路由
    const accessedRoutes = filterAsyncRoutes(asyncRoutes, permissions.value)
    const finalRoutes = [] // 最终返回的路由列表
    
    // 如果后端有返回路由配置，与前端路由合并
    if (routes.value && routes.value.length > 0) {
      try {
        console.log('开始处理后端路由', routes.value)
        const backendRoutes = convertBackendRoutes(routes.value)
        console.log('处理后的后端路由', backendRoutes)
        
        // 创建一个用于去重的路径集合
        const existingPaths = new Set(accessedRoutes.map(route => route.path))
        
        // 找到布局路由
        let layoutRoute = null
        for (const route of [...constantRoutes]) {
          if (route.path === '/') {
            layoutRoute = { ...route }
            break
          }
        }
        
        // 如果没有找到布局路由，创建一个新的
        if (!layoutRoute) {
          layoutRoute = {
            path: '/',
            component: () => import('@/layout/index.vue'),
            name: 'Layout',
            redirect: '/dashboard',
            children: [{
              path: 'dashboard',
              name: 'Dashboard',
              component: () => import('@/views/dashboard/index.vue'),
              meta: { title: '首页', icon: 'HomeFilled', affix: true }
            },{
              path: 'profile',
              name: 'Profile',
              component: () => import('@/views/profile/index.vue'),
              meta: { title: '个人信息', icon: 'User' }
            },
            {
              path: "test",
              name: 'Test',
              component: () => import('@/views/test/index.vue'),
            }
          ]
          }
        }
        
        // 将处理后的后端路由添加到布局路由的children中
        backendRoutes.forEach(route => {
          // 如果路由路径为根路径，将其子路由添加到布局路由的children中
          if (route.path === '/') {
            if (route.children && route.children.length > 0) {
              route.children.forEach(child => {
                // 避免重复添加
                if (!existingPaths.has(child.path)) {
                  layoutRoute.children.push(child)
                  existingPaths.add(child.path)
                }
              })
            }
          } else {
            // 非根路径的路由直接添加到布局路由的children中
            // 避免重复添加
            if (!existingPaths.has(route.path)) {
              layoutRoute.children.push(route)
              existingPaths.add(route.path)
            } else {
              console.warn(`路由路径 ${route.path} 已存在，跳过添加`)
            }
          }
        })
        
        // 添加访问权限过滤后的异步路由到布局路由的children中
        accessedRoutes.forEach(route => {
          if (!existingPaths.has(route.path)) {
            layoutRoute.children.push(route)
            existingPaths.add(route.path)
          }
        })
        console.log(layoutRoute);
        
        // 将布局路由添加到最终路由列表中
        finalRoutes.push(layoutRoute)
      } catch (error) {
        console.error('处理后端路由出错:', error)
      }
    } else {
      // 没有后端路由，直接返回前端路由
      finalRoutes.push(...accessedRoutes)
    }
    
    console.log('最终生成的路由:', finalRoutes)
    return finalRoutes
  }

  // 退出登录
  const logoutAction = async () => {
    try {
      if (token.value) {
        await logout()
      }
      
      // 导入路由状态重置函数
      const { resetRouteGeneratedState } = await import('@/router')
      // 重置路由生成状态
      resetRouteGeneratedState()
      
      resetState()
      router.push('/login')
      return true
    } catch (error) {
      console.error('登出失败:', error)
      return false
    }
  }

  // 重置状态
  const resetState = () => {
    clearUserInfo()
    token.value = ''
    userInfo.value = {}
    roles.value = []
    permissions.value = []
    menus.value = []
    routes.value = []
    }

    return {
        token,
        userInfo,
    roles,
    permissions,
    menus,
    routes,
    loading,
    loginAction,
    getUserInfoAction,
    getUserMenusAction,
    generateRoutes,
    logoutAction,
    resetState,
        hasPermission,
        hasRole
    }
})