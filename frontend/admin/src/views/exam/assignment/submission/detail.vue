<template>
  <div class="app-container">
    <el-card v-loading="pageLoading">
      <template #header>
        <div class="card-header">
          <span>作业提交详情</span>
          <div>
            <el-button type="primary" @click="goBack">返回</el-button>
            <el-button type="success" v-if="submissionInfo.status === 'submitted'" @click="handleStartGrade">开始批阅</el-button>
            <el-button type="warning" v-if="submissionInfo.status === 'graded'" @click="handleEditGrade">修改批阅</el-button>
          </div>
        </div>
      </template>

      <!-- 基本信息 -->
      <div class="basic-info">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="作业名称" :span="3">{{ assignmentInfo.title }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ submissionInfo.studentName }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDateTime(submissionInfo.submissionTime) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(submissionInfo.status)">{{ getStatusLabel(submissionInfo.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="得分" v-if="submissionInfo.status === 'graded'">
            <span class="score">{{ submissionInfo.totalScore }} / {{ assignmentInfo.totalPoints }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 答案列表 - 只查看模式 -->
      <div v-if="!isGrading" class="answers-container">
        <h3>答案列表</h3>
        <div v-if="submissionInfo.answers && submissionInfo.answers.length > 0">
          <div v-for="(answer, index) in submissionInfo.answers" :key="index" class="answer-item">
            <div class="question-header">
              <div class="question-title">
                <span class="question-num">{{ index + 1 }}.</span>
                <span class="question-type">[{{ answer.typeName || '未知类型' }}]</span>
                <div class="question-content" v-html="answer.content"></div>
              </div>
              <div class="question-points">
                <span>{{ answer.points || 0 }}分</span>
              </div>
            </div>

            <!-- 选项 -->
            <div class="question-options" v-if="answer.options">
              <template v-if="isObjectOptions(answer.options)">
                <div v-for="(option, key) in parseOptions(answer.options)" :key="key" class="option-item">
                  <span class="option-label">{{ key }}.</span>
                  <span class="option-content">{{ option }}</span>
                </div>
              </template>
              <div v-else class="option-text">{{ answer.options }}</div>
            </div>

            <!-- 正确答案 -->
            <div class="correct-answer">
              <span class="answer-label">正确答案:</span>
              <span class="answer-content">{{ answer.answer }}</span>
            </div>

            <!-- 学生答案 -->
            <div class="student-answer">
              <span class="answer-label">学生答案:</span>
              <span class="answer-content" :class="{ 'correct': isCorrectAnswer(answer), 'incorrect': !isCorrectAnswer(answer) }">
                {{ answer.studentAnswer || '未作答' }}
              </span>
            </div>

            <!-- 得分 -->
            <div class="score-info" v-if="submissionInfo.status === 'graded'">
              <span class="answer-label">得分:</span>
              <span class="answer-content score">{{ answer.score || 0 }} / {{ answer.points || 0 }}</span>
            </div>

            <!-- 评语 -->
            <div class="comment-info" v-if="answer.comment">
              <span class="answer-label">评语:</span>
              <span class="answer-content">{{ answer.comment }}</span>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无答案记录" />
      </div>

      <!-- 批阅模式 -->
      <div v-else class="grade-container">
        <h3>批阅作业</h3>
        <div v-if="submissionInfo.answers && submissionInfo.answers.length > 0">
          <div v-for="(answer, index) in submissionInfo.answers" :key="index" class="answer-item">
            <div class="question-header">
              <div class="question-title">
                <span class="question-num">{{ index + 1 }}.</span>
                <span class="question-type">[{{ answer.typeName || '未知类型' }}]</span>
                <div class="question-content" v-html="answer.content"></div>
              </div>
              <div class="question-points">
                <span>{{ answer.points || 0 }}分</span>
              </div>
            </div>

            <!-- 选项 -->
            <div class="question-options" v-if="answer.options">
              <template v-if="isObjectOptions(answer.options)">
                <div v-for="(option, key) in parseOptions(answer.options)" :key="key" class="option-item">
                  <span class="option-label">{{ key }}.</span>
                  <span class="option-content">{{ option }}</span>
                </div>
              </template>
              <div v-else class="option-text">{{ answer.options }}</div>
            </div>

            <!--
            正确答案 -->
            <div class="correct-answer">
              <span class="answer-label">正确答案:</span>
              <span class="answer-content">{{ answer.answer }}</span>
            </div>

            <!-- 学生答案 -->
            <div class="student-answer">
              <span class="answer-label">学生答案:</span>
              <span class="answer-content" :class="{ 'correct': isCorrectAnswer(answer), 'incorrect': !isCorrectAnswer(answer) }">
                {{ answer.studentAnswer || '未作答' }}
              </span>
            </div>

            <!-- 自动评分结果 -->
            <div class="auto-graded" v-if="answer.isAutoGraded">
              <span class="answer-label">自动评分:</span>
              <span class="answer-content">
                <el-tag :type="answer.isCorrect ? 'success' : 'danger'">
                  {{ answer.isCorrect ? '正确' : '错误' }}
                </el-tag>
              </span>
            </div>

            <!-- 教师评分 -->
            <div class="teacher-grade">
              <span class="answer-label">得分:</span>
              <span class="grade-input">
                <el-input-number 
                  v-model="answer.score" 
                  :min="0" 
                  :max="answer.points || 10" 
                  :step="0.5"
                  :precision="1"
                  @change="updateTotalScore"
                />
              </span>
            </div>

            <!-- 教师评语 -->
            <div class="teacher-comment">
              <span class="answer-label">评语:</span>
              <el-input 
                v-model="answer.comment" 
                type="textarea" 
                rows="2" 
                placeholder="请输入对此题的评语（可选）"
              />
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无答案记录" />

        <!-- 整体评语 -->
        <div class="overall-comment">
          <h3>整体评语</h3>
          <el-input 
            v-model="overallComment" 
            type="textarea" 
            rows="3" 
            placeholder="请输入对整体作业的评语（可选）"
          />
        </div>

        <!-- 提交按钮 -->
        <div class="submit-container">
          <el-button @click="cancelGrade">取消</el-button>
          <el-button type="primary" @click="submitGrade" :loading="submitting">提交批阅</el-button>
        </div>
      </div>

    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSubmissionInfo } from '@/api/exam/submission'
import { getAssignmentInfo } from '@/api/exam/assignment'
import { gradeSubmission } from '@/api/exam/submission'

const route = useRoute()
const router = useRouter()

// 页面加载状态
const pageLoading = ref(false)
// 提交状态
const submitting = ref(false)
// 是否处于批阅模式
const isGrading = ref(false)
// 整体评语
const overallComment = ref('')

// 作业信息
const assignmentInfo = reactive({
  assignmentId: '',
  title: '',
  description: '',
  totalPoints: 0,
  startTime: '',
  dueTime: ''
})

// 提交信息
const submissionInfo = reactive({
  submissionId: '',
  assignmentId: '',
  studentId: '',
  studentName: '',
  submissionTime: '',
  status: '',
  totalScore: 0,
  feedback: '',
  answers: []
})

// 生命周期钩子
onMounted(() => {
  const submissionId = route.params.id
  if (!submissionId) {
    ElMessage.error('提交ID不能为空')
    return goBack()
  }
  
  loadSubmissionData(submissionId)
})

/**
 * 加载提交数据
 */
const loadSubmissionData = async (submissionId) => {
  pageLoading.value = true
  try {
    // 获取提交详情
    const response = await getSubmissionInfo(submissionId)
    Object.assign(submissionInfo, response.data)
    
    // 获取作业信息
    if (submissionInfo.assignmentId) {
      const assignResponse = await getAssignmentInfo(submissionInfo.assignmentId)
      Object.assign(assignmentInfo, assignResponse.data)
    }
    
    // 如果已有整体评语，设置到表单中
    if (submissionInfo.feedback) {
      overallComment.value = submissionInfo.feedback
    }
    
    // 确保每个答案都有分数
    if (submissionInfo.answers && submissionInfo.answers.length > 0) {
      submissionInfo.answers.forEach(answer => {
        if (answer.score === undefined || answer.score === null) {
          // 对于自动评分的题目，如果答案正确，则给满分；否则给0分
          if (answer.isAutoGraded) {
            answer.score = answer.isCorrect ? (answer.points || 0) : 0
          } else {
            answer.score = 0
          }
        }
        
        // 添加评语字段（如果没有）
        if (!answer.comment) {
          answer.comment = ''
        }
      })
    }
  } catch (error) {
    console.error('加载提交数据失败', error)
    ElMessage.error('加载提交数据失败')
    goBack()
  } finally {
    pageLoading.value = false
  }
}

/**
 * 开始批阅
 */
const handleStartGrade = () => {
  isGrading.value = true
}

/**
 * 修改批阅
 */
const handleEditGrade = () => {
  isGrading.value = true
}

/**
 * 取消批阅
 */
const cancelGrade = () => {
  ElMessageBox.confirm('确定要取消批阅吗？所有修改将不会保存', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '继续批阅',
    type: 'warning'
  }).then(() => {
    isGrading.value = false
    // 重新加载数据，恢复原始状态
    loadSubmissionData(route.params.id)
  }).catch(() => {})
}

/**
 * 提交批阅
 */
const submitGrade = async () => {
  // 计算总分
  const totalScore = calculateTotalScore()
  
  ElMessageBox.confirm(`确认提交批阅结果？学生总得分: ${totalScore}分`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    submitting.value = true
    pageLoading.value = true
    
    try {
      // 准备提交数据
      const gradeData = {
        submissionId: submissionInfo.submissionId,
        assignmentId: submissionInfo.assignmentId,
        answers: submissionInfo.answers.map(answer => ({
          questionId: answer.questionId,
          score: answer.score || 0,
          comment: answer.comment
        })),
        feedback: overallComment.value
      }
      
      // 打印提交数据
      console.log('作业批阅提交数据:', gradeData)
      
      // 提交批阅
      await gradeSubmission(gradeData)
      ElMessage.success('批阅提交成功')
      
      // 更新状态
      isGrading.value = false
      submissionInfo.status = 'graded'
      submissionInfo.totalScore = totalScore
      submissionInfo.feedback = overallComment.value
    } catch (error) {
      console.error('提交批阅失败', error)
      ElMessage.error('提交批阅失败')
    } finally {
      submitting.value = false
      pageLoading.value = false
    }
  }).catch(() => {})
}

/**
 * 计算总分
 */
const calculateTotalScore = () => {
  if (!submissionInfo.answers || submissionInfo.answers.length === 0) {
    return 0
  }
  
  return submissionInfo.answers.reduce((total, answer) => {
    return total + (answer.score || 0)
  }, 0)
}

/**
 * 更新总分
 */
const updateTotalScore = () => {
  submissionInfo.totalScore = calculateTotalScore()
}

/**
 * 获取状态标签类型
 */
const getStatusType = (status) => {
  const statusMap = {
    'submitted': 'warning',
    'graded': 'success'
  }
  return statusMap[status] || 'info'
}

/**
 * 获取状态标签文本
 */
const getStatusLabel = (status) => {
  const statusMap = {
    'submitted': '已提交',
    'graded': '已批改'
  }
  return statusMap[status] || status
}

/**
 * 判断答案是否正确
 */
const isCorrectAnswer = (answer) => {
  if (!answer.studentAnswer || !answer.answer) {
    return false
  }
  
  return answer.studentAnswer === answer.answer
}

/**
 * 判断选项是否为对象类型
 */
const isObjectOptions = (options) => {
  if (!options) return false
  
  try {
    const parsed = typeof options === 'string' ? JSON.parse(options) : options
    return typeof parsed === 'object' && parsed !== null
  } catch (error) {
    return false
  }
}

/**
 * 解析选项
 */
const parseOptions = (options) => {
  if (!options) return {}
  
  try {
    return typeof options === 'string' ? JSON.parse(options) : options
  } catch (error) {
    return {}
  }
}

/**
 * 返回上一页
 */
const goBack = () => {
  router.go(-1)
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  
  // 如果已经是格式化的字符串，直接返回
  if (typeof dateTime === 'string' && dateTime.includes('-')) {
    return dateTime
  }
  
  // 否则格式化日期对象
  const date = new Date(dateTime)
  return `${date.getFullYear()}-${padZero(date.getMonth() + 1)}-${padZero(date.getDate())} ${padZero(date.getHours())}:${padZero(date.getMinutes())}:${padZero(date.getSeconds())}`
}

/**
 * 补零
 */
const padZero = (num) => {
  return num < 10 ? `0${num}` : num
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.basic-info {
  margin-bottom: 20px;
}

.score {
  color: #f56c6c;
  font-weight: bold;
}

.answers-container {
  margin-top: 30px;
}

.answer-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
  background-color: #fff;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.question-title {
  font-weight: bold;
  flex: 1;
}

.question-num {
  margin-right: 5px;
}

.question-type {
  color: #409eff;
  margin-right: 10px;
}

.question-content {
  margin-top: 5px;
}

.question-points {
  color: #f56c6c;
  font-weight: bold;
}

.question-options {
  margin-bottom: 15px;
  padding-left: 20px;
}

.option-item {
  margin-bottom: 5px;
}

.option-label {
  margin-right: 5px;
  font-weight: bold;
}

.correct-answer, .student-answer, .score-info, .comment-info, .auto-graded, .teacher-grade, .teacher-comment {
  margin-bottom: 15px;
}

.answer-label {
  margin-right: 10px;
  font-weight: bold;
  color: #606266;
  min-width: 70px;
  display: inline-block;
}

.correct {
  color: #67c23a;
}

.incorrect {
  color: #f56c6c;
}

.grade-input {
  display: inline-block;
}

.grade-container {
  margin-top: 30px;
}

.overall-comment {
  margin: 30px 0;
}

.submit-container {
  text-align: center;
  margin-top: 30px;
}
</style> 