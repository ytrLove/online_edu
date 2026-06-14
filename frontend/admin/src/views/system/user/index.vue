<template>
  <div class="user-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-actions">
            <el-button type="primary" plain v-permission="'system:user:add'" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增用户
            </el-button>
            <el-button type="danger" plain v-permission="'system:user:remove'" @click="handleBatchDelete" :disabled="!selectedRows.length">
              <el-icon><Delete /></el-icon>批量删除
            </el-button>
            <el-button type="warning" plain @click="fetchUserList">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="queryParams" @search="handleQuery" @reset="resetQuery">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="用户状态" clearable>
            <el-option label="正常" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
      </search-form>

      <!-- 表格区域 -->
      <data-table
        ref="tableRef"
        :data="userList"
        :loading="loading"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @pagination="fetchUserList"
        @update:current-page="currentPage = $event"
        @update:page-size="pageSize = $event"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="userId" label="用户ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column prop="nickname" label="昵称" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column prop="phone" label="手机号码" width="120" align="center" :show-overflow-tooltip="true" />
        <el-table-column label="角色" min-width="120" :show-overflow-tooltip="true">
          <template #default="{ row }">
            <el-tag
              v-for="role in row.roles"
              :key="role.roleId"
              class="role-tag"
              type="info"
              effect="light"
              size="small"
            >
              {{ role.roleName }}
            </el-tag>
            <span v-if="!row.roles || row.roles.length === 0" class="no-data">暂无角色</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 || row.status === '1' ? 'success' : 'danger'" size="small">
              {{ row.status === 1 || row.status === '1' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150" align="center" :show-overflow-tooltip="true" />
        <el-table-column label="操作" width="230" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'system:user:edit'" type="primary" size="small" text @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button v-permission="'system:user:query'" type="success" size="small" text @click="handleRole(row)">
              <el-icon><Setting /></el-icon>角色
            </el-button>
            <el-button v-permission="'system:user:remove'" type="danger" size="small" text @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
            <el-dropdown>
              <el-button type="info" size="small" text>
                <el-icon><More /></el-icon>更多
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-permission="'system:user:resetPwd'" @click="handleResetPassword(row)">
                    <el-icon><Key /></el-icon>重置密码
                  </el-dropdown-item>
                  <el-dropdown-item v-permission="'system:user:edit'" @click="handleToggleStatus(row)">
                    <el-icon><Switch /></el-icon>{{ row.status === 1 || row.status === '1' ? '禁用' : '启用' }}账号
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </data-table>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      :model-value="dialogVisible"
      @update:model-value="dialogVisible = $event"
      :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog
      :model-value="showPermModal"
      @update:model-value="showPermModal = $event"
      title="分配角色"
      width="500px"
      destroy-on-close
    >
      <el-form label-width="80px">
        <el-form-item label="角色">
          <el-select
            v-model="AssignRolesList.roleIds"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleOptions"
              :key="role.roleId"
              :label="role.roleKey"
              :value="role.roleId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showPermModal = false">取消</el-button>
          <el-button type="primary"  v-permission="'system:user:assignRole'" @click="handleSubmitAssignRole" >确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      :model-value="resetPwdVisible"
      @update:model-value="resetPwdVisible = $event"
      title="重置密码"
      width="500px"
      destroy-on-close
    >
      <el-form ref="resetPwdFormRef" :model="resetPwdForm" :rules="resetPwdRules" label-width="100px">
        <el-form-item label="新密码" prop="password">
          <el-input
            v-model="resetPwdForm.password"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="resetPwdForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="resetPwdVisible = false">取消</el-button>
          <el-button type="primary" @click="submitResetPassword">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, deleteUser, assignRoles, resetPassword, updateUserStatus } from '@/api/system/user'
import { getRoleList } from '@/api/system/role'
import { Edit, Delete, Setting, Plus, Refresh, More, Key, Switch } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm/index.vue'
import DataTable from '@/components/DataTable/index.vue'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 6,
  username: undefined,
  phone: undefined,
  status: undefined
})

// 选中行
const selectedRows = ref([])
const tableRef = ref(null)

// 遮罩层
const loading = ref(false)
const AssignRolesList = ref({
  userId: '',
  roleIds: []
})

const userList = ref([])
const currentPage = ref(1)
const pageSize = ref(6)
const total = ref(0)
const roleOptions = ref([])
const showPermModal = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const formData = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  password: '',
  status: 1
})

// 重置密码相关
const resetPwdVisible = ref(false)
const resetPwdFormRef = ref(null)
const resetPwdForm = reactive({
  userId: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: false, message: '请输入昵称', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ]
}

// 重置密码表单校验规则
const resetPwdRules = {
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetPwdForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 初始化
onMounted(() => {
  fetchUserList()
  fetchRoleList()
})

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 获取用户列表
const fetchUserList = async (params) => {
  loading.value = true
  try {
    const response = await getUserList({
      pageNum: params?.page || currentPage.value,
      pageSize: params?.limit || pageSize.value,
      username: queryParams.username,
      status: queryParams.status
    })
    userList.value = response.data.rows
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 获取角色列表
const fetchRoleList = async () => {
  try {
    const response = await getRoleList()
    roleOptions.value = response.data.rows
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  }
}

// 搜索相关
const handleQuery = () => {
  currentPage.value = 1
  fetchUserList()
}

const resetQuery = () => {
  queryParams.username = undefined
  queryParams.phone = undefined
  queryParams.status = undefined
  currentPage.value = 1
  fetchUserList()
}

// 添加用户
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  formData.username = ''
  formData.nickname = ''
  formData.phone = ''
  formData.email = ''
  formData.password = ''
  formData.status = 1
}

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(formData, {
    userId: row.userId,
    username: row.username,
    nickname: row.nickname,
    phone: row.phone,
    email: row.email,
    status: row.status
  })
}

// 分配角色
const handleRole = (row) => {
  showPermModal.value = true
  AssignRolesList.value.userId = row.userId
  AssignRolesList.value.roleIds = row.roles ? row.roles.map(role => role.roleId) : []
}

// 提交分配角色
const handleSubmitAssignRole = async () => {
  try {
    // 构建符合AssignRolesRequest的数据格式
    const roleData = {
      userId: AssignRolesList.value.userId,
      roleIds: AssignRolesList.value.roleIds
    }
    await assignRoles(roleData.userId, roleData.roleIds)
    ElMessage.success('分配角色成功')
    showPermModal.value = false
    fetchUserList()
  } catch (error) {
    ElMessage.error('分配角色失败')
  }
}

// 批量删除用户
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }
  
  const userIds = selectedRows.value.map(row => row.userId)
  ElMessageBox.confirm(
    `确认删除选中的 ${selectedRows.value.length} 个用户吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const promises = userIds.map(id => deleteUser(id))
      await Promise.all(promises)
      ElMessage.success('批量删除成功')
      await fetchUserList()
      if (tableRef.value) {
        tableRef.value.clearSelection()
      }
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  })
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确认删除该用户吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteUser(row.userId)
      ElMessage.success('删除成功')
      await fetchUserList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 重置密码
const handleResetPassword = (row) => {
  resetPwdVisible.value = true
  resetPwdForm.userId = row.userId
  resetPwdForm.password = ''
  resetPwdForm.confirmPassword = ''
}

// 提交重置密码
const submitResetPassword = async () => {
  if (!resetPwdFormRef.value) return
  
  await resetPwdFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 创建符合ResetPasswordRequest的对象
        const resetData = {
          userId: resetPwdForm.userId,
          password: resetPwdForm.password
        };
        await resetPassword(resetData)
        ElMessage.success('密码重置成功')
        resetPwdVisible.value = false
      } catch (error) {
        ElMessage.error('密码重置失败')
      }
    }
  })
}

// 切换用户状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 || row.status === '1' ? 0 : 1
  try {
    // 调用更新方法，使用UserUpdateRequest格式的数据
    await updateUser({
      userId: row.userId,
      status: Number(newStatus) // 确保状态是数字类型
    })
    ElMessage.success(`用户${newStatus === 1 ? '启用' : '禁用'}成功`)
    fetchUserList()
  } catch (error) {
    ElMessage.error(`用户${newStatus === 1 ? '启用' : '禁用'}失败`)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          // 构建符合UserCreateRequest的数据
          const userData = {
            username: formData.username,
            password: formData.password,
            nickname: formData.nickname || undefined,
            phone: formData.phone || undefined,
            email: formData.email || undefined,
            status: Number(formData.status), // 确保状态是数字类型
            remark: formData.remark || undefined
          }
          await addUser(userData)
          ElMessage.success('添加成功')
        } else {
          // 构建符合UserUpdateRequest的数据
          const userData = {
            userId: formData.userId,
            nickname: formData.nickname || undefined,
            phone: formData.phone || undefined,
            email: formData.email || undefined,
            status: Number(formData.status), // 确保状态是数字类型
            remark: formData.remark || undefined
          }
          await updateUser(userData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        await fetchUserList()
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
      }
    }
  })
}
</script>

<style scoped>
.user-container {
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

.role-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

.no-data {
  color: #909399;
  font-size: 13px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 