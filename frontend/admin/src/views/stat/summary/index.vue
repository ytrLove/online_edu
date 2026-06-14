<template>
  <div class="app-container1">
    <!-- 搜索区域 -->
    <el-card class="filter-container" shadow="never">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="学生姓名" prop="studentName">
          <el-input
            v-model="queryParams.studentName"
            placeholder="请输入学生姓名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="时间范围" prop="timeRange">
          <el-date-picker
            v-model="queryParams.timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD HH:mm:ss"
            :default-time="['00:00:00', '23:59:59']"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-container" shadow="never">
      <template #header>
        <div class="card-header">
          <span>学习情况汇总</span>
          <el-button type="primary" @click="handleGenerate">
            <el-icon><Plus /></el-icon>
            生成汇总
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="summaryList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column label="学生信息" min-width="120">
          <template #default="{ row }">
            <div class="student-info">
              <el-avatar :size="32" :src="row.avatar || '/avatar.png'" />
              <span class="student-name">{{ row.studentName || '未知学生' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="courseName" label="课程名称" min-width="150" show-overflow-tooltip />
        
        <el-table-column label="学习时长" align="center" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getTimeTagType(row.totalStudyTime)">
              {{ formatStudyTime(row.totalStudyTime) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="考试成绩" align="center" min-width="100">
          <template #default="{ row }">
            <el-progress
              :percentage="row.examAvgScore || 0"
              :status="getScoreStatus(row.examAvgScore)"
            />
          </template>
        </el-table-column>
        
        <el-table-column label="作业完成率" align="center" min-width="120">
          <template #default="{ row }">
            <el-progress
              :percentage="row.assignmentCompletion || 0"
              :status="getCompletionStatus(row.assignmentCompletion)"
            />
          </template>
        </el-table-column>
        
        <el-table-column label="学习活跃度" align="center" min-width="200">
          <template #default="{ row }">
            <div class="activity-info">
              <el-tooltip content="资源访问次数">
                <el-tag type="info" effect="plain">
                  <el-icon><Document /></el-icon>
                  {{ row.resourceAccessCount || 0 }}
                </el-tag>
              </el-tooltip>
              <el-tooltip content="讨论参与次数">
                <el-tag type="success" effect="plain">
                  <el-icon><ChatDotRound /></el-icon>
                  {{ row.discussionCount || 0 }}
                </el-tag>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="最后活跃" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tooltip :content="formatDateTime(row.lastActiveTime)">
              <span>{{ formatTimeAgo(row.lastActiveTime) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" link @click="handleDetail(row)">
                详情
              </el-button>
              <el-button type="success" link @click="handleAnalysis(row)">
                分析
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          current-page="queryParams.pageNum"
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Refresh, Document, ChatDotRound } from '@element-plus/icons-vue'
import { getSummaryList, generateSummary } from '@/api/stat/summary'
import { formatTimeAgo, formatDateTime } from '@/utils/formatTime'

const loading = ref(false)
const summaryList = ref([])
const total = ref(0)

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  studentName: '',
  timeRange: []
})

// 获取数据列表
const getList = async () => {
  try {
    loading.value = true
    const { startTime, endTime } = getTimeRange()
    const response = await getSummaryList({
      ...queryParams.value,
      startTime,
      endTime
    })
    summaryList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取学习汇总列表失败：', error)
    ElMessage.error('获取数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理时间范围
const getTimeRange = () => {
  const [startTime, endTime] = queryParams.value.timeRange || []
  return { startTime, endTime }
}

// 格式化学习时长
const formatStudyTime = (minutes) => {
  if (!minutes) return '0分钟'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const remainMinutes = minutes % 60
  return remainMinutes ? `${hours}小时${remainMinutes}分钟` : `${hours}小时`
}

// 获取时长标签类型
const getTimeTagType = (minutes) => {
  if (!minutes) return 'info'
  if (minutes < 30) return 'warning'
  if (minutes < 120) return 'success'
  return 'primary'
}

// 获取成绩状态
const getScoreStatus = (score) => {
  if (!score) return 'exception'
  if (score < 60) return 'exception'
  if (score < 80) return 'warning'
  return 'success'
}

// 获取完成率状态
const getCompletionStatus = (rate) => {
  if (!rate) return 'exception'
  if (rate < 30) return 'exception'
  if (rate < 70) return 'warning'
  return 'success'
}

// 查询操作
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}

// 重置操作
const resetQuery = () => {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    studentName: '',
    timeRange: []
  }
  getList()
}

// 生成汇总
const handleGenerate = async () => {
  try {
    const { startTime, endTime } = getTimeRange()
    await ElMessageBox.confirm('确认要重新生成学习情况汇总数据吗？', '提示', {
      type: 'warning'
    })
    const response = await generateSummary({ startTime, endTime })
    ElMessage.success('生成成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('生成汇总失败：', error)
      ElMessage.error('生成失败，请稍后重试')
    }
  }
}

// 查看详情
const handleDetail = (row) => {
  // TODO: 实现查看详情功能
  console.log('查看详情', row)
}

// 查看分析
const handleAnalysis = (row) => {
  // TODO: 实现查看分析功能
  console.log('查看分析', row)
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.app-container1 {
  padding: 20px;
  
  .filter-container {
    margin-bottom: 20px;
  }

  .table-container {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .student-info {
    display: flex;
    align-items: center;
    gap: 8px;

    .student-name {
      font-weight: 500;
    }
  }

  .activity-info {
    display: flex;
    justify-content: center;
    gap: 12px;

    .el-tag {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style> 