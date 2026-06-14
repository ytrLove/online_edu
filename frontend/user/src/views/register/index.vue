<template>
  <div class="register-container">
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
    <div class="register-content">
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
          <h2 class="welcome-title">加入我们！</h2>
          <p class="welcome-subtitle">创建您的学习账户，开始您的知识探索之旅</p>
          
          <div class="feature-highlights">
            <div class="feature-item">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M16 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2M12.5 11a4 4 0 100-8 4 4 0 000 8zM16 11h6M19 8v6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="feature-text">
                <h4>快速注册</h4>
                <p>简单几步快速创建账户</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12l2 2 4-4M21 12c0 4.97-4.03 9-9 9s-9-4.03-9-9 4.03-9 9-9 9 4.03 9 9z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="feature-text">
                <h4>安全可靠</h4>
                <p>专业级数据加密保护</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="feature-text">
                <h4>即即体验</h4>
                <p>注册后立即享受学习体验</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧注册表单 -->
      <div class="register-form-section">
        <div class="register-form-container">
          <div class="form-header">
            <div class="form-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M16 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2M12.5 11a4 4 0 100-8 4 4 0 000 8zM16 11h6M19 8v6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h2 class="form-title">创建您的账户</h2>
            <p class="form-subtitle">请填写以下信息完成注册</p>
          </div>
          
          <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" status-icon class="register-form">
            <div class="form-row">
              <el-form-item prop="username" class="form-item">
                <div class="input-wrapper">
                  <div class="input-icon">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2M12 11a4 4 0 100-8 4 4 0 000 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </div>
                  <el-input
                    v-model="registerForm.username"
                    placeholder="请输入用户名"
                    clearable
                    class="custom-input"
                  />
                </div>
              </el-form-item>
              
              <el-form-item prop="nickname" class="form-item">
                <div class="input-wrapper">
                  <div class="input-icon">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2M12 11a4 4 0 100-8 4 4 0 000 8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </div>
                  <el-input
                    v-model="registerForm.nickname"
                    placeholder="请输入姓名"
                    clearable
                    class="custom-input"
                  />
                </div>
              </el-form-item>
            </div>
            
            <el-form-item prop="email" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="currentColor" stroke-width="2"/>
                    <polyline points="22,6 12,13 2,6" stroke="currentColor" stroke-width="2"/>
                  </svg>
                </div>
                <el-input
                  v-model="registerForm.email"
                  placeholder="请输入邮箱地址"
                  clearable
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <el-form-item prop="phone" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07 19.5 19.5 0 01-6-6 19.79 19.79 0 01-3.07-8.67A2 2 0 014.11 2h3a2 2 0 012 1.72 12.84 12.84 0 00.7 2.81 2 2 0 01-.45 2.11L8.09 9.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45 12.84 12.84 0 002.81.7A2 2 0 0122 16.92z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <el-input
                  v-model="registerForm.phone"
                  placeholder="请输入手机号码"
                  clearable
                  class="custom-input"
                />
              </div>
            </el-form-item>
            
            <div class="form-row">
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
                    v-model="registerForm.password"
                    placeholder="请输入密码"
                    show-password
                    clearable
                    type="password"
                    class="custom-input"
                  />
                </div>
              </el-form-item>
              
              <el-form-item prop="confirmPassword" class="form-item">
                <div class="input-wrapper">
                  <div class="input-icon">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M9 12l2 2 4-4M21 12c0 4.97-4.03 9-9 9s-9-4.03-9-9 4.03-9 9-9 9 4.03 9 9z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </div>
                  <el-input
                    v-model="registerForm.confirmPassword"
                    placeholder="请再次输入密码"
                    show-password
                    clearable
                    type="password"
                    class="custom-input"
                  />
                </div>
              </el-form-item>
            </div>
            
            <el-form-item class="form-item">
              <el-button 
                type="primary" 
                :loading="loading" 
                class="register-button btn-ripple" 
                @click="handleRegister"
              >
                <span v-if="!loading">创建账户</span>
                <span v-else>注册中...</span>
              </el-button>
            </el-form-item>
            
            <div class="form-options">
              <div class="agreement-text">
                注册即表示您同意我们的
                <el-link type="primary">用户协议</el-link>
                和
                <el-link type="primary">隐私政策</el-link>
              </div>
              <el-link type="info" class="option-link" @click="goToLogin">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M15 3h4a2 2 0 012 2v14a2 2 0 01-2 2h-4M10 17l5-5-5-5M13.8 12H3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                已有账户？立即登录
              </el-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()
const loading = ref(false)
const registerFormRef = ref(null)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: '',
  phone: ''
})

// 密码一致性验证
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    const { confirmPassword, ...data } = registerForm
    await register(data)
    
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败', error)
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
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
/* 注册容器 - 与登录页面保持一致的设计风格 */
.register-container {
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
.register-content {
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
.register-form-section {
  flex: 0 0 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
}

.register-form-container {
  width: 100%;
  max-width: 480px;
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
.register-form {
  margin-bottom: 30px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-item {
  margin-bottom: 24px;
}

.form-row .form-item {
  margin-bottom: 0;
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

/* 注册按钮 */
.register-button {
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

.register-button:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4) !important;
}

.register-button:active {
  transform: translateY(0) !important;
}

/* 表单选项 */
.form-options {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
  text-align: center;
}

.agreement-text {
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.agreement-text .el-link {
  font-size: 13px;
  margin: 0 2px;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .register-content {
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
  
  .register-form-section {
    flex: 1;
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .register-container {
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
  
  .register-form-section {
    padding: 20px 16px;
  }
  
  .register-form-container {
    padding: 30px 24px;
    border-radius: 16px;
    max-width: 100%;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
  
  .form-row .form-item {
    margin-bottom: 24px;
  }
}
</style> 