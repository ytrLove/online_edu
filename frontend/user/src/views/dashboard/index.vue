<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-card">
        <div class="welcome-content">
          <div class="user-greeting">
            <div class="greeting-avatar">
              <el-avatar :size="60" :src="userInfo.avatar">
                {{ userInfo.nickname ? userInfo.nickname.charAt(0) : '学' }}
              </el-avatar>
              <div class="online-status"></div>
            </div>
            <div class="greeting-text">
              <h1 class="greeting-title">欢迎回来，{{ userInfo.nickname || '同学' }}！</h1>
              <p class="greeting-subtitle">{{ greetingMessage }}</p>
            </div>
          </div>
          <div class="quick-actions">
            <el-button type="primary" class="action-btn" @click="router.push('/courses')">
              <el-icon><Reading /></el-icon>
              浏览课程
            </el-button>
            <el-button class="action-btn" @click="router.push('/assignments')">
              <el-icon><Document /></el-icon>
              查看作业
            </el-button>
          </div>
        </div>
        <div class="welcome-decoration">
          <div class="floating-icon floating-icon-1">
            <el-icon><Reading /></el-icon>
          </div>
          <div class="floating-icon floating-icon-2">
            <el-icon><Trophy /></el-icon>
          </div>
          <div class="floating-icon floating-icon-3">
            <el-icon><Star /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card hover-lift" :class="'stat-card-' + index" v-for="(stat, index) in statsData" :key="index">
          <div class="stat-icon-wrapper">
            <div class="stat-icon" :class="stat.iconClass">
              <component :is="stat.icon" />
            </div>
          </div>
          <div class="stat-content">
            <div class="stat-value" :data-target="stat.value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-trend" :class="stat.trend">
              <el-icon v-if="stat.trend === 'up'"><ArrowUp /></el-icon>
              <el-icon v-else-if="stat.trend === 'down'"><ArrowDown /></el-icon>
              <span>{{ stat.change }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <el-row :gutter="24" class="main-content">
      <!-- 课程区域 -->
      <el-col :xs="24" :lg="16">
        <div class="content-card course-section">
          <div class="card-header">
            <div class="header-content">
              <div class="header-icon">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="header-text">
                <h2 class="section-title">我的课程</h2>
                <p class="section-subtitle">继续您的学习进度</p>
              </div>
            </div>
            <el-button type="primary" link class="view-all-btn" @click="router.push('/my-courses')">
              查看全部
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          
          <div class="course-list" v-if="recentCourses.length > 0">
            <div v-for="course in displayCourses" :key="course.courseId" class="course-item hover-lift" @click="goCourseDetail(course.courseId)">
              <div class="course-image">
                <el-image :src="course.coverImage || defaultCover" fit="cover" class="course-cover">
                  <template #error>
                    <div class="image-placeholder">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div class="course-overlay">
                  <el-icon class="play-icon"><VideoPlay /></el-icon>
                </div>
              </div>
              <div class="course-info">
                <h3 class="course-title">{{ course.courseName }}</h3>
                <p class="course-description">{{ course.description }}</p>
                <div class="course-meta">
                  <span class="course-progress">学习进度</span>
                  <el-progress :percentage="Math.random() * 100" :show-text="false" stroke-width="4" />
                </div>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-state">
            <div class="empty-icon">
              <el-icon><Reading /></el-icon>
            </div>
            <h3>暂无已选课程</h3>
            <p>开始您的学习之旅吧</p>
            <el-button type="primary" @click="router.push('/courses')">
              去选课
            </el-button>
          </div>
        </div>
      </el-col>
      
      <!-- 活动时间轴 -->
      <el-col :xs="24" :lg="8">
        <div class="content-card activity-section">
          <div class="card-header">
            <div class="header-content">
              <div class="header-icon">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="header-text">
                <h2 class="section-title">最近活动</h2>
                <p class="section-subtitle">您的学习动态</p>
              </div>
            </div>
          </div>
          
          <div class="activity-timeline" v-if="recentActivities.length > 0">
            <div v-for="(activity, index) in recentActivities" :key="index" class="timeline-item">
              <div class="timeline-dot" :class="getActivityType(activity.behaviorType)"></div>
              <div class="timeline-content">
                <div class="activity-header">
                  <span class="activity-title">{{ getActivityTitle(activity) }}</span>
                  <span class="activity-time">{{ formatTime(activity.behaviorTime) }}</span>
                </div>
                <p class="activity-detail">{{ activity.courseName }}</p>
                <div class="activity-duration" v-if="activity.duration">
                  持续时长: {{ formatDuration(activity.duration) }}
                </div>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-state">
            <div class="empty-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <h3>暂无活动记录</h3>
            <p>开始学习后这里会显示您的活动</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 待完成作业区域 -->
    <div class="assignment-section">
      <div class="content-card">
        <div class="card-header">
          <div class="header-content">
            <div class="header-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="header-text">
              <h2 class="section-title">待完成作业</h2>
              <p class="section-subtitle">及时完成您的作业</p>
            </div>
          </div>
          <el-button type="primary" link class="view-all-btn" @click="router.push('/assignments')">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
        
        <div v-if="pendingAssignments.length > 0" class="assignment-list">
          <div v-for="assignment in pendingAssignments" :key="assignment.assignmentId" class="assignment-item hover-lift">
            <div class="assignment-info">
              <div class="assignment-header">
                <h3 class="assignment-title">{{ assignment.title }}</h3>
                <el-tag :type="getAssignmentTagType(assignment)" size="small">
                  {{ getStatusText(assignment) }}
                </el-tag>
              </div>
              <p class="assignment-course">{{ assignment.courseName }}</p>
              <div class="assignment-meta">
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  截止时间: {{ assignment.dueTime }}
                </span>
                <span class="meta-item">
                  <el-icon><Trophy /></el-icon>
                  总分: {{ assignment.totalPoints }}分
                </span>
              </div>
            </div>
            <div class="assignment-actions">
              <el-button type="primary" size="small" @click="goAssignmentDetail(assignment.assignmentId)">
                {{ assignment.submissionStatus === 'submitted' ? '查看作业' : '开始作业' }}
              </el-button>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <div class="empty-icon">
            <el-icon><Document /></el-icon>
          </div>
          <h3>暂无待完成作业</h3>
          <p>所有作业都已完成，真棒！</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Reading, 
  Document, 
  Trophy, 
  Star, 
  ArrowUp, 
  ArrowDown, 
  ArrowRight,
  Clock, 
  Calendar, 
  Picture, 
  VideoPlay
} from '@element-plus/icons-vue'
import { getUserInfo } from '@/api/auth'
import { getCourseList, getHomeworkList } from '@/api/home'
import { getBehaviorList } from '@/api/behavior'
import { getAssignmentList } from '@/api/assignment'
import { formatDate } from '@/utils/date'

const router = useRouter()
const userInfo = ref({})
const stats = reactive({
  courseCount: 0,
  assignmentCount: 0,
  examCount: 0,
  resourceCount: 0
})

// 
const recentCourses = ref([])
const recentActivities = ref([])
const pendingAssignments = ref([])
const defaultCover = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'

const statusText = {
   '1' : '正常',
   '2' : '已截止',
}
// 记录映射
const behaviorMap = {
  'view_resource' : '阅览资源',
  'view_course' : '学习课程',
  'view_discussion' : '参与讨论',
}

const getBehaviorType = (type) => {
  return behaviorMap[type]
}

const getCourseName = (courseId) => {
  return recentCourses.value.find(course => course.courseId === courseId)?.courseName
}

const greetingMessage = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) {
    return '夜深了，注意休息哦'
  } else if (hour < 9) {
    return '早上好，新的一天开始啦'
  } else if (hour < 12) {
    return '上午好，学习状态如何'
  } else if (hour < 14) {
    return '中午好，记得午休哦'
  } else if (hour < 18) {
    return '下午好，继续加油吧'
  } else if (hour < 22) {
    return '晚上好，别忘了完成今日任务'
  } else {
    return '夜深了，注意休息哦'
  }
})

// 限制首页显示的课程数量为4个
const displayCourses = computed(() => {
  return recentCourses.value.slice(0, 4)
})

// 统计数据
const statsData = computed(() => [
  {
    value: recentCourses.value.length || 0,
    label: '已选课程',
    icon: Reading,
    iconClass: 'courses',
    trend: 'up',
    change: '+2 本月'
  },
  {
    value: stats.assignmentCount || 0,
    label: '待完成作业',
    icon: Document,
    iconClass: 'assignments',
    trend: 'down',
    change: '-3 本周'
  },
  {
    value: stats.examCount || 0,
    label: '即将到来的考试',
    icon: Trophy,
    iconClass: 'exams',
    trend: 'up',
    change: '+1 本月'
  },
  {
    value: stats.resourceCount || 0,
    label: '新的学习资源',
    icon: Star,
    iconClass: 'resources',
    trend: 'up',
    change: '+5 本周'
  }
])

// 获取作业标签类型
const getAssignmentTagType = (assignment) => {
  if (assignment.deadlineStatus === 'expired') {
    return 'danger'
  }
  if (assignment.submissionStatus === 'submitted') {
    return 'success'
  }
  return 'warning'
}

onMounted(async () => {
  await fetchUserInfo()
  await fetchEnrolledCourses()
  await fetchAssignments()
  // 模拟获取活动数据
  await generateMockData()
})

const fetchUserInfo = async () => {
  try {
    const result = await getUserInfo()
    userInfo.value = result.data.user
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

const fetchEnrolledCourses = async () => {
  try {
    const result = await getCourseList()
    recentCourses.value = result.data
    stats.courseCount = result.data.length
  } catch (error) {
    console.error('获取课程信息失败', error)
  }
}

const fetchAssignments = async () => {
  try {
    const result = await getHomeworkList()
    pendingAssignments.value = result.data
    stats.assignmentCount = result.data.length
  } catch (error) {
    console.error('获取作业信息失败', error)
  }
}

// 获取活动数据
const generateMockData = async () => {
  try {
    const result = await getBehaviorList()
    // 获取活动记录
    const activities = result.data.records
    
    // 获取所有相关课程ID
    const courseIds = [...new Set(activities.map(activity => activity.courseId))]
    
    // 获取课程信息
    const courseResult = await getCourseList()
    const courseMap = new Map(courseResult.data.map(course => [course.courseId, course]))
    
    // 处理活动数据，添加课程名称
    recentActivities.value = activities.map(activity => ({
      ...activity,
      courseName: courseMap.get(activity.courseId)?.courseName || '未知课程'
    })).slice(0, 3)
  } catch (error) {
    console.error('获取活动数据失败:', error)
  }
}

const goCourseDetail = (courseId) => {
  router.push(`/course/${courseId}`)
}

const goAssignmentDetail = (assignmentId) => {
  router.push(`/assignment/${assignmentId}`)
}

// 获取活动类型
const getActivityType = (type) => {
  const typeMap = {
    'view_resource': 'primary',
    'view_course': 'success',
    'submit_assignment': 'warning',
    'take_exam': 'danger',
    'post_discussion': 'info'
  }
  return typeMap[type] || 'primary'
}

// 获取活动颜色
const getActivityColor = (type) => {
  const colorMap = {
    'view_resource': '#409EFF',
    'view_course': '#67C23A',
    'submit_assignment': '#E6A23C',
    'take_exam': '#F56C6C',
    'post_discussion': '#909399'
  }
  return colorMap[type] || '#409EFF'
}

// 获取活动图标
const getActivityIcon = (type) => {
  const iconMap = {
    'view_resource': 'el-icon-document',
    'view_course': 'el-icon-reading',
    'submit_assignment': 'el-icon-edit-outline',
    'take_exam': 'el-icon-notebook-2',
    'post_discussion': 'el-icon-chat-dot-round'
  }
  return iconMap[type] || 'el-icon-info'
}

// 获取活动标题
const getActivityTitle = (activity) => {
  const typeMap = {
    'view_resource': '查看了学习资源',
    'view_course': '学习了课程',
    'submit_assignment': '提交了作业',
    'take_exam': '参加了考试',
    'post_discussion': '参与了讨论'
  }
  return typeMap[activity.behaviorType] || '未知活动'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化持续时间
const formatDuration = (seconds) => {
  if (seconds < 60) {
    return `${seconds}秒`
  } else if (seconds < 3600) {
    return `${Math.floor(seconds / 60)}分钟`
  } else {
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    return `${hours}小时${minutes}分钟`
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: var(--container-padding);
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 140px);
}

/* 欢迎区域 */
.welcome-section {
  margin-bottom: 32px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: var(--vt-radius-large);
  padding: 40px;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: var(--vt-shadow-3);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-content {
  flex: 1;
  z-index: 2;
  position: relative;
}

.user-greeting {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.greeting-avatar {
  position: relative;
}

.greeting-avatar .el-avatar {
  border: 3px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.online-status {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 16px;
  height: 16px;
  background: #52c41a;
  border: 3px solid white;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.greeting-text {
  flex: 1;
}

.greeting-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
}

.greeting-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
  text-shadow: 0 1px 10px rgba(0, 0, 0, 0.2);
}

.quick-actions {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.action-btn {
  background: rgba(255, 255, 255, 0.2) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: white !important;
  backdrop-filter: blur(10px);
  border-radius: var(--vt-radius-small) !important;
  padding: 12px 24px !important;
  font-weight: 500 !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.3) !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.welcome-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 300px;
  height: 100%;
  z-index: 1;
}

.floating-icon {
  position: absolute;
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.floating-icon-1 {
  top: 20%;
  right: 15%;
  animation: float 6s ease-in-out infinite;
}

.floating-icon-2 {
  top: 60%;
  right: 25%;
  animation: float 6s ease-in-out infinite reverse;
  animation-delay: -2s;
}

.floating-icon-3 {
  top: 40%;
  right: 5%;
  animation: float 6s ease-in-out infinite;
  animation-delay: -4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 统计卡片区域 */
.stats-section {
  margin-bottom: 32px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.stat-card {
  background: white;
  border-radius: var(--vt-radius-large);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: var(--vt-shadow-1);
  border: 1px solid var(--color-border);
  transition: all var(--vt-transition-medium) ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  border-radius: 2px;
}

.stat-card-0::before {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-card-1::before {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-card-2::before {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.stat-card-3::before {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--vt-shadow-3);
}

.stat-icon-wrapper {
  flex-shrink: 0;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--vt-radius-medium);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  position: relative;
}

.stat-icon.courses {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-icon.assignments {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-icon.exams {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.stat-icon.resources {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-heading);
  margin-bottom: 4px;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.stat-trend.up {
  color: #52c41a;
}

.stat-trend.down {
  color: #ff4d4f;
}

/* 主要内容区域 */
.main-content {
  margin-bottom: 32px;
}

.content-card {
  background: white;
  border-radius: var(--vt-radius-large);
  padding: 24px;
  box-shadow: var(--vt-shadow-1);
  border: 1px solid var(--color-border);
  transition: all var(--vt-transition-medium) ease;
  height: 100%;
}

.content-card:hover {
  box-shadow: var(--vt-shadow-2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-border);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--vt-radius-small);
  background: var(--vt-gradient-primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.header-text {
  flex: 1;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-heading);
  margin: 0 0 4px 0;
}

.section-subtitle {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

.view-all-btn {
  font-weight: 500 !important;
  display: flex !important;
  align-items: center !important;
  gap: 4px !important;
}

/* 课程区域 */
.course-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.course-item {
  background: var(--color-background-soft);
  border-radius: var(--vt-radius-medium);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--vt-transition-medium) ease;
  border: 1px solid var(--color-border);
}

.course-item:hover {
  transform: translateY(-4px);
  box-shadow: var(--vt-shadow-2);
}

.course-image {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.course-cover {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-background-mute);
  color: var(--color-text-tertiary);
  font-size: 32px;
}

.course-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--vt-transition-medium) ease;
}

.course-item:hover .course-overlay {
  opacity: 1;
}

.play-icon {
  font-size: 32px;
  color: white;
}

.course-info {
  padding: 16px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-heading);
  margin: 0 0 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-description {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.course-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.course-progress {
  font-size: 12px;
  color: var(--color-text-tertiary);
  margin-bottom: 4px;
}

/* 活动时间轴 */
.activity-timeline {
  position: relative;
}

.timeline-item {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  position: relative;
}

.timeline-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: 8px;
  top: 32px;
  width: 2px;
  bottom: -20px;
  background: var(--color-border);
}

.timeline-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 4px;
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.timeline-dot.view_resource {
  background: #409eff;
}

.timeline-dot.view_course {
  background: #67c23a;
}

.timeline-dot.view_discussion {
  background: #e6a23c;
}

.timeline-content {
  flex: 1;
  background: var(--color-background-soft);
  border-radius: var(--vt-radius-small);
  padding: 16px;
  border: 1px solid var(--color-border);
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.activity-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-heading);
}

.activity-time {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.activity-detail {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0 0 4px 0;
}

.activity-duration {
  font-size: 12px;
  color: var(--color-primary);
  font-weight: 500;
}

/* 作业区域 */
.assignment-section {
  margin-bottom: 32px;
}

.assignment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.assignment-item {
  background: var(--color-background-soft);
  border-radius: var(--vt-radius-medium);
  padding: 20px;
  border: 1px solid var(--color-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all var(--vt-transition-medium) ease;
}

.assignment-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--vt-shadow-2);
}

.assignment-info {
  flex: 1;
}

.assignment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.assignment-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-heading);
  margin: 0;
}

.assignment-course {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0 0 12px 0;
}

.assignment-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.assignment-actions {
  margin-left: 20px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: var(--color-background-mute);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-tertiary);
  font-size: 32px;
}

.empty-state h3 {
  font-size: 18px;
  color: var(--color-text-secondary);
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 14px;
  color: var(--color-text-tertiary);
  margin: 0 0 24px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  }
  
  .welcome-card {
    flex-direction: column;
    text-align: center;
  }
  
  .welcome-decoration {
    display: none;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .welcome-card {
    padding: 24px;
  }
  
  .greeting-title {
    font-size: 24px;
  }
  
  .user-greeting {
    flex-direction: column;
    text-align: center;
  }
  
  .quick-actions {
    justify-content: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .course-list {
    grid-template-columns: 1fr;
  }
  
  .assignment-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .assignment-actions {
    margin-left: 0;
    align-self: stretch;
  }
  
  .assignment-meta {
    flex-direction: column;
    gap: 8px;
  }
}
</style>