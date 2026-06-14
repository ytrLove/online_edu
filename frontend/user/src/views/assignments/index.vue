<template>
  <div class="assignment-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">作业与考试</h1>
          <p class="page-subtitle">管理您的学习任务，及时完成作业和考试</p>
        </div>
        <div class="header-decoration">
          <div class="decoration-icon">
            <el-icon><Document /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-card">
        <div class="search-header">
          <div class="search-title">
            <h2>筛选条件</h2>
            <p>根据课程和类型筛选您的学习任务</p>
          </div>
          <div class="search-decoration">
            <div class="decoration-icon">
              <el-icon><Search /></el-icon>
            </div>
          </div>
        </div>
        
        <el-form :model="searchForm" class="search-form">
          <div class="search-inputs">
            <div class="input-group">
              <div class="input-wrapper">
                <div class="input-icon">
                  <el-icon><Grid /></el-icon>
                </div>
                <el-select
                  v-model="searchForm.courseId"
                  placeholder="选择课程"
                  clearable
                  class="search-select"
                >
                  <el-option
                    v-for="item in courseOptions"
                    :key="item.courseId"
                    :label="item.courseName"
                    :value="item.courseId"
                  />
                </el-select>
              </div>
              
              <div class="input-wrapper">
                <div class="input-icon">
                  <el-icon><Filter /></el-icon>
                </div>
                <el-select
                  v-model="searchForm.type"
                  placeholder="选择类型"
                  clearable
                  class="search-select"
                >
                  <el-option label="全部" value="" />
                  <el-option label="作业" value="assignment" />
                  <el-option label="考试" value="exam" />
                </el-select>
              </div>
              
              <div class="search-actions">
                <el-button type="primary" class="search-btn" @click="handleSearch">
                  <el-icon><Search /></el-icon>
                  查询
                </el-button>
                <el-button class="reset-btn" @click="resetForm">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </div>
            </div>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="content-section">
      <div class="content-card">
        <!-- 状态筛选标签 -->
        <div class="status-filters">
          <div class="filter-header">
            <h3>任务状态</h3>
            <span class="filter-count">管理您的学习任务</span>
          </div>
          <div class="filter-tabs">
            <div
              class="filter-tab"
              :class="{ active: activeTab === 'all' }"
              @click="activeTab = 'all'; handleTabChange()"
            >
              <span class="tab-icon">📋</span>
              <span class="tab-text">全部</span>
            </div>
            <div
              class="filter-tab"
              :class="{ active: activeTab === 'todo' }"
              @click="activeTab = 'todo'; handleTabChange()"
            >
              <span class="tab-icon">⏰</span>
              <span class="tab-text">未完成</span>
            </div>
            <div
              class="filter-tab"
              :class="{ active: activeTab === 'done' }"
              @click="activeTab = 'done'; handleTabChange()"
            >
              <span class="tab-icon">✅</span>
              <span class="tab-text">已完成</span>
            </div>
          </div>
        </div>
        
        <!-- 内容类型切换器 -->
        <div class="content-type-section">
          <div class="type-switcher">
            <div class="switcher-header">
              <h3>任务类型</h3>
            </div>
            <el-radio-group v-model="contentType" @change="handleContentTypeChange" class="content-type-switch">
              <el-radio-button label="assignment" class="type-button">
                <span class="button-icon">📝</span>
                <span class="button-text">作业任务</span>
              </el-radio-button>
              <el-radio-button label="exam" class="type-button">
                <span class="button-icon">🎯</span>
                <span class="button-text">考试安排</span>
              </el-radio-button>
            </el-radio-group>
          </div>
        </div>
      
        <!-- 作业列表 -->
        <div v-if="contentType === 'assignment'" class="assignment-section">
          <div v-if="assignmentList.length === 0 && !loading" class="empty-container">
            <div class="empty-state">
              <div class="empty-icon">
                <el-icon><Document /></el-icon>
              </div>
              <h3>暂无作业数据</h3>
              <p>暂时没有找到符合条件的作业，试试其他筛选条件吧</p>
            </div>
          </div>
          
          <el-skeleton :rows="4" animated v-if="loading" class="skeleton-container" />
          
          <div v-else class="assignment-grid">
            <div v-for="item in assignmentList" :key="item.assignmentId" class="assignment-card-wrapper">
              <div class="assignment-card hover-lift">
                <!-- 作业卡片头部 -->
                <div class="assignment-header">
                  <div class="header-left">
                    <div class="course-info">
                      <div class="course-badge">
                        <span class="badge-icon">📚</span>
                        <span class="badge-text">{{ item.courseName }}</span>
                      </div>
                    </div>
                    <div class="difficulty-tags">
                      <el-tag 
                        v-for="(count, difficulty) in getDifficultyCount(item.questions)"
                        :key="difficulty"
                        :type="getDifficultyTagType(difficulty)"
                        size="small"
                        effect="light"
                        class="difficulty-tag"
                      >
                        <span class="difficulty-icon">{{ getDifficultyIcon(difficulty) }}</span>
                        {{ getDifficultyText(difficulty) }} x{{ count }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="status-badge" :class="getStatusClass(item)">
                    <span class="status-icon">{{ getStatusIcon(item) }}</span>
                    <span class="status-text">{{ getStatusText(item) }}</span>
                  </div>
                </div>
                
                <!-- 作业内容 -->
                <div class="assignment-content">
                  <div class="assignment-title" @click="handleViewAssignment(item.assignmentId)">
                    {{ item.title }}
                    <div v-if="item.description" class="assignment-description">
                      {{ item.description }}
                    </div>
                  </div>
                  
                  <div class="question-summary" v-if="item.questions && item.questions.length > 0">
                    <div class="summary-header">
                      <el-icon><List /></el-icon>
                      <span>题目分布</span>
                    </div>
                    <div class="question-types">
                      <div class="question-type" v-for="(count, type) in getQuestionTypeCount(item.questions)" :key="type">
                        <span class="type-dot"></span>
                        {{ type }}: {{ count }}题
                      </div>
                    </div>
                  </div>
                
                  <div class="assignment-meta">
                    <div class="meta-item">
                      <el-icon class="meta-icon"><Calendar /></el-icon>
                      <span class="meta-label">开始时间:</span>
                      <span class="meta-value">{{ formatDate(item.startTime) }}</span>
                    </div>
                    
                    <div class="meta-item">
                      <el-icon class="meta-icon"><Timer /></el-icon>
                      <span class="meta-label">截止时间:</span>
                      <span class="meta-value">{{ formatDate(item.dueTime) }}</span>
                    </div>
                  
                    <div class="meta-item">
                      <el-icon class="meta-icon"><Document /></el-icon>
                      <span class="meta-label">题目数量:</span>
                      <span class="meta-value">{{ item.questions ? item.questions.length : 0 }}题</span>
                    </div>
                    
                    <div class="meta-item">
                      <el-icon class="meta-icon"><Trophy /></el-icon>
                      <span class="meta-label">总分:</span>
                      <span class="meta-value points">{{ item.totalPoints }}分</span>
                    </div>
                    
                    <div v-if="item.score !== null" class="meta-item score-item">
                      <el-icon class="meta-icon"><StarFilled /></el-icon>
                      <span class="meta-label">得分:</span>
                      <span class="meta-value score">{{ item.score }}分</span>
                    </div>
                  </div>
                  
                  <!-- 进度显示 -->
                  <div v-if="item.deadlineStatus === 'in_progress'" class="progress-section">
                    <div class="progress-header">
                      <span class="progress-title">剩余时间</span>
                      <span class="progress-time">{{ item.remainingHours ? formatRemainingTime(item.remainingHours) : '进行中' }}</span>
                    </div>
                    <el-progress 
                      :percentage="getSubmissionProgress(item)" 
                      :status="getProgressStatus(item)"
                      :stroke-width="8"
                      :show-text="false"
                      class="assignment-progress"
                    />
                  </div>
                </div>
                
                <!-- 作业底部 -->
                <div class="assignment-footer">
                  <div class="footer-left">
                    <div v-if="item.submissionStatus === 'submitted'" class="submission-info">
                      <el-icon class="submit-icon"><Check /></el-icon>
                      <span class="submit-text">已提交</span>
                    </div>
                  </div>
                  <div class="footer-right">
                    <el-button 
                      :type="getActionButtonType(item)" 
                      size="default"
                      class="action-btn"
                      @click="handleViewAssignment(item.assignmentId)"
                    >
                      <el-icon><Reading /></el-icon>
                      {{ getActionText(item) }}
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      
        <!-- 考试列表 -->
        <div v-else-if="contentType === 'exam'" class="exam-section">
          <div v-if="examList.length === 0 && !loading" class="empty-container">
            <div class="empty-state">
              <div class="empty-icon">
                <el-icon><Trophy /></el-icon>
              </div>
              <h3>暂无考试数据</h3>
              <p>暂时没有安排的考试，请留意老师的考试通知</p>
            </div>
          </div>
          
          <el-skeleton :rows="4" animated v-if="loading" class="skeleton-container" />
          
          <div v-else class="exam-grid">
            <div class="exam-card-wrapper" v-for="exam in examList" :key="exam.examId">
              <div class="exam-card hover-lift">
                <!-- 考试卡片头部 -->
                <div class="exam-header">
                  <div class="exam-title-section">
                    <div class="title-wrapper">
                      <h3 class="exam-title" @click="handleViewExam(exam.examId)">
                        <span class="title-icon">🎯</span>
                        {{ exam.title }}
                      </h3>
                    </div>
                    <div class="exam-badges">
                      <div class="course-badge primary">
                        <span class="badge-icon">📚</span>
                        {{ exam.courseName }}
                      </div>
                      <div v-if="exam.teacherName" class="teacher-badge">
                        <span class="badge-icon">👨‍🏫</span>
                        {{ exam.teacherName }}
                      </div>
                    </div>
                    <div class="exam-status-tags">
                      <el-tag v-if="isExamActive(exam)" type="warning" size="small" class="status-tag active">
                        <span class="tag-icon">⚡</span>
                        进行中
                      </el-tag>
                      <el-tag v-else-if="isExamExpired(exam)" type="info" size="small" class="status-tag expired">
                        <span class="tag-icon">✅</span>
                        已结束
                      </el-tag>
                      <el-tag v-else-if="isExamUpcoming(exam)" type="primary" size="small" class="status-tag upcoming">
                        <span class="tag-icon">🕰️</span>
                        未开始
                      </el-tag>
                    </div>
                  </div>
                  
                  <div class="exam-score-section">
                    <div class="score-display">
                      <span class="score-label">总分</span>
                      <span class="score-value">{{ exam.totalPoints }}</span>
                      <span class="score-unit">分</span>
                    </div>
                  </div>
                </div>
                
                <!-- 考试信息 -->
                <div class="exam-content">
                  <div class="exam-description" v-if="exam.description">
                    <div class="description-header">
                      <el-icon><Document /></el-icon>
                      <span>考试说明</span>
                    </div>
                    <p class="description-text">{{ exam.description }}</p>
                  </div>
                  
                  <div class="exam-meta">
                    <div class="meta-item">
                      <el-icon class="meta-icon"><Calendar /></el-icon>
                      <div class="meta-content">
                        <span class="meta-label">考试时间</span>
                        <span class="meta-value">{{ formatDate(exam.startTime) }}</span>
                        <span class="meta-separator">~</span>
                        <span class="meta-value">{{ formatDate(exam.endTime) }}</span>
                      </div>
                    </div>
                    
                    <div class="meta-item">
                      <el-icon class="meta-icon"><Timer /></el-icon>
                      <div class="meta-content">
                        <span class="meta-label">考试时长</span>
                        <span class="meta-value duration">{{ exam.duration }}分钟</span>
                      </div>
                    </div>
                    
                    <div class="meta-item">
                      <el-icon class="meta-icon"><Document /></el-icon>
                      <div class="meta-content">
                        <span class="meta-label">题目数量</span>
                        <span class="meta-value">{{ exam.questions ? exam.questions.length : 0 }}题</span>
                      </div>
                    </div>
                    
                    <div class="meta-item question-distribution">
                      <el-icon class="meta-icon"><List /></el-icon>
                      <div class="meta-content">
                        <span class="meta-label">题型分布</span>
                        <div class="question-types-detail">
                          {{ getQuestionTypesSummary(exam.questions) }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- 考试底部 -->
                <div class="exam-footer">
                  <div class="footer-info">
                    <div v-if="isExamUpcoming(exam)" class="remaining-time upcoming">
                      <el-icon class="time-icon"><AlarmClock /></el-icon>
                      <span class="time-text">距离开始: {{ getTimeUntilStart(exam) }}</span>
                    </div>
                    
                    <div v-else-if="isExamActive(exam)" class="remaining-time active">
                      <el-icon class="time-icon"><Timer /></el-icon>
                      <span class="time-text">距离结束: {{ getTimeUntilEnd(exam) }}</span>
                    </div>
                    
                    <div v-else class="remaining-time finished">
                      <el-icon class="time-icon"><Check /></el-icon>
                      <span class="time-text">考试已结束</span>
                    </div>
                  </div>
                  
                  <div class="footer-actions">
                    <el-button 
                      type="primary" 
                      size="default"
                      class="exam-action-btn"
                      @click="handleViewExam(exam.examId)"
                      :disabled="!canTakeExam(exam)"
                    >
                      <el-icon><Reading /></el-icon>
                      {{ getExamActionText(exam) }}
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 考试分页 -->
            <div class="pagination-container" v-if="examTotal > 0">
              <el-pagination
                background
                layout="total, sizes, prev, pager, next, jumper"
                :total="examTotal"
                :current-page="examPageNum"
                :page-size="examPageSize"
                :page-sizes="[10, 20, 50]"
                @current-change="handleExamCurrentChange"
                @size-change="handleExamSizeChange"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Calendar, Trophy, Document, Timer, StarFilled, List, AlarmClock, Check, Search, Grid, Filter, Refresh, Reading } from '@element-plus/icons-vue'
import { 
  getStudentAssignments, 
  getStudentExams,
  getStudentCourses
} from '@/api/assignment'

const router = useRouter()

// 页面数据
const loading = ref(false)
const activeTab = ref('all')
const contentType = ref('assignment')
const assignmentList = ref([])
const examList = ref([])
const courseOptions = ref([])

// 考试分页数据
const examPageNum = ref(1)
const examPageSize = ref(10)
const examTotal = ref(0)

// 搜索表单
const searchForm = reactive({
  courseId: '',
  type: ''
})

// 日期格式化
function formatDate(dateString) {
  if (!dateString) return '未设置'
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 格式化剩余时间
function formatRemainingTime(hours) {
  if (!hours) return ''
  if (hours < 1) {
    return '不足1小时'
  } else if (hours < 24) {
    return `${Math.floor(hours)}小时`
  } else {
    const days = Math.floor(hours / 24)
    const remainingHours = Math.floor(hours % 24)
    return `${days}天${remainingHours > 0 ? remainingHours + '小时' : ''}`
  }
}

// 获取作业列表
const fetchAssignments = async () => {
  loading.value = true
  try {
    const res = await getStudentAssignments()
    if (res.code === 200) {
      // 根据当前标签页过滤数据
      const allAssignments = res.data || []
      
      if (activeTab.value === 'all') {
        assignmentList.value = allAssignments
      } else if (activeTab.value === 'todo') {
        assignmentList.value = allAssignments.filter(item => 
          item.deadlineStatus === 'in_progress' || 
          (item.submissionStatus !== 'submitted' && item.deadlineStatus !== 'expired')
        )
      } else if (activeTab.value === 'done') {
        assignmentList.value = allAssignments.filter(item => 
          item.submissionStatus === 'submitted' || 
          item.deadlineStatus === 'expired'
        )
      }
      
      // 根据课程ID过滤
      if (searchForm.courseId) {
        assignmentList.value = assignmentList.value.filter(item => 
          item.courseId === searchForm.courseId
        )
      }
    } else {
      ElMessage.error(res.message || '获取作业列表失败')
    }
  } catch (error) {
    console.error('获取作业列表失败', error)
    ElMessage.error('获取作业列表失败')
  } finally {
    loading.value = false
  }
}

// 获取考试列表
const fetchExams = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: examPageNum.value,
      pageSize: examPageSize.value
    }
    
    // 如果选择了课程，添加课程过滤条件
    if (searchForm.courseId) {
      params.courseId = searchForm.courseId
    }
    
    const res = await getStudentExams(params)
    if (res.code === 200) {
      examList.value = res.data.records || []
      examTotal.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取考试列表失败')
    }
  } catch (error) {
    console.error('获取考试列表失败', error)
    ElMessage.error('获取考试列表失败')
  } finally {
    loading.value = false
  }
}

// 获取课程选项
const fetchCourseOptions = async () => {
  try {
    const res = await getStudentCourses()
    if (res.code === 200) {
      courseOptions.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取课程选项失败')
    }
  } catch (error) {
    console.error('获取课程选项失败', error)
    ElMessage.error('获取课程选项失败')
  }
}

// 处理搜索
const handleSearch = () => {
  if (contentType.value === 'assignment') {
    fetchAssignments()
  } else {
    examPageNum.value = 1
    fetchExams()
  }
}

// 重置表单
const resetForm = () => {
  searchForm.courseId = ''
  searchForm.type = ''
  
  if (contentType.value === 'assignment') {
    fetchAssignments()
  } else {
    examPageNum.value = 1
    fetchExams()
  }
}

// 处理标签页切换
const handleTabChange = () => {
  if (contentType.value === 'assignment') {
    fetchAssignments()
  } else {
    examPageNum.value = 1
    fetchExams()
  }
}

// 处理内容类型切换
const handleContentTypeChange = () => {
  if (contentType.value === 'assignment') {
    fetchAssignments()
  } else {
    fetchExams()
  }
}

// 处理考试分页
const handleExamCurrentChange = (val) => {
  examPageNum.value = val
  fetchExams()
}

// 处理考试每页条数变化
const handleExamSizeChange = (val) => {
  examPageSize.value = val
  examPageNum.value = 1
  fetchExams()
}

// 查看作业详情
const handleViewAssignment = (id) => {
  router.push(`/assignment/${id}`)
}

// 查看考试详情
const handleViewExam = (id) => {
  router.push(`/exam/${id}`)
}

// 获取作业状态文本
const getStatusText = (assignment) => {
  if (assignment.deadlineStatus === 'expired') {
    return assignment.submissionStatus === 'submitted' ? '已提交(已过期)' : '未提交(已过期)'
  }
  
  if (assignment.submissionStatus === 'submitted') {
    return assignment.score !== null ? '已批改' : '已提交'
  }
  
  return assignment.deadlineStatus === 'in_progress' ? '进行中' : '未开始'
}

// 获取作业状态样式类名
const getStatusClass = (assignment) => {
  if (assignment.deadlineStatus === 'expired') {
    return assignment.submissionStatus === 'submitted' ? 'status-submitted' : 'status-expired'
  }
  
  if (assignment.submissionStatus === 'submitted') {
    return assignment.score !== null ? 'status-graded' : 'status-submitted'
  }
  
  return assignment.deadlineStatus === 'in_progress' ? 'status-in-progress' : 'status-not-started'
}

// 获取作业操作按钮文本
const getActionText = (assignment) => {
  if (assignment.deadlineStatus === 'expired') {
    return assignment.submissionStatus === 'submitted' ? '查看批改' : '查看详情'
  }
  
  if (assignment.submissionStatus === 'submitted') {
    return assignment.score !== null ? '查看批改' : '查看提交'
  }
  
  return '开始作业'
}

// 判断考试是否正在进行中
const isExamActive = (exam) => {
  const now = new Date()
  const startTime = new Date(exam.startTime)
  const endTime = new Date(exam.endTime)
  return now >= startTime && now <= endTime
}

// 判断考试是否已结束
const isExamExpired = (exam) => {
  const now = new Date()
  const endTime = new Date(exam.endTime)
  return now > endTime
}

// 判断考试是否未开始
const isExamUpcoming = (exam) => {
  const now = new Date()
  const startTime = new Date(exam.startTime)
  return now < startTime
}

// 获取距离考试开始的时间
const getTimeUntilStart = (exam) => {
  const now = new Date()
  const startTime = new Date(exam.startTime)
  const diffMs = startTime - now
  
  if (diffMs <= 0) return '已开始'
  
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
  const diffHours = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60))
  
  if (diffDays > 0) {
    return `${diffDays}天${diffHours}小时`
  } else if (diffHours > 0) {
    return `${diffHours}小时${diffMinutes}分钟`
  } else {
    return `${diffMinutes}分钟`
  }
}

// 获取距离考试结束的时间
const getTimeUntilEnd = (exam) => {
  const now = new Date()
  const endTime = new Date(exam.endTime)
  const diffMs = endTime - now
  
  if (diffMs <= 0) return '已结束'
  
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60))
  
  if (diffHours > 0) {
    return `${diffHours}小时${diffMinutes}分钟`
  } else {
    return `${diffMinutes}分钟`
  }
}

// 获取题型分布概要
const getQuestionTypesSummary = (questions) => {
  if (!questions || questions.length === 0) return '暂无题目'
  
  const typeCount = {}
  questions.forEach(q => {
    if (q.typeName) {
      typeCount[q.typeName] = (typeCount[q.typeName] || 0) + 1
    }
  })
  
  return Object.entries(typeCount)
    .map(([type, count]) => `${type} ${count}题`)
    .join('，')
}

// 判断是否可以参加考试
const canTakeExam = (exam) => {
  return isExamActive(exam) || isExamUpcoming(exam)
}

// 获取考试操作按钮文本
const getExamActionText = (exam) => {
  if (isExamExpired(exam)) {
    return '查看详情'
  } else if (isExamActive(exam)) {
    return '开始考试'
  } else {
    return '查看详情'
  }
}

// 获取题目难度统计
const getDifficultyCount = (questions) => {
  if (!questions) return {}
  return questions.reduce((acc, q) => {
    acc[q.difficulty] = (acc[q.difficulty] || 0) + 1
    return acc
  }, {})
}

// 获取题目类型统计
const getQuestionTypeCount = (questions) => {
  if (!questions) return {}
  return questions.reduce((acc, q) => {
    acc[q.typeName] = (acc[q.typeName] || 0) + 1
    return acc
  }, {})
}

// 获取难度标签类型
const getDifficultyTagType = (difficulty) => {
  const types = {
    easy: 'success',
    medium: 'warning',
    hard: 'danger'
  }
  return types[difficulty] || 'info'
}

// 获取提交进度
const getSubmissionProgress = (item) => {
  if (!item.remainingHours) return 100
  const totalDuration = new Date(item.dueTime) - new Date(item.startTime)
  const remainingDuration = item.remainingHours * 60 * 60 * 1000
  return Math.round((1 - remainingDuration / totalDuration) * 100)
}

// 获取进度条状态
const getProgressStatus = (item) => {
  if (item.remainingHours <= 24) return 'exception'
  if (item.remainingHours <= 72) return 'warning'
  return ''
}

// 获取操作按钮类型
const getActionButtonType = (item) => {
  if (item.deadlineStatus === 'expired') return 'info'
  if (item.submissionStatus === 'submitted') return 'success'
  if (item.deadlineStatus === 'in_progress' && item.remainingHours <= 24) return 'warning'
  return 'primary'
}

// 获取难度图标
const getDifficultyIcon = (difficulty) => {
  const icons = {
    easy: '🟢',
    medium: '🟡', 
    hard: '🔴'
  }
  return icons[difficulty] || '⚪'
}

// 获取难度文本
const getDifficultyText = (difficulty) => {
  const texts = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return texts[difficulty] || difficulty
}

// 获取状态图标
const getStatusIcon = (item) => {
  if (item.deadlineStatus === 'expired') {
    return item.submissionStatus === 'submitted' ? '✅' : '❌'
  }
  if (item.submissionStatus === 'submitted') {
    return item.score !== null ? '🎯' : '📝'
  }
  return item.deadlineStatus === 'in_progress' ? '⏳' : '📅'
}

// 生命周期钩子
onMounted(() => {
  fetchCourseOptions()
  fetchAssignments()
})
</script>

<style lang="scss" scoped>
.assignment-container {
  padding: var(--container-padding);
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 140px);
}

/* 页面标题区域 */
.page-header {
  margin-bottom: 32px;
}

.header-content {
  background: var(--vt-gradient-primary);
  border-radius: var(--vt-radius-large);
  padding: 40px;
  color: white;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--vt-shadow-3);
}

.header-text {
  flex: 1;
  z-index: 2;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
}

.page-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
  text-shadow: 0 1px 10px rgba(0, 0, 0, 0.2);
}

.header-decoration {
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.header-decoration .decoration-icon {
  font-size: 48px;
  opacity: 0.8;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 32px;
}

.search-card {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 32px;
  box-shadow: var(--vt-shadow-2);
  border: 1px solid var(--color-border-soft);
  backdrop-filter: blur(20px);
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.search-title h2 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: var(--color-text);
}

.search-title p {
  margin: 0;
  color: var(--color-text-soft);
  font-size: 14px;
}

.search-decoration {
  width: 60px;
  height: 60px;
  background: var(--vt-gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--vt-shadow-1);
}

.search-decoration .decoration-icon {
  font-size: 24px;
  color: white;
}

.search-inputs {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  align-items: end;
}

.input-wrapper {
  position: relative;
  flex: 1;
  min-width: 200px;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-soft);
  z-index: 2;
  font-size: 18px;
}

.search-select {
  width: 100%;
}

:deep(.search-select .el-input__wrapper) {
  padding-left: 48px;
  border-radius: var(--vt-radius-small);
  border: 2px solid var(--color-border-soft);
  background: var(--color-background);
  transition: all var(--vt-transition-fast) ease;
}

:deep(.search-select .el-input__wrapper:hover) {
  border-color: var(--vt-c-primary);
  box-shadow: 0 0 0 3px var(--vt-c-primary-light);
}

.search-actions {
  display: flex;
  gap: 12px;
}

.search-btn, .reset-btn {
  padding: 12px 24px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.search-btn {
  background: var(--vt-gradient-primary) !important;
  border: none !important;
  color: white !important;
  box-shadow: var(--vt-shadow-1) !important;
}

.search-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: var(--vt-shadow-2) !important;
}

.reset-btn {
  background: var(--color-background) !important;
  border: 2px solid var(--color-border-soft) !important;
  color: var(--color-text) !important;
}

.reset-btn:hover {
  border-color: var(--vt-c-primary) !important;
  color: var(--vt-c-primary) !important;
  transform: translateY(-2px) !important;
}

/* 主要内容区域 */
.content-section {
  margin-bottom: 32px;
}

.content-card {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 32px;
  box-shadow: var(--vt-shadow-2);
  border: 1px solid var(--color-border-soft);
  backdrop-filter: blur(20px);
}

/* 状态筛选标签 */
.status-filters {
  margin-bottom: 32px;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-header h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: var(--color-text);
}

.filter-count {
  font-size: 14px;
  color: var(--color-text-soft);
}

.filter-tabs {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: var(--vt-radius-small);
  background: var(--color-background);
  border: 2px solid var(--color-border-soft);
  cursor: pointer;
  transition: all var(--vt-transition-fast) ease;
  color: var(--color-text);
  font-weight: 500;
}

.filter-tab:hover {
  border-color: var(--vt-c-primary);
  color: var(--vt-c-primary);
  transform: translateY(-2px);
  box-shadow: var(--vt-shadow-1);
}

.filter-tab.active {
  background: var(--vt-gradient-primary);
  border-color: transparent;
  color: white;
  box-shadow: var(--vt-shadow-2);
}

.tab-icon {
  font-size: 16px;
}

.tab-text {
  font-size: 14px;
}

/* 内容类型切换器 */
.content-type-section {
  margin-bottom: 32px;
}

.type-switcher {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.switcher-header h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: var(--color-text);
}

.content-type-switch {
  display: flex;
  justify-content: center;
}

:deep(.content-type-switch .el-radio-button) {
  margin: 0 8px;
}

:deep(.content-type-switch .el-radio-button__inner) {
  padding: 16px 32px;
  border-radius: var(--vt-radius-small);
  border: 2px solid var(--color-border-soft);
  background: var(--color-background);
  color: var(--color-text);
  font-weight: 500;
  font-size: 16px;
  transition: all var(--vt-transition-fast) ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.content-type-switch .el-radio-button__inner:hover) {
  border-color: var(--vt-c-primary);
  color: var(--vt-c-primary);
  transform: translateY(-2px);
  box-shadow: var(--vt-shadow-1);
}

:deep(.content-type-switch .el-radio-button.is-active .el-radio-button__inner) {
  background: var(--vt-gradient-primary);
  border-color: transparent;
  color: white;
  box-shadow: var(--vt-shadow-2);
}

.type-button .button-icon {
  font-size: 18px;
}

.type-button .button-text {
  font-size: 14px;
}

/* 空状态 */
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80px 20px;
}

.empty-state {
  text-align: center;
  color: var(--color-text-soft);
}

.empty-state .empty-icon {
  font-size: 64px;
  color: var(--color-text-placeholder);
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: var(--color-text);
}

.empty-state p {
  font-size: 14px;
  margin: 0;
  color: var(--color-text-soft);
}

.skeleton-container {
  padding: 20px;
}

/* 作业区域 */
.assignment-section {
  margin-top: 24px;
}

.assignment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
  margin-top: 20px;
}

.assignment-card-wrapper {
  position: relative;
}

.assignment-card {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 24px;
  border: 1px solid var(--color-border-soft);
  transition: all var(--vt-transition-fast) ease;
  height: 100%;
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.assignment-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--vt-gradient-primary);
  opacity: 0;
  transition: opacity var(--vt-transition-fast) ease;
}

.assignment-card.hover-lift:hover {
  transform: translateY(-8px);
  box-shadow: var(--vt-shadow-3);
  border-color: var(--vt-c-primary-light);
}

.assignment-card.hover-lift:hover::before {
  opacity: 1;
}

/* 作业头部 */
.assignment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  gap: 16px;
}

.header-left {
  flex: 1;
}

.course-info {
  margin-bottom: 12px;
}

.course-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--vt-c-primary-light);
  color: white;
  border-radius: var(--vt-radius-small);
  font-size: 14px;
  font-weight: 500;
  box-shadow: var(--vt-shadow-1);
}

.badge-icon {
  font-size: 16px;
}

.difficulty-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.difficulty-tag {
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  display: flex !important;
  align-items: center !important;
  gap: 4px !important;
}

.difficulty-icon {
  font-size: 12px;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: var(--vt-radius-small);
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
}

.status-not-started {
  color: var(--color-text-soft);
  background: var(--color-background-soft);
}

.status-in-progress {
  color: #e6a23c;
  background: #fdf6ec;
}

.status-submitted {
  color: #409EFF;
  background: #ecf5ff;
}

.status-graded {
  color: #67c23a;
  background: #f0f9eb;
}

.status-expired {
  color: #f56c6c;
  background: #fef0f0;
}

/* 作业内容 */
.assignment-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.assignment-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  line-height: 1.4;
  cursor: pointer;
  transition: color var(--vt-transition-fast) ease;
}

.assignment-title:hover {
  color: var(--vt-c-primary);
}

.assignment-description {
  font-size: 14px;
  color: var(--color-text-soft);
  margin-top: 8px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.question-summary {
  background: var(--color-background-soft);
  border-radius: var(--vt-radius-small);
  padding: 16px;
  border: 1px solid var(--color-border-soft);
}

.summary-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-weight: 600;
  color: var(--color-text);
  font-size: 14px;
}

.question-types {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.question-type {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-soft);
}

.type-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--vt-c-primary);
}

.assignment-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  padding: 16px;
  background: var(--color-background-soft);
  border-radius: var(--vt-radius-small);
  border: 1px solid var(--color-border-soft);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.meta-icon {
  color: var(--vt-c-primary);
  font-size: 16px;
  flex-shrink: 0;
}

.meta-label {
  color: var(--color-text-soft);
  white-space: nowrap;
}

.meta-value {
  color: var(--color-text);
  font-weight: 500;
}

.meta-value.points {
  color: var(--vt-c-primary);
}

.meta-value.score {
  color: #67c23a;
}

.score-item {
  grid-column: 1 / -1;
  background: #f0f9eb;
  padding: 8px 12px;
  border-radius: var(--vt-radius-small);
  border: 1px solid #d4e9c7;
}

/* 进度区域 */
.progress-section {
  background: linear-gradient(135deg, #fff8f0 0%, #fef5f0 100%);
  border-radius: var(--vt-radius-small);
  padding: 16px;
  border: 1px solid #f3d19e;
  position: relative;
  overflow: hidden;
}

.progress-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #e6a23c 0%, #f56c6c 100%);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.progress-title {
  font-size: 14px;
  font-weight: 600;
  color: #e6a23c;
}

.progress-time {
  font-size: 13px;
  color: #f56c6c;
  font-weight: 500;
}

.assignment-progress {
  border-radius: var(--vt-radius-small);
}

/* 作业底部 */
.assignment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px solid var(--color-border-soft);
}

.footer-left {
  flex: 1;
}

.submission-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #67c23a;
  font-size: 14px;
  font-weight: 500;
}

.submit-icon {
  font-size: 18px;
}

.footer-right {
  display: flex;
  gap: 12px;
}

.action-btn {
  padding: 10px 20px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.action-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: var(--vt-shadow-2) !important;
}

/* 考试区域 */
.exam-section {
  margin-top: 24px;
}

.exam-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
  margin-top: 20px;
}

.exam-card-wrapper {
  position: relative;
}

.exam-card {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 24px;
  border: 1px solid var(--color-border-soft);
  transition: all var(--vt-transition-fast) ease;
  height: 100%;
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.exam-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #f39c12 0%, #e74c3c 100%);
  opacity: 0;
  transition: opacity var(--vt-transition-fast) ease;
}

.exam-card.hover-lift:hover {
  transform: translateY(-8px);
  box-shadow: var(--vt-shadow-3);
  border-color: #f39c12;
}

.exam-card.hover-lift:hover::before {
  opacity: 1;
}

/* 考试头部 */
.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  gap: 16px;
}

.exam-title-section {
  flex: 1;
}

.title-wrapper {
  margin-bottom: 16px;
}

.exam-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0;
  cursor: pointer;
  transition: color var(--vt-transition-fast) ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.exam-title:hover {
  color: #f39c12;
}

.title-icon {
  font-size: 20px;
}

.exam-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.course-badge.primary {
  background: linear-gradient(135deg, var(--vt-c-primary) 0%, var(--vt-c-primary-light) 100%);
}

.teacher-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: var(--color-background-soft);
  border: 1px solid var(--color-border-soft);
  border-radius: var(--vt-radius-small);
  font-size: 12px;
  color: var(--color-text-soft);
}

.exam-status-tags {
  display: flex;
  gap: 8px;
}

.status-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
  border-radius: var(--vt-radius-small) !important;
}

.status-tag.active {
  background: linear-gradient(135deg, #e6a23c 0%, #f39c12 100%) !important;
  border-color: transparent !important;
  color: white !important;
}

.status-tag.expired {
  background: var(--color-background-soft) !important;
  color: var(--color-text-soft) !important;
  border-color: var(--color-border-soft) !important;
}

.status-tag.upcoming {
  background: linear-gradient(135deg, var(--vt-c-primary) 0%, var(--vt-c-primary-light) 100%) !important;
  border-color: transparent !important;
  color: white !important;
}

.tag-icon {
  font-size: 12px;
}

.exam-score-section {
  display: flex;
  align-items: center;
}

.score-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f39c12 0%, #e74c3c 100%);
  border-radius: var(--vt-radius-small);
  color: white;
  box-shadow: var(--vt-shadow-2);
}

.score-label {
  font-size: 12px;
  opacity: 0.9;
  margin-bottom: 4px;
}

.score-value {
  font-size: 24px;
  font-weight: 700;
}

.score-unit {
  font-size: 12px;
  opacity: 0.9;
}

/* 考试内容 */
.exam-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.exam-description {
  background: var(--color-background-soft);
  border-radius: var(--vt-radius-small);
  padding: 16px;
  border: 1px solid var(--color-border-soft);
}

.description-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-weight: 600;
  color: var(--color-text);
  font-size: 14px;
}

.description-text {
  color: var(--color-text-soft);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

.exam-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  padding: 16px;
  background: var(--color-background-soft);
  border-radius: var(--vt-radius-small);
  border: 1px solid var(--color-border-soft);
}

.meta-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-separator {
  margin: 0 8px;
  color: var(--color-text-soft);
}

.meta-value.duration {
  color: #f39c12;
  font-weight: 600;
}

.question-distribution {
  grid-column: 1 / -1;
}

.question-types-detail {
  font-size: 13px;
  color: var(--color-text-soft);
  line-height: 1.5;
}

/* 考试底部 */
.exam-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px solid var(--color-border-soft);
}

.footer-info {
  flex: 1;
}

.remaining-time {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
}

.remaining-time.upcoming {
  color: var(--vt-c-primary);
}

.remaining-time.active {
  color: #e6a23c;
}

.remaining-time.finished {
  color: var(--color-text-soft);
}

.time-icon {
  font-size: 16px;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

.exam-action-btn {
  padding: 10px 20px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.exam-action-btn:hover:not(:disabled) {
  transform: translateY(-2px) !important;
  box-shadow: var(--vt-shadow-2) !important;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .assignment-grid,
  .exam-grid {
    grid-template-columns: 1fr;
  }

  .input-group {
    flex-direction: column;
  }

  .input-wrapper {
    min-width: unset;
  }

  .filter-tabs {
    flex-direction: column;
  }

  .assignment-header,
  .exam-header {
    flex-direction: column;
    gap: 12px;
  }

  .assignment-meta,
  .exam-meta {
    grid-template-columns: 1fr;
  }

  .assignment-footer,
  .exam-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .footer-right,
  .footer-actions {
    justify-content: center;
  }

  .search-inputs .input-group {
    align-items: stretch;
  }

  .search-actions {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .assignment-container {
    padding: 16px;
  }

  .header-content {
    padding: 24px;
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .page-title {
    font-size: 28px;
  }

  .search-card,
  .content-card {
    padding: 20px;
  }

  .assignment-card,
  .exam-card {
    padding: 20px;
  }
}
</style>