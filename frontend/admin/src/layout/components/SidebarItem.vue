<template>
  <div v-if="!item.meta || !item.meta.hidden">
    <template v-if="hasOneShowingChild(item) && !onlyOneChild.children">
      <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{ 'submenu-title-noDropdown': !isNest }">
        <el-icon v-if="onlyOneChild.meta && onlyOneChild.meta.icon">
          <component :is="onlyOneChild.meta.icon" />
        </el-icon>
        <template #title>
          <span>{{ onlyOneChild.meta && onlyOneChild.meta.title }}</span>
        </template>
      </el-menu-item>
    </template>

    <el-sub-menu v-else ref="subMenu" :index="resolvePath(item.path)" popper-append-to-body>
      <template #title>
        <el-icon v-if="item.meta && item.meta.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta && item.meta.title }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :is-nest="true"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import path from 'path-browserify'

export default {
  name: 'SidebarItem',
  props: {
    // 菜单项
    item: {
      type: Object,
      required: true
    },
    // 是否嵌套
    isNest: {
      type: Boolean,
      default: false
    },
    // 基础路径
    basePath: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    const onlyOneChild = ref(null)

    // 是否只有一个显示的子项
    const hasOneShowingChild = (parent) => {
      const showingChildren = parent.children ? parent.children.filter(item => {
        if (item.meta && item.meta.hidden) {
          return false
        }
        // 如果没有隐藏则显示
        onlyOneChild.value = item
        return true
      }) : []

      // 如果只有一个子节点，返回true
      if (showingChildren.length === 1) {
        return true
      }

      // 如果没有子节点，且不隐藏，返回true
      if (showingChildren.length === 0) {
        onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
        return true
      }

      return false
    }

    // 解析路径
    const resolvePath = (routePath) => {
      if (path.isAbsolute(routePath)) {
        return routePath
      }
      if (path.isAbsolute(props.basePath)) {
        return path.resolve(props.basePath, routePath)
      }
      return path.resolve(props.basePath, routePath)
    }

    return {
      onlyOneChild,
      hasOneShowingChild,
      resolvePath
    }
  }
}
</script>

<style scoped>
.submenu-title-noDropdown {
  padding-left: 20px;
}
</style> 