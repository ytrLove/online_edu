<template>
  <div class="loginlog-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>登录日志管理</span>
          <div class="header-actions">
            <el-button type="warning" plain @click="fetchLoginLogList">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="queryRequest" @search="handleQuery" @reset="resetQuery">
        <el-form-item label="用户名">
          <el-input v-model="queryRequest.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="登录状态">
          <el-select v-model="queryRequest.status" placeholder="登录状态" clearable>
            <el-option label="成功" value="1" />
            <el-option label="失败" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="登录时间">
          <el-date-picker
            v-model="queryRequest.dateRange"
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
        :data="loginLogList"
        :loading="loading"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @pagination="fetchLoginLogList"
        @update:current-page="currentPage = $event"
        @update:page-size="pageSize = $event"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column prop="ipAddress" label="IP地址" width="180" align="center" :show-overflow-tooltip="true" />
        <el-table-column prop="location" label="登录地点" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column prop="browser" label="浏览器" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column prop="os" label="操作系统" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column label="登录状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="loginTime" label="登录时间" width="180" align="center" :show-overflow-tooltip="true" />
        <!-- <el-table-column prop="remark" label="备注" min-width="200" :show-overflow-tooltip="true" /> -->
      </data-table>
    </el-card>

    <!-- 导出日志对话框 -->
    <el-dialog
      v-model="exportDialogVisible"
      title="导出登录日志"
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
        <el-form-item label="登录状态">
          <el-select v-model="exportForm.status" placeholder="请选择登录状态" clearable>
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
import { getLoginLogList } from '@/api/log/loginlog'
import { Refresh, Document } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm/index.vue'
import DataTable from '@/components/DataTable/index.vue'

// 查询参数对象，与后端LoginLogQueryRequest对应
const queryRequest = reactive({
  pageNum: 1,
  pageSize: 10,
  username: undefined,
  status: undefined,
  startTime: undefined,
  endTime: undefined
})

// 遮罩层
const loading = ref(false)
const exportDialogVisible = ref(false)

const loginLogList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 导出表单
const exportFormRef = ref(null)
const exportForm = reactive({
  dateRange: undefined,
  status: ''
})

// 初始化
onMounted(() => {
  fetchLoginLogList()
})

// 获取登录日志列表
const fetchLoginLogList = async () => {
  // 处理日期范围
  if (queryRequest.dateRange && queryRequest.dateRange.length === 2) {
    // 转换为后端需要的LocalDateTime格式 (yyyy-MM-dd HH:mm:ss)
    queryRequest.startTime = formatDate(queryRequest.dateRange[0])
    queryRequest.endTime = formatDate(queryRequest.dateRange[1])
  } else {
    queryRequest.startTime = undefined
    queryRequest.endTime = undefined
  }
  
  loading.value = true
  try {
    // 使用新的API调用方式，直接传递queryRequest对象
    const response = await getLoginLogList(queryRequest)

    loginLogList.value = response.data.records
    total.value = response.data.total
    currentPage.value = response.pageNum
    pageSize.value = response.pageSize
  } catch (error) {
    ElMessage.error('获取登录日志列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索相关
const handleQuery = () => {
  queryRequest.pageNum = 1
  fetchLoginLogList()
}

const resetQuery = () => {
  queryRequest.username = undefined
  queryRequest.status = undefined
  queryRequest.dateRange = undefined
  queryRequest.pageNum = 1
  fetchLoginLogList()
}

// 打开导出对话框
const handleExport = () => {
  exportDialogVisible.value = true
  exportForm.dateRange = queryRequest.dateRange
  exportForm.status = queryRequest.status
}

// 提交导出
const handleExportSubmit = async () => {
  if (!exportFormRef.value) return
  
  await exportFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 构建导出参数
        const exportParams = {
          pageNum: 1,
          pageSize: 9999, // 导出全部数据
          status: exportForm.status,
          startTime: exportForm.dateRange?.[0] ? formatDate(exportForm.dateRange[0]) : undefined,
          endTime: exportForm.dateRange?.[1] ? formatDate(exportForm.dateRange[1]) : undefined
        }
        await exportLoginLog(exportParams)
        ElMessage.success('日志导出成功')
        exportDialogVisible.value = false
      } catch (error) {
        ElMessage.error('日志导出失败')
      }
    }
  })
}

// 辅助函数：格式化日期为 yyyy-MM-dd HH:mm:ss 格式
const formatDate = (date) => {
  if (!date) return undefined
  return new Date(date).toISOString()
    .replace('T', ' ')
    .replace(/\.\d{3}Z$/, '')
}
</script>

<style scoped>
.loginlog-container {
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