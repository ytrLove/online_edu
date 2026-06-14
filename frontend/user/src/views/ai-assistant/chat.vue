<template>
  <div class="chat-page">
    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <h3>历史会话</h3>
          <button class="new-session-btn" @click="createNewSession">
            <i class="fas fa-plus"></i> 新建会话
          </button>
        </div>
        <div class="session-list">
          <div 
            v-for="session in sessions" 
            :key="session.sessionId"
            :class="['session-item', { active: session.sessionId === sessionId }]"
            @click="switchSession(session.sessionId)"
          >
            <i class="fas fa-comment"></i>
            <span class="session-title">{{ session.title || '新会话' }}</span>
            <span class="session-time">{{ formatTime(session.createdAt) }}</span>
          </div>
          
        </div>
      </div>
      
      <!-- 聊天主体 -->
      <div class="chat-body">
        <!-- 页面头部 -->
        <div class="header-container">
          <div class="header">
            <button class="back-button" @click="goBack">
              <i class="fas fa-arrow-left"></i>
            </button>
            <div class="chat-info">
              <h1>AI智能助手</h1>
              <p>随时为您提供帮助和支持</p>
            </div>
            <div class="ai-status">
              <i class="fas fa-circle"></i>
              <span>在线</span>
            </div>
          </div>
        </div>
      
        <!-- 聊天容器 -->
        <div class="chat-container">
          <!-- 聊天区域 -->
          <div class="chat-area" ref="chatArea">
            <!-- 消息列表 -->
            <div 
              v-for="(message, index) in messages" 
              :key="index" 
              :class="['message', message.isUser ? 'user-message' : 'ai-message']"
            >
              <div class="message-header">
                <div :class="['avatar', message.isUser ? 'user-avatar' : 'ai-avatar']">
                  <i :class="['fas', message.isUser ? 'fa-user' : 'fa-robot']"></i>
                </div>
                <div class="sender-name">{{ message.isUser ? '您' : 'AI助手' }}</div>
                <div class="message-time">{{ message.time }}</div>
              </div>
              <div class="message-content" v-html="message.content"></div>
            </div>
            
            <!-- 加载中提示 -->
            <div v-if="loading" class="message ai-message">
              <div class="message-header">
                <div class="avatar ai-avatar">
                  <i class="fas fa-robot"></i>
                </div>
                <div class="sender-name">AI助手</div>
                <div class="message-time">正在输入...</div>
              </div>
              <div class="message-content">
                <p><i class="fas fa-spinner fa-spin"></i> 思考中...</p>
              </div>
            </div>
          </div>
          
          <!-- 输入区域 -->
          <div class="input-area">
            <textarea 
              class="message-input" 
              v-model="userInput" 
              placeholder="输入您的问题或消息..."
              @keypress.enter.prevent="handleEnterPress"
            ></textarea>
            <button class="send-button" @click="sendMessage" :disabled="loading || !userInput.trim()">
              <i class="fas fa-paper-plane"></i>
            </button>
          </div>
          
          <!-- 快捷操作按钮 -->
          <div class="action-buttons">
            <button class="action-button" @click="clearChat">
              <i class="fas fa-trash-alt"></i>
              <span>清空对话</span>
            </button>
            <button class="action-button" @click="loadHistory" :disabled="loading">
              <i class="fas fa-history"></i>
              <span>历史记录</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { createChatSession, sendChatMessage, getChatHistory, getAllChatSessions } from '@/api/chat'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const chatArea = ref(null)
const userInput = ref('')
const messages = ref([])
const loading = ref(false)
const sessionId = ref('')
const sessions = ref([])

// 获取会话列表
const fetchSessions = async () => {
  try {
    const response = await getAllChatSessions()
    sessions.value = (response.data || []).sort((a, b) => 
      new Date(b.createdAt) - new Date(a.createdAt)
    )
    sessionId.value = sessions.value[0]?.sessionId || ''
  } catch (error) {
    console.error('获取会话列表失败', error)
    ElMessage.error('获取会话列表失败')
  }
}

// 创建新会话
const createNewSession = async () => {
  try {
    loading.value = true
    const response = await createChatSession()
    sessionId.value = response.data || response
    messages.value = []
    await fetchSessions()
    
    // 添加欢迎消息
    addMessage({
      content: '<p>👋 您好！我是您的AI助手，很高兴为您服务！</p><p>请问有什么我可以帮您的吗？</p>',
      isUser: false
    })
  } catch (error) {
    console.error('创建会话失败', error)
    ElMessage.error('创建会话失败')
  } finally {
    loading.value = false
  }
}

// 切换会话
const switchSession = async (id) => {
  if (id === sessionId.value) return
  
  try {
    loading.value = true
    sessionId.value = id
    messages.value = []
    await loadHistory()
    scrollToBottom()
  } catch (error) {
    console.error('切换会话失败', error)
    ElMessage.error('切换会话失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${date.getMonth()+1}/${date.getDate()} ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 初始化聊天会话
onMounted(async () => {
  try {
    // 先获取已有会话
    await fetchSessions()
    
    // 如果没有会话才创建新会话
    if (!sessionId.value) {
      const response = await createChatSession()
      sessionId.value = response.data || response

      await fetchSessions()
      
      // 添加欢迎消息
      addMessage({
        content: '<p>👋 您好！我是您的AI助手，很高兴为您服务！</p><p>我可以帮助您解答各种问题，提供建议，或者只是陪您聊天。请问有什么我可以帮您的吗？</p>',
        isUser: false
      })
    }
    
    // 尝试加载历史记录
    if (sessionId.value) {
      loading.value = true
      await loadHistory()
    }
  } catch (error) {
    console.error('初始化聊天会话失败', error)
    ElMessage.error('初始化聊天失败，请刷新页面重试')
  }
})

// 监听消息变化，自动滚动到底部
watch(messages, () => {
  nextTick(() => {
    scrollToBottom()
  })
}, { deep: true })

// 滚动到底部
const scrollToBottom = () => {
  if (chatArea.value) {
    chatArea.value.scrollTop = chatArea.value.scrollHeight
  }
}

// 添加消息到聊天区域
const addMessage = (message) => {
  const now = new Date()
  const timeString = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
  
  messages.value.push({
    ...message,
    time: message.time || timeString
  })
}

// 发送消息
const sendMessage = async () => {
  const content = userInput.value.trim()
  if (!content || loading.value) return
  
  // 添加用户消息
  addMessage({
    content: `<p>${content}</p>`,
    isUser: true
  })
  
  userInput.value = ''
  loading.value = true
  
  try {
    // 发送消息到后端
    const response = await sendChatMessage({
      query: content,
      sessionId: sessionId.value
    })
    console.log(response);
    const aiResponse = response.data
    // 添加AI回复
    addMessage({
      content: `<p>${aiResponse}</p>`,
      isUser: false
    })
  } catch (error) {
    console.error('发送消息失败', error)
    ElMessage.error('发送消息失败，请重试')
    
    // 添加错误提示
    addMessage({
      content: '<p>抱歉，我遇到了一些问题，无法回复您的消息。请稍后再试。</p>',
      isUser: false
    })
  } finally {
    loading.value = false
  }
}

// 处理回车键按下事件
const handleEnterPress = (e) => {
  if (!e.shiftKey) {
    sendMessage()
  }
}

// 清空聊天
const clearChat = () => {
  ElMessageBox.confirm(
    '确定要清空当前对话吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    messages.value = []
    // 添加欢迎消息
    addMessage({
      content: '<p>👋 您好！我是您的AI助手，很高兴为您服务！</p><p>请问有什么我可以帮您的吗？</p>',
      isUser: false
    })
  }).catch(() => {})
}

// 加载历史记录
const loadHistory = async () => {
  console.log('sessionId', sessionId.value, "loading" + loading.value);
  
  if (!sessionId.value || !loading.value) return
  
  loading.value = true
  try {
    const response = await getChatHistory(sessionId.value)
    const history = response.data || response
    
    if (history && history.length > 0) {
      // 清空当前消息
      messages.value = []
      
      // 添加历史消息
      history.forEach(msg => {
        const isUser = msg.role === 'user'
        addMessage({
          content: `<p>${msg.content}</p>`,
          isUser,
          time: new Date(msg.timestamp).toLocaleTimeString()
        })
      })
      
      ElMessage.success('历史记录加载成功')
    } else {
      ElMessage.info('没有找到历史记录')
    }
  } catch (error) {
    console.error('加载历史记录失败', error)
    ElMessage.error('加载历史记录失败')
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.push('/ai-assistant')
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 整体布局 */
.chat-page {
  height: 100vh;
  background: linear-gradient(135deg, #1a1f2c 0%, #2d3748 100%);
  color: #fff;
}

/* 主内容区 */
.main-content {
  display: flex;
  height: 100%;
  gap: 20px;
  padding: 20px;
}

/* 侧边栏样式 */
.sidebar {
  width: 320px;
  background: rgba(26, 32, 44, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.sidebar:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
}

.sidebar-header {
  padding: 25px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h3 {
  color: #fff;
  font-size: 1.4rem;
  margin-bottom: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #fff 0%, #a0aec0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.new-session-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #4a6bff 0%, #6a8cff 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.new-session-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(74, 107, 255, 0.4);
}

.session-list {
  flex-grow: 1;
  overflow-y: auto;
  padding: 15px;
}

.session-item {
  padding: 18px 20px;
  margin: 0 0 10px 0;
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 8px;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.session-item:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateX(5px);
}

.session-item.active {
  background: rgba(74, 107, 255, 0.15);
  border-color: rgba(74, 107, 255, 0.3);
  color: white;
}

.session-title {
  font-size: 1rem;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-time {
  font-size: 0.8rem;
  opacity: 0.7;
}

.session-item i {
  margin-right: 10px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.session-item.active i {
  color: #4a6bff;
}

/* 聊天主体 */
.chat-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: rgba(26, 32, 44, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.header {
  display: flex;
  align-items: center;
  padding: 25px 40px;
  background: rgba(26, 32, 44, 0.98);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.back-button {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  width: 45px;
  height: 45px;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-right: 20px;
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(-5px);
}

.chat-info {
  flex-grow: 1;
}

.chat-info h1 {
  font-size: 2rem;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #4a6bff 0%, #6a8cff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 700;
}

.chat-info p {
  font-size: 0.9rem;
  opacity: 0.8;
}

.ai-status {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  background: rgba(0, 255, 170, 0.1);
  border-radius: 20px;
  font-size: 0.9rem;
}

.ai-status i {
  margin-right: 8px;
  color: #00ffaa;
}

/* 聊天容器 */
.chat-container {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  max-width: 1200px;
  width: 100%;
  margin: 20px auto;
  padding: 0 20px;
}

/* 聊天区域 */
.chat-area {
  flex-grow: 1;
  background: rgba(26, 32, 44, 0.5);
  padding: 30px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 25px;
}

/* 消息样式 */
.message {
  max-width: 80%;
  padding: 20px 25px;
  border-radius: 20px;
  animation: messageSlide 0.5s ease;
  position: relative;
  line-height: 1.6;
}

@keyframes messageSlide {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  background: linear-gradient(135deg, #4a6bff 0%, #6a8cff 100%);
  align-self: flex-end;
  border-bottom-right-radius: 8px;
  box-shadow: 0 8px 20px rgba(74, 107, 255, 0.2);
}

.ai-message {
  background: rgba(50, 60, 80, 0.8);
  align-self: flex-start;
  border-bottom-left-radius: 8px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.message-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 12px;
  font-size: 18px;
}

.user-avatar {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
}

.ai-avatar {
  background: linear-gradient(135deg, #00c9ff 0%, #92fe9d 100%);
}

.sender-name {
  font-weight: bold;
  font-size: 1.1rem;
}

.message-time {
  font-size: 0.8rem;
  opacity: 0.7;
  margin-left: auto;
}

.message-content {
  font-size: 1.1rem;
}

.message-content p {
  margin-bottom: 10px;
}

.message-content a {
  color: #00c3ff;
  text-decoration: none;
}

/* 输入区域 */
.input-area {
  padding: 20px;
  background: rgba(26, 32, 44, 0.98);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.message-input {
  width: 100%;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 20px;
  color: white;
  font-size: 1.1rem;
  resize: none;
  min-height: 70px;
  transition: all 0.3s ease;
}

.message-input:focus {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(74, 107, 255, 0.5);
  box-shadow: 0 0 0 2px rgba(74, 107, 255, 0.2);
}

.send-button {
  position: absolute;
  right: 30px;
  bottom: 30px;
  background: linear-gradient(135deg, #4a6bff 0%, #6a8cff 100%);
  border: none;
  width: 50px;
  height: 50px;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
}

.send-button:hover:not(:disabled) {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 20px rgba(74, 107, 255, 0.4);
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px;
}

.action-button {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  padding: 15px 30px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  gap: 10px;
}

.action-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

/* 响应式设计优化 */
@media (max-width: 768px) {
  .main-content {
    padding: 10px;
    gap: 10px;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
    max-height: 300px;
  }
  
  .chat-body {
    height: calc(100vh - 320px);
  }
  
  .header {
    padding: 15px;
  }
  
  .chat-area {
    padding: 15px;
  }
  
  .message {
    max-width: 90%;
    padding: 15px;
  }
  
  .input-area {
    padding: 15px;
  }
  
  .message-input {
    padding: 15px;
  }
  
  .send-button {
    width: 45px;
    height: 45px;
    right: 20px;
    bottom: 20px;
  }
}
</style>
