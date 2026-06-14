<template>
    <div class="container profile-container">
        <el-row :gutter="15">
            <!-- 个人信息卡片 -->
            <el-col :xs="24" :sm="24" :md="8" :lg="7" :xl="6">
                <el-card class="profile-card" shadow="hover">
                    <div class="profile-header">
                        <el-avatar :size="90" :src="user.avatar || defaultAvatar" class="avatar"></el-avatar>
                        <h2 class="username">{{ user.username || '未知用户' }}</h2>
                        <el-tag size="small" type="info" effect="plain" class="role-tag">
                            {{ user.roles && user.roles.length > 0 ? user.roles[0].roleName : '暂无角色' }}
                        </el-tag>
                    </div>
                    <div class="profile-info">
                        <div class="info-item">
                            <el-icon><User /></el-icon>
                            <span>{{ user.nickname || '暂无昵称' }}</span>
                        </div>
                        <div class="info-item">
                            <el-icon><Message /></el-icon>
                            <span>{{ user.email || '暂无邮箱' }}</span>
                        </div>
                        <div class="info-item">
                            <el-icon><Phone /></el-icon>
                            <span>{{ user.phone || '暂无电话' }}</span>
                        </div>
                        <div class="info-item">
                            <el-icon><Calendar /></el-icon>
                            <span>{{ formatDate(user.createTime) || '暂无创建时间' }}</span>
                        </div>
                    </div>
                </el-card>
            </el-col>

            <!-- 右侧选项卡 -->
            <el-col :xs="24" :sm="24" :md="16" :lg="17" :xl="18">
                <el-card shadow="hover" class="tabs-card">
                    <el-tabs v-model="activeTab" class="profile-tabs">
                        <!-- 基本资料 -->
                        <el-tab-pane label="基本资料" name="basic">
                            <el-form 
                                ref="basicInfoFormRef" 
                                :model="basicInfoForm" 
                                :rules="basicInfoRules" 
                                label-width="90px"
                                class="profile-form"
                            >
                                <el-form-item label="用户昵称" prop="nickname">
                                    <el-input v-model="basicInfoForm.nickname" placeholder="请输入用户昵称"></el-input>
                                </el-form-item>
                                <el-form-item label="手机号码" prop="phone">
                                    <el-input v-model="basicInfoForm.phone" placeholder="请输入手机号码"></el-input>
                                </el-form-item>
                                <el-form-item label="邮箱" prop="email">
                                    <el-input v-model="basicInfoForm.email" placeholder="请输入邮箱"></el-input>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="updateBasicInfo">保存更新</el-button>
                                </el-form-item>
                            </el-form>
                        </el-tab-pane>

                        <!-- 修改密码 -->
                        <el-tab-pane label="修改密码" name="password">
                            <el-form 
                                ref="passwordFormRef" 
                                :model="passwordForm" 
                                :rules="passwordRules" 
                                label-width="90px"
                                class="profile-form"
                            >
                                <el-form-item label="旧密码" prop="oldPassword">
                                    <el-input 
                                        v-model="passwordForm.oldPassword" 
                                        placeholder="请输入旧密码" 
                                        type="password" 
                                        show-password>
                                    </el-input>
                                </el-form-item>
                                <el-form-item label="新密码" prop="newPassword">
                                    <el-input 
                                        v-model="passwordForm.newPassword" 
                                        placeholder="请输入新密码" 
                                        type="password" 
                                        show-password>
                                    </el-input>
                                </el-form-item>
                                <el-form-item label="确认密码" prop="confirmPassword">
                                    <el-input 
                                        v-model="passwordForm.confirmPassword" 
                                        placeholder="请确认新密码" 
                                        type="password" 
                                        show-password>
                                    </el-input>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="updatePassword">修改密码</el-button>
                                    <el-button @click="resetPasswordForm">重置</el-button>
                                </el-form-item>
                            </el-form>
                        </el-tab-pane>
                    </el-tabs>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, changePassword, updateUserInfo } from '@/api/auth'
import { parseTime } from '@/utils/ruoyi'
import { User, Message, Phone, Calendar } from '@element-plus/icons-vue'
import router from '@/router'

// 默认头像
const defaultAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png') 

// 用户信息
const user = ref({})

// 活动选项卡
const activeTab = ref('basic')

// 基本资料表单
const basicInfoFormRef = ref(null)
const basicInfoForm = reactive({
    nickname: '',
    phone: '',
    email: ''
})

// 基本资料表单校验规则
const basicInfoRules = {
    nickName: [
        { required: true, message: '用户昵称不能为空', trigger: 'blur' }
    ],
    phoneNumber: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ],
    email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ]
}

// 修改密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

// 修改密码表单校验规则
const passwordRules = {
    oldPassword: [
        { required: true, message: '旧密码不能为空', trigger: 'blur' }
    ],
    newPassword: [
        { required: true, message: '新密码不能为空', trigger: 'blur' },
        { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '确认密码不能为空', trigger: 'blur' },
        {
            validator: (rule, value, callback) => {
                if (value !== passwordForm.newPassword) {
                    callback(new Error('两次输入的密码不一致'))
                } else {
                    callback()
                }
            },
            trigger: 'blur'
        }
    ]
}

// 格式化日期
const formatDate = (time) => {
    if (!time) return '';
    return parseTime(time);
}

// 获取用户信息
const fetchUserInfo = async () => {
    try {
        const res = await getUserInfo()
        if (res && res.data) {
            user.value = res.data.user
            // 填充基本信息表单
            basicInfoForm.nickname = res.data.user.nickname || ''
            basicInfoForm.phone = res.data.user.phone || ''
            basicInfoForm.email = res.data.user.email || ''
        }
    } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败')
    }
}

// 更新基本资料
const updateBasicInfo = async () => {
    if (!basicInfoFormRef.value) return
    
    try {
        await basicInfoFormRef.value.validate()
        
        // 调用更新用户信息API
        await updateUserInfo({
            nickname: basicInfoForm.nickname,
            phone: basicInfoForm.phone,
            email: basicInfoForm.email
        })
        
        ElMessage.success('基本资料更新成功')
        fetchUserInfo() // 重新获取用户信息
    } catch (error) {
        console.error('基本资料更新失败:', error)
        ElMessage.error('基本资料更新失败: ' + (error.message || '未知错误'))
    }
}

// 修改密码
const updatePassword = async () => {
    if (!passwordFormRef.value) return
    
    try {
        await passwordFormRef.value.validate()
        
        await changePassword({
            oldPassword: passwordForm.oldPassword,
            newPassword: passwordForm.newPassword
        })
        
        ElMessage.success('密码修改成功')
        resetPasswordForm()
        router.push('/login')
    } catch (error) {
        console.error('密码修改失败:', error)
        ElMessage.error('密码修改失败: ' + (error.message || '未知错误'))
    }
}

// 重置密码表单
const resetPasswordForm = () => {
    if (passwordFormRef.value) {
        passwordFormRef.value.resetFields()
    }
}

// 组件挂载时获取用户信息
onMounted(() => {
    fetchUserInfo()
})
</script>

<style scoped>
.container {
    padding: 15px;
}

.profile-container {
    max-width: 1200px;
    margin: 0 auto;
}

.profile-card {
    height: 100%;
    margin-bottom: 15px;
}

.profile-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
}

.avatar {
    margin-bottom: 12px;
    border: 2px solid #e6e6e6;
}

.username {
    margin: 8px 0 5px;
    font-size: 18px;
    font-weight: 600;
    color: #333;
}

.role-tag {
    margin: 5px 0 10px;
}

.profile-info {
    padding: 15px 0;
}

.info-item {
    display: flex;
    align-items: center;
    padding: 10px 0;
    font-size: 14px;
    color: #606266;
    border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
    border-bottom: none;
}

.info-item .el-icon {
    margin-right: 10px;
    font-size: 16px;
    color: #409EFF;
}

.tabs-card {
    margin-bottom: 15px;
}

.profile-tabs :deep(.el-tabs__header) {
    margin-bottom: 15px;
}

.profile-form {
    max-width: 600px;
    padding: 0 10px;
}

/* 表单样式 */
:deep(.el-form-item) {
    margin-bottom: 18px;
}

:deep(.el-input) {
    max-width: 100%;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
    .app-container {
        padding: 10px;
    }
    
    .profile-form {
        padding: 0;
    }
    
    :deep(.el-form-item__label) {
        padding-right: 5px;
    }
}

@media screen and (min-width: 992px) {
    .profile-card {
        margin-bottom: 0;
    }
    .tabs-card {
        margin-bottom: 0;
    }
}
</style>