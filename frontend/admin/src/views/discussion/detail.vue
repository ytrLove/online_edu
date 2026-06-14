<template>
  <div class="discussion-detail-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ forumInfo.title || '讨论区详情' }}</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleReturn">
              <el-icon><Back /></el-icon>返回
            </el-button>
            <el-button type="success" @click="handleCreateTopic">
              <el-icon><Plus /></el-icon>发布主题
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="forum-info" v-if="forumInfo">
        <div class="description">{{ forumInfo.description }}</div>
        <div class="meta-info">
          <span>创建时间: {{ formatDate(forumInfo.createTime) }}</span>
          <span>主题数: {{ forumInfo.topicCount || 0 }}</span>
          <span>回复数: {{ forumInfo.replyCount || 0 }}</span>
        </div>
      </div>
      
      <!-- 主题列表 -->
      <div class="topic-list" v-loading="loading">
        <el-empty v-if="topicList.length === 0" description="暂无主题" />
        
        <div v-for="topic in topicList" :key="topic.topicId" class="topic-item">
          <div class="topic-header">
            <div class="topic-title" @click="viewTopicDetail(topic)">{{ topic.title }}</div>
            <div class="topic-actions">
              <el-button size="small" type="primary" @click="viewTopicDetail(topic)">查看</el-button>
              <el-button 
                size="small" 
                type="danger" 
                @click="handleDeleteTopic(topic)"
                v-if="topic.createdBy === currentUserId || isTeacher"
              >删除</el-button>
            </div>
          </div>
          <div class="topic-content">{{ topic.content }}</div>
          <div class="topic-footer">
            <span class="author">{{ topic.createdByName || '未知用户' }}</span>
            <span class="time">{{ formatDate(topic.createTime) }}</span>
            <span class="reply-count">回复: {{ topic.replyCount || 0 }}</span>
          </div>
        </div>
        
        <!-- 分页器 -->
        <div class="pagination-container" v-if="total > 0">
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
      </div>
    </el-card>
    
    <!-- 创建主题对话框 -->
    <el-dialog 
      v-model="createTopicDialogVisible" 
      title="发布主题" 
      width="50%"
      :before-close="handleCloseTopicDialog"
    >
      <el-form :model="topicForm" label-width="80px" :rules="topicRules" ref="topicFormRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="topicForm.title" placeholder="请输入主题标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input 
            v-model="topicForm.content" 
            type="textarea" 
            :rows="6" 
            placeholder="请输入主题内容"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseTopicDialog">取消</el-button>
          <el-button type="primary" @click="submitTopicForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 主题详情对话框 -->
    <el-dialog 
      v-model="topicDetailDialogVisible" 
      :title="currentTopic.title || '主题详情'" 
      width="70%"
      :before-close="handleCloseTopicDetailDialog"
    >
      <div class="topic-detail" v-if="currentTopic">
        <div class="topic-author">
          <span>发布者: {{ currentTopic.createdByName || '未知用户' }}</span>
          <span>发布时间: {{ formatDate(currentTopic.createTime) }}</span>
        </div>
        <div class="topic-content">{{ currentTopic.content }}</div>
        
        <div class="reply-section">
          <div class="section-header">
            <h3>回复列表</h3>
          </div>
          
          <div class="reply-list" v-loading="replyLoading">
            <el-empty v-if="replyList.length === 0" description="暂无回复" />
            <div v-for="reply in replyList" :key="reply.replyId" class="reply-item">
              <div class="reply-header">
                <span class="author">{{ reply.createdByName || '未知用户' }}</span>
                <span class="time">{{ formatDate(reply.createTime) }}</span>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
              <div class="reply-actions" v-if="reply.createdBy === currentUserId || isTeacher">
                <el-button size="small" type="danger" @click="handleDeleteReply(reply)">删除</el-button>
              </div>
            </div>
            
            <!-- 回复分页器 -->
            <div class="pagination-container" v-if="replyTotal > 0">
              <el-pagination
                :current-page="replyQueryParams.pageNum"
                :page-size="replyQueryParams.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="replyTotal"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleReplySizeChange"
                @current-change="handleReplyCurrentChange"
              />
            </div>
          </div>
          
          <div class="reply-form">
            <h4>发表回复</h4>
            <el-form :model="replyForm" label-width="0" :rules="replyRules" ref="replyFormRef">
              <el-form-item prop="content">
                <el-input 
                  v-model="replyForm.content" 
                  type="textarea" 
                  :rows="4" 
                  placeholder="请输入回复内容"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitReplyForm">发表回复</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Back, Plus } from '@element-plus/icons-vue';

// 导入API
import { getForumDetail } from '@/api/edu/discussion';
import { getTopicList, getTopicDetail, createTopic, deleteTopic } from '@/api/edu/discussion';
import { getReplyList, createReply, deleteReply } from '@/api/edu/discussion';
import { getUserInfo } from '@/api/auth';

const route = useRoute();
const router = useRouter();

// 加载状态
const loading = ref(false);
const replyLoading = ref(false);

// 讨论区信息
const forumInfo = ref({});
const forumId = computed(() => route.params.id || route.query.forumId);
const courseId = computed(() => route.query.courseId);
const courseName = computed(() => route.query.courseName);

// 当前用户信息
const currentUserId = ref(null);
const isTeacher = ref(false);

// 主题列表
const topicList = ref([]);
const total = ref(0);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  forumId: null,
  keyword: ''
});

// 主题创建
const createTopicDialogVisible = ref(false);
const topicForm = reactive({
  forumId: null,
  title: '',
  content: ''
});
const topicFormRef = ref(null);
const topicRules = {
  title: [{ required: true, message: '请输入主题标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入主题内容', trigger: 'blur' }]
};

// 主题详情
const topicDetailDialogVisible = ref(false);
const currentTopic = ref({});

// 回复列表
const replyList = ref([]);
const replyTotal = ref(0);
const replyQueryParams = reactive({
  pageNum: 1,
  pageSize: 10
});

// 回复创建
const replyForm = reactive({
  topicId: null,
  content: '',
  parentId: null
});
const replyFormRef = ref(null);
const replyRules = {
  content: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
};

/**
 * 页面初始化
 */
onMounted(async () => {
  // 设置讨论区ID和课程ID
  queryParams.forumId = forumId.value;
  
  // 获取当前用户信息
  await getCurrentUser();
  
  // 获取讨论区信息
  await getForumInfo();
  
  // 获取主题列表
  await getTopics();
});

/**
 * 获取当前用户信息
 */
const getCurrentUser = async () => {
  try {
    const response = await getUserInfo();
    const userInfo = response.data;
    currentUserId.value = userInfo.userId;
    
    // 假设用户角色信息在roles数组中
    isTeacher.value = userInfo.roles && userInfo.roles.some(role => 
      role.roleKey === 'teacher' || role.roleKey === 'admin'
    );
  } catch (error) {
    console.error('获取用户信息失败', error);
  }
};

/**
 * 获取讨论区信息
 */
const getForumInfo = async () => {
  try {
    const response = await getForumDetail(forumId.value);
    forumInfo.value = response.data || {};
  } catch (error) {
    console.error('获取讨论区信息失败', error);
    ElMessage.error('获取讨论区信息失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 获取主题列表
 */
const getTopics = async () => {
  loading.value = true;
  try {
    const response = await getTopicList(queryParams);
    topicList.value = response.data.records || [];
    total.value = response.data.total || 0;
  } catch (error) {
    console.error('获取主题列表失败', error);
    ElMessage.error('获取主题列表失败: ' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

/**
 * 查看主题详情
 */
const viewTopicDetail = async (topic) => {
  currentTopic.value = topic;
  topicDetailDialogVisible.value = true;
  
  // 重置回复分页
  replyQueryParams.pageNum = 1;
  replyQueryParams.pageSize = 10;
  
  // 设置回复表单的主题ID
  replyForm.topicId = topic.topicId;
  
  // 获取回复列表
  await getReplies();
};

/**
 * 获取回复列表
 */
const getReplies = async () => {
  if (!currentTopic.value.topicId) return;
  
  replyLoading.value = true;
  try {
    const response = await getReplyList(currentTopic.value.topicId, replyQueryParams);
    replyList.value = response.data.records || [];
    replyTotal.value = response.data.total || 0;
  } catch (error) {
    console.error('获取回复列表失败', error);
    ElMessage.error('获取回复列表失败: ' + (error.message || '未知错误'));
  } finally {
    replyLoading.value = false;
  }
};

/**
 * 处理返回
 */
const handleReturn = () => {
  router.go(-1);
};

/**
 * 处理创建主题
 */
const handleCreateTopic = () => {
  topicForm.forumId = forumId.value;
  topicForm.title = '';
  topicForm.content = '';
  createTopicDialogVisible.value = true;
};

/**
 * 关闭创建主题对话框
 */
const handleCloseTopicDialog = () => {
  createTopicDialogVisible.value = false;
  if (topicFormRef.value) {
    topicFormRef.value.resetFields();
  }
};

/**
 * 提交主题表单
 */
const submitTopicForm = () => {
  if (topicFormRef.value) {
    topicFormRef.value.validate(async (valid) => {
      if (valid) {
        try {
          await createTopic(topicForm);
          ElMessage.success('主题发布成功');
          createTopicDialogVisible.value = false;
          
          // 刷新主题列表
          getTopics();
        } catch (error) {
          console.error('主题发布失败', error);
          ElMessage.error('主题发布失败: ' + (error.message || '未知错误'));
        }
      }
    });
  }
};

/**
 * 删除主题
 */
const handleDeleteTopic = (topic) => {
  ElMessageBox.confirm(
    `确定要删除主题"${topic.title}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteTopic(topic.topicId);
      ElMessage.success('主题删除成功');
      
      // 刷新主题列表
      getTopics();
      
      // 如果当前主题详情对话框打开且是当前删除的主题，则关闭对话框
      if (topicDetailDialogVisible.value && currentTopic.value.topicId === topic.topicId) {
        topicDetailDialogVisible.value = false;
      }
    } catch (error) {
      console.error('主题删除失败', error);
      ElMessage.error('主题删除失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {
    // 取消删除
  });
};

/**
 * 关闭主题详情对话框
 */
const handleCloseTopicDetailDialog = () => {
  topicDetailDialogVisible.value = false;
  currentTopic.value = {};
  replyList.value = [];
  replyTotal.value = 0;
  
  // 重置回复表单
  if (replyFormRef.value) {
    replyFormRef.value.resetFields();
  }
};

/**
 * 提交回复表单
 */
const submitReplyForm = () => {
  if (replyFormRef.value) {
    replyFormRef.value.validate(async (valid) => {
      if (valid) {
        try {
          await createReply(replyForm);
          ElMessage.success('回复发表成功');
          
          // 清空回复内容
          replyForm.content = '';
          
          // 刷新回复列表
          getReplies();
          
          // 同时更新主题列表，因为回复数量变化了
          getTopics();
        } catch (error) {
          console.error('回复发表失败', error);
          ElMessage.error('回复发表失败: ' + (error.message || '未知错误'));
        }
      }
    });
  }
};

/**
 * 删除回复
 */
const handleDeleteReply = (reply) => {
  ElMessageBox.confirm(
    '确定要删除这条回复吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteReply(reply.replyId);
      ElMessage.success('回复删除成功');
      
      // 刷新回复列表
      getReplies();
      
      // 同时更新主题列表，因为回复数量变化了
      getTopics();
    } catch (error) {
      console.error('回复删除失败', error);
      ElMessage.error('回复删除失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {
    // 取消删除
  });
};

/**
 * 处理主题分页大小改变
 */
const handleSizeChange = (size) => {
  queryParams.pageSize = size;
  getTopics();
};

/**
 * 处理主题页码改变
 */
const handleCurrentChange = (num) => {
  queryParams.pageNum = num;
  getTopics();
};

/**
 * 处理回复分页大小改变
 */
const handleReplySizeChange = (size) => {
  replyQueryParams.pageSize = size;
  getReplies();
};

/**
 * 处理回复页码改变
 */
const handleReplyCurrentChange = (num) => {
  replyQueryParams.pageNum = num;
  getReplies();
};

/**
 * 格式化日期
 */
const formatDate = (dateStr) => {
  if (!dateStr) return '未设置';
  
  // 尝试解析日期时间
  try {
    const date = new Date(dateStr);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    return dateStr;
  }
};
</script>

<style scoped>
.discussion-detail-container {
  padding: 20px;
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

.forum-info {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.description {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.meta-info {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #909399;
}

.topic-list {
  margin-top: 20px;
}

.topic-item {
  padding: 15px;
  margin-bottom: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  transition: all 0.3s;
}

.topic-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.topic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.topic-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  cursor: pointer;
}

.topic-title:hover {
  color: #409eff;
}

.topic-actions {
  display: flex;
  gap: 10px;
}

.topic-content {
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.topic-footer {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.topic-footer .author {
  margin-right: 15px;
}

.topic-footer .time {
  margin-right: 15px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 主题详情样式 */
.topic-detail {
  padding: 10px;
}

.topic-author {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  margin-bottom: 15px;
}

.reply-section {
  margin-top: 30px;
}

.section-header {
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.reply-list {
  margin-bottom: 30px;
}

.reply-item {
  padding: 15px;
  margin-bottom: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  position: relative;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
  color: #909399;
}

.reply-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  white-space: pre-wrap;
}

.reply-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.reply-form {
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.reply-form h4 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}
</style> 