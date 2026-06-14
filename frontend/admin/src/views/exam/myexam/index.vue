<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="title">
            <span>我的考试</span>
            <el-tag v-if="courseId" type="success">{{ courseName }}</el-tag>
          </div>
          <div class="filter-container">
            <el-select
              v-model="queryParams.status"
              placeholder="考试状态"
              clearable
              @change="handleQuery"
            >
              <el-option label="全部" value="" />
              <el-option label="未开始" value="pending" />
              <el-option label="进行中" value="in_progress" />
              <el-option label="已完成" value="completed" />
              <el-option label="已批阅" value="graded" />
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
        :data="examList"
        style="width: 100%"
        border
      >
        <el-table-column
          prop="title"
          label="考试标题"
          min-width="180"
        />
        <el-table-column
          prop="courseName"
          label="所属课程"
          width="180"
          v-if="!courseId"
        />
        <el-table-column
          prop="examType"
          label="考试类型"
          width="120"
        >
          <template #default="scope">
            <el-tag :type="scope.row.examType === 'final' ? 'danger' : 'warning'">
              {{ scope.row.examType === 'final' ? '期末考试' : '期中考试' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="startTime"
          label="开始时间"
          width="180"
          :formatter="dateFormatter"
        />
        <el-table-column
          prop="endTime"
          label="结束时间"
          width="180"
          :formatter="dateFormatter"
        />
        <el-table-column
          prop="duration"
          label="考试时长"
          width="120"
        >
          <template #default="scope">
            {{ scope.row.duration }} 分钟
          </template>
        </el-table-column>
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
              v-if="scope.row.status === 'pending' && isTimeToStart(scope.row)"
              type="primary"
              size="small"
              text
              @click="startExam(scope.row)"
            >
              开始考试
            </el-button>
            <el-button
              v-if="scope.row.status === 'in_progress'"
              type="success"
              size="small"
              text
              @click="continueExam(scope.row)"
            >
              继续考试
            </el-button>
            <el-button
              v-if="scope.row.status === 'completed' || scope.row.status === 'graded'"
              type="info"
              size="small"
              text
              @click="viewExamResult(scope.row)"
            >
              查看结果
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
    
    <!-- 考试确认对话框 -->
    <el-dialog
      v-model="examConfirmVisible"
      title="考试确认"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <div class="exam-confirm-content">
        <h3>{{ currentExam.title }}</h3>
        <div class="exam-info">
          <p><strong>考试时长：</strong>{{ currentExam.duration }} 分钟</p>
          <p><strong>考试时间：</strong>{{ dateFormatter(null, null, currentExam.startTime) }} 至 {{ dateFormatter(null, null, currentExam.endTime) }}</p>
          <p class="warning">注意事项：</p>
          <ul>
            <li>考试开始后，计时不会暂停，请确保考试环境良好</li>
            <li>请勿刷新页面，否则可能导致答案丢失</li>
            <li>请在规定时间内完成考试并提交，超时将自动提交</li>
          </ul>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="examConfirmVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmStartExam">
            确认开始
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
const examList = ref([])
const total = ref(0)
const examConfirmVisible = ref(false)
const currentExam = ref({})

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

// 初始化查询参数
const initQueryParams = () => {
  // 如果有课程ID，添加到查询条件
  if (courseId.value) {
    queryParams.value.courseId = courseId.value
  }
}

// 获取考试列表
const getExamList = async () => {
  loading.value = true
  try {
    // TODO: 调用后端接口获取考试列表
    // const res = await getStudentExams(queryParams.value)
    // 模拟数据
    setTimeout(() => {
      examList.value = [
        {
          examId: 1,
          title: 'JavaScript期中考试',
          courseName: 'Web前端开发技术',
          examType: 'midterm',
          startTime: '2024-04-15 09:00:00',
          endTime: '2024-04-15 11:00:00',
          duration: 120,
          status: 'pending',
          score: null
        },
        {
          examId: 2,
          title: 'Vue框架实践测验',
          courseName: 'Web前端开发技术',
          examType: 'quiz',
          startTime: '2024-04-05 14:00:00',
          endTime: '2024-04-05 15:30:00',
          duration: 90,
          status: 'completed',
          score: null
        },
        {
          examId: 3,
          title: 'HTML与CSS基础考试',
          courseName: 'Web前端开发技术',
          examType: 'quiz',
          startTime: '2024-03-20 10:00:00',
          endTime: '2024-03-20 11:30:00',
          duration: 90,
          status: 'graded',
          score: 88
        }
      ]
      total.value = 3
      loading.value = false
    }, 300)
  } catch (error) {
    console.error('获取考试列表失败', error)
    ElMessage.error('获取考试列表失败')
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
      return 'info'
    case 'in_progress':
      return 'warning'
    case 'completed':
      return 'success'
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
      return '未开始'
    case 'in_progress':
      return '进行中'
    case 'completed':
      return '已完成'
    case 'graded':
      return '已批阅'
    default:
      return '未知'
  }
}

// 判断是否可以开始考试
const isTimeToStart = (exam) => {
  if (!exam.startTime || !exam.endTime) return false
  const now = new Date()
  const startTime = new Date(exam.startTime)
  const endTime = new Date(exam.endTime)
  
  // 当前时间在考试开始和结束时间之间
  return now >= startTime && now <= endTime
}

// 查询
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getExamList()
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
  getExamList()
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
  getExamList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  queryParams.value.pageNum = page
  getExamList()
}

// 开始考试
const startExam = (row) => {
  currentExam.value = row
  examConfirmVisible.value = true
}

// 确认开始考试
const confirmStartExam = () => {
  examConfirmVisible.value = false
  // 跳转到考试页面
  router.push({
    path: `/exam/paper/${currentExam.value.examId}`,
    query: { 
      mode: 'exam',
      duration: currentExam.value.duration
    }
  })
}

// 继续考试
const continueExam = (row) => {
  ElMessageBox.confirm('您有未完成的考试，是否继续？', '提示', {
    confirmButtonText: '继续考试',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.push({
      path: `/exam/paper/${row.examId}`,
      query: { 
        mode: 'exam',
        duration: row.duration
      }
    })
  }).catch(() => {})
}

// 查看考试结果
const viewExamResult = (row) => {
  router.push({
    path: `/exam/record/${row.examId}`,
    query: { 
      mode: 'view'
    }
  })
}

onMounted(() => {
  initQueryParams()
  getExamList()
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
.exam-confirm-content {
  padding: 10px;
}
.exam-info {
  margin-top: 20px;
}
.warning {
  color: #E6A23C;
  font-weight: bold;
  margin-top: 15px;
}
ul {
  padding-left: 20px;
}
li {
  margin-bottom: 5px;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 