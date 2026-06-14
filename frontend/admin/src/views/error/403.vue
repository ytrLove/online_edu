<template>
  <div class="unauthorized-container">
    <div class="content">
      <h1>403</h1>
      <h2>抱歉，您没有访问权限</h2>
      <p>您当前的账户权限不足，无法访问此页面</p>
      <el-button type="primary" @click="goBack">返回上一页</el-button>
      <el-button @click="goHome">回到首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.go(-1)
}

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.unauthorized-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  text-align: center;
  background-color: #f5f7fa;
}

.content {
  max-width: 500px;
  padding: 40px;
  border-radius: 8px;
  background-color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

h1 {
  font-size: 72px;
  margin: 0;
  color: #f56c6c;
}

h2 {
  font-size: 24px;
  margin: 10px 0 20px;
  color: #303133;
}

p {
  font-size: 16px;
  color: #606266;
  margin-bottom: 30px;
}

.el-button {
  margin: 0 10px;
}
</style> 