<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <el-row :gutter="20" justify="center">
        <el-col :span="24">
          <div class="welcome-card">
            <div class="welcome-header">
              <img src="@/assets/logo.png" alt="Logo" class="logo" />
              <h1>欢迎使用在线教学平台管理系统</h1>
              <p class="subtitle">{{ greetingMessage }}</p>
            </div>
            <el-divider />
            <div class="quick-actions">
              <h3>快速操作</h3>
              <el-row :gutter="20" class="action-buttons">
                <el-col :span="6" v-for="action in quickActions" :key="action.title">
                  <el-card shadow="hover" class="action-card" @click="handleActionClick(action.route)">
                    <el-icon :size="24" class="action-icon">
                      <component :is="action.icon" />
                    </el-icon>
                    <div class="action-title">{{ action.title }}</div>
                    <div class="action-desc">{{ action.description }}</div>
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" class="system-info" justify="center">
        <el-col :span="24">
          <el-card class="tips-card">
            <template #header>
              <div class="tips-header">
                <el-icon><Bell /></el-icon>
                <span>系统公告</span>
              </div>
            </template>
            <div class="tips-content">
              <el-timeline>
                <el-timeline-item
                  v-for="(tip, index) in systemTips"
                  :key="index"
                  :timestamp="tip.time"
                  :type="tip.type">
                  {{ tip.content }}
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  Bell,
  Document,
  User,
  VideoCamera,
  Reading
} from '@element-plus/icons-vue'

const router = useRouter()

// 根据时间生成问候语
const greetingMessage = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，请注意休息'
  if (hour < 9) return '早上好，开启活力的一天'
  if (hour < 12) return '上午好，工作顺利'
  if (hour < 14) return '中午好，请适当休息'
  if (hour < 17) return '下午好，继续加油'
  if (hour < 19) return '傍晚好，工作即将结束'
  return '晚上好，请不要太劳累'
})

// 快速操作按钮
const quickActions = [
  {
    title: '课程管理',
    description: '管理所有在线课程',
    icon: 'Document',
    route: '/course/list'
  },
  {
    title: '用户管理',
    description: '管理教师和学生账号',
    icon: 'User',
    route: '/system/user'
  },
  {
    title: '视频资源',
    description: '管理课程视频资源',
    icon: 'VideoCamera',
    route: '/resource/manage'
  },
  {
    title: '教学评价',
    description: '查看课程评价数据',
    icon: 'Reading',
    route: '/stat/behavior'
  }
]

// 系统公告
const systemTips = [
  {
    content: '系统将于本周日凌晨2点进行例行维护，请提前做好相关工作安排',
    time: '2024-03-15',
    type: 'warning'
  },
  {
    content: '新版本课程管理功能已上线，支持批量导入课程',
    time: '2024-03-14',
    type: 'success'
  },
  {
    content: '请各位教师及时更新课程资料，保证教学质量',
    time: '2024-03-13',
    type: 'info'
  }
]

// 处理快速操作点击
const handleActionClick = (route) => {
  router.push(route)
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.welcome-section {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.welcome-header {
  text-align: center;
  margin-bottom: 30px;
}

.welcome-header .logo {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}

.welcome-header h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 16px;
  color: #909399;
}

.quick-actions {
  margin-top: 30px;
}

.quick-actions h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
}

.action-buttons {
  margin-top: 20px;
}

.action-card {
  height: 160px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-5px);
}

.action-icon {
  color: #409EFF;
  margin-bottom: 10px;
}

.action-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.action-desc {
  font-size: 12px;
  color: #909399;
  text-align: center;
}

.system-info {
  margin-top: 20px;
}

.tips-card {
  margin-bottom: 20px;
}

.tips-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}

.tips-content {
  padding: 20px 0;
}

:deep(.el-timeline-item__node) {
  background-color: #409EFF;
}

:deep(.el-timeline-item__content) {
  color: #606266;
}
</style> 