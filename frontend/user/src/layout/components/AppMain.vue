<template>
  <section class="app-main">
    <router-view v-slot="{ Component }">
      <transition name="fade-transform" mode="out-in">
        <keep-alive :include="cachedViews">
          <component :is="Component" :key="key" />
        </keep-alive>
      </transition>
    </router-view>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useLayoutStore } from '@/stores/layout'

const route = useRoute()
const layoutStore = useLayoutStore()

// 缓存视图
const cachedViews = computed(() => layoutStore.cachedViews)

// 路由key
const key = computed(() => route.path)
</script>

<style lang="scss" scoped>
.app-main {
  padding: 20px;
  position: relative;
  overflow: auto;
  width: 100%;
  flex: 1;
  box-sizing: border-box;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>