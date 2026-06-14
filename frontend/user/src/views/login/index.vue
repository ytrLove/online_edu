<template>
  <div class="login-container">
    <!-- 背景动画元素 -->
    <div class="background-animation">
      <div class="floating-particles">
        <div v-for="i in 20" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
      <div class="wave-animation">
        <svg class="wave" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
          <path fill="rgba(102, 126, 234, 0.1)" fill-opacity="1" d="M0,32L48,80C96,128,192,224,288,224C384,224,480,128,576,90.7C672,53,768,75,864,96C960,117,1056,139,1152,149.3C1248,160,1344,160,1392,160L1440,160L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path>
        </svg>
        <svg class="wave wave-2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
          <path fill="rgba(118, 75, 162, 0.1)" fill-opacity="1" d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,133.3C672,139,768,181,864,197.3C960,213,1056,203,1152,181.3C1248,160,1344,128,1392,112L1440,96L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path>
        </svg>
      </div>
    </div>
    
    <!-- 主内容区域 -->
    <div class="login-content">
      <!-- 左侧介绍区域 -->
      <div class="welcome-section">
        <div class="welcome-content">
          <div class="platform-logo">
            <div class="logo-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h1 class="platform-title">在线教学平台</h1>
          </div>
          <h2 class="welcome-title">欢迎回来！</h2>
          <p class="welcome-subtitle">开启你的学习之旅，探索无限的知识海洋</p>
          
          <div class="feature-highlights">
            <div class="feature-item">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="feature-text">
                <h4>丰富课程</h4>
                <p>精选优质内容</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="feature-text">
                <h4>互动学习</h4>
                <p>实时交流经验</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="feature-text">
                <h4>AI 助手</h4>
                <p>智能学习指导</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录表单 -->
      <div class="login-form-section">
        <div class="login-form-container">
          <div class="form-header">
            <div class="form-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M15 3h4a2 2 0 012 2v14a2 2 0 01-2 2h-4M10 17l5-5-5-5M13.8 12H3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h2 class="form-title">登录您的账户</h2>
            <p class="form-subtitle">请输入您的登录信息</p>
          </div>
          
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" status-icon class="login-form">
            <el-form-item prop="username" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2M12 11a4 4 0 100-8 4 4 0 000 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  clearable
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <el-form-item prop="password" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                    <circle cx="12" cy="16" r="1" fill="currentColor"/>
                    <path d="M7 11V7a5 5 0 0110 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <el-input
                  v-model="loginForm.password"
                  placeholder="请输入密码"
                  show-password
                  clearable
                  type="password"
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <el-form-item class="form-item">
              <el-button 
                type="primary" 
                :loading="loading" 
                class="login-button btn-ripple" 
                @click="handleLogin"
              >
                <span v-if="!loading">登录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
            
            <div class="form-options">
              <el-link type="primary" class="option-link" @click="goToRegister">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M16 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2M12.5 11a4 4 0 100-8 4 4 0 000 8zM16 11h6M19 8v6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                没有账户？立即注册
              </el-link>
              <el-link type="info" class="option-link">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                  <path d="M9.09 9a3 3 0 015.83 1c0 2-3 3-3 3M12 17h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                忘记密码？
              </el-link>
            </div>
          </el-form>
          
          <div class="form-footer">
            <div class="divider">
              <span class="divider-text">或者</span>
            </div>
            <div class="social-login">
              <button class="social-btn">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 00-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0020 4.77 5.07 5.07 0 0019.91 1S18.73.65 16 2.48a13.38 13.38 0 00-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 005 4.77a5.44 5.44 0 00-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 009 18.13V22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                GitHub
              </button>
              <button class="social-btn">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M18 2h-3a5 5 0 00-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 011-1h3z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                微信
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'

const router = useRouter()
const loading = ref(false)
const loginFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const result = await login(loginForm)
    localStorage.setItem('token', result.data.token)
    
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    console.error('登录失败', error)
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}

// 生成粒子动画样式
const getParticleStyle = (index) => {
  const size = Math.random() * 6 + 4
  const animationDelay = Math.random() * 15
  const animationDuration = Math.random() * 10 + 15
  const left = Math.random() * 100
  const top = Math.random() * 100
  
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    top: `${top}%`,
    animationDelay: `${animationDelay}s`,
    animationDuration: `${animationDuration}s`
  }
}
</script>

<style scoped>
/* 登录容器 */
.login-container {
  height: 100vh;
  width: 100%;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
}

/* 背景动画 */
.background-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
}

/* 浮动粒子 */
.floating-particles {
  position: absolute;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: floatUp infinite linear;
  pointer-events: none;
}

@keyframes floatUp {
  0% {
    opacity: 0;
    transform: translateY(100vh) scale(0);
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    opacity: 0;
    transform: translateY(-100px) scale(1);
  }
}

/* 波浪动画 */
.wave-animation {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: auto;
}

.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: auto;
  animation: wave 20s ease-in-out infinite;
}

.wave-2 {
  animation: wave2 18s ease-in-out infinite;
}

@keyframes wave {
  0%, 100% {
    transform: translateX(0px);
  }
  50% {
    transform: translateX(-50px);
  }
}

@keyframes wave2 {
  0%, 100% {
    transform: translateX(0px) scaleX(-1);
  }
  50% {
    transform: translateX(50px) scaleX(-1);
  }
}

/* 主内容区域 */
.login-content {
  position: relative;
  z-index: 1;
  display: flex;
  height: 100vh;
  max-width: 1400px;
  margin: 0 auto;
}

/* 欢迎区域 */
.welcome-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: white;
}

.welcome-content {
  max-width: 500px;
}

.platform-logo {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 40px;
}

.logo-icon {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: logoFloat 3s ease-in-out infinite;
}

.logo-icon svg {
  width: 32px;
  height: 32px;
  color: white;
}

@keyframes logoFloat {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-10px) rotate(180deg);
  }
}

.platform-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
}

.welcome-title {
  font-size: 48px;
  font-weight: 800;
  margin: 0 0 20px 0;
  background: linear-gradient(45deg, #ffffff, #f0f8ff);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.welcome-subtitle {
  font-size: 18px;
  margin-bottom: 50px;
  opacity: 0.9;
  line-height: 1.6;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

/* 特性亮点 */
.feature-highlights {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all var(--vt-transition-medium) ease;
}

.feature-item:hover {
  transform: translateX(10px);
  background: rgba(255, 255, 255, 0.15);
}

.feature-icon {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

.feature-text h4 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: white;
}

.feature-text p {
  font-size: 14px;
  margin: 0;
  opacity: 0.8;
  color: white;
}

/* 表单区域 */
.login-form-section {
  flex: 0 0 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
}

.login-form-container {
  width: 100%;
  max-width: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 40px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  animation: slideInRight 0.8s ease-out;
}

@keyframes slideInRight {
  0% {
    opacity: 0;
    transform: translateX(50px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 表单头部 */
.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
  animation: iconPulse 2s ease-in-out infinite;
}

.form-icon svg {
  width: 28px;
  height: 28px;
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-heading);
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
  opacity: 0.8;
}

/* 表单样式 */
.login-form {
  margin-bottom: 30px;
}

.form-item {
  margin-bottom: 24px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  z-index: 2;
  color: var(--color-text-tertiary);
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color var(--vt-transition-fast) ease;
}

.input-icon svg {
  width: 16px;
  height: 16px;
}

.custom-input :deep(.el-input__wrapper) {
  padding-left: 50px !important;
  height: 50px !important;
  border-radius: 12px !important;
  border: 2px solid var(--color-border) !important;
  transition: all var(--vt-transition-fast) ease !important;
  background: rgba(255, 255, 255, 0.8) !important;
  backdrop-filter: blur(10px) !important;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: var(--color-primary) !important;
  background: rgba(255, 255, 255, 0.9) !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1) !important;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--color-primary) !important;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1) !important;
  background: white !important;
}

.custom-input :deep(.el-input__wrapper.is-focus) + .input-icon {
  color: var(--color-primary);
}

.custom-input :deep(.el-input__inner) {
  font-size: 15px !important;
  color: var(--color-text) !important;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 50px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  border-radius: 12px !important;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  position: relative;
  overflow: hidden;
  transition: all var(--vt-transition-fast) ease !important;
}

.login-button:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4) !important;
}

.login-button:active {
  transform: translateY(0) !important;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 30px;
}

.option-link {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all var(--vt-transition-fast) ease;
  text-decoration: none !important;
}

.option-link:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-1px);
}

.option-link svg {
  width: 16px;
  height: 16px;
}

/* 表单底部 */
.form-footer {
  margin-top: 30px;
}

.divider {
  position: relative;
  text-align: center;
  margin: 30px 0;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: var(--color-border);
}

.divider-text {
  background: rgba(255, 255, 255, 0.95);
  padding: 0 20px;
  color: var(--color-text-secondary);
  font-size: 14px;
  position: relative;
  z-index: 1;
}

.social-login {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.social-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  border: 2px solid var(--color-border);
  border-radius: 10px;
  background: white;
  color: var(--color-text-secondary);
  font-size: 14px;
  cursor: pointer;
  transition: all var(--vt-transition-fast) ease;
  text-decoration: none;
}

.social-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: rgba(102, 126, 234, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.social-btn svg {
  width: 18px;
  height: 18px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .login-content {
    flex-direction: column;
  }
  
  .welcome-section {
    flex: 0 0 auto;
    padding: 40px 20px;
    min-height: 40vh;
  }
  
  .welcome-content {
    max-width: 100%;
    text-align: center;
  }
  
  .welcome-title {
    font-size: 36px;
  }
  
  .feature-highlights {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .feature-item {
    flex: 0 1 calc(33% - 20px);
    min-width: 200px;
  }
  
  .login-form-section {
    flex: 1;
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .login-container {
    padding: 0;
  }
  
  .welcome-section {
    padding: 30px 20px;
    min-height: 35vh;
  }
  
  .welcome-title {
    font-size: 28px;
  }
  
  .welcome-subtitle {
    font-size: 16px;
  }
  
  .feature-highlights {
    flex-direction: column;
  }
  
  .feature-item {
    flex: none;
  }
  
  .login-form-section {
    padding: 20px 16px;
  }
  
  .login-form-container {
    padding: 30px 24px;
    border-radius: 16px;
  }
  
  .form-options {
    flex-direction: column;
    gap: 12px;
  }
  
  .social-login {
    flex-direction: column;
  }
}
</style> 