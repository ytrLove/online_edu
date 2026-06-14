<template>
  <div class="grades-container">
    <div class="page-header">
      <h2>成绩详情</h2>
      <el-button @click="goBack" link>
        <el-icon><Back /></el-icon> 返回统计
      </el-button>
    </div>

    <!-- 成绩概览 -->
    <el-card class="overview-card">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-item">
            <div class="item-label">平均分</div>
            <div class="item-value accent">{{ summaryData.averageScore }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-item">
            <div class="item-label">最高分</div>
            <div class="item-value good">{{ summaryData.highestScore }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-item">
            <div class="item-label">最低分</div>
            <div class="item-value warning">{{ summaryData.lowestScore }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="overview-item">
            <div class="item-label">评价</div>
            <div class="item-value">{{ summaryData.evaluation }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 分数分布统计 -->
    <div class="chart-section">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>成绩趋势</span>
                <el-radio-group v-model="chartTimeRange" size="small">
                  <el-radio-button label="semester">本学期</el-radio-button>
                  <el-radio-button label="year">本学年</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div class="chart-container" ref="trendChartContainer">
              <el-empty v-if="!hasTrendData" description="暂无趋势数据" />
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>分数分布</span>
              </div>
            </template>
            <div class="chart-container" ref="distributionChartContainer">
              <el-empty v-if="!hasDistributionData" description="暂无分布数据" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 成绩列表 -->
    <el-card class="grades-card">
      <template #header>
        <div class="card-header">
          <span>成绩记录</span>
          <div class="header-actions">
            <el-select v-model="filterType" placeholder="类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="作业" value="assignment" />
              <el-option label="考试" value="exam" />
            </el-select>
            <el-select v-model="filterCourse" placeholder="课程" clearable>
              <el-option label="全部" value="" />
              <el-option
                v-for="course in courseOptions"
                :key="course.value"
                :label="course.label"
                :value="course.value"
              />
            </el-select>
            <el-select v-model="sortOrder" placeholder="排序">
              <el-option label="时间 (新→旧)" value="timeDesc" />
              <el-option label="时间 (旧→新)" value="timeAsc" />
              <el-option label="分数 (高→低)" value="scoreDesc" />
              <el-option label="分数 (低→高)" value="scoreAsc" />
            </el-select>
          </div>
        </div>
      </template>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="filteredGradesList.length === 0" class="empty-container">
        <el-empty description="暂无成绩记录" />
      </div>

      <el-table
        v-else
        :data="filteredGradesList"
        style="width: 100%"
        stripe
        border
      >
        <el-table-column label="类型" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.type === 'assignment' ? 'primary' : 'warning'"
              size="small"
            >
              {{ scope.row.type === 'assignment' ? '作业' : '考试' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="courseName" label="课程" min-width="150" show-overflow-tooltip />
        <el-table-column label="得分" width="150" align="center">
          <template #default="scope">
            <span :class="getScoreClass(scope.row.score)">
              {{ scope.row.score }} / {{ scope.row.totalScore }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="得分率" width="120" align="center">
          <template #default="scope">
            <el-progress
              :percentage="Math.round((scope.row.score / scope.row.totalScore) * 100)"
              :color="getProgressColor(scope.row.score, scope.row.totalScore)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button 
              link 
              type="primary" 
              @click="viewDetail(scope.row)"
              :disabled="!scope.row.canView"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container" v-if="totalItems > 0">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalItems"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          @update:current-page="currentPage = $event"
          @update:page-size="pageSize = $event"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts/core'
import { BarChart, LineChart } from 'echarts/charts'
import { 
  TitleComponent, 
  TooltipComponent, 
  LegendComponent, 
  GridComponent,
  MarkLineComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 注册必要的 echarts 组件
echarts.use([
  BarChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  MarkLineComponent,
  CanvasRenderer
])

const router = useRouter()

// 数据状态
const loading = ref(false)
const filterType = ref('')
const filterCourse = ref('')
const sortOrder = ref('timeDesc')
const chartTimeRange = ref('semester')
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const trendChartContainer = ref(null)
const distributionChartContainer = ref(null)

// 课程选项
const courseOptions = [
  { value: 'vue', label: 'Vue.js前端开发' },
  { value: 'react', label: 'React入门与实践' },
  { value: 'node', label: 'Node.js后端开发' },
  { value: 'python', label: 'Python基础编程' }
]

// 成绩摘要数据
const summaryData = reactive({
  averageScore: 85.6,
  highestScore: 98,
  lowestScore: 62,
  evaluation: '优秀'
})

// 模拟成绩数据
const gradesList = ref([
  { 
    id: 1, 
    type: 'exam', 
    title: 'Vue.js期中考试', 
    courseName: 'Vue.js前端开发实战', 
    courseId: 'vue',
    score: 92, 
    totalScore: 100,
    date: '2023-05-12',
    canView: true
  },
  { 
    id: 2, 
    type: 'assignment', 
    title: '第五章作业', 
    courseName: 'Vue.js前端开发实战', 
    courseId: 'vue',
    score: 88, 
    totalScore: 100,
    date: '2023-05-10',
    canView: true 
  },
  { 
    id: 3, 
    type: 'assignment', 
    title: '第四章作业', 
    courseName: 'Vue.js前端开发实战', 
    courseId: 'vue',
    score: 95, 
    totalScore: 100,
    date: '2023-05-05',
    canView: true 
  },
  { 
    id: 4, 
    type: 'exam', 
    title: 'React基础期末考试', 
    courseName: 'React入门与实践', 
    courseId: 'react',
    score: 78, 
    totalScore: 100,
    date: '2023-04-28',
    canView: true 
  },
  { 
    id: 5, 
    type: 'assignment', 
    title: 'React组件作业', 
    courseName: 'React入门与实践', 
    courseId: 'react',
    score: 82, 
    totalScore: 100,
    date: '2023-04-20',
    canView: true 
  },
  { 
    id: 6, 
    type: 'assignment', 
    title: 'Node.js路由作业', 
    courseName: 'Node.js后端开发', 
    courseId: 'node',
    score: 90, 
    totalScore: 100,
    date: '2023-04-15',
    canView: true 
  },
  { 
    id: 7, 
    type: 'exam', 
    title: 'Python期中测验', 
    courseName: 'Python基础编程', 
    courseId: 'python',
    score: 85, 
    totalScore: 100,
    date: '2023-04-10',
    canView: true 
  },
  { 
    id: 8, 
    type: 'assignment', 
    title: 'Python数据结构作业', 
    courseName: 'Python基础编程', 
    courseId: 'python',
    score: 80, 
    totalScore: 100,
    date: '2023-04-05',
    canView: true 
  }
])

// 计算属性
const filteredGradesList = computed(() => {
  let result = [...gradesList.value]
  
  // 过滤类型
  if (filterType.value) {
    result = result.filter(item => item.type === filterType.value)
  }
  
  // 过滤课程
  if (filterCourse.value) {
    result = result.filter(item => item.courseId === filterCourse.value)
  }
  
  // 排序
  switch (sortOrder.value) {
    case 'timeAsc':
      result.sort((a, b) => new Date(a.date) - new Date(b.date))
      break
    case 'timeDesc':
      result.sort((a, b) => new Date(b.date) - new Date(a.date))
      break
    case 'scoreAsc':
      result.sort((a, b) => a.score - b.score)
      break
    case 'scoreDesc':
      result.sort((a, b) => b.score - a.score)
      break
  }
  
  totalItems.value = result.length
  
  // 分页
  const startIndex = (currentPage.value - 1) * pageSize.value
  return result.slice(startIndex, startIndex + pageSize.value)
})

const hasTrendData = computed(() => {
  return gradesList.value.length > 0
})

const hasDistributionData = computed(() => {
  return gradesList.value.length > 0
})

// 方法
const goBack = () => {
  router.push('/statistics')
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const viewDetail = (row) => {
  if (row.type === 'assignment') {
    router.push(`/assignments/detail/${row.id}`)
  } else {
    router.push(`/assignments/exam/${row.id}`)
  }
}

const getScoreClass = (score) => {
  if (score < 60) return 'score-poor'
  if (score < 75) return 'score-fair'
  if (score < 90) return 'score-good'
  return 'score-excellent'
}

const getProgressColor = (score, totalScore) => {
  const percentage = (score / totalScore) * 100
  if (percentage < 60) return '#F56C6C'
  if (percentage < 75) return '#E6A23C'
  if (percentage < 90) return '#409EFF'
  return '#67C23A'
}

// 初始化成绩趋势图表
const initTrendChart = () => {
  if (!trendChartContainer.value) return
  
  const chart = echarts.init(trendChartContainer.value)
  
  // 从成绩列表提取数据
  const sortedGrades = [...gradesList.value].sort((a, b) => new Date(a.date) - new Date(b.date))
  const dates = sortedGrades.map(item => item.date)
  const scores = sortedGrades.map(item => item.score)
  
  // 计算平均分线
  const averageScore = summaryData.averageScore
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c}分'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '分数',
      min: Math.max(0, Math.floor(Math.min(...scores) / 10) * 10 - 10),
      max: 100
    },
    series: [
      {
        name: '成绩',
        type: 'line',
        data: scores,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        itemStyle: {
          color: '#409EFF'
        },
        lineStyle: {
          width: 3
        },
        markLine: {
          data: [
            {
              name: '平均分',
              yAxis: averageScore,
              lineStyle: {
                color: '#E6A23C',
                type: 'dashed'
              },
              label: {
                formatter: '平均分: ' + averageScore,
                position: 'end'
              }
            },
            {
              name: '及格线',
              yAxis: 60,
              lineStyle: {
                color: '#F56C6C',
                type: 'dashed'
              },
              label: {
                formatter: '及格线: 60',
                position: 'start'
              }
            }
          ]
        }
      }
    ]
  }
  
  chart.setOption(option)
  
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化分数分布图表
const initDistributionChart = () => {
  if (!distributionChartContainer.value) return
  
  const chart = echarts.init(distributionChartContainer.value)
  
  // 计算分数段分布
  const scoreCounts = {
    '优秀 (90-100)': 0,
    '良好 (80-89)': 0,
    '中等 (70-79)': 0,
    '及格 (60-69)': 0,
    '不及格 (<60)': 0
  }
  
  gradesList.value.forEach(grade => {
    if (grade.score >= 90) {
      scoreCounts['优秀 (90-100)']++
    } else if (grade.score >= 80) {
      scoreCounts['良好 (80-89)']++
    } else if (grade.score >= 70) {
      scoreCounts['中等 (70-79)']++
    } else if (grade.score >= 60) {
      scoreCounts['及格 (60-69)']++
    } else {
      scoreCounts['不及格 (<60)']++
    }
  })
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: Object.keys(scoreCounts)
    },
    yAxis: {
      type: 'value',
      name: '数量',
      minInterval: 1
    },
    series: [
      {
        name: '成绩分布',
        type: 'bar',
        data: Object.entries(scoreCounts).map(([key, value]) => ({
          value,
          itemStyle: {
            color: key.includes('优秀') ? '#67C23A' 
              : key.includes('良好') ? '#409EFF' 
              : key.includes('中等') ? '#E6A23C' 
              : key.includes('及格') ? '#909399' 
              : '#F56C6C'
          }
        })),
        barWidth: '40%',
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  }
  
  chart.setOption(option)
  
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化数据
const fetchGrades = async () => {
  loading.value = true
  try {
    // 模拟 API 请求
    await new Promise(resolve => setTimeout(resolve, 800))
    // 在实际应用中，这里应该是真实的 API 调用
    // 目前使用上面定义的模拟数据 gradesList
  } catch (error) {
    console.error('获取成绩数据失败', error)
  } finally {
    loading.value = false
  }
}

// 生命周期钩子
onMounted(async () => {
  await fetchGrades()
  
  // 初始化图表
  await nextTick()
  initTrendChart()
  initDistributionChart()
})
</script>

<style scoped>
.grades-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.overview-card {
  margin-bottom: 24px;
}

.overview-item {
  padding: 20px 0;
  text-align: center;
}

.item-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.item-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.item-value.accent {
  color: #409EFF;
}

.item-value.good {
  color: #67C23A;
}

.item-value.warning {
  color: #E6A23C;
}

.chart-section {
  margin-bottom: 24px;
}

.chart-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.grades-card {
  margin-bottom: 24px;
}

.loading-container,
.empty-container {
  padding: 30px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.score-poor {
  color: #F56C6C;
  font-weight: bold;
}

.score-fair {
  color: #E6A23C;
  font-weight: bold;
}

.score-good {
  color: #409EFF;
  font-weight: bold;
}

.score-excellent {
  color: #67C23A;
  font-weight: bold;
}

@media (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  .chart-container {
    height: 250px;
  }
}
</style> 