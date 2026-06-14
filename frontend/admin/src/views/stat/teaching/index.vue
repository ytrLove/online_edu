<template>
  <div class="container">
    <el-row :gutter="20">
      <!-- 教学概览 -->
      <el-col :span="24">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>教学概览</span>
              <div>
                <el-select
                  v-model="semester"
                  placeholder="选择学期"
                  clearable
                  @change="handleSemesterChange"
                >
                  <el-option label="2023-2024-1" value="2023-2024-1" />
                  <el-option label="2023-2024-2" value="2023-2024-2" />
                  <el-option label="2022-2023-2" value="2022-2023-2" />
                  <el-option label="2022-2023-1" value="2022-2023-1" />
                </el-select>
              </div>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-title">开设课程数</div>
                <div class="stat-value">{{ overview.totalCourses }}</div>
                <div class="stat-trend" :class="{ 'up': overview.coursesTrend > 0, 'down': overview.coursesTrend < 0 }">
                  <i :class="overview.coursesTrend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ Math.abs(overview.coursesTrend) }}%
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-title">选课学生数</div>
                <div class="stat-value">{{ overview.totalStudents }}</div>
                <div class="stat-trend" :class="{ 'up': overview.studentsTrend > 0, 'down': overview.studentsTrend < 0 }">
                  <i :class="overview.studentsTrend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ Math.abs(overview.studentsTrend) }}%
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-title">平均出勤率</div>
                <div class="stat-value">{{ overview.averageAttendance }}%</div>
                <div class="stat-trend" :class="{ 'up': overview.attendanceTrend > 0, 'down': overview.attendanceTrend < 0 }">
                  <i :class="overview.attendanceTrend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ Math.abs(overview.attendanceTrend) }}%
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-title">平均成绩</div>
                <div class="stat-value">{{ overview.averageScore }}</div>
                <div class="stat-trend" :class="{ 'up': overview.scoreTrend > 0, 'down': overview.scoreTrend < 0 }">
                  <i :class="overview.scoreTrend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ Math.abs(overview.scoreTrend) }}%
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 课程数据分析 -->
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>学生成绩分布</span>
            </div>
          </template>
          <div ref="scoreDistributionRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 出勤率分析 -->
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>课程出勤率趋势</span>
            </div>
          </template>
          <div ref="attendanceChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 学习进度分析 -->
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>学习资源使用情况</span>
            </div>
          </template>
          <div ref="resourceUsageRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 作业完成率 -->
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>作业完成率</span>
            </div>
          </template>
          <div ref="assignmentCompletionRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 课程列表 -->
      <el-col :span="24">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>课程数据分析</span>
              <div>
                <el-input
                  v-model="searchKeyword"
                  placeholder="搜索课程名称"
                  style="width: 200px"
                  clearable
                  @keyup.enter="handleSearch"
                />
                <el-button type="primary" @click="handleSearch" style="margin-left: 10px">搜索</el-button>
              </div>
            </div>
          </template>
          <el-table
            :data="courseList"
            style="width: 100%"
            border
            :highlight-current-row="true"
          >
            <el-table-column prop="courseName" label="课程名称" min-width="200" />
            <el-table-column prop="courseCode" label="课程代码" width="120" />
            <el-table-column prop="teacherName" label="授课教师" width="120" />
            <el-table-column prop="studentCount" label="选课人数" width="100" align="center" />
            <el-table-column prop="averageAttendance" label="平均出勤率" width="120" align="center">
              <template #default="scope">
                {{ scope.row.averageAttendance }}%
              </template>
            </el-table-column>
            <el-table-column prop="averageScore" label="平均成绩" width="100" align="center" />
            <el-table-column prop="assignmentCompletion" label="作业完成率" width="120" align="center">
              <template #default="scope">
                {{ scope.row.assignmentCompletion }}%
              </template>
            </el-table-column>
            <el-table-column prop="resourceUsage" label="资源使用率" width="120" align="center">
              <template #default="scope">
                {{ scope.row.resourceUsage }}%
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button
                  type="primary"
                  link
                  @click="viewCourseDetail(scope.row)"
                >
                  详情
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
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

const router = useRouter()
const semester = ref('2023-2024-2')
const searchKeyword = ref('')
const scoreDistributionRef = ref(null)
const attendanceChartRef = ref(null)
const resourceUsageRef = ref(null)
const assignmentCompletionRef = ref(null)

let scoreDistributionChart = null
let attendanceChart = null
let resourceUsageChart = null
let assignmentCompletionChart = null

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  semester: '2023-2024-2',
  keyword: ''
})

// 总记录数
const total = ref(0)

// 教学概览数据
const overview = ref({
  totalCourses: 35,
  coursesTrend: 5.2,
  totalStudents: 1250,
  studentsTrend: 8.7,
  averageAttendance: 92.5,
  attendanceTrend: 1.8,
  averageScore: 83.2,
  scoreTrend: 2.5
})

// 课程数据
const courseList = ref([
  {
    courseId: 1,
    courseName: 'Web前端开发技术',
    courseCode: 'CS-001',
    teacherName: '张三',
    studentCount: 62,
    averageAttendance: 93.5,
    averageScore: 85.2,
    assignmentCompletion: 91.8,
    resourceUsage: 87.5
  },
  {
    courseId: 2,
    courseName: 'Java程序设计',
    courseCode: 'CS-002',
    teacherName: '李四',
    studentCount: 48,
    averageAttendance: 90.2,
    averageScore: 82.7,
    assignmentCompletion: 88.3,
    resourceUsage: 79.1
  },
  {
    courseId: 3,
    courseName: '数据库原理与应用',
    courseCode: 'CS-003',
    teacherName: '王五',
    studentCount: 54,
    averageAttendance: 95.1,
    averageScore: 87.3,
    assignmentCompletion: 93.5,
    resourceUsage: 91.2
  },
  {
    courseId: 4,
    courseName: '计算机网络',
    courseCode: 'CS-004',
    teacherName: '赵六',
    studentCount: 42,
    averageAttendance: 89.7,
    averageScore: 80.5,
    assignmentCompletion: 86.2,
    resourceUsage: 75.8
  },
  {
    courseId: 5,
    courseName: '软件工程',
    courseCode: 'CS-005',
    teacherName: '钱七',
    studentCount: 38,
    averageAttendance: 91.3,
    averageScore: 83.9,
    assignmentCompletion: 90.1,
    resourceUsage: 83.7
  }
])

// 处理学期变化
const handleSemesterChange = (val) => {
  queryParams.value.semester = val
  queryParams.value.pageNum = 1
  fetchData()
}

// 搜索处理
const handleSearch = () => {
  queryParams.value.keyword = searchKeyword.value
  queryParams.value.pageNum = 1
  fetchData()
}

// 初始化成绩分布图
const initScoreDistributionChart = () => {
  if (scoreDistributionChart) {
    scoreDistributionChart.dispose()
  }
  
  scoreDistributionChart = echarts.init(scoreDistributionRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: '5%',
      left: 'center'
    },
    series: [
      {
        name: '成绩分布',
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
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 15, name: '优秀(90-100)' },
          { value: 28, name: '良好(80-89)' },
          { value: 42, name: '中等(70-79)' },
          { value: 10, name: '及格(60-69)' },
          { value: 5, name: '不及格(<60)' }
        ]
      }
    ]
  }
  
  scoreDistributionChart.setOption(option)
  window.addEventListener('resize', scoreDistributionChart.resize)
}

// 初始化出勤率趋势图
const initAttendanceChart = () => {
  if (attendanceChart) {
    attendanceChart.dispose()
  }
  
  attendanceChart = echarts.init(attendanceChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['平均出勤率']
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
      data: ['第1周', '第2周', '第3周', '第4周', '第5周', '第6周', '第7周', '第8周', '第9周', '第10周', '第11周', '第12周', '第13周', '第14周', '第15周', '第16周']
    },
    yAxis: {
      type: 'value',
      min: 70,
      max: 100
    },
    series: [
      {
        name: '平均出勤率',
        type: 'line',
        data: [96, 95, 94, 92, 90, 89, 92, 93, 90, 95, 93, 91, 90, 92, 91, 90],
        areaStyle: {}
      }
    ]
  }
  
  attendanceChart.setOption(option)
  window.addEventListener('resize', attendanceChart.resize)
}

// 初始化资源使用情况图
const initResourceUsageChart = () => {
  if (resourceUsageChart) {
    resourceUsageChart.dispose()
  }
  
  resourceUsageChart = echarts.init(resourceUsageRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['访问量', '下载量', '平均使用时长(分钟)']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['课件', '视频', '练习', '讨论', '案例']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '访问量',
        type: 'bar',
        data: [320, 302, 301, 284, 250]
      },
      {
        name: '下载量',
        type: 'bar',
        data: [240, 202, 191, 0, 190]
      },
      {
        name: '平均使用时长(分钟)',
        type: 'bar',
        data: [35, 48, 25, 18, 30]
      }
    ]
  }
  
  resourceUsageChart.setOption(option)
  window.addEventListener('resize', resourceUsageChart.resize)
}

// 初始化作业完成率图
const initAssignmentCompletionChart = () => {
  if (assignmentCompletionChart) {
    assignmentCompletionChart.dispose()
  }
  
  assignmentCompletionChart = echarts.init(assignmentCompletionRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['按时提交率', '完成质量']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['作业1', '作业2', '作业3', '作业4', '作业5', '大作业']
    },
    yAxis: {
      type: 'value',
      min: 60,
      max: 100
    },
    series: [
      {
        name: '按时提交率',
        type: 'line',
        data: [95, 93, 88, 85, 82, 78]
      },
      {
        name: '完成质量',
        type: 'line',
        data: [88, 86, 84, 87, 83, 80]
      }
    ]
  }
  
  assignmentCompletionChart.setOption(option)
  window.addEventListener('resize', assignmentCompletionChart.resize)
}

// 获取数据
const fetchData = () => {
  // TODO: 调用后端接口获取数据
  // 这里使用模拟数据，实际应该调用API
  console.log('获取数据，查询参数:', queryParams.value)
  
  // 模拟数据总数
  total.value = 15
}

// 处理每页数量变化
const handleSizeChange = (size) => {
  queryParams.value.pageSize = size
  fetchData()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  queryParams.value.pageNum = page
  fetchData()
}

// 查看课程详情
const viewCourseDetail = (row) => {
  router.push({
    path: `/course/detail/${row.courseId}`,
    query: { name: row.courseName }
  })
}

onMounted(() => {
  fetchData()
  
  nextTick(() => {
    initScoreDistributionChart()
    initAttendanceChart()
    initResourceUsageChart()
    initAssignmentCompletionChart()
  })
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
.mt-20 {
  margin-top: 20px;
}
.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  background-color: #f9f9f9;
  border-radius: 5px;
}
.stat-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}
.stat-trend {
  display: flex;
  align-items: center;
  font-size: 14px;
}
.stat-trend.up {
  color: #67c23a;
}
.stat-trend.down {
  color: #f56c6c;
}
.chart-container {
  height: 350px;
}
.pagination-container {
  margin-top: 15px;
  text-align: right;
}
</style>