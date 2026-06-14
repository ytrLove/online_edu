<template>
  <div class="submission-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>作业提交管理</span>
          <el-button v-permission="['exam:submission:export']" type="primary" @click="exportData">
            <el-icon><Download /></el-icon>导出数据
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="作业名称">
          <el-input v-model="queryParams.assignmentTitle" placeholder="请输入作业名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="queryParams.studentId" placeholder="请输入学号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="未提交" value="unsubmitted" />
            <el-option label="待批改" value="submitted" />
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
      <el-table v-loading="loading" :data="submissionList" border style="width: 100%" class="submission-table">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="assignmentTitle" label="作业名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="studentName" label="学生姓名" width="100" align="center" />
        <el-table-column prop="studentId" label="学号" width="120" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submissionTime" label="提交时间" width="160" align="center" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.status !== 'unsubmitted'">{{ formatDateTime(scope.row.submissionTime) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="分数" width="100" align="center">
          <template #default="scope">
            <span v-if="scope.row.status === 'graded'">{{ scope.row.totalScore }}</span>
            <el-tag v-else-if="scope.row.status === 'submitted'" type="warning">待批改</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="批改时间" width="160" align="center" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.status === 'graded'">{{ formatDateTime(scope.row.updateTime) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.status !== 'unsubmitted'" v-permission="['exam:submission:query']" type="primary" link @click="handleDetail(scope.row)">
              查看
            </el-button>
            <el-button v-if="scope.row.status === 'submitted'" v-permission="['exam:submission:grade']" type="warning" link @click="handleGrade(scope.row)">
              批改
            </el-button>
            <el-button v-if="scope.row.status === 'graded'" v-permission="['exam:submission:grade']" type="info" link @click="handleGrade(scope.row)">
              修改批改
            </el-button>
            <el-button v-permission="['exam:submission:remove']" type="danger" link @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页区域 -->
      <el-pagination
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>
    
    <!-- 查看详情对话框 -->
    <el-dialog 
      v-model="detailDialog.visible" 
      :title="detailDialog.title" 
      width="800px"
      destroy-on-close
    >
      <div class="submission-detail" v-loading="detailLoading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="作业名称" :span="2">{{ submissionDetail.assignmentTitle }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ submissionDetail.studentName }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ submissionDetail.studentId }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDateTime(submissionDetail.submissionTime) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTag(submissionDetail.status)">
              {{ getStatusLabel(submissionDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="submissionDetail.attachmentUrl" label="附件">
            <el-button type="primary" link @click="handleDownload(submissionDetail)">下载附件</el-button>
          </el-descriptions-item>
          <el-descriptions-item v-if="submissionDetail.status === 'graded'" label="得分">
            {{ submissionDetail.totalScore }}
          </el-descriptions-item>
          <el-descriptions-item v-if="submissionDetail.status === 'graded'" label="批改时间">
            {{ formatDateTime(submissionDetail.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div v-if="submissionDetail.content" class="submission-content-section">
          <h3>提交内容</h3>
          <div v-html="submissionDetail.content || ''" class="content-box"></div>
        </div>
        
        <div v-if="submissionDetail.status === 'graded' && submissionDetail.feedback" class="feedback-section">
          <h3>教师评语</h3>
          <div class="feedback-box">{{ submissionDetail.feedback }}</div>
        </div>
        
        <!-- 答案列表 -->
        <div v-if="submissionDetail.answers && submissionDetail.answers.length > 0" class="answers-section">
          <h3>作业题目 ({{ submissionDetail.answers.length }})</h3>
          <div v-for="(answer, index) in submissionDetail.answers" :key="answer.questionId" class="question-item">
            <div class="question-header">
              <span class="question-number">{{ index + 1 }}.</span>
              <span class="question-title" v-html="answer.question ? answer.question.questionContent : ''"></span>
              <span class="question-points">{{ answer.points }}分</span>
            </div>
            
            <!-- 选择题选项 -->
            <div v-if="answer.question && ['单选题', '多选题'].includes(answer.question.typeName)" class="question-options">
              <div class="question-type-tag">{{ answer.question.typeName }}</div>
              <div v-for="option in parseOptions(answer.question.options)" :key="option.key" 
                  :class="{
                    'option-item': true,
                    'selected': isOptionSelected(answer.question.typeName, answer.studentAnswer, option.key),
                    'correct': isCorrectAnswer(answer.question.correctAnswer, option.key)
                  }">
                <span class="option-key">{{ option.key }}.</span>
                <span class="option-value">{{ option.value }}</span>
                <span class="answer-indicator">
                  <el-tag v-if="isCorrectAnswer(answer.question.correctAnswer, option.key)" size="small" type="success">正确答案</el-tag>
                  <el-tag v-if="isOptionSelected(answer.question.typeName, answer.studentAnswer, option.key)" size="small" :type="isCorrectAnswer(answer.question.correctAnswer, option.key) ? 'success' : 'danger'">
                    {{ isCorrectAnswer(answer.question.correctAnswer, option.key) ? '答对了' : '答错了' }}
                  </el-tag>
                </span>
              </div>
            </div>
            
            <div class="student-answer-section">
              <div class="label">学生答案：</div>
              <div class="content">
                <template v-if="answer.question && answer.question.typeName === '多选题'">
                  {{ formatMultiAnswer(answer.studentAnswer) }}
                </template>
                <template v-else>
                  {{ answer.studentAnswer || '未作答' }}
                </template>
              </div>
              <div class="correct-answer">
                <div class="label">正确答案：</div>
                <div class="content">
                  <template v-if="answer.question && answer.question.typeName === '多选题'">
                    {{ formatMultiAnswer(answer.question.correctAnswer) }}
                  </template>
                  <template v-else>
                    {{ answer.question.correctAnswer }}
                  </template>
                </div>
              </div>
            </div>
            
            <div v-if="submissionDetail.status === 'graded'" class="score-feedback-section">
              <div class="score">得分：{{ answer.score || 0 }} / {{ answer.points }}</div>
              <div v-if="answer.feedback" class="feedback">
                <div class="label">教师评语：</div>
                <div class="content">{{ answer.feedback }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="dialog-footer">
          <el-button @click="detailDialog.visible = false">关闭</el-button>
          <el-button v-if="submissionDetail.status === 'submitted'" type="warning" @click="handleGrade(submissionDetail)">批改</el-button>
          <el-button v-if="submissionDetail.status === 'graded'" type="info" @click="handleGrade(submissionDetail)">修改批改</el-button>
        </div>
      </div>
    </el-dialog>
    
    <!-- 批改对话框 -->
    <el-dialog 
      v-model="gradeDialog.visible" 
      :title="gradeDialog.title" 
      width="800px"
      destroy-on-close
      @closed="handleGradeDialogClosed"
    >
      <div class="grade-container" v-loading="gradeLoading">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border size="small" class="grade-info">
          <el-descriptions-item label="作业名称" :span="2">{{ gradeForm.assignmentTitle }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ gradeForm.studentName }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ gradeForm.studentId }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDateTime(gradeForm.submissionTime) }}</el-descriptions-item>
          <el-descriptions-item v-if="gradeForm.attachmentUrl" label="附件">
            <el-button type="primary" link @click="handleDownload(gradeForm)">下载附件</el-button>
          </el-descriptions-item>
        </el-descriptions>
        
        <!-- 提交内容 -->
        <div v-if="gradeForm.content" class="submission-section">
          <h3>提交内容</h3>
          <div v-html="gradeForm.content" class="content-box"></div>
        </div>
        
        <el-divider content-position="center">批改区域</el-divider>
        
        <!-- 批改表单 -->
        <el-form :model="gradeForm" ref="gradeFormRef" label-width="100px">
          <!-- 答案评分区域 -->
          <div v-if="gradeForm.answers && gradeForm.answers.length > 0" class="answers-section">
            <h3>答案评分</h3>
            <div v-for="(answer, index) in gradeForm.answers" :key="index" class="answer-item">
              <div class="question-header">
                <span class="question-number">{{ index + 1 }}.</span>
                <span class="question-title" v-html="answer.question ? answer.question.questionContent : ''"></span>
                <span class="question-points">{{ answer.points }}分</span>
              </div>
              
              <!-- 选择题选项 -->
              <div v-if="answer.question && ['单选题', '多选题'].includes(answer.question.typeName)" class="question-options">
                <div class="question-type-tag">{{ answer.question.typeName }}</div>
                <div v-for="option in parseOptions(answer.question.options)" :key="option.key" 
                    :class="{
                      'option-item': true,
                      'selected': isOptionSelected(answer.question.typeName, answer.studentAnswer, option.key),
                      'correct': isCorrectAnswer(answer.question.correctAnswer, option.key)
                    }">
                  <span class="option-key">{{ option.key }}.</span>
                  <span class="option-value">{{ option.value }}</span>
                  <span class="answer-indicator">
                    <el-tag v-if="isCorrectAnswer(answer.question.correctAnswer, option.key)" size="small" type="success">正确答案</el-tag>
                    <el-tag v-if="isOptionSelected(answer.question.typeName, answer.studentAnswer, option.key)" size="small" :type="isCorrectAnswer(answer.question.correctAnswer, option.key) ? 'success' : 'danger'">
                      {{ isCorrectAnswer(answer.question.correctAnswer, option.key) ? '答对了' : '答错了' }}
                    </el-tag>
                  </span>
                </div>
              </div>
              
              <div class="student-answer-section">
                <div class="label">学生答案：</div>
                <div class="content">
                  <template v-if="answer.question && answer.question.typeName === '多选题'">
                    {{ formatMultiAnswer(answer.studentAnswer) }}
                  </template>
                  <template v-else>
                    {{ answer.studentAnswer || '未作答' }}
                  </template>
                </div>
                <div class="correct-answer">
                  <div class="label">正确答案：</div>
                  <div class="content">
                    <template v-if="answer.question && answer.question.typeName === '多选题'">
                      {{ formatMultiAnswer(answer.question.correctAnswer) }}
                    </template>
                    <template v-else>
                      {{ answer.question.correctAnswer }}
                    </template>
                  </div>
                </div>
              </div>

              <div class="answer-grade">
                <el-form-item :label="`得分：`" :prop="`answers[${index}].score`">
                  <el-input-number 
                    v-model="answer.score" 
                    :min="0" 
                    :max="answer.points" 
                    :step="0.5" 
                    :precision="1"
                    @change="handleScoreChange(answer)"
                  />
                  <span class="points-tip">满分：{{ answer.points }}分</span>
                </el-form-item>
                <el-form-item :label="`评语：`" :prop="`answers[${index}].feedback`">
                  <el-input v-model="answer.feedback" type="textarea" :rows="2" placeholder="请输入评语" />
                </el-form-item>
              </div>
            </div>
          </div>
          
          <!-- 总体评分和评语 -->
          <el-form-item label="总分" prop="totalScore">
            <el-input-number 
              v-model="gradeForm.totalScore" 
              :min="0" 
              :max="getTotalPoints()" 
              :step="1" 
              controls-position="right"
              disabled
            />
            <span class="total-points-tip">满分：{{ getTotalPoints() }}分</span>
          </el-form-item>
          
          <el-form-item label="教师评语" prop="feedback">
            <el-input 
              v-model="gradeForm.feedback" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入整体评语"
            />
          </el-form-item>
        </el-form>
        
        <div class="dialog-footer">
          <el-button @click="cancelGrade">取消</el-button>
          <el-button type="primary" @click="submitGrade" :loading="submitLoading">提交批改</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Download } from '@element-plus/icons-vue'
import { getSubmissionList, getSubmissionInfo, deleteSubmission, exportSubmissionData, gradeSubmission } from '@/api/exam/submission'

const router = useRouter()

// 加载状态
const loading = ref(false)
const detailLoading = ref(false)
const gradeLoading = ref(false)
const submitLoading = ref(false)

// 提交统计
const submissionStats = reactive({
  totalSubmissions: 0,
  pendingGradeCount: 0,
  gradedCount: 0,
  averageScore: 0
})

// 提交列表
const submissionList = ref([])
// 总记录数
const total = ref(0)
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  assignmentTitle: '',
  assignmentId: undefined,
  studentName: '',
  studentId: '',
  status: undefined
})

// 详情对话框
const detailDialog = reactive({
  visible: false,
  title: '提交详情'
})

// 提交详情
const submissionDetail = reactive({
  submissionId: '',
  assignmentId: '',
  assignmentTitle: '',
  studentId: '',
  studentName: '',
  submissionTime: '',
  status: '',
  attachmentUrl: '',
  content: '',
  totalScore: 0,
  updateTime: '',
  feedback: ''
})

// 批改对话框
const gradeDialog = reactive({
  visible: false,
  title: '批改作业'
})

// 批改表单引用
const gradeFormRef = ref(null)

// 批改表单数据
const gradeForm = reactive({
  submissionId: '',
  assignmentId: '',
  assignmentTitle: '',
  studentId: '',
  studentName: '',
  submissionTime: '',
  attachmentUrl: '',
  content: '',
  totalScore: 0,
  feedback: '',
  answers: []
})

/**
 * 获取提交统计信息
 */
const getStats = async () => {
  try {
    const response = await getSubmissionStats()
    Object.assign(submissionStats, response.data)
  } catch (error) {
    console.error('获取提交统计信息失败', error)
  }
}

/**
 * 获取提交列表
 */
const getList = async () => {
  loading.value = true
  try {
    const response = await getSubmissionList(queryParams)
    submissionList.value = response.data.rows
    total.value = response.data.total
  } catch (error) {
    console.error('获取提交列表失败', error)
    ElMessage.error('获取提交列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  // 将ISO格式的日期时间转为更易读的格式
  const date = new Date(dateTime)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

/**
 * 导出数据
 */
const exportData = async () => {
  try {
    const response = await exportSubmissionData(queryParams)
    const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = '作业提交数据.xlsx'
    link.click()
    URL.revokeObjectURL(link.href)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出数据失败', error)
    ElMessage.error('导出数据失败')
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
  queryParams.assignmentTitle = ''
  queryParams.assignmentId = undefined
  queryParams.studentName = ''
  queryParams.studentId = ''
  queryParams.status = undefined
  handleQuery()
}

/**
 * 处理下载操作
 */
const handleDownload = (row) => {
  if (row.attachmentUrl) {
    window.open(row.attachmentUrl)
  } else {
    ElMessage.warning('该提交没有附件')
  }
}

/**
 * 解析选项字符串为对象数组
 */
const parseOptions = (optionsStr) => {
  if (!optionsStr) return []
  try {
    const options = JSON.parse(optionsStr)
    return Array.isArray(options) ? options : []
  } catch (error) {
    console.error('解析选项出错:', error)
    return []
  }
}

/**
 * 处理提交数据
 */
const processSubmissionData = (data) => {
  if (!data) return {}
  
  return {
    ...data,
    content: data.content || '',
    attachmentUrl: data.attachmentUrl || '',
    feedback: data.feedback || '',
    updateTime: data.updateTime || null,
    totalScore: data.totalScore || 0,
    
    // 处理答案列表
    answers: (data.answers || []).map(answer => ({
      ...answer,
      studentAnswer: answer.studentAnswer || '-',
      score: answer.score || 0,
      feedback: answer.feedback || '',
      question: answer.question || {
        questionContent: '',
        correctAnswer: '',
        options: '[]',
        typeName: ''
      }
    }))
  }
}

/**
 * 处理查看详情操作
 */
const handleDetail = async (row) => {
  detailDialog.visible = true
  detailDialog.title = `提交详情 - ${row.studentName}`
  detailLoading.value = true
  
  try {
    const response = await getSubmissionInfo(row.submissionId)
    // 使用处理函数来确保数据完整性
    Object.assign(submissionDetail, processSubmissionData(response.data))
  } catch (error) {
    console.error('获取提交详情失败', error)
    ElMessage.error('获取提交详情失败')
  } finally {
    detailLoading.value = false
  }
}

/**
 * 处理批改操作
 */
const handleGrade = async (row) => {
  // 显示批改对话框
  gradeDialog.visible = true
  gradeDialog.title = `批改作业 - ${row.studentName}`
  gradeLoading.value = true
  
  try {
    // 获取详细信息
    const response = await getSubmissionInfo(row.submissionId)
    // 使用处理函数来确保数据完整性
    const submissionData = processSubmissionData(response.data)
    
    // 重置批改表单
    resetGradeForm()
    
    // 填充批改表单
    Object.assign(gradeForm, submissionData)
    
  } catch (error) {
    console.error('获取提交详情失败', error)
    ElMessage.error('获取提交详情失败')
    gradeDialog.visible = false
  } finally {
    gradeLoading.value = false
  }
}

/**
 * 重置批改表单
 */
const resetGradeForm = () => {
  gradeForm.submissionId = ''
  gradeForm.assignmentId = ''
  gradeForm.assignmentTitle = ''
  gradeForm.studentId = ''
  gradeForm.studentName = ''
  gradeForm.submissionTime = ''
  gradeForm.attachmentUrl = ''
  gradeForm.content = ''
  gradeForm.totalScore = 0
  gradeForm.feedback = ''
  gradeForm.answers = []
  
  if (gradeFormRef.value) {
    gradeFormRef.value.resetFields()
  }
}

/**
 * 取消批改
 */
const cancelGrade = () => {
  ElMessageBox.confirm('确定要取消批改吗？所有修改将不会保存', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '继续批改',
    type: 'warning'
  }).then(() => {
    gradeDialog.visible = false
  }).catch(() => {})
}

/**
 * 提交批改
 */
const submitGrade = async () => {
  submitLoading.value = true
  
  try {
    // 准备提交数据
    const submitData = {
      submissionId: gradeForm.submissionId,
      assignmentId: gradeForm.assignmentId,
      totalScore: gradeForm.totalScore,
      feedback: gradeForm.feedback,
      answers: gradeForm.answers ? gradeForm.answers.map(answer => ({
        questionId: answer.questionId,
        score: answer.score,
        feedback: answer.feedback
      })) : []
    }
    
    // 提交批改
    await gradeSubmission(submitData)
    ElMessage.success('批改提交成功')
    
    // 关闭对话框
    gradeDialog.visible = false
    
    // 刷新列表
    getList()
  } catch (error) {
    console.error('提交批改失败', error)
    ElMessage.error('提交批改失败：' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

/**
 * 处理批改对话框关闭
 */
const handleGradeDialogClosed = () => {
  resetGradeForm()
}

/**
 * 处理删除操作
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该提交记录吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteSubmission(row.submissionId)
        ElMessage.success('删除成功')
        getList()
        // getStats()
      } catch (error) {
        console.error('删除提交记录失败', error)
        ElMessage.error('删除提交记录失败')
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
 * 获取状态标签类型
 */
const getStatusTag = (status) => {
  if (status === 'unsubmitted') return 'info'
  if (status === 'submitted') return 'warning'
  if (status === 'graded') return 'success'
  return 'info'
}

/**
 * 获取状态标签文本
 */
const getStatusLabel = (status) => {
  if (status === 'unsubmitted') return '未提交'
  if (status === 'submitted') return '待批改'
  if (status === 'graded') return '已批改'
  return '未知'
}

// 判断选项是否被选中
const isOptionSelected = (questionType, studentAnswer, optionKey) => {
  if (!studentAnswer) return false
  if (questionType === '单选题') {
    return studentAnswer === optionKey
  }
  return studentAnswer.split(',').includes(optionKey)
}

// 判断是否是正确答案
const isCorrectAnswer = (correctAnswer, optionKey) => {
  if (!correctAnswer) return false
  return correctAnswer.includes(optionKey)
}

// 格式化多选题答案显示
const formatMultiAnswer = (answer) => {
  if (!answer) return '未作答'
  return answer.split(',').join(', ')
}

// 计算总分
const calculateTotalScore = () => {
  if (!gradeForm.answers) return 0
  return gradeForm.answers.reduce((total, answer) => total + (Number(answer.score) || 0), 0)
}

// 获取总分值
const getTotalPoints = () => {
  if (!gradeForm.answers) return 0
  return gradeForm.answers.reduce((total, answer) => total + (Number(answer.points) || 0), 0)
}

// 处理单题得分变化
const handleScoreChange = (answer) => {
  // 确保分数不超过题目分值
  if (answer.score > answer.points) {
    answer.score = answer.points
    ElMessage.warning(`该题最高分为${answer.points}分`)
  }
  // 自动计算总分
  gradeForm.totalScore = calculateTotalScore()
}

// 监听答案变化自动计算总分
watch(() => gradeForm.answers, () => {
  gradeForm.totalScore = calculateTotalScore()
}, { deep: true })

onMounted(async () => {
  await getList()
})
</script>

<style scoped>
.submission-list-container {
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

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 10px;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.submission-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.submission-detail {
  padding: 10px;
}

.submission-content-section, .feedback-section, .submission-section {
  margin-top: 20px;
}

h3 {
  font-size: 16px;
  margin-bottom: 10px;
  color: #303133;
  font-weight: 500;
}

.content-box, .feedback-box, .submission-content {
  padding: 10px;
  background: #f8f8f8;
  border-radius: 4px;
  min-height: 60px;
}

.dialog-footer {
  margin-top: 20px;
  text-align: right;
}

/* 批改对话框样式 */
.grade-container {
  padding: 10px;
}

.grade-info {
  margin-bottom: 20px;
}

.answers-section {
  margin: 20px 0;
}

.question-item, .answer-item {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 15px;
  background-color: #fafafa;
}

.question-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
}

.question-number {
  font-weight: bold;
  margin-right: 10px;
}

.question-content, .question-title {
  flex: 1;
}

.question-points {
  color: #f56c6c;
  margin-left: 10px;
}

.question-options {
  margin-bottom: 15px;
}

.question-type-tag {
  display: inline-block;
  padding: 2px 8px;
  background-color: #f0f9eb;
  color: #67c23a;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 10px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  margin-bottom: 8px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  transition: all 0.3s;
  position: relative;
}

.option-item.selected {
  background-color: #fef0f0;
  border-color: #fbc4c4;
}

.option-item.selected.correct {
  background-color: #f0f9eb;
  border-color: #67c23a;
}

.option-item.correct {
  border-color: #67c23a;
}

.answer-indicator {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.student-answer-section {
  margin-top: 12px;
  padding: 12px;
  background-color: #fafafa;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.correct-answer {
  margin-top: 8px;
  padding: 8px 12px;
  background-color: #f0f9eb;
  border-radius: 4px;
  border: 1px solid #e1f3d8;
}

.label {
  font-weight: bold;
  color: #606266;
  margin-bottom: 4px;
}

.content {
  color: #303133;
}

.points-tip,
.total-points-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}
</style> 