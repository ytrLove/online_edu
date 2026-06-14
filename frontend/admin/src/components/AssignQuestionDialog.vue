<template>
  <el-dialog 
    v-model="dialogVisible" 
    :title="dialogTitle" 
    width="700px" 
    destroy-on-close
  >
    <div class="assign-questions-container">
      <div class="assignment-info mb-20">
        <h3>作业信息</h3>
        <p><strong>标题:</strong> {{ assignment.title }}</p>
        <p v-if="assignment.questions && assignment.questions.length > 0">
          <strong>已关联题目数:</strong> {{ assignment.questions.length }}
        </p>
        <p v-if="assignment.totalPoints">
          <strong>当前总分:</strong> {{ assignment.totalPoints }}
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
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="#" width="50" align="center" />
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
      </el-table>
      
      <div class="assign-question-footer">
        <div class="total-points">
          已选择: <span>{{ selectedQuestions.length }}</span> 题，
          总分: <span>{{ totalPoints }}</span> 分
        </div>
      </div>
    </div>
    
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
    default: '关联题目'
  },
  assignment: {
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
  courseId: undefined
});

// 列表相关
const questionList = ref([]);
const questionTableRef = ref(null);
const loading = ref(false);
const submitLoading = ref(false);
const selectedQuestions = ref([]);
const totalPoints = ref(0);
const existingQuestionMap = ref(new Map()); // 存储已关联题目的Map

// 监听assignment变化，设置courseId
watch(() => props.assignment, (newVal) => {
  if (newVal && newVal.courseId) {
    queryParams.courseId = newVal.courseId;
    
    // 构建已关联题目的Map
    existingQuestionMap.value.clear();
    if (newVal.questions && newVal.questions.length > 0) {
      newVal.questions.forEach(q => {
        existingQuestionMap.value.set(q.questionId, q.points || 5);
      });
    }
    
    searchQuestions();
  }
}, { immediate: true });

// 监听题目列表变化，自动选中已关联的题目
watch(() => questionList.value, async (newList) => {
  if (newList.length > 0 && existingQuestionMap.value.size > 0) {
    // 等待表格渲染完成
    await nextTick();
    // 选择行
    newList.forEach((question, index) => {
      if (existingQuestionMap.value.has(question.questionId)) {
        // 设置已有的分数
        question.points = existingQuestionMap.value.get(question.questionId);
        // 选中行
        if (questionTableRef.value) {
          questionTableRef.value.toggleRowSelection(question, true);
        }
      }
    });
    // 重新计算总分
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
    // 添加默认分值并保留已有的分数
    questionList.value = questions.map(q => {
      // 如果是已关联的题目，使用原有分数
      const points = existingQuestionMap.value.has(q.questionId) 
        ? existingQuestionMap.value.get(q.questionId) 
        : 5; // 默认5分
      
      return {
        ...q,
        points
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
  searchQuestions();
};

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedQuestions.value = selection;
  calculateTotalPoints();
};

// 计算总分
const calculateTotalPoints = () => {
  totalPoints.value = selectedQuestions.value.reduce((total, question) => {
    return total + (question.points || 5);
  }, 0);
};

// 格式化题目内容
const formatQuestionContent = (content) => {
  if (!content) return '';
  
  // 移除HTML标签并截取部分内容
  const plainText = content.replace(/<[^>]+>/g, '');
  
  // 如果内容过长，截取前100个字符并添加省略号
  if (plainText.length > 100) {
    return plainText.substring(0, 100) + '...';
  }
  
  return plainText;
};

// 获取难度标签样式
const getDifficultyTag = (difficulty) => {
  switch (difficulty) {
    case 'easy': return 'success';
    case 'medium': return 'warning';
    case 'hard': return 'danger';
    default: return 'info';
  }
};

// 获取难度标签文本
const getDifficultyLabel = (difficulty) => {
  switch (difficulty) {
    case 'easy': return '简单';
    case 'medium': return '中等';
    case 'hard': return '困难';
    default: return '未知';
  }
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
    // 构建提交数据
    const submitData = {
      assignmentId: props.assignment.assignmentId,
      questions: selectedQuestions.value.map(question => ({
        questionId: question.questionId,
        points: question.points || 5
      }))
    };
    
    // 发送提交事件
    emit('submit', submitData);
    
    // 关闭对话框
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

.assignment-info {
  background-color: #f8f9fb;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.assignment-info h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
  color: #303133;
}

.assignment-info p {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.total-points {
  font-size: 14px;
  color: #606266;
}

.total-points span {
  font-weight: bold;
  color: #409eff;
}

.mb-20 {
  margin-bottom: 20px;
}
</style> 