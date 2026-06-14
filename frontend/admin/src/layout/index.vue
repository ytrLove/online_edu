<!-- The exported code uses Tailwind CSS. Install Tailwind CSS in your dev environment to ensure all styles work. -->
<template>
    <div class="app-wrapper">
        <!-- 顶部导航 -->
        <el-header class="app-header">
            <div class="header-left">
                <div class="logo-container">
                    <el-icon><School /></el-icon>
                    <span class="logo-text">教育管理系统</span>
                </div>
            </div>
            <div class="header-right">
                <el-badge :value="3" class="notification-badge">
                    <el-button @click="toggleNotifications">
                        <el-icon><Bell /></el-icon>
                    </el-button>
                </el-badge>
                <el-dropdown @command="handleCommand" trigger="click">
                    <div class="user-info">
                        <el-avatar :size="32" :src="userStore.userInfo.user.avatar || ''" />
                        <span class="username">{{ userStore.userInfo.user.username || '用户' }}</span>
                        <el-icon><CaretBottom /></el-icon>
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="profile">
                                <el-icon><User /></el-icon>个人信息
                            </el-dropdown-item>
                            <!-- <el-dropdown-item command="settings">
                                <el-icon><Setting /></el-icon>系统设置
                            </el-dropdown-item> -->
                            <el-dropdown-item divided command="logout" class="text-red">
                                <el-icon><SwitchButton /></el-icon>退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </el-header>

        <div class="app-container">
            <!-- 侧边栏 -->
            <el-aside :width="isCollapsed ? '64px' : '240px'" class="app-sidebar">
                <el-menu
                    :collapse="isCollapsed"
                    :default-active="activeMenu"
                    class="sidebar-menu"
                    background-color="#001529"
                    text-color="#fff"
                    active-text-color="#409EFF"
                    :unique-opened="true"
                    router
                >
                    <el-menu-item class="collapse-btn" @click="toggleSidebar">
                        <el-icon>
                            <component :is="isCollapsed ? 'Expand' : 'Fold'" />
                        </el-icon>
                        <template #title>
                            <span>收起菜单</span>
                        </template>
                    </el-menu-item>

                    <!-- 首页菜单 -->
                    <el-menu-item index="/dashboard">
                        <el-icon><HomeFilled /></el-icon>
                        <template #title>首页</template>
                    </el-menu-item>

                    <!-- 渲染后端返回的菜单 -->
                    <div v-for="menu in userStore.menus" :key="menu.id">
                        <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
                            <template #title>
                                <el-icon>
                                    <component :is="getMenuIcon(menu.icon)" />
                                </el-icon>
                                <span>{{ menu.name }}</span>
                            </template>
                            <el-menu-item
                                v-for="child in menu.children"
                                :key="child.id"
                                :index="child.path"
                                @click="handleMenuClick(child.path)"
                            >
                                <el-icon v-if="child.icon">
                                    <component :is="getMenuIcon(child.icon)" />
                                </el-icon>
                                <span>{{ child.name }}</span>
                            </el-menu-item>
                        </el-sub-menu>
                        <el-menu-item v-else :index="menu.path" @click="handleMenuClick(menu.path)">
                            <el-icon>
                                <component :is="getMenuIcon(menu.icon)" />
                            </el-icon>
                            <template #title>{{ menu.name }}</template>
                        </el-menu-item>
                    </div>

                    <!-- 不再渲染前端静态菜单，避免重复 -->
                    <!-- 
                    <div v-for="route in filteredRoutes" :key="route.path">
                        <sidebar-item :item="route" :base-path="route.path" />
                    </div>
                    -->
                </el-menu>
            </el-aside>

            <!-- 主内容区 -->
            <el-main class="app-main">
                <el-breadcrumb class="app-breadcrumb">
                    <el-breadcrumb-item :to="{ path: '/' }">
                        <el-icon><HomeFilled /></el-icon>首页
                    </el-breadcrumb-item>
                    <el-breadcrumb-item v-if="route.meta && route.meta.title">
                        {{ route.meta.title }}
                    </el-breadcrumb-item>
                    <!-- <el-breadcrumb-item v-else>
                        {{ route.name || '未知页面' }}
                    </el-breadcrumb-item> -->
                </el-breadcrumb>
                
                <router-view />
            </el-main>
        </div>

        <!-- 通知面板 -->
        <el-drawer
            v-model="showNotifications"
            title="通知消息"
            direction="rtl"
            size="300px"
        >
            <template #header>
                <div class="notification-header">
                    <span>通知消息</span>
                    <el-button link type="primary" size="small">全部标记已读</el-button>
                </div>
            </template>
            <el-scrollbar height="calc(100vh - 120px)">
                <div class="notification-list">
                    <div
                        v-for="notice in notifications"
                        :key="notice.id"
                        class="notification-item"
                    >
                        <el-icon :class="notice.iconColor">
                            <component :is="notificationIconMap[notice.icon]" />
                        </el-icon>
                        <div class="notification-content">
                            <div class="notification-title">{{ notice.title }}</div>
                            <div class="notification-time">{{ notice.time }}</div>
                        </div>
                    </div>
                </div>
            </el-scrollbar>
        </el-drawer>
    </div>
</template>
<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '../stores/user';
import SidebarItem from './components/SidebarItem.vue';
import {
    Bell,
    User,
    DataLine,
    Reading,
    Folder,
    List,
    ChatDotRound,
    Star,
    HomeFilled
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();
const isCollapsed = ref(false);
const searchQuery = ref('');
const showNotifications = ref(false);
const userStore = useUserStore();

// 计算当前激活的菜单
const activeMenu = computed(() => {
    return route.path
})

// 初始化加载用户菜单
const loadUserMenus = async () => {
    // 确保用户已登录
    if (!userStore.userInfo.userId) {
        await userStore.getUserInfoAction()
    }
    
    await userStore.getUserMenusAction()
}

// 注意：将onMounted放在这里，在所有async函数定义之前
onMounted(() => {
    console.log(userStore.userInfo);
    loadUserMenus()
})

// 获取可显示的菜单路由
const filteredRoutes = computed(() => {
    // 从路由列表中过滤出需要在侧边栏显示的路由
    const routes = router.getRoutes();
    return routes
        .filter(route => {
            // 排除首页路由和隐藏路由
            return route.meta && 
                   !route.meta.hidden && 
                   route.children &&
                   route.path !== '/' // 排除包含首页的布局路由
        })
        .map(route => {
            return {
                path: route.path,
                name: route.name,
                meta: route.meta,
                children: route.children
                    .filter(child => {
                        // 排除dashboard路由和隐藏路由
                        return child.meta && 
                               !child.meta.hidden && 
                               child.path !== 'dashboard'
                    })
                    .map(child => ({
                        path: child.path,
                        name: child.name,
                        meta: child.meta
                    }))
            };
        })
        .filter(route => route.children && route.children.length > 0);
});

// 获取菜单图标
const getMenuIcon = (iconName) => {
    // 如果没有图标名称，返回默认图标
    if (!iconName) return 'Menu';
    
    // 处理fontawesome图标转换
    if (typeof iconName === 'string' && iconName.startsWith('fa-')) {
        const iconMap = {
            'fa-chart-line': 'DataLine',
            'fa-users': 'User',
            'fa-book': 'Reading',
            'fa-folder': 'Folder',
            'fa-tasks': 'List',
            'fa-comments': 'ChatDotRound',
            'fa-star': 'Star'
        }
        return iconMap[iconName] || 'Menu'
    }
    
    // 直接使用Element Plus图标
    const elementIconMap = {
        'system': 'Setting',
        'education': 'Reading',
        'documentation': 'Document',
        'clipboard': 'Document',
        'chart': 'DataLine',
        'user': 'User',
        'peoples': 'User',
        'tree-table': 'Operation',
        'tree': 'Notebook',
        'list': 'List',
        'guide': 'Guide',
        'form': 'Tickets',
        'edit': 'Edit',
        'education': 'School',
        'dict': 'Collection',
        'zip': 'Files',
        'build': 'Folder',
        'download': 'Download',
        'time': 'Timer',
        'date': 'Calendar',
        'skill': 'Aim',
        'message': 'ChatDotSquare',
        'chat': 'ChatDotRound',
        'email': 'Message',
        'menu': 'Menu',
        'setting': 'Setting',
        'home': 'HomeFilled',
        'dashboard': 'DataLine'
    }
    
    // 1. 检查是否已经是Element Plus图标名称
    if (typeof iconName === 'string') {
        // 2. 尝试从映射表中获取对应图标
        if (elementIconMap[iconName.toLowerCase()]) {
            return elementIconMap[iconName.toLowerCase()];
        }
        
        // 3. 尝试直接返回，可能本身就是图标名称
        return iconName;
    }
    
    // 4. 如果都不匹配，返回默认图标
    return 'Menu';
}

// 处理菜单点击
const handleMenuClick = (path) => {
    console.log('点击菜单:', path);
    // 如果路径不是以/开头，添加/
    if (!path.startsWith('/')) {
        path = `/${path}`;
    }
    
    // 手动处理路由导航
    router.push(path).catch(err => {
        console.error('导航失败:', err);
        if (err.name !== 'NavigationDuplicated') {
            // 如果不是重复导航错误，则显示提示
            ElMessage.error({
                message: '页面加载失败，正在刷新路由...',
                duration: 2000,
                onClose: () => {
                    // 刷新页面以重新加载路由
                    window.location.reload();
                }
            });
        }
    });
};

const toggleSidebar = () => {
    isCollapsed.value = !isCollapsed.value;
};

const toggleNotifications = () => {
    showNotifications.value = !showNotifications.value;
};

const handleCommand = async (command) => {
    switch (command) {
        case 'profile':
            console.log('尝试导航到个人信息页面')
            try {
                // 使用路径导航，避免名称不匹配问题
                await router.push('/profile')
                console.log('导航成功')
            } catch (error) {
                console.error('导航到个人信息页面失败:', error)
                ElMessage.error('无法访问个人信息页面')
            }
            break
        case 'settings':
            ElMessage.info('系统设置功能开发中')
            break
        case 'logout':
            await userStore.logoutAction()
            router.push('/login')
            break
    }
}

const notifications = [
    {
        id: 1,
        title: '新作业提醒：高三数学第12章习题已发布',
        time: '10分钟前',
        icon: 'fa-book',
        iconColor: 'text-blue-500'
    },
    {
        id: 2,
        title: '课程提醒：物理实验课程将在30分钟后开始',
        time: '30分钟前',
        icon: 'fa-clock',
        iconColor: 'text-green-500'
    },
    {
        id: 3,
        title: '考试通知：期中考试时间安排已发布',
        time: '1小时前',
        icon: 'fa-file-alt',
        iconColor: 'text-orange-500'
    }
];

// 通知图标映射
const notificationIconMap = {
    'fa-book': Reading,
    'fa-user': User,
    'fa-bell': Bell
}
</script>
<style scoped>
.app-wrapper {
    min-height: 100vh;
}

.app-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
    height: 60px;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
}

.header-left {
    width: 240px;
}

.logo-container {
    display: flex;
    align-items: center;
    gap: 8px;
}

.logo-icon {
    font-size: 24px;
    color: var(--el-color-primary);
}

.logo-text {
    font-size: 18px;
    font-weight: 600;
    color: var(--el-text-color-primary);
}

.header-center {
    flex: 1;
    display: flex;
    justify-content: center;
}

.search-input {
    width: 400px;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 20px;
}

.notification-badge :deep(.el-badge__content) {
    z-index: 1001;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 0 8px;
}

.username {
    font-size: 14px;
    color: var(--el-text-color-primary);
}

.app-container {
    padding-top: 60px;
    display: flex;
}

.app-sidebar {
    position: fixed;
    left: 0;
    top: 60px;
    bottom: 0;
    transition: width 0.3s;
    z-index: 999;
    overflow-x: hidden;
    box-shadow: 2px 0 6px rgba(0, 21, 41, 0.05);
}

.sidebar-menu {
    height: 100%;
    border-right: none;
}

:deep(.el-menu--collapse) {
    width: 64px !important;
}

.collapse-btn {
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.app-main {
    margin-left: v-bind('isCollapsed ? "64px" : "240px"');
    padding: 20px;
    transition: margin-left 0.3s;
    width: calc(100% - v-bind('isCollapsed ? "64px" : "240px"'));
}

.app-breadcrumb {
    margin-bottom: 20px;
}

.notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
}

.notification-list {
    padding: 0 16px;
}

.notification-item {
    display: flex;
    gap: 12px;
    padding: 16px 0;
    border-bottom: 1px solid var(--el-border-color-lighter);
}

.notification-content {
    flex: 1;
}

.notification-title {
    font-size: 14px;
    color: var(--el-text-color-primary);
    margin-bottom: 4px;
}

.notification-time {
    font-size: 12px;
    color: var(--el-text-color-secondary);
}

.text-red {
    color: var(--el-color-danger);
}
</style>