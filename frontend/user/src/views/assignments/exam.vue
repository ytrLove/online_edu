<template>
  <div class="exam-container">
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 考试信息区域 -->
    <div v-else-if="!examStarted && !examSubmitted" class="exam-info-container">
      <el-card class="exam-info-card">
        <template #header>
          <div class="card-header">
            <h2 class="exam-title">{{ examInfo.title }}</h2>
            <el-tag 
              :type="isExpired ? 'danger' : 'primary'" 
              size="large"
            >
              {{ isExpired ? '已过期' : '未开始' }}
            </el-tag>
          </div>
        </template>

        <div class="exam-info">
          <div class="info-item">
            <span class="label">所属课程：</span>
            <span class="value">{{ examInfo.courseName }}</span>
          </div>
          <div class="info-item">
            <span class="label">考试时长：</span>
            <span class="value">{{ examInfo.duration }} 分钟</span>
          </div>
          <div class="info-item">
            <span class="label">总分：</span>
            <span class="value">{{ examInfo.totalPoints }} 分</span>
          </div>
          <div class="info-item">
            <span class="label">题目数量：</span>
            <span class="value">{{ examInfo.questionCount }} 题</span>
          </div>
          <div class="info-item">
            <span class="label">开始时间：</span>
            <span class="value">{{ examInfo.startTime }}</span>
          </div>
          <div class="info-item">
            <span class="label">截止时间：</span>
            <span class="value">{{ examInfo.endTime }}</span>
          </div>
          <div class="info-item full-width" v-if="examInfo.description">
            <span class="label">考试说明：</span>
            <div class="description">{{ examInfo.description }}</div>
          </div>
        </div>

        <div class="exam-rules">
          <h3>考试须知：</h3>
          <ol>
            <li>考试开始后，计时器将自动启动，请合理安排答题时间</li>
            <li>考试过程中请勿刷新页面或关闭浏览器，否则可能导致答案丢失</li>
            <li>点击"开始考试"按钮后，将不能再返回此页面，请确保已做好准备</li>
            <li>考试时间结束后，系统将自动提交当前的答案</li>
          </ol>
        </div>

        <div class="exam-actions">
          <el-button 
            type="primary" 
            size="large" 
            @click="startExam" 
            :disabled="isExpired"
          >
            开始考试
          </el-button>
          <el-button @click="goBack">返回</el-button>
        </div>
      </el-card>
    </div>

    <!-- 考试内容区域 -->
    <div v-else-if="examStarted && !examSubmitted" class="exam-content-container">
      <!-- 顶部信息栏 -->
      <div class="exam-header">
        <h2 class="exam-title">{{ examInfo.title }}</h2>
        <div class="exam-timer" :class="{ 'warning': timeLeft < 300 }">
          <el-icon><Timer /></el-icon>
          <span>剩余时间：{{ formatTimeLeft }}</span>
        </div>
      </div>

      <!-- 考试内容 -->
      <div class="exam-content">
        <div class="question-navigation">
          <div class="question-nav-title">题目导航</div>
          <div class="question-buttons">
            <el-button 
              v-for="(_, index) in questions" 
              :key="index"
              :type="currentQuestionIndex === index ? 'primary' : hasAnswer(index) ? 'success' : 'default'"
              size="small"
              @click="goToQuestion(index)"
            >
              {{ index + 1 }}
            </el-button>
          </div>
          <div class="question-stats">
            <div class="stat-item">
              <div class="stat-label">已答：</div>
              <div class="stat-value">{{ answeredCount }}/{{ questions.length }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">未答：</div>
              <div class="stat-value">{{ questions.length - answeredCount }}</div>
            </div>
          </div>
          <el-button 
            type="danger" 
            @click="confirmSubmit"
            size="large"
            style="margin-top: 15px; width: 100%"
          >
            交卷
          </el-button>
        </div>

        <div class="question-container">
          <div v-if="currentQuestion" class="question">
            <div class="question-header">
              <div class="question-title">
                <span class="question-index">{{ currentQuestionIndex + 1 }}.</span>
                <span class="question-type-tag">[{{ getQuestionTypeName(currentQuestion.type) }}]</span>
                <span class="question-score">({{ currentQuestion.score }}分)</span>
              </div>
            </div>
            
            <div class="question-content">{{ currentQuestion.content }}</div>

            <!-- 单选题 -->
            <div v-if="currentQuestion.type === 'single'" class="question-options">
              <el-radio-group v-model="answers[currentQuestionIndex]">
                <el-radio 
                  v-for="option in currentQuestion.options" 
                  :key="option.id" 
                  :label="option.id"
                  class="option-item"
                >
                  {{ option.label }}. {{ option.content }}
                </el-radio>
              </el-radio-group>
            </div>

            <!-- 多选题 -->
            <div v-else-if="currentQuestion.type === 'multiple'" class="question-options">
              <el-checkbox-group v-model="answers[currentQuestionIndex]">
                <el-checkbox 
                  v-for="option in currentQuestion.options" 
                  :key="option.id" 
                  :label="option.id"
                  class="option-item"
                >
                  {{ option.label }}. {{ option.content }}
                </el-checkbox>
              </el-checkbox-group>
            </div>

            <!-- 判断题 -->
            <div v-else-if="currentQuestion.type === 'judgment'" class="question-options">
              <el-radio-group v-model="answers[currentQuestionIndex]">
                <el-radio label="true" class="option-item">正确</el-radio>
                <el-radio label="false" class="option-item">错误</el-radio>
              </el-radio-group>
            </div>

            <!-- 填空题 -->
            <div v-else-if="currentQuestion.type === 'fill'" class="question-blank">
              <el-input 
                v-model="answers[currentQuestionIndex]" 
                type="text" 
                placeholder="请输入答案"
              />
            </div>

            <!-- 简答题 -->
            <div v-else-if="currentQuestion.type === 'essay'" class="question-essay">
              <el-input 
                v-model="answers[currentQuestionIndex]" 
                type="textarea" 
                :rows="6" 
                placeholder="请输入答案"
              />
            </div>
          </div>

          <div class="question-navigation-buttons">
            <el-button 
              :disabled="currentQuestionIndex === 0" 
              @click="prevQuestion"
            >
              上一题
            </el-button>
            <el-button 
              :disabled="currentQuestionIndex === questions.length - 1" 
              @click="nextQuestion" 
              type="primary"
            >
              下一题
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 考试已提交 -->
    <div v-else-if="examSubmitted" class="exam-submitted-container">
      <el-result
        icon="success"
        title="考试已提交"
        sub-title="您的答案已成功提交，请等待教师批阅"
      >
        <template #extra>
          <div class="submission-info">
            <div class="info-item">
              <span class="label">提交时间：</span>
              <span class="value">{{ formatDate(submissionTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">答题数量：</span>
              <span class="value">{{ answeredCount }}/{{ questions.length }}</span>
            </div>
          </div>
          <el-button type="primary" @click="goToAssignmentsList">返回作业列表</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/date'
import { Timer } from '@element-plus/icons-vue'
import { getExamDetail, startExam as startExamApi, submitExam as submitExamApi } from '@/api/assignment'
import { recordBehavior } from '@/api/behavior'

const route = useRoute()
const router = useRouter()
const examId = route.params.id

// 数据状态
const loading = ref(true)
const examStarted = ref(false)
const examSubmitted = ref(false)
const submissionTime = ref(null)
const currentQuestionIndex = ref(0)
const timeLeft = ref(0)
const timerInterval = ref(null)
const recordId = ref(null)
const startTime = ref(null)
const endTime = ref(null)
// 考试信息
const examInfo = ref({
  examId: '',
  title: '',
  courseId: '',
  courseName: '',
  description: '',
  startTime: '',
  endTime: '',
  duration: 0,
  totalPoints: 0,
  status: '',
  questionCount: 0
})

// 题目列表
const questions = ref([])

// 答案
const answers = ref([])

// 获取当前题目
const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value] || null
})

// 已回答题目数量
const answeredCount = computed(() => {
  return answers.value.filter(answer => {
    if (Array.isArray(answer)) {
      return answer.length > 0
    }
    return answer !== undefined && answer !== null && answer !== ''
  }).length
})

// 是否已过期
const isExpired = computed(() => {
  if (!examInfo.value.endTime) return false
  return new Date() > new Date(examInfo.value.endTime)
})

// 格式化剩余时间
const formatTimeLeft = computed(() => {
  const hours = Math.floor(timeLeft.value / 3600)
  const minutes = Math.floor((timeLeft.value % 3600) / 60)
  const seconds = timeLeft.value % 60
  
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

// 生命周期钩子
onMounted(() => {
  if (!examId) {
    ElMessage.error('缺少考试ID')
    router.push('/assignments')
    return
  }
  
  loadExamInfo()
})

// 组件销毁前清除定时器
onBeforeUnmount(() => {
  clearInterval(timerInterval.value)
})

// 加载考试信息
const loadExamInfo = async () => {
  loading.value = true
  try {
    // 调用API获取考试信息
    const response = await getExamDetail(examId)
    
    // 处理响应数据
    if (response.code === 200 && response.data) {
      const data = response.data
      
      // 填充考试信息
      examInfo.value = {
        examId: data.examId,
        title: data.title,
        courseId: data.courseId,
        courseName: data.courseName,
        description: data.description,
        startTime: data.startTime,
        endTime: data.endTime,
        duration: data.duration,
        totalPoints: data.totalPoints,
        status: data.status,
        questionCount: data.questions ? data.questions.length : 0
      }
      
      // 如果已经开始考试，直接进入考试界面
      if (data.recordId) {
        recordId.value = data.recordId
        examStarted.value = true
        
        // 如果已经提交，则显示提交结果页面
        if (data.submitted) {
          examSubmitted.value = true
          submissionTime.value = data.endExamTime
        } else {
          // 计算剩余时间
          if (data.startExamTime) {
            const startTime = new Date(data.startExamTime).getTime()
            const now = new Date().getTime()
            const elapsedSeconds = Math.floor((now - startTime) / 1000)
            const totalSeconds = data.duration * 60
            timeLeft.value = Math.max(0, totalSeconds - elapsedSeconds)
            
            // 如果时间还未结束，开始计时器
            if (timeLeft.value > 0) {
              startTimer()
            } else {
              // 时间已经结束，自动提交
              ElMessage.warning('考试时间已结束，系统将自动提交答案')
              submitExam()
            }
          }
        }
      }
      
      // 处理题目
      if (data.questions && data.questions.length > 0) {
        questions.value = data.questions.map(q => {
          // 将后端题目类型转换为前端类型
          let type = 'essay'
          if (q.typeName.includes('单选')) {
            type = 'single'
          } else if (q.typeName.includes('多选')) {
            type = 'multiple'
          } else if (q.typeName.includes('判断')) {
            type = 'judgment'
          } else if (q.typeName.includes('填空')) {
            type = 'fill'
          }
          
          // 处理选项，将JSON字符串转换为对象
          let options = []
          if (q.options) {
            try {
              const parsed = JSON.parse(q.options)
              options = Object.keys(parsed).map(key => ({
                id: key,
                label: key.toUpperCase(),
                content: parsed[key]
              }))
            } catch (e) {
              console.error('解析选项失败', e)
            }
          }
          
          return {
            id: q.questionId,
            type: type,
            content: q.content,
            score: q.points,
            options: options,
            studentAnswer: q.studentAnswer
          }
        })
        
        // 初始化答案数组
        initAnswers()
      }
    } else {
      ElMessage.error('获取考试信息失败')
    }
  } catch (error) {
    console.error('获取考试信息失败', error)
    ElMessage.error('获取考试信息失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 初始化答案数组
const initAnswers = () => {
  answers.value = questions.value.map(question => {
    // 如果已有答案，则使用已有答案
    if (question.studentAnswer) {
      if (question.type === 'multiple') {
        return question.studentAnswer.split(',')
      }
      return question.studentAnswer
    }
    
    // 否则初始化为空
    if (question.type === 'multiple') {
      return []
    }
    return ''
  })
}

// 开始考试
const startExam = async () => {
  if (isExpired.value) {
    ElMessage.warning('该考试已过期，无法参加')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      '确定要开始考试吗？考试开始后计时器将启动，且不可暂停。',
      '开始考试',
      {
        confirmButtonText: '确定开始',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    startTime.value = Date.now()
    // 调用开始考试API
    const response = await startExamApi(examId)
    
    if (response.code === 200 && response.data) {
      recordId.value = response.data
      examStarted.value = true
      timeLeft.value = examInfo.value.duration * 60
      startTimer()
      
      ElMessage.success('考试已开始，请注意合理安排时间')
    } else {
      // 提供更具体的错误信息
      if (response.code === 200 && response.data === null) {
        // 检查是否已结束考试或已提交
        if (isExpired.value) {
          ElMessage.error('考试已结束，无法开始考试')
        } else if (examInfo.value.submitted) {
          ElMessage.error('您已提交过本次考试，不能重复参加')
        } else {
          ElMessage.error('无法开始考试，请联系管理员')
        }
      } else {
        ElMessage.error(response.msg || '开始考试失败')
      }
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消开始，不做处理
      return
    }
    
    console.error('开始考试失败', error)
    ElMessage.error('开始考试失败：' + (error.message || '未知错误'))
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 判断题目是否已回答
const hasAnswer = (index) => {
  const answer = answers.value[index]
  if (Array.isArray(answer)) {
    return answer.length > 0
  }
  return answer !== undefined && answer !== null && answer !== ''
}

// 启动计时器
const startTimer = () => {
  timerInterval.value = setInterval(() => {
    if (timeLeft.value > 0) {
      timeLeft.value--
    } else {
      // 时间到，自动提交
      clearInterval(timerInterval.value)
      ElMessage.warning('考试时间已结束，系统将自动提交答案')
      submitExam()
    }
    
    // 如果剩余时间少于5分钟，弹出提醒
    if (timeLeft.value === 300) {
      ElMessage.warning('剩余考试时间不足5分钟，请抓紧时间作答')
    }
  }, 1000)
}

// 导航到指定题目
const goToQuestion = (index) => {
  if (index >= 0 && index < questions.value.length) {
    currentQuestionIndex.value = index
  }
}

// 上一题
const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

// 下一题
const nextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++
  }
}

// 确认提交
const confirmSubmit = () => {
  const unansweredCount = questions.value.length - answeredCount.value
  
  let message = '确定要提交考试答案吗？'
  if (unansweredCount > 0) {
    message = `您还有${unansweredCount}道题未作答，确定要提交吗？`
  }
  
  ElMessageBox.confirm(
    message,
    '提交确认',
    {
      confirmButtonText: '确定提交',
      cancelButtonText: '继续答题',
      type: 'warning'
    }
  )
    .then(() => {
      endTime.value = Date.now()
      submitExam()
      recordBehavior({
        behaviorType: 'take_exam',
        courseId: examInfo.value.courseId,
        duration: Math.floor((endTime.value - startTime.value) / 1000), // 转换为秒
      })
    })
    .catch(() => {
      // 取消提交，继续答题
    })
}

// 提交考试
const submitExam = async () => {
  try {
    // 停止计时器
    clearInterval(timerInterval.value)
    
    // 准备提交数据
    const submitData = {
      submissionId: recordId.value,
      assignmentId: examId, // 按照后端SubmissionParam参数要求传递
      answers: answers.value.map((answer, index) => {
        const question = questions.value[index]
        return {
          questionId: question.id,
          studentAnswer: Array.isArray(answer) ? answer.join(',') : answer
        }
      })
    }
    
    // 发送API请求
    const response = await submitExamApi(submitData)
    
    if (response.code === 200) {
      // 记录提交时间
      submissionTime.value = new Date().toISOString()
      
      // 更新状态
      examSubmitted.value = true
      examStarted.value = false
      
      ElMessage.success('考试提交成功')
    } else {
      ElMessage.error(response.msg || '提交考试失败')
      // 提交失败，继续计时
      startTimer()
    }
  } catch (error) {
    console.error('提交考试失败', error)
    ElMessage.error('提交考试失败：' + (error.message || '未知错误'))
    
    // 提交失败，继续计时
    startTimer()
  }
}

// 返回作业列表
const goToAssignmentsList = () => {
  router.push('/assignments')
}

// 获取题型名称
const getQuestionTypeName = (type) => {
  const typeMap = {
    'single': '单选题',
    'multiple': '多选题',
    'judgment': '判断题',
    'fill': '填空题',
    'essay': '简答题'
  }
  return typeMap[type] || '未知题型'
}
</script>

<style scoped>
.exam-container {
  padding: 20px;
}

.loading-container {
  padding: 40px;
}

/* 考试信息样式 */
.exam-info-container {
  max-width: 800px;
  margin: 0 auto;
}

.exam-info-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-title {
  margin: 0;
  font-size: 20px;
}

.exam-info {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.info-item {
  flex: 0 0 50%;
  margin-bottom: 15px;
}

.info-item.full-width {
  flex: 0 0 100%;
}

.label {
  font-weight: bold;
  color: #606266;
}

.value {
  color: #303133;
}

.description {
  margin-top: 10px;
  color: #606266;
  line-height: 1.6;
}

.exam-rules {
  background-color: #f8f8f8;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.exam-rules h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}

.exam-rules ol {
  padding-left: 20px;
  margin: 0;
}

.exam-rules li {
  line-height: 1.8;
  color: #606266;
}

.exam-actions {
  text-align: center;
  margin-top: 30px;
}

/* 考试内容样式 */
.exam-content-container {
  max-width: 1200px;
  margin: 0 auto;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: #fff;
  padding: 15px;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.exam-timer {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
  color: #409EFF;
}

.exam-timer.warning {
  color: #E6A23C;
  animation: blink 1s infinite;
}

.exam-timer .el-icon {
  margin-right: 5px;
}

.exam-content {
  display: flex;
  gap: 20px;
}

.question-navigation {
  flex: 0 0 200px;
  background-color: #fff;
  padding: 15px;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.question-nav-title {
  font-weight: bold;
  margin-bottom: 15px;
  text-align: center;
  color: #606266;
}

.question-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.question-stats {
  margin-top: 20px;
  border-top: 1px solid #EBEEF5;
  padding-top: 15px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.stat-label {
  color: #606266;
}

.stat-value {
  font-weight: bold;
}

.question-container {
  flex: 1;
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.question {
  margin-bottom: 30px;
}

.question-header {
  margin-bottom: 15px;
}

.question-title {
  font-size: 16px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.question-index {
  font-weight: bold;
}

.question-type-tag {
  color: #409EFF;
  font-size: 14px;
}

.question-score {
  color: #E6A23C;
  font-size: 14px;
}

.question-content {
  margin-bottom: 20px;
  line-height: 1.6;
  color: #303133;
}

.question-options {
  padding-left: 20px;
}

.option-item {
  margin-bottom: 12px;
  display: block;
}

.question-blank,
.question-essay {
  margin-top: 15px;
}

.question-navigation-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
}

/* 提交结果样式 */
.exam-submitted-container {
  max-width: 800px;
  margin: 0 auto;
}

.submission-info {
  text-align: center;
  margin-bottom: 20px;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@media (max-width: 768px) {
  .exam-content {
    flex-direction: column;
  }
  
  .question-navigation {
    flex: none;
    width: 100%;
    margin-bottom: 20px;
  }
  
  .info-item {
    flex: 0 0 100%;
  }
}
</style> 