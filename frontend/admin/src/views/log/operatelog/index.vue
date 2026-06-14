<template>
    <div class="operationlog-container">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span>操作日志管理</span>
            <div class="header-actions">
              <el-button type="warning" plain @click="fetchOperationLogList">
                <el-icon><Refresh /></el-icon>刷新
              </el-button>
              <el-button type="primary" plain v-permission="'system:operationlog:export'" @click="handleExport">
                <el-icon><Document /></el-icon>导出日志
              </el-button>
            </div>
          </div>
        </template>
        
        <!-- 搜索区域 -->
        <search-form :form-model="queryParams" @search="handleQuery" @reset="resetQuery">
          <el-form-item label="用户名">
            <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="操作类型">
            <el-select v-model="queryParams.operationType" placeholder="请选择操作类型" clearable>
              <el-option label="新增" value="CREATE" />
              <el-option label="修改" value="UPDATE" />
              <el-option label="删除" value="DELETE" />
              <el-option label="查询" value="QUERY" />
              <el-option label="导入" value="IMPORT" />
              <el-option label="导出" value="EXPORT" />
            </el-select>
          </el-form-item>
          <el-form-item label="操作状态">
            <el-select v-model="queryParams.status" placeholder="请选择操作状态" clearable>
              <el-option label="成功" value="0" />
              <el-option label="失败" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="操作时间">
            <el-date-picker
              v-model="queryParams.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :disabled-date="disabledDate"
              style="width: 100%"
            />
          </el-form-item>
        </search-form>
  
        <!-- 表格区域 -->
        <data-table
          ref="tableRef"
          :data="operationLogList"
          :loading="loading"
          :current-page="currentPage"
          :page-size="size"
          :total="total"
          @pagination="fetchOperationLogList"
          @update:current-page="currentPage = $event"
          @update:page-size="size = $event"
        >
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="username" label="用户名" min-width="120" :show-overflow-tooltip="true" />
          <el-table-column prop="operationType" label="操作类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getOperationTypeTagType(row.operationType)" size="small">
                {{ getOperationTypeName(row.operationType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operationName" label="操作名称" min-width="120" :show-overflow-tooltip="true" />
          <el-table-column prop="module" label="操作模块" width="120" align="center" :show-overflow-tooltip="true" />
          <el-table-column prop="ipAddress" label="IP地址" width="150" align="center" :show-overflow-tooltip="true" />
          <el-table-column label="操作状态" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
                {{ row.status === 0 ? '成功' : '失败' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operationTime" label="操作时间" width="180" align="center" :show-overflow-tooltip="true" />
          <!-- <el-table-column prop="executionTime" label="执行时间(ms)" width="120" align="center" /> -->
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button type="primary" size="small" text @click="handleViewDetail(row)">
                <el-icon><View /></el-icon>详情
              </el-button>
            </template>
          </el-table-column>
        </data-table>
      </el-card>
  
      <!-- 操作日志详情对话框 -->
      <el-dialog
        :model-value="detailVisible"
        @update:model-value="detailVisible = $event"
        title="操作日志详情"
        width="800px"
        destroy-on-close
      >
        <el-form label-width="120px">
          <el-form-item label="用户名">
            <el-input v-model="detailData.operName" disabled />
          </el-form-item>
          <el-form-item label="操作类型">
            <el-input v-model="detailData.operatorType" disabled />
          </el-form-item>
          <el-form-item label="请求方法">
            <el-input v-model="detailData.requestMethod" disabled />
          </el-form-item>
          <el-form-item label="名称">
            <el-input v-model="detailData.method" disabled />
          </el-form-item>
          <el-form-item label="IP地址">
            <el-input v-model="detailData.operIp" disabled />
          </el-form-item>
          <el-form-item label="登录地点">
            <el-input v-model="detailData.operLocation" disabled />
          </el-form-item>
          <el-form-item label="操作状态">
            <el-tag :type="detailData.status === 0 ? 'success' : 'danger'" size="small">
              {{ detailData.status === 0 ? '成功' : '失败' }}
            </el-tag>
          </el-form-item>
          <el-form-item label="操作时间">
            <el-input v-model="detailData.createdAt" disabled />
          </el-form-item>
          <el-form-item label="请求URL">
            <el-input v-model="detailData.operUrl" disabled textarea :rows="2" />
          </el-form-item>
          <el-form-item label="请求参数">
            <el-input v-model="detailData.operParam" disabled textarea :rows="4" />
          </el-form-item>
          <el-form-item label="返回结果">
            <el-input v-model="detailData.jsonResult" disabled textarea :rows="6" />
          </el-form-item>
          <el-form-item label="错误信息" v-if="detailData.status === 1">
            <el-input v-model="detailData.errorMsg" disabled textarea :rows="3" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="detailVisible = false">关闭</el-button>
          </div>
        </template>
      </el-dialog>
  
      <!-- 导出日志对话框 -->
      <el-dialog
        v-model="exportDialogVisible"
        title="导出操作日志"
        width="350px"
        destroy-on-close
      >
        <el-form
          ref="exportFormRef"
          :model="exportForm"
          :rules="exportRules"
          label-width="100px"
        >
          <el-form-item label="导出时间范围">
            <el-date-picker
              v-model="exportForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :disabled-date="disabledDate"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="操作类型">
            <el-select v-model="exportForm.operationType" placeholder="请选择操作类型" clearable>
              <el-option label="全部" value="" />
              <el-option label="新增" value="CREATE" />
              <el-option label="修改" value="UPDATE" />
              <el-option label="删除" value="DELETE" />
              <el-option label="查询" value="QUERY" />
              <el-option label="导入" value="IMPORT" />
              <el-option label="导出" value="EXPORT" />
            </el-select>
          </el-form-item>
          <el-form-item label="操作状态">
            <el-select v-model="exportForm.status" placeholder="请选择操作状态" clearable>
              <el-option label="全部" value="" />
              <el-option label="成功" value="0" />
              <el-option label="失败" value="1" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="exportDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleExportSubmit">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, reactive } from 'vue'
  import { ElMessage } from 'element-plus'
  import { getOperationLogList, exportOperationLog, getOperationLogDetail } from '@/api/log/operationLog'
  import { Refresh, Document, View } from '@element-plus/icons-vue'
  import SearchForm from '@/components/SearchForm/index.vue'
  import DataTable from '@/components/DataTable/index.vue'
  
  // 查询参数
  const queryParams = reactive({
    page: 1,
    size: 10,
    username: undefined,
    operationType: undefined,
    status: undefined,
    dateRange: undefined
  })
  
  // 遮罩层
  const loading = ref(false)
  const detailVisible = ref(false)
  const exportDialogVisible = ref(false)
  
  const operationLogList = ref([])
  const currentPage = ref(1)
  const size = ref(10)
  const total = ref(0)
  const detailData = ref({})
  
  // 导出表单
  const exportFormRef = ref(null)
  const exportForm = reactive({
    dateRange: undefined,
    operationType: '',
    status: ''
  })
  
  // 表单验证规则
  const exportRules = {
    dateRange: [
      { required: true, message: '请选择导出时间范围', trigger: 'change' }
    ]
  }
  
  // 禁用过去日期（仅作为示例，可根据需求调整）
  const disabledDate = (time) => {
    return time.getTime() > Date.now() - 8.64e7
  }
  
  // 操作类型映射
  const operationTypeMap = {
    'CREATE': '新增',
    'UPDATE': '修改',
    'DELETE': '删除',
    'QUERY': '查询',
    'IMPORT': '导入',
    'EXPORT': '导出',
    'LOGIN': '登录',
    'LOGOUT': '登出'
  }
  
  // 操作类型标签样式
  const operationTypeTagTypeMap = {
    'CREATE': 'success',
    'UPDATE': 'warning',
    'DELETE': 'danger',
    'QUERY': 'info',
    'IMPORT': 'primary',
    'EXPORT': 'primary',
    'LOGIN': 'info',
    'LOGOUT': 'info'
  }
  
  // 初始化
  onMounted(() => {
    fetchOperationLogList()
  })
  
  // 获取操作日志列表
  const fetchOperationLogList = async () => {
    loading.value = true
    try {
      const response = await getOperationLogList({
        page: queryParams.page,
        size: queryParams.size,
        username: queryParams.username,
        operationType: queryParams.operationType,
        status: queryParams.status,
        beginTime: queryParams.dateRange?.[0],
        endTime: queryParams.dateRange?.[1]
      })
      operationLogList.value = (response.data.records || []).map(item => ({
        username: item.operName,
        operationType: item.operatorType,
        operationName: item.method,
        module: item.operUrl,
        ipAddress: item.operIp,
        status: item.status,
        operationTime: item.createdAt,
        id: item.id,
      }))
      total.value = response.data.total
      currentPage.value = response.data.current
      size.value = response.data.size
    } catch (error) {
      ElMessage.error('获取操作日志列表失败')
    } finally {
      loading.value = false
    }
  }
  
  // 搜索相关
  const handleQuery = () => {
    queryParams.page = 1
    fetchOperationLogList()
  }
  
  const resetQuery = () => {
    queryParams.username = undefined
    queryParams.operationType = undefined
    queryParams.status = undefined
    queryParams.dateRange = undefined
    queryParams.page = 1
    fetchOperationLogList()
  }
  
  // 查看详情
  const handleViewDetail = async (row) => {
    try {
      const response = await getOperationLogDetail(row.id)
      const data = response.data
      
      // 格式化数据
      data.operationTypeText = operationTypeMap[data.operationType] || data.operationType
      data.executionTimeText = `${data.executionTime}ms`
      
      detailData.value = data
      detailVisible.value = true
    } catch (error) {
      ElMessage.error('获取日志详情失败')
    }
  }
  
  // 获取操作类型名称
  const getOperationTypeName = (type) => {
    return operationTypeMap[type] || type
  }
  
  // 获取操作类型标签样式
  const getOperationTypeTagType = (type) => {
    return operationTypeTagTypeMap[type] || 'info'
  }
  
  // 打开导出对话框
  const handleExport = () => {
    exportDialogVisible.value = true
    exportForm.dateRange = queryParams.dateRange
    exportForm.operationType = queryParams.operationType
    exportForm.status = queryParams.status
  }
  
  // 提交导出
  const handleExportSubmit = async () => {
    if (!exportFormRef.value) return
    
    await exportFormRef.value.validate(async (valid) => {
      if (valid) {
        try {
          // 构建导出参数
          const exportParams = {
            page: 1,
            size: 9999, // 导出全部数据
            operationType: exportForm.operationType,
            status: exportForm.status,
            beginTime: exportForm.dateRange?.[0],
            endTime: exportForm.dateRange?.[1]
          }
          await exportOperationLog(exportParams)
          ElMessage.success('日志导出成功')
          exportDialogVisible.value = false
        } catch (error) {
          ElMessage.error('日志导出失败')
        }
      }
    })
  }
  </script>
  
  <style scoped>
  .operationlog-container {
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