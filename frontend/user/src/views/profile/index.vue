<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人设置</h2>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-form
            ref="userFormRef"
            :model="userForm"
            :rules="userRules"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="用户名">
              <el-input v-model="userForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="姓名" prop="nickname">
              <el-input v-model="userForm.nickname" placeholder="请输入姓名"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
            </el-form-item>
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入手机号码"></el-input>
            </el-form-item>
            <el-form-item label="头像">
              <div class="avatar-uploader">
                <el-avatar v-if="userForm.avatar" :size="100" :src="userForm.avatar"></el-avatar>
                <el-avatar v-else :size="100">{{ userForm.nickname ? userForm.nickname.charAt(0) : '学' }}</el-avatar>
                <el-upload
                  class="avatar-upload"
                  action="#"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                  :http-request="uploadAvatar"
                >
                  <el-button type="primary" class="avatar-button">更换头像</el-button>
                </el-upload>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitLoading" @click="submitUserInfo">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="修改密码" name="password">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                placeholder="请输入原密码"
                show-password
                type="password"
              ></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                placeholder="请输入新密码"
                show-password
                type="password"
              ></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                placeholder="请再次输入新密码"
                show-password
                type="password"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="passwordLoading" @click="changeUserPassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo, changePassword } from '@/api/auth'

// 标签页激活状态
const activeTab = ref('basic')

// 用户表单
const userFormRef = ref(null)
const userForm = reactive({
  userId: '',
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: ''
})

// 密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 加载状态
const submitLoading = ref(false)
const passwordLoading = ref(false)

// 表单校验规则
const userRules = {
  nickname: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 校验密码是否一致
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 获取用户信息
onMounted(async () => {
  await fetchUserInfo()
})

const fetchUserInfo = async () => {
  try {
    const result = await getUserInfo()
    const user = result.data.user
    // 填充表单
    Object.assign(userForm, {
      userId: user.userId,
      username: user.username,
      nickname: user.nickname,
      email: user.email,
      phone: user.phone,
      avatar: user.avatar
    })
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

// 提交用户信息
const submitUserInfo = async () => {
  if (!userFormRef.value) return
  try {
    await userFormRef.value.validate()
    submitLoading.value = true

    await updateUserInfo(userForm)
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    submitLoading.value = false
  }
}

// 修改密码
const changeUserPassword = async () => {
  if (!passwordFormRef.value) return
  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true

    await changePassword(passwordForm)
    ElMessage.success('密码修改成功')
    // 重置表单
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('密码修改失败', error)
  } finally {
    passwordLoading.value = false
  }
}

// 头像上传前校验
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 上传头像
const uploadAvatar = (options) => {
  // 模拟上传
  const file = options.file
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = (e) => {
    // 实际项目中应该调用API上传头像
    userForm.avatar = e.target.result
    ElMessage.success('头像上传成功')
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  margin: 0 auto;
  max-width: 800px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  font-size: 18px;
  margin: 0;
}

.profile-form {
  margin-top: 20px;
  max-width: 600px;
}

.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar-upload {
  margin-top: 10px;
}

.avatar-button {
  margin-top: 10px;
}
</style> 