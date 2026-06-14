<template>
  <div class="paper-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>试卷管理</span>
          <el-button v-permission="['exam:paper:add']" type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增试卷
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="试卷名称">
          <el-input v-model="queryParams.title" placeholder="请输入试卷名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="queryParams.courseId" placeholder="请选择课程" clearable>
            <el-option v-for="item in courseOptions" :key="item.courseId" :label="item.courseName" :value="item.courseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="试卷状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="paperList" border style="width: 100%">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="title" label="试卷名称" width="200" show-overflow-tooltip />
        <el-table-column prop="courseName" label="所属课程" width="150" show-overflow-tooltip />
        <el-table-column prop="totalPoints" label="总分" width="80" align="center" />
        <el-table-column prop="duration" label="答题时长" width="100" align="center">
          <template #default="scope">
            {{ scope.row.duration }} 分钟
          </template>
        </el-table-column>
        <el-table-column label="题目数量" width="100" align="center">
          <template #default="scope">
            <template v-if="scope.row.questionCount">
              {{ scope.row.questionCount }}
            </template>
            <template v-else>
              <el-button type="small" link size="small" @click="fetchQuestionCount(scope.row)">
                <el-icon><View /></el-icon>查看
              </el-button>
            </template>
          </template>
        </el-table-column>
        <el-table-column label="考试时间" width="200" align="center">
          <template #default="scope">
            {{ scope.row.startTime }} 至 {{ scope.row.endTime }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="330" fixed="right" align="center">
          <template #default="scope">
            <el-button v-permission="['exam:paper:query']" type="small" link @click="handleView(scope.row)">
              <el-icon><View /></el-icon>预览
            </el-button>
            <el-button v-permission="['exam:paper:edit']" type="warning" link @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <!-- <el-button v-permission="['exam:paper:edit']" type="info" link @click="handleCopy(scope.row)">
              <el-icon><DocumentCopy /></el-icon>复制
            </el-button> -->
            <el-button
              v-permission="['exam:paper:edit']"
              v-if="scope.row.status === 0"
              type="success"
              link
              @click="handlePublish(scope.row)"
            >
              <el-icon><Upload /></el-icon>发布
            </el-button>
            <el-button
              v-permission="['exam:paper:edit']"
              v-if="scope.row.status === 1"
              type="info"
              link
              @click="handleUnPublish(scope.row)"
            >
              <el-icon><CircleClose /></el-icon>下架
            </el-button>
            <!-- <el-button
              v-if="scope.row.status === 1"
              type="small"
              link
              @click="handleStats(scope.row)"
            >
              <el-icon><DataAnalysis /></el-icon>统计
            </el-button> -->
            <el-button
              v-permission="['exam:paper:remove']"
              type="danger"
              link
              @click="handleDelete(scope.row)"
            >
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页区域 -->
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
      
      <!-- 预览试卷对话框 -->
      <el-dialog v-model="previewDialog.visible" :title="previewDialog.title" width="800px" fullscreen destroy-on-close append-to-body>
        <div class="paper-preview" v-loading="previewLoading">
          <div class="paper-header">
            <h1>{{ previewPaper.title }}</h1>
            <div class="paper-info">
              <span>总分：{{ previewPaper.totalPoints }}分</span>
              <span>时长：{{ previewPaper.duration }}分钟</span>
              <span>考试时间：{{ previewPaper.startTime }} 至 {{ previewPaper.endTime }}</span>
            </div>
            <div class="paper-info">
              <span>所属课程：{{ previewPaper.courseName }}</span>
              <span>出题教师：{{ previewPaper.teacherName || '系统管理员' }}</span>
            </div>
            <div class="paper-description" v-if="previewPaper.description">{{ previewPaper.description }}</div>
          </div>
          
          <!-- 试卷题目区域 -->
          <div class="paper-questions" v-if="previewPaper.questions && previewPaper.questions.length > 0">
            <!-- 题目按题型分组 -->
            <div v-for="(group, groupIndex) in groupedQuestions" :key="groupIndex" class="question-section">
              <div class="section-header">
                <h2>{{ romanNumerals[groupIndex] }}、{{ group.typeName }}</h2>
                <div class="section-info">（共{{ group.questions.length }}题，{{ calculateGroupTotalPoints(group.questions) }}分）</div>
              </div>
              
              <!-- 题目列表 -->
              <div v-for="(question, questionIndex) in group.questions" :key="questionIndex" class="question-item">
                <div class="question-header">
                  <span class="question-number">{{ questionIndex + 1 }}.</span>
                  <span class="question-title" v-html="question.content || question.questionName || `[题目${question.questionId}]`"></span>
                  <span class="question-score">{{ question.points }}分</span>
                </div>
                
                <!-- 选择题选项 -->
                <div v-if="['单选题', '多选题'].includes(question.typeName) && question.options" class="question-options">
                  <div v-for="option in parseOptions(question.options)" :key="option.key" class="option-item">
                    <div class="option-label">{{ option.key }}.</div>
                    <div class="option-content">{{ option.value }}</div>
                  </div>
                </div>
                
                <!-- 判断题选项 -->
                <div v-if="question.typeName === '判断题'" class="question-options">
                  <div class="option-item">
                    <div class="option-label">A.</div>
                    <div class="option-content">{{ question.answer === 'true' || question.answer === 'A' ? '✓ ' : '' }}正确</div>
                  </div>
                  <div class="option-item">
                    <div class="option-label">B.</div>
                    <div class="option-content">{{ question.answer === 'false' || question.answer === 'B' ? '✓ ' : '' }}错误</div>
                  </div>
                </div>
                
                <!-- 填空题 -->
                <div v-if="question.typeName === '填空题'" class="question-blank">
                  <div class="blank-line">(请在此处填写答案)</div>
                </div>
                
                <!-- 简答题和编程题 -->
                <div v-if="['简答题', '编程题'].includes(question.typeName)" class="question-answer-area">
                  <div class="answer-area-placeholder">(答题区域)</div>
                </div>
                
                <!-- 答案和解析(如果有) -->
                <div v-if="question.answer" class="question-answer">
                  <div class="answer-label">参考答案：</div>
                  <div class="answer-content" v-html="formatAnswer(question)"></div>
                </div>
                
                <div v-if="question.analysis" class="question-analysis">
                  <div class="analysis-label">解析：</div>
                  <div class="analysis-content" v-html="question.analysis"></div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 无题目提示 -->
          <div v-else class="no-questions">
            <el-empty description="当前试卷暂无题目" />
          </div>
        </div>
      </el-dialog>
      
      <!-- 考试统计对话框 -->
      <el-dialog v-model="statsDialog.visible" :title="statsDialog.title" width="600px" destroy-on-close append-to-body>
        <div class="stats-content" v-loading="statsLoading">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="总学生数">{{ examStats.totalStudents }}人</el-descriptions-item>
            <el-descriptions-item label="已提交">{{ examStats.submitted }}人</el-descriptions-item>
            <el-descriptions-item label="已批改">{{ examStats.graded }}人</el-descriptions-item>
            <el-descriptions-item label="平均分">{{ examStats.avgScore }}分</el-descriptions-item>
            <el-descriptions-item label="最高分">{{ examStats.maxScore }}分</el-descriptions-item>
            <el-descriptions-item label="最低分">{{ examStats.minScore }}分</el-descriptions-item>
            <el-descriptions-item label="通过率">{{ (examStats.passRate * 100).toFixed(2) }}%</el-descriptions-item>
          </el-descriptions>
          
          <div class="stats-charts" style="margin-top: 20px; height: 300px;">
            <!-- 这里可以添加图表组件，例如使用 ECharts -->
          </div>
        </div>
      </el-dialog>
      
      <!-- 新增/编辑试卷对话框 -->
      <el-dialog 
        v-model="paperDialog.visible" 
        :title="paperDialog.title" 
        width="850px" 
        destroy-on-close
        append-to-body
      >
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-form 
              ref="paperFormRef" 
              :model="paperForm" 
              :rules="paperRules" 
              label-width="120px"
              v-loading="paperDialog.loading"
            >
              <el-form-item label="试卷名称" prop="paperName">
                <el-input v-model="paperForm.paperName" placeholder="请输入试卷名称" />
              </el-form-item>
              
              <el-form-item label="所属课程" prop="courseId">
                <el-select v-model="paperForm.courseId" placeholder="请选择课程" style="width: 100%">
                  <el-option v-for="item in courseOptions" :key="item.courseId" :label="item.courseName" :value="item.courseId" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="答题时长" prop="duration">
                <el-input-number v-model="paperForm.duration" :min="1" :max="360" :step="5" controls-position="right">
                  <template #append>分钟</template>
                </el-input-number>
              </el-form-item>
              
              <el-form-item label="开始时间" prop="startTime">
                <el-date-picker
                  v-model="paperForm.startTime"
                  type="datetime"
                  placeholder="选择开始时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DDTHH:mm:ss"
                  style="width: 100%"
                />
              </el-form-item>
              
              <el-form-item label="结束时间" prop="endTime">
                <el-date-picker
                  v-model="paperForm.endTime"
                  type="datetime"
                  placeholder="选择结束时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DDTHH:mm:ss"
                  style="width: 100%"
                />
              </el-form-item>
              
              <el-form-item label="试卷说明" prop="description">
                <el-input v-model="paperForm.description" type="textarea" :rows="3" placeholder="请输入试卷说明" />
              </el-form-item>
            </el-form>
          </el-tab-pane>
          
          <el-tab-pane label="添加试题" name="questions" :disabled="!paperForm.paperId">
            <div class="question-select-container" v-loading="questionsLoading">
              <div class="question-select-header">
                <el-alert
                  v-if="!paperForm.paperId"
                  title="请先保存试卷基本信息才能添加试题"
                  type="warning"
                  :closable="false"
                />
                <div v-else class="question-select-tools">
                  <el-form :inline="true">
                    <el-form-item label="题型">
                      <el-select v-model="quickAddForm.typeId" placeholder="选择题型" clearable>
                        <el-option
                          v-for="item in typeOptions"
                          :key="item.typeId"
                          :label="item.typeName"
                          :value="item.typeId"
                        />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="内容">
                      <el-input v-model="quickAddForm.content" placeholder="搜索题目内容" clearable />
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="searchAvailableQuestions">
                        <el-icon><Search /></el-icon>搜索
                      </el-button>
                      <el-button @click="resetQuickAddForm">
                        <el-icon><Refresh /></el-icon>重置
                      </el-button>
                    </el-form-item>
                  </el-form>
                </div>
              </div>
              
              <div v-if="paperForm.paperId" class="question-select-body">
                <el-table
                  :data="availableQuestions"
                  border
                  style="width: 100%"
                  max-height="300"
                  @selection-change="handleQuickSelectChange"
                >
                  <el-table-column type="selection" width="55" />
                  <el-table-column prop="questionContent" label="题目内容" show-overflow-tooltip />
                  <el-table-column prop="typeName" label="题型" width="100" align="center" />
                  <el-table-column label="难度" width="80" align="center">
                    <template #default="scope">
                      <el-tag :type="getDifficultyTag(scope.row.difficulty)">
                            {{ getDifficultyLabel(scope.row.difficulty) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="分值" width="100" align="center">
                    <template #default="scope">
                      <el-input-number 
                        v-model="scope.row.score" 
                        :min="0" 
                        :max="100" 
                        :precision="1" 
                        size="small" 
                        controls-position="right"
                      />
                    </template>
                  </el-table-column>
                </el-table>
                
                <div class="question-select-pagination">
                  <el-pagination
                    :current-page="quickAddForm.pageNum"
                    :page-size="quickAddForm.pageSize"
                    :page-sizes="[5, 10, 20, 50]"
                    :total="availableQuestionsTotal"
                    layout="total, sizes, prev, pager, next"
                    @size-change="handleQuickAddSizeChange"
                    @current-change="handleQuickAddCurrentChange"
                  />
                </div>
                
                <div class="question-select-actions">
                  <el-button 
                    type="primary" 
                    @click="addSelectedQuestions" 
                    :disabled="selectedAvailableQuestions.length === 0"
                  >
                    <el-icon><Plus /></el-icon>添加选中试题
                  </el-button>
                </div>
                
                <div class="added-questions-section">
                  <div class="section-title">已添加的试题</div>
                  <el-empty v-if="!paperQuestions || paperQuestions.length === 0" description="暂无试题" />
                  <el-table v-else :data="paperQuestions" border style="width: 100%" max-height="200">
                    <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
                    <el-table-column prop="typeName" label="题型" width="100" align="center" />
                    <el-table-column prop="questionOrder" label="顺序" width="80" align="center" />
                    <el-table-column prop="points" label="分值" width="80" align="center" />
                    <el-table-column label="操作" width="120" align="center">
                      <template #default="scope">
                        <el-button type="primary" link @click="editAddedQuestion(scope.row)">
                          <el-icon><Edit /></el-icon>编辑
                        </el-button>
                        <el-button type="danger" link @click="removeAddedQuestion(scope.row)">
                          <el-icon><Delete /></el-icon>删除
                        </el-button>

                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="cancelPaperDialog">
              <el-icon><Close /></el-icon>取消
            </el-button>
            <el-button type="primary" @click="submitPaperForm" :loading="paperDialog.loading">
              <el-icon><Check /></el-icon>{{paperForm.paperId ? '保存' : '创建试卷'}}
            </el-button>
            <!-- <el-button v-if="paperForm.paperId" type="success" @click="goToFullEditor">
              <el-icon><Edit /></el-icon>进入完整编辑
            </el-button> -->
            <el-button type="primary" @click="handleConfirm" :loading="paperDialog.loading">
              <el-icon><Check /></el-icon>确定
            </el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Search, Refresh, View, Edit, Upload, Download, Delete, CircleClose, DocumentCopy, Plus, DataAnalysis, Close, Check } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamPaperList, getExamPaperInfo, addExamPaper, updateExamPaper, deleteExamPaper, publishExamPaper, unpublishExamPaper } from '@/api/exam/paper'
import { getCourseList } from '@/api/edu/course'
import { getExamStats } from '@/api/exam/record'
import { useRouter } from 'vue-router'
import { getQuestionList } from '@/api/exam/question'
import { getAllQuestionTypes } from '@/api/exam/questionType'
import { addExamPaperQuestions, deleteExamPaperQuestion, getExamPaperQuestions } from '@/api/exam/paperQuestion'

const router = useRouter()

// 加载状态
const loading = ref(false)
const previewLoading = ref(false)
const statsLoading = ref(false)
// 试卷列表
const paperList = ref([])
// 总记录数
const total = ref(0)
// 课程选项
const courseOptions = ref([])
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  courseId: undefined,
  status: undefined
})

// 罗马数字映射
const romanNumerals = ['I', 'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'IX', 'X']

// 预览对话框控制
const previewDialog = reactive({
  visible: false,
  title: '试卷预览'
})

// 预览的试卷
const previewPaper = reactive({
  examId: undefined,
  title: '',
  description: '',
  totalPoints: 0,
  duration: 0,
  startTime: '',
  endTime: '',
  sections: []
})

// 统计对话框控制
const statsDialog = reactive({
  visible: false,
  title: '考试统计'
})

// 考试统计数据
const examStats = reactive({
  totalStudents: 0,
  submitted: 0,
  graded: 0,
  avgScore: 0,
  maxScore: 0,
  minScore: 0,
  passRate: 0
})

// 试卷表单引用
const paperFormRef = ref(null)

// 试卷对话框控制
const paperDialog = reactive({
  visible: false,
  title: '新增试卷',
  loading: false
})

// 试卷表单数据
const paperForm = reactive({
  paperId: undefined,
  paperName: '',
  courseId: undefined,
  duration: 60,
  startTime: '',
  endTime: '',
  description: ''
})

// 试卷表单校验规则
const paperRules = {
  paperName: [
    { required: true, message: '请输入试卷名称', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择所属课程', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请设置答题时长', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

// 标签页控制
const activeTab = ref('basic')

// 题库相关
const typeOptions = ref([])
const availableQuestions = ref([])
const availableQuestionsTotal = ref(0)
const selectedAvailableQuestions = ref([])
const paperQuestions = ref([])
const questionsLoading = ref(false)

// 快速添加题目表单
const quickAddForm = reactive({
  typeId: undefined,
  content: '',
  pageNum: 1,
  pageSize: 10
})

/**
 * 获取课程选项
 */
const getCourses = async () => {
  try {
    const response = await getCourseList()
    courseOptions.value = response.data.records
  } catch (error) {
    console.error('获取课程选项失败', error)
  }
}

/**
 * 查询试卷列表
 */
const getList = async () => {
  loading.value = true
  try {
    const response = await getExamPaperList(queryParams)
    paperList.value = response.data.rows
    total.value = response.data.total
  } catch (error) {
    console.error('获取试卷列表失败', error)
    ElMessage.error('获取试卷列表失败')
  } finally {
    loading.value = false
  }
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
  queryParams.title = ''
  queryParams.courseId = undefined
  queryParams.status = undefined
  handleQuery()
}

/**
 * 打开新增试卷对话框
 */
const openAddPaperDialog = () => {
  paperDialog.title = '新增试卷'
  paperDialog.visible = true
  // 重置表单
  resetPaperForm()
}

/**
 * 重置试卷表单
 */
const resetPaperForm = () => {
  if (paperFormRef.value) {
    paperFormRef.value.resetFields()
  }
  // 重置表单数据
  paperForm.paperId = undefined
  paperForm.paperName = ''
  paperForm.courseId = undefined
  paperForm.duration = 60
  paperForm.startTime = ''
  paperForm.endTime = ''
  paperForm.description = ''
}

/**
 * 取消试卷对话框
 */
const cancelPaperDialog = () => {
  paperDialog.visible = false
  resetPaperForm()
}

/**
 * 获取题目类型
 */
const loadQuestionTypes = async () => {
  try {
    const res = await getAllQuestionTypes()
    typeOptions.value = res.data
  } catch (error) {
    console.error('获取题目类型失败', error)
  }
}

/**
 * 重置快速添加表单
 */
const resetQuickAddForm = () => {
  quickAddForm.typeId = undefined
  quickAddForm.content = ''
  quickAddForm.pageNum = 1
  searchAvailableQuestions()
}

/**
 * 获取难度标签类型
 */
const getDifficultyTag = (difficulty) => {
  if (difficulty === 1) return 'success'
  if (difficulty === 2) return 'warning'
  if (difficulty === 3) return 'danger'
  return 'info'
}

/**
 * 获取难度标签文本
 */
const getDifficultyLabel = (difficulty) => {
  if (difficulty === 'easy') return '简单'
  if (difficulty === 'medium') return '中等'
  if (difficulty === 'hard') return '困难'
  return '未知'
}

/**
 * 搜索可用题目
 */
const searchAvailableQuestions = async () => {
  if (!paperForm.paperId) return
  
  questionsLoading.value = true
  try {
    const queryParams = {
      pageNum: quickAddForm.pageNum,
      pageSize: quickAddForm.pageSize,
      questionContent: quickAddForm.content,
      questionTypeId: quickAddForm.typeId
    }
    
    const res = await getQuestionList(queryParams)
    availableQuestions.value = res.data.rows.map(item => ({
      ...item,
      score: item.defaultScore || 5 // 设置默认分值
    }))
    availableQuestionsTotal.value = res.data.total
  } catch (error) {
    console.error('获取题目列表失败', error)
    ElMessage.error('获取题目列表失败')
  } finally {
    questionsLoading.value = false
  }
}

/**
 * 处理快速添加分页大小变化
 */
const handleQuickAddSizeChange = (size) => {
  quickAddForm.pageSize = size
  searchAvailableQuestions()
}

/**
 * 处理快速添加分页页码变化
 */
const handleQuickAddCurrentChange = (page) => {
  quickAddForm.pageNum = page
  searchAvailableQuestions()
}

/**
 * 处理选中题目变化
 */
const handleQuickSelectChange = (selection) => {
  selectedAvailableQuestions.value = selection
}

/**
 * 添加选中的题目
 */
const addSelectedQuestions = async () => {
  if (selectedAvailableQuestions.value.length === 0) {
    ElMessage.warning('请至少选择一道题目')
    return
  }
  
  questionsLoading.value = true
  try {
    const questions = selectedAvailableQuestions.value.map(q => ({
      examId: paperForm.paperId,
      questionId: q.questionId,
      points: q.score
    }))
    
    await addExamPaperQuestions(questions)
    ElMessage.success('添加题目成功')
    
    // 重新加载已添加的题目
    loadPaperQuestions()
  } catch (error) {
    console.error('添加题目失败', error)
    ElMessage.error('添加题目失败')
  } finally {
    questionsLoading.value = false
  }
}

/**
 * 移除已添加的题目
 */
const removeAddedQuestion = async (question) => {
  try {
    await deleteExamPaperQuestion(paperForm.paperId, question.questionId)
    ElMessage.success('移除题目成功')
    
    // 重新加载已添加的题目
    loadPaperQuestions()
  } catch (error) {
    console.error('移除题目失败', error)
    ElMessage.error('移除题目失败')
  }
}

/**
 * 加载试卷已添加的题目
 */
const loadPaperQuestions = async () => {
  if (!paperForm.paperId) return
  
  questionsLoading.value = true
  try {
    const res = await getExamPaperQuestions(paperForm.paperId)
    paperQuestions.value = res.data || []
  } catch (error) {
    console.error('获取试卷题目失败', error)
    ElMessage.error('获取试卷题目失败')
  } finally {
    questionsLoading.value = false
  }
}

/**
 * 前往完整编辑页面
 */
const goToFullEditor = () => {
  router.push(`/exam/paper/edit/${paperForm.paperId}`)
}

/**
 * 打开编辑试卷对话框
 */
const openEditPaperDialog = async (row) => {
  paperDialog.title = '编辑试卷'
  paperDialog.loading = true
  activeTab.value = 'basic'
  
  try {
    // 获取试卷详情
    const response = await getExamPaperInfo(row.examId)
    const paperData = response.data
    
    // 填充表单数据
    paperForm.paperId = paperData.examId
    paperForm.paperName = paperData.title
    paperForm.courseId = paperData.courseId
    paperForm.duration = paperData.duration
    paperForm.description = paperData.description
    paperForm.startTime = paperData.startTime
    paperForm.endTime = paperData.endTime
    
    // 加载试题数据
    await loadPaperQuestions()
    
    paperDialog.visible = true
  } catch (error) {
    console.error('获取试卷详情失败', error)
    ElMessage.error('获取试卷详情失败')
  } finally {
    paperDialog.loading = false
  }
}

/**
 * 处理添加操作
 */
const handleAdd = () => {
  // 原来是跳转到编辑页面
  // router.push('/exam/paper/edit')
  // 现在改为打开对话框
  openAddPaperDialog()
}

/**
 * 按题型对试卷题目进行分组
 */
const groupedQuestions = computed(() => {
  if (!previewPaper.questions || previewPaper.questions.length === 0) {
    return [];
  }
  
  // 对题目按类型分组
  const groupMap = {};
  
  // 按照questionOrder排序
  const sortedQuestions = [...previewPaper.questions].sort((a, b) => a.questionOrder - b.questionOrder);
  
  sortedQuestions.forEach(question => {
    if (!groupMap[question.typeName]) {
      groupMap[question.typeName] = {
        typeName: question.typeName,
        questions: []
      };
    }
    groupMap[question.typeName].questions.push(question);
  });
  
  // 转换为数组
  return Object.values(groupMap);
});

/**
 * 计算题目组的总分
 */
const calculateGroupTotalPoints = (questions) => {
  return questions.reduce((sum, q) => sum + Number(q.points || 0), 0);
};

/**
 * 解析选项字符串为标准格式
 */
const parseOptions = (optionsStr) => {
  if (!optionsStr) return [];
  
  try {
    const options = JSON.parse(optionsStr);
    
    // 处理新格式的选项 {"A":"元组","B":"字段",...}
    if (typeof options === 'object' && !Array.isArray(options)) {
      return Object.entries(options).map(([key, value]) => ({
        key,
        value
      }));
    }
    
    // 处理旧格式的选项 [{"key":"A","value":"数组"},...]
    if (Array.isArray(options)) {
      return options;
    }
    
    return [];
  } catch (e) {
    console.error('解析选项字符串失败:', e);
    return [];
  }
};

/**
 * 处理预览操作并获取题目数量
 */
const fetchQuestionCount = async (row) => {
  previewLoading.value = true;
  
  try {
    const response = await getExamPaperInfo(row.examId);
    // 更新题目数量
    if (response.data && response.data.questions) {
      // 为当前行添加题目数量字段
      row.questionCount = response.data.questions.length;
    }
  } catch (error) {
    console.error('获取试卷题目数量失败', error);
    ElMessage.error('获取试卷题目数量失败');
  } finally {
    previewLoading.value = false;
  }
}

/**
 * 处理确定操作
 */
const handleConfirm = () => {
  // 关闭对话框
  paperDialog.visible = false
  // 刷新列表
  getList()
}

/**
 * 处理预览操作
 */
const handleView = async (row) => {
  previewDialog.visible = true;
  previewDialog.title = `试卷预览 - ${row.title}`;
  previewLoading.value = true;
  
  try {
    const response = await getExamPaperInfo(row.examId);
    // 清空之前的数据
    Object.keys(previewPaper).forEach(key => {
      if (Array.isArray(previewPaper[key])) {
        previewPaper[key] = [];
      } else if (typeof previewPaper[key] === 'object' && previewPaper[key] !== null) {
        previewPaper[key] = {};
      } else {
        previewPaper[key] = null;
      }
    });
    
    // 填充新数据
    Object.assign(previewPaper, response.data);
    
    // 更新题目数量
    if (response.data && response.data.questions) {
      row.questionCount = response.data.questions.length;
    }
  } catch (error) {
    console.error('获取试卷详情失败', error);
    ElMessage.error('获取试卷详情失败');
  } finally {
    previewLoading.value = false;
  }
}

/**
 * 处理修改操作
 */
const handleEdit = (row) => {
  // 原来是跳转到编辑页面
  // router.push(`/exam/paper/edit/${row.examId}`)
  // 现在改为打开对话框
  openEditPaperDialog(row)
}

/**
 * 处理发布操作
 */
const handlePublish = (row) => {
  ElMessageBox.confirm(`确定要发布试卷"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        // 这里应该调用发布API
        await publishExamPaper(row.examId)
        ElMessage.success('发布成功')
        getList()
      } catch (error) {
        console.error('发布试卷失败', error)
        ElMessage.error('发布试卷失败')
      }
    })
    .catch(() => {})
}

/**
 * 处理取消发布操作
 */
const handleUnPublish = (row) => {
  ElMessageBox.confirm(`确定要取消发布试卷"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        // 这里应该调用取消发布API
        await unpublishExamPaper(row.examId)
        ElMessage.success('取消发布成功')
        getList()
      } catch (error) {
        console.error('取消发布试卷失败', error)
        ElMessage.error('取消发布试卷失败')
      }
    })
    .catch(() => {})
}

/**
 * 处理统计操作
 */
const handleStats = async (row) => {
  statsDialog.visible = true
  statsDialog.title = `统计信息 - ${row.title}`
  statsLoading.value = true
  
  try {
    const response = await getExamStats(row.examId)
    Object.assign(examStats, response.data)
  } catch (error) {
    console.error('获取考试统计失败', error)
    ElMessage.error('获取考试统计失败')
  } finally {
    statsLoading.value = false
  }
}

/**
 * 处理删除操作
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除试卷"${row.title}"吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteExamPaper(row.examId)
        ElMessage.success('删除成功')
        getList()
      } catch (error) {
        console.error('删除试卷失败', error)
        ElMessage.error('删除试卷失败')
      }
    })
    .catch(() => {})
}

/**
 * 打开复制试卷对话框
 */
const openCopyPaperDialog = async (row) => {
  paperDialog.title = '复制试卷'
  paperDialog.loading = true
  
  try {
    // 获取试卷详情
    const response = await getExamPaperInfo(row.examId)
    const paperData = response.data
    
    // 填充表单数据，但不设置paperId，这样提交时会创建新的试卷
    paperForm.paperId = undefined
    paperForm.paperName = `${paperData.title} - 副本`
    paperForm.courseId = paperData.courseId
    paperForm.duration = paperData.duration
    paperForm.description = paperData.description
    paperForm.startTime = paperData.startTime
    paperForm.endTime = paperData.endTime
    
    paperDialog.visible = true
  } catch (error) {
    console.error('获取试卷详情失败', error)
    ElMessage.error('获取试卷详情失败')
  } finally {
    paperDialog.loading = false
  }
}

/**
 * 处理复制操作
 */
const handleCopy = (row) => {
  // 原来是跳转到编辑页面
  // router.push({
  //   path: '/exam/paper/edit',
  //   query: { copy: row.examId }
  // })
  // 现在改为打开对话框
  openCopyPaperDialog(row)
}

/**
 * 处理分页大小改变
 */
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  getList()
}

/**
 * 处理页码改变
 */
const handleCurrentChange = (num) => {
  queryParams.pageNum = num
  getList()
}

/**
 * 提交试卷表单
 */
const submitPaperForm = async () => {
  if (!paperFormRef.value) return
  
  await paperFormRef.value.validate(async (valid) => {
    if (valid) {
      paperDialog.loading = true
      try {
        // 构建提交数据
        const submitData = {
          examId: paperForm.paperId,
          title: paperForm.paperName,
          courseId: paperForm.courseId,
          duration: paperForm.duration,
          description: paperForm.description,
          startTime: paperForm.startTime,
          endTime: paperForm.endTime
        }
        
        let examId
        
        if (paperForm.paperId) {
          // 编辑试卷
          try {
            const response = await updateExamPaper(submitData)
            ElMessage.success('修改试卷成功')
            examId = paperForm.paperId
          } catch (error) {
            console.error('修改试卷失败', error)
            ElMessage.error("修改试卷失败")
          }
        } else {
          // 新增试卷
          const response = await addExamPaper(submitData)
          ElMessage.success('新增试卷成功')
          // 获取返回的试卷ID
          examId = response.data
        }
        
        // 关闭对话框
        paperDialog.visible = false
        resetPaperForm()
        
        // 刷新列表
        getList()
        
        // 询问是否立即编辑添加试题
        if (examId) {
          ElMessageBox.confirm('试卷创建成功，是否立即添加试题？', '提示', {
            confirmButtonText: '去添加试题',
            cancelButtonText: '稍后再说',
            type: 'info'
          }).then(() => {
            // 跳转到编辑页面
            router.push(`/exam/paper/edit/${examId}`)
          }).catch(() => {
            // 用户选择稍后再说，不做任何操作
          })
        }
      } catch (error) {
        console.error(paperForm.paperId ? '修改试卷失败' : '新增试卷失败', error)
        ElMessage.error(paperForm.paperId ? '修改试卷失败' : '新增试卷失败')
      } finally {
        paperDialog.loading = false
      }
    }
  })
}

/**
 * 格式化答案显示
 */
const formatAnswer = (question) => {
  if (!question.answer) return '无参考答案';
  
  // 判断题特殊处理
  if (question.typeName === '判断题') {
    return question.answer === 'true' || question.answer === 'A' ? '正确' : '错误';
  }
  
  // 多选题特殊处理
  if (question.typeName === '多选题') {
    // 展示选项内容
    const options = parseOptions(question.options);
    if (options.length > 0) {
      const answerKeys = question.answer.split('');
      const selectedOptions = answerKeys.map(key => {
        const option = options.find(opt => opt.key === key);
        return option ? `${key}. ${option.value}` : key;
      });
      return selectedOptions.join('<br>');
    }
    return question.answer;
  }
  
  // 单选题特殊处理
  if (question.typeName === '单选题') {
    // 展示选项内容
    const options = parseOptions(question.options);
    if (options.length > 0) {
      const option = options.find(opt => opt.key === question.answer);
      return option ? `${question.answer}. ${option.value}` : question.answer;
    }
    return question.answer;
  }
  
  return question.answer;
};

onMounted(() => {
  getCourses()
  getList()
  loadQuestionTypes()
})
</script>

<style scoped>
.paper-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.paper-preview {
  padding: 20px;
  background: #fff;
  max-width: 900px;
  margin: 0 auto;
}

.paper-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #ebeef5;
}

.paper-header h1 {
  font-size: 24px;
  margin-bottom: 15px;
}

.paper-info {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 10px;
  font-size: 16px;
  color: #606266;
}

.paper-description {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin-top: 15px;
  line-height: 1.6;
}

.question-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  align-items: baseline;
  margin-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 5px;
}

.section-header h2 {
  font-size: 18px;
  margin-right: 10px;
  margin-bottom: 0;
}

.section-info {
  font-size: 14px;
  color: #909399;
}

.question-item {
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px dashed #ebeef5;
}

.question-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
}

.question-number {
  font-weight: bold;
  margin-right: 10px;
  min-width: 20px;
}

.question-title {
  flex: 1;
  line-height: 1.6;
}

.question-score {
  color: #f56c6c;
  margin-left: 10px;
  white-space: nowrap;
}

.question-options {
  margin-left: 30px;
  margin-bottom: 15px;
}

.option-item {
  display: flex;
  margin-bottom: 8px;
}

.option-label {
  width: 30px;
  font-weight: bold;
}

.option-content {
  flex: 1;
}

.question-blank {
  margin-left: 30px;
  margin-bottom: 15px;
}

.blank-line {
  border-bottom: 1px solid #ddd;
  height: 24px;
  width: 80%;
  margin: 8px 0;
  color: #999;
  font-style: italic;
  display: flex;
  align-items: flex-end;
}

.question-answer-area {
  margin-left: 30px;
  margin-bottom: 15px;
}

.answer-area-placeholder {
  background-color: #f8f8f8;
  border: 1px dashed #ddd;
  min-height: 100px;
  padding: 10px;
  color: #999;
  font-style: italic;
}

.question-answer, .question-analysis {
  display: flex;
  margin-top: 10px;
  background-color: #f8f8f8;
  padding: 8px;
  border-radius: 4px;
  margin-left: 30px;
}

.answer-label, .analysis-label {
  width: 80px;
  font-weight: bold;
  color: #409eff;
}

.answer-content, .analysis-content {
  flex: 1;
}

.no-questions {
  padding: 40px 0;
  text-align: center;
}

.stats-content {
  padding: 20px 0;
}

.question-select-container {
  padding: 15px 0;
}

.question-select-header {
  margin-bottom: 15px;
}

.question-select-tools {
  display: flex;
  justify-content: space-between;
}

.question-select-pagination {
  margin: 15px 0;
  display: flex;
  justify-content: flex-end;
}

.question-select-actions {
  margin: 15px 0;
  display: flex;
  justify-content: flex-end;
}

.added-questions-section {
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #303133;
}
</style>