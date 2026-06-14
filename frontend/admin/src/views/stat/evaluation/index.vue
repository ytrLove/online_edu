<template>
  <div class="container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教学评价管理</span>
          <div>
            <el-select
              v-model="queryParams.courseId"
              placeholder="选择课程"
              clearable
              @change="handleCourseChange"
              style="width: 200px; margin-right: 10px;"
            >
              <el-option
                v-for="item in courseOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <el-input
              v-model="queryParams.teacherName"
              placeholder="搜索教师姓名"
              style="width: 200px; margin-right: 10px;"
              clearable
              @keyup.enter="handleQuery"
            />
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 300px; margin-right: 10px;"
              @change="handleDateRangeChange"
            />
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon> 搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon> 重置
            </el-button>
          </div>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stat-cards">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">评价总数</div>
            <div class="stat-value">{{ stats.totalEvaluations || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">平均教学质量</div>
            <div class="stat-value">{{ stats.avgTeachingQuality ? stats.avgTeachingQuality.toFixed(1) : 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">平均内容质量</div>
            <div class="stat-value">{{ stats.avgContentQuality ? stats.avgContentQuality.toFixed(1) : 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">平均总体评分</div>
            <div class="stat-value">{{ stats.avgOverallScore ? stats.avgOverallScore.toFixed(1) : 0 }}</div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 评价列表 -->
      <el-table
        v-loading="loading"
        :data="evaluationList"
        border
        style="width: 100%; margin-top: 20px;"
      >
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="teacherName" label="教师姓名" width="120" align="center" />
        <el-table-column prop="studentName" label="评价学生" width="120" align="center" />
        <el-table-column prop="teachingQuality" label="教学质量" width="100" align="center">
          <template #default="scope">
            <el-rate
              v-model="scope.row.teachingQuality"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column prop="contentQuality" label="内容质量" width="100" align="center">
          <template #default="scope">
            <el-rate
              v-model="scope.row.contentQuality"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column prop="interactionScore" label="互动评分" width="100" align="center">
          <template #default="scope">
            <el-rate
              v-model="scope.row.interactionScore"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column prop="resourceQuality" label="资源质量" width="100" align="center">
          <template #default="scope">
            <el-rate
              v-model="scope.row.resourceQuality"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column prop="overallScore" label="总体评分" width="100" align="center">
          <template #default="scope">
            <el-rate
              v-model="scope.row.overallScore"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column prop="evaluateTime" label="评价时间" width="160" align="center" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; display: flex; justify-content: flex-end;"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="教学评价详情"
      width="700px"
      destroy-on-close
    >
      <div v-loading="detailLoading">
        <el-descriptions border :column="2">
          <el-descriptions-item label="课程名称" :span="2">{{ currentEvaluation.courseName }}</el-descriptions-item>
          <el-descriptions-item label="教师姓名">{{ currentEvaluation.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="评价学生">{{ currentEvaluation.studentName }}</el-descriptions-item>
          <el-descriptions-item label="评价时间">{{ currentEvaluation.evaluateTime }}</el-descriptions-item>
          <el-descriptions-item label="总体评分">
            <el-rate
              v-model="currentEvaluation.overallScore"
              disabled
              show-score
              text-color="#ff9900"
            />
          </el-descriptions-item>
        </el-descriptions>

        <div class="score-detail">
          <h3>评分详情</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="score-item">
                <span class="score-label">教学质量：</span>
                <el-rate
                  v-model="currentEvaluation.teachingQuality"
                  disabled
                  show-score
                  text-color="#ff9900"
                />
              </div>
            </el-col>
            <el-col :span="12">
              <div class="score-item">
                <span class="score-label">内容质量：</span>
                <el-rate
                  v-model="currentEvaluation.contentQuality"
                  disabled
                  show-score
                  text-color="#ff9900"
                />
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="score-item">
                <span class="score-label">互动评分：</span>
                <el-rate
                  v-model="currentEvaluation.interactionScore"
                  disabled
                  show-score
                  text-color="#ff9900"
                />
              </div>
            </el-col>
            <el-col :span="12">
              <div class="score-item">
                <span class="score-label">资源质量：</span>
                <el-rate
                  v-model="currentEvaluation.resourceQuality"
                  disabled
                  show-score
                  text-color="#ff9900"
                />
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="suggest-section" v-if="currentEvaluation.improvementSuggest">
          <h3>改进建议</h3>
          <div class="suggest-content">{{ currentEvaluation.improvementSuggest }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getTeacherEvaluationList, getCourseEvaluationStats, getEvaluationDetail } from '@/api/stat/evaluation'
import { getCourseList } from '@/api/edu/course'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  courseId: undefined,
  teacherName: '',
  startTime: '',
  endTime: ''
})

// 日期范围
const dateRange = ref([])

// 课程选项
const courseOptions = ref([])

// 评价列表数据
const evaluationList = ref([])
const total = ref(0)
const loading = ref(false)

// 统计数据
const stats = reactive({
  totalEvaluations: 0,
  avgTeachingQuality: 0,
  avgContentQuality: 0,
  avgInteractionScore: 0,
  avgResourceQuality: 0,
  avgOverallScore: 0
})

// 详情对话框
const dialogVisible = ref(false)
const detailLoading = ref(false)
const currentEvaluation = reactive({
  evaluationId: '',
  courseId: '',
  courseName: '',
  teacherId: '',
  teacherName: '',
  studentId: '',
  studentName: '',
  teachingQuality: 0,
  contentQuality: 0,
  interactionScore: 0,
  resourceQuality: 0,
  overallScore: 0,
  improvementSuggest: '',
  evaluateTime: ''
})

// 获取评价列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getTeacherEvaluationList(queryParams)
    evaluationList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取评价列表失败', error)
    ElMessage.error('获取评价列表失败')
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const getStats = async () => {
  if (!queryParams.courseId) {
    return
  }
  try {
    const res = await getCourseEvaluationStats(queryParams.courseId)
    if (res.code === 200) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

// 获取课程列表
const getCourses = async () => {
  try {
    const res = await getCourseList({pageSize: 999})
    courseOptions.value = res.data.records.map(item => ({
      value: item.courseId,
      label: item.courseName
    }))
  } catch (error) {
    console.error('获取课程列表失败', error)
  }
}

// 查询按钮点击
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
  getStats()
}

// 重置按钮点击
const handleReset = () => {
  dateRange.value = []
  Object.assign(queryParams, {
    pageNum: 1,
    courseId: undefined,
    teacherName: '',
    startTime: '',
    endTime: ''
  })
  getList()
}

// 课程选择变化
const handleCourseChange = (val) => {
  queryParams.courseId = val
  if (val) {  // 只在选择了课程时获取统计数据
    getStats()
  } else {
    // 重置统计数据
    Object.assign(stats, {
      totalEvaluations: 0,
      avgTeachingQuality: 0,
      avgContentQuality: 0,
      avgInteractionScore: 0,
      avgResourceQuality: 0,
      avgOverallScore: 0
    })
  }
}

// 查看详情
const handleViewDetail = async (row) => {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    const res = await getEvaluationDetail(row.evaluationId)
    if (res.code === 200) {
      Object.assign(currentEvaluation, res.data)
    }
  } catch (error) {
    console.error('获取评价详情失败', error)
    ElMessage.error('获取评价详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 日期范围变化
const handleDateRangeChange = (val) => {
  if (val) {
    queryParams.startTime = val[0]
    queryParams.endTime = val[1]
  } else {
    queryParams.startTime = ''
    queryParams.endTime = ''
  }
}

// 页码变化
const handlePageChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 每页条数变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

onMounted(() => {
  getCourses()
  getList()
})
</script>

<style scoped>
.container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-cards {
  margin-top: 20px;
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.score-detail,
.suggest-section {
  margin-top: 20px;
}

h3 {
  font-size: 16px;
  margin-bottom: 15px;
  font-weight: 500;
  color: #303133;
}

.score-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.score-label {
  width: 90px;
}

.suggest-content {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  min-height: 80px;
}
</style> 