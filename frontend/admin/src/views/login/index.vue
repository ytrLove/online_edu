模板
<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>在线教学平台</h2>
        <p>管理员/教师登录</p>
      </div>
      <div class="login-form">
        <div class="form-item">
          <input
            v-model="loginForm.username"
            type="text"
            placeholder="请输入用户名"
            @keyup.enter="handleLogin"
          >
          <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
              <path fill="none" d="M0 0h24v24H0z"/>
              <path d="M12 4a4 4 0 0 1 4 4 4 4 0 0 1-4 4 4 4 0 0 1-4-4 4 4 0 0 1 4-4zm0 10c4.42 0 8 1.79 8 4v2H4v-2c0-2.21 3.58-4 8-4z" fill="currentColor"/>
            </svg>
          </span>
        </div>
        <div class="form-item">
          <input
            v-model="loginForm.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          >
          <span class="icon clickable" @click="showPassword = !showPassword">
            <svg v-if="!showPassword" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
              <path fill="none" d="M0 0h24v24H0z"/>
              <path d="M12 3c5.392 0 9.878 3.88 10.819 9-.94 5.12-5.427 9-10.819 9-5.392 0-9.878-3.88-10.819-9C2.121 6.88 6.608 3 12 3zm0 16a9.005 9.005 0 0 0 8.777-7 9.005 9.005 0 0 0-17.554 0A9.005 9.005 0 0 0 12 19zm0-2.5a4.5 4.5 0 1 1 0-9 4.5 4.5 0 0 1 0 9zm0-2a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z" fill="currentColor"/>
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
              <path fill="none" d="M0 0h24v24H0z"/>
              <path d="M17.882 19.297A10.949 10.949 0 0 1 12 21c-5.392 0-9.878-3.88-10.819-9a10.982 10.982 0 0 1 3.34-6.066L1.392 2.808l1.415-1.415 19.799 19.8-1.415 1.414-3.31-3.31zM5.935 7.35A8.965 8.965 0 0 0 3.223 12a9.005 9.005 0 0 0 13.201 5.838l-2.028-2.028A4.5 4.5 0 0 1 8.19 9.604L5.935 7.35zm6.979 6.978l-3.242-3.242a2.5 2.5 0 0 0 3.241 3.241zm7.893 2.264l-1.431-1.43A8.935 8.935 0 0 0 20.777 12 9.005 9.005 0 0 0 9.552 5.338L7.974 3.76C9.221 3.27 10.58 3 12 3c5.392 0 9.878 3.88 10.819 9a10.947 10.947 0 0 1-2.012 4.592zm-9.084-9.084a4.5 4.5 0 0 1 4.769 4.769l-4.77-4.769z" fill="currentColor"/>
            </svg>
          </span>
        </div>
        <div class="form-item checkbox">
          <label>
            <input
              v-model="loginForm.remember"
              type="checkbox"
            >
            <span>记住密码</span>
          </label>
        </div>
        <button
          class="login-button"
          :class="{ loading: loading }"
          @click="handleLogin"
          :disabled="loading"
        >
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const showPassword = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 初始化页面时检查本地存储
onMounted(() => {
  // 如果本地存储有用户名和密码，填充表单
  const savedUsername = localStorage.getItem('username')
  const savedPassword = localStorage.getItem('password')
  
  if (savedUsername) {
    loginForm.username = savedUsername
    loginForm.remember = true
  }
  
  if (savedPassword) {
    loginForm.password = savedPassword
  }
})

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }

  try {
    loading.value = true
    
    // 使用userStore登录
    const success = await userStore.loginAction({
      username: loginForm.username,
      password: loginForm.password
    })
    
    if (success) {
      // 登录成功，处理记住密码
      if (loginForm.remember) {
        localStorage.setItem('username', loginForm.username)
        localStorage.setItem('password', loginForm.password)
      } else {
        localStorage.removeItem('username')
        localStorage.removeItem('password')
      }
      
      ElMessage.success('登录成功')
      
      // 跳转到首页或重定向页面
      const redirect = router.currentRoute.value.query.redirect || '/'
      router.push(redirect)
    } else {
      ElMessage.error('登录失败，请检查用户名和密码')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  color: #333;
  font-size: 28px;
  margin-bottom: 8px;
}

.login-header p {
  color: #666;
  font-size: 16px;
}

.form-item {
  position: relative;
  margin-bottom: 20px;
}

.form-item input[type="text"],
.form-item input[type="password"] {
  width: 100%;
  padding: 12px 40px 12px 16px;
  border: 2px solid #e1e1e1;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s;
  outline: none;
}

.form-item input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-item .icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
}

.form-item .clickable {
  cursor: pointer;
}

.form-item.checkbox {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.form-item.checkbox label {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.form-item.checkbox input[type="checkbox"] {
  margin-right: 8px;
  width: 16px;
  height: 16px;
}

.login-button {
  width: 100%;
  padding: 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.login-button:hover {
  background: #5a6fd6;
}

.login-button:active {
  transform: scale(0.98);
}

.login-button.loading {
  opacity: 0.8;
  cursor: not-allowed;
}

@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: 30px;
  }
}
</style>
