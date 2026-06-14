<template>
  <div class="assignment-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>作业管理</span>
          <el-button v-permission="['exam:assignment:add']" type="primary" @click="handleAdd">发布作业</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="作业标题">
          <el-input v-model="queryParams.title" placeholder="请输入作业标题" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="进行中" value="1" />
            <el-option label="已截止" value="2" />
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
      <el-table v-loading="loading" :data="assignmentList" border style="width: 100%">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="title" label="作业标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="courseName" label="所属课程" width="150" align="center" show-overflow-tooltip />
        <el-table-column prop="totalPoints" label="总分" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="发布时间" width="160" align="center" show-overflow-tooltip />
        <el-table-column prop="dueTime" label="截止时间" width="160" align="center" show-overflow-tooltip />
        <el-table-column label="操作" width="300" fixed="right" align="center">
          <template #default="scope">
            <el-button v-permission="['exam:assignment:query']" type="primary" size="small" text @click="handleView(scope.row)">
              <el-icon><View /></el-icon>详情
            </el-button>
            <el-button v-permission="['exam:assignment:edit']" type="warning" size="small" text @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button v-permission="['exam:assignment:remove']" type="danger" size="small" text @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>删除
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
      
      <!-- 作业表单对话框 -->
      <el-dialog 
        v-model="dialog.visible" 
        :title="dialog.title" 
        width="700px" 
        destroy-on-close
        @closed="resetForm"
      >
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="所属课程" prop="courseId">
            <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%">
              <el-option v-for="item in courseOptions" :key="item.courseId" :label="item.courseName" :value="item.courseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="作业标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入作业标题" />
          </el-form-item>
          <el-form-item label="作业内容" prop="content">
            <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入作业内容" />
          </el-form-item>
          <el-form-item label="总分" prop="totalScore">
            <el-input-number v-model="form.totalPoints" :min="0" :max="100" :step="5" style="width: 150px" />
          </el-form-item>
          <el-form-item label="发布时间" prop="startTime">
            <el-date-picker
              v-model="form.startTime"
              type="datetime"
              placeholder="选择发布时间"
              style="width: 100%"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DDTHH:mm:ss"
            />
          </el-form-item>
          <el-form-item label="截止时间" prop="dueTime">
            <el-date-picker
              v-model="form.dueTime"
              type="datetime"
              placeholder="选择截止时间"
              style="width: 100%"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DDTHH:mm:ss"
            />
          </el-form-item>
          <el-form-item label="上传要求" prop="fileRequirements">
            <el-input v-model="form.fileRequirements" type="textarea" :rows="3" placeholder="请输入上传文件要求（可选）" />
          </el-form-item>
          <el-form-item label="是否公开评分">
            <el-switch v-model="form.publicScore" />
            <div class="switch-help">开启后，学生可以查看自己的评分和教师评语</div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </template>
      </el-dialog>

      <!-- 作业详情对话框 -->
      <el-dialog
        v-model="detailDialog.visible"
        title="作业详情"
        width="800px"
        :close-on-click-modal="false"
      >
        <div v-loading="detailDialog.loading" class="detail-content">
          <template v-if="detailInfo.assignmentId">
            <el-descriptions border :column="2" size="medium">
              <el-descriptions-item label="作业标题" :span="2">{{ detailInfo.title }}</el-descriptions-item>
              <el-descriptions-item label="所属课程">{{ detailInfo.courseName }}</el-descriptions-item>
              <el-descriptions-item label="发布教师">{{ detailInfo.teacherName }}</el-descriptions-item>
              <el-descriptions-item label="总分">{{ detailInfo.totalPoints }} 分</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="getStatusTag(detailInfo.status)">{{ getStatusLabel(detailInfo.status) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="发布时间">{{ detailInfo.startTime }}</el-descriptions-item>
              <el-descriptions-item label="截止时间">{{ detailInfo.dueTime }}</el-descriptions-item>
              <el-descriptions-item label="上传要求" :span="2">
                {{ detailInfo.fileRequirements || '无特殊要求' }}
              </el-descriptions-item>
              <el-descriptions-item label="是否公开评分">
                {{ detailInfo.publicScore ? '是' : '否' }}
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ detailInfo.createTime }}</el-descriptions-item>
            </el-descriptions>

            <div class="content-section">
              <h3>作业内容</h3>
              <div class="assignment-content" v-html="detailInfo.content || detailInfo.description || '无作业内容'"></div>
            </div>

            <!-- 题目列表 -->
            <div class="questions-section" v-if="detailInfo.questions && detailInfo.questions.length > 0">
              <h3>题目列表 (共{{ detailInfo.questions.length }}题，总分{{ detailInfo.totalPoints }}分)</h3>
              <el-collapse accordion>
                <el-collapse-item 
                  v-for="(item, index) in sortedQuestions" 
                  :key="item.questionId" 
                  :title="`第${item.questionOrder || index + 1}题: ${item.question.typeName} (${item.points}分)`"
                >
                  <div class="question-content">
                    <div class="question-text" v-html="item.question.questionContent"></div>
                    
                    <!-- 选项 -->
                    <div class="question-options" v-if="item.question.options">
                      <div v-for="option in parseOptions(item.question.options)" :key="option.key" class="option-item">
                        <span class="option-key">{{ option.key }}.</span>
                        <span class="option-value">{{ option.value }}</span>
                      </div>
                    </div>
                    
                    <!-- 正确答案 -->
                    <div class="question-answer">
                      <span class="answer-label">正确答案:</span>
                      <span class="answer-value">{{ formatAnswer(item.question) }}</span>
                    </div>
                    
                    <!-- 解析 -->
                    <div class="question-explanation" v-if="item.question.explanation && item.question.explanation !== '无'">
                      <span class="explanation-label">解析:</span>
                      <div class="explanation-text" v-html="item.question.explanation"></div>
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>

            <!-- 提交记录 -->
            <div class="submission-info" v-if="detailInfo.submission">
              <h3>学生提交记录</h3>
              <el-descriptions border :column="2" size="medium">
                <el-descriptions-item label="提交状态">
                  <el-tag :type="getSubmissionStatusType(detailInfo.submission.status)">
                    {{ getSubmissionStatusText(detailInfo.submission.status) }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="提交时间">
                  {{ formatDateTime(detailInfo.submission.submissionTime) }}
                </el-descriptions-item>
                <el-descriptions-item label="得分" v-if="detailInfo.submission.status === 'graded'">
                  {{ detailInfo.submission.totalScore }} / {{ detailInfo.totalPoints }}
                </el-descriptions-item>
                <el-descriptions-item label="评语" v-if="detailInfo.submission.feedback">
                  {{ detailInfo.submission.feedback }}
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="submission-stats" v-if="detailInfo.submittedCount !== undefined">
              <h3>提交统计</h3>
              <el-progress
                :percentage="calculateSubmissionRate(detailInfo.submittedCount, detailInfo.totalCount)"
                :format="percentageFormat"
                :stroke-width="20"
                status="success"
              />
              <div class="stats-detail">
                <span>已提交: {{ detailInfo.submittedCount || 0 }} 人</span>
                <span>总学生: {{ detailInfo.totalCount || 0 }} 人</span>
                <span>提交率: {{ calculateSubmissionRate(detailInfo.submittedCount, detailInfo.totalCount) }}%</span>
              </div>
            </div>
          </template>
          <el-empty v-else description="暂无详细信息"></el-empty>
        </div>
        <template #footer>
          <el-button @click="detailDialog.visible = false">关闭</el-button>
          <el-button type="success" @click="handleRecord(detailInfo)">批阅作业</el-button>
          <el-button type="primary" @click="handleSubmissionList">查看提交列表</el-button>
        </template>
      </el-dialog>
    </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Search, Refresh, View, Edit, Delete, Clock, DocumentCopy } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAssignmentList, getAssignmentInfo, addAssignment, updateAssignment, deleteAssignment } from '@/api/exam/assignment'
import { getCourseList } from '@/api'
import { useRouter } from 'vue-router'

const router = useRouter()

// 加载状态
const loading = ref(false)
const submitLoading = ref(false)
// 作业列表
const assignmentList = ref([])
// 总记录数
const total = ref(0)
// 课程选项
const courseOptions = ref([])
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  status: undefined
})

// 表单引用
const formRef = ref(null)

// 对话框控制
const dialog = reactive({
  visible: false,
  title: ''
})

// 详情对话框控制
const detailDialog = reactive({
  visible: false,
  loading: false
})

// 详情信息
const detailInfo = reactive({
  assignmentId: '',
  courseId: '',
  courseName: '',
  teacherId: '',
  teacherName: '',
  title: '',
  content: '',
  description: '',
  totalPoints: 0,
  startTime: '',
  dueTime: '',
  fileRequirements: '',
  publicScore: false,
  status: 0,
  createTime: '',
  updateTime: '',
  submittedCount: 0,
  totalCount: 0,
  questions: []
})

// 表单数据
const form = reactive({
  assignmentId: undefined,
  courseId: undefined,
  title: '',
  content: '',
  totalPoints: 100,
  startTime: '',
  dueTime: '',
  fileRequirements: '',
  publicScore: true
})

// 表单校验规则
const rules = {
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入作业标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入作业内容', trigger: 'blur' }
  ],
  totalPoints: [
    { required: true, message: '请设置总分', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择发布时间', trigger: 'change' }
  ],
  dueTime: [
    { required: true, message: '请选择截止时间', trigger: 'change' }
  ]
}

/**
 * 查询作业列表
 */
const getList = async () => {
  loading.value = true
  try {
    const response = await getAssignmentList(queryParams)
    assignmentList.value = response.data.rows
    total.value = response.data.total
  } catch (error) {
    console.error('获取作业列表失败', error)
    ElMessage.error('获取作业列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取课程列表
 */
const getCourses = async () => {
  try {
    const response = await getCourseList()
    courseOptions.value = response.data.records || []
  } catch (error) {
    console.error('获取课程列表失败', error)
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
  queryParams.title = ''
  queryParams.status = undefined
  handleQuery()
}

/**
 * 处理添加操作
 */
const handleAdd = () => {
  dialog.visible = true
  dialog.title = '发布作业'
  resetForm()
  
  // 设置默认发布时间为当前时间
  const now = new Date()
  form.startTime = now.getFullYear() + '-' + 
    String(now.getMonth() + 1).padStart(2, '0') + '-' + 
    String(now.getDate()).padStart(2, '0') + 'T' + 
    String(now.getHours()).padStart(2, '0') + ':' + 
    String(now.getMinutes()).padStart(2, '0') + ':' + 
    String(now.getSeconds()).padStart(2, '0')
  
  // 设置默认截止时间为7天后
  const endDate = new Date()
  endDate.setDate(endDate.getDate() + 7)
  form.dueTime = endDate.getFullYear() + '-' + 
    String(endDate.getMonth() + 1).padStart(2, '0') + '-' + 
    String(endDate.getDate()).padStart(2, '0') + 'T' + 
    String(endDate.getHours()).padStart(2, '0') + ':' + 
    String(endDate.getMinutes()).padStart(2, '0') + ':' + 
    String(endDate.getSeconds()).padStart(2, '0')
}

/**
 * 处理修改操作
 */
const handleEdit = async (row) => {
  dialog.visible = true
  dialog.title = '修改作业'
  resetForm()
  
  try {
    loading.value = true
    const response = await getAssignmentInfo(row.assignmentId)
    Object.assign(form, response.data)
  } catch (error) {
    console.error('获取作业详情失败', error)
    ElMessage.error('获取作业详情失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理详情操作
 */
const handleView = async (row) => {
  detailDialog.visible = true
  detailDialog.loading = true
  
  try {
    const response = await getAssignmentInfo(row.assignmentId)
    Object.assign(detailInfo, response.data)
  } catch (error) {
    console.error('获取作业详情失败', error)
    ElMessage.error('获取作业详情失败')
  } finally {
    detailDialog.loading = false
  }
}

/**
 * 处理提交列表操作
 */
const handleSubmissionList = () => {
  if (detailInfo.assignmentId) {
    router.push(`/exam/assignment/submission/${detailInfo.assignmentId}`)
  }
}

/**
 * 处理批阅操作
 */
const handleRecord = (row) => {
  router.push(`/exam/assignment/submission/${row.assignmentId}`)
}

/**
 * 处理删除操作
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除作业"${row.title}"吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteAssignment(row.assignmentId)
        ElMessage.success('删除成功')
        getList()
      } catch (error) {
        console.error('删除作业失败', error)
        ElMessage.error('删除作业失败')
      }
    })
    .catch(() => {})
}

/**
 * 提交表单
 */
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      // 校验截止时间必须大于发布时间
      const startTime = new Date(form.startTime).getTime()
      const dueTime = new Date(form.dueTime).getTime()
      
      if (dueTime <= startTime) {
        ElMessage.error('截止时间必须晚于发布时间')
        return
      }
      
      submitLoading.value = true
      try {
        if (form.assignmentId) {
          // 修改
          await updateAssignment(form)
          ElMessage.success('修改成功')
        } else {
          // 新增
          await addAssignment(form)
          ElMessage.success('发布成功')
        }
        dialog.visible = false
        getList()
      } catch (error) {
        console.error('保存作业失败', error)
        ElMessage.error('保存作业失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

/**
 * 重置表单
 */
const resetForm = () => {
  form.assignmentId = undefined
  form.courseId = undefined
  form.title = ''
  form.content = ''
  form.totalPoints = 100
  form.startTime = ''
  form.dueTime = ''
  form.fileRequirements = ''
  form.publicScore = true
  
  if (formRef.value) {
    formRef.value.resetFields()
  }
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
 * 计算提交率百分比
 */
const calculateSubmissionRate = (submitted, total) => {
  if (!total || total <= 0) return 0
  return Math.round((submitted / total) * 100)
}

/**
 * 格式化百分比显示
 */
const percentageFormat = (percentage) => {
  return percentage + '%'
}

/**
 * 解析选项JSON字符串
 */
const parseOptions = (optionsStr) => {
  if (!optionsStr) return []
  
  try {
    return JSON.parse(optionsStr)
  } catch (error) {
    console.error('解析选项失败:', error)
    return []
  }
}

// 添加计算属性
// 按题目序号排序的题目列表
const sortedQuestions = computed(() => {
  if (!detailInfo.questions || detailInfo.questions.length === 0) return []
  return [...detailInfo.questions].sort((a, b) => {
    return (a.questionOrder || 0) - (b.questionOrder || 0)
  })
})

// 添加格式化方法
/**
 * 格式化答案显示
 */
const formatAnswer = (question) => {
  if (!question.correctAnswer) return '无答案'
  
  // 对于多选题，格式化显示
  if (question.questionTypeId === 2) {
    return question.correctAnswer.split('').join(', ')
  }
  
  return question.correctAnswer
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
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

/**
 * 获取提交状态类型
 */
const getSubmissionStatusType = (status) => {
  if (status === 'submitted') return 'warning'
  if (status === 'graded') return 'success'
  return 'info'
}

/**
 * 获取提交状态文本
 */
const getSubmissionStatusText = (status) => {
  if (status === 'submitted') return '已提交'
  if (status === 'graded') return '已批改'
  return '未知'
}

onMounted(() => {
  getList()
  getCourses()
})
</script>

<style scoped>
.assignment-container {
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

.switch-help {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.detail-content {
  padding: 10px;
  max-height: 70vh;
  overflow-y: auto;
}

.content-section {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.content-section h3 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #303133;
}

.assignment-content {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #f9f9f9;
  min-height: 100px;
  white-space: pre-wrap;
  line-height: 1.6;
}

.submission-stats {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.submission-stats h3 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #303133;
}

.stats-detail {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  color: #606266;
}

.questions-section {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.questions-section h3 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #303133;
}

.question-content {
  padding: 10px;
}

.question-text {
  font-weight: 500;
  margin-bottom: 15px;
  line-height: 1.6;
}

.question-options {
  margin-bottom: 15px;
  padding-left: 20px;
}

.option-item {
  margin-bottom: 8px;
  display: flex;
}

.option-key {
  font-weight: bold;
  margin-right: 10px;
  min-width: 20px;
}

.question-answer {
  margin-top: 15px;
  padding: 10px;
  background-color: #f0f9eb;
  border-radius: 4px;
  display: flex;
  align-items: center;
}

.answer-label {
  font-weight: bold;
  margin-right: 10px;
  color: #67c23a;
}

.answer-value {
  font-weight: 500;
}

.question-explanation {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border-left: 3px solid #909399;
}

.explanation-label {
  font-weight: bold;
  display: block;
  margin-bottom: 5px;
  color: #606266;
}

.explanation-text {
  color: #606266;
  line-height: 1.6;
}

.submission-info {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.submission-info h3 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #303133;
}
</style>