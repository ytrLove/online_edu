<template>
  <div class="data-table-container">
    <el-table
      v-loading="loading"
      :data="data"
      border
      stripe
      highlight-current-row
      style="width: 100%; margin-bottom: 20px;"
      v-bind="$attrs"
    >
      <slot></slot>
    </el-table>
    
    <el-pagination
      v-if="showPagination && total > 0"
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      :layout="paginationLayout"
      @update:current-page="handleCurrentChange"
      @update:page-size="handleSizeChange"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination-container"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  showPagination: {
    type: Boolean,
    default: true
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 6
  },
  pageSizes: {
    type: Array,
    default: () => [6, 12, 30, 50]
  },
  total: {
    type: Number,
    default: 0
  },
  paginationLayout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  }
})

const emit = defineEmits(['update:currentPage', 'update:pageSize', 'pagination'])

const handleCurrentChange = (val) => {
  emit('update:currentPage', val)
  emit('pagination', { page: val, limit: props.pageSize })
}

const handleSizeChange = (val) => {
  emit('update:pageSize', val)
  emit('pagination', { page: props.currentPage, limit: val })
}
</script>

<style scoped>
.data-table-container {
  width: 100%;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  font-size: 14px;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-table__header-wrapper) {
  background-color: #f5f7fa;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
  height: 50px;
}

:deep(.el-table__row) {
  height: 55px;
}

:deep(.el-table__row:hover) {
  background-color: #f0f9ff !important;
}

:deep(.el-button--text) {
  padding: 4px 0;
  margin: 0 5px;
}
</style> 