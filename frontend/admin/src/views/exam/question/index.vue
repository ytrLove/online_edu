<template>
  <div class="question-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">试题管理{{ courseName ? ' - ' + courseName : '' }}</span>
          <el-button v-permission="['exam:question:add']" type="primary" @click="handleAdd">新增试题</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="auto" class="search-form">
        <el-form-item label="试题内容" prop="content">
          <el-input v-model="queryParams.questionContent" placeholder="请输入试题内容" clearable style="width: 240px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="试题类型" prop="typeId">
          <el-select v-model="queryParams.questionTypeId" placeholder="请选择试题类型" clearable style="width: 200px">
            <el-option
              v-for="item in typeOptions"
              :key="item.typeId"
              :label="item.typeName"
              :value="item.typeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格展示区域 -->
      <el-table v-loading="loading" :data="questionList" border>
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column prop="questionContent" label="试题内容" show-overflow-tooltip>
          <template #default="scope">
            <div v-html="formatContent(scope.row.questionContent)"></div>
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="试题类型" width="120" align="center" />
        <el-table-column label="难度" width="100" align="center">
          <template #default="scope">
            {{ 
              scope.row.difficulty === 'easy' ? '简单' :
              scope.row.difficulty === 'medium' ? '中等' :
              scope.row.difficulty === 'hard' ? '困难' : '未知'
            }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160" align="center">
          <template #default="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right" align="center">
          <template #default="scope">
            <el-button v-permission="['exam:question:query']" type="primary" size="small" text @click="handleView(scope.row)">
              <el-icon><View /></el-icon>查看
            </el-button>
            <el-button v-permission="['exam:question:edit']" type="warning" size="small" text @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button v-permission="['exam:question:remove']" type="danger" size="small" text @click="handleDelete(scope.row)">
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
      
      <!-- 预览对话框 -->
      <el-dialog v-model="previewDialog.visible" :title="previewDialog.title" width="50%" destroy-on-close>
        <div class="preview-question">
          <div class="question-content" v-html="currentQuestion.questionContent"></div>
          
          <div v-if="currentQuestion.questionTypeId === 1 || currentQuestion.questionTypeId === 2" class="question-options">
            <div v-for="(option, index) in JSON.parse(currentQuestion.options || '[]')" :key="index" class="option-item">
              <span class="option-label">{{ option.key }}.</span>
              <div class="option-content" v-html="option.value"></div>
            </div>
          </div>
          
          <div class="question-answer">
            <div class="answer-label">答案：</div>
            <div class="answer-content">
              <template v-if="currentQuestion.questionTypeId === 1">
                {{ currentQuestion.correctAnswer }}
              </template>
              <template v-else-if="currentQuestion.questionTypeId === 2">
                {{ currentQuestion.correctAnswer.split('').join('、') }}
              </template>
              <template v-else-if="currentQuestion.questionTypeId === 3">
                {{ currentQuestion.correctAnswer === 'true' ? '正确' : '错误' }}
              </template>
              <template v-else>
                {{ currentQuestion.correctAnswer }}
              </template>
            </div>
          </div>
          
          <div v-if="currentQuestion.explanation" class="question-analysis">
            <div class="analysis-label">解析：</div>
            <div class="analysis-content" v-html="currentQuestion.explanation"></div>
          </div>

          <div class="question-info">
            <div class="info-item">
              <span class="info-label">难度：</span>
              <span class="info-value">
                {{ 
                  currentQuestion.difficulty === 'easy' ? '简单' :
                  currentQuestion.difficulty === 'medium' ? '中等' :
                  currentQuestion.difficulty === 'hard' ? '困难' : '未知'
                }}
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">创建时间：</span>
              <span class="info-value">{{ currentQuestion.createTime }}</span>
            </div>
          </div>
        </div>
      </el-dialog>
      
      <!-- 添加/编辑对话框 -->
      <el-dialog 
        v-model="editDialog.visible" 
        :title="editDialog.title" 
        width="60%" 
        destroy-on-close
        @close="resetForm"
      >
        <el-form ref="questionFormRef" :model="questionForm" :rules="rules" label-width="100px">
          <!-- 试题类型 -->
          <el-form-item label="试题类型" prop="questionTypeId">
            <el-select v-model="questionForm.questionTypeId" placeholder="请选择试题类型" style="width: 100%" @change="handleTypeChange">
              <el-option
                v-for="item in typeOptions"
                :key="item.typeId"
                :label="item.typeName"
                :value="item.typeId"
              />
            </el-select>
          </el-form-item>
          
          <!-- 试题内容 -->
          <el-form-item label="试题内容" prop="questionContent">
            <el-input
              v-model="questionForm.questionContent"
              type="textarea"
              :rows="4"
              placeholder="请输入试题内容"
            />
            <!-- 图片上传功能 -->
           
          </el-form-item>
          
          <!-- 选项 (单选题/多选题) -->
          <div v-if="questionForm.questionTypeId === 1 || questionForm.questionTypeId === 2">
            <el-divider content-position="left">选项</el-divider>
            <div v-for="(option, index) in questionForm._options" :key="index" class="option-form-item">
              <el-form-item :label="`选项 ${String.fromCharCode(65 + index)}`" :prop="`_options.${index}.content`" :rules="{ required: true, message: '选项内容不能为空', trigger: 'blur' }">
                <div class="option-form-content">
                  <el-input v-model="option.content" placeholder="请输入选项内容" />
                  <el-button type="danger" icon="Delete" circle @click="removeOption(index)" v-if="questionForm._options.length > 2"></el-button>
                </div>
                <div class="option-error" v-if="option.content === ''">选项内容不能为空</div>
              </el-form-item>
            </div>
            <el-button type="primary" plain @click="addOption" :disabled="questionForm._options.length >= 6">添加选项</el-button>
          </div>
          
          <!-- 答案 -->
          <el-form-item label="答案" prop="correctAnswer">
            <!-- 单选题 -->
            <template v-if="questionForm.questionTypeId === 1">
              <el-radio-group v-model="questionForm.correctAnswer">
                <el-radio v-for="(option, index) in questionForm._options" :key="index" :label="String.fromCharCode(65 + index)">
                  {{ String.fromCharCode(65 + index) }}
                </el-radio>
              </el-radio-group>
            </template>
            
            <!-- 多选题 -->
            <template v-else-if="questionForm.questionTypeId === 2">
              <el-checkbox-group v-model="questionForm._answerArray">
                <el-checkbox v-for="(option, index) in questionForm._options" :key="index" :label="String.fromCharCode(65 + index)">
                  {{ String.fromCharCode(65 + index) }}
                </el-checkbox>
              </el-checkbox-group>
            </template>
            
            <!-- 判断题 -->
            <template v-else-if="questionForm.questionTypeId === 3">
              <el-radio-group v-model="questionForm.correctAnswer">
                <el-radio label="true">正确</el-radio>
                <el-radio label="false">错误</el-radio>
              </el-radio-group>
            </template>
            
            <!-- 填空题/简答题/其他 -->
            <template v-else>
              <el-input
                v-model="questionForm.correctAnswer"
                type="textarea"
                :rows="3"
                placeholder="请输入答案"
              />
            </template>
          </el-form-item>
          
          <!-- 分析 -->
          <el-form-item label="解析" prop="explanation">
            <el-input
              v-model="questionForm.explanation"
              type="textarea"
              :rows="3"
              placeholder="请输入试题解析（选填）"
            />
          </el-form-item>
          
          <!-- 难度 -->
          <el-form-item label="难度" prop="difficulty">
            <el-select v-model="questionForm.difficulty" placeholder="请选择难度" style="width: 100%">
              <el-option label="简单" value="easy" />
              <el-option label="中等" value="medium" />
              <el-option label="困难" value="hard" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="editDialog.visible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">确定</el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 添加图片预览弹窗 -->
      <el-dialog v-model="previewImage.visible" :title="previewImage.title">
        <img :src="previewImage.url" class="preview-image" alt="预览图片">
      </el-dialog>
    </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getQuestionList, getQuestionInfo, addQuestion, updateQuestion, deleteQuestion } from '@/api/exam/question'
import { getQuestionTypeList } from '@/api/exam/questionType'
import { Plus, Search, Refresh, View, Edit, Delete, DocumentCopy } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'
import { getFileUrl } from '@/api/common/upload'
import { useRoute } from 'vue-router'

// 获取路由参数
const route = useRoute()
const courseId = ref(route.query.courseId ? Number(route.query.courseId) : undefined)
const courseName = ref(route.query.courseName || '')

// 加载状态
const loading = ref(false)
// 试题列表
const questionList = ref([])
// 试题类型选项
const typeOptions = ref([])
// 总记录数
const total = ref(0)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 6,
  questionContent: '',
  questionTypeId: undefined,
  courseId: courseId.value // 设置课程ID
})

// 预览对话框
const previewDialog = reactive({
  visible: false,
  title: '试题预览'
})

// 编辑对话框
const editDialog = reactive({
  visible: false,
  title: '添加试题',
  isEdit: false
})

// 当前操作的试题
const currentQuestion = reactive({
  questionId: undefined,
  questionTypeId: undefined,
  typeName: '',
  questionContent: '',
  options: [],
  correctAnswer: '',
  explanation: '',
  difficulty: '',
  createTime: ''
})

// 预览图片对话框
const previewImage = reactive({
  visible: false,
  title: '图片预览',
  url: ''
})

// 上传地址和请求头
const uploadUrl = 'http://110.41.15.40:4000/api/upload'
const uploadHeaders = computed(() => {
  return {
    Authorization: `Bearer ${getToken()}`
  }
})

// 表单对象
const questionFormRef = ref()
const questionForm = reactive({
  questionId: undefined, // 题目ID（修改时必填）
  questionTypeId: undefined, // 题目类型ID
  questionContent: '', // 题目内容
  options: '[]', // 选项（JSON格式）
  correctAnswer: '', // 正确答案
  explanation: '', // 解析
  difficulty: 'medium', // 难度（easy简单 medium中等 hard困难）
  courseId: undefined, // 课程ID
  status: 1, // 状态（1正常 0停用）
  // 用于UI展示的临时字段
  _options: [
    { content: '' },
    { content: '' },
    { content: '' },
    { content: '' }
  ],
  _answerArray: [], // 用于多选题的临时答案数组
  images: [] // 上传的图片列表
})

// 表单校验规则
const rules = {
  questionTypeId: [
    { required: true, message: '请选择题目类型', trigger: 'change' }
  ],
  questionContent: [
    { required: true, message: '请输入题目内容', trigger: 'blur' }
  ],
  correctAnswer: [
    { required: true, message: '请选择/输入正确答案', trigger: 'blur' }
  ],
  difficulty: [
    { required: true, message: '请选择难度', trigger: 'change' }
  ]
}

// 监听多选题答案数组变化，自动更新答案字段
watch(() => questionForm._answerArray, (val) => {
  if (questionForm.questionTypeId === 2) {
    questionForm.correctAnswer = val.sort().join('')
  }
}, { deep: true })

/**
 * 获取试题类型列表
 */
const getQuestionType = async () => {
  try {
    const response = await getQuestionTypeList()
    typeOptions.value = response.data.rows
    console.log(typeOptions.value);
    
  } catch (error) {
    console.error('获取试题类型失败', error)
  }
}

/**
 * 获取试题列表
 */
const getList = async () => {
  loading.value = true
  try {
    const response = await getQuestionList(queryParams)
    questionList.value = response.data.rows
    total.value = response.data.total
  } catch (error) {
    console.error('获取试题列表失败', error)
    ElMessage.error('获取试题列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 格式化内容，截取长文本
 */
const formatContent = (content) => {
  if (!content) return ''
  return content.length > 50 ? content.substring(0, 50) + '...' : content
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
  queryParams.questionContent = ''
  queryParams.questionTypeId = undefined
  // 保留课程ID
  queryParams.courseId = courseId.value
  handleQuery()
}

/**
 * 处理预览操作
 */
const handleView = async (row) => {
  try {
    const response = await getQuestionInfo(row.questionId)
    // 直接使用后端返回的数据
    Object.assign(currentQuestion, response.data)
    previewDialog.visible = true
  } catch (error) {
    console.error('获取试题详情失败', error)
    ElMessage.error('获取试题详情失败')
  }
}

/**
 * 添加试题操作
 */
const handleAdd = () => {
  editDialog.title = '添加试题'
  editDialog.isEdit = false
  editDialog.visible = true
  
  // 默认选择第一个试题类型
  if (typeOptions.value.length > 0) {
    questionForm.questionTypeId = typeOptions.value[0].typeId
  }
  
  // 设置当前选中的课程ID
  questionForm.courseId = courseId.value
}

/**
 * 编辑试题操作
 */
const handleEdit = async (row) => {
  editDialog.title = '编辑试题'
  editDialog.isEdit = true
  
  try {
    const response = await getQuestionInfo(row.questionId)
    const questionData = response.data
    
    // 重置表单
    resetForm()
    
    // 设置表单数据
    questionForm.questionId = questionData.questionId
    questionForm.questionTypeId = questionData.questionTypeId
    questionForm.questionContent = questionData.questionContent
    questionForm.correctAnswer = questionData.correctAnswer
    questionForm.explanation = questionData.explanation
    questionForm.difficulty = questionData.difficulty || 'medium'
    questionForm.courseId = questionData.courseId
    questionForm.status = questionData.status || 1
    
    // 处理选项
    if (questionData.questionTypeId === 1 || questionData.questionTypeId === 2) {
      const optionsData = JSON.parse(questionData.options || '[]')
      questionForm._options = optionsData.map(option => ({
        content: option.value
      }))
      questionForm.options = questionData.options
      
      // 处理多选题答案
      if (questionData.questionTypeId === 2 && questionData.correctAnswer) {
        questionForm._answerArray = questionData.correctAnswer.split('')
      }
    }
    
    // 处理图片列表(如果有)
    if (questionData.images && Array.isArray(questionData.images)) {
      questionForm.images = questionData.images
    } else {
      questionForm.images = []
    }
    
    editDialog.visible = true
  } catch (error) {
    console.error('获取试题详情失败', error)
    ElMessage.error('获取试题详情失败')
  }
}

/**
 * 删除试题操作
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除试题 "${formatContent(row.questionContent)}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteQuestion(row.questionId)
        ElMessage.success('删除成功')
        getList()
      } catch (error) {
        console.error('删除试题失败', error)
        ElMessage.error('删除试题失败')
      }
    })
    .catch(() => {})
}

/**
 * 处理分页大小改变
 */
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

/**
 * 处理页码改变
 */
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

/**
 * 处理试题类型改变
 */
const handleTypeChange = (typeId) => {
  // 根据试题类型重置部分表单内容
  questionForm.correctAnswer = ''
  questionForm._answerArray = []
  
  // 重置选项
  if (typeId === 1 || typeId === 2) {
    questionForm._options = [
      { content: '' },
      { content: '' },
      { content: '' },
      { content: '' }
    ]
  } else {
    questionForm._options = []
    questionForm.options = '[]'
  }
}

/**
 * 添加选项
 */
const addOption = () => {
  if (questionForm._options.length < 6) {
    questionForm._options.push({ content: '' })
  }
}

/**
 * 移除选项
 */
const removeOption = (index) => {
  if (questionForm._options.length > 2) {
    questionForm._options.splice(index, 1)
    
    // 如果删除的选项是当前选中的答案，则重置答案
    if (questionForm.questionTypeId === 1) {
      const optionLabel = String.fromCharCode(65 + index)
      if (questionForm.correctAnswer === optionLabel) {
        questionForm.correctAnswer = ''
      }
    } else if (questionForm.questionTypeId === 2) {
      const optionLabel = String.fromCharCode(65 + index)
      const answerIndex = questionForm._answerArray.indexOf(optionLabel)
      if (answerIndex !== -1) {
        questionForm._answerArray.splice(answerIndex, 1)
      }
    }
    
    // 更新选项的key
    questionForm._options.forEach((item, idx) => {
      // 更新多选答案中的选项
      if (questionForm.questionTypeId === 2) {
        const oldKey = String.fromCharCode(65 + idx + (idx >= index ? 1 : 0))
        const newKey = String.fromCharCode(65 + idx)
        
        const answerIndex = questionForm._answerArray.indexOf(oldKey)
        if (answerIndex !== -1) {
          questionForm._answerArray[answerIndex] = newKey
        }
      }
    })
  }
}

/**
 * 表单提交
 */
const submitForm = async () => {
  if (!questionFormRef.value) return
  
  // 检查选项是否为空
  let hasEmptyOption = false
  if (questionForm.questionTypeId === 1 || questionForm.questionTypeId === 2) {
    questionForm._options.forEach(option => {
      if (!option.content.trim()) {
        hasEmptyOption = true
      }
    })
    
    if (hasEmptyOption) {
      ElMessage.error('选项内容不能为空')
      return
    }
  }
  
  // 检查单选/多选题是否选择了答案
  if (questionForm.questionTypeId === 1 && !questionForm.correctAnswer) {
    ElMessage.error('请选择正确答案')
    return
  }
  
  if (questionForm.questionTypeId === 2 && (!questionForm._answerArray || questionForm._answerArray.length === 0)) {
    ElMessage.error('请至少选择一个正确答案')
    return
  }
  
  // 更新选项JSON数据
  if (questionForm.questionTypeId === 1 || questionForm.questionTypeId === 2) {
    questionForm.options = JSON.stringify(questionForm._options.map((item, index) => ({
      key: String.fromCharCode(65 + index),
      value: item.content
    })))
  }
  
  await questionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 直接提交 questionForm，因为它已经符合后端 QuestionParam 的格式
        if (editDialog.isEdit) {
          await updateQuestion(questionForm)
          ElMessage.success('更新成功')
        } else {
          await addQuestion(questionForm)
          ElMessage.success('添加成功')
        }
        editDialog.visible = false
        getList()
      } catch (error) {
        console.error('保存试题失败', error)
        ElMessage.error(error.message || '保存试题失败')
      }
    }
  })
}

/**
 * 重置表单
 */
const resetForm = () => {
  if (questionFormRef.value) {
    questionFormRef.value.resetFields()
  }
  
  questionForm.questionId = undefined
  questionForm.questionTypeId = undefined
  questionForm.questionContent = ''
  questionForm.options = '[]'
  questionForm.correctAnswer = ''
  questionForm.explanation = ''
  questionForm.difficulty = 'medium'
  questionForm.courseId = undefined
  questionForm.status = 1
  questionForm.images = []
  
  // 重置UI相关字段
  questionForm._options = [
    { content: '' },
    { content: '' },
    { content: '' },
    { content: '' }
  ]
  questionForm._answerArray = []
}

/**
 * 图片上传前的验证
 */
const beforeImageUpload = (file) => {
  // 验证图片类型
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  
  // 验证图片大小（限制为2MB）
  const isLtMaxSize = file.size / 1024 / 1024 < 2
  if (!isLtMaxSize) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  
  return true
}

/**
 * 图片上传成功处理
 */
const handleImageSuccess = (response, file, fileList) => {
  if (response && response.data) {
    // 获取上传后的文件URL
    const imageUrl = getFileUrl(response.data.fileId)
    
    // 将图片添加到图片列表
    questionForm.images.push({
      name: file.name,
      url: imageUrl,
      fileId: response.data.fileId
    })
    
    // 可以选择将图片URL插入到内容中或提供给用户复制
    ElMessage.success('图片上传成功，您可以复制链接插入到内容中')
    
    // 生成Markdown格式的图片链接，方便用户复制
    const markdownLink = `![${file.name}](${imageUrl})`
    console.log('图片链接:', markdownLink)
  }
}

/**
 * 图片上传失败处理
 */
const handleImageError = (error) => {
  console.error('图片上传失败', error)
  ElMessage.error('图片上传失败，请重试')
}

/**
 * 预览图片
 */
const handleImagePreview = (file) => {
  previewImage.url = file.url
  previewImage.visible = true
}

onMounted(() => {
  getQuestionType()
  getList()
})
</script>

<style scoped>
.question-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 预览样式 */
.preview-question {
  padding: 20px;
  background-color: #f8f8f8;
  border-radius: 4px;
}

.question-content {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 20px;
}

.question-options {
  margin-bottom: 20px;
}

.option-item {
  display: flex;
  margin-bottom: 10px;
}

.option-label {
  margin-right: 10px;
  font-weight: bold;
}

.option-content {
  flex: 1;
}

.question-answer, .question-analysis, .question-info {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px dashed #ccc;
}

.answer-label, .analysis-label, .info-label {
  font-weight: bold;
  margin-bottom: 10px;
}

.info-item {
  margin-bottom: 10px;
}

.info-label {
  color: #666;
  margin-right: 10px;
}

.info-value {
  color: #333;
}

/* 表单样式 */
.option-form-item {
  margin-bottom: 15px;
}

.option-form-content {
  display: flex;
  align-items: center;
}

.option-form-content .el-input {
  flex: 1;
  margin-right: 10px;
}

.option-error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 5px;
}

/* 图片上传样式 */
.image-upload-container {
  margin-top: 10px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
}
</style>
