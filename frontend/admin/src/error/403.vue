<template>
  <div class="forbidden-container">
    <div class="forbidden-content">
      <img src="@/assets/images/403.svg" alt="403" class="forbidden-image" />
      <h1 class="forbidden-title">403</h1>
      <p class="forbidden-text">抱歉，您没有权限访问此页面</p>
      <div class="forbidden-actions">
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
.forbidden-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.forbidden-content {
  text-align: center;
  padding: 40px;
  max-width: 500px;
}

.forbidden-image {
  width: 200px;
  margin-bottom: 20px;
}

.forbidden-title {
  font-size: 72px;
  color: #f56c6c;
  margin: 0 0 20px;
  line-height: 1;
}

.forbidden-text {
  font-size: 20px;
  color: #606266;
  margin-bottom: 30px;
}

.forbidden-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style> 