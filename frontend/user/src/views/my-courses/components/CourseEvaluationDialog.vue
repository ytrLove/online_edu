<template>
  <el-dialog
    v-model="dialogVisible"
    title="课程评价"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="evaluationFormRef"
      :model="evaluationForm"
      :rules="evaluationRules"
      label-width="100px"
    >
      <el-form-item label="教学质量" prop="teachingQuality">
        <el-rate
          v-model="evaluationForm.teachingQuality"
          :max="5"
          :texts="['很差', '较差', '一般', '良好', '优秀']"
          show-text
          show-score
        />
      </el-form-item>
      
      <el-form-item label="内容质量" prop="contentQuality">
        <el-rate
          v-model="evaluationForm.contentQuality"
          :max="5"
          :texts="['很差', '较差', '一般', '良好', '优秀']"
          show-text
          show-score
        />
      </el-form-item>
      
      <el-form-item label="互动评分" prop="interactionScore">
        <el-rate
          v-model="evaluationForm.interactionScore"
          :max="5"
          :texts="['很差', '较差', '一般', '良好', '优秀']"
          show-text
          show-score
        />
      </el-form-item>
      
      <el-form-item label="资源质量" prop="resourceQuality">
        <el-rate
          v-model="evaluationForm.resourceQuality"
          :max="5"
          :texts="['很差', '较差', '一般', '良好', '优秀']"
          show-text
          show-score
        />
      </el-form-item>
      
      <el-form-item label="总体评分" prop="overallScore">
        <el-rate
          v-model="evaluationForm.overallScore"
          :max="5"
          :texts="['很差', '较差', '一般', '良好', '优秀']"
          show-text
          show-score
        />
      </el-form-item>
      
      <el-form-item label="改进建议" prop="improvementSuggest">
        <el-input
          v-model="evaluationForm.improvementSuggest"
          type="textarea"
          :rows="4"
          placeholder="请输入您的建议和意见"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          提交评价
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { submitEvaluation } from '@/api/evaluation'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  courseId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['update:visible', 'success'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const evaluationFormRef = ref(null)
const submitting = ref(false)

// 评价表单数据
const evaluationForm = reactive({
  courseId: props.courseId,
  teachingQuality: 0,
  contentQuality: 0,
  interactionScore: 0,
  resourceQuality: 0,
  overallScore: 0,
  improvementSuggest: ''
})

// 监听courseId的变化
watch(() => props.courseId, (newVal) => {
  evaluationForm.courseId = newVal
}, { immediate: true })

// 表单验证规则
const evaluationRules = {
  teachingQuality: [
    { required: true, message: '请对教学质量进行评分', trigger: 'change' }
  ],
  contentQuality: [
    { required: true, message: '请对内容质量进行评分', trigger: 'change' }
  ],
  interactionScore: [
    { required: true, message: '请对互动情况进行评分', trigger: 'change' }
  ],
  resourceQuality: [
    { required: true, message: '请对资源质量进行评分', trigger: 'change' }
  ],
  overallScore: [
    { required: true, message: '请进行总体评分', trigger: 'change' }
  ]
}

// 重置表单
const resetForm = () => {
  if (evaluationFormRef.value) {
    evaluationFormRef.value.resetFields()
  }
  Object.assign(evaluationForm, {
    courseId: props.courseId, // 重置时保持courseId
    teachingQuality: 0,
    contentQuality: 0,
    interactionScore: 0,
    resourceQuality: 0,
    overallScore: 0,
    improvementSuggest: ''
  })
}

// 提交评价
const handleSubmit = async () => {
  if (!evaluationFormRef.value) return
  
  try {
    await evaluationFormRef.value.validate()
    
    // 确保courseId存在
    if (!evaluationForm.courseId) {
      ElMessage.error('课程ID不能为空')
      return
    }
    
    submitting.value = true
    const response = await submitEvaluation(evaluationForm)
    
    if (response.code === 200) {
      ElMessage.success('评价提交成功')
      emit('success')
      handleClose()
    } else {
      ElMessage.error(response.msg || '评价提交失败')
    }
  } catch (error) {
    console.error('提交评价失败:', error)
    ElMessage.error('提交评价失败：' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  resetForm()
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-rate) {
  margin-top: 8px;
}

:deep(.el-rate__text) {
  margin-left: 10px;
  font-size: 14px;
}
</style> 