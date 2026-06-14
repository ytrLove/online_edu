<template>
  <div class="app-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
    
    <el-container>
      <el-header height="70px" class="main-header">
        <div class="header-container">
          <div class="logo-section">
            <div class="logo-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="logo-content">
              <h1 class="logo-title">
                <span class="title-main">在线教学平台</span>
                <span class="title-sub">学生端</span>
              </h1>
            </div>
          </div>
          
          <nav class="navigation">
            <el-menu
              :default-active="activeIndex"
              class="nav-menu"
              mode="horizontal"
              router
            >
              <el-menu-item index="/dashboard" class="nav-item">
                <el-icon><HomeFilled /></el-icon>
                <span>首页</span>
              </el-menu-item>
              <el-menu-item index="/courses" class="nav-item">
                <el-icon><Reading /></el-icon>
                <span>课程中心</span>
              </el-menu-item>
              <el-menu-item index="/my-courses" class="nav-item">
                <el-icon><User /></el-icon>
                <span>我的课程</span>
              </el-menu-item>
              <el-menu-item index="/assignments" class="nav-item">
                <el-icon><Document /></el-icon>
                <span>作业考试</span>
              </el-menu-item>
              <el-menu-item index="/discussion" class="nav-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>讨论区</span>
              </el-menu-item>
              <el-menu-item index="/ai-assistant" class="nav-item">
                <el-icon><Cpu /></el-icon>
                <span>AI助手</span>
              </el-menu-item>
            </el-menu>
          </nav>
          
          <div class="user-section">
            <el-dropdown trigger="hover" @command="handleCommand" class="user-dropdown">
              <div class="user-info">
                <div class="avatar-wrapper">
                  <img v-if="user.avatar" :src="user.avatar" class="user-avatar" />
                  <el-avatar v-else :size="42" class="user-avatar-default">
                    {{ user.nickname ? user.nickname.charAt(0) : '学' }}
                  </el-avatar>
                  <div class="online-indicator"></div>
                </div>
                <div class="user-details">
                  <span class="user-name">{{ user.nickname || '同学' }}</span>
                  <span class="user-role">学生</span>
                </div>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="user-menu">
                  <el-dropdown-item command="profile" class="menu-item">
                    <el-icon><User /></el-icon>
                    <span>个人设置</span>
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout" class="menu-item logout">
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <div class="content-area">
          <transition name="page" mode="out-in">
            <router-view />
          </transition>
        </div>
      </el-main>
      
      <el-footer height="80px" class="main-footer">
        <div class="footer-container">
          <div class="footer-content">
            <div class="footer-info">
              <div class="footer-logo">
                <span class="footer-title">在线教学平台</span>
              </div>
              <p class="copyright">© 2025 在线教学平台 - 学生端 版权所有</p>
            </div>
            <div class="footer-links">
              <a href="#" class="footer-link">帮助中心</a>
              <a href="#" class="footer-link">联系我们</a>
              <a href="#" class="footer-link">隐私政策</a>
            </div>
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ArrowDown, 
  HomeFilled, 
  Reading, 
  User, 
  Document, 
  ChatDotRound, 
  Cpu,
  SwitchButton
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo, logout } from '@/api/auth'

const router = useRouter()
const activeIndex = computed(() => router.currentRoute.value.path)
const user = ref({})

onMounted(async () => {
  try {
    const result = await getUserInfo()
    user.value = result.data.user
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
})

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    ).then(async () => {
      try {
        await logout()
        localStorage.removeItem('token')
        ElMessage.success('退出登录成功')
        router.push('/login')
      } catch (error) {
        console.error('退出登录失败', error)
      }
    }).catch(() => {})
  }
}
</script>

<style scoped>
/* 容器样式 */
.app-container {
  height: 100vh;
  width: 100%;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-attachment: fixed;
}

/* 背景装饰 */
.background-decoration {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: float 20s infinite linear;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 70%;
  right: 10%;
  animation-delay: -7s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  top: 40%;
  left: 80%;
  animation-delay: -14s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  33% {
    transform: translateY(-30px) rotate(120deg);
  }
  66% {
    transform: translateY(30px) rotate(240deg);
  }
}

.el-container {
  height: 100%;
  position: relative;
  z-index: 1;
}

/* 头部样式 */
.main-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  position: relative;
}

.header-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Logo 区域 */
.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all var(--vt-transition-medium) ease;
}

.logo-icon:hover {
  transform: rotate(360deg) scale(1.1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.logo-icon svg {
  width: 24px;
  height: 24px;
}

.logo-content {
  display: flex;
  flex-direction: column;
}

.logo-title {
  font-size: 20px;
  margin: 0;
  line-height: 1.2;
}

.title-main {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 700;
  font-size: 18px;
}

.title-sub {
  color: var(--color-text-secondary);
  font-weight: 500;
  font-size: 12px;
  margin-left: 4px;
}

/* 导航样式 */
.navigation {
  flex: 1;
  display: flex;
  justify-content: center;
  margin: 0 40px;
}

.nav-menu {
  border: none !important;
  background: transparent !important;
  display: flex;
  gap: 8px;
}

.nav-item {
  border-radius: 12px !important;
  margin: 0 !important;
  padding: 0 20px !important;
  height: 44px !important;
  line-height: 44px !important;
  transition: all var(--vt-transition-fast) ease !important;
  background: transparent !important;
  color: var(--color-text) !important;
  position: relative;
  overflow: hidden;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 0;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: width var(--vt-transition-medium) ease;
  z-index: -1;
}

.nav-item:hover::before {
  width: 100%;
}

.nav-item:hover {
  color: white !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.nav-item.is-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.nav-item .el-icon {
  margin-right: 6px;
  font-size: 16px;
}

/* 用户区域 */
.user-section {
  display: flex;
  align-items: center;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all var(--vt-transition-fast) ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.95);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.avatar-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.user-avatar,
.user-avatar-default {
  width: 42px !important;
  height: 42px !important;
  border-radius: 50%;
  border: 3px solid rgba(102, 126, 234, 0.2);
  transition: all var(--vt-transition-fast) ease;
}

.user-avatar:hover,
.user-avatar-default:hover {
  border-color: rgba(102, 126, 234, 0.5);
  transform: scale(1.05);
}

.online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #52c41a;
  border: 2px solid white;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.user-name {
  font-weight: 600;
  font-size: 14px;
  color: var(--color-text);
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: var(--color-text-secondary);
  line-height: 1.2;
}

.dropdown-icon {
  color: var(--color-text-secondary);
  font-size: 12px;
  transition: transform var(--vt-transition-fast) ease;
}

.user-dropdown:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 下拉菜单 */
:deep(.user-menu) {
  border-radius: 12px !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1) !important;
  backdrop-filter: blur(20px) !important;
  background: rgba(255, 255, 255, 0.95) !important;
  padding: 8px !important;
}

:deep(.menu-item) {
  border-radius: 8px !important;
  margin: 2px 0 !important;
  padding: 12px 16px !important;
  transition: all var(--vt-transition-fast) ease !important;
  display: flex !important;
  align-items: center !important;
  gap: 8px !important;
}

:deep(.menu-item:hover) {
  background: rgba(102, 126, 234, 0.1) !important;
  color: var(--color-primary) !important;
  transform: translateX(4px);
}

:deep(.menu-item.logout:hover) {
  background: rgba(255, 77, 79, 0.1) !important;
  color: #ff4d4f !important;
}

/* 主内容区域 */
.main-content {
  background: transparent;
  padding: 0;
  position: relative;
  overflow-y: auto;
}

.content-area {
  min-height: 100%;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 24px 24px 0 0;
  margin: 20px 20px 0 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 页面切换动画 */
.page-enter-active,
.page-leave-active {
  transition: all var(--vt-transition-medium) ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 页脚样式 */
.main-footer {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  margin: 0 20px;
  border-radius: 0 0 24px 24px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.1);
}

.footer-container {
  height: 100%;
  display: flex;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.footer-title {
  font-weight: 600;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.copyright {
  color: var(--color-text-secondary);
  font-size: 13px;
  margin: 0;
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-link {
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: 14px;
  transition: all var(--vt-transition-fast) ease;
  padding: 8px 12px;
  border-radius: 8px;
}

.footer-link:hover {
  color: var(--color-primary);
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .navigation {
    margin: 0 20px;
  }
  
  .nav-item {
    padding: 0 16px !important;
  }
  
  .user-details {
    display: none;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }
  
  .logo-content .title-sub {
    display: none;
  }
  
  .navigation {
    display: none;
  }
  
  .content-area {
    margin: 10px;
    border-radius: 16px 16px 0 0;
  }
  
  .main-footer {
    margin: 0 10px;
    border-radius: 0 0 16px 16px;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
  
  .footer-links {
    gap: 16px;
  }
}
</style>