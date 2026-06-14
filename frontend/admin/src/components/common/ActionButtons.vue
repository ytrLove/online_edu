<template>
  <div class="action-buttons">
    <!-- 查看按钮 -->
    <el-tooltip
      v-if="showView"
      effect="dark"
      content="查看详情"
      placement="top"
      :enterable="false"
    >
      <el-button
        link
        type="primary"
        :size="size"
        @click.stop="handleView"
      >
        <el-icon><View /></el-icon>
        <span v-if="showText">查看</span>
      </el-button>
    </el-tooltip>

    <!-- 编辑按钮 -->
    <el-tooltip
      v-if="showEdit"
      effect="dark"
      content="编辑"
      placement="top"
      :enterable="false"
    >
      <el-button
        link
        type="primary"
        :size="size"
        @click.stop="handleEdit"
      >
        <el-icon><Edit /></el-icon>
        <span v-if="showText">编辑</span>
      </el-button>
    </el-tooltip>

    <!-- 自定义按钮插槽 -->
    <slot></slot>

    <!-- 删除按钮 -->
    <el-tooltip
      v-if="showDelete"
      effect="dark"
      content="删除"
      placement="top"
      :enterable="false"
    >
      <el-popconfirm
        :title="deleteConfirmText"
        @confirm="handleDelete"
        confirm-button-text="确定"
        cancel-button-text="取消"
        icon="Warning"
        icon-color="red"
      >
        <template #reference>
          <el-button
            link
            type="danger"
            :size="size"
          >
            <el-icon><Delete /></el-icon>
            <span v-if="showText">删除</span>
          </el-button>
        </template>
      </el-popconfirm>
    </el-tooltip>
  </div>
</template>

<script setup>
import { View, Edit, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  // 按钮显示控制
  showView: {
    type: Boolean,
    default: true
  },
  showEdit: {
    type: Boolean,
    default: true
  },
  showDelete: {
    type: Boolean,
    default: true
  },
  // 是否显示文字
  showText: {
    type: Boolean,
    default: false
  },
  // 按钮大小
  size: {
    type: String,
    default: 'small'
  },
  // 删除确认文字
  deleteConfirmText: {
    type: String,
    default: '确认删除该记录吗？'
  },
  // 行数据
  row: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['view', 'edit', 'delete'])

// 查看
const handleView = (e) => {
  e.stopPropagation()
  emit('view', props.row)
}

// 编辑
const handleEdit = (e) => {
  e.stopPropagation()
  emit('edit', props.row)
}

// 删除
const handleDelete = () => {
  emit('delete', props.row)
}
</script>

<style scoped>
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-button--small) {
  padding: 5px 8px;
}

:deep(.el-button .el-icon) {
  font-size: 16px;
}

/* 为了使图标在没有文字时居中 */
:deep(.el-button) {
  justify-content: center;
}
</style> 