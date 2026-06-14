<template>
  <div class="ai-assistant-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">🤖 智能AI助手</h1>
          <p class="page-subtitle">探索人工智能的无限可能，让AI成为您学习路上的智能伙伴</p>
        </div>
        <div class="header-decoration">
          <div class="decoration-icon">
            <el-icon><Cpu /></el-icon>
          </div>
          <div class="floating-particles">
            <div v-for="i in 8" :key="i" class="particle" :style="getParticleStyle(i)"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 功能介绍区域 -->
    <div class="features-section">
      <div class="section-header">
        <h2 class="section-title">为什么选择我们的AI助手？</h2>
        <p class="section-subtitle">体验前所未有的智能学习服务</p>
      </div>
      
      <div class="features-grid">
        <div class="feature-card hover-lift" v-for="(feature, index) in features" :key="index">
          <div class="feature-header">
            <div class="feature-icon" :class="feature.iconClass">
              <component :is="feature.icon" />
            </div>
            <div class="feature-badge">
              <span class="badge-text">{{ feature.badge }}</span>
            </div>
          </div>
          
          <div class="feature-content">
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
            
            <div class="feature-highlights">
              <div v-for="highlight in feature.highlights" :key="highlight" class="highlight-item">
                <el-icon class="highlight-icon"><Check /></el-icon>
                <span class="highlight-text">{{ highlight }}</span>
              </div>
            </div>
          </div>
          
          <div class="feature-footer">
            <el-button type="primary" class="feature-btn" @click="goToChat">
              <el-icon><ChatDotRound /></el-icon>
              立即体验
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速入口区域 -->
    <div class="quick-start-section">
      <div class="quick-start-card">
        <div class="card-content">
          <div class="start-icon">
            <el-icon><ChatRound /></el-icon>
          </div>
          <div class="start-text">
            <h3>准备好开始了吗？</h3>
            <p>点击下方按钮，立即与AI助手开始对话，获取个性化的学习建议和帮助。</p>
          </div>
          <div class="start-actions">
            <el-button type="primary" size="large" class="start-chat-btn" @click="goToChat">
              <el-icon><VideoCamera /></el-icon>
              开始对话
            </el-button>
            <el-button size="large" class="learn-more-btn">
              <el-icon><Document /></el-icon>
              了解更多
            </el-button>
          </div>
        </div>
        
        <div class="card-decoration">
          <div class="decoration-shape shape-1"></div>
          <div class="decoration-shape shape-2"></div>
          <div class="decoration-shape shape-3"></div>
        </div>
      </div>
    </div>

    <!-- AI助手悬浮球 -->
    <div class="ai-pet" @click="goToChat" :class="{ 'show-bubble': showBubble }">
      <div class="pet-container">
        <transition name="bubble">
          <div v-show="showBubble" class="speech-bubble">
            <div class="bubble-content">
              <span class="bubble-icon">👋</span>
              <span class="bubble-text">{{ currentBubbleText }}</span>
            </div>
          </div>
        </transition>
        
        <div class="pet-avatar">
          <div class="avatar-ring"></div>
          <div class="avatar-core">
            <el-icon class="pet-icon"><Cpu /></el-icon>
          </div>
          <div class="pulse-waves">
            <div class="wave wave-1"></div>
            <div class="wave wave-2"></div>
            <div class="wave wave-3"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Cpu, 
  ChatDotRound, 
  ChatRound, 
  VideoCamera, 
  Document, 
  Check,
  MessageBox,
  EditPen,
  TrendCharts
} from '@element-plus/icons-vue'

const router = useRouter()
const showBubble = ref(false)
const currentBubbleText = ref('')
let bubbleInterval = null

// 功能特色数据
const features = ref([
  {
    title: '智能对话助手',
    description: '24/7随时为您提供学习指导，支持多学科知识问答，让学习更高效',
    icon: ChatDotRound,
    iconClass: 'chat-icon',
    badge: 'AI驱动',
    highlights: [
      '自然语言理解',
      '多学科知识覆盖', 
      '个性化回答',
      '实时响应'
    ]
  },
  {
    title: '学习内容生成',
    description: '根据您的学习需求，智能生成练习题、总结笔记和学习计划',
    icon: EditPen,
    iconClass: 'content-icon',
    badge: '创新功能',
    highlights: [
      '智能题目生成',
      '知识点总结',
      '学习计划制定',
      '个性化推荐'
    ]
  },
  {
    title: '学习数据分析',
    description: '深度分析您的学习数据，提供专业的学习建议和改进方案',
    icon: TrendCharts,
    iconClass: 'analysis-icon',
    badge: '数据驱动',
    highlights: [
      '学习进度跟踪',
      '弱点识别',
      '效率评估',
      '智能建议'
    ]
  }
])

// 气泡提示文本
const bubbleTexts = [
  '嗨！我是您的AI学习助手 😊',
  '需要学习帮助吗？点击我！💡',
  '有疑问随时问我哦 🤔',
  '让我们一起高效学习吧！📚',
  '我在这里等您呢～ 🎯'
]

// 获取随机气泡文本
const getRandomBubbleText = () => {
  const randomIndex = Math.floor(Math.random() * bubbleTexts.length)
  return bubbleTexts[randomIndex]
}

// 获取粒子样式
const getParticleStyle = (index) => {
  const size = Math.random() * 8 + 4
  const left = Math.random() * 100
  const animationDelay = Math.random() * 3
  const animationDuration = Math.random() * 3 + 2
  
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDelay: `${animationDelay}s`,
    animationDuration: `${animationDuration}s`
  }
}

// 显示对话气泡
onMounted(() => {
  // 初始显示气泡
  setTimeout(() => {
    currentBubbleText.value = getRandomBubbleText()
    showBubble.value = true
    
    // 3秒后隐藏
    setTimeout(() => {
      showBubble.value = false
    }, 3000)
  }, 2000)
  
  // 每15秒随机显示气泡提示
  bubbleInterval = setInterval(() => {
    if (Math.random() > 0.4) {
      currentBubbleText.value = getRandomBubbleText()
      showBubble.value = true
      
      // 4秒后隐藏
      setTimeout(() => {
        showBubble.value = false
      }, 4000)
    }
  }, 15000)
})

onUnmounted(() => {
  if (bubbleInterval) {
    clearInterval(bubbleInterval)
  }
})

// 跳转到聊天页面
const goToChat = () => {
  router.push('/ai-assistant/chat')
}
</script>

<style lang="scss" scoped>
.ai-assistant-container {
  padding: var(--container-padding);
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 140px);
}

/* 页面标题区域 */
.page-header {
  margin-bottom: 48px;
}

.header-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: var(--vt-radius-large);
  padding: 60px 40px;
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
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 16px 0;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  letter-spacing: -1px;
}

.page-subtitle {
  font-size: 18px;
  margin: 0;
  opacity: 0.95;
  text-shadow: 0 1px 10px rgba(0, 0, 0, 0.2);
  line-height: 1.6;
  max-width: 500px;
}

.header-decoration {
  position: relative;
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.decoration-icon {
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(20px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  font-size: 48px;
  animation: float 4s ease-in-out infinite;
  z-index: 2;
}

.floating-particles {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: particleFloat 5s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-10px) rotate(180deg); }
}

@keyframes particleFloat {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.3; }
  50% { transform: translateY(-20px) scale(1.2); opacity: 0.8; }
}

/* 功能介绍区域 */
.features-section {
  margin-bottom: 48px;
}

.section-header {
  text-align: center;
  margin-bottom: 48px;
}

.section-title {
  font-size: 36px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: var(--color-text);
  background: var(--vt-gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-subtitle {
  font-size: 16px;
  color: var(--color-text-soft);
  margin: 0;
  line-height: 1.6;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 32px;
}

.feature-card {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 32px;
  border: 1px solid var(--color-border-soft);
  transition: all var(--vt-transition-fast) ease;
  height: 100%;
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
}

.feature-card::before {
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

.feature-card.hover-lift:hover {
  transform: translateY(-12px);
  box-shadow: var(--vt-shadow-3);
  border-color: var(--vt-c-primary-light);
}

.feature-card.hover-lift:hover::before {
  opacity: 1;
}

.feature-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.feature-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  box-shadow: var(--vt-shadow-2);
}

.chat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.content-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.analysis-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.feature-badge {
  padding: 6px 12px;
  background: var(--vt-gradient-primary);
  color: white;
  border-radius: var(--vt-radius-small);
  font-size: 12px;
  font-weight: 500;
  box-shadow: var(--vt-shadow-1);
}

.feature-content {
  flex: 1;
  margin-bottom: 24px;
}

.feature-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 16px 0;
  line-height: 1.3;
}

.feature-description {
  font-size: 16px;
  color: var(--color-text-soft);
  line-height: 1.6;
  margin: 0 0 20px 0;
}

.feature-highlights {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.highlight-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--color-text);
}

.highlight-icon {
  color: #67c23a;
  font-size: 16px;
}

.feature-footer {
  margin-top: auto;
}

.feature-btn {
  width: 100%;
  padding: 12px 24px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  font-size: 16px !important;
  background: var(--vt-gradient-primary) !important;
  border: none !important;
  color: white !important;
  box-shadow: var(--vt-shadow-1) !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.feature-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: var(--vt-shadow-2) !important;
}

/* 快速入口区域 */
.quick-start-section {
  margin-bottom: 48px;
}

.quick-start-card {
  background: var(--color-background-overlay);
  border-radius: var(--vt-radius-large);
  padding: 48px;
  border: 1px solid var(--color-border-soft);
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
  box-shadow: var(--vt-shadow-2);
}

.card-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 32px;
  flex-wrap: wrap;
}

.start-icon {
  width: 80px;
  height: 80px;
  background: var(--vt-gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  box-shadow: var(--vt-shadow-3);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.start-text {
  flex: 1;
  min-width: 300px;
}

.start-text h3 {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 12px 0;
  color: var(--color-text);
}

.start-text p {
  font-size: 16px;
  color: var(--color-text-soft);
  margin: 0;
  line-height: 1.6;
}

.start-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.start-chat-btn {
  padding: 16px 32px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  font-size: 16px !important;
  background: var(--vt-gradient-primary) !important;
  border: none !important;
  color: white !important;
  box-shadow: var(--vt-shadow-2) !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.start-chat-btn:hover {
  transform: translateY(-3px) !important;
  box-shadow: var(--vt-shadow-3) !important;
}

.learn-more-btn {
  padding: 16px 32px !important;
  border-radius: var(--vt-radius-small) !important;
  font-weight: 500 !important;
  font-size: 16px !important;
  background: var(--color-background) !important;
  border: 2px solid var(--color-border-soft) !important;
  color: var(--color-text) !important;
  transition: all var(--vt-transition-fast) ease !important;
}

.learn-more-btn:hover {
  border-color: var(--vt-c-primary) !important;
  color: var(--vt-c-primary) !important;
  transform: translateY(-3px) !important;
}

.card-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 200px;
  height: 100%;
  pointer-events: none;
}

.decoration-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(102, 126, 234, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 60px;
  height: 60px;
  top: 20%;
  right: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 40px;
  height: 40px;
  top: 60%;
  right: 30%;
  animation-delay: 2s;
}

.shape-3 {
  width: 30px;
  height: 30px;
  top: 40%;
  right: 5%;
  animation-delay: 4s;
}

/* AI助手悬浮球 */
.ai-pet {
  position: fixed;
  bottom: 30px;
  right: 30px;
  cursor: pointer;
  z-index: 1000;
  transition: all var(--vt-transition-fast) ease;
}

.pet-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.speech-bubble {
  position: absolute;
  bottom: 120px;
  right: 0;
  background: var(--color-background-overlay);
  border: 1px solid var(--color-border-soft);
  border-radius: var(--vt-radius-small);
  padding: 16px 20px;
  backdrop-filter: blur(20px);
  box-shadow: var(--vt-shadow-2);
  max-width: 200px;
  z-index: 10;
}

.speech-bubble::after {
  content: '';
  position: absolute;
  bottom: -8px;
  right: 20px;
  width: 16px;
  height: 16px;
  background: var(--color-background-overlay);
  border: 1px solid var(--color-border-soft);
  border-top: none;
  border-left: none;
  transform: rotate(45deg);
}

.bubble-content {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--color-text);
  font-size: 14px;
  font-weight: 500;
}

.bubble-icon {
  font-size: 16px;
}

.pet-avatar {
  position: relative;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 3px solid var(--vt-c-primary-light);
  border-radius: 50%;
  animation: rotate 8s linear infinite;
}

.avatar-core {
  width: 64px;
  height: 64px;
  background: var(--vt-gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--vt-shadow-3);
  transition: all var(--vt-transition-fast) ease;
  z-index: 2;
}

.ai-pet:hover .avatar-core {
  transform: scale(1.1);
  box-shadow: var(--vt-shadow-4);
}

.pet-icon {
  font-size: 28px;
  color: white;
}

.pulse-waves {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.wave {
  position: absolute;
  border: 2px solid var(--vt-c-primary);
  border-radius: 50%;
  opacity: 0;
  animation: wave 3s ease-out infinite;
}

.wave-1 {
  animation-delay: 0s;
}

.wave-2 {
  animation-delay: 1s;
}

.wave-3 {
  animation-delay: 2s;
}

@keyframes wave {
  0% {
    width: 80px;
    height: 80px;
    opacity: 1;
  }
  100% {
    width: 160px;
    height: 160px;
    opacity: 0;
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 气泡动画 */
.bubble-enter-active {
  animation: bubbleIn 0.5s ease;
}

.bubble-leave-active {
  animation: bubbleOut 0.3s ease;
}

@keyframes bubbleIn {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.8);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes bubbleOut {
  from {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateY(-10px) scale(0.8);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-assistant-container {
    padding: 16px;
  }

  .header-content {
    padding: 40px 24px;
    flex-direction: column;
    text-align: center;
    gap: 32px;
  }

  .page-title {
    font-size: 32px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .section-title {
    font-size: 28px;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .card-content {
    flex-direction: column;
    text-align: center;
    gap: 24px;
  }

  .start-actions {
    justify-content: center;
  }

  .ai-pet {
    bottom: 20px;
    right: 20px;
  }

  .pet-avatar {
    width: 60px;
    height: 60px;
  }

  .avatar-core {
    width: 48px;
    height: 48px;
  }

  .pet-icon {
    font-size: 20px;
  }

  .speech-bubble {
    bottom: 80px;
    max-width: 160px;
    font-size: 12px;
    padding: 12px 16px;
  }
}
</style>