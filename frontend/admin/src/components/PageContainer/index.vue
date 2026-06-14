// 注意：此组件已被替换为直接在各页面中使用el-card，
// 新页面开发不应再使用此组件，而是使用标准的Element Plus组件

<template>
  <div class="page-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ title }}</span>
          <div class="header-actions">
            <slot name="toolbar"></slot>
            <slot name="action">
              <el-button 
                v-if="canShowButton" 
                type="primary" 
                @click="$emit('add')"
              >
                <el-icon><Plus /></el-icon>新增{{ addButtonText }}
              </el-button>
            </slot>
          </div>
        </div>
      </template>
      
      <slot name="search"></slot>
      
      <slot></slot>
      
      <slot name="pagination"></slot>
    </el-card>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  showAddButton: {
    type: Boolean,
    default: true
  },
  addButtonText: {
    type: String,
    default: ''
  },
  permission: {
    type: String,
    default: ''
  }
})

defineEmits(['add'])

// 判断是否显示按钮
const userStore = useUserStore()
const canShowButton = computed(() => {
  if (!props.showAddButton) return false
  if (!props.permission) return true
  return userStore.hasPermission(props.permission)
})
</script>

<style scoped>
.page-container {
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
  align-items: center;
}

:deep(.el-card) {
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-card__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 15px 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style> 