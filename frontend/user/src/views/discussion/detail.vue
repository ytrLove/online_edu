<template>
  <div class="discussion-detail">
    <div class="nav-breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/discussion' }">讨论区</el-breadcrumb-item>
        <el-breadcrumb-item v-if="topic.forumName">{{ topic.forumName }}</el-breadcrumb-item>
        <el-breadcrumb-item>话题详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="topic-detail-card" v-loading="loading">
      <!-- 主题详情 -->
      <div v-if="topic.topicId" class="topic-detail">
        <div class="topic-header">
          <div class="title-area">
            <h1 class="topic-title">{{ topic.title }}</h1>
            <div class="topic-tags">
              <el-tag v-if="topic.isPinned === 1" type="danger" effect="dark" size="small">置顶</el-tag>
              <el-tag v-if="topic.status === 'normal'" type="success" effect="dark" size="small">正常</el-tag>
            </div>
          </div>
          <div class="topic-meta">
            <div class="author-info">
              <el-avatar :size="40" :src="topic.avatar">{{ topic.username ? topic.username.charAt(0) : 'U' }}</el-avatar>
              <div class="author-detail">
                <div class="author-name">{{ topic.username || '匿名用户' }}</div>
                <div class="post-time">发布于 {{ formatDate(topic.createTime) }}</div>
              </div>
            </div>
            <div class="topic-stats">
              <span class="stat-item">
                <el-icon><View /></el-icon> {{ topic.viewCount || 0 }}
              </span>
              <span class="stat-item">
                <el-icon><ChatRound /></el-icon> {{ topic.replyCount || 0 }}
              </span>
            </div>
          </div>
        </div>
        <div class="topic-content">{{ topic.content }}</div>
        
        <div class="topic-actions">
          <el-button v-if="canEdit" type="primary" plain size="small" @click="editTopic">编辑</el-button>
          <el-button v-if="canDelete" type="danger" plain size="small" @click="confirmDeleteTopic">删除</el-button>
        </div>
      </div>
      
      <!-- 回复列表 -->
      <div class="reply-section">
        <div class="reply-header">
          <h2>全部回复 ({{ replyPagination.total || 0 }})</h2>
          <el-button type="primary" @click="showReplyForm = true">
            <el-icon><Plus /></el-icon> 添加回复
          </el-button>
        </div>
        
        <el-empty v-if="replyList.length === 0" description="暂无回复" />
        
        <div v-else class="reply-list">
          <div v-for="(reply, index) in replyList" :key="reply.replyId" class="reply-item" :id="`reply-${reply.replyId}`">
            <div class="reply-index">#{{ (replyPagination.currentPage - 1) * replyPagination.pageSize + index + 1 }}</div>
            <div class="reply-content-wrapper">
              <div class="reply-author-info">
                <el-avatar :size="40" :src="reply.avatar || defaultAvatar">
                  {{ reply.username ? reply.username.charAt(0).toUpperCase() : 'U' }}
                </el-avatar>
                <div class="reply-author-detail">
                  <div class="reply-author-name">{{ reply.username || '匿名用户' }}</div>
                  <div class="reply-time">{{ formatDateTime(reply.createTime) }}</div>
                </div>
              </div>
              <div class="reply-content markdown-body" v-html="renderContent(reply.content)"></div>
              <div class="reply-actions">
                <el-button v-if="canEditReply(reply)" type="text" size="small" @click="editReply(reply)">
                  <el-icon><Edit /></el-icon> 编辑
                </el-button>
                <el-button v-if="canDeleteReply(reply)" type="text" size="small" @click="confirmDeleteReply(reply)">
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
                <el-button type="text" size="small" @click="replyToReply(reply)">
                  <el-icon><ChatDotRound /></el-icon> 回复
                </el-button>
              </div>

              <!-- 嵌套回复 -->
              <div v-if="reply.childReplies && reply.childReplies.length > 0" class="child-replies">
                <div v-for="childReply in reply.childReplies" :key="childReply.replyId" class="child-reply-item">
                  <div class="reply-author-info">
                    <el-avatar :size="36" :src="childReply.avatar || defaultAvatar">
                      {{ childReply.username ? childReply.username.charAt(0).toUpperCase() : 'U' }}
                    </el-avatar>
                    <div class="reply-author-detail">
                      <div class="reply-author-name">{{ childReply.username || '匿名用户' }}</div>
                      <div class="reply-time">{{ formatDateTime(childReply.createTime) }}</div>
                    </div>
                  </div>
                  <div class="reply-content markdown-body" v-html="renderContent(childReply.content)"></div>
                  <div class="reply-actions">
                    <el-button v-if="canEditReply(childReply)" type="text" size="small" @click="editReply(childReply)">
                      <el-icon><Edit /></el-icon> 编辑
                    </el-button>
                    <el-button v-if="canDeleteReply(childReply)" type="text" size="small" @click="confirmDeleteReply(childReply)">
                      <el-icon><Delete /></el-icon> 删除
                    </el-button>
                    <el-button type="text" size="small" @click="replyToReply(reply, childReply)">
                      <el-icon><ChatDotRound /></el-icon> 回复
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 分页 -->
        <div class="pagination-container" v-if="replyPagination.total > 0">
          <el-pagination
            :current-page="replyPagination.currentPage"
            :page-sizes="[10, 20, 30, 50]"
            :page-size="replyPagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="replyPagination.total"
            @size-change="handleReplySizeChange"
            @current-change="handleReplyPageChange"
          />
        </div>
      </div>
      
      <!-- 回复表单 -->
      <el-card v-if="showReplyForm" class="reply-form-card">
        <div class="reply-form-header">
          <h3>{{ replyFormTitle }}</h3>
          <el-button type="text" @click="cancelReply">取消</el-button>
        </div>
        <el-form
          ref="replyFormRef"
          :model="replyForm"
          :rules="replyRules"
          class="reply-form"
        >
          <el-form-item prop="content">
            <el-input
              v-model="replyForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入回复内容"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitReply" :loading="submitting">提交回复</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, View, ChatRound, ChatDotRound, Edit, Delete } from '@element-plus/icons-vue'
import { getTopicById, updateTopic, deleteTopic } from '@/api/discussion'
import { getReplyList, createReply, updateReply, deleteReply } from '@/api/discussion'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const router = useRouter()
const route = useRoute()

// 数据状态
const loading = ref(false)
const submitting = ref(false)
const showReplyForm = ref(false)
const replyFormRef = ref(null)
const editingReply = ref(null)
const topicId = ref(null)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 主题详情
const topic = ref({})

// 回复列表
const replyList = ref([])

// 回复分页参数
const replyPagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 回复表单
const replyForm = reactive({
  content: '',
  topicId: null,
  parentId: null,
  replyToUser: null
})

// 回复表单规则
const replyRules = {
  content: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 3, message: '回复内容不能少于3个字符', trigger: 'blur' }
  ]
}

// 计算回复表单标题
const replyFormTitle = computed(() => {
  if (editingReply.value) {
    return '编辑回复'
  } else if (replyForm.parentId) {
    const parentReply = replyList.value.find(r => r.replyId === replyForm.parentId)
    return `回复 ${replyForm.replyToUser || parentReply?.username || '用户'} 的评论`
  } else {
    return '添加回复'
  }
})

// 是否可以编辑主题
const canEdit = computed(() => {
  // 判断当前用户是否是主题作者或管理员
  // 实际项目中应该根据用户权限判断
  return true
})

// 是否可以删除主题
const canDelete = computed(() => {
  // 判断当前用户是否是主题作者或管理员
  // 实际项目中应该根据用户权限判断
  return true
})

// 生命周期钩子
onMounted(async () => {
  topicId.value = route.params.id
  if (topicId.value) {
    await fetchTopicDetail()
    await fetchReplyList()
  } else {
    ElMessage.error('无效的主题ID')
    router.push('/discussion')
  }
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 获取主题详情
const fetchTopicDetail = async () => {
  loading.value = true
  try {
    const result = await getTopicById(topicId.value)
    if (result.code === 200) {
      topic.value = result.data
      // 设置回复表单中的主题ID
      replyForm.topicId = topicId.value
    } else {
      ElMessage.error(result.message || '获取主题详情失败')
    }
  } catch (error) {
    console.error('获取主题详情失败', error)
    ElMessage.error('获取主题详情失败')
  } finally {
    loading.value = false
  }
}

// 获取回复列表
const fetchReplyList = async () => {
  loading.value = true
  try {
    const params = {
      current: replyPagination.currentPage,
      size: replyPagination.pageSize
    }
    
    const result = await getReplyList(topicId.value, params)
    if (result.code === 200) {
      replyList.value = result.data.records || []
      replyPagination.total = result.data.total || 0
      replyPagination.currentPage = result.data.current || 1
      replyPagination.pageSize = result.data.size || 20
    } else {
      ElMessage.error(result.message || '获取回复列表失败')
    }
  } catch (error) {
    console.error('获取回复列表失败', error)
    ElMessage.error('获取回复列表失败')
  } finally {
    loading.value = false
  }
}

// 处理回复分页大小变化
const handleReplySizeChange = (size) => {
  replyPagination.pageSize = size
  fetchReplyList()
}

// 处理回复页码变化
const handleReplyPageChange = (page) => {
  replyPagination.currentPage = page
  fetchReplyList()
}

// 编辑主题
const editTopic = () => {
  // 跳转到编辑页面或显示编辑对话框
  ElMessage.info('编辑主题功能待实现')
}

// 确认删除主题
const confirmDeleteTopic = () => {
  ElMessageBox.confirm(
    '确定要删除该主题吗？删除后将无法恢复。',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(() => {
      deleteTopic(topicId.value)
        .then(() => {
          ElMessage.success('主题已删除')
          router.push('/discussion')
        })
        .catch(error => {
          console.error('删除主题失败', error)
          ElMessage.error('删除主题失败')
        })
    })
    .catch(() => {
      // 取消删除操作
    })
}

// 判断是否可以编辑回复
const canEditReply = (reply) => {
  // 判断当前用户是否是回复作者或管理员
  // 实际项目中应该根据用户权限判断
  return true
}

// 判断是否可以删除回复
const canDeleteReply = (reply) => {
  // 判断当前用户是否是回复作者或管理员
  // 实际项目中应该根据用户权限判断
  return true
}

// 编辑回复
const editReply = (reply) => {
  editingReply.value = reply
  replyForm.content = reply.content
  replyForm.parentId = reply.parentId
  showReplyForm.value = true
}

// 确认删除回复
const confirmDeleteReply = (reply) => {
  ElMessageBox.confirm(
    '确定要删除该回复吗？删除后将无法恢复。',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(() => {
      deleteReply(reply.replyId)
        .then(() => {
          ElMessage.success('回复已删除')
          fetchReplyList()
        })
        .catch(error => {
          console.error('删除回复失败', error)
          ElMessage.error('删除回复失败')
        })
    })
    .catch(() => {
      // 取消删除操作
    })
}

// 回复他人的评论
const replyToReply = (parentReply, childReply = null) => {
  replyForm.parentId = parentReply.replyId
  
  if (childReply) {
    // 如果是回复子回复，则显示"回复xxx"的文字提示
    replyForm.replyToUser = childReply.username
  } else {
    replyForm.replyToUser = parentReply.username
  }
  
  showReplyForm.value = true
  
  // 添加回复预填文本
  replyForm.content = `@${replyForm.replyToUser} `
  
  // 平滑滚动到回复表单
  setTimeout(() => {
    const formElement = document.querySelector('.reply-form-card')
    if (formElement) {
      formElement.scrollIntoView({ behavior: 'smooth' })
    }
  }, 100)
}

// 取消回复
const cancelReply = () => {
  showReplyForm.value = false
  editingReply.value = null
  replyForm.content = ''
  replyForm.parentId = null
  replyForm.replyToUser = null
}

// 提交回复
const submitReply = () => {
  replyFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    
    submitting.value = true
    try {
      if (editingReply.value) {
        // 更新回复
        const updateData = {
          replyId: editingReply.value.replyId,
          content: replyForm.content
        }
        await updateReply(updateData)
        ElMessage.success('回复已更新')
      } else {
        // 创建新回复
        await createReply({
          topicId: replyForm.topicId,
          content: replyForm.content,
          parentId: replyForm.parentId
        })
        ElMessage.success('回复已发布')
      }
      
      // 重置表单
      showReplyForm.value = false
      editingReply.value = null
      replyForm.content = ''
      replyForm.parentId = null
      replyForm.replyToUser = null
      
      // 刷新回复列表
      await fetchReplyList()
      
      // 刷新主题详情以更新回复数
      await fetchTopicDetail()
    } catch (error) {
      console.error('提交回复失败', error)
      ElMessage.error('提交回复失败')
    } finally {
      submitting.value = false
    }
  })
}

// 获取话题类型标签颜色
const getTypeTagColor = (type) => {
  const typeColorMap = {
    'question': 'warning',
    'discussion': 'info',
    'share': 'success',
    'notice': 'danger'
  }
  return typeColorMap[type] || 'info'
}

// 获取话题类型文本
const getTypeLabel = (type) => {
  const typeLabelMap = {
    'question': '问答',
    'discussion': '讨论',
    'share': '分享',
    'notice': '通知'
  }
  return typeLabelMap[type] || '未知'
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
  
  // 如果是今天发布的
  if (diffDays === 0) {
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `今天 ${hours}:${minutes}`
  } 
  // 如果是昨天发布的
  else if (diffDays === 1) {
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `昨天 ${hours}:${minutes}`
  }
  // 如果是最近7天发布的
  else if (diffDays < 7) {
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    const weekday = weekdays[date.getDay()]
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `${weekday} ${hours}:${minutes}`
  }
  // 如果是今年发布的
  else {
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const day = date.getDate().toString().padStart(2, '0')
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `${month}-${day} ${hours}:${minutes}`
  }
}

// 渲染内容（支持markdown和@用户标记）
const renderContent = (content) => {
  if (!content) return ''
  
  // 处理@用户标记
  const contentWithUserLinks = content.replace(/@(\w+)/g, '<span class="mention-user">@$1</span>')
  
  // 使用marked渲染Markdown
  let html = marked(contentWithUserLinks)
  
  // 使用DOMPurify清洁HTML，防止XSS攻击
  html = DOMPurify.sanitize(html)
  
  return html
}
</script>

<style scoped>
.discussion-detail {
  padding: 20px;
}

.nav-breadcrumb {
  margin-bottom: 20px;
}

.topic-detail-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.topic-header {
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 20px;
}

.title-area {
  margin-bottom: 15px;
}

.topic-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.topic-tags {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.topic-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-detail {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: bold;
  color: #409eff;
}

.post-time {
  font-size: 12px;
  color: #909399;
}

.topic-stats {
  display: flex;
  gap: 15px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
}

.topic-content {
  font-size: 16px;
  line-height: 1.8;
  padding: 10px 0;
  white-space: pre-line;
}

.topic-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.reply-section {
  margin-top: 30px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
}

.reply-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.reply-list {
  margin-bottom: 20px;
}

.reply-item {
  display: flex;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  position: relative;
}

.reply-item:target {
  background-color: #f8f9fa;
  border-radius: 4px;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.reply-index {
  font-size: 14px;
  color: #909399;
  margin-right: 15px;
  min-width: 30px;
  text-align: center;
}

.reply-content-wrapper {
  flex: 1;
}

.reply-author-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.reply-author-detail {
  margin-left: 10px;
}

.reply-author-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

.reply-content {
  margin-bottom: 12px;
  line-height: 1.6;
  color: #303133;
  word-break: break-word;
}

.reply-actions {
  display: flex;
  gap: 10px;
}

.child-replies {
  margin-top: 15px;
  margin-left: 20px;
  padding: 15px;
  background-color: #f9fafc;
  border-radius: 4px;
  border-left: 3px solid #ebeef5;
}

.child-reply-item {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #ebeef5;
}

.child-reply-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.mention-user {
  color: #409eff;
  font-weight: 500;
}

/* Markdown样式 */
.markdown-body {
  font-size: 14px;
  line-height: 1.6;
}

.markdown-body p {
  margin-bottom: 8px;
}

.markdown-body ul, .markdown-body ol {
  margin-bottom: 8px;
  padding-left: 20px;
}

.markdown-body code {
  background-color: #f5f7fa;
  padding: 2px 4px;
  border-radius: 2px;
  font-family: monospace;
}

.markdown-body pre {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  margin-bottom: 8px;
}

.markdown-body blockquote {
  border-left: 4px solid #ebeef5;
  padding-left: 12px;
  color: #606266;
  margin-bottom: 8px;
}

.markdown-body a {
  color: #409eff;
  text-decoration: none;
}

.markdown-body a:hover {
  text-decoration: underline;
}

/* 回复表单样式 */
.reply-form-card {
  margin-top: 20px;
}

.reply-form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.reply-form-header h3 {
  font-size: 16px;
  margin: 0;
  color: #303133;
}

.reply-form {
  margin-top: 10px;
}
</style> 