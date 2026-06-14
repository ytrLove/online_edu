<template>
  <div class="assignment-list">
    <el-empty v-if="assignments.length === 0 && !loading" description="暂无数据" />
    
    <el-skeleton :rows="4" animated v-if="loading" />
    
    <div v-else class="assignment-items">
      <div v-for="item in assignments" :key="item.id" class="assignment-item">
        <div class="assignment-header">
          <div class="course-tag">{{ item.courseName }}</div>
          <div class="status-tag" :class="getStatusClass(item.status)">
            {{ getStatusText(item.status) }}
          </div>
        </div>
        
        <div class="assignment-title" @click="handleViewDetail(item.id)">
          {{ item.title }}
        </div>
        
        <div class="assignment-meta">
          <div class="meta-item">
            <el-icon><Calendar /></el-icon>
            <span>截止日期: {{ formatDate(item.endTime) }}</span>
          </div>
          
          <div v-if="item.score !== null" class="meta-item score">
            <el-icon><Trophy /></el-icon>
            <span>得分: {{ item.score }}</span>
          </div>
        </div>
        
        <div class="assignment-footer">
          <el-button 
            type="primary" 
            size="small" 
            @click="handleViewDetail(item.id)"
            :icon="View"
          >
            {{ getActionText(item.status) }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import { Calendar, Trophy, View } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/date'

const props = defineProps({
  assignments: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['view-detail'])

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'NOT_STARTED': '未开始',
    'IN_PROGRESS': '进行中',
    'SUBMITTED': '已提交',
    'GRADED': '已批改',
    'EXPIRED': '已过期'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态样式类名
const getStatusClass = (status) => {
  const classMap = {
    'NOT_STARTED': 'status-not-started',
    'IN_PROGRESS': 'status-in-progress',
    'SUBMITTED': 'status-submitted',
    'GRADED': 'status-graded',
    'EXPIRED': 'status-expired'
  }
  return classMap[status] || ''
}

// 获取操作按钮文本
const getActionText = (status) => {
  const textMap = {
    'NOT_STARTED': '开始作业',
    'IN_PROGRESS': '继续作业',
    'SUBMITTED': '查看作业',
    'GRADED': '查看批改',
    'EXPIRED': '查看详情'
  }
  return textMap[status] || '查看详情'
}

// 查看作业详情
const handleViewDetail = (id) => {
  emit('view-detail', id)
}
</script>

<style scoped>
.assignment-list {
  min-height: 200px;
}

.assignment-items {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.assignment-item {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 16px;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.assignment-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.assignment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.course-tag {
  color: #409EFF;
  font-size: 12px;
  background-color: #ecf5ff;
  padding: 2px 6px;
  border-radius: 4px;
}

.status-tag {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
}

.status-not-started {
  color: #909399;
  background-color: #f4f4f5;
}

.status-in-progress {
  color: #e6a23c;
  background-color: #fdf6ec;
}

.status-submitted {
  color: #409EFF;
  background-color: #ecf5ff;
}

.status-graded {
  color: #67c23a;
  background-color: #f0f9eb;
}

.status-expired {
  color: #f56c6c;
  background-color: #fef0f0;
}

.assignment-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 12px;
  cursor: pointer;
  flex: 1;
}

.assignment-title:hover {
  color: #409EFF;
}

.assignment-meta {
  margin-bottom: 16px;
  color: #606266;
  font-size: 13px;
}

.meta-item {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.meta-item .el-icon {
  margin-right: 4px;
  font-size: 14px;
}

.meta-item.score {
  color: #e6a23c;
}

.assignment-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 