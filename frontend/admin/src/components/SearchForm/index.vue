<template>
  <div class="search-form-container">
    <el-form :inline="true" :model="formModel" class="search-form">
      <slot></slot>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          <span>搜索</span>
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          <span>重置</span>
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { Search, Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  formModel: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['search', 'reset'])

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  emit('reset')
}
</script>

<style scoped>
.search-form-container {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

:deep(.el-form-item) {
  margin-bottom: 15px;
  margin-right: 15px;
}

:deep(.el-form-item__content) {
  display: flex;
  align-items: center;
}

:deep(.el-button) {
  display: inline-flex;
  align-items: center;
}

:deep(.el-button .el-icon) {
  margin-right: 4px;
}
</style> 