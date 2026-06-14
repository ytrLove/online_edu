<template>
  <div class="topic-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ forumName ? forumName + ' - 讨论主题管理' : '讨论主题管理' }}</span>
          <div class="header-actions">
            <el-button type="primary" plain @click="handleCreate" v-permission="'comm:topic:add'">
              <el-icon><Plus /></el-icon>新增主题
            </el-button>
            <el-button type="info" plain @click="backToForumList">
              <el-icon><Back /></el-icon>返回讨论区
            </el-button>
            <el-button type="warning" plain @click="getList">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="queryParams" @search="handleQuery" @reset="resetQuery">
        <el-form-item label="主题标题">
          <el-input v-model="queryParams.title" placeholder="请输入主题标题" clearable />
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-select v-model="queryParams.isPinned" placeholder="请选择" clearable>
            <el-option label="置顶" value="1" />
            <el-option label="普通" value="0" />
          </el-select>
        </el-form-item>
      </search-form>

      <!-- 表格区域 -->
      <data-table
        :data="topicList"
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
        <el-table-column label="主题ID" prop="topicId" width="80" align="center" />
        <el-table-column label="主题标题" prop="title" min-width="180" :show-overflow-tooltip="true" />
        <el-table-column label="发布者" prop="username" width="100" align="center" />
        <el-table-column label="浏览量" prop="viewCount" width="80" align="center" />
        <el-table-column label="回复数" prop="replyCount" width="80" align="center" />
        <el-table-column label="是否置顶" align="center" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isPinned == 1 ? 'danger' : 'info'" size="small">
              {{ scope.row.isPinned == 1 ? '置顶' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'normal' ? 'success' : 'info'" size="small">
              {{ scope.row.status === 'normal' ? '正常' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后回复时间" align="center" prop="lastReplyTime" width="150" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center" width="280" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              text
              @click="handleReplies(scope.row)"
              v-permission="'comm:reply:list'"
            >
              <el-icon><View /></el-icon>查看回复
            </el-button>
            <el-button
              type="success"
              size="small"
              text
              @click="handleUpdate(scope.row)"
              v-permission="'comm:topic:edit'"
            >
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button
              v-if="scope.row.isPinned == 0"
              type="warning"
              size="small"
              text
              @click="handlePin(scope.row, 1)"
              v-permission="'comm:topic:edit'"
            >
              <el-icon><Top /></el-icon>置顶
            </el-button>
            <el-button
              v-else
              type="info"
              size="small"
              text
              @click="handlePin(scope.row, 0)"
              v-permission="'comm:topic:edit'"
            >
              <el-icon><Bottom /></el-icon>取消置顶
            </el-button>
            <el-button
              type="danger"
              size="small"
              text
              @click="handleDelete(scope.row)"
              v-permission="'comm:topic:remove'"
            >
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>
    </el-card>

    <!-- 添加或修改主题对话框 -->
    <el-dialog 
      :model-value="open" 
      @update:model-value="open = $event" 
      :title="title" 
      width="500px" 
      append-to-body
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入主题标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入主题内容" />
        </el-form-item>
        <el-form-item label="是否置顶" prop="isPinned" v-if="form.topicId">
          <el-radio-group v-model="form.isPinned">
            <el-radio :label="1">置顶</el-radio>
            <el-radio :label="0">普通</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="form.topicId">
          <el-radio-group v-model="form.status">
            <el-radio label="normal">正常</el-radio>
            <el-radio label="hidden">隐藏</el-radio>
          </el-radio-group>
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
import { useRouter, useRoute } from 'vue-router'
import { listTopic, getTopic, createTopic, updateTopic, deleteTopic } from '@/api/discussion/topic'
import { getForum } from '@/api/discussion/forum'
import { Plus, Edit, Delete, View, Refresh, Top, Bottom, Back } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm/index.vue'
import DataTable from '@/components/DataTable/index.vue'

const router = useRouter()
const route = useRoute()

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
// 主题表格数据
const topicList = ref([])
// 弹出层标题
const title = ref("")
// 是否显示弹出层
const open = ref(false)
// 讨论区ID
const forumId = ref(null)
// 讨论区名称
const forumName = ref('')
// 表单引用
const formRef = ref(null)
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  forumId: null,
  title: undefined,
  isPinned: undefined
})
// 表单参数
const form = reactive({
  topicId: undefined,
  forumId: null,
  title: undefined,
  content: undefined,
  isPinned: 0,
  status: 'normal'
})
// 表单校验
const rules = {
  title: [
    { required: true, message: "主题标题不能为空", trigger: "blur" }
  ],
  content: [
    { required: true, message: "主题内容不能为空", trigger: "blur" }
  ]
}

// 生命周期钩子
onMounted(() => {
  const routeForumId = route.query.forumId
  if (routeForumId) {
    forumId.value = routeForumId
    queryParams.forumId = routeForumId
    getForumInfo(routeForumId)
    getList()
  }else{
    ElMessage.warning('请选择讨论区')
  }
})

// 获取讨论区信息
const getForumInfo = (id) => {
  getForum(id).then(response => {
    forumName.value = response.data.forumName
  })
}

// 查询主题列表
const getList = () => {
  loading.value = true
  listTopic(queryParams).then(response => {
    topicList.value = response.data.records
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
  queryParams.title = undefined
  queryParams.isPinned = undefined
  queryParams.pageNum = 1
  // 保留forumId查询参数
  queryParams.forumId = forumId.value
  getList()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.topicId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 返回讨论区列表
const backToForumList = () => {
  router.push({ path: '/interaction/forum' })
}

// 新增按钮操作
const handleCreate = () => {
  reset()
  form.forumId = forumId.value
  open.value = true
  title.value = "添加主题"
}

// 修改按钮操作
const handleUpdate = (row) => {
  reset()
  const topicId = row.topicId || ids.value[0]
  getTopic(topicId).then(response => {
    Object.assign(form, response.data)
    open.value = true
    title.value = "修改主题"
  })
}

// 置顶/取消置顶操作
const handlePin = (row, isPinned) => {
  const text = isPinned === 1 ? "置顶" : "取消置顶"
  ElMessageBox.confirm('确认要' + text + '主题"' + row.title + '"吗?', "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return updateTopic({
      topicId: row.topicId,
      isPinned: isPinned
    })
  }).then(() => {
    getList()
    ElMessage.success(text + "成功")
  }).catch(() => {})
}

// 查看回复按钮操作
const handleReplies = (row) => {
  router.push({ path: '/interaction/reply', query: { topicId: row.topicId } })
}

// 提交按钮
const submitForm = () => {
  formRef.value.validate(valid => {
    if (valid) {
      if (form.topicId != null) {
        updateTopic(form).then(response => {
          ElMessage.success("修改成功")
          open.value = false
          getList()
        })
      } else {
        createTopic(form).then(response => {
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
  const topicIds = row.topicId || ids.value
  ElMessageBox.confirm('是否确认删除主题编号为"' + topicIds + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return deleteTopic(topicIds)
  }).then(() => {
    getList()
    ElMessage.success("删除成功")
  }).catch(() => {})
}

// 表单重置
const reset = () => {
  form.topicId = undefined
  form.forumId = forumId.value
  form.title = undefined
  form.content = undefined
  form.isPinned = 0
  form.status = 'normal'
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}
</script>

<style scoped>
.topic-container {
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