<template>
  <div class="reply-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ topicTitle ? topicTitle + ' - 回复管理' : '回复管理' }}</span>
          <div class="header-actions">
            <el-button type="primary" plain @click="handleCreate" >
              <el-icon><Plus /></el-icon>添加回复
            </el-button>
            <el-button type="info" plain @click="backToTopicList">
              <el-icon><Back /></el-icon>返回主题
            </el-button>
            <el-button type="warning" plain @click="getList">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 主题内容显示区域 -->
      <div v-if="topicDetail" class="topic-detail">
        <h3>{{ topicDetail.title }}</h3>
        <div class="topic-info">
          <span>作者：{{ topicDetail.username }}</span>
          <span style="margin-left: 20px">发布时间：{{ topicDetail.createTime }}</span>
          <span style="margin-left: 20px">浏览量：{{ topicDetail.viewCount }}</span>
        </div>
        <div class="topic-content">{{ topicDetail.content }}</div>
      </div>
      
      <!-- 回复列表 -->
      <el-tabs v-model="activeTab" type="card">
        <el-tab-pane label="顶层回复" name="top">
          <data-table
            :data="replyList"
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
            <el-table-column label="回复ID" prop="replyId" width="80" align="center" />
            <el-table-column label="回复内容" prop="content" min-width="300" :show-overflow-tooltip="true" />
            <el-table-column label="回复者" prop="username" width="100" align="center" />
            <el-table-column label="回复时间" align="center" prop="createTime" width="150" :show-overflow-tooltip="true" />
            <el-table-column label="子回复数" align="center" width="80">
              <template #default="scope">
                <el-tag type="info" size="small">
                  {{ scope.row.childReplies ? scope.row.childReplies.length : 0 }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="250" fixed="right">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  text
                  @click="handleViewChildReplies(scope.row)"
                >
                  <el-icon><View /></el-icon>查看子回复
                </el-button>
                <el-button
                  type="success"
                  size="small"
                  text
                  @click="handleUpdate(scope.row)"
                  v-permission="'comm:reply:edit'"
                >
                  <el-icon><Edit /></el-icon>修改
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  text
                  @click="handleDelete(scope.row)"
                  v-permission="'comm:reply:remove'"
                >
                  <el-icon><Delete /></el-icon>删除
                </el-button>
              </template>
            </el-table-column>
          </data-table>
        </el-tab-pane>
        
        <el-tab-pane label="子回复" name="child" v-if="showChildTab">
          <div class="parent-reply">
            <div class="reply-info">
              <span class="username">{{ parentReply.username }}</span>
              <span class="time">{{ parentReply.createTime }}</span>
            </div>
            <div class="reply-content">{{ parentReply.content }}</div>
          </div>
          
          <el-divider content-position="center">子回复列表</el-divider>
          
          <data-table
            :data="childReplyList"
            :loading="childLoading"
            :show-pagination="false"
          >
            <el-table-column type="index" label="序号" width="50" align="center" />
            <el-table-column label="回复ID" prop="replyId" width="80" align="center" />
            <el-table-column label="回复内容" prop="content" min-width="300" :show-overflow-tooltip="true" />
            <el-table-column label="回复者" prop="username" width="100" align="center" />
            <el-table-column label="回复时间" align="center" prop="createTime" width="150" :show-overflow-tooltip="true" />
            <el-table-column label="操作" align="center" width="180" fixed="right">
              <template #default="scope">
                <el-button
                  type="success"
                  size="small"
                  text
                  @click="handleUpdate(scope.row)"
                  v-permission="'comm:reply:edit'"
                >
                  <el-icon><Edit /></el-icon>修改
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  text
                  @click="handleDelete(scope.row)"
                  v-permission="'comm:reply:remove'"
                >
                  <el-icon><Delete /></el-icon>删除
                </el-button>
              </template>
            </el-table-column>
          </data-table>
          
          <!-- 子回复添加按钮 -->
          <div class="child-reply-add">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              @click="handleCreateChildReply"
              v-permission="'comm:reply:add'"
            >
              <el-icon><Plus /></el-icon>添加子回复
            </el-button>
            <el-button
              type="info"
              plain
              @click="backToTopReplies"
            >
              <el-icon><Back /></el-icon>返回顶层回复
            </el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加或修改回复对话框 -->
    <el-dialog 
      :model-value="open" 
      @update:model-value="open = $event" 
      :title="title" 
      width="500px" 
      append-to-body
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="回复内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入回复内容" />
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
import { listReply, createReply, updateReply, deleteReply } from '@/api/discussion/reply'
import { getTopic } from '@/api/discussion/topic'
import { Plus, Edit, Delete, View, Refresh, Back } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm/index.vue'
import DataTable from '@/components/DataTable/index.vue'

const router = useRouter()
const route = useRoute()

// 遮罩层
const loading = ref(true)
const childLoading = ref(false)
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
// 回复表格数据
const replyList = ref([])
// 子回复列表
const childReplyList = ref([])
// 父回复对象
const parentReply = ref(null)
// 主题详情
const topicDetail = ref(null)
// 主题标题
const topicTitle = ref('')
// 弹出层标题
const title = ref("")
// 是否显示弹出层
const open = ref(false)
// 主题ID
const topicId = ref(null)
// 标签页
const activeTab = ref('top')
// 是否显示子回复标签页
const showChildTab = ref(false)
// 表单引用
const formRef = ref(null)
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  topicId: null,
  parentId: null
})
// 表单参数
const form = reactive({
  replyId: undefined,
  topicId: null,
  content: undefined,
  parentId: null
})
// 表单校验
const rules = {
  content: [
    { required: true, message: "回复内容不能为空", trigger: "blur" }
  ]
}

// 生命周期钩子
onMounted(() => {
  const routeTopicId = route.query.topicId
  if (routeTopicId) {
    topicId.value = routeTopicId
    queryParams.topicId = routeTopicId
    getTopicInfo(routeTopicId)
    getList()
  }
})

// 获取主题信息
const getTopicInfo = (id) => {
  getTopic(id).then(response => {
    topicDetail.value = response.data
    topicTitle.value = response.data.title
  })
}

// 查询回复列表
const getList = () => {
  loading.value = true
  listReply(topicId.value, queryParams).then(response => {
    replyList.value = response.data.records
    total.value = response.data.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

// 选择条目改变
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.replyId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 查看子回复
const handleViewChildReplies = (row) => {
  parentReply.value = row
  childReplyList.value = row.childReplies || []
  showChildTab.value = true
  activeTab.value = 'child'
}

// 返回顶层回复
const backToTopReplies = () => {
  activeTab.value = 'top'
  showChildTab.value = false
}

// 返回主题列表
const backToTopicList = () => {
  router.push({ 
    path: '/interaction/topic',
    query: { forumId: topicDetail.value ? topicDetail.value.forumId : null }
  })
}

// 新增按钮操作
const handleCreate = () => {
  reset()
  form.topicId = topicId.value
  form.parentId = null
  open.value = true
  title.value = "添加回复"
}

// 新增子回复按钮操作
const handleCreateChildReply = () => {
  reset()
  form.topicId = topicId.value
  form.parentId = parentReply.value.replyId
  open.value = true
  title.value = "添加子回复"
}

// 修改按钮操作
const handleUpdate = (row) => {
  reset()
  form.replyId = row.replyId
  form.content = row.content
  form.topicId = topicId.value
  form.parentId = row.parentId
  open.value = true
  title.value = "修改回复"
}

// 提交按钮
const submitForm = () => {
  formRef.value.validate(valid => {
    if (valid) {
      if (form.replyId != null) {
        updateReply(form).then(response => {
          ElMessage.success("修改成功")
          open.value = false
          if (activeTab.value === 'top') {
            getList()
          } else {
            // 刷新子回复列表
            refreshChildReplies()
          }
        })
      } else {
        createReply(form).then(response => {
          ElMessage.success("新增成功")
          open.value = false
          if (activeTab.value === 'top' || !form.parentId) {
            getList()
          } else {
            // 刷新子回复列表
            refreshChildReplies()
          }
        })
      }
    }
  })
}

// 刷新子回复列表
const refreshChildReplies = () => {
  const params = {
    topicId: topicId.value,
    parentId: parentReply.value.replyId
  }
  childLoading.value = true
  listReply(topicId.value, params).then(response => {
    parentReply.value.childReplies = response.data.recodes
    childReplyList.value = response.data.recodes
    childLoading.value = false
  }).catch(() => {
    childLoading.value = false
  })
}

// 删除按钮操作
const handleDelete = (row) => {
  const replyIds = row.replyId || ids.value
  ElMessageBox.confirm('是否确认删除回复编号为"' + replyIds + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return deleteReply(replyIds)
  }).then(() => {
    if (activeTab.value === 'top') {
      getList()
    } else {
      refreshChildReplies()
    }
    ElMessage.success("删除成功")
  }).catch(() => {})
}

// 表单重置
const reset = () => {
  form.replyId = undefined
  form.topicId = topicId.value
  form.content = undefined
  form.parentId = null
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}
</script>

<style scoped>
.reply-container {
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
.topic-detail {
  padding: 15px;
  margin-bottom: 20px;
  background: #f8f8f8;
  border-radius: 4px;
  border-left: 4px solid #409EFF;
}
.topic-info {
  margin: 10px 0;
  color: #666;
  font-size: 14px;
}
.topic-content {
  padding: 10px 0;
  line-height: 1.6;
  white-space: pre-wrap;
}
.parent-reply {
  padding: 15px;
  margin-bottom: 15px;
  background: #f8f8f8;
  border-radius: 4px;
  border-left: 4px solid #67C23A;
}
.reply-info {
  margin-bottom: 8px;
}
.reply-info .username {
  font-weight: bold;
  margin-right: 10px;
}
.reply-info .time {
  color: #888;
  font-size: 13px;
}
.reply-content {
  line-height: 1.6;
  white-space: pre-wrap;
}
.child-reply-add {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 15px;
}
.dialog-footer {
  text-align: right;
}
</style>