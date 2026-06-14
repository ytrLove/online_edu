import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLayoutStore = defineStore('layout', () => {
  // 是否显示侧边栏
  const sidebarOpened = ref(true)
  
  // 是否显示标签栏
  const showTagsView = ref(true)
  
  // 访问过的视图
  const visitedViews = ref([])
  const cachedViews = ref([])
  
  // 切换侧边栏
  function toggleSidebar() {
    sidebarOpened.value = !sidebarOpened.value
  }
  
  // 添加访问视图
  function addVisitedView(view) {
    if (visitedViews.value.some(v => v.path === view.path)) return
    visitedViews.value.push(
      Object.assign({}, view, {
        title: view.meta.title || 'no-name'
      })
    )
  }
  
  // 添加缓存视图
  function addCachedView(view) {
    if (view.name && !view.meta.noCache && !cachedViews.value.includes(view.name)) {
      cachedViews.value.push(view.name)
    }
  }
  
  // 删除访问视图
  function delVisitedView(view) {
    for (const [i, v] of visitedViews.value.entries()) {
      if (v.path === view.path) {
        visitedViews.value.splice(i, 1)
        break
      }
    }
  }
  
  // 删除缓存视图
  function delCachedView(view) {
    const index = cachedViews.value.indexOf(view.name)
    if (index > -1) {
      cachedViews.value.splice(index, 1)
    }
  }
  
  // 关闭视图
  function closeView(view) {
    delVisitedView(view)
    delCachedView(view)
  }
  
  return {
    sidebarOpened,
    showTagsView,
    visitedViews,
    cachedViews,
    toggleSidebar,
    addVisitedView,
    addCachedView,
    delVisitedView,
    delCachedView,
    closeView
  }
}) 