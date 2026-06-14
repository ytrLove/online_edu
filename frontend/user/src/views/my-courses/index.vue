<template>
  <div class="my-courses-container">
    <div class="page-header">
      <h1>我的课程</h1>
      <div class="action-btns">
        <el-button type="primary" @click="goToCourseCenter">浏览更多课程</el-button>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card shadow="never" class="filter-card">
      <div class="filter-container">
        <div class="search-box">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索课程" 
            clearable 
            @clear="handleSearch"
            @keyup.enter="handleSearch">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        
        <div class="filter-options">
          <el-select v-model="filterStatus" placeholder="课程状态" @change="handleSearch" clearable>
            <el-option label="全部状态" value=""></el-option>
            <el-option label="进行中" value="in_progress"></el-option>
            <el-option label="已完成" value="completed"></el-option>
            <el-option label="未开始" value="not_started"></el-option>
          </el-select>
          
          <el-select v-model="filterSubject" placeholder="课程分类" @change="handleSearch" clearable>
            <el-option label="全部分类" value=""></el-option>
            <el-option 
              v-for="subject in subjectOptions" 
              :key="subject.subjectId" 
              :label="subject.subjectName" 
              :value="subject.subjectId">
            </el-option>
          </el-select>
          
          <el-select v-model="sortOption" placeholder="排序方式" @change="handleSearch">
            <el-option label="最近学习" value="last_learn"></el-option>
            <el-option label="最早选课" value="enroll_time_asc"></el-option>
            <el-option label="最近选课" value="enroll_time_desc"></el-option>
            <el-option label="学习进度" value="progress"></el-option>
          </el-select>
        </div>
      </div>
    </el-card>

    <!-- 课程筛选标签 -->
    <div v-if="subjectOptions.length > 0" class="subject-tags">
      <el-scrollbar>
        <div class="tags-container">
          <el-tag 
            :effect="filterSubject === '' ? 'dark' : 'plain'"
            @click="selectSubject('')"
            class="filter-tag"
          >
            全部
          </el-tag>
          <el-tag 
            v-for="subject in subjectOptions" 
            :key="subject.subjectId"
            :type="getTagType(subject.subjectId)"
            :effect="filterSubject === subject.subjectId ? 'dark' : 'plain'"
            @click="selectSubject(subject.subjectId)"
            class="filter-tag"
          >
            {{ subject.subjectName }}
          </el-tag>
        </div>
      </el-scrollbar>
    </div>

    <!-- 课程列表 -->
    <div class="course-list" v-loading="loading">
      <template v-if="displayCourses.length > 0">
        <el-row :gutter="24">
          <el-col v-for="course in displayCourses" :key="course.courseId" :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
            <div class="course-card-wrapper">
              <el-card class="course-card" :body-style="{ padding: '0px' }">
                <div class="course-content" @click="goToCourseDetail(course.courseId)">
                  <div class="course-img">
                    <el-image :src="course.coverImage || defaultCover" fit="cover">
                      <template #error>
                        <div class="image-placeholder">
                          <el-icon><Picture /></el-icon>
                        </div>
                      </template>
                    </el-image>
                    <div class="course-progress-badge">
                      <el-progress 
                        type="circle" 
                        :percentage="course.progress || 0" 
                        :width="40"
                        :stroke-width="6"
                        :color="getProgressColor(course.progress || 0)">
                      </el-progress>
                    </div>
                  </div>
                  <div class="course-info">
                    <div class="course-subject" v-if="course.subjectName">{{ course.subjectName }}</div>
                    <h3 class="course-title" :title="course.courseName">{{ course.courseName }}</h3>
                    
                    <div class="course-status">
                      <el-tag 
                        :type="getStatusTagType(course.completionStatus || 'in_progress')" 
                        size="small" 
                        effect="light">
                        {{ getStatusText(course.completionStatus || 'in_progress') }}
                      </el-tag>
                      <el-tag 
                        v-if="course.courseCode" 
                        type="info" 
                        size="small" 
                        effect="plain">
                        {{ course.courseCode }}
                      </el-tag>
                    </div>
                    
                    <div class="course-meta">
                      <div class="meta-item" v-if="course.enrollmentDate">
                        <el-icon><Calendar /></el-icon>
                        <span>选课时间: {{ formatDate(course.enrollmentDate || course.createTime) }}</span>
                      </div>
                      <div class="meta-item">
                        <el-icon><Clock /></el-icon>
                        <span>最近学习: {{ formatDate(course.lastViewTime) || '未学习' }}</span>
                      </div>
                    </div>
                    
                    <div class="course-desc" v-if="course.description">
                      {{ truncateText(course.description, 60) }}
                    </div>
                    
                    <div class="course-progress-bar">
                      <div class="progress-label">
                        <span>学习进度</span>
                        <span>{{ course.progress || 0 }}%</span>
                      </div>
                      <el-progress 
                        :percentage="course.progress || 0" 
                        :show-text="false"
                        :color="getProgressColor(course.progress || 0)">
                      </el-progress>
                    </div>
                  </div>
                </div>
                
                <div class="course-actions">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="startLearning(course)">
                    {{ course.progress > 0 ? '继续学习' : '开始学习' }}
                  </el-button>
                  <el-dropdown @command="handleCommand($event, course)" trigger="click">
                    <el-button size="small" plain>
                      更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="evaluate" v-if="course.completionStatus === 'in_progress' || course.completionStatus === 'completed'">评价课程</el-dropdown-item>
                        <el-dropdown-item command="assignments">查看作业</el-dropdown-item>
                        <el-dropdown-item command="exams">查看考试</el-dropdown-item>
                        <el-dropdown-item command="resources">学习资料</el-dropdown-item>
                        <el-dropdown-item command="discussion">讨论区</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </el-card>
            </div>
          </el-col>
        </el-row>

        <!-- 只有在数据是分页的情况下才显示分页控件 -->
        <div class="pagination-container" v-if="isPaginated && total > 0">
          <el-pagination
            :current-page="query.pageNum"
            :page-size="query.pageSize"
            :page-sizes="[8, 12, 16, 24]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          ></el-pagination>
        </div>
      </template>

      <el-empty v-else description="您还没有选修任何课程" class="empty-course">
        <template #extra>
          <el-button type="primary" @click="goToCourseCenter">去选课</el-button>
        </template>
      </el-empty>
    </div>

    <!-- 学习统计区 -->
    <el-card v-if="courseList.length > 0" class="statistics-card">
      <template #header>
        <div class="card-header">
          <span>学习统计</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8">
          <div class="stat-item">
            <div class="stat-icon">
              <el-icon><Collection /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ courseList.length || 0 }}</div>
              <div class="stat-label">已选课程</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="stat-item">
            <div class="stat-icon completed-icon">
              <el-icon><SuccessFilled /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.completedCourses || 0 }}</div>
              <div class="stat-label">已完成课程</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="stat-item">
            <div class="stat-icon time-icon">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatTime(stats.totalStudyTime || 0) }}</div>
              <div class="stat-label">总学习时长</div>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <div class="learning-trends" v-if="stats.learningTrend && stats.learningTrend.length > 0">
        <h3>最近学习趋势</h3>
        <div class="trend-chart">
          <div class="trend-bars">
            <div 
              v-for="(item, index) in stats.learningTrend" 
              :key="index" 
              class="trend-bar-item">
              <div 
                class="trend-bar-inner" 
                :style="{ height: item.minutes > 0 ? Math.min(item.minutes / 60 * 100, 100) + '%' : '5%', 
                         background: getBarColor(item.minutes) }">
              </div>
              <div class="trend-bar-value">{{ item.minutes > 0 ? item.minutes + '分钟' : '0' }}</div>
              <div class="trend-bar-date">{{ formatTrendDate(item.date) }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 教学评价对话框 -->
    <CourseEvaluationDialog
      v-model="evaluationDialog.visible"
      :course-id="evaluationDialog.courseId"
      @success="handleEvaluationSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getEnrolledCourses, getSubjectList } from '@/api/course'
import { submitEvaluation } from '@/api/evaluation'
import { recordBehavior } from '@/api/resource'
import { Search, Picture, Calendar, Clock, Collection, SuccessFilled, Timer, ArrowDown, User } from '@element-plus/icons-vue'
import CourseEvaluationDialog from './components/CourseEvaluationDialog.vue'

const router = useRouter()
const loading = ref(false)
const courseList = ref([])
const displayCourses = computed(() => {
  let filtered = [...courseList.value]
  
  // 关键词筛选
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(course => 
      (course.courseName && course.courseName.toLowerCase().includes(keyword)) || 
      (course.description && course.description.toLowerCase().includes(keyword)) ||
      (course.courseCode && course.courseCode.toLowerCase().includes(keyword))
    )
  }
  
  // 状态筛选
  if (filterStatus.value) {
    filtered = filtered.filter(course => course.completionStatus === filterStatus.value)
  }
  
  // 学科筛选
  if (filterSubject.value) {
    filtered = filtered.filter(course => String(course.subjectId) === String(filterSubject.value))
  }
  
  // 排序
  if (sortOption.value) {
    filtered.sort((a, b) => {
      switch (sortOption.value) {
        case 'last_learn':
          return new Date(b.lastViewTime || 0) - new Date(a.lastViewTime || 0)
        case 'enroll_time_asc':
          return new Date(a.enrollmentDate || a.createTime) - new Date(b.enrollmentDate || b.createTime)
        case 'enroll_time_desc':
          return new Date(b.enrollmentDate || b.createTime) - new Date(a.enrollmentDate || a.createTime)
        case 'progress':
          return (b.progress || 0) - (a.progress || 0)
        default:
          return 0
      }
    })
  }
  
  return filtered
})

const total = ref(0)
const subjectOptions = ref([])
const defaultCover = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'
const isPaginated = ref(false) // 判断是否是分页数据

// 搜索和筛选
const searchKeyword = ref('')
const filterStatus = ref('')
const filterSubject = ref('')
const sortOption = ref('last_learn')

const query = reactive({
  pageNum: 1,
  pageSize: 8,
  keyword: '',
  status: '',
  subjectId: '',
  sort: 'last_learn'
})

// 学习统计数据
const stats = reactive({
  totalCourses: 0,
  completedCourses: 0,
  totalStudyTime: 0,
  // 示例数据，实际应从后端获取
  learningTrend: generateLast7DaysData()
})

// 生成最近7天的日期数据
function generateLast7DaysData() {
  const result = []
  const today = new Date()
  
  for (let i = 6; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(today.getDate() - i)
    
    const formattedDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    
    result.push({
      date: formattedDate,
      minutes: Math.floor(Math.random() * 120) // 模拟数据
    })
  }
  
  return result
}

onMounted(async () => {
  await fetchSubjects()
  await fetchCourses()
})

// 获取课程分类
const fetchSubjects = async () => {
  try {
    const result = await getSubjectList()
    if (result && result.data) {
      subjectOptions.value = result.data
    }
  } catch (error) {
    console.error('获取课程分类失败', error)
  }
}

// 获取已选课程
const fetchCourses = async () => {
  loading.value = true
  try {
    // 将筛选条件应用到查询参数
    query.keyword = searchKeyword.value
    query.status = filterStatus.value
    query.subjectId = filterSubject.value
    query.sort = sortOption.value
    
    const result = await getEnrolledCourses(query)
    
    if (result && result.code === 200) {
      // 确定数据结构
      if (Array.isArray(result.data)) {
        // 如果后端直接返回数组
        courseList.value = result.data.map(course => {
          // 添加默认的学习进度数据（实际应该从后端获取）
          return {
            ...course,
            progress: Math.floor(Math.random() * 100),
            lastViewTime: generateRandomRecentDate(),
            completionStatus: getRandomStatus()
          }
        })
        total.value = courseList.value.length
        isPaginated.value = false
      } else if (result.data && result.data.records) {
        // 如果后端返回的是分页数据
        courseList.value = result.data.records.map(course => {
          return {
            ...course,
            progress: Math.floor(Math.random() * 100),
            lastViewTime: generateRandomRecentDate(),
            completionStatus: getRandomStatus()
          }
        })
        total.value = result.data.total
        isPaginated.value = true
      }
      
      // 更新统计数据
      updateStatistics()
    }
  } catch (error) {
    console.error('获取课程失败', error)
    ElMessage.error('获取已选课程失败')
  } finally {
    loading.value = false
  }
}

// 生成随机近期日期（用于演示）
const generateRandomRecentDate = () => {
  const now = new Date()
  const daysAgo = Math.floor(Math.random() * 30) // 0-30天前
  const date = new Date(now)
  date.setDate(now.getDate() - daysAgo)
  return date.toISOString()
}

// 生成随机状态（用于演示）
const getRandomStatus = () => {
  const statuses = ['not_started', 'in_progress', 'completed']
  const idx = Math.floor(Math.random() * 3)
  return statuses[idx]
}

// 更新统计数据
const updateStatistics = () => {
  stats.totalCourses = courseList.value.length
  stats.completedCourses = courseList.value.filter(c => c.completionStatus === 'completed').length
  
  // 这里应该从后端获取实际学习时间，这里使用模拟数据
  stats.totalStudyTime = 3600 * (Math.floor(Math.random() * 10) + 1) // 模拟1-10小时
}

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

// 处理搜索
const handleSearch = () => {
  if (isPaginated.value) {
    query.pageNum = 1 // 重置到第一页
    fetchCourses()
  }
}

// 选择学科分类
const selectSubject = (subjectId) => {
  filterSubject.value = subjectId
  handleSearch()
}

// 分页处理
const handleSizeChange = (val) => {
  query.pageSize = val
  query.pageNum = 1
  fetchCourses()
}

const handleCurrentChange = (val) => {
  query.pageNum = val
  fetchCourses()
}

// 跳转到课程详情
const goToCourseDetail = (courseId) => {
  router.push(`/course/${courseId}`)
}

// 开始学习
const startLearning = (course) => {
  // 记录学习行为
  recordBehavior({
    courseId: course.courseId,
    behaviorType: 'start_learning'
  }).catch(error => console.error('记录行为失败', error))
  
  router.push(`/course/${course.courseId}/learn`)
}

// 前往课程中心
const goToCourseCenter = () => {
  router.push('/courses')
}

// 处理下拉菜单命令
const handleCommand = (command, course) => {
  switch (command) {
    case 'evaluate':
      if (!course.courseId) {
        ElMessage.error('课程ID不能为空')
        return
      }
      evaluationDialog.courseId = course.courseId
      evaluationDialog.visible = true
      break
    case 'assignments':
      router.push(`/course/${course.courseId}/assignments`)
      break
    case 'exams':
      router.push(`/course/${course.courseId}/exams`)
      break
    case 'resources':
      router.push(`/course/${course.courseId}/resources`)
      break
    case 'discussion':
      router.push(`/discussion/course/${course.courseId}`)
      break
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
}

// 格式化趋势图日期（只显示月-日）
const formatTrendDate = (dateStr) => {
  if (!dateStr) return ''
  
  const parts = dateStr.split('-')
  if (parts.length === 3) {
    return `${parts[1]}-${parts[2]}`
  }
  return dateStr
}

// 格式化时间
const formatTime = (seconds) => {
  if (!seconds) return '0分钟'
  
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  
  if (hours > 0) {
    return `${hours}小时${minutes > 0 ? minutes + '分钟' : ''}`
  } else {
    return `${minutes}分钟`
  }
}

// 获取进度颜色
const getProgressColor = (progress) => {
  if (progress >= 100) return '#67c23a'
  if (progress >= 60) return '#409eff'
  if (progress >= 30) return '#e6a23c'
  return '#909399'
}

// 获取柱状图颜色
const getBarColor = (minutes) => {
  if (minutes >= 90) return '#67c23a'
  if (minutes >= 60) return '#409eff'
  if (minutes >= 30) return '#e6a23c'
  return '#909399'
}

// 获取标签类型
const getTagType = (subjectId) => {
  const types = ['', 'success', 'warning', 'danger', 'info']
  return types[subjectId % types.length]
}

// 获取课程状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'completed': return '已完成'
    case 'in_progress': return '进行中'
    case 'not_started': return '未开始'
    default: return '进行中'
  }
}

// 获取课程状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 'completed': return 'success'
    case 'in_progress': return 'primary'
    case 'not_started': return 'info'
    default: return 'info'
  }
}

// 获取选课状态文本
const getEnrollmentStatusText = (status) => {
  switch (status) {
    case 'open': return '选课中'
    case 'closed': return '已结课'
    case 'upcoming': return '即将开始'
    default: return ''
  }
}

// 获取选课状态标签类型
const getEnrollmentStatusType = (status) => {
  switch (status) {
    case 'open': return 'success'
    case 'closed': return 'info'
    case 'upcoming': return 'warning'
    default: return 'info'
  }
}

// 评价对话框相关
const evaluationDialog = reactive({
  visible: false,
  courseId: null
})

// 评价成功回调
const handleEvaluationSuccess = () => {
      ElMessage.success('评价提交成功')
  fetchCourses() // 刷新课程列表
}
</script>

<style scoped>
.my-courses-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  justify-content: space-between;
}

.search-box {
  flex: 1;
  min-width: 200px;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.subject-tags {
  margin-bottom: 20px;
}

.tags-container {
  display: flex;
  gap: 10px;
  padding: 5px 0;
}

.filter-tag {
  cursor: pointer;
  user-select: none;
}

.course-list {
  margin-bottom: 30px;
}

.course-card-wrapper {
  height: 100%;
  margin-bottom: 24px;
}

.course-card {
  height: 100%;
  transition: all 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.1);
}

.course-content {
  padding: 16px;
  cursor: pointer;
  flex: 1;
}

.course-img {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.course-img .el-image {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 24px;
}

.course-progress-badge {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  padding: 3px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.course-info {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.course-subject {
  font-size: 12px;
  color: #409eff;
  margin-bottom: 8px;
}

.course-title {
  font-size: 16px;
  margin: 0 0 12px 0;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-status {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.course-meta {
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 12px;
  margin-bottom: 6px;
}

.course-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-progress-bar {
  margin-bottom: 16px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 12px;
  color: #606266;
}

.course-actions {
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 8px;
  background-color: #fff;
}

.statistics-card {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #ecf5ff;
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 12px;
}

.completed-icon {
  background-color: #f0f9eb;
  color: #67c23a;
}

.time-icon {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.learning-trends {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.learning-trends h3 {
  font-size: 16px;
  margin-bottom: 20px;
  color: #303133;
}

.trend-chart {
  height: 200px;
  margin-top: 16px;
}

.trend-bars {
  display: flex;
  justify-content: space-between;
  height: 100%;
  align-items: flex-end;
}

.trend-bar-item {
  flex: 1;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.trend-bar-inner {
  width: 24px;
  border-radius: 4px;
  background-color: #409eff;
  margin-bottom: 8px;
  transition: height 0.3s;
}

.trend-bar-value {
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
}

.trend-bar-date {
  font-size: 12px;
  color: #909399;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.empty-course {
  margin: 60px 0;
}

@media (max-width: 768px) {
  .filter-container {
    flex-direction: column;
  }
  
  .search-box {
    margin-bottom: 10px;
  }
  
  .filter-options {
    flex-direction: column;
  }
  
  .trend-bar-inner {
    width: 18px;
  }
  
  .trend-bar-value {
    font-size: 10px;
  }
  
  .trend-bar-date {
    font-size: 10px;
  }
  
  .stat-item {
    margin-bottom: 30px;
  }
}

/* 评价表单样式 */
.evaluation-course-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.course-info-text {
  margin-left: 15px;
}

.course-info-text h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.course-info-text p {
  margin: 0;
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.course-info-text p .el-icon {
  margin-right: 5px;
}

.anonymous-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
</style> 