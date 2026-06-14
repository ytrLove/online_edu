<template>
  <div class="discussion-container">
    <!-- 论坛列表视图 -->
    <div v-if="viewMode === 'forum'" class="forum-view">
      <!-- 页面标题区域 -->
      <div class="page-header">
        <div class="header-content">
          <div class="header-text">
            <h1 class="page-title">学习讨论区</h1>
            <p class="page-subtitle">与同学们一起讨论学习问题，分享学习经验</p>
          </div>
          <div class="header-decoration">
            <div class="decoration-icon">
              <el-icon><ChatDotRound /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 论坛列表卡片 -->
      <div class="forum-section">
        <div class="section-header">
          <div class="header-info">
            <h2 class="section-title">讨论区列表</h2>
            <p class="section-subtitle">选择感兴趣的讨论区开始交流</p>
          </div>
          <div class="forum-stats">
            <div class="stat-item">
              <span class="stat-number">{{ forumList.length }}</span>
              <span class="stat-label">个讨论区</span>
            </div>
          </div>
        </div>
        
        <div class="forum-list" v-loading="loading">
          <div v-if="forumList.length === 0" class="empty-container">
            <div class="empty-state">
              <div class="empty-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <h3>暂无讨论区</h3>
              <p>当前还没有可用的讨论区</p>
            </div>
          </div>
          
          <div v-else class="forum-grid">
            <div v-for="forum in forumList" :key="forum.forumId" class="forum-card-wrapper">
              <div class="forum-card hover-lift" @click="enterForum(forum.forumId)">
                <div class="forum-header">
                  <div class="forum-icon">
                    <el-icon><ChatDotRound /></el-icon>
                  </div>
                  <div class="forum-badges">
                    <el-tag v-if="!forum.isPublic" type="warning" size="small" effect="light" class="privacy-badge">
                      <span class="badge-icon">🔒</span>
                      私密
                    </el-tag>
                    <el-tag type="success" size="small" effect="light" class="status-badge">
                      <span class="badge-icon">✅</span>
                      开放
                    </el-tag>
                  </div>
                </div>
                
                <div class="forum-content">
                  <h3 class="forum-title">{{ forum.forumName }}</h3>
                  <p class="forum-description">{{ forum.description }}</p>
                  
                  <div class="forum-meta">
                    <div class="meta-item">
                      <el-icon class="meta-icon"><Reading /></el-icon>
                      <span class="meta-label">所属课程:</span>
                      <span class="meta-value">{{ forum.courseName }}</span>
                    </div>
                    <div v-if="forum.createTime" class="meta-item">
                      <el-icon class="meta-icon"><Calendar /></el-icon>
                      <span class="meta-label">创建时间:</span>
                      <span class="meta-value">{{ formatDate(forum.createTime) }}</span>
                    </div>
                  </div>
                </div>
                
                <div class="forum-footer">
                  <div class="forum-stats">
                    <div class="stat-item">
                      <el-icon><User /></el-icon>
                      <span>{{ forum.memberCount || 0 }}人</span>
                    </div>
                    <div class="stat-item">
                      <el-icon><ChatRound /></el-icon>
                      <span>{{ forum.topicCount || 0 }}话题</span>
                    </div>
                  </div>
                  <div class="enter-btn">
                    <el-button type="primary" size="small">
                      <el-icon><Right /></el-icon>
                      进入讨论
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div class="pagination-container" v-if="forumList.length > 0">
            <el-pagination
              :current-page="forumPagination.current"
              :page-sizes="[10, 20, 30, 50]"
              :page-size="forumPagination.size"
              layout="total, sizes, prev, pager, next, jumper"
              :total="forumPagination.total"
              @size-change="handleForumSizeChange"
              @current-change="handleForumPageChange"
              background
            />
          </div>
        </div>
      </div>
    </div>
    <!-- 话题列表视图 -->
    <div v-if="viewMode === 'topic'" class="topic-view">
      <!-- 话题视图头部 -->
      <div class="topic-header">
        <div class="header-content">
          <div class="header-left">
            <el-button class="back-btn" @click="backToForumList">
              <el-icon><ArrowLeft /></el-icon>
              返回列表
            </el-button>
            <div class="current-forum" v-if="currentForum">
              <h2 class="forum-name">
                <span class="forum-icon">💬</span>
                {{ currentForum.forumName }}
              </h2>
              <p class="forum-desc">{{ currentForum.description }}</p>
            </div>
          </div>
          <div class="header-actions">
            <el-button type="primary" class="create-btn" @click="createTopic">
              <el-icon><Plus /></el-icon>
              发起讨论
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-card">
          <div class="search-header">
            <div class="search-title">
              <h3>筛选话题</h3>
              <p>按类型和关键词查找感兴趣的讨论内容</p>
            </div>
          </div>
          
          <el-form :model="searchForm" class="search-form">
            <div class="search-inputs">
              <div class="input-group">
                <div class="input-wrapper">
                  <div class="input-icon">
                    <el-icon><Filter /></el-icon>
                  </div>
                  <el-select
                    v-model="searchForm.type"
                    placeholder="选择话题类型"
                    clearable
                    class="search-select"
                  >
                    <el-option
                      v-for="item in typeOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </div>
                
                <div class="input-wrapper">
                  <div class="input-icon">
                    <el-icon><Search /></el-icon>
                  </div>
                  <el-input
                    v-model="searchForm.keyword"
                    placeholder="搜索话题关键词..."
                    clearable
                    class="search-input"
                    @keyup.enter="searchTopics"
                  />
                </div>
                
                <div class="search-actions">
                  <el-button type="primary" class="search-btn" @click="searchTopics">
                    <el-icon><Search /></el-icon>
                    搜索
                  </el-button>
                  <el-button class="reset-btn" @click="resetForm">
                    <el-icon><Refresh /></el-icon>
                    重置
                  </el-button>
                </div>
              </div>
            </div>
          </el-form>
        </div>
      </div>

      <!-- 话题类型筛选标签 -->
      <div class="topic-tabs-section">
        <div class="tabs-header">
          <h3>话题分类</h3>
        </div>
        <div class="topic-tabs">
          <div
            class="tab-item"
            :class="{ active: activeTabs === 'all' }"
            @click="activeTabs = 'all'; handleTabClick()"
          >
            <span class="tab-icon">📋</span>
            <span class="tab-text">全部话题</span>
          </div>
          <div
            class="tab-item"
            :class="{ active: activeTabs === 'hot' }"
            @click="activeTabs = 'hot'; handleTabClick()"
          >
            <span class="tab-icon">🔥</span>
            <span class="tab-text">热门话题</span>
          </div>
          <div
            class="tab-item"
            :class="{ active: activeTabs === 'latest' }"
            @click="activeTabs = 'latest'; handleTabClick()"
          >
            <span class="tab-icon">✨</span>
            <span class="tab-text">最新回复</span>
          </div>
          <div
            class="tab-item"
            :class="{ active: activeTabs === 'my' }"
            @click="activeTabs = 'my'; handleTabClick()"
          >
            <span class="tab-icon">👤</span>
            <span class="tab-text">我的话题</span>
          </div>
        </div>
      </div>

      <!-- 话题列表 -->
      <div class="topic-list" v-loading="loading">
        <div v-if="topicList.length === 0" class="empty-container">
          <div class="empty-state">
            <div class="empty-icon">
              <el-icon><ChatRound /></el-icon>
            </div>
            <h3>暂无讨论话题</h3>
            <p>暂时没有相关的讨论内容，不如发起一个新话题吧</p>
            <el-button type="primary" @click="createTopic">
              发起讨论
            </el-button>
          </div>
        </div>

        <div v-else class="topic-grid">
          <div
            v-for="topic in topicList"
            :key="topic.topicId"
            class="topic-card-wrapper"
          >
            <div class="topic-card hover-lift" @click="viewTopic(topic.topicId)">
              <!-- 话题头部 -->
              <div class="topic-header">
                <div class="topic-author">
                  <el-avatar :size="45" :src="topic.avatar" class="author-avatar">
                    {{ topic.username ? topic.username.charAt(0) : 'U' }}
                  </el-avatar>
                  <div class="author-info">
                    <span class="author-name">{{ topic.username || '匿名用户' }}</span>
                    <span class="publish-time">{{ formatDate(topic.createTime) }}</span>
                  </div>
                </div>
                <div class="topic-badges">
                  <el-tag v-if="topic.isPinned === 1" type="danger" size="small" effect="dark" class="pin-badge">
                    <span class="badge-icon">📍</span>
                    置顶
                  </el-tag>
                  <el-tag :type="getTypeTagColor(topic.type)" size="small" effect="light" class="type-badge">
                    {{ getTypeLabel(topic.type) }}
                  </el-tag>
                </div>
              </div>
              
              <!-- 话题内容 -->
              <div class="topic-content">
                <h3 class="topic-title">{{ topic.title }}</h3>
                <p class="topic-preview">{{ topic.content }}</p>
              </div>
              
              <!-- 话题底部 -->
              <div class="topic-footer">
                <div class="topic-stats">
                  <div class="stat-item">
                    <el-icon><View /></el-icon>
                    <span>{{ topic.viewCount || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <el-icon><ChatRound /></el-icon>
                    <span>{{ topic.replyCount || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <el-icon><Star /></el-icon>
                    <span>{{ topic.likeCount || 0 }}</span>
                  </div>
                </div>
                
                <div v-if="topic.lastReplyTime" class="last-reply">
                  <div class="reply-info">
                    <span class="reply-label">最后回复:</span>
                    <span class="reply-time">{{ formatDate(topic.lastReplyTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container" v-if="topicList.length > 0">
          <el-pagination
            :current-page="topicPagination.current"
            :page-sizes="[10, 20, 30, 50]"
            :page-size="topicPagination.size"
            layout="total, sizes, prev, pager, next, jumper"
            :total="topicPagination.total"
            @size-change="handleTopicSizeChange"
            @current-change="handleTopicPageChange"
            background
          />
        </div>
      </div>
    </div>

    <!-- 创建话题对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="发起新讨论"
      width="70%"
      :before-close="handleDialogClose"
      class="create-topic-dialog"
    >
      <div class="dialog-content">
        <div class="dialog-header">
          <div class="header-icon">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="header-text">
            <h3>创建一个新的讨论话题</h3>
            <p>分享您的想法，与大家一起交流学习</p>
          </div>
        </div>
        
        <el-form
          ref="topicFormRef"
          :model="topicForm"
          :rules="topicRules"
          label-width="100px"
          class="topic-form"
        >
          <el-form-item label="话题标题" prop="title">
            <div class="form-item-wrapper">
              <div class="input-icon">
                <el-icon><Edit /></el-icon>
              </div>
              <el-input 
                v-model="topicForm.title" 
                placeholder="请输入吸引人的讨论标题（5-50个字符）"
                class="title-input"
              />
            </div>
          </el-form-item>

          <el-form-item label="话题类型" prop="type">
            <div class="form-item-wrapper">
              <div class="input-icon">
                <el-icon><Flag /></el-icon>
              </div>
              <el-select 
                v-model="topicForm.type" 
                placeholder="请选择讨论类型" 
                class="type-select"
              >
                <el-option
                  v-for="item in typeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                  <div class="option-content">
                    <span class="option-icon">{{ getTypeIcon(item.value) }}</span>
                    <span class="option-label">{{ item.label }}</span>
                  </div>
                </el-option>
              </el-select>
            </div>
          </el-form-item>

          <el-form-item label="讨论内容" prop="content">
            <div class="form-item-wrapper">
              <div class="textarea-icon">
                <el-icon><Document /></el-icon>
              </div>
              <el-input
                v-model="topicForm.content"
                type="textarea"
                :rows="8"
                placeholder="请详细描述您的问题或想法，让大家更好地理解和参与讨论..."
                class="content-textarea"
              />
            </div>
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="handleDialogClose">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
          <el-button type="primary" class="submit-btn" @click="submitTopic" :loading="submitting">
            <el-icon v-if="!submitting"><Check /></el-icon>
            {{ submitting ? '发布中...' : '发布讨论' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, View, ChatRound, Star, Document, ChatDotRound, ArrowLeft, Reading, Calendar, User, Right, Filter, Search, Refresh, Edit, Flag, Close, Check } from '@element-plus/icons-vue'
import { 
  getForumList, 
  getForumById, 
  getTopicList, 
  getTopicById, 
  createTopic as createTopicApi 
} from '@/api/discussion'

const router = useRouter()
const route = useRoute()

// 数据状态
const loading = ref(false)
const submitting = ref(false)
const createDialogVisible = ref(false)
const activeTabs = ref('all')
const topicFormRef = ref(null)
const viewMode = ref('forum') // 'forum' 或 'topic'
const currentForumId = ref(null)
const currentForum = ref(null)

// 话题类型选项
const typeOptions = [
  { value: 'question', label: '问答' },
  { value: 'discussion', label: '讨论' },
  { value: 'share', label: '分享' },
  { value: 'notice', label: '通知' }
]

// 搜索表单
const searchForm = reactive({
  keyword: '',
  type: ''
})

// 话题表单
const topicForm = reactive({
  title: '',
  content: '',
  type: 'question',
  forumId: null
})

// 表单校验规则
const topicRules = {
  title: [
    { required: true, message: '请输入讨论标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度在5到50个字符之间', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择讨论类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入讨论内容', trigger: 'blur' },
    { min: 10, message: '内容不能少于10个字符', trigger: 'blur' }
  ]
}

// 论坛列表
const forumList = ref([])

// 话题列表
const topicList = ref([])

// 论坛分页参数
const forumPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 话题分页参数
const topicPagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 生命周期钩子
onMounted(async () => {
  // 检查是否有论坛ID参数
  const forumId = route.query.forumId
  if (forumId) {
    await enterForum(forumId)
  } else {
    // 加载论坛列表
    await fetchForumList()
  }
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 获取论坛列表
const fetchForumList = async () => {
  loading.value = true
  try {
    const params = {
      current: forumPagination.current,
      size: forumPagination.size
    }
    
    const result = await getForumList(params)
    if (result.code === 200) {
      forumList.value = result.data.records || []
      forumPagination.total = result.data.total || 0
    } else {
      ElMessage.error(result.message || '获取讨论区列表失败')
    }
  } catch (error) {
    console.error('获取讨论区列表失败', error)
    ElMessage.error('获取讨论区列表失败')
  } finally {
    loading.value = false
  }
}

// 进入论坛
const enterForum = (forumId) => {
  viewMode.value = 'topic'
  currentForumId.value = forumId
  currentForum.value = forumList.value.find(f => f.forumId === forumId)
  // 重置话题分页
  topicPagination.current = 1
  topicPagination.size = 10
  // 重置搜索表单
  searchForm.keyword = ''
  searchForm.type = ''
  fetchTopicList()
}

// 返回论坛列表
const backToForumList = () => {
  viewMode.value = 'forum'
  currentForumId.value = null
  currentForum.value = null
  topicForm.forumId = null
}

// 获取话题列表
const fetchTopicList = async () => {
  try {
    loading.value = true
    const params = {
      forumId: currentForumId.value,
      current: topicPagination.current,
      size: topicPagination.size,
      keyword: searchForm.keyword,
      type: searchForm.type
    }
    const res = await getTopicList(params)
    if (res.code === 200) {
      topicList.value = res.data.records
      topicPagination.total = res.data.total
      topicPagination.current = res.data.current
      topicPagination.size = res.data.size
    } else {
      ElMessage.error(res.message || '获取话题列表失败')
    }
  } catch (error) {
    console.error('获取话题列表失败:', error)
    ElMessage.error('获取话题列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索话题
const searchTopics = () => {
  topicPagination.current = 1
  fetchTopicList()
}

// 重置搜索表单
const resetForm = () => {
  searchForm.keyword = ''
  searchForm.type = ''
  searchTopics()
}

// 处理标签页点击
const handleTabClick = () => {
  topicPagination.current = 1
  fetchTopicList()
}

// 处理论坛分页大小变化
const handleForumSizeChange = (size) => {
  forumPagination.size = size
  fetchForumList()
}

// 处理论坛页码变化
const handleForumPageChange = (page) => {
  forumPagination.current = page
  fetchForumList()
}

// 处理话题分页大小变化
const handleTopicSizeChange = (size) => {
  topicPagination.size = size
  fetchTopicList()
}

// 处理话题页码变化
const handleTopicPageChange = (page) => {
  topicPagination.current = page
  fetchTopicList()
}

// 查看话题详情
const viewTopic = (id) => {
  router.push(`/discussion/detail/${id}`)
}

// 创建新话题
const createTopic = () => {
  // 重置表单
  topicForm.title = ''
  topicForm.content = ''
  topicForm.type = 'question'
  // 保留当前论坛ID
  topicForm.forumId = currentForumId.value
  
  // 显示对话框
  createDialogVisible.value = true
}

// 关闭对话框
const handleDialogClose = () => {
  ElMessageBox.confirm(
    '确定要取消创建吗？已编辑的内容将会丢失。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(() => {
      createDialogVisible.value = false
    })
    .catch(() => {
      // 取消关闭，不做任何处理
    })
}

// 提交话题
const submitTopic = () => {
  topicFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    
    submitting.value = true
    try {
      await createTopicApi(topicForm)
      ElMessage.success('话题创建成功')
      createDialogVisible.value = false
      
      // 刷新列表
      if (activeTabs.value !== 'my') {
        activeTabs.value = 'my'
      }
      await fetchTopicList()
    } catch (error) {
      console.error('创建话题失败', error)
      ElMessage.error('创建话题失败')
    } finally {
      submitting.value = false
    }
  })
}

// 获取话题类型图标
const getTypeIcon = (type) => {
  const iconMap = {
    'question': '❓',
    'discussion': '💬', 
    'share': '📤',
    'notice': '📢'
  }
  return iconMap[type] || '💬'
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
</script>

<style lang="scss" scoped>
.discussion-container {
  padding: var(--container-padding);
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 140px);
}

/* 页面标题区域 */
.page-header {
  margin-bottom: 32px;
}

.header-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: var(--vt-radius-large);
  padding: 40px;
  color: white;
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--vt-shadow-3);
}

.header-text {
  flex: 1;
  z-index: 2;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
}

.page-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
  text-shadow: 0 1px 10px rgba(0, 0, 0, 0.2);
}

.header-decoration {
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.header-decoration .decoration-icon {
  font-size: 48px;
  opacity: 0.8;
}

/* 论坛列表区域 */
.forum-section {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 32px;
  box-shadow: var(--vt-shadow-2);
  border: 1px solid var(--color-border-soft);
  backdrop-filter: blur(20px);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 2px solid var(--color-border-soft);
}

.header-info h2 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: var(--color-text);
}

.header-info p {
  margin: 0;
  color: var(--color-text-soft);
  font-size: 14px;
}

.forum-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 16px 20px;
  background: var(--vt-gradient-primary);
  border-radius: var(--vt-radius-small);
  color: white;
  box-shadow: var(--vt-shadow-1);
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
}

/* 论坛网格 */
.forum-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 24px;
}

.forum-card {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 24px;
  border: 1px solid var(--color-border-soft);
  transition: all var(--vt-transition-fast) ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.forum-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--vt-gradient-primary);
  opacity: 0;
  transition: opacity var(--vt-transition-fast) ease;
}

.forum-card.hover-lift:hover {
  transform: translateY(-8px);
  box-shadow: var(--vt-shadow-3);
  border-color: var(--vt-c-primary-light);
}

.forum-card.hover-lift:hover::before {
  opacity: 1;
}

.forum-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.forum-icon {
  width: 60px;
  height: 60px;
  background: var(--vt-gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  box-shadow: var(--vt-shadow-2);
}

.forum-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.privacy-badge, .status-badge {
  display: flex !important;
  align-items: center !important;
  gap: 4px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
}

.badge-icon {
  font-size: 12px;
}

.forum-content {
  flex: 1;
  margin-bottom: 20px;
}

.forum-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.forum-description {
  color: var(--color-text-soft);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
}

.forum-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.meta-icon {
  color: var(--vt-c-primary);
  font-size: 16px;
}

.meta-label {
  color: var(--color-text-soft);
}

.meta-value {
  color: var(--color-text);
  font-weight: 500;
}

.forum-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--color-border-soft);
}

.forum-stats {
  display: flex;
  gap: 16px;
}

.forum-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--color-text-soft);
  background: none;
  padding: 0;
  box-shadow: none;
}

.enter-btn .el-button {
  padding: 8px 16px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.enter-btn .el-button:hover {
  transform: translateY(-2px) !important;
  box-shadow: var(--vt-shadow-2) !important;
}

/* 话题视图 */
.topic-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 话题头部 */
.topic-header {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 24px;
  box-shadow: var(--vt-shadow-2);
  border: 1px solid var(--color-border-soft);
  backdrop-filter: blur(20px);
}

.topic-header .header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.header-left {
  flex: 1;
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.back-btn {
  background: var(--color-background) !important;
  border: 2px solid var(--color-border-soft) !important;
  color: var(--color-text) !important;
  padding: 10px 20px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.back-btn:hover {
  border-color: var(--vt-c-primary) !important;
  color: var(--vt-c-primary) !important;
  transform: translateY(-2px) !important;
}

.current-forum {
  flex: 1;
}

.forum-name {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.forum-icon {
  font-size: 20px;
}

.forum-desc {
  margin: 0;
  color: var(--color-text-soft);
  font-size: 14px;
  line-height: 1.6;
}

.create-btn {
  background: var(--vt-gradient-primary) !important;
  border: none !important;
  color: white !important;
  padding: 12px 24px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  box-shadow: var(--vt-shadow-1) !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.create-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: var(--vt-shadow-2) !important;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 24px;
}

.search-card {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 32px;
  box-shadow: var(--vt-shadow-2);
  border: 1px solid var(--color-border-soft);
  backdrop-filter: blur(20px);
}

.search-header {
  margin-bottom: 24px;
}

.search-title h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: var(--color-text);
}

.search-title p {
  margin: 0;
  color: var(--color-text-soft);
  font-size: 14px;
}

.search-inputs {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  align-items: end;
}

.input-wrapper {
  position: relative;
  flex: 1;
  min-width: 200px;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-soft);
  z-index: 2;
  font-size: 18px;
}

.search-select, .search-input {
  width: 100%;
}

:deep(.search-select .el-input__wrapper), 
:deep(.search-input .el-input__wrapper) {
  padding-left: 48px;
  border-radius: var(--vt-radius-small);
  border: 2px solid var(--color-border-soft);
  background: var(--color-background);
  transition: all var(--vt-transition-fast) ease;
}

:deep(.search-select .el-input__wrapper:hover),
:deep(.search-input .el-input__wrapper:hover) {
  border-color: var(--vt-c-primary);
  box-shadow: 0 0 0 3px var(--vt-c-primary-light);
}

.search-actions {
  display: flex;
  gap: 12px;
}

.search-btn, .reset-btn {
  padding: 12px 24px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.search-btn {
  background: var(--vt-gradient-primary) !important;
  border: none !important;
  color: white !important;
  box-shadow: var(--vt-shadow-1) !important;
}

.reset-btn {
  background: var(--color-background) !important;
  border: 2px solid var(--color-border-soft) !important;
  color: var(--color-text) !important;
}

/* 话题标签页 */
.topic-tabs-section {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 24px;
  box-shadow: var(--vt-shadow-2);
  border: 1px solid var(--color-border-soft);
  backdrop-filter: blur(20px);
}

.tabs-header h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: var(--color-text);
}

.topic-tabs {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: var(--vt-radius-small);
  background: var(--color-background);
  border: 2px solid var(--color-border-soft);
  cursor: pointer;
  transition: all var(--vt-transition-fast) ease;
  color: var(--color-text);
  font-weight: 500;
}

.tab-item:hover {
  border-color: var(--vt-c-primary);
  color: var(--vt-c-primary);
  transform: translateY(-2px);
  box-shadow: var(--vt-shadow-1);
}

.tab-item.active {
  background: var(--vt-gradient-primary);
  border-color: transparent;
  color: white;
  box-shadow: var(--vt-shadow-2);
}

.tab-icon {
  font-size: 16px;
}

/* 话题网格 */
.topic-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
}

.topic-card {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 24px;
  border: 1px solid var(--color-border-soft);
  transition: all var(--vt-transition-fast) ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.topic-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #f39c12 0%, #e74c3c 100%);
  opacity: 0;
  transition: opacity var(--vt-transition-fast) ease;
}

.topic-card.hover-lift:hover {
  transform: translateY(-8px);
  box-shadow: var(--vt-shadow-3);
  border-color: #f39c12;
}

.topic-card.hover-lift:hover::before {
  opacity: 1;
}

.topic-card .topic-header {
  background: none;
  border-radius: 0;
  padding: 0;
  box-shadow: none;
  border: none;
  backdrop-filter: none;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.topic-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  border: 2px solid var(--color-border-soft);
  box-shadow: var(--vt-shadow-1);
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.author-name {
  font-weight: 500;
  color: var(--color-text);
  font-size: 14px;
}

.publish-time {
  font-size: 12px;
  color: var(--color-text-soft);
}

.topic-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pin-badge, .type-badge {
  display: flex !important;
  align-items: center !important;
  gap: 4px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
}

.topic-content {
  flex: 1;
  margin-bottom: 16px;
}

.topic-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.topic-preview {
  color: var(--color-text-soft);
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.topic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--color-border-soft);
}

.topic-stats {
  display: flex;
  gap: 16px;
}

.topic-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--color-text-soft);
}

.last-reply {
  font-size: 12px;
}

.reply-label {
  color: var(--color-text-soft);
}

.reply-time {
  color: var(--color-text);
  font-weight: 500;
}

/* 创建话题对话框 */
:deep(.create-topic-dialog) {
  border-radius: var(--vt-radius-large);
}

:deep(.create-topic-dialog .el-dialog__header) {
  background: var(--vt-gradient-primary);
  color: white;
  padding: 24px;
  border-radius: var(--vt-radius-large) var(--vt-radius-large) 0 0;
}

:deep(.create-topic-dialog .el-dialog__body) {
  padding: 0;
}

.dialog-content {
  padding: 32px;
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  padding: 24px;
  background: var(--color-background-soft);
  border-radius: var(--vt-radius-small);
  border: 1px solid var(--color-border-soft);
}

.header-icon {
  width: 60px;
  height: 60px;
  background: var(--vt-gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  box-shadow: var(--vt-shadow-2);
}

.header-text h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: var(--color-text);
}

.header-text p {
  margin: 0;
  color: var(--color-text-soft);
  font-size: 14px;
}

.topic-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-item-wrapper {
  position: relative;
}

.input-icon, .textarea-icon {
  position: absolute;
  left: 16px;
  top: 12px;
  color: var(--color-text-soft);
  z-index: 2;
  font-size: 18px;
}

.textarea-icon {
  top: 16px;
}

:deep(.title-input .el-input__wrapper),
:deep(.type-select .el-select__wrapper) {
  padding-left: 48px;
  border-radius: var(--vt-radius-small);
  border: 2px solid var(--color-border-soft);
  transition: all var(--vt-transition-fast) ease;
}

:deep(.content-textarea .el-textarea__inner) {
  padding-left: 48px;
  border-radius: var(--vt-radius-small);
  border: 2px solid var(--color-border-soft);
  transition: all var(--vt-transition-fast) ease;
  min-height: 200px;
}

.option-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.option-icon {
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px 32px;
  border-top: 1px solid var(--color-border-soft);
  background: var(--color-background-soft);
}

.cancel-btn, .submit-btn {
  padding: 12px 24px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.cancel-btn {
  background: var(--color-background) !important;
  border: 2px solid var(--color-border-soft) !important;
  color: var(--color-text) !important;
}

.submit-btn {
  background: var(--vt-gradient-primary) !important;
  border: none !important;
  color: white !important;
  box-shadow: var(--vt-shadow-1) !important;
}

/* 空状态 */
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 80px 20px;
}

.empty-state {
  text-align: center;
  color: var(--color-text-soft);
}

.empty-state .empty-icon {
  font-size: 64px;
  color: var(--color-text-placeholder);
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: var(--color-text);
}

.empty-state p {
  font-size: 14px;
  margin: 0 0 20px 0;
  color: var(--color-text-soft);
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .forum-grid, .topic-grid {
    grid-template-columns: 1fr;
  }

  .input-group {
    flex-direction: column;
  }

  .input-wrapper {
    min-width: unset;
  }

  .topic-tabs {
    flex-direction: column;
  }

  .header-content {
    flex-direction: column;
    gap: 20px;
  }

  .topic-header .header-content {
    align-items: stretch;
  }

  .header-left {
    flex-direction: column;
    gap: 16px;
  }

  .forum-footer, .topic-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
}
</style>
