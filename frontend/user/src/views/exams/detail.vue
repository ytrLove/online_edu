<template>
  <div class="exam-detail-container" v-loading="loading">
    <!-- 考试基本信息 -->
    <el-card class="exam-info-card" v-if="!isExamStarted">
      <template #header>
        <div class="card-header">
          <h2>{{ examInfo.title }}</h2>
          <div class="exam-status">
            <el-tag :type="getStatusType(examInfo.status)">
              {{ getStatusText(examInfo.status) }}
            </el-tag>
          </div>
        </div>
      </template>
      <div class="exam-info">
        <div class="info-item">
          <span class="info-label">所属课程：</span>
          <span class="info-value">{{ examInfo.courseName }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">开始时间：</span>
          <span class="info-value">{{ examInfo.startTime }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">结束时间：</span>
          <span class="info-value">{{ examInfo.endTime }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">考试时长：</span>
          <span class="info-value">{{ examInfo.duration }}分钟</span>
        </div>
        <div class="info-item">
          <span class="info-label">总分：</span>
          <span class="info-value">{{ examInfo.totalScore }}分</span>
        </div>
        <div class="info-item">
          <span class="info-label">试卷类型：</span>
          <span class="info-value">{{ examInfo.paperType || '标准试卷' }}</span>
        </div>
        <div class="info-item full-width" v-if="examInfo.description">
          <span class="info-label">考试说明：</span>
          <div class="info-description">{{ examInfo.description }}</div>
        </div>
      </div>

      <!-- 考试按钮 -->
      <div class="exam-actions" v-if="isInProgress && !isEnded">
        <el-button type="primary" @click="startExam">开始考试</el-button>
      </div>

      <!-- 考试结束或未开始 -->
      <div v-else-if="!isInProgress" class="exam-notice">
        <el-alert
          :title="isNotStarted ? '考试尚未开始' : '考试已结束'"
          :type="isNotStarted ? 'info' : 'warning'"
          :description="isNotStarted ? '请在考试开始时间后参加考试' : '考试已结束，您可以查看成绩'"
          show-icon
        />
      </div>

      <!-- 考试成绩 -->
      <div v-if="isEnded && resultInfo.score !== undefined" class="result-preview">
        <el-divider content-position="center">考试成绩</el-divider>
        <div class="score-summary">
          <div class="score-value">{{ resultInfo.score }}</div>
          <div class="score-total">/ {{ examInfo.totalScore }}分</div>
        </div>
        <el-button type="primary" @click="viewExamResult">查看详情</el-button>
      </div>
    </el-card>

    <!-- 考试进行中 -->
    <div v-if="isExamStarted" class="exam-content">
      <!-- 考试顶部信息栏 -->
      <div class="exam-header">
        <div class="exam-title">{{ examInfo.title }}</div>
        <div class="exam-timer">
          <el-alert
            :title="`剩余时间: ${formatRemainingTime}`"
            type="warning"
            :closable="false"
            show-icon
          />
        </div>
        <div class="exam-progress">
          <div>已答题目: {{ answeredCount }} / {{ totalQuestions }}</div>
          <el-progress :percentage="progressPercentage" :stroke-width="10"></el-progress>
        </div>
      </div>

      <!-- 试卷内容 -->
      <el-card class="exam-paper-card">
        <el-form ref="answerFormRef" :model="answerForm" label-position="top">
          <div
            v-for="(section, sectionIndex) in examPaper.sections"
            :key="sectionIndex"
            class="paper-section"
          >
            <h3 class="section-title">{{ section.title || `第${sectionIndex + 1}部分` }}</h3>
            <div v-if="section.description" class="section-desc">{{ section.description }}</div>

            <div
              v-for="(question, questionIndex) in section.questions"
              :key="question.questionId"
              class="question-item"
            >
              <div class="question-header">
                <span class="question-index">{{ getQuestionIndex(sectionIndex, questionIndex) }}.</span>
                <span class="question-type">【{{ getQuestionType(question.typeId) }}】</span>
                <span class="question-score">({{ question.score }}分)</span>
              </div>
              <div class="question-content">{{ question.content }}</div>

              <!-- 选择题 -->
              <div v-if="question.typeId === 1 || question.typeId === 2" class="question-options">
                <template v-if="question.typeId === 1">
                  <!-- 单选题 -->
                  <el-radio-group v-model="answerForm.answers[question.questionId]">
                    <el-radio
                      v-for="option in question.options"
                      :key="option.optionId"
                      :label="option.optionId"
                    >
                      {{ option.optionId }}. {{ option.content }}
                    </el-radio>
                  </el-radio-group>
                </template>
                <template v-else>
                  <!-- 多选题 -->
                  <el-checkbox-group v-model="answerForm.answers[question.questionId]">
                    <el-checkbox
                      v-for="option in question.options"
                      :key="option.optionId"
                      :label="option.optionId"
                    >
                      {{ option.optionId }}. {{ option.content }}
                    </el-checkbox>
                  </el-checkbox-group>
                </template>
              </div>

              <!-- 填空题 -->
              <div v-else-if="question.typeId === 3" class="question-input">
                <el-input
                  v-model="answerForm.answers[question.questionId]"
                  placeholder="请输入答案"
                  type="text"
                ></el-input>
              </div>

              <!-- 判断题 -->
              <div v-else-if="question.typeId === 4" class="question-judge">
                <el-radio-group v-model="answerForm.answers[question.questionId]">
                  <el-radio label="T">正确</el-radio>
                  <el-radio label="F">错误</el-radio>
                </el-radio-group>
              </div>

              <!-- 简答题 -->
              <div v-else-if="question.typeId === 5" class="question-textarea">
                <el-input
                  v-model="answerForm.answers[question.questionId]"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入答案"
                  maxlength="1000"
                  show-word-limit
                ></el-input>
              </div>
            </div>
          </div>

          <div class="submit-actions">
            <el-button type="primary" @click="submitExam" :loading="submitting">
              提交试卷
            </el-button>
            <el-button @click="autoSave" :loading="saving">保存答案</el-button>
          </div>
        </el-form>
      </el-card>
    </div>

    <!-- 考试结果 -->
    <div v-if="showResult" class="exam-result">
      <el-card class="result-card">
        <template #header>
          <div class="card-header">
            <h2>考试成绩</h2>
            <div class="total-score">
              <span class="score-label">总得分：</span>
              <span class="score-value">{{ resultInfo.score }} / {{ examInfo.totalScore }}分</span>
            </div>
          </div>
        </template>

        <div class="score-details">
          <div class="submit-info">
            <div class="info-item">
              <span class="info-label">考试时间：</span>
              <span class="info-value">{{ resultInfo.examTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">用时：</span>
              <span class="info-value">{{ resultInfo.usedTime || '-' }}分钟</span>
            </div>
            <div class="info-item">
              <span class="info-label">正确题数：</span>
              <span class="info-value">{{ resultInfo.correctCount || 0 }} / {{ totalQuestions }}</span>
            </div>
          </div>

          <el-divider content-position="center">得分分布</el-divider>
          
          <div class="score-distribution">
            <div
              v-for="(section, index) in resultInfo.sectionScores"
              :key="index"
              class="section-score"
            >
              <div class="section-name">{{ section.title || `第${index + 1}部分` }}</div>
              <div class="section-points">
                <span class="points">{{ section.score }}</span>
                <span class="total-points">/ {{ section.totalScore }}分</span>
              </div>
              <el-progress
                :percentage="Math.round((section.score / section.totalScore) * 100)"
                :format="format => `${format}%`"
                :stroke-width="10"
              ></el-progress>
            </div>
          </div>
        </div>

        <el-divider content-position="center">答题详情</el-divider>
        
        <el-collapse v-model="activeNames">
          <el-collapse-item
            v-for="(section, sIndex) in resultInfo.sections"
            :key="sIndex"
            :title="`${section.title || `第${sIndex + 1}部分`} (${section.score}/${section.totalScore}分)`"
            :name="sIndex"
          >
            <div
              v-for="(answer, qIndex) in section.questions"
              :key="answer.questionId"
              class="question-result"
            >
              <div class="question-result-header">
                <div class="question-index-title">
                  <span class="question-index">{{ getQuestionIndex(sIndex, qIndex) }}.</span>
                  <span class="question-content">{{ answer.questionContent }}</span>
                </div>
                <div class="question-result-score" :class="{ correct: answer.isCorrect, wrong: !answer.isCorrect }">
                  {{ answer.score }} / {{ answer.maxScore }}分
                </div>
              </div>
              
              <div class="answer-details">
                <div class="answer-item">
                  <div class="answer-label">我的答案：</div>
                  <div class="my-answer" :class="{ correct: answer.isCorrect, wrong: !answer.isCorrect }">
                    {{ answer.answer || '未作答' }}
                  </div>
                </div>
                
                <div class="answer-item" v-if="examInfo.showAnswer">
                  <div class="answer-label">正确答案：</div>
                  <div class="correct-answer">{{ answer.correctAnswer }}</div>
                </div>
                
                <div class="answer-item" v-if="answer.explanation">
                  <div class="answer-label">解析：</div>
                  <div class="answer-explanation">{{ answer.explanation }}</div>
                </div>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
        
        <div class="back-action">
          <el-button @click="showResult = false">返回</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamDetail, getExamPaper, submitExamPaper, getExamResult } from '@/api/exam'

const route = useRoute()
const router = useRouter()
const examId = computed(() => route.params.id)

// 数据状态
const loading = ref(false)
const submitting = ref(false)
const saving = ref(false)
const isExamStarted = ref(false)
const showResult = ref(false)
const activeNames = ref([0]) // 默认展开第一个部分

// 考试信息
const examInfo = ref({
  title: '',
  courseId: '',
  courseName: '',
  description: '',
  startTime: '',
  endTime: '',
  duration: 0,
  totalScore: 0,
  paperType: '',
  status: 'not_started',
  showAnswer: false
})

// 试卷内容
const examPaper = ref({
  paperId: '',
  title: '',
  totalScore: 0,
  sections: []
})

// 答案表单
const answerFormRef = ref(null)
const answerForm = reactive({
  paperId: '',
  examId: '',
  answers: {} // 题目ID -> 答案
})

// 成绩信息
const resultInfo = ref({
  score: 0,
  examTime: '',
  usedTime: 0,
  correctCount: 0,
  sectionScores: [],
  sections: []
})

// 剩余时间相关
const startExamTime = ref(0) // 开始考试的时间戳
const currentExamTime = ref(0) // 当前时间戳
const examTimerInterval = ref(null)

// 总题目数
const totalQuestions = computed(() => {
  return examPaper.value.sections.reduce((count, section) => {
    return count + (section.questions ? section.questions.length : 0)
  }, 0)
})

// 已答题目数
const answeredCount = computed(() => {
  const answers = answerForm.answers
  return Object.keys(answers).filter(key => {
    const answer = answers[key]
    if (Array.isArray(answer)) {
      return answer.length > 0
    }
    return answer !== '' && answer !== null && answer !== undefined
  }).length
})

// 完成百分比
const progressPercentage = computed(() => {
  if (totalQuestions.value === 0) return 0
  return Math.floor((answeredCount.value / totalQuestions.value) * 100)
})

// 剩余时间（秒）
const remainingSeconds = computed(() => {
  if (!startExamTime.value || !currentExamTime.value) return examInfo.value.duration * 60
  
  const elapsedSeconds = Math.floor((currentExamTime.value - startExamTime.value) / 1000)
  const totalExamSeconds = examInfo.value.duration * 60
  return Math.max(0, totalExamSeconds - elapsedSeconds)
})

// 格式化剩余时间
const formatRemainingTime = computed(() => {
  const seconds = remainingSeconds.value
  
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
})

// 判断考试状态
const isNotStarted = computed(() => examInfo.value.status === 'not_started')
const isInProgress = computed(() => examInfo.value.status === 'in_progress')
const isEnded = computed(() => examInfo.value.status === 'ended')

// 本地存储键
const localStorageKey = computed(() => `exam_answers_${examId.value}`)

// 自动保存定时器
const autoSaveInterval = ref(null)

// 初始化
onMounted(async () => {
  if (!examId.value) {
    ElMessage.error('考试ID不能为空')
    router.push('/assignments')
    return
  }
  
  loading.value = true
  try {
    await fetchExamDetail()
    
    // 如果考试已结束，尝试获取成绩
    if (isEnded.value) {
      await fetchExamResult()
    }
  } catch (error) {
    console.error('获取考试信息失败', error)
    ElMessage.error('获取考试信息失败')
  } finally {
    loading.value = false
  }
})

// 组件销毁前停止定时器
onBeforeUnmount(() => {
  stopExamTimer()
  stopAutoSave()
  
  // 如果考试进行中，自动保存答案
  if (isExamStarted.value && !isEnded.value) {
    saveAnswers()
  }
})

// 监听剩余时间，自动提交
watch(remainingSeconds, (newValue) => {
  if (newValue <= 0 && isExamStarted.value) {
    ElMessage.warning('考试时间已到，系统将自动提交')
    submitExam()
  }
})

// 获取考试详情
const fetchExamDetail = async () => {
  try {
    const result = await getExamDetail(examId.value)
    examInfo.value = result.data
  } catch (error) {
    console.error('获取考试详情失败', error)
    throw error
  }
}

// 获取试卷内容
const fetchExamPaper = async () => {
  try {
    const result = await getExamPaper(examInfo.value.paperId)
    examPaper.value = result.data
    
    // 初始化表单
    answerForm.paperId = examPaper.value.paperId
    answerForm.examId = examId.value
    
    // 初始化答案对象
    examPaper.value.sections.forEach(section => {
      if (section.questions) {
        section.questions.forEach(question => {
          // 根据题型设置默认值
          if (question.typeId === 1 || question.typeId === 3 || question.typeId === 4) {
            // 单选、填空、判断
            answerForm.answers[question.questionId] = ''
          } else if (question.typeId === 2) {
            // 多选
            answerForm.answers[question.questionId] = []
          } else {
            // 简答
            answerForm.answers[question.questionId] = ''
          }
        })
      }
    })
    
    // 恢复本地保存的答案
    loadSavedAnswers()
  } catch (error) {
    console.error('获取试卷失败', error)
    throw error
  }
}

// 获取考试结果
const fetchExamResult = async () => {
  try {
    const result = await getExamResult(examId.value)
    resultInfo.value = result.data
  } catch (error) {
    console.error('获取考试结果失败', error)
  }
}

// 开始考试
const startExam = async () => {
  if (!isInProgress.value) {
    ElMessage.warning('当前不在考试时间范围内，无法开始考试')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `您即将开始考试，考试时长 ${examInfo.value.duration} 分钟，开始后计时不会暂停，确定现在开始吗？`,
      '开始考试',
      {
        confirmButtonText: '开始考试',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch (e) {
    return // 用户取消
  }
  
  loading.value = true
  try {
    // 获取试卷内容
    await fetchExamPaper()
    
    // 标记考试开始
    isExamStarted.value = true
    
    // 开始计时
    startExamTime.value = Date.now()
    startExamTimer()
    
    // 启动自动保存
    startAutoSave()
  } catch (error) {
    console.error('开始考试失败', error)
    ElMessage.error('获取试卷失败，请重试')
  } finally {
    loading.value = false
  }
}

// 开始计时器
const startExamTimer = () => {
  if (examTimerInterval.value) return
  
  currentExamTime.value = Date.now()
  examTimerInterval.value = setInterval(() => {
    currentExamTime.value = Date.now()
  }, 1000)
}

// 停止计时器
const stopExamTimer = () => {
  if (examTimerInterval.value) {
    clearInterval(examTimerInterval.value)
    examTimerInterval.value = null
  }
}

// 开始自动保存
const startAutoSave = () => {
  if (autoSaveInterval.value) return
  
  // 每60秒自动保存一次
  autoSaveInterval.value = setInterval(() => {
    autoSave()
  }, 60000)
}

// 停止自动保存
const stopAutoSave = () => {
  if (autoSaveInterval.value) {
    clearInterval(autoSaveInterval.value)
    autoSaveInterval.value = null
  }
}

// 自动保存
const autoSave = () => {
  saveAnswers(true)
}

// 保存答案到本地
const saveAnswers = (isAuto = false) => {
  if (isAuto) {
    // 自动保存不显示加载状态
    try {
      localStorage.setItem(localStorageKey.value, JSON.stringify(answerForm.answers))
      console.log('答案已自动保存')
    } catch (error) {
      console.error('自动保存答案失败', error)
    }
  } else {
    saving.value = true
    try {
      localStorage.setItem(localStorageKey.value, JSON.stringify(answerForm.answers))
      ElMessage.success('答案已保存到本地')
    } catch (error) {
      console.error('保存答案失败', error)
      ElMessage.error('保存答案失败')
    } finally {
      saving.value = false
    }
  }
}

// 从本地加载已保存的答案
const loadSavedAnswers = () => {
  try {
    const savedAnswers = localStorage.getItem(localStorageKey.value)
    if (savedAnswers) {
      const parsedAnswers = JSON.parse(savedAnswers)
      // 合并到现有答案中
      Object.keys(parsedAnswers).forEach(questionId => {
        if (answerForm.answers.hasOwnProperty(questionId)) {
          answerForm.answers[questionId] = parsedAnswers[questionId]
        }
      })
      ElMessage.info('已恢复本地保存的答案')
    }
  } catch (error) {
    console.error('加载已保存答案失败', error)
  }
}

// 提交试卷
const submitExam = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要提交试卷吗？提交后将无法修改',
      '提交确认',
      {
        confirmButtonText: '确定提交',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch (e) {
    return // 用户取消提交
  }
  
  submitting.value = true
  try {
    // 转换为提交格式
    const data = {
      paperId: answerForm.paperId,
      examId: answerForm.examId,
      usedTime: Math.ceil((Date.now() - startExamTime.value) / (1000 * 60)), // 使用时间（分钟）
      answers: Object.keys(answerForm.answers).map(questionId => ({
        questionId,
        answer: Array.isArray(answerForm.answers[questionId]) 
          ? answerForm.answers[questionId].join(',') 
          : answerForm.answers[questionId]
      }))
    }
    
    const result = await submitExamPaper(data)
    ElMessage.success('试卷提交成功')
    
    // 停止计时和自动保存
    stopExamTimer()
    stopAutoSave()
    
    // 提交成功后清除本地存储
    localStorage.removeItem(localStorageKey.value)
    
    // 获取考试结果
    resultInfo.value = result.data
    
    // 显示结果页面
    isExamStarted.value = false
    showResult.value = true
  } catch (error) {
    console.error('提交试卷失败', error)
    ElMessage.error('提交试卷失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 查看考试结果
const viewExamResult = () => {
  showResult.value = true
}

// 获取题目序号
const getQuestionIndex = (sectionIndex, questionIndex) => {
  let index = questionIndex + 1
  
  // 如果前面有部分，累加之前部分的题目数量
  for (let i = 0; i < sectionIndex; i++) {
    if (examPaper.value.sections[i] && examPaper.value.sections[i].questions) {
      index += examPaper.value.sections[i].questions.length
    }
  }
  
  return index
}

// 获取题型文本
const getQuestionType = (typeId) => {
  const typeMap = {
    1: '单选题',
    2: '多选题',
    3: '填空题',
    4: '判断题',
    5: '简答题'
  }
  return typeMap[typeId] || '未知题型'
}

// 获取考试状态类型（用于标签颜色）
const getStatusType = (status) => {
  const statusMap = {
    'not_started': 'info',
    'in_progress': 'warning',
    'ended': 'success'
  }
  return statusMap[status] || 'info'
}

// 获取考试状态文本
const getStatusText = (status) => {
  const statusMap = {
    'not_started': '未开始',
    'in_progress': '进行中',
    'ended': '已结束'
  }
  return statusMap[status] || '未知'
}
</script>

<style scoped>
.exam-detail-container {
  padding: 20px;
}

.exam-info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  font-size: 18px;
  margin: 0;
}

.exam-status {
  display: flex;
  align-items: center;
}

.exam-info {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 15px;
}

.info-item {
  flex: 0 0 50%;
  margin-bottom: 10px;
}

.info-item.full-width {
  flex: 0 0 100%;
}

.info-label {
  font-weight: bold;
  color: #606266;
}

.info-value {
  color: #303133;
}

.info-description {
  margin-top: 5px;
  color: #606266;
  line-height: 1.6;
}

.exam-actions {
  margin-top: 20px;
  text-align: center;
}

.exam-notice {
  margin-top: 20px;
}

.result-preview {
  margin-top: 20px;
  text-align: center;
}

.score-summary {
  display: flex;
  justify-content: center;
  align-items: baseline;
  margin: 20px 0;
}

.score-value {
  font-size: 36px;
  font-weight: bold;
  color: #f56c6c;
}

.score-total {
  font-size: 16px;
  color: #909399;
  margin-left: 5px;
}

/* 考试内容样式 */
.exam-content {
  margin-bottom: 20px;
}

.exam-header {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.exam-title {
  flex: 1 1 100%;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
}

.exam-timer {
  flex: 1;
}

.exam-progress {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.exam-paper-card {
  margin-bottom: 20px;
}

.paper-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  margin: 0 0 10px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.section-desc {
  color: #606266;
  margin-bottom: 15px;
}

.question-item {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px dashed #ebeef5;
}

.question-item:last-child {
  border-bottom: none;
}

.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.question-index {
  font-weight: bold;
  margin-right: 5px;
}

.question-type {
  font-size: 14px;
  color: #606266;
  margin-right: 10px;
}

.question-score {
  font-size: 14px;
  color: #f56c6c;
}

.question-content {
  margin-bottom: 15px;
  line-height: 1.6;
}

.question-options,
.question-input,
.question-judge,
.question-textarea {
  margin-top: 15px;
}

.question-options .el-radio,
.question-options .el-checkbox {
  display: block;
  margin-bottom: 10px;
}

.submit-actions {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  gap: 20px;
}

/* 考试结果样式 */
.exam-result {
  margin-bottom: 20px;
}

.result-card {
  margin-bottom: 20px;
}

.total-score {
  display: flex;
  align-items: center;
}

.score-label {
  font-weight: bold;
  color: #606266;
  margin-right: 5px;
}

.score-value {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.score-details {
  margin-bottom: 20px;
}

.submit-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.score-distribution {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin: 20px 0;
}

.section-score {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.section-name {
  font-weight: bold;
}

.section-points {
  display: flex;
  align-items: baseline;
}

.points {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.total-points {
  font-size: 14px;
  color: #909399;
  margin-left: 5px;
}

.question-result {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #ebeef5;
}

.question-result:last-child {
  border-bottom: none;
}

.question-result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.question-index-title {
  display: flex;
  align-items: center;
}

.question-result-score {
  font-weight: bold;
}

.question-result-score.correct {
  color: #67c23a;
}

.question-result-score.wrong {
  color: #f56c6c;
}

.answer-details {
  margin-left: 20px;
}

.answer-item {
  margin-bottom: 10px;
}

.answer-label {
  font-weight: bold;
  color: #606266;
  margin-bottom: 5px;
}

.my-answer {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
}

.my-answer.correct {
  background-color: rgba(103, 194, 58, 0.1);
  border-left: 3px solid #67c23a;
}

.my-answer.wrong {
  background-color: rgba(245, 108, 108, 0.1);
  border-left: 3px solid #f56c6c;
}

.correct-answer {
  padding: 10px;
  background-color: rgba(103, 194, 58, 0.1);
  border-radius: 4px;
  line-height: 1.6;
  border-left: 3px solid #67c23a;
}

.answer-explanation {
  padding: 10px;
  background-color: #fdf6ec;
  border-radius: 4px;
  line-height: 1.6;
  color: #e6a23c;
}

.back-action {
  margin-top: 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .info-item {
    flex: 0 0 100%;
  }
  
  .exam-header {
    flex-direction: column;
  }
  
  .question-result-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .question-result-score {
    margin-top: 5px;
    margin-left: 20px;
  }
}
</style> 