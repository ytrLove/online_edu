<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="modelValue" class="fixed inset-0 z-50 flex items-center justify-center">
        <!-- 背景遮罩 -->
        <div class="absolute inset-0 bg-black bg-opacity-50" @click="closeModal"></div>
        
        <!-- 模态框内容 -->
        <div class="relative bg-white rounded-lg shadow-xl max-w-md w-full mx-4 max-h-[90vh] overflow-hidden">
          <!-- 模态框头部 -->
          <div class="flex justify-between items-center p-4 border-b">
            <h3 class="text-lg font-medium text-gray-900">{{ title }}</h3>
            <button 
              @click="closeModal" 
              class="text-gray-400 hover:text-gray-500 focus:outline-none"
            >
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          
          <!-- 模态框内容 -->
          <div class="p-4 overflow-y-auto max-h-[calc(90vh-130px)]">
            <slot></slot>
          </div>
          
          <!-- 模态框底部 -->
          <div v-if="$slots.footer" class="p-4 border-t flex justify-end space-x-2">
            <slot name="footer">
              <button 
                @click="closeModal" 
                class="px-4 py-2 border rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
              >
                取消
              </button>
              <button 
                @click="confirm" 
                class="px-4 py-2 bg-blue-500 border border-transparent rounded-md text-sm font-medium text-white hover:bg-blue-600"
              >
                确定
              </button>
            </slot>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { watch, onMounted, onUnmounted } from 'vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: '提示'
  },
  closeOnEsc: {
    type: Boolean,
    default: true
  },
  closeOnClickOutside: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['update:modelValue', 'confirm']);

const closeModal = () => {
  if (props.closeOnClickOutside) {
    emit('update:modelValue', false);
  }
};

const confirm = () => {
  emit('confirm');
  emit('update:modelValue', false);
};

// 处理ESC键关闭模态框
const handleKeyDown = (e) => {
  if (e.key === 'Escape' && props.closeOnEsc && props.modelValue) {
    emit('update:modelValue', false);
  }
};

// 监听键盘事件
onMounted(() => {
  document.addEventListener('keydown', handleKeyDown);
});

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeyDown);
});

// 当模态框打开时，禁止背景滚动
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = '';
  }
});
</script>

<style scoped>
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}
</style>