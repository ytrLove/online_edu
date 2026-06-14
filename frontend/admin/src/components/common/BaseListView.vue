<template>
  <div class="base-list-container">
    <!-- 头部搜索和操作区 -->
    <el-card class="search-card">
      <div class="search-area">
        <div class="left-section">
          <el-input
            v-model="searchText"
            :placeholder="searchPlaceholder"
            class="search-input"
            clearable
            @keyup.enter="handleSearch"
            @clear="handleClear"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            <span>搜索</span>
          </el-button>
          
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            <span>重置</span>
          </el-button>
          
          <slot name="extraFilters"></slot>
        </div>
        
        <div class="right-section">
          <slot name="topActions"></slot>
          <el-button type="primary" v-if="showAddButton" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            <span>{{ addButtonText }}</span>
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <div class="table-top-bar" v-if="$slots.tableActions">
        <slot name="tableActions"></slot>
      </div>
      
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
        :max-height="tableMaxHeight"
        highlight-current-row
        :empty-text="emptyText"
        @row-click="handleRowClick"
      >
        <slot></slot>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container" v-if="showPagination">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="pageSizes"
          :layout="paginationLayout"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const props = defineProps({
  // 搜索相关
  searchPlaceholder: {
    type: String,
    default: '请输入关键词搜索'
  },
  
  // 表格相关
  tableData: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  emptyText: {
    type: String,
    default: '暂无数据'
  },
  tableMaxHeight: {
    type: [String, Number],
    default: 'auto'
  },
  
  // 分页相关
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  },
  total: {
    type: Number,
    default: 0
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  paginationLayout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  showPagination: {
    type: Boolean,
    default: true
  },
  
  // 按钮相关
  showAddButton: {
    type: Boolean,
    default: true
  },
  addButtonText: {
    type: String,
    default: '添加'
  }
})

const emit = defineEmits([
  'search', 
  'reset', 
  'add', 
  'size-change', 
  'page-change', 
  'row-click',
  'search-clear'
])

// 搜索文本
const searchText = ref('')

// 防抖定时器
let searchDebounce = null

// 搜索处理
const handleSearch = () => {
  if (searchDebounce) clearTimeout(searchDebounce)
  
  searchDebounce = setTimeout(() => {
    emit('search', searchText.value)
  }, 300)
}

// 清空搜索
const handleClear = () => {
  emit('search-clear')
}

// 重置搜索
const handleReset = () => {
  searchText.value = ''
  emit('reset')
}

// 添加
const handleAdd = () => {
  emit('add')
}

// 分页大小变化
const handleSizeChange = (val) => {
  emit('size-change', val)
}

// 页码变化
const handleCurrentChange = (val) => {
  emit('page-change', val)
}

// 行点击
const handleRowClick = (row, column, event) => {
  emit('row-click', row, column, event)
}

// 监听搜索文本变化
watch(searchText, (newVal, oldVal) => {
  // 如果启用搜索输入实时触发，可以取消注释下面的代码
  // handleSearch()
})
</script>

<style scoped>
.base-list-container {
  padding: 20px;
}

.search-card {
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

.search-area {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.left-section {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.right-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 280px;
}

.table-card {
  margin-top: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.table-top-bar {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 深度选择器修改内部样式 */
:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-table) {
  margin: 8px 0;
  /* 改进表格样式 */
  --el-table-border-color: #f0f0f0;
  --el-table-header-bg-color: #f5f7fa;
}

:deep(.el-table th) {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

:deep(.el-table__row) {
  transition: all 0.3s;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

:deep(.el-button) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 32px;
}

:deep(.el-input__wrapper) {
  height: 32px;
}

:deep(.el-button .el-icon) {
  font-size: 16px;
  vertical-align: middle;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .search-area {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .left-section,
  .right-section {
    width: 100%;
  }
  
  .right-section {
    justify-content: flex-end;
    margin-top: 12px;
  }
  
  .search-input {
    width: 100%;
  }
}
</style> 