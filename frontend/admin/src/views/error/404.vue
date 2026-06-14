<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <p class="not-found-text">抱歉，您访问的页面不存在</p>
      <div class="not-found-actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.not-found-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.not-found-content {
  text-align: center;
  padding: 40px;
  max-width: 500px;
}

.not-found-image {
  width: 200px;
  margin-bottom: 20px;
}

.not-found-title {
  font-size: 72px;
  color: #409eff;
  margin: 0 0 20px;
  line-height: 1;
}

.not-found-text {
  font-size: 20px;
  color: #606266;
  margin-bottom: 30px;
}

.not-found-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style> 