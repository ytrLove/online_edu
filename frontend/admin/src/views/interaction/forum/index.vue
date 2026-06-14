<template>
  <div class="container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>讨论区管理</span>
          <div class="header-actions">
            <el-button type="primary" plain @click="handleCreate" v-permission="'comm:forum:create'">
              <el-icon><Plus /></el-icon>新增讨论区
            </el-button>
            <el-button type="warning" plain @click="getList">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="queryParams" @search="handleQuery" @reset="resetQuery">
        <el-form-item label="讨论区名称">
          <el-input v-model="queryParams.forumName" placeholder="请输入讨论区名称" clearable />
        </el-form-item>
        <el-form-item label="课程ID">
          <el-input v-model="queryParams.courseId" placeholder="请输入课程ID" clearable />
        </el-form-item>
        <el-form-item label="是否公开">
          <el-select v-model="queryParams.isPublic" placeholder="请选择" clearable>
            <el-option label="公开" value="1" />
            <el-option label="不公开" value="0" />
          </el-select>
        </el-form-item>
      </search-form>

      <!-- 表格区域 -->
      <data-table
        :data="forumList"
        :loading="loading"
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :total="total"
        @pagination="getList"
        @update:current-page="val => queryParams.pageNum = val"
        @update:page-size="val => queryParams.pageSize = val"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="50" align="center" />
        <!-- <el-table-column label="讨论区ID" prop="forumId" width="80" align="center" /> -->
        <el-table-column label="讨论区名称" prop="forumName" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column label="课程名称" prop="courseName" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column label="描述" prop="description" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column label="是否公开" align="center" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isPublic == 1 ? 'success' : 'info'" size="small">
              {{ scope.row.isPublic == 1 ? '公开' : '不公开' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="80">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
              v-permission="'comm:forum:edit'"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="150" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center" width="250" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              text
              @click="handleTopics(scope.row)"
              v-permission="'comm:topic:list'"
            >
              <el-icon><View /></el-icon>查看主题
            </el-button>
            <el-button
              type="success"
              size="small"
              text
              @click="handleUpdate(scope.row)"
              v-permission="'comm:forum:edit'"
            >
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button
              type="danger"
              size="small"
              text
              @click="handleDelete(scope.row)"
              v-permission="'comm:forum:remove'"
            >
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>
    </el-card>

    <!-- 添加或修改讨论区对话框 -->
    <el-dialog 
      :model-value="open" 
      @update:model-value="open = $event" 
      :title="title" 
      width="500px" 
      append-to-body
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程ID" prop="courseId">
          <el-input v-model="form.courseId" placeholder="请输入课程ID" />
        </el-form-item>
        <el-form-item label="名称" prop="forumName">
          <el-input v-model="form.forumName" placeholder="请输入讨论区名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="是否公开" prop="isPublic">
          <el-radio-group v-model="form.isPublic">
            <el-radio :label="1">公开</el-radio>
            <el-radio :label="0">不公开</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { listForum, getForum, createForum, updateForum, deleteForum } from '@/api/discussion/forum'
import { Plus, Edit, Delete, View, Refresh } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm/index.vue'
import DataTable from '@/components/DataTable/index.vue'

const router = useRouter()

// 遮罩层
const loading = ref(true)
// 选中数组
const ids = ref([])
// 非单个禁用
const single = ref(true)
// 非多个禁用
const multiple = ref(true)
// 显示搜索条件
const showSearch = ref(true)
// 总条数
const total = ref(0)
// 讨论区表格数据
const forumList = ref([])
// 弹出层标题
const title = ref("")
// 是否显示弹出层
const open = ref(false)
// 表单参数
const form = reactive({
  forumId: undefined,
  courseId: undefined,
  forumName: undefined,
  description: undefined,
  isPublic: 1,
  status: 1,
  remark: undefined
})
// 表单校验规则
const rules = {
  courseId: [
    { required: true, message: "课程ID不能为空", trigger: "blur" }
  ],
  forumName: [
    { required: true, message: "讨论区名称不能为空", trigger: "blur" }
  ]
}
// 表单引用
const formRef = ref(null)
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  forumName: undefined,
  courseId: undefined,
  isPublic: undefined
})

// 生命周期钩子
onMounted(() => {
  getList()
})

// 获取讨论区列表
const getList = () => {
  loading.value = true
  listForum(queryParams).then(response => {
    forumList.value = response.data.records
    total.value = response.data.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.forumName = undefined
  queryParams.courseId = undefined
  queryParams.isPublic = undefined
  queryParams.pageNum = 1
  getList()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.forumId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 新增按钮操作
const handleCreate = () => {
  reset()
  open.value = true
  title.value = "添加讨论区"
}

// 修改按钮操作
const handleUpdate = (row) => {
  reset()
  const forumId = row.forumId || ids.value[0]
  getForum(forumId).then(response => {
    Object.assign(form, response.data)
    open.value = true
    title.value = "修改讨论区"
  })
}

// 查看主题按钮操作
const handleTopics = (row) => {
  router.push({ path: '/interaction/topic', query: { forumId: row.forumId } })
}

// 提交按钮
const submitForm = () => {
  formRef.value.validate(valid => {
    if (valid) {
      if (form.forumId != null) {
        updateForum(form).then(response => {
          ElMessage.success("修改成功")
          open.value = false
          getList()
        })
      } else {
        createForum(form).then(response => {
          ElMessage.success("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

// 删除按钮操作
const handleDelete = (row) => {
  const forumIds = row.forumId || ids.value
  ElMessageBox.confirm('是否确认删除讨论区编号为"' + forumIds + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return deleteForum(forumIds)
  }).then(() => {
    getList()
    ElMessage.success("删除成功")
  }).catch(() => {})
}

// 状态修改
const handleStatusChange = (row) => {
  let text = row.status === 1 ? "启用" : "停用"
  ElMessageBox.confirm('确认要"' + text + '""' + row.forumName + '"讨论区吗?', "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return updateForum({
      forumId: row.forumId,
      status: row.status
    })
  }).then(() => {
    ElMessage.success(text + "成功")
  }).catch(() => {
    row.status = row.status === 1 ? 0 : 1
  })
}

// 表单重置
const reset = () => {
  form.forumId = undefined
  form.courseId = undefined
  form.forumName = undefined
  form.description = undefined
  form.isPublic = 1
  form.status = 1
  form.remark = undefined
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}
</script>

<style scoped>
.container {
  padding: 10px;
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
.dialog-footer {
  text-align: right;
}
</style>