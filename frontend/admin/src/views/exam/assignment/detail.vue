<template>
  <div class="assignment-detail-container">
    <el-page-header @back="goBack" title="返回作业列表">
      <template #content>
        <div class="page-header-content">
          <span class="page-title">作业详情</span>
        </div>
      </template>
      <template #extra>
        <el-button type="primary" @click="handleEdit">编辑作业</el-button>
      </template>
    </el-page-header>
    
    <div class="assignment-detail-content" v-loading="loading">
      <!-- 作业信息 -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>作业信息</span>
            <el-tag :type="getStatusTag(assignmentInfo.status)">
              {{ getStatusLabel(assignmentInfo.status) }}
            </el-tag>
          </div>
        </template>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="作业标题" :span="2">{{ assignmentInfo.title }}</el-descriptions-item>
          <el-descriptions-item label="所属课程">{{ assignmentInfo.courseName }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ assignmentInfo.totalScore }}分</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ assignmentInfo.startTime }}</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ assignmentInfo.endTime }}</el-descriptions-item>
          <el-descriptions-item label="评分公开">
            <el-tag :type="assignmentInfo.publicScore ? 'success' : 'info'">
              {{ assignmentInfo.publicScore ? '是' : '否' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交情况">
            {{ assignmentInfo.submittedCount || 0 }} / {{ assignmentInfo.studentCount || 0 }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
      
      <!-- 作业内容 -->
      <el-card class="content-card">
        <template #header>
          <div class="card-header">
            <span>作业内容</span>
          </div>
        </template>
        
        <div class="assignment-content">{{ assignmentInfo.content }}</div>
      </el-card>
      
      <!-- 上传要求 -->
      <el-card v-if="assignmentInfo.fileRequirements" class="requirements-card">
        <template #header>
          <div class="card-header">
            <span>上传要求</span>
          </div>
        </template>
        
        <div class="file-requirements">{{ assignmentInfo.fileRequirements }}</div>
      </el-card>
      
      <!-- 提交统计 -->
      <el-card class="statistics-card">
        <template #header>
          <div class="card-header">
            <span>提交统计</span>
            <el-button type="primary" size="small" @click="handleSubmissionList">查看提交列表</el-button>
          </div>
        </template>
        
        <div class="submission-statistics">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="statistic-card">
                <div class="statistic-title">总学生数</div>
                <div class="statistic-value">{{ assignmentInfo.studentCount || 0 }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="statistic-card">
                <div class="statistic-title">已提交数</div>
                <div class="statistic-value">{{ assignmentInfo.submittedCount || 0 }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="statistic-card">
                <div class="statistic-title">提交率</div>
                <div class="statistic-value">{{ calculateSubmissionRate() }}%</div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div v-if="assignmentInfo.submittedCount > 0" class="grading-statistics">
          <h3>评分统计</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="statistic-card">
                <div class="statistic-title">已批改数</div>
                <div class="statistic-value">{{ assignmentInfo.gradedCount || 0 }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="statistic-card">
                <div class="statistic-title">平均分</div>
                <div class="statistic-value">{{ assignmentInfo.averageScore || 0 }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="statistic-card">
                <div class="statistic-title">最高分</div>
                <div class="statistic-value">{{ assignmentInfo.highestScore || 0 }}</div>
              </div>
            </el-col>
          </el-row>
          
          <!-- 分数分布 -->
          <div v-if="assignmentInfo.scoreDistribution" class="score-distribution">
            <h4>分数分布</h4>
            <el-progress 
              :percentage="calculateDistributionPercentage(90, 100)" 
              :color="'#67C23A'" 
              :format="() => formatDistribution(90, 100)"
              :stroke-width="20"
              class="distribution-bar"
            />
            <el-progress 
              :percentage="calculateDistributionPercentage(80, 89)" 
              :color="'#85CE61'" 
              :format="() => formatDistribution(80, 89)"
              :stroke-width="20"
              class="distribution-bar"
            />
            <el-progress 
              :percentage="calculateDistributionPercentage(70, 79)" 
              :color="'#E6A23C'" 
              :format="() => formatDistribution(70, 79)"
              :stroke-width="20"
              class="distribution-bar"
            />
            <el-progress 
              :percentage="calculateDistributionPercentage(60, 69)" 
              :color="'#F56C6C'" 
              :format="() => formatDistribution(60, 69)"
              :stroke-width="20"
              class="distribution-bar"
            />
            <el-progress 
              :percentage="calculateDistributionPercentage(0, 59)" 
              :color="'#909399'" 
              :format="() => formatDistribution(0, 59)"
              :stroke-width="20"
              class="distribution-bar"
            />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAssignmentInfo } from '@/api/exam/assignment'

const route = useRoute()
const router = useRouter()

// 加载状态
const loading = ref(false)

// 作业信息
const assignmentInfo = reactive({
  assignmentId: '',
  courseId: '',
  courseName: '',
  title: '',
  content: '',
  totalScore: 0,
  startTime: '',
  endTime: '',
  status: 0,
  fileRequirements: '',
  publicScore: true,
  studentCount: 0,
  submittedCount: 0,
  gradedCount: 0,
  averageScore: 0,
  highestScore: 0,
  scoreDistribution: null
})

/**
 * 返回列表页
 */
const goBack = () => {
  router.push('/exam/assignment')
}

/**
 * 获取作业详情
 */
const getDetail = async () => {
  const assignmentId = route.params.id
  if (!assignmentId) {
    ElMessage.error('作业ID不存在')
    return goBack()
  }
  
  loading.value = true
  try {
    const response = await getAssignmentInfo(assignmentId)
    Object.assign(assignmentInfo, response.data)
    
    // 模拟分数分布数据，实际应该从后端获取
    if (assignmentInfo.gradedCount > 0) {
      assignmentInfo.scoreDistribution = {
        '90-100': Math.floor(Math.random() * assignmentInfo.gradedCount),
        '80-89': Math.floor(Math.random() * assignmentInfo.gradedCount),
        '70-79': Math.floor(Math.random() * assignmentInfo.gradedCount),
        '60-69': Math.floor(Math.random() * assignmentInfo.gradedCount),
        '0-59': Math.floor(Math.random() * assignmentInfo.gradedCount)
      }
    }
  } catch (error) {
    console.error('获取作业详情失败', error)
    ElMessage.error('获取作业详情失败')
    goBack()
  } finally {
    loading.value = false
  }
}

/**
 * 处理编辑操作
 */
const handleEdit = () => {
  router.push(`/exam/assignment/edit/${assignmentInfo.assignmentId}`)
}

/**
 * 处理提交列表操作
 */
const handleSubmissionList = () => {
  router.push(`/exam/assignment/submission/${assignmentInfo.assignmentId}`)
}

/**
 * 获取状态标签类型
 */
const getStatusTag = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'info'
  return 'info'
}

/**
 * 获取状态标签文本
 */
const getStatusLabel = (status) => {
  if (status === 1) return '进行中'
  if (status === 2) return '已截止'
  return '未知'
}

/**
 * 计算提交率
 */
const calculateSubmissionRate = () => {
  if (!assignmentInfo.studentCount || assignmentInfo.studentCount === 0) return 0
  return Math.round((assignmentInfo.submittedCount / assignmentInfo.studentCount) * 100)
}

/**
 * 计算分数分布百分比
 */
const calculateDistributionPercentage = (min, max) => {
  if (!assignmentInfo.scoreDistribution || !assignmentInfo.gradedCount) return 0
  const key = `${min}-${max}`
  return (assignmentInfo.scoreDistribution[key] / assignmentInfo.gradedCount) * 100
}

/**
 * 格式化分数分布显示
 */
const formatDistribution = (min, max) => {
  if (!assignmentInfo.scoreDistribution) return '0人 (0%)'
  
  const key = `${min}-${max}`
  const count = assignmentInfo.scoreDistribution[key] || 0
  const percentage = Math.round((count / assignmentInfo.gradedCount) * 100) || 0
  
  return `${min}-${max}分: ${count}人 (${percentage}%)`
}

onMounted(() => {
  getDetail()
})
</script>

<style scoped>
.assignment-detail-container {
  padding: 20px;
}

.page-header-content {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
}

.assignment-detail-content {
  margin-top: 20px;
}

.info-card, .content-card, .requirements-card, .statistics-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.assignment-content, .file-requirements {
  line-height: 1.8;
  white-space: pre-line;
}

.submission-statistics {
  margin-bottom: 30px;
}

.statistic-card {
  text-align: center;
  padding: 20px;
  border-radius: 4px;
  background-color: #f8f8f8;
  height: 100%;
}

.statistic-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.statistic-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.grading-statistics h3, .score-distribution h4 {
  margin: 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.distribution-bar {
  margin: 15px 0;
}
</style> 