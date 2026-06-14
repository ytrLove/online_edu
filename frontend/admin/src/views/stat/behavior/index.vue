<template>
  <div class="container">
    <el-card class="main-card">
      <template #header>
        <div class="card-header">
          <div class="title-area">
            <el-icon class="title-icon"><DataAnalysis /></el-icon>
            <span>学习行为分析</span>
          </div>
          <div class="filter-area">
            <el-select
              v-model="queryParams.courseId"
              placeholder="选择课程"
              clearable
              @change="handleQuery"
              style="width: 200px; margin-right: 10px;"
            >
              <el-option
                v-for="item in courseOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <el-select
              v-model="queryParams.studentId"
              placeholder="选择学生"
              clearable
              @change="handleQuery"
              style="width: 200px; margin-right: 10px;"
            >
              <el-option
                v-for="item in studentOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
            <el-select
              v-model="queryParams.behaviorType"
              placeholder="行为类型"
              clearable
              @change="handleQuery"
              style="width: 200px; margin-right: 10px;"
            >
              <el-option label="观看视频" value="VIDEO_WATCH" />
              <el-option label="资料下载" value="RESOURCE_DOWNLOAD" />
              <el-option label="作业提交" value="ASSIGNMENT_SUBMIT" />
              <el-option label="论坛参与" value="FORUM_PARTICIPATE" />
              <el-option label="测验完成" value="QUIZ_COMPLETE" />
            </el-select>
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
            <el-button type="primary" @click="handleQuery" :icon="Search">
              搜索
            </el-button>
            <el-button @click="resetQuery" :icon="Refresh">
              重置
            </el-button>
            <el-button type="success" @click="exportData" :icon="Download">
              导出
            </el-button>
          </div>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stat-cards">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="stat-card stat-card-1">
            <div class="stat-title">行为记录总数</div>
            <div class="stat-value">{{ stats.totalBehaviors || 0 }}</div>
            <el-icon class="stat-icon"><Document /></el-icon>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="stat-card stat-card-2">
            <div class="stat-title">活跃学生数</div>
            <div class="stat-value">{{ stats.activeStudents || 0 }}</div>
            <el-icon class="stat-icon"><User /></el-icon>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="stat-card stat-card-3">
            <div class="stat-title">平均学习时长</div>
            <div class="stat-value">{{ stats.avgStudyDuration ? stats.avgStudyDuration.toFixed(1) : 0 }}分钟</div>
            <el-icon class="stat-icon"><Timer /></el-icon>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="stat-card stat-card-4">
            <div class="stat-title">平均完成率</div>
            <div class="stat-value">{{ stats.avgCompletionRate ? (stats.avgCompletionRate * 100).toFixed(1) : 0 }}%</div>
            <el-icon class="stat-icon"><Check /></el-icon>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表分析 -->
      <el-row :gutter="20" class="chart-section">
        <el-col :xs="24" :sm="24" :md="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <el-icon><PieChart /></el-icon>
                行为类型分布
              </div>
            </template>
            <div ref="behaviorTypeChart" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="24" :md="12">
          <el-card class="chart-card">
            <template #header>
              <div class="chart-header">
                <el-icon><TrendCharts /></el-icon>
                每日活跃度趋势
              </div>
            </template>
            <div ref="activeChart" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 行为记录列表 -->
      <div class="table-header">
        <el-icon><Grid /></el-icon>
        <span>行为记录列表</span>
      </div>
      <el-table
        v-loading="loading"
        :data="behaviorList"
        border
        style="width: 100%; margin-top: 10px;"
        stripe
        highlight-current-row
        @row-click="viewDetail"
      >
        <el-table-column prop="courseName" label="课程名称" min-width="150">
          <template #default="scope">
            <el-tooltip :content="scope.row.courseName" placement="top" :show-after="500">
              <span>{{ scope.row.courseName || '未知课程' }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="studentName" label="学生姓名" width="120" align="center" />
        <el-table-column prop="behaviorType" label="行为类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getBehaviorTypeTag(scope.row.behaviorType)">
              {{ getBehaviorTypeLabel(scope.row.behaviorType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="行为描述" min-width="200">
          <template #default="scope">
            <el-tooltip :content="scope.row.description" placement="top" :show-after="500">
              <span>{{ scope.row.description || '-' }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="持续时长" width="100" align="center">
          <template #default="scope">
            <span v-if="scope.row.duration">{{ scope.row.duration }}分钟</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="记录时间" width="160" align="center" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link @click.stop="viewDetail(scope.row)">
              <el-icon><View /></el-icon> 详情
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
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="学习行为详情"
      width="700px"
      destroy-on-close
      top="5vh"
    >
      <div v-loading="detailLoading" class="dialog-content">
        <el-descriptions border :column="2" direction="vertical">
          <el-descriptions-item label="课程名称" :span="2">
            <el-tag type="info" effect="plain" size="large">{{ currentBehavior.courseName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学生姓名">
            <el-avatar :size="24" style="margin-right: 5px">
              {{ currentBehavior.studentName ? currentBehavior.studentName.substring(0, 1) : 'U' }}
            </el-avatar>
            {{ currentBehavior.studentName }}
          </el-descriptions-item>
          <el-descriptions-item label="行为类型">
            <el-tag :type="getBehaviorTypeTag(currentBehavior.behaviorType)">
              {{ getBehaviorTypeLabel(currentBehavior.behaviorType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="记录时间">{{ currentBehavior.createTime }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentBehavior.ipAddress || '-' }}</el-descriptions-item>
          <el-descriptions-item label="行为描述" :span="2">{{ currentBehavior.description }}</el-descriptions-item>
          <el-descriptions-item label="持续时长" v-if="currentBehavior.duration">
            <el-tag type="success">{{ currentBehavior.duration }}分钟</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="完成率" v-if="currentBehavior.completionRate != null">
            <el-progress 
              :percentage="(currentBehavior.completionRate * 100).toFixed(0)" 
              :color="getProgressColor(currentBehavior.completionRate)"
              :stroke-width="15"
            />
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-section" v-if="currentBehavior.resourceInfo">
          <h3><el-icon><InfoFilled /></el-icon> 资源信息</h3>
          <div class="detail-content">{{ currentBehavior.resourceInfo }}</div>
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
import { ref, reactive, onMounted, nextTick } from 'vue'
import { Search, Refresh, Download, DataAnalysis, Document, User, Timer, Check, 
          PieChart, TrendCharts, Grid, View, InfoFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getBehaviorList, getBehaviorStats, exportBehaviorData } from '@/api/stat/behavior'
import { getCourseList } from '@/api/edu/course'
import { getStudentList } from '@/api/edu/student'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  courseId: undefined,
  studentId: undefined,
  behaviorType: undefined,
  startTime: '',
  endTime: ''
})

// 日期范围
const dateRange = ref([])

// 选项数据
const courseOptions = ref([])
const studentOptions = ref([])

// 行为列表数据
const behaviorList = ref([])
const total = ref(0)
const loading = ref(false)

// 统计数据
const stats = reactive({
  totalBehaviors: 0,
  activeStudents: 0,
  avgStudyDuration: 0,
  avgCompletionRate: 0,
  behaviorTypeDistribution: {},
  activityTrend: []
})

// 图表实例
let behaviorTypeChartInstance = null
let activeChartInstance = null
const behaviorTypeChart = ref(null)
const activeChart = ref(null)

// 详情对话框
const dialogVisible = ref(false)
const detailLoading = ref(false)
const currentBehavior = reactive({
  behaviorId: '',
  courseId: '',
  courseName: '',
  studentId: '',
  studentName: '',
  behaviorType: '',
  description: '',
  createTime: '',
  duration: null,
  completionRate: null,
  ipAddress: '',
  resourceInfo: ''
})

// 获取行为列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getBehaviorList(queryParams)
    behaviorList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取行为列表失败', error)
    ElMessage.error('获取行为列表失败')
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const getStatistics = async () => {
  try {
    const res = await getBehaviorStats(queryParams)
    Object.assign(stats, res.data)
    nextTick(() => {
      initCharts()
    })
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

// 获取课程选项
const getCourseOptions = async () => {
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

// 获取学生选项
const getStudentOptions = async () => {
  try {
    const res = await getStudentList({pageSize: 999})
    studentOptions.value = res.data.records.map(item => ({
      value: item.studentId,
      label: item.username
    }))
  } catch (error) {
    console.error('获取学生列表失败', error)
  }
}

// 初始化图表
const initCharts = () => {
  // 行为类型分布饼图
  if (behaviorTypeChartInstance) {
    behaviorTypeChartInstance.dispose()
  }
  behaviorTypeChartInstance = echarts.init(behaviorTypeChart.value)
  const behaviorTypeData = Object.entries(stats.behaviorTypeDistribution || {}).map(([key, value]) => ({
    name: getBehaviorTypeLabel(key),
    value
  }))
  behaviorTypeChartInstance.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: behaviorTypeData.map(item => item.name)
    },
    series: [
      {
        name: '行为类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: behaviorTypeData
      }
    ]
  })

  // 每日活跃度趋势图
  if (activeChartInstance) {
    activeChartInstance.dispose()
  }
  activeChartInstance = echarts.init(activeChart.value)
  const activityTrend = stats.activityTrend || []
  activeChartInstance.setOption({
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: activityTrend.map(item => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '活跃人数',
        type: 'line',
        smooth: true,
        data: activityTrend.map(item => item.activeCount),
        areaStyle: {
          opacity: 0.3
        },
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '行为记录数',
        type: 'bar',
        data: activityTrend.map(item => item.behaviorCount),
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  })
}

// 查看详情
const viewDetail = (row) => {
  dialogVisible.value = true
  Object.assign(currentBehavior, row)
}

// 处理日期范围变化
const handleDateRangeChange = (val) => {
  if (val) {
    queryParams.startTime = val[0]
    queryParams.endTime = val[1]
  } else {
    queryParams.startTime = ''
    queryParams.endTime = ''
  }
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
  getStatistics()
}

// 重置按钮操作
const resetQuery = () => {
  dateRange.value = []
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    courseId: undefined,
    studentId: undefined,
    behaviorType: undefined,
    startTime: '',
    endTime: ''
  })
  handleQuery()
}

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 导出数据
const exportData = async () => {
  try {
    // 构建查询参数
    const params = new URLSearchParams()
    if (queryParams.courseId) params.append('courseId', queryParams.courseId)
    if (queryParams.studentId) params.append('studentId', queryParams.studentId)
    if (queryParams.behaviorType) params.append('behaviorType', queryParams.behaviorType)
    if (queryParams.startTime) params.append('startTime', queryParams.startTime)
    if (queryParams.endTime) params.append('endTime', queryParams.endTime)
    
    // 构建下载 URL

    // const baseUrl = import.meta.env.VITE_APP_BASE_API || ''
    // const downloadUrl = `${baseUrl}/api/api/stat/behavior/export?${params.toString()}`
    
    // // 创建下载链接
    // const link = document.createElement('a')
    // link.href = downloadUrl
    // link.target = '_blank'
    // link.download = '学习行为数据.xlsx'
    // link.click()
    const response =  await exportBehaviorData(params)
    console.log(response);
    

     // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    
    // 从响应头获取文件名，或使用默认文件名
    const contentDisposition = response.headers['content-disposition']
    let fileName = 'learning_behavior_' + new Date().toISOString().slice(0, 10) + '.xlsx'
    
    if (contentDisposition) {
      const fileNameMatch = contentDisposition.match(/filename="?(.+)"?/i)
      console.log(fileNameMatch);
      
      if (fileNameMatch.length > 1) {
        fileName = fileNameMatch[1]
      }
    }
    link.setAttribute('download', fileName.replace("\"", ""))
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出请求已发送')
  } catch (error) {
    console.error('导出数据失败', error)
    ElMessage.error('导出数据失败')
  }
}

// 获取行为类型标签
const getBehaviorTypeLabel = (type) => {
  const typeMap = {
    'VIDEO_WATCH': '观看视频',
    'RESOURCE_DOWNLOAD': '资料下载',
    'ASSIGNMENT_SUBMIT': '作业提交',
    'FORUM_PARTICIPATE': '论坛参与',
    'QUIZ_COMPLETE': '测验完成'
  }
  return typeMap[type] || type
}

// 获取行为类型标签类型
const getBehaviorTypeTag = (type) => {
  const typeMap = {
    'VIDEO_WATCH': 'success',
    'RESOURCE_DOWNLOAD': 'info',
    'ASSIGNMENT_SUBMIT': 'warning',
    'FORUM_PARTICIPATE': 'primary',
    'QUIZ_COMPLETE': 'danger'
  }
  return typeMap[type] || ''
}

// 获取进度条颜色
const getProgressColor = (rate) => {
  if (rate < 0.3) return '#F56C6C'
  if (rate < 0.7) return '#E6A23C'
  return '#67C23A'
}

// 监听窗口大小变化，重绘图表
window.addEventListener('resize', () => {
  if (behaviorTypeChartInstance) {
    behaviorTypeChartInstance.resize()
  }
  if (activeChartInstance) {
    activeChartInstance.resize()
  }
})

onMounted(() => {
  getCourseOptions()
  getStudentOptions()
  getList()
  getStatistics()
})
</script>

<style scoped>
.container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.main-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.title-area {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.title-icon {
  margin-right: 8px;
  font-size: 20px;
  color: #409EFF;
}

.card-header span {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.filter-area {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 10px;
}

.table-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 20px 0 10px;
}

.table-header .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.stat-cards {
  margin: 20px 0;
}

.stat-card {
  text-align: center;
  transition: all 0.3s;
  border-radius: 8px;
  overflow: hidden;
  height: 100%;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  position: relative;
  padding-right: 30px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 40px;
  opacity: 0.1;
  color: #000;
}

.stat-card-1 .stat-value, .stat-card-1 .stat-icon {
  color: #409EFF;
}

.stat-card-2 .stat-value, .stat-card-2 .stat-icon {
  color: #67C23A;
}

.stat-card-3 .stat-value, .stat-card-3 .stat-icon {
  color: #E6A23C;
}

.stat-card-4 .stat-value, .stat-card-4 .stat-icon {
  color: #F56C6C;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.chart-section {
  margin-bottom: 25px;
}

.chart-card {
  height: 350px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.chart-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.chart-header {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  display: flex;
  align-items: center;
}

.chart-header .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.chart {
  height: 300px;
}

.dialog-content {
  padding: 10px;
}

.detail-section {
  margin-top: 20px;
}

h3 {
  font-size: 16px;
  margin-bottom: 15px;
  font-weight: 500;
  color: #303133;
  display: flex;
  align-items: center;
}

h3 .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.detail-content {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  min-height: 60px;
  border: 1px solid #ebeef5;
}

/* 美化表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafafa;
}

:deep(.el-table tbody tr:hover > td) {
  background-color: #ecf5ff !important;
}

:deep(.el-table--border td:first-child, .el-table--border th:first-child) {
  border-left: none;
}

:deep(.el-table--border td:last-child, .el-table--border th:last-child) {
  border-right: none;
}

/* 美化标签 */
:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 8px;
  margin-right: 5px;
  font-weight: 500;
}

/* 美化分页样式 */
:deep(.el-pagination) {
  margin-top: 25px;
  padding: 15px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: flex-end;
}

/* 美化详情对话框 */
:deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background-color: #f5f7fa;
  padding: 15px 20px;
  margin-right: 0;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: #303133;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 15px 20px;
  border-top: 1px solid #ebeef5;
  background-color: #f5f7fa;
}

:deep(.el-descriptions) {
  margin-bottom: 20px;
}

:deep(.el-descriptions__body) {
  background-color: #fff;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  color: #606266;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .stat-cards .el-col {
    margin-bottom: 15px;
  }
  
  .chart-section .el-col {
    margin-bottom: 20px;
  }
  
  .filter-area {
    flex-direction: column;
    width: 100%;
  }
  
  .filter-area .el-select,
  .filter-area .el-date-picker {
    width: 100% !important;
    margin-right: 0 !important;
    margin-bottom: 10px;
  }
  
  .filter-area .el-button {
    margin-bottom: 5px;
  }
}
</style> 