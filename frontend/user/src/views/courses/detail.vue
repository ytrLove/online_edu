<template>
  <div class="course-detail-container" v-loading="loading">
    <!-- 课程基本信息卡片 -->
    <el-card class="course-info-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8">
          <div class="course-cover">
            <el-image :src="courseInfo.coverImage || defaultCover" fit="cover"></el-image>
          </div>
        </el-col>
        <el-col :xs="24" :sm="16">
          <div class="course-info">
            <h1 class="course-title">{{ courseInfo.courseName }}</h1>
            <div class="course-meta">
              <el-tag v-if="courseInfo.subjectName">{{ courseInfo.subjectName }}</el-tag>
              <el-tag v-if="courseInfo.courseCode" type="info">{{ courseInfo.courseCode }}</el-tag>
              <span class="meta-item"><i class="el-icon-user"></i> {{ courseInfo.studentCount || 0 }}人正在学习</span>
              <span class="meta-item">
                <i class="el-icon-date"></i> 选课时间: 
                {{ formatDateTime(courseInfo.enrollmentStartTime) }} - {{ formatDateTime(courseInfo.enrollmentEndTime) }}
              </span>
              <span class="meta-item">
                <el-tag :type="getStatusType(courseInfo.enrollmentStatus)">
                  {{ getStatusText(courseInfo.enrollmentStatus) }}
                </el-tag>
              </span>
            </div>
            <div class="course-desc">{{ courseInfo.description }}</div>
            <div class="course-actions">
              <el-button type="primary" @click="handleEnroll" :disabled="isEnrolled || !isEnrollable">
                {{ isEnrolled ? '已加入学习' : '加入学习' }}
              </el-button>
              <el-button v-if="isEnrolled" type="success" @click="goToStudy">开始学习</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 课程内容标签页 -->
    <el-card class="course-content-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="课程详情" name="detail">
          <div class="course-detail">
            <div class="detail-section">
              <h3>课程介绍</h3>
              <div class="detail-content">
                {{ courseInfo.description || '暂无课程介绍' }}
              </div>
            </div>
            
            <div class="detail-section">
              <h3>教学目标</h3>
              <div class="detail-content">
                {{ courseInfo.objectives || '暂无教学目标' }}
              </div>
            </div>
            
            <div class="detail-section">
              <h3>教学大纲</h3>
              <div class="detail-content syllabus">
                {{ courseInfo.syllabus || '暂无教学大纲' }}
              </div>
            </div>
            
            <div class="detail-section" v-if="courseInfo.remark">
              <h3>备注</h3>
              <div class="detail-content">
                {{ courseInfo.remark }}
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="教师信息" name="teacher">
          <div v-if="courseInfo.teachers && courseInfo.teachers.length > 0">
            <div v-for="teacher in courseInfo.teachers" :key="teacher.teacherId" class="teacher-item">
              <el-avatar :size="64" :src="teacher.avatar || defaultAvatar">
                {{ teacher.teacherName ? teacher.teacherName.charAt(0) : 'T' }}
              </el-avatar>
              <div class="teacher-info">
                <h3>
                  {{ teacher.teacherName || '未知教师' }} 
                  <el-tag v-if="teacher.role === 'main'" type="success">主讲</el-tag>
                  <el-tag v-else-if="teacher.role === 'assistant'" type="info">助教</el-tag>
                </h3>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无教师信息"></el-empty>
        </el-tab-pane>
        
        <el-tab-pane label="课程资源" name="resources" v-if="isEnrolled">
          <el-card class="resource-list-card">
            <template #header>
              <div class="card-header">
                <span>课程资源</span>
                <el-input
                  v-model="resourceSearch"
                  placeholder="搜索资源..."
                  class="search-input"
                  clearable
                  @input="handleResourceSearch">
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
              </div>
            </template>

            <div class="resource-filter">
              <el-radio-group v-model="resourceTypeFilter" @change="handleResourceFilter">
                <el-radio-button label="">全部</el-radio-button>
                <el-radio-button label="1">课件</el-radio-button>
                <el-radio-button label="2">文档</el-radio-button>
                <el-radio-button label="3">教材</el-radio-button>
                <el-radio-button label="4">视频</el-radio-button>
              </el-radio-group>
            </div>

            <div v-loading="resourceLoading" class="resource-list">
              <template v-if="filteredResources.length > 0">
                <div v-for="resource in filteredResources" 
                     :key="resource.resourceId" 
                     class="resource-item"
                     :class="{ 'is-downloading': downloadingMap[resource.resourceId] }">
                  <div class="resource-icon">
                    <el-icon :size="24" :color="getResourceTypeColor(resource.resourceTypeId)">
                      <component :is="getResourceTypeIcon(resource.resourceTypeId)" />
                    </el-icon>
                  </div>
                  
                  <div class="resource-info">
                    <div class="resource-name">{{ resource.resourceName }}</div>
                    <div class="resource-meta">
                      <span class="resource-type">{{ getResourceTypeName(resource.resourceTypeId) }}</span>
                      <span class="resource-size">{{ formatFileSize(resource.fileSize) }}</span>
                      <span class="download-count">
                        <el-icon><Download /></el-icon>
                        {{ resource.downloadCount }}次下载
                      </span>
                      <span class="update-time">
                        <el-icon><Timer /></el-icon>
                        {{ formatDate(resource.updateTime) }}
                      </span>
                    </div>
                    <div class="resource-desc" v-if="resource.description">
                      {{ resource.description }}
                    </div>
                  </div>
                  
                  <div class="resource-actions">
                    <el-tooltip content="下载资源" placement="top">
                      <el-button 
                        type="primary" 
                        :icon="Download"
                        circle
                        :loading="downloadingMap[resource.resourceId]"
                        @click="handleDownload(resource)">
                      </el-button>
                    </el-tooltip>
                    <el-tooltip content="预览" placement="top" v-if="canPreview(resource)">
                      <el-button 
                        type="info" 
                        :icon="View"
                        circle
                        @click="handlePreview(resource)">
                      </el-button>
                    </el-tooltip>
                  </div>
                </div>
              </template>
              
              <el-empty v-else description="暂无资源" />
            </div>
          </el-card>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 资源预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      :title="currentResource && currentResource.resourceName"
      width="80%"
      class="preview-dialog"
      destroy-on-close
      @close="handlePreviewClose"
    >
      <div v-loading="previewLoading" class="preview-container">
        <!-- PDF预览 -->
        <iframe
          v-if="isPreviewPDF"
          :src="previewUrl"
          class="preview-iframe"
          frameborder="0"
        ></iframe>
        
        <!-- 视频预览 -->
        <video
          v-else-if="isPreviewVideo"
          :src="previewUrl"
          class="preview-video"
          controls
          autoplay
        ></video>
        
        <!-- 图片预览 -->
        <el-image
          v-else-if="isPreviewImage"
          :src="previewUrl"
          class="preview-image"
          fit="contain"
        ></el-image>
        
        <!-- 不支持预览的文件类型 -->
        <div v-else class="preview-unsupported">
          <el-icon :size="48"><Document /></el-icon>
          <p>该文件类型暂不支持在线预览，请下载后查看</p>
          <el-button type="primary" @click="handleDownloadInPreview">下载文件</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { recordBehavior } from '@/api/behavior'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseDetail, enrollCourse, getEnrolledCourses } from '@/api/course'
import { getCourseResources, downloadResource } from '@/api/resource'
import { Document, FolderOpened, VideoPlay, Collection, Download, View, Search, Timer } from '@element-plus/icons-vue'

const startTime = ref(null)
const endTime = ref(null)
const route = useRoute()
const router = useRouter()
const courseId = computed(() => route.params.id)
const defaultCover = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 数据状态
const loading = ref(false)
const activeTab = ref('detail')
const courseInfo = ref({})
const isEnrolled = ref(false)
const resourceList = ref([])
const resourceTotal = ref(0)
const resourceQuery = reactive({
  pageNum: 1,
  pageSize: 10,
  resourceName: '',
  resourceTypeId: null
})

// 资源相关
const resourceSearch = ref('')
const resourceTypeFilter = ref('')
const resourceLoading = ref(false)
const downloadingMap = ref({})

// 预览相关
const previewDialogVisible = ref(false)
const previewLoading = ref(false)
const currentResource = ref(null)
const previewUrl = ref('')

// 判断文件类型
const isPreviewPDF = computed(() => {
  return currentResource.value?.fileUrl?.toLowerCase().endsWith('.pdf')
})

const isPreviewVideo = computed(() => {
  const videoExts = ['.mp4', '.webm', '.ogg']
  return videoExts.some(ext => currentResource.value?.fileUrl?.toLowerCase().endsWith(ext))
})

const isPreviewImage = computed(() => {
  const imageExts = ['.jpg', '.jpeg', '.png', '.gif', '.webp']
  return imageExts.some(ext => currentResource.value?.fileUrl?.toLowerCase().endsWith(ext))
})

// 计算属性：是否可选课
const isEnrollable = computed(() => {
  if (!courseInfo.value.enrollmentStatus) return false
  return courseInfo.value.enrollmentStatus === 'open'
})

// 获取课程详情
onMounted(async () => {
  if (!courseId.value) {
    ElMessage.error('课程ID不能为空')
    return
  }
  
  loading.value = true
  try {
    await fetchCourseDetail()
    await checkEnrollmentStatus()
    
    // 如果已选课，获取资源列表
    if (isEnrolled.value) {
      await fetchResources()
    }
  } catch (error) {
    console.error('获取课程信息失败', error)
    ElMessage.error('获取课程信息失败')
  } finally {
    loading.value = false
  }
})

// 获取课程详情
const fetchCourseDetail = async () => {
  try {
    const result = await getCourseDetail(courseId.value)
    courseInfo.value = result.data
    
    // 记录查看行为
    recordBehavior({
      courseId: courseId.value,
      behaviorType: 'view_course'
    }).catch(error => console.error('记录行为失败', error))
  } catch (error) {
    console.error('获取课程详情失败', error)
    throw error
  }
}

// 检查是否已选课
const checkEnrollmentStatus = async () => {
  try {
    const result = await getEnrolledCourses()
    const enrolledCourses = result.data || []
    isEnrolled.value = enrolledCourses.some(course => course.courseId == courseId.value)
  } catch (error) {
    console.error('获取已选课程失败', error)
    throw error
  }
}

// 获取课程资源
const fetchResources = async () => {
  try {
    const result = await getCourseResources(courseId.value)
    resourceList.value = result.data
    resourceTotal.value = result.data.total
  } catch (error) {
    console.error('获取课程资源失败', error)
    ElMessage.error('获取课程资源失败')
  }
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '未设置'
  const date = new Date(dateTimeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'upcoming': '即将开始',
    'open': '开放选课',
    'closed': '选课已结束'
  }
  return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'upcoming': 'info',
    'open': 'success',
    'closed': 'danger'
  }
  return typeMap[status] || ''
}

// 资源分页处理
const handleResourceSizeChange = (val) => {
  resourceQuery.pageSize = val
  resourceQuery.pageNum = 1
  fetchResources()
}

const handleResourceCurrentChange = (val) => {
  resourceQuery.pageNum = val
  fetchResources()
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 选课
const handleEnroll = async () => {
  if (isEnrolled.value) {
    return
  }
  
  ElMessageBox.confirm(
    `确定要加入《${courseInfo.value.courseName}》这门课程吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      await enrollCourse({ courseId: courseId.value })
      ElMessage.success('选课成功，可以开始学习了')
      isEnrolled.value = true
      // 切换到资源页签
      activeTab.value = 'resources'
      // 加载资源列表
      await fetchResources()
    } catch (error) {
      console.error('选课失败', error)
    }
  }).catch(() => {})
}

// 前往学习
const goToStudy = () => {
  activeTab.value = 'resources'
}

// 预览资源
const handlePreview = async (resource) => {
  currentResource.value = resource
  previewDialogVisible.value = true
  previewLoading.value = true
  
  try {
    // 开始计时
    startTime.value = Date.now()
    
    // 获取预览URL
    previewUrl.value = resource.fileUrl
  } catch (error) {
    console.error('预览资源失败', error)
    ElMessage.error('预览资源失败')
  } finally {
    previewLoading.value = false
  }
}

// 处理预览关闭
const handlePreviewClose = async () => {
  if (!currentResource.value || !startTime.value) return
  
  try {
    const endTime = Date.now()
    const duration = Math.floor((endTime - startTime.value) / 1000) // 转换为秒
    
    // 记录预览行为和时长
    await recordBehavior({
      courseId: courseId.value,
      resourceId: currentResource.value.resourceId,
      behaviorType: 'view_resource',
      duration: duration
    })
    
    // 清理状态
    startTime.value = null
    currentResource.value = null
    previewUrl.value = ''
  } catch (error) {
    console.error('记录预览行为失败', error)
  }
}

// 在预览对话框中下载
const handleDownloadInPreview = () => {
  if (currentResource.value) {
    handleDownload(currentResource.value)
  }
}

// 处理资源下载
const handleDownload = async (resource) => {
  if (downloadingMap.value[resource.resourceId]) return
  
  try {
    downloadingMap.value[resource.resourceId] = true
    
    // 记录下载行为
    await recordBehavior({
      courseId: courseId.value,
      resourceId: resource.resourceId,
      behaviorType: 'download_resource'
    })

    // 调用下载接口获取下载链接
    const res = await downloadResource(resource.resourceId)
    if (!res.data || !res.data.fileUrl) {
      throw new Error('获取下载链接失败')
    }

    // 创建一个隐藏的a标签来处理下载
    const link = document.createElement('a')
    link.style.display = 'none'
    link.href = res.data.fileUrl
    
    // 从URL中提取文件名，如果没有则使用资源名称
    const fileName = resource.resourceName || 
                    decodeURIComponent(res.data.fileUrl.split('/').pop()) || 
                    'downloaded_file'
    
    // 设置下载文件名
    link.setAttribute('download', fileName)
    
    // 添加到页面并触发点击
    document.body.appendChild(link)
    link.click()
    
    // 移除临时创建的链接
    document.body.removeChild(link)
    
    ElMessage.success('开始下载')
  } catch (error) {
    console.error('下载出错', error)
    ElMessage.error(error.message || '下载失败')
  } finally {
    downloadingMap.value[resource.resourceId] = false
  }
}

// 根据资源类型获取图标
const getResourceTypeIcon = (typeId) => {
  switch (Number(typeId)) {
    case 1: return FolderOpened // 课件
    case 2: return Document // 文档
    case 3: return Collection // 教材
    case 4: return VideoPlay // 视频
    default: return Document
  }
}

// 获取资源类型名称
const getResourceTypeName = (typeId) => {
  switch (Number(typeId)) {
    case 1: return '课件'
    case 2: return '文档'
    case 3: return '教材'
    case 4: return '视频'
    default: return '其他'
  }
}

// 获取资源类型颜色
const getResourceTypeColor = (typeId) => {
  switch (Number(typeId)) {
    case 1: return '#409EFF' // 课件-蓝色
    case 2: return '#67C23A' // 文档-绿色
    case 3: return '#E6A23C' // 教材-黄色
    case 4: return '#F56C6C' // 视频-红色
    default: return '#909399'
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 判断是否可以预览
const canPreview = (resource) => {
  const previewableTypes = ['.pdf', '.doc', '.docx', '.ppt', '.pptx', '.mp4']
  return previewableTypes.some(type => resource.fileUrl.toLowerCase().endsWith(type))
}

// 过滤资源列表
const filteredResources = computed(() => {
  let result = resourceList.value
  
  // 类型筛选
  if (resourceTypeFilter.value) {
    result = result.filter(item => String(item.resourceTypeId) === resourceTypeFilter.value)
  }
  
  // 关键词搜索
  if (resourceSearch.value) {
    const keyword = resourceSearch.value.toLowerCase()
    result = result.filter(item => 
      item.resourceName.toLowerCase().includes(keyword) ||
      item.description?.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 处理资源搜索
const handleResourceSearch = () => {
  // 可以添加防抖处理
}

// 处理资源类型筛选
const handleResourceFilter = () => {
  // 可以添加额外的筛选逻辑
}
</script>

<style scoped>
.course-detail-container {
  padding: 20px;
}

.course-info-card {
  margin-bottom: 20px;
}

.course-cover {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
}

.course-cover .el-image {
  width: 100%;
  height: 100%;
}

.course-info {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.course-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
  align-items: center;
}

.meta-item {
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
}

.meta-item i {
  margin-right: 5px;
}

.course-desc {
  flex: 1;
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.course-actions {
  display: flex;
  gap: 10px;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h3 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
}

.detail-content {
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
  padding: 0 10px;
}

.syllabus {
  white-space: pre-line;
}

.teacher-item {
  display: flex;
  align-items: flex-start;
  padding: 15px;
  margin-bottom: 15px;
  border-radius: 8px;
  background-color: #f5f7fa;
}

.teacher-info {
  margin-left: 15px;
  flex: 1;
}

.teacher-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.teacher-intro {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.teacher-career {
  font-size: 13px;
  color: #909399;
}

.empty-data {
  padding: 30px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.pagination-container {
  padding: 15px 0;
  display: flex;
  justify-content: center;
}

.resource-list-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-input {
  width: 300px;
}

.resource-filter {
  margin-bottom: 20px;
}

.resource-list {
  min-height: 200px;
}

.resource-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  transition: all 0.3s;
}

.resource-item:last-child {
  border-bottom: none;
}

.resource-item:hover {
  background-color: #f5f7fa;
}

.resource-item.is-downloading {
  background-color: #ecf5ff;
}

.resource-icon {
  padding: 12px;
  border-radius: 8px;
  background-color: #f5f7fa;
  margin-right: 16px;
}

.resource-info {
  flex: 1;
  min-width: 0;
}

.resource-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.resource-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 8px;
  color: #909399;
  font-size: 13px;
}

.resource-meta .el-icon {
  margin-right: 4px;
}

.resource-type {
  color: #409eff;
}

.resource-desc {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.resource-actions {
  display: flex;
  gap: 8px;
  margin-left: 16px;
}

.download-count,
.update-time {
  display: flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .search-input {
    width: 100%;
  }
  
  .resource-filter {
    overflow-x: auto;
  }
  
  .resource-item {
    flex-direction: column;
  }
  
  .resource-icon {
    margin-bottom: 12px;
  }
  
  .resource-actions {
    margin-left: 0;
    margin-top: 12px;
  }
  
  .resource-meta {
    gap: 8px;
  }
}

/* 预览对话框样式 */
.preview-dialog :deep(.el-dialog__body) {
  padding: 0;
  height: 70vh;
  overflow: hidden;
}

.preview-container {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.preview-iframe,
.preview-video {
  width: 100%;
  height: 100%;
  background-color: white;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
}

.preview-unsupported {
  text-align: center;
  padding: 40px;
  color: #909399;
}

.preview-unsupported .el-icon {
  margin-bottom: 20px;
}

.preview-unsupported p {
  margin-bottom: 20px;
}
</style> 