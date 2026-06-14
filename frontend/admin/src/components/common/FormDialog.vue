<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    :width="width"
    :destroy-on-close="destroyOnClose"
    :close-on-click-modal="closeOnClickModal"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="modelValue"
      :rules="rules"
      :label-width="labelWidth"
      :label-position="labelPosition"
      :disabled="loading"
    >
      <slot></slot>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel" :disabled="loading">{{ cancelText }}</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">{{ confirmText }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  // 对话框基本属性
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '表单'
  },
  width: {
    type: String,
    default: '500px'
  },
  destroyOnClose: {
    type: Boolean,
    default: true
  },
  closeOnClickModal: {
    type: Boolean,
    default: false
  },
  
  // 表单相关
  modelValue: {
    type: Object,
    required: true
  },
  rules: {
    type: Object,
    default: () => ({})
  },
  labelWidth: {
    type: String,
    default: '100px'
  },
  labelPosition: {
    type: String,
    default: 'right'
  },
  
  // 按钮文本
  confirmText: {
    type: String,
    default: '确定'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'submit', 'cancel', 'closed'])

// 表单引用
const formRef = ref(null)
// 对话框可见状态
const dialogVisible = ref(props.visible)

// 监听visible变化，同步dialogVisible
watch(() => props.visible, (val) => {
  dialogVisible.value = val
})

// 监听dialogVisible变化，向父组件同步visible
watch(() => dialogVisible.value, (val) => {
  emit('update:visible', val)
})

// 对话框关闭后回调
const handleClosed = () => {
  emit('closed')
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 取消按钮
const handleCancel = () => {
  dialogVisible.value = false
  emit('cancel')
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    emit('submit', props.modelValue)
  } catch (error) {
    ElMessage.warning('请检查表单填写是否正确')
  }
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-dialog__body) {
  padding: 20px 20px 0 20px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input) {
  width: 100%;
}
</style> 