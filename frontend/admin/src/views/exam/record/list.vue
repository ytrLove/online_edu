<template>
  <div class="app-container">
    <div class="filter-container">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>考试记录列表</span>
            <div>
              <el-button type="primary" @click="goBack">返回</el-button>
            </div>
          </div>
        </template>

        <!-- 基本信息 -->
        <div class="exam-info" v-if="examInfo.examId">
          <h2>{{ examInfo.title }}</h2>
          <div class="exam-meta">
            <span>总分: {{ examInfo.totalPoints }} 分</span>
            <span>考试时间: {{ examInfo.startTime }} 至 {{ examInfo.endTime }}</span>
            <span>答题时长: {{ examInfo.duration }} 分钟</span>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="stats-container" v-if="examStats.totalCount > 0">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">参与人数</div>
                <div class="stat-value">{{ examStats.totalCount }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">已批改</div>
                <div class="stat-value">{{ examStats.gradedCount }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">平均分</div>
                <div class="stat-value">{{ examStats.averageScore }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-title">最高分</div>
                <div class="stat-value">{{ examStats.highestScore }}</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 搜索区域 -->
        <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form" v-show="showSearch">
          <el-form-item label="学生姓名">
            <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
              <el-option label="未开始" value="not_started" />
              <el-option label="进行中" value="in_progress" />
              <el-option label="已提交" value="submitted" />
              <el-option label="已批改" value="graded" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">搜索</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 表格 -->
        <el-table v-loading="loading" :data="recordList" border>
          <el-table-column label="序号" type="index" width="50" align="center" />
          <el-table-column label="学生姓名" prop="studentName" min-width="100" />
          <el-table-column label="开始时间" prop="startTime" min-width="150" />
          <el-table-column label="提交时间" prop="submitTime" min-width="150" />
          <el-table-column label="总分" prop="totalScore" width="100" align="center">
            <template #default="scope">
              <span v-if="scope.row.totalScore !== null">{{ scope.row.totalScore }} / {{ examInfo.totalPoints }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" prop="status" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <el-button v-if="scope.row.status === 'not_started' || scope.row.status === 'in_progress'" 
                         size="small" type="info" disabled>待提交</el-button>
              <el-button v-else-if="scope.row.status === 'submitted'" 
                         size="small" type="primary" @click="handleGrade(scope.row)">批阅</el-button>
              <el-button v-else-if="scope.row.status === 'graded'" 
                         size="small" type="success" @click="viewGradedRecord(scope.row)">查看</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
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
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamPaperInfo } from '@/api/exam/paper'
import { getExamRecordList, getExamStats, deleteExamRecord } from '@/api/exam/record'

const route = useRoute()
const router = useRouter()

// 展示搜索条件
const showSearch = ref(true)
// 加载状态
const loading = ref(false)
// 考试ID
const examId = ref(null)
// 考试信息
const examInfo = reactive({
  examId: '',
  title: '',
  totalPoints: 100,
  startTime: '',
  endTime: '',
  duration: 120
})
// 考试统计信息
const examStats = reactive({
  totalCount: 0,
  submittedCount: 0,
  gradedCount: 0,
  averageScore: 0,
  highestScore: 0
})
// 记录列表
const recordList = ref([])
// 总记录数
const total = ref(0)
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  examId: '',
  studentName: '',
  status: ''
})

// 生命周期钩子
onMounted(() => {
  examId.value = route.params.id
  if (!examId.value) {
    ElMessage.error('考试ID不能为空')
    return
  }
  
  queryParams.examId = examId.value
  initData()
})

/**
 * 初始化数据
 */
const initData = async () => {
  await getExamInformation()
  await getExamStatistics()
  await getList()
}

/**
 * 获取考试信息
 */
const getExamInformation = async () => {
  try {
    const response = await getExamPaperInfo(examId.value)
    Object.assign(examInfo, response.data)
  } catch (error) {
    console.error('获取考试信息失败', error)
    ElMessage.error('获取考试信息失败')
  }
}

/**
 * 获取考试统计信息
 */
const getExamStatistics = async () => {
  try {
    const response = await getExamStats(examId.value)
    examStats.totalCount = response.data?.length || 0
    examStats.gradedCount = response.data?.filter(item => item.status === 'graded').length || 0
    
    // 计算平均分
    if (examStats.gradedCount > 0) {
      const totalScore = response.data
        .filter(item => item.status === 'graded' && item.totalScore)
        .reduce((sum, item) => sum + Number(item.totalScore), 0)
      examStats.averageScore = Number((totalScore / examStats.gradedCount).toFixed(2))
    }
    
    // 计算最高分
    examStats.highestScore = response.data
      .filter(item => item.status === 'graded' && item.totalScore)
      .reduce((max, item) => Math.max(max, Number(item.totalScore)), 0)
  } catch (error) {
    console.error('获取考试统计信息失败', error)
  }
}

/**
 * 获取记录列表
 */
const getList = async () => {
  if (!queryParams.examId) return
  
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
 * 获取状态标签类型
 */
const getStatusType = (status) => {
  const statusMap = {
    'not_started': 'info',
    'in_progress': 'warning',
    'submitted': 'primary',
    'graded': 'success'
  }
  return statusMap[status] || 'info'
}

/**
 * 获取状态标签文本
 */
const getStatusLabel = (status) => {
  const statusMap = {
    'not_started': '未开始',
    'in_progress': '进行中',
    'submitted': '已提交',
    'graded': '已批改'
  }
  return statusMap[status] || status
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
  queryParams.studentName = ''
  queryParams.status = ''
  handleQuery()
}

/**
 * 处理批阅操作
 */
const handleGrade = (row) => {
  router.push(`/exam/record/grade/${row.recordId}`)
}

/**
 * 查看已批阅的记录
 */
const viewGradedRecord = (row) => {
  router.push(`/exam/record/detail/${row.recordId}`)
}

/**
 * 处理删除操作
 */
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该考试记录吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteExamRecord(row.recordId)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除考试记录失败', error)
      ElMessage.error('删除考试记录失败')
    }
  }).catch(() => {})
}

/**
 * 返回按钮操作
 */
const goBack = () => {
  router.go(-1)
}

/**
 * 处理分页大小变化
 */
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  getList()
}

/**
 * 处理页码变化
 */
const handleCurrentChange = (page) => {
  queryParams.pageNum = page
  getList()
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

.exam-meta {
  display: flex;
  color: #666;
  margin-top: 8px;
}

.exam-meta span {
  margin-right: 20px;
}

.stats-container {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.stat-card {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  text-align: center;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 