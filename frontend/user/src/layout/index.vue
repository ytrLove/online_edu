<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <sidebar class="sidebar-container" />
    
    <!-- 主区域 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <navbar />
      
      <!-- 标签栏 -->
      <tags-view v-if="showTagsView" />
      
      <!-- 主要内容区 -->
      <app-main />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useLayoutStore } from '@/stores/layout'
import Navbar from './components/Navbar.vue'
import Sidebar from './components/Sidebar.vue'
import AppMain from './components/AppMain.vue'
import TagsView from './components/TagsView.vue'

const layoutStore = useLayoutStore()
const showTagsView = computed(() => layoutStore.showTagsView)
</script>

<style lang="scss" scoped>
.app-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  
  .sidebar-container {
    width: 210px;
    height: 100%;
    background-color: #304156;
    transition: width 0.3s;
    overflow: hidden;
  }
  
  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    height: 100%;
  }
}
</style> 