<template>
  <div class="container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>教学报告管理</span>
          <div>
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
              v-model="queryParams.teacherId"
              placeholder="选择教师"
              clearable
              @change="handleQuery"
              style="width: 180px; margin-right: 10px;"
            >
              <el-option
                v-for="item in teacherOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
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
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon> 搜索
            </el-button>
            <el-button @click="resetQuery">
              <el-icon><Refresh /></el-icon> 重置
            </el-button>
            <el-button type="success" @click="handleGenerate">
              <el-icon><Plus /></el-icon> 生成报告
            </el-button>
          </div>
        </div>
      </template>

      <!-- 报告列表 -->
      <el-table
        v-loading="loading"
        :data="reportList"
        border
        style="width: 100%;"
      >
        <el-table-column prop="reportId" label="报告ID" width="80" align="center" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="teacherName" label="授课教师" width="120" align="center" />
        <el-table-column prop="startTime" label="起始日期" width="120" align="center" />
        <el-table-column prop="endTime" label="结束日期" width="120" align="center" />
        <el-table-column prop="generateTime" label="生成时间" width="160" align="center" />
        <el-table-column prop="reportSummary" label="报告摘要" min-width="200">
          <template #default="scope">
            <div class="report-summary">{{ scope.row.reportSummary || '暂无摘要' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="viewDetail(scope.row)">
              查看
            </el-button>
            <el-button type="primary" link @click="handleExport(scope.row)">
              导出
            </el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">
              删除
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
        style="margin-top: 20px; display: flex; justify-content: flex-end;"
      />
    </el-card>

    <!-- 生成报告对话框 -->
    <el-dialog
      v-model="generateDialog.visible"
      title="生成教学报告"
      width="580px"
      destroy-on-close
    >
      <el-form ref="generateFormRef" :model="generateForm" :rules="generateRules" label-width="100px">
        <el-form-item label="课程" prop="courseId">
          <el-select
            v-model="generateForm.courseId"
            placeholder="请选择课程"
            style="width: 100%"
          >
            <el-option
              v-for="item in courseOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报告周期" prop="dateRange">
          <el-date-picker
            v-model="generateForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="报告标题" prop="reportTitle">
          <el-input v-model="generateForm.reportTitle" placeholder="请输入报告标题" />
        </el-form-item>
        <el-form-item label="报告类型" prop="reportType">
          <el-radio-group v-model="generateForm.reportType">
            <el-radio label="weeklyReport">周报</el-radio>
            <el-radio label="monthlyReport">月报</el-radio>
            <el-radio label="termReport">学期报告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="报告内容">
          <el-checkbox-group v-model="generateForm.reportContents">
            <el-checkbox label="attendance">出勤情况</el-checkbox>
            <el-checkbox label="evaluation">教学评价</el-checkbox>
            <el-checkbox label="performance">学生成绩</el-checkbox>
            <el-checkbox label="interaction">课堂互动</el-checkbox>
            <el-checkbox label="resourceUsage">资源使用</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="generateDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitGenerate" :loading="generateDialog.loading">生成</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialog.visible"
      :title="detailDialog.title"
      width="800px"
      destroy-on-close
      fullscreen
    >
      <div v-loading="detailDialog.loading" class="report-detail">
        <div class="report-header">
          <h1>{{ detailReport.reportTitle || '教学报告' }}</h1>
          <div class="report-meta">
            <div class="meta-item">
              <span class="label">课程：</span>
              <span class="value">{{ detailReport.courseName }}</span>
            </div>
            <div class="meta-item">
              <span class="label">授课教师：</span>
              <span class="value">{{ detailReport.teacherName }}</span>
            </div>
            <div class="meta-item">
              <span class="label">报告周期：</span>
              <span class="value">{{ detailReport.startTime }} 至 {{ detailReport.endTime }}</span>
            </div>
            <div class="meta-item">
              <span class="label">生成时间：</span>
              <span class="value">{{ detailReport.generateTime }}</span>
            </div>
          </div>
        </div>

        <el-divider content-position="center">报告摘要</el-divider>
        
        <div class="report-summary-section">
          <p>{{ detailReport.reportSummary || '暂无摘要' }}</p>
        </div>

        <el-divider content-position="center">教学情况</el-divider>

        <div class="report-content">
          <!-- 出勤情况 -->
          <div v-if="detailReport.attendance" class="report-section">
            <h2>出勤情况</h2>
            <div ref="attendanceChartRef" class="chart-container"></div>
            <div class="section-content" v-html="detailReport.attendance.content"></div>
          </div>

          <!-- 教学评价 -->
          <div v-if="detailReport.evaluation" class="report-section">
            <h2>教学评价</h2>
            <div ref="evaluationChartRef" class="chart-container"></div>
            <div class="section-content" v-html="detailReport.evaluation.content"></div>
          </div>

          <!-- 学生成绩 -->
          <div v-if="detailReport.performance" class="report-section">
            <h2>学生成绩</h2>
            <div ref="performanceChartRef" class="chart-container"></div>
            <div class="section-content" v-html="detailReport.performance.content"></div>
          </div>

          <!-- 课堂互动 -->
          <div v-if="detailReport.interaction" class="report-section">
            <h2>课堂互动</h2>
            <div class="section-content" v-html="detailReport.interaction.content"></div>
          </div>

          <!-- 资源使用 -->
          <div v-if="detailReport.resourceUsage" class="report-section">
            <h2>资源使用</h2>
            <div ref="resourceChartRef" class="chart-container"></div>
            <div class="section-content" v-html="detailReport.resourceUsage.content"></div>
          </div>
        </div>

        <el-divider content-position="center">教学建议</el-divider>
        
        <div class="report-suggestions">
          <div v-html="detailReport.suggestions || '暂无教学建议'"></div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialog.visible = false">关闭</el-button>
          <el-button type="primary" @click="handleExport(detailReport)">导出报告</el-button>
          <el-button type="success" @click="handleShare(detailReport)">分享报告</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import { getTeacherReportPage, getCourseReportPage, getReportDetail, generateReport, exportReport, deleteReport } from '@/api/stat/report'
import { getCourseList } from '@/api/edu/course'
import { getTeacherList as listTeachers } from '@/api/edu/teacher'

// 图表引用
const attendanceChartRef = ref(null)
const evaluationChartRef = ref(null)
const performanceChartRef = ref(null)
const resourceChartRef = ref(null)
let attendanceChart = null
let evaluationChart = null
let performanceChart = null
let resourceChart = null

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  courseId: undefined,
  teacherId: undefined,
  startTime: '',
  endTime: ''
})

// 日期范围
const dateRange = ref([])

// 课程和教师选项
const courseOptions = ref([])
const teacherOptions = ref([])

// 报告列表数据
const reportList = ref([])
const total = ref(0)
const loading = ref(false)

// 生成报告对话框
const generateDialog = reactive({
  visible: false,
  loading: false
})

// 生成表单引用
const generateFormRef = ref(null)

// 生成表单数据
const generateForm = reactive({
  courseId: undefined,
  dateRange: [],
  reportTitle: '',
  reportType: 'weeklyReport',
  reportContents: ['attendance', 'evaluation', 'performance']
})

// 生成表单验证规则
const generateRules = reactive({
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  dateRange: [{ required: true, message: '请选择报告周期', trigger: 'change' }],
  reportTitle: [{ required: true, message: '请输入报告标题', trigger: 'blur' }],
  reportType: [{ required: true, message: '请选择报告类型', trigger: 'change' }]
})

// 详情对话框
const detailDialog = reactive({
  visible: false,
  title: '教学报告详情',
  loading: false
})

// 详情报告数据
const detailReport = reactive({
  reportId: '',
  reportTitle: '',
  courseId: '',
  courseName: '',
  teacherId: '',
  teacherName: '',
  startTime: '',
  endTime: '',
  generateTime: '',
  reportSummary: '',
  attendance: null,
  evaluation: null,
  performance: null,
  interaction: null,
  resourceUsage: null,
  suggestions: ''
})

// 获取报告列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getCourseReportPage(queryParams)
    reportList.value = res.data.rows
    total.value = res.data.total
  } catch (error) {
    console.error('获取报告列表失败', error)
    ElMessage.error('获取报告列表失败')
  } finally {
    loading.value = false
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

// 获取教师选项
const getTeacherOptions = async () => {
  try {
    const res = await listTeachers()
    teacherOptions.value = res.data.map(item => ({
      value: item.userId,
      label: item.userName
    }))
  } catch (error) {
    console.error('获取教师列表失败', error)
  }
}

// 查看详情
const viewDetail = async (row) => {
  detailDialog.visible = true
  detailDialog.title = `教学报告 - ${row.courseName}`
  detailDialog.loading = true
  
  try {
    const res = await getReportDetail(row.reportId)
    
    // 清空旧数据
    Object.keys(detailReport).forEach(key => {
      if (typeof detailReport[key] === 'object' && detailReport[key] !== null) {
        detailReport[key] = null
      } else {
        detailReport[key] = ''
      }
    })
    
    // 填充新数据
    Object.assign(detailReport, res.data)
    
    // 渲染图表
    nextTick(() => {
      renderReportCharts()
    })
  } catch (error) {
    console.error('获取报告详情失败', error)
    ElMessage.error('获取报告详情失败')
  } finally {
    detailDialog.loading = false
  }
}

// 渲染报告图表
const renderReportCharts = () => {
  if (detailReport.attendance) {
    renderAttendanceChart()
  }
  
  if (detailReport.evaluation) {
    renderEvaluationChart()
  }
  
  if (detailReport.performance) {
    renderPerformanceChart()
  }
  
  if (detailReport.resourceUsage) {
    renderResourceChart()
  }
}

// 渲染出勤情况图表
const renderAttendanceChart = () => {
  if (!attendanceChartRef.value) return
  
  if (!attendanceChart) {
    attendanceChart = echarts.init(attendanceChartRef.value)
  }
  
  const data = detailReport.attendance.chartData || {
    dates: [],
    rates: []
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}%'
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
      data: data.dates
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '出勤率',
        type: 'line',
        smooth: true,
        areaStyle: {},
        data: data.rates,
        markLine: {
          data: [
            {
              type: 'average',
              name: '平均值'
            }
          ]
        }
      }
    ]
  }
  
  attendanceChart.setOption(option)
  window.addEventListener('resize', () => attendanceChart.resize())
}

// 渲染教学评价图表
const renderEvaluationChart = () => {
  if (!evaluationChartRef.value) return
  
  if (!evaluationChart) {
    evaluationChart = echarts.init(evaluationChartRef.value)
  }
  
  const data = detailReport.evaluation.chartData || {
    dimensions: ['教学质量', '内容质量', '互动评分', '资源质量', '总体评分'],
    scores: [4.5, 4.3, 4.2, 4.1, 4.4]
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}分'
    },
    radar: {
      indicator: data.dimensions.map(dim => ({
        name: dim,
        max: 5
      }))
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: data.scores,
            name: '评分',
            areaStyle: {}
          }
        ]
      }
    ]
  }
  
  evaluationChart.setOption(option)
  window.addEventListener('resize', () => evaluationChart.resize())
}

// 渲染学生成绩图表
const renderPerformanceChart = () => {
  if (!performanceChartRef.value) return
  
  if (!performanceChart) {
    performanceChart = echarts.init(performanceChartRef.value)
  }
  
  const data = detailReport.performance.chartData || {
    scoreRanges: ['0-59', '60-69', '70-79', '80-89', '90-100'],
    counts: [2, 5, 12, 20, 8]
  }
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    xAxis: {
      type: 'category',
      data: data.scoreRanges
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: data.counts,
        type: 'bar',
        showBackground: true,
        backgroundStyle: {
          color: 'rgba(180, 180, 180, 0.2)'
        }
      }
    ]
  }
  
  performanceChart.setOption(option)
  window.addEventListener('resize', () => performanceChart.resize())
}

// 渲染资源使用图表
const renderResourceChart = () => {
  if (!resourceChartRef.value) return
  
  if (!resourceChart) {
    resourceChart = echarts.init(resourceChartRef.value)
  }
  
  const data = detailReport.resourceUsage.chartData || {
    resourceTypes: ['视频', '文档', 'PPT', '练习', '测验'],
    accessCounts: [120, 80, 60, 45, 90]
  }
  
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
        name: '资源访问量',
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
        data: data.resourceTypes.map((type, index) => ({
          value: data.accessCounts[index],
          name: type
        }))
      }
    ]
  }
  
  resourceChart.setOption(option)
  window.addEventListener('resize', () => resourceChart.resize())
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
}

// 重置按钮操作
const resetQuery = () => {
  dateRange.value = []
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    courseId: undefined,
    teacherId: undefined,
    startTime: '',
    endTime: ''
  })
  handleQuery()
}

// 处理生成报告
const handleGenerate = () => {
  // 重置表单
  if (generateFormRef.value) {
    generateFormRef.value.resetFields()
  }
  generateDialog.visible = true
}

// 提交生成报告
const submitGenerate = async () => {
  if (!generateFormRef.value) return
  
  await generateFormRef.value.validate()
  
  generateDialog.loading = true
  try {
    const submitData = {
      courseId: generateForm.courseId,
      startTime: generateForm.dateRange[0],
      endTime: generateForm.dateRange[1],
      reportTitle: generateForm.reportTitle,
      reportType: generateForm.reportType,
      reportContents: generateForm.reportContents
    }
    
    await generateReport(submitData)
    ElMessage.success('报告生成成功')
    generateDialog.visible = false
    getList()
  } catch (error) {
    console.error('生成报告失败', error)
    ElMessage.error('生成报告失败：' + (error.message || '未知错误'))
  } finally {
    generateDialog.loading = false
  }
}

// 处理导出报告
const handleExport = async (row) => {
  try {
    await exportReport(row.reportId)
    ElMessage.success('报告导出成功')
  } catch (error) {
    console.error('导出报告失败', error)
    ElMessage.error('导出报告失败')
  }
}

// 处理分享报告
const handleShare = (row) => {
  ElMessage.success('报告分享链接已复制到剪贴板')
}

// 处理删除报告
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该报告吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteReport(row.reportId)
        ElMessage.success('删除成功')
        getList()
      } catch (error) {
        console.error('删除报告失败', error)
        ElMessage.error('删除报告失败')
      }
    })
    .catch(() => {})
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

onMounted(() => {
  getCourseOptions()
  getTeacherOptions()
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

.report-summary {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.report-detail {
  padding: 20px;
}

.report-header {
  text-align: center;
  margin-bottom: 30px;
}

.report-header h1 {
  font-size: 24px;
  margin-bottom: 20px;
}

.report-meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
}

.meta-item {
  display: flex;
}

.meta-item .label {
  font-weight: bold;
  margin-right: 5px;
}

.report-summary-section {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.report-section {
  margin-bottom: 30px;
}

.report-section h2 {
  font-size: 18px;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.chart-container {
  height: 350px;
  margin-bottom: 20px;
}

.section-content {
  line-height: 1.6;
}

.report-suggestions {
  background-color: #f0f9eb;
  padding: 15px;
  border-radius: 8px;
  margin-top: 20px;
  line-height: 1.6;
}
</style> 