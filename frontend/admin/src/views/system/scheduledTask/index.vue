<template>
    <div class="scheduled-task-container">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span>定时任务管理</span>
            <div class="header-actions">
              <el-button type="primary" plain v-permission="'system:task:add'" @click="handleAdd">
                <el-icon><Plus /></el-icon>新增任务
              </el-button>
              <el-button type="danger" plain v-permission="'system:task:remove'" @click="handleBatchDelete" :disabled="!selectedRows.length">
                <el-icon><Delete /></el-icon>批量删除
              </el-button>
              <el-button type="warning" plain @click="fetchTaskList">
                <el-icon><Refresh /></el-icon>刷新
              </el-button>
            </div>
          </div>
        </template>
        
        <!-- 搜索区域 -->
        <search-form :form-model="queryParams" @search="handleQuery" @reset="resetQuery">
          <el-form-item label="任务名称">
            <el-input v-model="queryParams.taskName" placeholder="请输入任务名称" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" placeholder="任务状态" clearable>
              <el-option label="启用" value="1" />
              <el-option label="禁用" value="0" />
            </el-select>
          </el-form-item>
        </search-form>
  
        <!-- 表格区域 -->
        <data-table
          ref="tableRef"
          :data="taskList"
          :loading="loading"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          @pagination="fetchTaskList"
          @update:current-page="currentPage = $event"
          @update:page-size="pageSize = $event"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="taskName" label="任务名称" min-width="150" :show-overflow-tooltip="true" />
          <el-table-column prop="taskDescription" label="任务描述" min-width="150" :show-overflow-tooltip="true" />
          <el-table-column prop="taskClass" label="任务类" min-width="200" :show-overflow-tooltip="true" />
          <el-table-column prop="taskMethod" label="任务方法" width="120" align="center" />
          <el-table-column prop="cronExpression" label="Cron表达式" width="150" align="center" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
          <el-table-column label="操作" width="280" align="center" fixed="right">
            <template #default="{ row }">
              <el-button v-permission="'system:task:edit'" type="primary" size="small" text @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
              <el-button v-permission="'system:task:execute'" type="success" size="small" text @click="handleExecute(row)">
                <el-icon><CaretRight /></el-icon>执行
              </el-button>
              <el-button v-permission="'system:task:log'" type="info" size="small" text @click="handleShowLogs(row)">
                <el-icon><Document /></el-icon>日志
              </el-button>
              <el-button v-permission="'system:task:remove'" type="danger" size="small" text @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>删除
              </el-button>
              <el-dropdown>
                <el-button type="info" size="small" text>
                  <el-icon><More /></el-icon>更多
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-permission="'system:task:status'" @click="handleToggleStatus(row)">
                      <el-icon><Switch /></el-icon>{{ row.status === 1 ? '禁用' : '启用' }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
        </data-table>
      </el-card>
  
      <!-- 添加/编辑任务对话框 -->
      <el-dialog
        :model-value="dialogVisible"
        @update:model-value="dialogVisible = $event"
        :title="dialogType === 'add' ? '添加定时任务' : '编辑定时任务'"
        width="600px"
        destroy-on-close
      >
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="120px"
        >
          <el-form-item label="任务名称" prop="taskName">
            <el-input v-model="formData.taskName" placeholder="请输入任务名称" />
          </el-form-item>
          <el-form-item label="任务描述" prop="taskDescription">
            <el-input v-model="formData.taskDescription" placeholder="请输入任务描述" />
          </el-form-item>
          <el-form-item label="任务方法" prop="taskMethod">
            <el-input v-model="formData.taskMethod" placeholder="请输入任务方法名" />
          </el-form-item>
          <el-form-item label="Cron表达式" prop="cronExpression">
            <el-input v-model="formData.cronExpression" placeholder="请输入Cron表达式">
              <template #append>
                <el-button @click="showCronDialog = true">生成</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="任务参数" prop="params">
            <el-input v-model="formData.params" placeholder="请输入任务参数(JSON格式)" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
          </div>
        </template>
      </el-dialog>
  
      <!-- Cron表达式生成器对话框 -->
      <el-dialog
        v-model="showCronDialog"
        title="Cron表达式生成器"
        width="700px"
        destroy-on-close
      >
        <cron-editor v-model="formData.cronExpression" />
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="showCronDialog = false">取消</el-button>
            <el-button type="primary" @click="showCronDialog = false">确定</el-button>
          </div>
        </template>
      </el-dialog>
  
      <!-- 任务日志对话框 -->
      <el-dialog
        v-model="showLogsDialog"
        :title="`任务日志 - ${currentTask?.taskName}`"
        width="80%"
        destroy-on-close
      >
        <el-table :data="taskLogs" border style="width: 100%">
          <el-table-column prop="startTime" label="开始时间" width="180" />
          <el-table-column prop="endTime" label="结束时间" width="180" />
          <el-table-column label="执行状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.executionStatus === 1 ? 'success' : 'danger'">
                {{ row.executionStatus === 1 ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="executionResult" label="执行结果" :show-overflow-tooltip="true" />
          <el-table-column prop="errorMessage" label="错误信息" :show-overflow-tooltip="true" />
        </el-table>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="showLogsDialog = false">关闭</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, reactive } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { 
    getScheduledTaskList, 
    addScheduledTask, 
    updateScheduledTask, 
    deleteScheduledTask, 
    getTaskLogs,
    executeTaskNow,
    changeTaskStatus
  } from '@/api/system/scheduledTask'
  import { Edit, Delete, Plus, Refresh, More, Switch, CaretRight, Document } from '@element-plus/icons-vue'
  import SearchForm from '@/components/SearchForm/index.vue'
  import DataTable from '@/components/DataTable/index.vue'
  import CronEditor from '@/components/CronEditor/index.vue'
  
  // 查询参数
  const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    taskName: undefined,
    status: undefined
  })
  
  // 选中行
  const selectedRows = ref([])
  const tableRef = ref(null)
  
  // 遮罩层
  const loading = ref(false)
  const classLoading = ref(false)
  
  // 数据
  const taskList = ref([])
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)
  const taskClassOptions = ref([])
  const taskLogs = ref([])
  const currentTask = ref(null)
  
  // 对话框相关
  const dialogVisible = ref(false)
  const showCronDialog = ref(false)
  const showLogsDialog = ref(false)
  const dialogType = ref('add')
  const formRef = ref(null)
  const formData = reactive({
    taskName: '',
    taskDescription: '',
    taskClass: '',
    taskMethod: '',
    cronExpression: '',
    params: '',
    status: 1,
    remark: ''
  })
  
  // 表单验证规则
  const formRules = {
    taskName: [
      { required: true, message: '请输入任务名称', trigger: 'blur' },
      { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
    ],
    taskClass: [
      { required: true, message: '请输入任务类', trigger: 'blur' }
    ],
    taskMethod: [
      { required: true, message: '请输入任务方法', trigger: 'blur' }
    ],
    cronExpression: [
      { required: true, message: '请输入Cron表达式', trigger: 'blur' }
    ],
    status: [
      { required: true, message: '请选择状态', trigger: 'change' }
    ]
  }
  
  // 初始化
  onMounted(() => {
    fetchTaskList()
  })
  
  // 处理表格选择变化
  const handleSelectionChange = (selection) => {
    selectedRows.value = selection
  }
  
  // 获取任务列表
  const fetchTaskList = async (params) => {
    loading.value = true
    try {
      const response = await getScheduledTaskList({
        pageNum: params?.page || currentPage.value,
        pageSize: params?.limit || pageSize.value,
        taskName: queryParams.taskName,
        status: queryParams.status
      })
      taskList.value = response.data.records
      total.value = response.data.total
    } catch (error) {
      ElMessage.error('获取任务列表失败')
    } finally {
      loading.value = false
    }
  }
  
  // 搜索相关
  const handleQuery = () => {
    currentPage.value = 1
    fetchTaskList()
  }
  
  const resetQuery = () => {
    queryParams.taskName = undefined
    queryParams.status = undefined
    currentPage.value = 1
    fetchTaskList()
  }
  
  // 添加任务
  const handleAdd = () => {
    dialogType.value = 'add'
    dialogVisible.value = true
    Object.assign(formData, {
      taskName: '',
      taskDescription: '',
      taskClass: '',
      taskMethod: '',
      cronExpression: '',
      params: '',
      status: 1,
      remark: ''
    })
  }
  
  // 编辑任务
  const handleEdit = (row) => {
    dialogType.value = 'edit'
    dialogVisible.value = true
    Object.assign(formData, row)
  }
  
  // 执行任务
  const handleExecute = (row) => {
    ElMessageBox.confirm(
      '确认立即执行该任务吗?',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      try {
        await executeTaskNow(row.id)
        ElMessage.success('任务已开始执行')
      } catch (error) {
        ElMessage.error('任务执行失败')
      }
    })
  }
  
  // 显示任务日志
  const handleShowLogs = async (row) => {
    currentTask.value = row
    showLogsDialog.value = true
    try {
      const response = await getTaskLogs(row.id)
      taskLogs.value = response.data
    } catch (error) {
      ElMessage.error('获取任务日志失败')
    }
  }
  
  // 批量删除任务
  const handleBatchDelete = () => {
    if (selectedRows.value.length === 0) {
      ElMessage.warning('请选择要删除的任务')
      return
    }
    
    const taskIds = selectedRows.value.map(row => row.id)
    ElMessageBox.confirm(
      `确认删除选中的 ${selectedRows.value.length} 个任务吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      try {
        const promises = taskIds.map(id => deleteScheduledTask(id))
        await Promise.all(promises)
        ElMessage.success('批量删除成功')
        await fetchTaskList()
        if (tableRef.value) {
          tableRef.value.clearSelection()
        }
      } catch (error) {
        ElMessage.error('批量删除失败')
      }
    })
  }
  
  // 删除任务
  const handleDelete = (row) => {
    ElMessageBox.confirm(
      '确认删除该任务吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      try {
        await deleteScheduledTask(row.id)
        ElMessage.success('删除成功')
        await fetchTaskList()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
  }
  
  // 切换任务状态
  const handleToggleStatus = async (row) => {
    const newStatus = row.status === 1 ? 0 : 1
    try {
      await changeTaskStatus(row.id, newStatus)
      ElMessage.success(`任务${newStatus === 1 ? '启用' : '禁用'}成功`)
      fetchTaskList()
    } catch (error) {
      ElMessage.error(`任务${newStatus === 1 ? '启用' : '禁用'}失败`)
    }
  }
  
  // 提交表单
  const handleSubmit = async () => {
    if (!formRef.value) return
    
    await formRef.value.validate(async (valid) => {
      if (valid) {
        try {
          if (dialogType.value === 'add') {
            await addScheduledTask(formData)
            ElMessage.success('添加成功')
          } else {
            await updateScheduledTask(formData.id, formData)
            ElMessage.success('更新成功')
          }
          dialogVisible.value = false
          await fetchTaskList()
        } catch (error) {
          ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
        }
      }
    })
  }
  </script>
  
  <style scoped>
  .scheduled-task-container {
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
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
  </style>