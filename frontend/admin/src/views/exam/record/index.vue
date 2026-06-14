<template>
  <div class="exam-record-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考试记录管理</span>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="考试试卷">
          <el-input v-model="queryParams.examTitle" placeholder="请输入试卷名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="未完成" value="pending" />
            <el-option label="进行中" value="started" />
            <el-option label="已提交" value="submitted" />
            <el-option label="批改中" value="grading" />
            <el-option label="已批改" value="graded" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="recordList" border stripe style="width: 100%">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="studentName" label="学生姓名" min-width="100" align="center" />
        <el-table-column prop="examTitle" label="试卷名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="courseName" label="所属课程" min-width="150" show-overflow-tooltip />
        <el-table-column label="开始时间" width="170" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="170" align="center">
          <template #default="scope">
            {{ scope.row.submitTime ? formatDateTime(scope.row.submitTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="用时" width="100" align="center">
          <template #default="scope">
            <span v-if="scope.row.status !== 'pending'">{{ calculateDuration(scope.row.startTime, scope.row.submitTime) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="成绩" width="100" align="center">
          <template #default="scope">
            <span v-if="scope.row.status === 'graded'" :class="getScoreClass(scope.row.totalScore, scope.row.totalPoints)">
              {{ scope.row.totalScore }} / {{ scope.row.totalPoints }}
            </span>
            <el-tag v-else-if="scope.row.status === 'submitted'" type="warning">待批改</el-tag>
            <el-tag v-else-if="scope.row.status === 'grading'" type="warning">批改中</el-tag>
            <el-tag v-else type="info">未提交</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <el-button 
              v-if="['submitted', 'grading'].includes(scope.row.status)" 
              v-permission="['exam:record:edit']" 
              type="success" 
              size="small" 
              @click="openGradeDialog(scope.row)"
            >
              批改
            </el-button>
            <el-button 
              v-permission="['exam:record:query']" 
              type="primary" 
              size="small" 
              @click="handleDetail(scope.row)"
            >
              详情
            </el-button>
            <el-button 
              size="small" 
              type="info" 
              @click="loadAnswers(scope.row.recordId)"
              :disabled="scope.row.answers !== null"
            >
              加载答案
            </el-button>
            <el-button 
              v-permission="['exam:record:remove']" 
              type="danger" 
              size="small" 
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          :current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 批改对话框 -->
    <el-dialog
      v-model="gradeDialogVisible"
      title="批改考试"
      width="80%"
      :close-on-click-modal="false"
      :before-close="closeGradeDialog"
    >
      <div v-loading="gradeLoading" class="grade-dialog-content">
        <template v-if="currentRecord">
          <div class="exam-info">
            <h3>{{ currentRecord.examTitle }}</h3>
            <div class="student-info">
              <span>学生：{{ currentRecord.studentName }}</span>
              <span>提交时间：{{ formatDateTime(currentRecord.submitTime) }}</span>
              <span>状态：
                <el-tag :type="getStatusType(currentRecord.status)">
                  {{ getStatusText(currentRecord.status) }}
                </el-tag>
              </span>
            </div>
          </div>

          <div class="action-bar">
            <el-button type="primary" @click="autoEvaluateAll">
              自动评分所有客观题
            </el-button>
            <div class="score-summary">
              当前总分：<span class="current-score">{{ calculateTotalScore() }}</span> / 
              <span class="total-points">{{ calculateTotalPoints() }}</span>
            </div>
          </div>

          <div v-if="currentAnswers && currentAnswers.length > 0" class="answer-list">
            <el-card v-for="(question, index) in currentAnswers" :key="index" class="answer-item">
              <div class="question-info">
                <div class="question-header">
                  <span class="question-number">第{{ index + 1 }}题</span>
                  <el-tag size="small" class="question-type-tag">
                    {{ getQuestionTypeText(question.questionType) }}
                  </el-tag>
                  <span class="question-points">分值：{{ question.points }}分</span>
                </div>
                <div class="question-title" v-html="question.content"></div>
              </div>

              <div v-if="question.parsedOptions && question.parsedOptions.length > 0" class="question-options">
                <div v-for="option in question.parsedOptions" :key="option.key" class="option-item">
                  <span class="option-key">{{ option.key }}.</span>
                  <span class="option-value">{{ option.value }}</span>
                </div>
              </div>

              <div class="answer-content">
                <div class="answer-section">
                  <div class="student-answer">
                    <div class="answer-label">学生答案：</div>
                    <div :class="['answer-text', isAnswerCorrect(question) ? 'correct-answer' : 'incorrect-answer']">
                      {{ formatStudentAnswer(question.studentAnswer, question.questionType, question.parsedOptions) }}
                    </div>
                  </div>

                  <div class="correct-answer">
                    <div class="answer-label">正确答案：</div>
                    <div class="answer-text">
                      {{ formatStudentAnswer(question.answer, question.questionType, question.parsedOptions) }}
                    </div>
                  </div>
                </div>

                <div class="explanation" v-if="question.analysis">
                  <div class="answer-label">解析：</div>
                  <div class="explanation-text" v-html="question.analysis"></div>
                </div>
              </div>

              <div class="score-section">
                <el-row :gutter="20">
                  <el-col :span="8">
                    <el-form-item :label="`评分 (0-${question.points}分)`">
                      <el-input-number 
                        v-model="question.score" 
                        :min="0" 
                        :max="parseFloat(question.points)" 
                        :precision="1" 
                        :step="0.5"
                        controls-position="right"
                        style="width: 130px;"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="评语">
                      <el-input 
                        v-model="question.feedback" 
                        type="textarea" 
                        placeholder="请输入评语" 
                        :rows="2"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="4" v-if="isObjectiveQuestion(question.questionTypeId)">
                    <div class="auto-grade-button">
                      <el-button type="primary" size="small" @click="autoEvaluate(index)">
                        自动评分
                      </el-button>
                    </div>
                  </el-col>
                </el-row>

                <div v-if="isAnswerCorrect(question)" class="auto-result correct">
                  <el-icon><Check /></el-icon> 答案正确
                </div>
                <div v-else-if="question.studentAnswer" class="auto-result incorrect">
                  <el-icon><Close /></el-icon> 答案错误
                </div>
              </div>
            </el-card>
          </div>
          <div v-else class="no-answers">
            未找到答案数据，请先加载答案
          </div>

          <div class="total-score-section">
            <div class="total-label">总分：</div>
            <div class="total-value">{{ calculateTotalScore() }} / {{ calculateTotalPoints() }}</div>
          </div>
        </template>
        <div v-else class="loading-tip">
          正在加载考试数据...
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeGradeDialog">取 消</el-button>
          <el-button type="primary" @click="submitGrade" :loading="submitLoading">
            提交批改
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="考试记录详情"
      width="80%"
      :close-on-click-modal="false"
      :before-close="closeDetailDialog"
    >
      <div v-loading="detailLoading" class="detail-dialog-content">
        <template v-if="currentRecord">
          <div class="exam-info">
            <h3>{{ currentRecord.examTitle }}</h3>
            <div class="student-info">
              <span>学生：{{ currentRecord.studentName }}</span>
              <span>提交时间：{{ formatDateTime(currentRecord.submitTime) }}</span>
              <span>状态：
                <el-tag :type="getStatusType(currentRecord.status)">
                  {{ getStatusText(currentRecord.status) }}
                </el-tag>
              </span>
            </div>
          </div>

          <div class="detail-section">
            <el-descriptions border :column="2" size="large">
              <el-descriptions-item label="考试名称">{{ currentRecord.examTitle }}</el-descriptions-item>
              <el-descriptions-item label="所属课程">{{ currentRecord.courseName }}</el-descriptions-item>
              <el-descriptions-item label="开始时间">{{ formatDateTime(currentRecord.startTime) }}</el-descriptions-item>
              <el-descriptions-item label="提交时间">{{ formatDateTime(currentRecord.submitTime) }}</el-descriptions-item>
              <el-descriptions-item label="用时">{{ calculateDuration(currentRecord.startTime, currentRecord.submitTime) }}</el-descriptions-item>
              <el-descriptions-item label="成绩" :class="getScoreClass(currentRecord.totalScore, currentRecord.totalPoints)">
                {{ currentRecord.totalScore }} / {{ currentRecord.totalPoints }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div v-if="currentAnswers && currentAnswers.length > 0" class="answer-list">
            <h3>答题情况</h3>
            <el-card v-for="(question, index) in currentAnswers" :key="index" class="answer-item">
              <div class="question-info">
                <div class="question-header">
                  <span class="question-number">第{{ index + 1 }}题</span>
                  <el-tag size="small" class="question-type-tag">
                    {{ getQuestionTypeText(question.questionType) }}
                  </el-tag>
                  <span class="question-points">分值：{{ question.points }}分</span>
                </div>
                <div class="question-title" v-html="question.content"></div>
              </div>

              <div v-if="question.parsedOptions && question.parsedOptions.length > 0" class="question-options">
                <div v-for="option in question.parsedOptions" :key="option.key" class="option-item">
                  <span class="option-key">{{ option.key }}.</span>
                  <span class="option-value">{{ option.value }}</span>
                </div>
              </div>

              <div class="answer-content">
                <div class="answer-section">
                  <div class="student-answer">
                    <div class="answer-label">学生答案：</div>
                    <div :class="['answer-text', isAnswerCorrect(question) ? 'correct-answer' : 'incorrect-answer']">
                      {{ formatStudentAnswer(question.studentAnswer, question.questionType, question.parsedOptions) }}
                    </div>
                  </div>

                  <div class="correct-answer">
                    <div class="answer-label">正确答案：</div>
                    <div class="answer-text">
                      {{ formatStudentAnswer(question.answer, question.questionType, question.parsedOptions) }}
                    </div>
                  </div>
                </div>

                <div class="explanation" v-if="question.analysis">
                  <div class="answer-label">解析：</div>
                  <div class="explanation-text" v-html="question.analysis"></div>
                </div>
              </div>

              <div class="score-display" v-if="question.score !== undefined">
                <div class="score-label">得分：</div>
                <div class="score-value" :class="question.score == question.points ? 'full-score' : 'partial-score'">
                  {{ question.score }} / {{ question.points }}
                </div>
                <div class="feedback-display" v-if="question.feedback">
                  <div class="feedback-label">教师评语：</div>
                  <div class="feedback-text">{{ question.feedback }}</div>
                </div>
              </div>
            </el-card>
          </div>
          <div v-else class="no-answers">
            未找到答案数据，请先加载答案
          </div>
        </template>
        <div v-else class="loading-tip">
          正在加载考试详情...
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Search, Refresh, Check, Close } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamRecordList, getExamRecordDetail, deleteExamRecord, getExamAnswers, gradeExam } from '@/api/exam/record'
import { useRouter } from 'vue-router'

const router = useRouter()

// 加载状态
const loading = ref(false)
// 批改对话框状态
const gradeDialogVisible = ref(false)
const gradeLoading = ref(false)
const submitLoading = ref(false)
// 详情对话框状态
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
// 记录列表
const recordList = ref([])
// 总记录数
const total = ref(0)
// 当前批改的记录
const currentRecord = ref(null)
const currentAnswers = ref([])
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  examTitle: '',
  studentName: '',
  status: undefined
})

/**
 * 查询考试记录列表
 */
const getList = async () => {
  loading.value = true
  try {
    const response = await getExamRecordList(queryParams)
    recordList.value = response.data.rows
    total.value = response.data.total
  } catch (error) {
    console.error('获取考试记录列表失败', error)
    ElMessage.error('获取考试记录列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载学生答案
 */
const loadAnswers = async (recordId) => {
  loading.value = true
  try {
    const res = await getExamAnswers(recordId)
    if (res.code === 200) {
      // 查找对应记录并更新答案
      const record = recordList.value.find(r => r.recordId === recordId)
      if (record) {
        record.answers = res.data.answers || []
        ElMessage.success('加载答案成功')
      }
    }
  } catch (error) {
    console.error('加载答案失败', error)
    ElMessage.error('加载答案失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

/**
 * 打开批改对话框
 */
const openGradeDialog = async (row) => {
  currentRecord.value = { ...row }
  gradeDialogVisible.value = true
  gradeLoading.value = true
  
  try {
    // 如果已经加载了答案，使用已加载的答案
    if (row.answers && row.answers.length > 0) {
      currentAnswers.value = preprocessAnswers(JSON.parse(JSON.stringify(row.answers)))
    } else {
      // 否则重新加载考试详情
      const res = await getExamRecordDetail(row.recordId)
      if (res.code === 200) {
        currentRecord.value = res.data
        currentAnswers.value = preprocessAnswers(res.data.answers || [])
      } else {
        ElMessage.error('获取考试详情失败')
      }
    }
  } catch (error) {
    console.error('获取考试详情失败', error)
    ElMessage.error('获取考试详情失败：' + (error.message || '未知错误'))
  } finally {
    gradeLoading.value = false
  }
}

/**
 * 预处理答案数据
 */
const preprocessAnswers = (answers) => {
  return answers.map(answer => {
    // 解析选项JSON字符串
    if (answer.options && typeof answer.options === 'string') {
      try {
        answer.parsedOptions = JSON.parse(answer.options)
      } catch (e) {
        answer.parsedOptions = []
        console.error('解析选项失败:', e)
      }
    } else {
      answer.parsedOptions = []
    }
    
    // 处理题目类型
    if (!answer.typeName && answer.questionTypeId) {
      answer.questionType = mapQuestionTypeId(answer.questionTypeId)
    } else {
      answer.questionType = answer.typeName || 'unknown'
    }
    
    // 标准化多选题答案格式
    if (answer.questionTypeId === 2) { // 多选题
      if (answer.studentAnswer) {
        // 将B,C格式转换为BC格式
        answer.normalizedStudentAnswer = answer.studentAnswer.split(',').join('')
      } else {
        answer.normalizedStudentAnswer = ''
      }
    }
    
    return answer
  }).sort((a, b) => (a.questionOrder || 0) - (b.questionOrder || 0)) // 按题目顺序排序
}

/**
 * 题目类型ID映射
 */
const mapQuestionTypeId = (typeId) => {
  const typeMap = {
    1: 'single_choice',   // 单选题
    2: 'multiple_choice', // 多选题
    3: 'true_false',      // 判断题
    4: 'fill_blank',      // 填空题
    5: 'short_answer',    // 简答题
    6: 'essay'            // 论述题
  }
  return typeMap[typeId] || 'unknown'
}

/**
 * 关闭批改对话框
 */
const closeGradeDialog = () => {
  gradeDialogVisible.value = false
  currentRecord.value = null
  currentAnswers.value = []
}

/**
 * 自动评分
 */
const autoEvaluate = (index) => {
  const question = currentAnswers.value[index]
  if (!question || !isObjectiveQuestion(question.questionTypeId)) return
  
  const correctAnswer = question.answer || ''
  
  // 根据题目类型选择比较方法
  let isCorrect = false
  if (question.questionTypeId === 1 || question.questionTypeId === 3) { // 单选题或判断题
    isCorrect = question.studentAnswer && question.studentAnswer.trim().toUpperCase() === correctAnswer.trim().toUpperCase()
  } else if (question.questionTypeId === 2) { // 多选题
    isCorrect = question.normalizedStudentAnswer && question.normalizedStudentAnswer.toUpperCase() === correctAnswer.toUpperCase()
  }
  
  question.score = isCorrect ? parseFloat(question.points || 0) : 0
  question.feedback = isCorrect ? '回答正确' : '回答错误'
  question.isCorrect = isCorrect
}

/**
 * 判断是否为客观题
 */
const isObjectiveQuestion = (typeIdOrName) => {
  if (typeof typeIdOrName === 'number') {
    return [1, 2, 3].includes(typeIdOrName) // 题目类型ID为1,2,3的是客观题
  }
  return ['single_choice', 'multiple_choice', 'true_false'].includes(typeIdOrName)
}

/**
 * 判断答案是否正确
 */
const isAnswerCorrect = (question) => {
  if (!question || !question.studentAnswer || !question.answer) return false
  
  if (question.questionTypeId === 2) { // 多选题
    // 使用格式化后的答案比较
    return question.normalizedStudentAnswer.toUpperCase() === question.answer.toUpperCase()
  }
  
  return question.studentAnswer.trim().toUpperCase() === question.answer.trim().toUpperCase()
}

/**
 * 计算总分
 */
const calculateTotalScore = () => {
  if (!currentAnswers.value || currentAnswers.value.length === 0) return 0
  return currentAnswers.value.reduce((sum, answer) => {
    return sum + (answer.score || 0)
  }, 0)
}

/**
 * 计算总分值
 */
const calculateTotalPoints = () => {
  if (!currentAnswers.value || currentAnswers.value.length === 0) return 0
  return currentAnswers.value.reduce((sum, answer) => {
    return sum + (answer.points || 0)
  }, 0)
}

/**
 * 提交批改
 */
const submitGrade = async () => {
  if (!currentRecord.value || !currentAnswers.value || currentAnswers.value.length === 0) {
    ElMessage.warning('没有可提交的批改数据')
    return
  }
  
  // 验证是否所有题目都已评分
  const ungraded = currentAnswers.value.find(answer => answer.score === undefined || answer.score === null)
  if (ungraded) {
    ElMessage.warning('请为所有题目评分后再提交')
    return
  }
  
  submitLoading.value = true
  try {
    // 检查数据格式
    console.log('提交批改数据:', currentRecord.value)
    
    const data = {
      recordId: currentRecord.value.recordId,
      examId: currentRecord.value.examId, // 添加作业ID字段
      answers: currentAnswers.value.map(answer => ({
        questionId: answer.questionId,
        score: answer.score,
        feedback: answer.feedback || answer.teacherComment || ''
      }))
    }
    
    console.log('提交批改参数:', data)
    const res = await gradeExam(data)
    if (res.code === 200) {
      ElMessage.success('批改提交成功')
      closeGradeDialog()
      getList() // 刷新列表
    } else {
      ElMessage.error(res.msg || '批改提交失败')
    }
  } catch (error) {
    console.error('批改提交失败', error)
    ElMessage.error('批改提交失败：' + (error.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

/**
 * 搜索按钮操作
 */
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

/**
 * 重置搜索表单
 */
const resetQuery = () => {
  queryParams.examTitle = ''
  queryParams.studentName = ''
  queryParams.status = undefined
  handleQuery()
}

/**
 * 处理详情操作
 */
const handleDetail = async (row) => {
  currentRecord.value = { ...row }
  detailDialogVisible.value = true
  detailLoading.value = true
  
  try {
    // 如果已经加载了答案，使用已加载的答案
    if (row.answers && row.answers.length > 0) {
      currentAnswers.value = preprocessAnswers(JSON.parse(JSON.stringify(row.answers)))
    } else {
      // 否则重新加载考试详情
      const res = await getExamRecordDetail(row.recordId)
      if (res.code === 200) {
        Object.assign(currentRecord.value, res.data)
        currentAnswers.value = preprocessAnswers(res.data.answers || [])
      } else {
        ElMessage.error('获取考试详情失败')
      }
    }
  } catch (error) {
    console.error('获取考试详情失败', error)
    ElMessage.error('获取考试详情失败：' + (error.message || '未知错误'))
  } finally {
    detailLoading.value = false
  }
}

/**
 * 关闭详情对话框
 */
const closeDetailDialog = () => {
  detailDialogVisible.value = false
  // 不清空当前记录，以便下次打开时能快速显示
}

/**
 * 处理删除操作
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该考试记录吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteExamRecord(row.recordId)
        ElMessage.success('删除成功')
        getList()
      } catch (error) {
        console.error('删除考试记录失败', error)
        ElMessage.error('删除考试记录失败')
      }
    })
    .catch(() => {})
}

/**
 * 处理分页大小改变
 */
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  getList()
}

/**
 * 处理页码改变
 */
const handleCurrentChange = (num) => {
  queryParams.pageNum = num
  getList()
}

/**
 * 获取状态类型
 */
const getStatusType = (status) => {
  const typeMap = {
    'pending': 'info',
    'started': 'warning',
    'submitted': 'primary',
    'grading': 'warning',
    'graded': 'success'
  }
  return typeMap[status] || 'info'
}

/**
 * 获取状态文本
 */
const getStatusText = (status) => {
  const statusMap = {
    'pending': '未开始',
    'started': '进行中',
    'submitted': '已提交',
    'grading': '批改中',
    'graded': '已批改'
  }
  return statusMap[status] || status
}

/**
 * 获取题目类型文本
 */
const getQuestionTypeText = (type) => {
  const typeMap = {
    'single_choice': '单选题',
    'multiple_choice': '多选题',
    'true_false': '判断题',
    'fill_blank': '填空题',
    'short_answer': '简答题',
    'essay': '论述题'
  }
  return typeMap[type] || type
}

/**
 * 格式化答案显示
 */
const formatAnswer = (answer) => {
  if (!answer) return '未作答'
  if (typeof answer === 'string' && answer.startsWith('<') && answer.endsWith('>')) {
    return answer // 如果已经是HTML内容，直接返回
  }
  return answer.replace(/\n/g, '<br>') // 将换行符转换为HTML换行
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '--'
  const date = new Date(dateTimeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

/**
 * 计算用时
 */
const calculateDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return '--'
  
  const start = new Date(startTime).getTime()
  const end = new Date(endTime).getTime()
  const diffSeconds = Math.floor((end - start) / 1000)
  
  const hours = Math.floor(diffSeconds / 3600)
  const minutes = Math.floor((diffSeconds % 3600) / 60)
  const seconds = diffSeconds % 60
  
  let result = ''
  if (hours > 0) {
    result += `${hours}时`
  }
  if (minutes > 0 || hours > 0) {
    result += `${minutes}分`
  }
  result += `${seconds}秒`
  
  return result
}

/**
 * 获取分数样式
 */
const getScoreClass = (score, totalPoints) => {
  if (score === null || score === undefined) return 'score-pending'
  const percentage = score / totalPoints
  if (percentage >= 0.9) return 'score-excellent'
  if (percentage >= 0.7) return 'score-good'
  if (percentage >= 0.6) return 'score-pass'
  return 'score-fail'
}

/**
 * 格式化选项显示
 */
const formatOptions = (options) => {
  if (!options || !options.length) return '无选项'
  
  return options.map(opt => `${opt.key}. ${opt.value}`).join('<br>')
}

/**
 * 格式化学生答案显示
 */
const formatStudentAnswer = (answer, questionType, parsedOptions) => {
  if (!answer) return '未作答'
  
  // 处理单选题和判断题
  if (questionType === 'single_choice' || questionType === 'true_false' || questionType === 1 || questionType === 3) {
    // 尝试查找对应选项内容
    if (parsedOptions && parsedOptions.length) {
      const option = parsedOptions.find(opt => opt.key === answer)
      if (option) {
        return `${answer}. ${option.value}`
      }
    }
    return answer
  }
  
  // 处理多选题
  if (questionType === 'multiple_choice' || questionType === 2) {
    const choices = answer.includes(',') ? answer.split(',') : answer.split('')
    
    // 尝试查找对应选项内容
    if (parsedOptions && parsedOptions.length) {
      return choices.map(choice => {
        const option = parsedOptions.find(opt => opt.key === choice)
        return option ? `${choice}. ${option.value}` : choice
      }).join('<br>')
    }
    
    return choices.join(', ')
  }
  
  // 其他类型直接返回
  return answer
}

/**
 * 批量自动评分
 */
const autoEvaluateAll = () => {
  if (!currentAnswers.value || currentAnswers.value.length === 0) return
  
  // 只对客观题进行自动评分
  currentAnswers.value.forEach((answer, index) => {
    if (isObjectiveQuestion(answer.questionTypeId)) {
      autoEvaluate(index)
    }
  })
  
  ElMessage.success('所有客观题已自动评分完成')
}

/**
 * 处理批改操作
 */
const handleGrade = (row) => {
  router.push(`/exam/record/grade/${row.recordId}`)
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.exam-record-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.score-excellent {
  color: #67c23a;
  font-weight: bold;
}

.score-good {
  color: #409eff;
  font-weight: bold;
}

.score-pass {
  color: #e6a23c;
  font-weight: bold;
}

.score-fail {
  color: #f56c6c;
  font-weight: bold;
}

.score-pending {
  color: #909399;
}

/* 批改对话框样式 */
.grade-dialog-content {
  max-height: 70vh;
  overflow-y: auto;
  padding: 10px;
}

.exam-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.exam-info h3 {
  margin-bottom: 10px;
  text-align: center;
  color: #303133;
}

.student-info {
  display: flex;
  justify-content: center;
  gap: 20px;
  color: #606266;
}

.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background-color: #f0f9eb;
  border-radius: 6px;
}

.score-summary {
  font-size: 16px;
  font-weight: bold;
}

.current-score {
  color: #67c23a;
  font-size: 18px;
}

.total-points {
  color: #409eff;
}

.answer-list {
  margin-bottom: 20px;
}

.answer-item {
  margin-bottom: 20px;
  border-radius: 6px;
  transition: box-shadow 0.3s;
}

.answer-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.question-info {
  margin-bottom: 15px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.question-number {
  font-weight: bold;
  margin-right: 10px;
  min-width: 50px;
  color: #409eff;
}

.question-type-tag {
  margin-right: 10px;
}

.question-points {
  color: #f56c6c;
  font-weight: bold;
  margin-left: auto;
}

.question-title {
  margin-bottom: 15px;
  color: #303133;
  font-weight: 500;
  line-height: 1.5;
}

.question-options {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.option-item {
  display: flex;
  margin-bottom: 8px;
  padding: 5px 0;
}

.option-key {
  font-weight: bold;
  margin-right: 10px;
  color: #409eff;
}

.option-value {
  color: #606266;
}

.answer-content {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.answer-section {
  display: flex;
  margin-bottom: 15px;
  flex-wrap: wrap;
  gap: 20px;
}

.student-answer, .correct-answer {
  flex: 1;
  min-width: 300px;
  padding: 10px;
  border-radius: 4px;
}

.student-answer {
  background-color: #f0f9ff;
}

.correct-answer {
  background-color: #f0f9eb;
}

.answer-label {
  font-weight: bold;
  margin-bottom: 10px;
  color: #303133;
}

.answer-text {
  color: #606266;
  padding: 5px;
  border-radius: 2px;
}

.correct-answer .answer-text {
  color: #67c23a;
  font-weight: 500;
}

.incorrect-answer {
  color: #f56c6c;
}

.explanation {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border-left: 3px solid #909399;
}

.explanation-text {
  color: #606266;
  line-height: 1.6;
}

.score-section {
  margin-top: 15px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.auto-grade-button {
  text-align: right;
  margin-top: 20px;
}

.auto-result {
  margin-top: 10px;
  text-align: right;
  padding: 5px 10px;
  border-radius: 4px;
  display: inline-block;
  float: right;
}

.correct {
  color: #67c23a;
  background-color: rgba(103, 194, 58, 0.1);
}

.incorrect {
  color: #f56c6c;
  background-color: rgba(245, 108, 108, 0.1);
}

.no-answers {
  text-align: center;
  padding: 50px 0;
  color: #909399;
  font-size: 16px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.total-score-section {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  font-size: 18px;
  padding: 15px 20px;
  background-color: #f0f9eb;
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.total-label {
  font-weight: bold;
  margin-right: 10px;
  color: #303133;
}

.total-value {
  font-size: 22px;
  color: #67c23a;
  font-weight: bold;
}

/* 批改对话框里的表单项 */
:deep(.el-form-item__label) {
  font-weight: bold;
  color: #303133;
}

:deep(.el-input-number.is-controls-right .el-input-number__decrease),
:deep(.el-input-number.is-controls-right .el-input-number__increase) {
  background-color: #f5f7fa;
}

:deep(.el-textarea__inner) {
  border: 1px solid #dcdfe6;
  transition: border-color 0.3s;
}

:deep(.el-textarea__inner:focus) {
  border-color: #409eff;
}

/* 提交按钮区域 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 详情对话框样式 */
.detail-dialog-content {
  max-height: 70vh;
  overflow-y: auto;
  padding: 10px;
}

.detail-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.score-display {
  margin-top: 15px;
  padding: 10px;
  background-color: #f0f9eb;
  border-radius: 4px;
}

.score-label {
  font-weight: bold;
  margin-right: 10px;
  color: #303133;
}

.score-value {
  font-size: 16px;
  font-weight: bold;
}

.full-score {
  color: #67c23a;
}

.partial-score {
  color: #e6a23c;
}

.feedback-display {
  margin-top: 10px;
  border-top: 1px dashed #dcdfe6;
  padding-top: 10px;
}

.feedback-label {
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.feedback-text {
  color: #606266;
  line-height: 1.5;
}
</style>