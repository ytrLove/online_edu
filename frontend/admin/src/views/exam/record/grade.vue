<template>
  <div class="app-container">
    <el-card v-loading="pageLoading">
      <template #header>
        <div class="card-header">
          <span>考试批阅</span>
          <div>
            <el-button type="primary" @click="goBack">返回</el-button>
            <el-button type="success" @click="handleSubmitGrade" :disabled="!canSubmit">提交批阅</el-button>
          </div>
        </div>
      </template>

      <!-- 考试基本信息 -->
      <div class="exam-info" v-if="recordInfo.examTitle">
        <div class="exam-title">
          <h2>{{ recordInfo.examTitle }}</h2>
        </div>
        <div class="exam-meta">
          <div class="meta-item">
            <span class="meta-label">学生姓名:</span>
            <span class="meta-value">{{ recordInfo.studentName }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">开始时间:</span>
            <span class="meta-value">{{ formatDateTime(recordInfo.startTime) }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">提交时间:</span>
            <span class="meta-value">{{ formatDateTime(recordInfo.submitTime) }}</span>
          </div>
          <div class="meta-item total-score">
            <span class="meta-label">当前得分:</span>
            <span class="meta-value score">{{ calculateTotalScore() }} / {{ recordInfo.totalPoints || 100 }}</span>
          </div>
        </div>
      </div>

      <!-- 答案列表 -->
      <div class="answers-container" v-if="recordInfo.answers && recordInfo.answers.length > 0">
        <div v-for="(answer, index) in recordInfo.answers" :key="index" class="answer-item">
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

      <!-- 整体评语 -->
      <div class="overall-comment">
        <h3>整体评语</h3>
        <el-input 
          v-model="overallComment" 
          type="textarea" 
          rows="3" 
          placeholder="请输入对整体考试的评语（可选）"
        />
      </div>

      <!-- 提交按钮 -->
      <div class="submit-container">
        <el-button type="success" @click="handleSubmitGrade" :disabled="!canSubmit">提交批阅</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamRecordDetail, gradeExam } from '@/api/exam/record'

const route = useRoute()
const router = useRouter()

// 页面加载状态
const pageLoading = ref(false)
// 记录信息
const recordInfo = reactive({
  recordId: '',
  examId: '',
  examTitle: '',
  studentId: '',
  studentName: '',
  startTime: null,
  submitTime: null,
  status: '',
  totalScore: null,
  totalPoints: 100,
  answers: []
})
// 整体评语
const overallComment = ref('')

// 计算属性：是否可提交
const canSubmit = computed(() => {
  return recordInfo.answers && recordInfo.answers.length > 0
})

// 生命周期钩子
onMounted(() => {
  const recordId = route.params.id
  if (!recordId) {
    ElMessage.error('记录ID不能为空')
    return goBack()
  }
  
  loadRecordData(recordId)
})

/**
 * 加载记录数据
 */
const loadRecordData = async (recordId) => {
  pageLoading.value = true
  try {
    const response = await getExamRecordDetail(recordId)
    const data = response.data
    
    // 设置基本信息
    Object.assign(recordInfo, data)
    
    // 确保每个答案都有评分
    if (data.answers && data.answers.length > 0) {
      recordInfo.answers = data.answers.map(answer => {
        // 对于自动评分的题目，如果答案正确，则给满分；否则给0分
        if (answer.isAutoGraded && answer.score === null) {
          answer.score = answer.isCorrect ? (answer.points || 0) : 0
        }
        
        // 添加评语字段
        answer.comment = ''
        return answer
      })
    }
  } catch (error) {
    console.error('加载记录数据失败', error)
    ElMessage.error('加载记录数据失败')
    goBack()
  } finally {
    pageLoading.value = false
  }
}

/**
 * 计算总分
 */
const calculateTotalScore = () => {
  if (!recordInfo.answers || recordInfo.answers.length === 0) {
    return 0
  }
  
  return recordInfo.answers.reduce((total, answer) => {
    return total + (answer.score || 0)
  }, 0)
}

/**
 * 更新总分
 */
const updateTotalScore = () => {
  recordInfo.totalScore = calculateTotalScore()
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
 * 提交批阅
 */
const handleSubmitGrade = async () => {
  // 计算总分
  const totalScore = calculateTotalScore()
  
  // 确认提交
  ElMessageBox.confirm(`确认提交批阅结果？学生总得分: ${totalScore}分`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    pageLoading.value = true
    try {
      // 准备提交数据
      const submissionData = {
        submissionId: recordInfo.recordId,
        assignmentId: recordInfo.assignmentId || recordInfo.examId,
        answers: recordInfo.answers.map(answer => ({
          questionId: answer.questionId,
          score: answer.score || 0
        }))
      }
      
      // 如果有整体评语，添加到提交数据中
      if (overallComment.value) {
        submissionData.feedback = overallComment.value
      }
      
      // 提交批阅前打印数据
      console.log('提交批阅数据:', submissionData)
      
      // 提交批阅
      await gradeExam(submissionData)
      ElMessage.success('批阅提交成功')
      goBack()
    } catch (error) {
      console.error('提交批阅失败', error)
      ElMessage.error('提交批阅失败')
    } finally {
      pageLoading.value = false
    }
  }).catch(() => {})
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

.exam-info {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.exam-title {
  margin-bottom: 15px;
}

.exam-meta {
  display: flex;
  flex-wrap: wrap;
}

.meta-item {
  margin-right: 30px;
  margin-bottom: 10px;
}

.meta-label {
  color: #606266;
  margin-right: 5px;
}

.meta-value {
  font-weight: bold;
}

.total-score {
  color: #f56c6c;
}

.answers-container {
  margin-top: 20px;
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

.correct-answer, .student-answer, .auto-graded, .teacher-grade, .teacher-comment {
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

.overall-comment {
  margin: 30px 0;
}

.submit-container {
  text-align: center;
  margin-top: 30px;
}
</style> 