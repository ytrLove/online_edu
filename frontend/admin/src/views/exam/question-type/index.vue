<template>
  <div class="question-type-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>题目类型管理</span>
          <el-button v-permission="['exam:question:add']" type="primary" @click="handleAdd">新增题目类型</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-row :gutter="20" class="search-container">
        <el-col :span="6">
          <el-input v-model="queryParams.typeName" placeholder="题目类型名称" clearable @keyup.enter="handleQuery"></el-input>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-col>
      </el-row>
      
      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="typeList" border style="width: 100%">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="typeName" label="类型名称" width="120" align="center" />
        <el-table-column label="自动评分" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isAutoGraded === 1 ? 'success' : 'info'">
              {{ scope.row.isAutoGraded === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="类型描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button v-permission="['exam:question:edit']" type="primary" link size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button v-permission="['exam:question:remove']" type="danger" link size="small" @click="handleDelete(scope.row)">
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
      
      <!-- 添加/修改对话框 -->
      <el-dialog
        v-model="dialog.visible"
        :title="dialog.title"
        width="500px"
        destroy-on-close
        @closed="resetForm"
      >
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="类型名称" prop="typeName">
            <el-input v-model="form.typeName" placeholder="请输入类型名称" />
          </el-form-item>
          <el-form-item label="自动评分" prop="isAutoGraded">
            <el-switch
              v-model.number="form.isAutoGraded"
              :active-value="1"
              :inactive-value="0"
              active-text="是"
              inactive-text="否"
            />
          </el-form-item>
          <el-form-item label="类型描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入类型描述" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getQuestionTypeList, getQuestionTypeInfo, addQuestionType, updateQuestionType, deleteQuestionType } from '@/api/exam/questionType'

// 加载状态
const loading = ref(false)
// 题目类型列表
const typeList = ref([])
// 总记录数
const total = ref(0)
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  typeName: ''
})
// 表单对象
const formRef = ref(null)
const form = reactive({
  typeId: undefined,
  typeName: '',
  isAutoGraded: 1,
  description: '',
  status: 1
})
// 表单校验规则
const rules = {
  typeName: [
    { required: true, message: '请输入类型名称', trigger: 'blur' }
  ],
  isAutoGraded: [
    { required: true, message: '请选择是否自动评分', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入类型描述', trigger: 'blur' }
  ]
}
// 对话框控制
const dialog = reactive({
  visible: false,
  title: ''
})

/**
 * 查询题目类型列表
 */
const getList = async () => {
  loading.value = true
  try {
    const response = await getQuestionTypeList(queryParams)
    typeList.value = response.data.rows
    total.value = response.data.total
  } catch (error) {
    console.error('获取题目类型列表失败', error)
    ElMessage.error('获取题目类型列表失败')
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
  queryParams.typeName = ''
  handleQuery()
}

/**
 * 处理添加操作
 */
const handleAdd = () => {
  resetForm()
  dialog.visible = true
  dialog.title = '添加题目类型'
}

/**
 * 处理修改操作
 */
const handleEdit = async (row) => {
  resetForm()
  dialog.visible = true
  dialog.title = '修改题目类型'
  
  try {
    loading.value = true
    const response = await getQuestionTypeInfo(row.typeId)
    Object.assign(form, response.data)
  } catch (error) {
    console.error('获取题目类型详情失败', error)
    ElMessage.error('获取题目类型详情失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理删除操作
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除题目类型"${row.typeName}"吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteQuestionType(row.typeId)
        ElMessage.success('删除成功')
        getList()
      } catch (error) {
        console.error('删除题目类型失败', error)
        ElMessage.error('删除题目类型失败')
      }
    })
    .catch(() => {})
}

/**
 * 重置表单
 */
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.typeId = undefined
  form.typeName = ''
  form.isAutoGraded = 1
  form.description = ''
  form.status = 1
}

/**
 * 提交表单
 */
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        if (form.typeId) {
          // 修改
          await updateQuestionType(form)
          ElMessage.success('修改成功')
        } else {
          // 新增
          await addQuestionType(form)
          ElMessage.success('新增成功')
        }
        dialog.visible = false
        getList()
      } catch (error) {
        console.error('保存题目类型失败', error)
        ElMessage.error('保存题目类型失败')
      } finally {
        loading.value = false
      }
    }
  })
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

onMounted(() => {
  getList()
})
</script>

<style scoped>
.question-type-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-container {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 