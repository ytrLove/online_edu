<template>
  <div class="course-container">
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-card">
        <div class="search-header">
          <div class="search-title">
            <h2>发现精彩课程</h2>
            <p>探索知识的海洋，找到最适合您的学习内容</p>
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
                  <el-icon><Search /></el-icon>
                </div>
                <el-input
                  v-model="searchForm.title"
                  placeholder="搜索您感兴趣的课程..."
                  clearable
                  class="search-input"
                  @keyup.enter="handleSearch"
                />
              </div>
              
              <div class="input-wrapper">
                <div class="input-icon">
                  <el-icon><Grid /></el-icon>
                </div>
                <el-select
                  v-model="searchForm.subjectId"
                  placeholder="选择课程分类"
                  clearable
                  class="search-select"
                >
                  <el-option
                    v-for="item in subjectOptions"
                    :key="item.subjectId"
                    :label="item.subjectName"
                    :value="item.subjectId"
                  />
                </el-select>
              </div>
              
              <div class="search-actions">
                <el-button type="primary" class="search-btn" @click="handleSearch">
                  <el-icon><Search /></el-icon>
                  搜索
                </el-button>
                <el-button class="reset-btn" @click="resetSearch">
                  <el-icon><Refresh /></el-icon>
                  重置
                </el-button>
              </div>
            </div>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 筛选标签区域 -->
    <div v-if="subjectOptions.length > 0" class="filter-section">
      <div class="filter-header">
        <h3>课程分类</h3>
        <span class="filter-count">共 {{ total }} 门课程</span>
      </div>
      <div class="filter-tags">
        <div
          class="filter-tag"
          :class="{ active: searchForm.subjectId === null }"
          @click="selectSubject(null)"
        >
          <span class="tag-icon">📚</span>
          <span class="tag-text">全部课程</span>
          <span class="tag-count">{{ total }}</span>
        </div>
        <div
          v-for="item in subjectOptions"
          :key="item.subjectId"
          class="filter-tag"
          :class="{ active: searchForm.subjectId === item.subjectId }"
          @click="selectSubject(item.subjectId)"
        >
          <span class="tag-icon">{{ getSubjectIcon(item.subjectName) }}</span>
          <span class="tag-text">{{ item.subjectName }}</span>
          <span class="tag-count">{{ getSubjectCount(item.subjectId) }}</span>
        </div>
      </div>
    </div>

    <!-- 课程网格 -->
    <div v-if="courseList.length === 0 && !loading" class="empty-container">
      <div class="empty-state">
        <div class="empty-icon">
          <el-icon><Reading /></el-icon>
        </div>
        <h3>暂无课程数据</h3>
        <p>暂时没有找到符合条件的课程，试试其他搜索条件吧</p>
        <el-button type="primary" @click="resetSearch">
          重置搜索
        </el-button>
      </div>
    </div>

    <div v-else class="course-grid" v-loading="loading">
      <div v-for="course in courseList" :key="course.courseId" class="course-card-wrapper">
        <div class="course-card hover-lift" @click="goToCourseDetail(course.courseId)">
          <div class="course-image">
            <el-image :src="course.coverImage || defaultCover" fit="cover" class="course-cover">
              <template #error>
                <div class="image-placeholder">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            
            <!-- 课程状态标签 -->
            <div class="course-badges">
              <el-tag v-if="course.status === 'published'" type="success" size="small" class="status-badge">
                已发布
              </el-tag>
              <el-tag v-else-if="course.status === 'draft'" type="info" size="small" class="status-badge">
                草稿
              </el-tag>
              <el-tag v-else-if="course.status === 'closed'" type="danger" size="small" class="status-badge">
                已关闭
              </el-tag>
            </div>
            
            <!-- 悬停遮罩 -->
            <div class="course-overlay">
              <div class="overlay-content">
                <el-icon class="play-icon"><VideoPlay /></el-icon>
                <p class="overlay-text">点击查看详情</p>
              </div>
            </div>
          </div>
          
          <div class="course-content">
            <div class="course-header">
              <div class="course-subject">{{ course.subjectName }}</div>
              <div class="course-rating">
                <el-rate
                  v-model="course.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                  :max="5"
                  size="small"
                />
              </div>
            </div>
            
            <h3 class="course-title" :title="course.courseName">{{ course.courseName }}</h3>
            
            <div class="course-code" v-if="course.courseCode">
              课程代码: {{ course.courseCode }}
            </div>
            
            <div class="course-description">
              {{ truncateText(course.description, 100) }}
            </div>
            
            <div class="course-meta">
              <div class="meta-item" v-if="course.studentCount !== null">
                <el-icon><User /></el-icon>
                <span>{{ course.studentCount || 0 }}人学习</span>
              </div>
              <div class="meta-item" v-if="course.createTime">
                <el-icon><Calendar /></el-icon>
                <span>{{ formatDate(course.createTime) }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Clock /></el-icon>
                <span>{{ getCourseDuration() }}</span>
              </div>
            </div>
            
            <div class="course-footer">
              <div class="course-price">
                <span class="price-label">免费</span>
              </div>
              <div class="course-actions">
                <el-button
                  type="primary"
                  size="small"
                  class="action-btn"
                  @click.stop="handleEnroll(course)"
                >
                  加入学习
                </el-button>
                <el-button
                  size="small"
                  class="detail-btn"
                  @click.stop="goToCourseDetail(course.courseId)"
                >
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-container" v-if="total > 0">
      <el-pagination
        :current-page="searchForm.pageNum"
        :page-size="searchForm.pageSize"
        :page-sizes="[8, 12, 16, 24]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  User, 
  Picture, 
  Calendar, 
  Search, 
  Grid, 
  Refresh, 
  Reading,
  VideoPlay,
  Clock
} from '@element-plus/icons-vue'
import { getCourseList, getSubjectList, enrollCourse } from '@/api/course'

const router = useRouter()
const courseList = ref([])
const total = ref(0)
const subjectOptions = ref([])
const loading = ref(false)
const defaultCover = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'

const searchForm = reactive({
  title: '',
  subjectId: null,
  pageNum: 1,
  pageSize: 12
})

onMounted(async () => {
  await fetchSubjects()
  await fetchCourses()
})

// 获取课程分类
const fetchSubjects = async () => {
  try {
    const result = await getSubjectList()
    subjectOptions.value = result.data
  } catch (error) {
    console.error('获取课程分类失败', error)
    ElMessage.error('获取课程分类失败')
  }
}

// 获取课程列表
const fetchCourses = async () => {
  loading.value = true
  try {
    // 处理搜索参数
    const params = { ...searchForm }
    
    if (!params.subjectId) {
      delete params.subjectId
    }

    const result = await getCourseList(params)
    if (result.code === 200) {
      courseList.value = result.data.records || result.data
      total.value = result.data.total || result.data.length
    } else {
      ElMessage.error(result.message || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表失败', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 选择学科分类
const selectSubject = (subjectId) => {
  searchForm.subjectId = subjectId
  searchForm.pageNum = 1
  fetchCourses()
}

// 获取学科图标
const getSubjectIcon = (subjectName) => {
  const iconMap = {
    '计算机科学': '💻',
    '数学': '📊',
    '英语': '🇬🇧',
    '物理': '⚙️',
    '化学': '⚗️',
    '经济学': '📈',
    '艺术': '🎨',
    '语言': '🌍'
  }
  return iconMap[subjectName] || '📚'
}

// 获取学科课程数量
const getSubjectCount = (subjectId) => {
  if (!subjectId) return total.value
  return courseList.value.filter(course => course.subjectId === subjectId).length
}

// 获取课程时长
const getCourseDuration = () => {
  const durations = ['2小时', '3小时', '5小时', '8小时', '12小时']
  return durations[Math.floor(Math.random() * durations.length)]
}

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
}

// 搜索
const handleSearch = () => {
  searchForm.pageNum = 1 // 重置到第一页
  fetchCourses()
}

// 重置搜索
const resetSearch = () => {
  searchForm.title = ''
  searchForm.subjectId = null
  searchForm.pageNum = 1
  fetchCourses()
}

// 页码变化
const handleCurrentChange = (val) => {
  searchForm.pageNum = val
  fetchCourses()
}

// 每页数量变化
const handleSizeChange = (val) => {
  searchForm.pageSize = val
  searchForm.pageNum = 1
  fetchCourses()
}

// 前往课程详情
const goToCourseDetail = (courseId) => {
  router.push(`/course/${courseId}`)
}

// 选课/加入课程
const handleEnroll = (course) => {
  ElMessageBox.confirm(
    `确定要加入《${course.courseName}》这门课程吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      await enrollCourse({ courseId: course.courseId })
      ElMessage.success('选课成功，可在"我的课程"中查看')
    } catch (error) {
      console.error('选课失败', error)
      ElMessage.error('选课失败')
    }
  }).catch(() => {})
}
</script>

<style scoped>
.course-container {
  padding: var(--container-padding);
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 140px);
}

/* 搜索区域 */
.search-section {
  margin-bottom: 32px;
}

.search-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: var(--vt-radius-large);
  padding: 40px;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: var(--vt-shadow-3);
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.search-title h2 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
}

.search-title p {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
  text-shadow: 0 1px 10px rgba(0, 0, 0, 0.2);
}

.search-decoration {
  position: relative;
}

.decoration-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.search-form {
  position: relative;
  z-index: 2;
}

.search-inputs {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  display: grid;
  grid-template-columns: 2fr 1fr auto;
  gap: 16px;
  align-items: end;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  z-index: 2;
  color: var(--color-text-tertiary);
  font-size: 18px;
}

.search-input :deep(.el-input__wrapper),
.search-select :deep(.el-select__wrapper) {
  height: 50px !important;
  border-radius: 12px !important;
  border: 2px solid rgba(255, 255, 255, 0.3) !important;
  background: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(10px) !important;
  transition: all var(--vt-transition-fast) ease !important;
  padding-left: 50px !important;
}

.search-input :deep(.el-input__wrapper:hover),
.search-select :deep(.el-select__wrapper:hover) {
  border-color: rgba(255, 255, 255, 0.6) !important;
  background: white !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

.search-input :deep(.el-input__wrapper.is-focus),
.search-select :deep(.el-select__wrapper.is-focus) {
  border-color: white !important;
  background: white !important;
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.3) !important;
}

.search-actions {
  display: flex;
  gap: 12px;
}

.search-btn {
  height: 50px !important;
  padding: 0 24px !important;
  background: rgba(255, 255, 255, 0.2) !important;
  border: 2px solid rgba(255, 255, 255, 0.3) !important;
  color: white !important;
  backdrop-filter: blur(10px) !important;
  border-radius: 12px !important;
  font-weight: 500 !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.search-btn:hover {
  background: rgba(255, 255, 255, 0.3) !important;
  border-color: rgba(255, 255, 255, 0.5) !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.reset-btn {
  height: 50px !important;
  padding: 0 24px !important;
  background: transparent !important;
  border: 2px solid rgba(255, 255, 255, 0.3) !important;
  color: white !important;
  border-radius: 12px !important;
  font-weight: 500 !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.reset-btn:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.5) !important;
}

/* 筛选区域 */
.filter-section {
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
  color: var(--color-heading);
  margin: 0;
}

.filter-count {
  font-size: 14px;
  color: var(--color-text-secondary);
  background: var(--color-background-soft);
  padding: 6px 12px;
  border-radius: 16px;
  border: 1px solid var(--color-border);
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-tag {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: white;
  border: 2px solid var(--color-border);
  border-radius: 24px;
  cursor: pointer;
  transition: all var(--vt-transition-fast) ease;
  font-size: 14px;
  font-weight: 500;
  box-shadow: var(--vt-shadow-1);
}

.filter-tag:hover {
  transform: translateY(-2px);
  box-shadow: var(--vt-shadow-2);
  border-color: var(--color-primary);
}

.filter-tag.active {
  background: var(--vt-gradient-primary);
  color: white;
  border-color: transparent;
  transform: translateY(-2px);
  box-shadow: var(--vt-shadow-3);
}

.tag-icon {
  font-size: 18px;
}

.tag-text {
  flex: 1;
}

.tag-count {
  background: rgba(0, 0, 0, 0.1);
  color: inherit;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.filter-tag.active .tag-count {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

/* 课程网格 */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.course-card-wrapper {
  display: flex;
  flex-direction: column;
}

.course-card {
  background: white;
  border-radius: var(--vt-radius-large);
  overflow: hidden;
  box-shadow: var(--vt-shadow-1);
  border: 1px solid var(--color-border);
  transition: all var(--vt-transition-medium) ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--vt-shadow-4);
}

.course-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-cover {
  width: 100%;
  height: 100%;
  transition: transform var(--vt-transition-slow) ease;
}

.course-card:hover .course-cover {
  transform: scale(1.05);
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

.course-badges {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 2;
}

.status-badge {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.9) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
}

.course-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--vt-transition-medium) ease;
  backdrop-filter: blur(2px);
}

.course-card:hover .course-overlay {
  opacity: 1;
}

.overlay-content {
  text-align: center;
  color: white;
}

.play-icon {
  font-size: 48px;
  margin-bottom: 12px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.overlay-text {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

.course-content {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.course-subject {
  font-size: 12px;
  color: var(--color-primary);
  background: rgba(102, 126, 234, 0.1);
  padding: 4px 12px;
  border-radius: 16px;
  font-weight: 500;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.course-rating {
  display: flex;
  align-items: center;
  gap: 4px;
}

.course-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-heading);
  margin: 0 0 12px 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-code {
  font-size: 13px;
  color: var(--color-text-tertiary);
  margin-bottom: 12px;
  font-family: 'Courier New', monospace;
  background: var(--color-background-mute);
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
}

.course-description {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
  padding: 12px;
  background: var(--color-background-soft);
  border-radius: var(--vt-radius-small);
  border: 1px solid var(--color-border);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--color-text-secondary);
  font-size: 13px;
}

.meta-item .el-icon {
  font-size: 14px;
  color: var(--color-text-tertiary);
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.course-price {
  display: flex;
  align-items: center;
}

.price-label {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary);
  background: rgba(102, 126, 234, 0.1);
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.course-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: var(--vt-gradient-primary) !important;
  border: none !important;
  color: white !important;
  font-weight: 500 !important;
  border-radius: var(--vt-radius-small) !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--vt-shadow-2);
}

.detail-btn {
  background: transparent !important;
  border: 1px solid var(--color-border) !important;
  color: var(--color-text) !important;
  font-weight: 500 !important;
  border-radius: var(--vt-radius-small) !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.detail-btn:hover {
  border-color: var(--color-primary) !important;
  color: var(--color-primary) !important;
  background: rgba(102, 126, 234, 0.05) !important;
}

/* 空状态 */
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.empty-state {
  text-align: center;
  max-width: 400px;
}

.empty-icon {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  background: var(--color-background-mute);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-tertiary);
  font-size: 48px;
}

.empty-state h3 {
  font-size: 20px;
  color: var(--color-text-secondary);
  margin: 0 0 12px 0;
}

.empty-state p {
  font-size: 14px;
  color: var(--color-text-tertiary);
  margin: 0 0 24px 0;
  line-height: 1.6;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .course-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
}

@media (max-width: 768px) {
  .course-container {
    padding: 16px;
  }
  
  .search-card {
    padding: 24px;
  }
  
  .search-title h2 {
    font-size: 24px;
  }
  
  .search-decoration {
    display: none;
  }
  
  .input-group {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .search-actions {
    justify-content: center;
  }
  
  .filter-tags {
    justify-content: center;
  }
  
  .course-grid {
    grid-template-columns: 1fr;
  }
  
  .course-footer {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .course-actions {
    justify-content: space-between;
  }
}
</style> 