import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/layout/AppLayout.vue'

// 导入统计模块的视图组件
import StatisticsIndex from '@/views/statistics/index.vue'
import StatisticsGrades from '@/views/statistics/grades.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { title: '登录', noAuth: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/register/index.vue'),
      meta: { title: '注册', noAuth: true }
    },
    {
      path: '/',
      component: AppLayout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '首页', icon: 'dashboard' }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/profile/index.vue'),
          meta: { title: '个人设置', icon: 'user' }
        },
        {
          path: 'courses',
          name: 'Courses',
          component: () => import('@/views/courses/index.vue'),
          meta: { title: '课程中心', icon: 'courses' }
        },
        {
          path: 'course/:id',
          name: 'CourseDetail',
          component: () => import('@/views/courses/detail.vue'),
          meta: { title: '课程详情', activeMenu: '/courses' }
        },
        {
          path: 'my-courses',
          name: 'MyCourses',
          component: () => import('@/views/my-courses/index.vue'),
          meta: { title: '我的课程', icon: 'my-courses' }
        },
        {
          path: 'assignments',
          name: 'Assignments',
          component: () => import('@/views/assignments/index.vue'),
          meta: { title: '作业考试', icon: 'assignments' }
        },
        {
          path: 'assignment/:id',
          name: 'AssignmentDetail',
          component: () => import('@/views/assignments/detail.vue'),
          meta: { title: '作业详情', activeMenu: '/assignments' }
        },
        {
          path: 'exam/:id',
          name: 'ExamDetail',
          component: () => import('@/views/assignments/exam.vue'),
          meta: { title: '考试', activeMenu: '/assignments' }
        },
        {
          path: 'discussion',
          name: 'Discussion',
          component: () => import('@/views/discussion/index.vue'),
          meta: { title: '讨论区', icon: 'discussion' }
        },
        {
          path: 'discussion/detail/:id',
          name: 'TopicDetail',
          component: () => import('@/views/discussion/detail.vue'),
          meta: { title: '话题详情', activeMenu: '/discussion' }
        },
        {
          path: 'ai-assistant',
          name: 'AIAssistant',
          component: () => import('@/views/ai-assistant/index.vue'),
          meta: { title: 'AI助手', icon: 'ChatDotRound' }
        },
        {
          path: 'ai-assistant/chat',
          name: 'AIChat',
          component: () => import('@/views/ai-assistant/chat.vue'),
          meta: { title: 'AI聊天', activeMenu: '/ai-assistant' }
        },
        // {
        //   path: 'statistics',
        //   name: 'Statistics',
        //   component: StatisticsIndex,
        //   meta: {
        //     title: '学习统计',
        //     icon: 'PieChart',
        //     requireAuth: true
        //   }
        // },
        {
          path: 'grades',
          name: 'Grades',
          component: StatisticsGrades,
          meta: { 
            title: '成绩详情',
            requireAuth: true 
          }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/error/404.vue'),
      meta: { title: '404', noAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 在线教学平台学生端` : '在线教学平台学生端'
  
  // 检查是否需要登录权限
  const token = localStorage.getItem('token')
  
  if (!to.meta.noAuth && !token) {
    // 需要登录但未登录，重定向到登录页
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    // 不需要登录或已登录
    next()
  }
})

export default router
