<template>
  <el-dialog 
    v-model="dialogVisible" 
    :title="dialogTitle" 
    width="900px" 
    destroy-on-close
  >
    <div class="assign-questions-container">
      <div class="exam-info mb-20">
        <h3>考试信息</h3>
        <p><strong>标题:</strong> {{ exam.title }}</p>
        <p v-if="exam.questions && exam.questions.length > 0">
          <strong>已关联题目数:</strong> {{ exam.questions.length }}
        </p>
        <p v-if="exam.totalPoints">
          <strong>当前总分:</strong> {{ exam.totalPoints }}
        </p>
        <p v-if="exam.duration">
          <strong>考试时长:</strong> {{ exam.duration }}分钟
        </p>
      </div>
      
      <el-form :inline="true" :model="queryParams" class="question-search-form">
        <el-form-item label="试题内容">
          <el-input v-model="queryParams.questionContent" placeholder="请输入试题内容关键字" clearable @keyup.enter="searchQuestions" />
        </el-form-item>
        <el-form-item label="题目类型">
          <el-select v-model="queryParams.questionTypeId" placeholder="请选择题目类型" clearable>
            <el-option 
              v-for="item in questionTypeOptions" 
              :key="item.typeId" 
              :label="item.typeName" 
              :value="item.typeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="queryParams.difficulty" placeholder="请选择难度" clearable>
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchQuestions">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table 
        ref="questionTableRef"
        :data="questionList" 
        border 
        stripe 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
        row-key="questionId"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="序号" width="80" align="center">
          <template #default="scope">
            <el-input-number 
              v-if="isSelected(scope.row)"
              v-model="scope.row.questionOrder" 
              :min="1" 
              :max="selectedQuestions.length" 
              size="small"
              @change="handleOrderChange"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="questionContent" label="试题内容" min-width="250" show-overflow-tooltip>
          <template #default="scope">
            <div v-html="formatQuestionContent(scope.row.questionContent)"></div>
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="题目类型" width="100" align="center" />
        <el-table-column prop="difficulty" label="难度" width="80" align="center">
          <template #default="scope">
            <el-tag :type="getDifficultyTag(scope.row.difficulty)">
              {{ getDifficultyLabel(scope.row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分值" width="120" align="center">
          <template #default="scope">
            <el-input-number 
              v-model="scope.row.points" 
              :min="1" 
              :max="100" 
              :step="1" 
              size="small"
              @change="calculateTotalPoints"
            />
          </template>
        </el-table-column>
        <el-table-column label="预览" width="80" align="center">
          <template #default="scope">
            <el-button 
              type="primary" 
              link
              @click="handlePreviewQuestion(scope.row)"
            >
              预览
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="assign-question-footer">
        <div class="total-info">
          <span class="info-item">已选择: <em>{{ selectedQuestions.length }}</em> 题</span>
          <span class="info-item">总分: <em>{{ totalPoints }}</em> 分</span>
          <span class="info-item">
            题型分布: 
            <el-tag 
              v-for="(count, type) in questionTypeCount" 
              :key="type"
              size="small"
              class="ml-5"
            >
              {{ type }}: {{ count }}
            </el-tag>
          </span>
        </div>
      </div>
    </div>
    
    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="题目预览"
      width="600px"
      append-to-body
    >
      <div class="question-preview">
        <div class="question-content" v-html="currentPreviewQuestion.questionContent"></div>
        <div class="question-options" v-if="currentPreviewQuestion.options">
          <div v-for="option in JSON.parse(currentPreviewQuestion.options)" :key="option.key" class="option-item">
            {{ option.key }}. {{ option.value }}
          </div>
        </div>
        <div class="question-answer">
          <strong>正确答案:</strong> {{ currentPreviewQuestion.correctAnswer }}
        </div>
        <div class="question-analysis" v-if="currentPreviewQuestion.analysis">
          <strong>解析:</strong> {{ currentPreviewQuestion.analysis }}
        </div>
      </div>
    </el-dialog>
    
    <template #footer>
      <div>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定关联</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { getQuestionList } from '@/api/exam/question';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '关联试题'
  },
  exam: {
    type: Object,
    default: () => ({})
  },
  questionTypeOptions: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:visible', 'submit', 'cancel']);

// 对话框相关
const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});
const dialogTitle = computed(() => props.title);

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  questionContent: '',
  questionTypeId: undefined,
  difficulty: undefined,
  courseId: undefined
});

// 列表相关
const questionList = ref([]);
const questionTableRef = ref(null);
const loading = ref(false);
const submitLoading = ref(false);
const selectedQuestions = ref([]);
const totalPoints = ref(0);
const existingQuestionMap = ref(new Map());

// 预览相关
const previewDialogVisible = ref(false);
const currentPreviewQuestion = ref({});

// 计算题型分布
const questionTypeCount = computed(() => {
  const count = {};
  selectedQuestions.value.forEach(question => {
    count[question.typeName] = (count[question.typeName] || 0) + 1;
  });
  return count;
});

// 监听exam变化
watch(() => props.exam, (newVal) => {
  if (newVal && newVal.courseId) {
    queryParams.courseId = newVal.courseId;
    
    // 构建已关联题目的Map
    existingQuestionMap.value.clear();
    if (newVal.questions && newVal.questions.length > 0) {
      newVal.questions.forEach(q => {
        existingQuestionMap.value.set(q.questionId, {
          points: q.points || 5,
          order: q.questionOrder || 1
        });
      });
    }
    
    searchQuestions();
  }
}, { immediate: true });

// 监听题目列表变化
watch(() => questionList.value, async (newList) => {
  if (newList.length > 0 && existingQuestionMap.value.size > 0) {
    await nextTick();
    newList.forEach((question) => {
      if (existingQuestionMap.value.has(question.questionId)) {
        const existingData = existingQuestionMap.value.get(question.questionId);
        question.points = existingData.points;
        question.questionOrder = existingData.order;
        if (questionTableRef.value) {
          questionTableRef.value.toggleRowSelection(question, true);
        }
      }
    });
    calculateTotalPoints();
  }
}, { deep: true });

// 搜索题目
const searchQuestions = async () => {
  if (!queryParams.courseId) {
    ElMessage.warning('课程ID不能为空');
    return;
  }
  
  loading.value = true;
  try {
    const response = await getQuestionList(queryParams);
    let questions = response.data.rows || response.data.records || [];
    questionList.value = questions.map(q => {
      const existingData = existingQuestionMap.value.get(q.questionId);
      return {
        ...q,
        points: existingData ? existingData.points : 5,
        questionOrder: existingData ? existingData.order : null
      };
    });
  } catch (error) {
    console.error('获取题目列表失败', error);
    ElMessage.error('获取题目列表失败: ' + (error.message || '未知错误'));
    questionList.value = [];
  } finally {
    loading.value = false;
  }
};

// 重置查询
const resetQuery = () => {
  queryParams.questionContent = '';
  queryParams.questionTypeId = undefined;
  queryParams.difficulty = undefined;
  searchQuestions();
};

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedQuestions.value = selection;
  // 为新选中的题目设置序号
  selection.forEach((question, index) => {
    if (!question.questionOrder) {
      question.questionOrder = index + 1;
    }
  });
  calculateTotalPoints();
};

// 判断题目是否被选中
const isSelected = (row) => {
  return selectedQuestions.value.some(q => q.questionId === row.questionId);
};

// 处理序号变化
const handleOrderChange = () => {
  // 对选中的题目按序号排序
  selectedQuestions.value.sort((a, b) => a.questionOrder - b.questionOrder);
};

// 计算总分
const calculateTotalPoints = () => {
  totalPoints.value = selectedQuestions.value.reduce((total, question) => {
    return total + (question.points || 5);
  }, 0);
};

// 预览题目
const handlePreviewQuestion = (question) => {
  currentPreviewQuestion.value = question;
  previewDialogVisible.value = true;
};

// 格式化题目内容
const formatQuestionContent = (content) => {
  if (!content) return '';
  const plainText = content.replace(/<[^>]+>/g, '');
  return plainText.length > 100 ? plainText.substring(0, 100) + '...' : plainText;
};

// 获取难度标签样式
const getDifficultyTag = (difficulty) => {
  const tags = {
    easy: 'success',
    medium: 'warning',
    hard: 'danger'
  };
  return tags[difficulty] || 'info';
};

// 获取难度标签文本
const getDifficultyLabel = (difficulty) => {
  const labels = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  };
  return labels[difficulty] || '未知';
};

// 取消操作
const handleCancel = () => {
  emit('cancel');
  dialogVisible.value = false;
};

// 提交操作
const handleSubmit = () => {
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请至少选择一道题目');
    return;
  }
  
  submitLoading.value = true;
  try {
    const submitData = {
      examId: props.exam.examId,
      questions: selectedQuestions.value.map(question => ({
        examId: props.exam.examId,
        questionId: question.questionId,
        points: question.points || 5,
        questionOrder: question.questionOrder
      }))
    };
    
    emit('submit', submitData);
    dialogVisible.value = false;
  } catch (error) {
    console.error('提交失败', error);
    ElMessage.error('提交失败: ' + (error.message || '未知错误'));
  } finally {
    submitLoading.value = false;
  }
};
</script>

<style scoped>
.assign-questions-container {
  padding: 16px 0;
}

.exam-info {
  background-color: #f8f9fb;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.exam-info h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
  color: #303133;
}

.exam-info p {
  margin: 5px 0;
  color: #606266;
}

.question-search-form {
  margin-bottom: 16px;
  background-color: #f8f9fb;
  padding: 16px;
  border-radius: 6px;
}

.assign-question-footer {
  margin-top: 16px;
  padding: 16px;
  background-color: #f8f9fb;
  border-radius: 6px;
}

.total-info {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.info-item {
  color: #606266;
  font-size: 14px;
}

.info-item em {
  font-style: normal;
  color: #409eff;
  font-weight: bold;
}

.ml-5 {
  margin-left: 5px;
}

.question-preview {
  padding: 20px;
}

.question-content {
  margin-bottom: 20px;
  line-height: 1.6;
}

.question-options {
  margin-bottom: 20px;
}

.option-item {
  margin: 8px 0;
  padding: 8px;
  background-color: #f8f9fb;
  border-radius: 4px;
}

.question-answer {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f0f9eb;
  border-radius: 4px;
  color: #67c23a;
}

.question-analysis {
  padding: 10px;
  background-color: #f4f4f5;
  border-radius: 4px;
  color: #909399;
}

.mb-20 {
  margin-bottom: 20px;
}
</style> 