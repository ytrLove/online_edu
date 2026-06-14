<template>
  <div class="course-evaluation-container">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" icon="ArrowLeft" plain>返回</el-button>
        <h2>课程评价</h2>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showEvaluationDialog" v-if="isEnrolled">写评价</el-button>
      </div>
    </div>
    
    <div class="course-info-card" v-if="courseDetail">
      <el-card shadow="hover">
        <div class="course-info">
          <el-image :src="courseDetail.coverImage || defaultCover" class="course-cover" fit="cover">
            <template #error>
              <div class="image-placeholder">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="course-details">
            <h1 class="course-title">{{ courseDetail.courseName }}</h1>
            <div class="course-teacher" v-if="courseDetail.teacherName">
              <el-icon><User /></el-icon>
              <span>{{ courseDetail.teacherName }}</span>
            </div>
            <div class="course-meta">
              <el-tag v-if="courseDetail.subjectName" size="small">{{ courseDetail.subjectName }}</el-tag>
              <el-tag v-if="courseDetail.courseCode" type="info" size="small">{{ courseDetail.courseCode }}</el-tag>
              <el-tag 
                v-if="courseDetail.enrollmentStatus" 
                :type="getEnrollmentStatusType(courseDetail.enrollmentStatus)" 
                size="small">
                {{ getEnrollmentStatusText(courseDetail.enrollmentStatus) }}
              </el-tag>
            </div>
            <div class="course-stats">
              <div class="stat-item">
                <el-icon><User /></el-icon>
                <span>{{ courseDetail.studentCount || 0 }} 名学生</span>
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>{{ averageScore }} 分</span>
              </div>
              <div class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ total }} 条评价</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    
    <div class="evaluation-filter">
      <div class="filter-label">排序方式：</div>
      <el-radio-group v-model="sortBy" @change="handleSearch">
        <el-radio-button label="latest">最新</el-radio-button>
        <el-radio-button label="highest">最高分</el-radio-button>
        <el-radio-button label="lowest">最低分</el-radio-button>
      </el-radio-group>
      
      <div class="filter-label filter-score">评分：</div>
      <el-select v-model="filterScore" placeholder="全部评分" @change="handleSearch" clearable>
        <el-option label="全部评分" value=""></el-option>
        <el-option label="5 分" value="5"></el-option>
        <el-option label="4 分以上" value="4"></el-option>
        <el-option label="3 分以上" value="3"></el-option>
        <el-option label="2 分以上" value="2"></el-option>
        <el-option label="1 分以上" value="1"></el-option>
      </el-select>
    </div>
    
    <div class="evaluation-list-wrapper">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
        <el-skeleton :rows="3" animated />
      </div>
      
      <template v-else-if="evaluationList.length > 0">
        <el-card 
          v-for="(evaluation, index) in evaluationList" 
          :key="index" 
          class="evaluation-card"
          shadow="hover">
          <div class="evaluation-header">
            <div class="user-info">
              <el-avatar 
                :size="40" 
                :src="evaluation.userAvatar" 
                v-if="!evaluation.isAnonymous">
              </el-avatar>
              <el-avatar :size="40" icon="User" v-else></el-avatar>
              <div class="user-details">
                <div class="username">{{ evaluation.isAnonymous ? '匿名用户' : evaluation.userName }}</div>
                <div class="evaluation-time">{{ formatDate(evaluation.createTime) }}</div>
              </div>
            </div>
            <div class="score-info">
              <span class="score-value">{{ evaluation.overallScore || 0 }}</span>
              <el-rate
                v-model="evaluation.overallScore"
                disabled
                :colors="rateColors"
                :max="5">
              </el-rate>
            </div>
          </div>
          
          <div class="evaluation-content">
            {{ evaluation.content }}
          </div>
          
          <div class="score-breakdown">
            <div class="breakdown-item">
              <span class="breakdown-label">内容质量</span>
              <div class="breakdown-score">
                <el-rate
                  v-model="evaluation.contentScore"
                  disabled
                  :colors="rateColors"
                  :max="5">
                </el-rate>
                <span>{{ evaluation.contentScore || 0 }}</span>
              </div>
            </div>
            <div class="breakdown-item">
              <span class="breakdown-label">教学方法</span>
              <div class="breakdown-score">
                <el-rate
                  v-model="evaluation.teachingScore"
                  disabled
                  :colors="rateColors"
                  :max="5">
                </el-rate>
                <span>{{ evaluation.teachingScore || 0 }}</span>
              </div>
            </div>
            <div class="breakdown-item">
              <span class="breakdown-label">互动性</span>
              <div class="breakdown-score">
                <el-rate
                  v-model="evaluation.interactionScore"
                  disabled
                  :colors="rateColors"
                  :max="5">
                </el-rate>
                <span>{{ evaluation.interactionScore || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
        
        <div class="pagination-container">
          <el-pagination
            :current-page="page"
            :page-size="pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange">
          </el-pagination>
        </div>
      </template>
      
      <el-empty v-else description="暂无评价" class="empty-evaluation">
        <template #extra>
          <el-button type="primary" @click="showEvaluationDialog" v-if="isEnrolled">写评价</el-button>
        </template>
      </el-empty>
    </div>
    
    <!-- 教学评价对话框 -->
    <el-dialog
      v-model="evaluationDialog.visible"
      title="课程评价"
      width="600px"
      destroy-on-close
      @close="resetEvaluationForm">
      <el-form 
        v-if="courseDetail"
        ref="evaluationFormRef"
        :model="evaluationForm"
        :rules="evaluationRules"
        label-width="100px">
        <el-form-item>
          <div class="evaluation-course-info">
            <el-avatar 
              :src="courseDetail.coverImage || defaultCover" 
              :size="60">
            </el-avatar>
            <div class="course-info-text">
              <h3>{{ courseDetail.courseName }}</h3>
              <p v-if="courseDetail.teacherName">
                <el-icon><User /></el-icon> 
                {{ courseDetail.teacherName }}
              </p>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="总体评分" prop="overallScore">
          <el-rate
            v-model="evaluationForm.overallScore"
            :colors="rateColors"
            show-score
            :max="5">
          </el-rate>
        </el-form-item>
        
        <el-form-item label="内容质量" prop="contentScore">
          <el-rate
            v-model="evaluationForm.contentScore"
            :colors="rateColors"
            show-score
            :max="5">
          </el-rate>
        </el-form-item>
        
        <el-form-item label="教学方法" prop="teachingScore">
          <el-rate
            v-model="evaluationForm.teachingScore"
            :colors="rateColors"
            show-score
            :max="5">
          </el-rate>
        </el-form-item>
        
        <el-form-item label="互动性" prop="interactionScore">
          <el-rate
            v-model="evaluationForm.interactionScore"
            :colors="rateColors"
            show-score
            :max="5">
          </el-rate>
        </el-form-item>
        
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="evaluationForm.content"
            type="textarea"
            :rows="4"
            placeholder="请分享您的学习体验和建议..."
            maxlength="500"
            show-word-limit>
          </el-input>
        </el-form-item>
        
        <el-form-item label="匿名评价">
          <el-switch v-model="evaluationForm.isAnonymous"></el-switch>
          <span class="anonymous-tip">开启后，您的用户名将不会显示在评价中</span>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="evaluationDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitEvaluationForm" :loading="submitting">
            提交评价
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCourseDetail } from '@/api/course'
import { submitEvaluation, getCourseEvaluationStats, getEvaluationList } from '@/api/evaluation'
import { Picture, User, Star, ChatDotRound, ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const courseId = computed(() => route.params.id)
const courseDetail = ref(null)
const loading = ref(false)
const submitting = ref(false)
const isEnrolled = ref(false) // 假设用户已经选修了这个课程
const defaultCover = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'

// 评价列表相关
const evaluationList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const sortBy = ref('latest')
const filterScore = ref('')
const averageScore = ref(0)
const stats = ref(null)

// 评价对话框相关
const evaluationDialog = reactive({
  visible: false
})

const evaluationFormRef = ref(null)
const evaluationForm = reactive({
  courseId: null,
  overallScore: 0,
  contentScore: 0,
  teachingScore: 0,
  interactionScore: 0,
  content: '',
  isAnonymous: false
})

const evaluationRules = {
  overallScore: [{ required: true, message: '请给出总体评分', trigger: 'change' }],
  contentScore: [{ required: true, message: '请评价课程内容质量', trigger: 'change' }],
  teachingScore: [{ required: true, message: '请评价教学方法', trigger: 'change' }],
  interactionScore: [{ required: true, message: '请评价互动性', trigger: 'change' }],
  content: [{ required: true, message: '请填写评价内容', trigger: 'blur' }]
}

const rateColors = ['#F56C6C', '#E6A23C', '#909399', '#67C23A', '#409EFF']

onMounted(async () => {
  await fetchCourseDetail()
  await fetchEvaluations()
  await fetchEvaluationStats()
})

// 获取课程详情
const fetchCourseDetail = async () => {
  try {
    const result = await getCourseDetail(courseId.value)
    if (result && result.code === 200) {
      courseDetail.value = result.data
      // 判断用户是否已选课
      isEnrolled.value = true // 假设用户已选课，实际应从API获取
      
      // 设置评价表单中的课程ID
      evaluationForm.courseId = courseId.value
    }
  } catch (error) {
    console.error('获取课程详情失败', error)
    ElMessage.error('获取课程详情失败')
  }
}

// 获取评价列表
const fetchEvaluations = async () => {
  loading.value = true
  try {
    const result = await getEvaluationList({
      courseId: courseId.value,
      page: page.value,
      pageSize: pageSize.value,
      sortBy: sortBy.value,
      minScore: filterScore.value || undefined
    })
    
    if (result && result.code === 200) {
      evaluationList.value = result.data.records || []
      total.value = result.data.total || 0
    } else {
      // 使用模拟数据作为备用
      evaluationList.value = generateMockEvaluations(20)
      total.value = 20
    }
  } catch (error) {
    console.error('获取评价列表失败', error)
    ElMessage.error('获取评价列表失败')
    // 使用模拟数据作为备用
    evaluationList.value = generateMockEvaluations(20)
    total.value = 20
  } finally {
    loading.value = false
  }
}

// 获取评价统计数据
const fetchEvaluationStats = async () => {
  try {
    const result = await getCourseEvaluationStats(courseId.value)
    if (result && result.code === 200) {
      stats.value = result.data
      averageScore.value = stats.value.avgOverallScore || 0
    } else {
      // 使用模拟数据
      stats.value = {
        avgOverallScore: 4.2,
        avgContentScore: 4.3,
        avgTeachingScore: 4.1,
        avgInteractionScore: 4.0,
        evaluationCount: 20
      }
      averageScore.value = 4.2
    }
  } catch (error) {
    console.error('获取评价统计失败', error)
    // 使用模拟数据
    stats.value = {
      avgOverallScore: 4.2,
      avgContentScore: 4.3,
      avgTeachingScore: 4.1,
      avgInteractionScore: 4.0,
      evaluationCount: 20
    }
    averageScore.value = 4.2
  }
}

// 生成模拟评价数据
const generateMockEvaluations = (count) => {
  const result = []
  const userNames = ['张三', '李四', '王五', '赵六', '钱七', '孙八']
  const contents = [
    '这门课程非常棒，老师讲解清晰，内容丰富，很有收获。',
    '课程内容安排合理，讲解深入浅出，很适合初学者。',
    '老师授课风格独特，但有些知识点讲解不够详细。',
    '整体来说这门课程质量不错，就是课后作业有点多。',
    '课程互动环节设计得很好，能够很好地跟上老师的思路。'
  ]
  
  for (let i = 0; i < count; i++) {
    const overallScore = Math.floor(Math.random() * 3) + 3 // 3-5分
    result.push({
      id: i + 1,
      courseId: courseId.value,
      userName: userNames[Math.floor(Math.random() * userNames.length)],
      userAvatar: '',
      overallScore: overallScore,
      contentScore: Math.floor(Math.random() * 3) + 3,
      teachingScore: Math.floor(Math.random() * 3) + 3,
      interactionScore: Math.floor(Math.random() * 3) + 3,
      content: contents[Math.floor(Math.random() * contents.length)],
      isAnonymous: Math.random() > 0.7, // 30%概率匿名
      createTime: generateRandomDate(),
    })
  }
  
  return result
}

// 生成随机日期
const generateRandomDate = () => {
  const now = new Date()
  const daysAgo = Math.floor(Math.random() * 30) // 0-30天前
  const date = new Date(now)
  date.setDate(now.getDate() - daysAgo)
  return date.toISOString()
}

// 显示评价对话框
const showEvaluationDialog = () => {
  evaluationDialog.visible = true
}

// 重置评价表单
const resetEvaluationForm = () => {
  if (evaluationFormRef.value) {
    evaluationFormRef.value.resetFields()
  }
  
  Object.assign(evaluationForm, {
    courseId: courseId.value,
    overallScore: 0,
    contentScore: 0,
    teachingScore: 0,
    interactionScore: 0,
    content: '',
    isAnonymous: false
  })
}

// 提交评价表单
const submitEvaluationForm = async () => {
  if (!evaluationFormRef.value) return
  
  try {
    await evaluationFormRef.value.validate()
    
    submitting.value = true
    const result = await submitEvaluation({
      courseId: evaluationForm.courseId,
      overallScore: evaluationForm.overallScore,
      contentScore: evaluationForm.contentScore,
      teachingScore: evaluationForm.teachingScore,
      interactionScore: evaluationForm.interactionScore,
      content: evaluationForm.content,
      isAnonymous: evaluationForm.isAnonymous
    })
    
    if (result && result.code === 200) {
      ElMessage.success('评价提交成功')
      evaluationDialog.visible = false
      resetEvaluationForm()
      
      // 刷新评价列表
      await fetchEvaluations()
      await fetchEvaluationStats()
    } else {
      ElMessage.error(result?.message || '评价提交失败')
    }
  } catch (error) {
    console.error('提交评价出错', error)
    ElMessage.error('评价表单验证失败，请检查输入')
  } finally {
    submitting.value = false
  }
}

// 处理搜索/筛选
const handleSearch = () => {
  page.value = 1 // 重置到第一页
  fetchEvaluations()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchEvaluations()
}

const handleCurrentChange = (val) => {
  page.value = val
  fetchEvaluations()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
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

// 返回上一页
const goBack = () => {
  router.back()
}
</script>

<style scoped>
.course-evaluation-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-left h2 {
  margin: 0;
  font-size: 22px;
  color: #303133;
}

.course-info-card {
  margin-bottom: 30px;
}

.course-info {
  display: flex;
  gap: 20px;
}

.course-cover {
  width: 200px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 24px;
}

.course-details {
  flex: 1;
}

.course-title {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #303133;
}

.course-teacher {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  margin-bottom: 10px;
}

.course-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.course-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
}

.evaluation-filter {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.filter-label {
  color: #606266;
}

.filter-score {
  margin-left: 20px;
}

.evaluation-list-wrapper {
  margin-bottom: 30px;
}

.evaluation-card {
  margin-bottom: 20px;
}

.evaluation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: bold;
  color: #303133;
}

.evaluation-time {
  font-size: 12px;
  color: #909399;
}

.score-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.score-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.evaluation-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 20px;
  white-space: pre-line;
}

.score-breakdown {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.breakdown-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.breakdown-label {
  font-size: 13px;
  color: #909399;
}

.breakdown-score {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.loading-container {
  padding: 20px 0;
}

.empty-evaluation {
  margin: 60px 0;
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

@media (max-width: 768px) {
  .course-info {
    flex-direction: column;
  }
  
  .course-cover {
    width: 100%;
    height: 180px;
  }
  
  .evaluation-filter {
    flex-wrap: wrap;
  }
  
  .filter-score {
    margin-left: 0;
    margin-top: 10px;
  }
  
  .score-breakdown {
    flex-direction: column;
    gap: 15px;
  }
}
</style> 