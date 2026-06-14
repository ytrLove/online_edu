// 统计分析模块路由
const statRoutes = {
  path: '/stat',
  component: () => import('@/layout/index.vue'),
  redirect: '/stat/evaluation',
  name: 'Stat',
  meta: { title: '统计分析', icon: 'DataAnalysis', permissions: ['stat:view'] },
  children: [
    {
      path: 'evaluation',
      name: 'StatEvaluation',
      component: () => import('@/views/stat/evaluation/index.vue'),
      meta: { title: '教学评价', icon: 'Star', permissions: ['stat:evaluation:view'] }
    },
    {
      path: 'behavior',
      name: 'StatBehavior',
      component: () => import('@/views/stat/behavior/index.vue'),
      meta: { title: '学习行为', icon: 'Operation', permissions: ['stat:behavior:view'] }
    },
    {
      path: 'report',
      name: 'StatReport',
      component: () => import('@/views/stat/report/index.vue'),
      meta: { title: '教学报告', icon: 'Document', permissions: ['stat:report:view'] }
    },
    {
      path: 'summary',
      name: 'StatSummary',
      component: () => import('@/views/stat/summary/index.vue'),
      meta: { title: '学习总结', icon: 'Files', permissions: ['stat:summary:view'] }
    }
  ]
}

export default statRoutes 