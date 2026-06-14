<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="title">
            <span>我的作业</span>
            <el-tag v-if="courseId" type="success">{{ courseName }}</el-tag>
          </div>
          <div class="filter-container">
            <el-select
              v-model="queryParams.status"
              placeholder="作业状态"
              clearable
              @change="handleQuery"
            >
              <el-option label="全部" value="" />
              <el-option label="未提交" value="pending" />
              <el-option label="已提交" value="submitted" />
              <el-option label="已批改" value="graded" />
            </el-select>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleDateRangeChange"
            />
            <el-button type="primary" @click="handleQuery">搜索</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </div>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="assignmentList"
        style="width: 100%"
        border
      >
        <el-table-column
          prop="title"
          label="作业标题"
          min-width="200"
        />
        <el-table-column
          prop="courseName"
          label="所属课程"
          width="180"
          v-if="!courseId"
        />
        <el-table-column
          prop="startTime"
          label="开始时间"
          width="180"
          :formatter="dateFormatter"
        />
        <el-table-column
          prop="endTime"
          label="截止时间"
          width="180"
          :formatter="dateFormatter"
        />
        <el-table-column
          prop="status"
          label="状态"
          width="100"
        >
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="score"
          label="成绩"
          width="80"
        >
          <template #default="scope">
            <span v-if="scope.row.status === 'graded'">{{ scope.row.score }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="200"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              type="primary"
              link
              @click="viewAssignment(scope.row)"
            >
              查看
            </el-button>
            <el-button
              v-if="scope.row.status === 'pending' || scope.row.status === 'submitted'"
              type="success"
              link
              @click="submitAssignment(scope.row)"
              :disabled="isExpired(scope.row.endTime)"
            >
              {{ scope.row.status === 'pending' ? '提交' : '重新提交' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          :current-page="queryParams.pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 提交作业对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="提交作业"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="submitFormRef"
        :model="submitForm"
        :rules="submitRules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="submitForm.title" disabled />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="submitForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入作业内容"
          />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            action="#"
            list-type="text"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持格式：.pdf, .doc, .docx, .zip, .rar，大小不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="submitDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitAssignment">
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const assignmentList = ref([])
const total = ref(0)
const submitDialogVisible = ref(false)
const fileList = ref([])
const submitFormRef = ref(null)

// 从路由参数获取课程ID和名称
const courseId = computed(() => route.query.courseId)
const courseName = computed(() => route.query.courseName)

// 日期范围
const dateRange = ref([])

// 查询参数
const queryParams = ref({
  courseId: '',
  status: '',
  startTime: '',
  endTime: '',
  pageNum: 1,
  pageSize: 10
})

// 提交表单
const submitForm = ref({
  assignmentId: '',
  title: '',
  content: '',
  files: []
})

// 表单验证规则
const submitRules = {
  content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }]
}

// 初始化查询参数
const initQueryParams = () => {
  // 如果有课程ID，添加到查询条件
  if (courseId.value) {
    queryParams.value.courseId = courseId.value
  }
}

// 获取作业列表
const getAssignmentList = async () => {
  loading.value = true
  try {
    // TODO: 调用后端接口获取作业列表
    // const res = await getStudentAssignments(queryParams.value)
    // 模拟数据
    setTimeout(() => {
      assignmentList.value = [
        {
          assignmentId: 1,
          title: 'JavaScript基础练习',
          courseName: 'Web前端开发技术',
          startTime: '2024-04-01 00:00:00',
          endTime: '2024-04-30 23:59:59',
          status: 'pending',
          score: null
        },
        {
          assignmentId: 2,
          title: 'Vue组件开发作业',
          courseName: 'Web前端开发技术',
          startTime: '2024-03-15 00:00:00',
          endTime: '2024-04-15 23:59:59',
          status: 'submitted',
          score: null
        },
        {
          assignmentId: 3,
          title: 'React项目实践',
          courseName: 'Web前端开发技术',
          startTime: '2024-03-01 00:00:00',
          endTime: '2024-03-30 23:59:59',
          status: 'graded',
          score: 92
        }
      ]
      total.value = 3
      loading.value = false
    }, 300)
  } catch (error) {
    console.error('获取作业列表失败', error)
    ElMessage.error('获取作业列表失败')
    loading.value = false
  }
}

// 日期格式化
const dateFormatter = (row, column, cellValue) => {
  if (!cellValue) return ''
  return cellValue.substring(0, 16).replace('T', ' ')
}

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case 'pending':
      return 'warning'
    case 'submitted':
      return 'info'
    case 'graded':
      return 'success'
    default:
      return ''
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'pending':
      return '未提交'
    case 'submitted':
      return '已提交'
    case 'graded':
      return '已批改'
    default:
      return '未知'
  }
}

// 判断是否过期
const isExpired = (endTime) => {
  if (!endTime) return false
  return new Date(endTime) < new Date()
}

// 查询
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getAssignmentList()
}

// 重置查询
const resetQuery = () => {
  dateRange.value = []
  queryParams.value = {
    courseId: courseId.value || '',
    status: '',
    startTime: '',
    endTime: '',
    pageNum: 1,
    pageSize: 10
  }
  getAssignmentList()
}

// 日期范围变化
const handleDateRangeChange = (val) => {
  if (val) {
    queryParams.value.startTime = val[0]
    queryParams.value.endTime = val[1]
  } else {
    queryParams.value.startTime = ''
    queryParams.value.endTime = ''
  }
}

// 处理每页数量变化
const handleSizeChange = (size) => {
  queryParams.value.pageSize = size
  getAssignmentList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  queryParams.value.pageNum = page
  getAssignmentList()
}

// 查看作业
const viewAssignment = (row) => {
  router.push({
    path: `/exam/assignment/${row.assignmentId}`,
    query: { 
      mode: 'view',
      courseId: courseId.value,
      courseName: courseName.value
    }
  })
}

// 提交作业
const submitAssignment = (row) => {
  submitForm.value = {
    assignmentId: row.assignmentId,
    title: row.title,
    content: '',
    files: []
  }
  fileList.value = []
  submitDialogVisible.value = true
}

// 处理文件变化
const handleFileChange = (file) => {
  // 限制文件大小 (10MB)
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB!')
    return
  }
  
  // 限制文件类型
  const allowTypes = ['.pdf', '.doc', '.docx', '.zip', '.rar']
  const fileName = file.name.toLowerCase()
  const isAllowedType = allowTypes.some(type => fileName.endsWith(type))
  if (!isAllowedType) {
    ElMessage.error('文件类型不支持!')
    return
  }
}

// 处理文件移除
const handleFileRemove = (file, fileList) => {
  fileList.value = fileList
}

// 处理作业提交
const handleSubmitAssignment = () => {
  submitFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // TODO: 调用后端接口提交作业
        // await submitAssignmentApi(submitForm.value)
        // 模拟提交成功
        ElMessage.success('作业提交成功')
        submitDialogVisible.value = false
        
        // 更新列表状态
        const index = assignmentList.value.findIndex(
          item => item.assignmentId === submitForm.value.assignmentId
        )
        if (index !== -1) {
          assignmentList.value[index].status = 'submitted'
        }
      } catch (error) {
        console.error('作业提交失败', error)
        ElMessage.error('作业提交失败，请稍后重试')
      }
    }
  })
}

onMounted(() => {
  initQueryParams()
  getAssignmentList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.card-header {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.title {
  display: flex;
  align-items: center;
  gap: 10px;
}
.filter-container {
  display: flex;
  gap: 10px;
  align-items: center;
}
.pagination-container {
  margin-top: 15px;
  text-align: right;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 