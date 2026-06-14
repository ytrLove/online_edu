<template>
  <div class="assignment-detail-container">
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <div v-else class="content-container">
      <!-- 页面头部和状态 -->
      <div class="page-header">
        <div class="header-left">
          <h2 class="title">{{ assignment.title }}</h2>
          <div class="meta-row">
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>截止日期: {{ assignment.dueTime }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Reading /></el-icon>
              <span>课程: {{ assignment.courseName }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Money /></el-icon>
              <span>总分: {{ assignment.totalPoints }}</span>
            </div>
          </div>
        </div>
        <div class="header-right">
          <div class="status-tag" :class="getStatusClass()">
            {{ getStatusText() }}
          </div>
          <div v-if="assignment.submission && assignment.submission.totalScore !== null" class="score-display">
            <span class="score-label">得分</span>
            <span class="score-value">{{ assignment.submission.totalScore }}</span>
            <span class="score-total">/{{ assignment.totalPoints }}</span>
          </div>
        </div>
      </div>

      <!-- 作业内容 -->
      <el-card class="assignment-card">
        <template #header>
          <div class="card-header">
            <span>作业内容</span>
          </div>
        </template>
        <div class="assignment-content" v-html="assignment.description || '暂无详细描述'"></div>
      </el-card>

      <!-- 题目列表 -->
      <el-card class="questions-card">
        <template #header>
          <div class="card-header">
            <span>题目列表</span>
          </div>
        </template>
        
        <div class="questions-list">
          <div 
            v-for="(item, index) in assignment.questions" 
            :key="item.questionId"
            class="question-item"
          >
            <!-- 题目头部 -->
            <div class="question-header">
              <div class="question-number">{{ index + 1 }}</div>
              <div class="question-type-tag">{{ item.question.typeName }}</div>
              <div class="question-points">{{ item.points }} 分</div>
            </div>
            
            <!-- 题目内容 -->
            <div class="question-content">
              <div class="question-text">{{ item.question.questionContent }}</div>
              
              <!-- 选择题选项 -->
              <div v-if="item.question.typeName === '单选题'" class="question-options">
                <el-radio-group v-model="answers[item.questionId]">
                  <div v-for="option in parseOptions(item.question.options)" :key="option.key" class="option-item">
                    <el-radio :label="option.key">
                      {{ option.key }}: {{ option.value }}
                    </el-radio>
                  </div>
                </el-radio-group>
              </div>
              
              <div v-else-if="item.question.typeName === '多选题'" class="question-options">
                <el-checkbox-group v-model="answers[item.questionId]">
                  <div v-for="option in parseOptions(item.question.options)" :key="option.key" class="option-item">
                    <el-checkbox :label="option.key">
                      {{ option.key }}: {{ option.value }}
                    </el-checkbox>
                  </div>
                </el-checkbox-group>
              </div>
              
              <!-- 填空题 -->
              <div v-else-if="item.question.typeName === '填空题'" class="question-input">
                <el-input 
                  v-model="answers[item.questionId]" 
                  type="textarea" 
                  :rows="3" 
                  placeholder="请输入您的答案..."
                />
              </div>
              
              <!-- 简答题 -->
              <div v-else-if="item.question.typeName === '简答题'" class="question-input">
                <el-input 
                  v-model="answers[item.questionId]" 
                  type="textarea" 
                  :rows="5" 
                  placeholder="请输入您的答案..."
                />
              </div>
              
              <!-- 其他类型题目 -->
              <div v-else class="question-input">
                <el-input 
                  v-model="answers[item.questionId]" 
                  type="textarea" 
                  :rows="3" 
                  placeholder="请输入您的答案..."
                />
              </div>
            </div>
            
            <!-- 提交后的结果展示 -->
            <div v-if="assignment.submission && item.studentAnswer" class="answer-result">
              <div class="student-answer">
                <div class="answer-label">您的答案：</div>
                <div class="answer-content">{{ item.studentAnswer }}</div>
              </div>
              
              <div v-if="item.score !== null" class="answer-score">
                <div class="score-label">得分：</div>
                <div class="score-value">{{ item.score }}/{{ item.points }}</div>
              </div>
              
              <div v-if="item.feedback" class="answer-feedback">
                <div class="feedback-label">教师评语：</div>
                <div class="feedback-content">{{ item.feedback }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 提交区域 -->
      <el-card class="submission-card">
        <template #header>
          <div class="card-header">
            <span>提交状态</span>
            <div class="submission-status">
              <template v-if="!isExpired && !isSubmitted">
                <el-tag type="warning">尚未提交</el-tag>
              </template>
              <template v-else-if="isSubmitted && !isGraded">
                <el-tag type="success">已提交</el-tag>
                <span class="submission-time">提交于 {{ formatDate(assignment.submission.submissionTime) }}</span>
              </template>
              <template v-else-if="isGraded">
                <el-tag type="success">已批改</el-tag>
                <span class="submission-time">总分: {{ assignment.submission.totalScore }}/{{ assignment.totalPoints }}</span>
              </template>
              <template v-else-if="isExpired">
                <el-tag type="danger">已过期</el-tag>
              </template>
            </div>
          </div>
        </template>

        <!-- 未提交且未过期 - 可提交 -->
        <div v-if="!isSubmitted && !isExpired" class="submission-form">
          <el-button 
            type="primary" 
            @click="submitAssignment" 
            :loading="submitting"
          >
            提交作业
          </el-button>
          <div class="submission-tips">
            提交后将无法修改，请确认您的答案
          </div>
        </div>

        <!-- 已提交 - 显示提交内容 -->
        <div v-else-if="isSubmitted" class="submission-content">
          <div v-if="assignment.submission.feedback" class="teacher-feedback">
            <h3>教师评语</h3>
            <div class="feedback-content">
              {{ assignment.submission.feedback }}
            </div>
          </div>
        </div>

        <!-- 已过期未提交 -->
        <div v-else-if="isExpired && !isSubmitted" class="expired-message">
          <el-empty description="已过期，无法提交">
            <template #description>
              <p>该作业已过截止日期，无法提交</p>
              <p>截止日期：{{ formatDate(assignment.dueTime) }}</p>
            </template>
          </el-empty>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Calendar, Reading, Money, Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/date'
import { getAssignmentDetail, submitAssignment as submitAssignmentApi } from '@/api/assignment'
import { recordBehavior } from '@/api/behavior' 
const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const startTime = ref(null)
const endTime = ref(null)
// 作业数据
const assignment = ref({
  assignmentId: null,
  title: '',
  description: '',
  courseName: '',
  courseId: null,
  status: 1,
  startTime: '',
  dueTime: '',
  totalPoints: 0,
  teacherId: null,
  teacherName: '',
  questions: [],
  submission: null
})

// 学生答案
const answers = reactive({})

// 计算属性：是否已过期
const isExpired = computed(() => {
  if (!assignment.value.dueTime) return false
  const now = new Date()
  const dueTime = new Date(assignment.value.dueTime)
  return now > dueTime
})

// 计算属性：是否已提交
const isSubmitted = computed(() => {
  return assignment.value.submission !== null
})

// 计算属性：是否已批改
const isGraded = computed(() => {
  return isSubmitted.value && assignment.value.submission.status === 'graded'
})

// 获取状态类名
const getStatusClass = () => {
  if (isExpired.value && !isSubmitted.value) return 'status-expired'
  if (isGraded.value) return 'status-graded'
  if (isSubmitted.value) return 'status-submitted'
  return 'status-active'
}

// 获取状态文本
const getStatusText = () => {
  if (isExpired.value && !isSubmitted.value) return '已过期'
  if (isGraded.value) return '已批改'
  if (isSubmitted.value) return '已提交'
  return '未完成'
}

// 解析选项JSON
const parseOptions = (optionsStr) => {
  try {
    return JSON.parse(optionsStr)
  } catch (error) {
    console.error('解析选项出错', error)
    return []
  }
}

// 提交作业
const submitAssignment = async () => {
  try {
    // 检查是否所有题目都已回答
    const unansweredQuestions = assignment.value.questions.filter(q => !answers[q.questionId])
    
    if (unansweredQuestions.length > 0) {
      const confirm = await ElMessageBox.confirm(
        `您还有${unansweredQuestions.length}道题目未回答，确定要提交吗？`,
        '确认提交',
        {
          confirmButtonText: '是，提交',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).catch(() => false)
      
      if (!confirm) return
    }
    
    submitting.value = true
    
    // 构建提交数据
    const submitData = {
      assignmentId: assignment.value.assignmentId,
      answers: assignment.value.questions.map(question => {
        const answer = answers[question.questionId]
        let formattedAnswer = ''
        
        if (answer !== undefined && answer !== null) {
          if (question.question.typeName === '多选题') {
            formattedAnswer = Array.isArray(answer) ? answer.join(',') : answer
          } else if (question.question.typeName === '单选题') {
            formattedAnswer = String(answer)
          } else {
            formattedAnswer = String(answer || '')
          }
        }
        
        return {
          questionId: question.questionId,
          studentAnswer: formattedAnswer
        }
      })
    }
    
    console.log('提交的数据：', JSON.stringify(submitData, null, 2)) // 添加日志
    
    // 调用API提交
    const res = await submitAssignmentApi(submitData)
    
    if (res.code === 200) {
      ElMessage.success('作业提交成功')
      // 刷新页面获取最新数据
      fetchAssignmentDetail()
      endTime.value = Date.now()
      recordBehavior({
        behaviorType: 'submit_assignment',
        courseId: assignment.value.courseId,
        duration: Math.floor((endTime.value - startTime.value) / 1000), // 转换为秒
      })
    } else {
      ElMessage.error(res.message || '提交失败，请重试')
    }
  } catch (error) {
    console.error('提交作业出错', error)
    ElMessage.error('提交作业出错，请重试')
  } finally {
    submitting.value = false
  }
}

// 获取作业详情
const fetchAssignmentDetail = async () => {
  try {
    loading.value = true
    const assignmentId = route.params.id
    
    if (!assignmentId) {
      ElMessage.error('作业ID不能为空')
      router.push('/assignments')
      return
    }
    
    const res = await getAssignmentDetail(assignmentId)
    
    if (res.code === 200 && res.data) {
      assignment.value = res.data
      
      // 如果有提交记录，填充答案
      if (assignment.value.submission && assignment.value.submission.answers) {
        assignment.value.submission.answers.forEach(ans => {
          answers[ans.questionId] = ans.studentAnswer
        })
      }
    } else {
      ElMessage.error('获取作业详情失败')
      router.push('/assignments')
    }
  } catch (error) {
    console.error('获取作业详情出错', error)
    ElMessage.error('获取作业详情出错')
    router.push('/assignments')
  } finally {
    loading.value = false
  }
}

// 生命周期钩子
onMounted(() => {
  startTime.value = Date.now()
  fetchAssignmentDetail()
})
</script>

<style scoped>
.assignment-detail-container {
  padding: 20px;
}

.loading-container {
  padding: 40px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.title {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.meta-row {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
}

.status-tag {
  padding: 8px 15px;
  border-radius: 4px;
  font-weight: bold;
  margin-bottom: 10px;
}

.status-active {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-submitted {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-graded {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-expired {
  background-color: #fff2f0;
  color: #ff4d4f;
}

.score-display {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
}

.score-label {
  display: block;
  font-size: 12px;
  color: #606266;
}

.score-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.score-total {
  font-size: 14px;
  color: #606266;
}

.assignment-card,
.questions-card,
.submission-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
}

.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.question-number {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  margin-right: 10px;
}

.question-type-tag {
  background-color: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 10px;
}

.question-points {
  color: #ff9800;
  font-weight: bold;
}

.question-content {
  margin-bottom: 15px;
}

.question-text {
  margin-bottom: 15px;
  font-weight: bold;
}

.option-item {
  margin-bottom: 8px;
}

.question-input {
  margin-top: 10px;
}

.answer-result {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #ebeef5;
}

.student-answer,
.answer-score,
.answer-feedback {
  margin-bottom: 10px;
}

.answer-label,
.score-label,
.feedback-label {
  font-weight: bold;
  margin-bottom: 5px;
}

.submission-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.submission-tips {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}

.submission-status {
  display: flex;
  align-items: center;
  gap: 10px;
}

.submission-time {
  font-size: 12px;
  color: #909399;
}

.teacher-feedback {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-top: 20px;
}

.teacher-feedback h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
}
</style> 