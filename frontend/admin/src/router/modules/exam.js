// 考试管理路由
const examRoutes = {
  path: '/',
  component: () => import('@/layout/index.vue'),
  redirect: '/exam/paper',
  name: 'Exam',
  meta: { title: '考试管理', icon: 'Document', permissions: ['exam:list'] },
  children: [
    {
      path: 'paper',
      name: 'ExamPaperList',
      component: () => import('@/views/exam/paper/index.vue'),
      meta: { title: '考试列表', icon: 'List', permissions: ['exam:list'] }
    },
    {
      path: 'record/list/:id',
      name: 'ExamRecordList',
      hidden: true,
      component: () => import('@/views/exam/record/list.vue'),
      meta: { title: '考试记录列表', activeMenu: '/exam/paper', permissions: ['exam:record:list'] }
    },
    {
      path: 'record/grade/:id',
      name: 'ExamRecordGrade',
      hidden: true,
      component: () => import('@/views/exam/record/grade.vue'),
      meta: { title: '考试批阅', activeMenu: '/exam/paper', permissions: ['exam:record:grade'] }
    },
    {
      path: 'record/detail/:id',
      name: 'ExamRecordDetail',
      hidden: true,
      component: () => import('@/views/exam/record/detail.vue'),
      meta: { title: '考试详情', activeMenu: '/exam/paper', permissions: ['exam:record:detail'] }
    },
    {
      path: 'assignment',
      name: 'AssignmentList',
      component: () => import('@/views/exam/assignment/index.vue'),
      meta: { title: '作业列表', icon: 'Notebook', permissions: ['exam:assignment:list'] }
    },
    {
      path: 'assignment/submission/:id',
      name: 'AssignmentSubmissionList',
      hidden: true,
      component: () => import('@/views/exam/assignment/submission/index.vue'),
      meta: { title: '作业提交列表', activeMenu: '/exam/assignment', permissions: ['exam:submission:list'] }
    },
    {
      path: 'assignment/submission/detail/:id',
      name: 'AssignmentSubmissionDetail',
      hidden: true,
      component: () => import('@/views/exam/assignment/submission/detail.vue'),
      meta: { title: '作业批阅', activeMenu: '/exam/assignment', permissions: ['exam:submission:detail'] }
    },
    {
      path: 'question',
      name: 'QuestionList',
      component: () => import('@/views/exam/question/index.vue'),
      meta: { title: '题库管理', icon: 'Collection', permissions: ['exam:question:list'] }
    }
  ]
}

export default examRoutes 