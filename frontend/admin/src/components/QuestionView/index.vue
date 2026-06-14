<template>
  <div class="question-view">
    <!-- 问题头部信息 -->
    <div class="question-header">
      <div class="question-type-tag">
        <el-tag size="small" type="primary">{{ question.typeName }}</el-tag>
        <el-tag size="small" :type="getDifficultyType(question.difficulty)" class="ml-2">
          {{ getDifficultyLabel(question.difficulty) }}
        </el-tag>
      </div>
      <div class="question-meta">
        <span v-if="showSubject && question.subjectName">学科：{{ question.subjectName }}</span>
        <span>题号：{{ questionIndex }}</span>
      </div>
    </div>

    <!-- 问题内容 -->
    <div class="question-content">
      <span class="question-index" v-if="showIndex">{{ questionIndex }}. </span>
      <span>{{ question.questionContent }}</span>
    </div>

    <!-- 选项 -->
    <div class="question-options" v-if="hasOptions">
      <div 
        v-for="option in parsedOptions" 
        :key="option.key" 
        class="option-item"
        :class="{
          'correct-option': showAnswer && option.key === question.correctAnswer,
          'selected-option': showAnswer && selectedAnswer && selectedAnswer.includes(option.key),
          'wrong-option': showAnswer && selectedAnswer && selectedAnswer.includes(option.key) && option.key !== question.correctAnswer
        }"
      >
        <span class="option-key">{{ option.key }}.</span>
        <span class="option-value">{{ option.value }}</span>
      </div>
    </div>

    <!-- 答案区域 -->
    <div class="question-answer" v-if="showAnswer">
      <div class="answer-header">正确答案</div>
      <div class="answer-content">
        <template v-if="question.typeName === '单选题'">
          {{ question.correctAnswer }}
        </template>
        <template v-else-if="question.typeName === '多选题'">
          {{ question.correctAnswer }}
        </template>
        <template v-else-if="question.typeName === '判断题'">
          {{ question.correctAnswer === 'T' ? '正确' : '错误' }}
        </template>
        <template v-else>
          {{ question.correctAnswer }}
        </template>
      </div>

      <!-- 解释 -->
      <div class="explanation" v-if="question.explanation">
        <div class="explanation-header">解析</div>
        <div class="explanation-content">{{ question.explanation }}</div>
      </div>

      <!-- 学生答案对比 -->
      <div class="student-answer" v-if="selectedAnswer">
        <div class="student-answer-header">您的答案</div>
        <div class="student-answer-content" :class="isCorrect ? 'correct' : 'incorrect'">
          {{ selectedAnswer }}
          <el-icon v-if="isCorrect"><Check /></el-icon>
          <el-icon v-else><Close /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { Check, Close } from '@element-plus/icons-vue'

export default {
  name: 'QuestionView',
  components: {
    Check,
    Close
  },
  props: {
    question: {
      type: Object,
      required: true
    },
    questionIndex: {
      type: [Number, String],
      default: 1
    },
    selectedAnswer: {
      type: String,
      default: null
    },
    showAnswer: {
      type: Boolean,
      default: false
    },
    showIndex: {
      type: Boolean,
      default: true
    },
    showSubject: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    // 解析选项
    const parsedOptions = computed(() => {
      if (!props.question.options) return []
      
      try {
        const options = JSON.parse(props.question.options)
        return Array.isArray(options) ? options : []
      } catch (error) {
        console.error('解析选项出错:', error)
        return []
      }
    })

    // 判断是否有选项
    const hasOptions = computed(() => {
      return ['单选题', '多选题'].includes(props.question.typeName) && parsedOptions.value.length > 0
    })

    // 判断答案是否正确
    const isCorrect = computed(() => {
      if (!props.selectedAnswer || !props.question.correctAnswer) return false
      
      // 单选题直接比较
      if (props.question.typeName === '单选题') {
        return props.selectedAnswer === props.question.correctAnswer
      }
      
      // 多选题需要排序后比较
      if (props.question.typeName === '多选题') {
        const selected = props.selectedAnswer.split('').sort().join('')
        const correct = props.question.correctAnswer.split('').sort().join('')
        return selected === correct
      }
      
      // 判断题
      if (props.question.typeName === '判断题') {
        return props.selectedAnswer === props.question.correctAnswer
      }
      
      // 其他类型
      return props.selectedAnswer === props.question.correctAnswer
    })

    // 获取难度标签类型
    const getDifficultyType = (difficulty) => {
      switch(difficulty) {
        case 'easy': return 'success'
        case 'medium': return 'warning'
        case 'hard': return 'danger'
        default: return 'info'
      }
    }

    // 获取难度标签文本
    const getDifficultyLabel = (difficulty) => {
      switch(difficulty) {
        case 'easy': return '简单'
        case 'medium': return '中等'
        case 'hard': return '困难'
        default: return '未知'
      }
    }

    return {
      parsedOptions,
      hasOptions,
      isCorrect,
      getDifficultyType,
      getDifficultyLabel
    }
  }
}
</script>

<style scoped>
.question-view {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 16px;
  background-color: #fff;
}

.question-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.question-meta {
  font-size: 14px;
  color: #909399;
}

.question-meta span {
  margin-left: 12px;
}

.question-content {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 16px;
  font-weight: 500;
}

.question-index {
  font-weight: bold;
  margin-right: 4px;
}

.question-options {
  margin-bottom: 16px;
}

.option-item {
  display: flex;
  padding: 8px 12px;
  margin-bottom: 8px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}

.option-item:hover {
  background-color: #f5f7fa;
}

.option-key {
  font-weight: bold;
  margin-right: 8px;
  min-width: 24px;
}

.correct-option {
  background-color: #f0f9eb;
  border-color: #e1f3d8;
}

.selected-option {
  background-color: #f4f4f5;
  border-color: #e9e9eb;
}

.wrong-option {
  background-color: #fef0f0;
  border-color: #fde2e2;
}

.question-answer {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px dashed #dcdfe6;
}

.answer-header, .explanation-header, .student-answer-header {
  font-weight: bold;
  font-size: 15px;
  margin-bottom: 8px;
  color: #303133;
}

.answer-content {
  padding: 8px 12px;
  border-radius: 4px;
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
  margin-bottom: 16px;
}

.explanation {
  margin-top: 16px;
}

.explanation-content {
  padding: 8px 12px;
  border-radius: 4px;
  background-color: #f4f4f5;
  border: 1px solid #e9e9eb;
  line-height: 1.6;
}

.student-answer {
  margin-top: 16px;
}

.student-answer-content {
  padding: 8px 12px;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.student-answer-content.correct {
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
}

.student-answer-content.incorrect {
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
}

.ml-2 {
  margin-left: 8px;
}
</style> 