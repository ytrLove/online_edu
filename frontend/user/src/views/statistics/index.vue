<template>
  <div class="statistics-container">
    <div class="page-header">
      <h2>学习统计分析</h2>
    </div>

    <!-- 概览卡片 -->
    <div class="overview-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="card-icon course-icon">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="card-data">
              <div class="data-value">{{ statisticsData.coursesCount }}</div>
              <div class="data-title">已学课程</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="card-icon assignment-icon">
              <el-icon><List /></el-icon>
            </div>
            <div class="card-data">
              <div class="data-value">{{ statisticsData.assignmentsCount }}</div>
              <div class="data-title">已交作业</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="card-icon exam-icon">
              <el-icon><Finished /></el-icon>
            </div>
            <div class="card-data">
              <div class="data-value">{{ statisticsData.examsCount }}</div>
              <div class="data-title">已完成考试</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="card-icon rank-icon">
              <el-icon><Top /></el-icon>
            </div>
            <div class="card-data">
              <div class="data-value">{{ statisticsData.rank || 'N/A' }}</div>
              <div class="data-title">班级排名</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近成绩 -->
    <el-card class="content-card">
      <template #header>
        <div class="card-header">
          <span>最近成绩</span>
          <el-button link @click="showAllGrades">查看全部</el-button>
        </div>
      </template>
      
      <div class="grades-overview">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <div class="average-score-section">
              <div class="section-title">平均分</div>
              <div class="average-score-circle">
                <el-progress 
                  type="dashboard" 
                  :percentage="averageScorePercentage" 
                  :color="getScoreColor"
                  :stroke-width="10"
                >
                  <template #default>
                    <div class="progress-content">
                      <span class="progress-value">{{ averageScore.toFixed(1) }}</span>
                      <span class="progress-label">分</span>
                    </div>
                  </template>
                </el-progress>
              </div>
              <div class="score-trend">
                <span class="trend-label">较上月:</span>
                <span :class="statisticsData.scoreChange > 0 ? 'trend-up' : statisticsData.scoreChange < 0 ? 'trend-down' : 'trend-equal'">
                  <el-icon v-if="statisticsData.scoreChange > 0"><ArrowUp /></el-icon>
                  <el-icon v-else-if="statisticsData.scoreChange < 0"><ArrowDown /></el-icon>
                  <el-icon v-else><Remove /></el-icon>
                  {{ Math.abs(statisticsData.scoreChange).toFixed(1) }}分
                </span>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12">
            <div class="recent-grades">
              <div class="section-title">最近成绩记录</div>
              <el-empty v-if="!statisticsData.recentGrades.length" description="暂无成绩记录" />
              <el-table 
                v-else
                :data="statisticsData.recentGrades" 
                style="width: 100%"
                size="small"
                :show-header="false"
              >
                <el-table-column width="36">
                  <template #default="scope">
                    <el-icon :size="20" :color="getIconColor(scope.row.type)">
                      <Document v-if="scope.row.type === 'assignment'" />
                      <Reading v-else-if="scope.row.type === 'exam'" />
                      <Reading v-else />
                    </el-icon>
                  </template>
                </el-table-column>
                <el-table-column prop="title" label="名称" show-overflow-tooltip />
                <el-table-column width="80">
                  <template #default="scope">
                    <span :style="{ color: getScoreTextColor(scope.row.score) }">
                      {{ scope.row.score }}分
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 学习进度与课程分布 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="content-card chart-card">
          <template #header>
            <div class="card-header">
              <span>学习进度</span>
            </div>
          </template>
          <div class="progress-chart-container">
            <div class="chart-filters">
              <el-select v-model="selectedCourse" placeholder="选择课程" clearable>
                <el-option
                  v-for="course in courseOptions"
                  :key="course.value"
                  :label="course.label"
                  :value="course.value"
                />
              </el-select>
            </div>
            <div class="progress-indicators">
              <div class="progress-item">
                <div class="progress-label">课程完成率</div>
                <el-progress :percentage="progressData.courseCompletion" :format="percentageFormatter" />
              </div>
              <div class="progress-item">
                <div class="progress-label">作业完成率</div>
                <el-progress :percentage="progressData.assignmentCompletion" :format="percentageFormatter" />
              </div>
              <div class="progress-item">
                <div class="progress-label">考试通过率</div>
                <el-progress :percentage="progressData.examPassRate" :format="percentageFormatter" />
              </div>
              <div class="progress-item">
                <div class="progress-label">讨论参与率</div>
                <el-progress :percentage="progressData.discussionParticipation" :format="percentageFormatter" />
              </div>
            </div>
            <div class="chart-summary">
              <div class="summary-label">总体完成进度</div>
              <div class="summary-value">{{ progressData.overallCompletion }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="content-card chart-card">
          <template #header>
            <div class="card-header">
              <span>学科分布</span>
            </div>
          </template>
          <div class="distribution-chart-container">
            <div class="chart-placeholder" ref="subjectDistributionChart">
              <el-empty v-if="!hasDistributionData" description="暂无数据" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学习活跃度 -->
    <el-card class="content-card">
      <template #header>
        <div class="card-header">
          <span>学习活跃度</span>
          <el-radio-group v-model="activityTimeRange" size="small">
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
            <el-radio-button label="year">全年</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div class="activity-chart-container">
        <div class="chart-placeholder" ref="activityChart">
          <el-empty v-if="!hasActivityData" description="暂无活跃度数据" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Reading, List, Finished, Top, ArrowUp, ArrowDown, Remove, Document } from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { BarChart, PieChart, LineChart } from 'echarts/charts'
import { 
  TitleComponent, 
  TooltipComponent, 
  LegendComponent, 
  GridComponent 
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 注册必要的 echarts 组件
echarts.use([
  BarChart,
  PieChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  CanvasRenderer
])

const router = useRouter()

// 统计数据
const statisticsData = reactive({
  coursesCount: 5,
  assignmentsCount: 23,
  examsCount: 4,
  rank: 12,
  scoreChange: 5.2,
  recentGrades: [
    { id: 1, title: 'Vue.js期中考试', score: 92, type: 'exam', date: '2023-05-12' },
    { id: 2, title: '第五章作业', score: 88, type: 'assignment', date: '2023-05-10' },
    { id: 3, title: '第四章作业', score: 95, type: 'assignment', date: '2023-05-05' },
    { id: 4, title: 'CSS期末考试', score: 78, type: 'exam', date: '2023-04-28' }
  ]
})

// 进度数据
const progressData = reactive({
  courseCompletion: 68,
  assignmentCompletion: 85,
  examPassRate: 100,
  discussionParticipation: 42,
  overallCompletion: 74
})

// 过滤条件
const selectedCourse = ref('')
const activityTimeRange = ref('month')

// 课程选项
const courseOptions = [
  { value: 'all', label: '全部课程' },
  { value: 'vue', label: 'Vue.js前端开发' },
  { value: 'react', label: 'React入门与实践' },
  { value: 'node', label: 'Node.js后端开发' },
  { value: 'python', label: 'Python基础编程' }
]

// 图表DOM引用
const subjectDistributionChart = ref(null)
const activityChart = ref(null)

// 计算属性
const averageScore = computed(() => {
  if (!statisticsData.recentGrades.length) return 0
  const sum = statisticsData.recentGrades.reduce((acc, curr) => acc + curr.score, 0)
  return sum / statisticsData.recentGrades.length
})

const averageScorePercentage = computed(() => {
  return Math.min(Math.round(averageScore.value), 100)
})

const hasDistributionData = computed(() => {
  return true // 用于示例，实际应根据数据判断
})

const hasActivityData = computed(() => {
  return true // 用于示例，实际应根据数据判断
})

// 方法
const showAllGrades = () => {
  router.push('/grades')
}

const percentageFormatter = (percentage) => {
  return `${percentage}%`
}

const getScoreColor = (percentage) => {
  if (percentage < 60) return '#F56C6C'
  if (percentage < 80) return '#E6A23C'
  return '#67C23A'
}

const getScoreTextColor = (score) => {
  if (score < 60) return '#F56C6C'
  if (score < 80) return '#E6A23C'
  return '#67C23A'
}

const getIconColor = (type) => {
  switch (type) {
    case 'assignment': return '#409EFF'
    case 'exam': return '#E6A23C'
    default: return '#909399'
  }
}

// 初始化学科分布图表
const initSubjectDistributionChart = () => {
  if (!subjectDistributionChart.value) return
  
  const chart = echarts.init(subjectDistributionChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: ['前端开发', '后端开发', '数据库', 'UI设计', '计算机基础']
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 3, name: '前端开发' },
          { value: 2, name: '后端开发' },
          { value: 1, name: '数据库' },
          { value: 1, name: 'UI设计' },
          { value: 2, name: '计算机基础' }
        ]
      }
    ]
  }
  
  chart.setOption(option)
  
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化活跃度图表
const initActivityChart = () => {
  if (!activityChart.value) return
  
  const chart = echarts.init(activityChart.value)
  
  // 模拟数据
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const data = [3.2, 4.5, 2.1, 5.8, 3.9, 1.2, 6.4]
  
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
    xAxis: [
      {
        type: 'category',
        data: days,
        axisTick: {
          alignWithLabel: true
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '学习时长(小时)'
      }
    ],
    series: [
      {
        name: '学习时长',
        type: 'bar',
        barWidth: '60%',
        data: data.map(value => ({
          value,
          itemStyle: {
            color: value > 4 ? '#67C23A' : '#409EFF'
          }
        }))
      }
    ]
  }
  
  chart.setOption(option)
  
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 生命周期钩子
onMounted(async () => {
  // 延迟初始化图表，确保DOM已渲染
  await nextTick()
  initSubjectDistributionChart()
  initActivityChart()
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.overview-cards {
  margin-bottom: 24px;
}

.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  padding: 20px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.card-icon .el-icon {
  font-size: 28px;
  color: white;
}

.course-icon {
  background-color: #409EFF;
}

.assignment-icon {
  background-color: #67C23A;
}

.exam-icon {
  background-color: #E6A23C;
}

.rank-icon {
  background-color: #F56C6C;
}

.card-data {
  flex: 1;
}

.data-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.data-title {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.content-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.grades-overview {
  padding: 10px 0;
}

.average-score-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 15px;
  color: #606266;
}

.average-score-circle {
  margin-bottom: 15px;
}

.progress-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.progress-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.progress-label {
  font-size: 12px;
  color: #909399;
}

.score-trend {
  margin-top: 10px;
  font-size: 14px;
}

.trend-label {
  color: #909399;
  margin-right: 5px;
}

.trend-up {
  color: #67C23A;
}

.trend-down {
  color: #F56C6C;
}

.trend-equal {
  color: #909399;
}

.recent-grades {
  padding: 10px;
}

.chart-row {
  margin-bottom: 24px;
}

.chart-card {
  height: 100%;
}

.progress-chart-container {
  padding: 15px;
}

.chart-filters {
  margin-bottom: 20px;
}

.progress-indicators {
  margin-bottom: 20px;
}

.progress-item {
  margin-bottom: 15px;
}

.progress-label {
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.chart-summary {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}

.summary-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.distribution-chart-container {
  padding: 15px;
  height: 300px;
}

.activity-chart-container {
  padding: 15px;
  height: 350px;
}

.chart-placeholder {
  width: 100%;
  height: 100%;
}

@media (max-width: 768px) {
  .stat-card {
    margin-bottom: 15px;
  }
  
  .distribution-chart-container,
  .activity-chart-container {
    height: 250px;
  }
}
</style> 