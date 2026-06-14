<template>
  <div class="container">
    <el-row :gutter="20">
      <!-- 论坛概览 -->
      <el-col :span="24">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>论坛活跃度概览</span>
              <div>
                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  @change="handleDateChange"
                />
              </div>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-title">总帖子数</div>
                <div class="stat-value">{{ overview.totalPosts }}</div>
                <div class="stat-trend" :class="{ 'up': overview.postsTrend > 0, 'down': overview.postsTrend < 0 }">
                  <i :class="overview.postsTrend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ Math.abs(overview.postsTrend) }}%
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-title">总回复数</div>
                <div class="stat-value">{{ overview.totalReplies }}</div>
                <div class="stat-trend" :class="{ 'up': overview.repliesTrend > 0, 'down': overview.repliesTrend < 0 }">
                  <i :class="overview.repliesTrend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ Math.abs(overview.repliesTrend) }}%
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-title">活跃用户数</div>
                <div class="stat-value">{{ overview.activeUsers }}</div>
                <div class="stat-trend" :class="{ 'up': overview.usersTrend > 0, 'down': overview.usersTrend < 0 }">
                  <i :class="overview.usersTrend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ Math.abs(overview.usersTrend) }}%
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-title">平均回复率</div>
                <div class="stat-value">{{ overview.replyRate }}%</div>
                <div class="stat-trend" :class="{ 'up': overview.replyRateTrend > 0, 'down': overview.replyRateTrend < 0 }">
                  <i :class="overview.replyRateTrend > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  {{ Math.abs(overview.replyRateTrend) }}%
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 帖子趋势图 -->
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>帖子发布趋势</span>
            </div>
          </template>
          <div ref="postsChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 回复趋势图 -->
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>回复趋势</span>
            </div>
          </template>
          <div ref="repliesChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 热门板块 -->
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>热门板块</span>
            </div>
          </template>
          <div ref="categoriesChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 活跃用户 -->
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>活跃用户TOP10</span>
            </div>
          </template>
          <el-table
            :data="activeUsers"
            style="width: 100%"
            border
            :highlight-current-row="true"
          >
            <el-table-column prop="rank" label="排名" width="60" align="center" />
            <el-table-column prop="username" label="用户名">
              <template #default="scope">
                <div class="user-info">
                  <el-avatar :size="30" :src="scope.row.avatar"></el-avatar>
                  <span class="ml-10">{{ scope.row.username }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="posts" label="发帖数" width="80" align="center" />
            <el-table-column prop="replies" label="回复数" width="80" align="center" />
            <el-table-column prop="totalActivity" label="活跃度" width="80" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- 热门帖子 -->
      <el-col :span="24">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>热门帖子TOP10</span>
            </div>
          </template>
          <el-table
            :data="hotPosts"
            style="width: 100%"
            border
            :highlight-current-row="true"
          >
            <el-table-column prop="rank" label="排名" width="60" align="center" />
            <el-table-column prop="title" label="帖子标题" min-width="300">
              <template #default="scope">
                <el-link type="primary" @click="viewPost(scope.row)">{{ scope.row.title }}</el-link>
              </template>
            </el-table-column>
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="category" label="所属板块" width="120" />
            <el-table-column prop="createTime" label="发布时间" width="180" />
            <el-table-column prop="viewCount" label="浏览量" width="100" align="center" />
            <el-table-column prop="replyCount" label="回复数" width="100" align="center" />
            <el-table-column prop="likeCount" label="点赞数" width="100" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

const router = useRouter()
const dateRange = ref([])
const postsChartRef = ref(null)
const repliesChartRef = ref(null)
const categoriesChartRef = ref(null)
let postsChart = null
let repliesChart = null
let categoriesChart = null

// 论坛概览数据
const overview = ref({
  totalPosts: 1528,
  postsTrend: 12.5,
  totalReplies: 8732,
  repliesTrend: 8.3,
  activeUsers: 312,
  usersTrend: -2.1,
  replyRate: 87.4,
  replyRateTrend: 3.6
})

// 活跃用户数据
const activeUsers = ref([
  { rank: 1, username: '张教授', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 42, replies: 128, totalActivity: 170 },
  { rank: 2, username: '李同学', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 36, replies: 115, totalActivity: 151 },
  { rank: 3, username: '王老师', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 31, replies: 109, totalActivity: 140 },
  { rank: 4, username: '赵助教', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 28, replies: 95, totalActivity: 123 },
  { rank: 5, username: '钱同学', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 25, replies: 83, totalActivity: 108 },
  { rank: 6, username: '孙教授', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 19, replies: 76, totalActivity: 95 },
  { rank: 7, username: '周助教', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 18, replies: 68, totalActivity: 86 },
  { rank: 8, username: '吴同学', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 16, replies: 62, totalActivity: 78 },
  { rank: 9, username: '郑老师', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 15, replies: 57, totalActivity: 72 },
  { rank: 10, username: '冯同学', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 14, replies: 52, totalActivity: 66 }
])

// 热门帖子数据
const hotPosts = ref([
  { rank: 1, id: 1, title: '如何提高Java编程效率的10个技巧', author: '张教授', category: '编程技术', createTime: '2024-04-01 14:30:22', viewCount: 2560, replyCount: 156, likeCount: 342 },
  { rank: 2, id: 2, title: 'Python在人工智能领域的应用前景', author: '李同学', category: '人工智能', createTime: '2024-04-02 09:15:36', viewCount: 2105, replyCount: 128, likeCount: 287 },
  { rank: 3, id: 3, title: '大数据分析实战案例分享', author: '王老师', category: '大数据', createTime: '2024-03-28 16:42:15', viewCount: 1856, replyCount: 112, likeCount: 241 },
  { rank: 4, id: 4, title: '前端框架比较：React vs Vue vs Angular', author: '赵助教', category: '前端技术', createTime: '2024-03-25 11:20:48', viewCount: 1742, replyCount: 98, likeCount: 226 },
  { rank: 5, id: 5, title: '区块链技术入门指南', author: '钱同学', category: '区块链', createTime: '2024-03-30 13:38:52', viewCount: 1568, replyCount: 86, likeCount: 198 },
  { rank: 6, id: 6, title: '如何准备软件工程师面试', author: '孙教授', category: '求职就业', createTime: '2024-04-03 10:05:33', viewCount: 1472, replyCount: 82, likeCount: 176 },
  { rank: 7, id: 7, title: '数据库性能优化实践', author: '周助教', category: '数据库', createTime: '2024-03-27 15:24:19', viewCount: 1325, replyCount: 74, likeCount: 165 },
  { rank: 8, id: 8, title: '移动应用开发趋势分析', author: '吴同学', category: '移动开发', createTime: '2024-03-29 08:52:41', viewCount: 1287, replyCount: 68, likeCount: 153 },
  { rank: 9, id: 9, title: '算法竞赛备战攻略', author: '郑老师', category: '算法竞赛', createTime: '2024-04-04 16:35:27', viewCount: 1156, replyCount: 62, likeCount: 132 },
  { rank: 10, id: 10, title: '云计算服务比较与选择', author: '冯同学', category: '云计算', createTime: '2024-03-26 11:48:33', viewCount: 1092, replyCount: 58, likeCount: 121 }
])

// 初始化帖子趋势图
const initPostsChart = () => {
  if (postsChart) {
    postsChart.dispose()
  }
  
  postsChart = echarts.init(postsChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['发帖数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '发帖数',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230, 210, 182, 191, 234, 290, 330],
        areaStyle: {}
      }
    ]
  }
  
  postsChart.setOption(option)
  window.addEventListener('resize', postsChart.resize)
}

// 初始化回复趋势图
const initRepliesChart = () => {
  if (repliesChart) {
    repliesChart.dispose()
  }
  
  repliesChart = echarts.init(repliesChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['回复数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '回复数',
        type: 'line',
        data: [320, 332, 301, 334, 390, 530, 610, 582, 591, 634, 690, 730],
        areaStyle: {}
      }
    ]
  }
  
  repliesChart.setOption(option)
  window.addEventListener('resize', repliesChart.resize)
}

// 初始化板块分布图
const initCategoriesChart = () => {
  if (categoriesChart) {
    categoriesChart.dispose()
  }
  
  categoriesChart = echarts.init(categoriesChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: ['编程技术', '人工智能', '大数据', '前端技术', '区块链', '求职就业', '数据库', '移动开发', '算法竞赛', '云计算']
    },
    series: [
      {
        name: '帖子分布',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 256, name: '编程技术' },
          { value: 210, name: '人工智能' },
          { value: 186, name: '大数据' },
          { value: 174, name: '前端技术' },
          { value: 156, name: '区块链' },
          { value: 147, name: '求职就业' },
          { value: 132, name: '数据库' },
          { value: 128, name: '移动开发' },
          { value: 115, name: '算法竞赛' },
          { value: 109, name: '云计算' }
        ]
      }
    ]
  }
  
  categoriesChart.setOption(option)
  window.addEventListener('resize', categoriesChart.resize)
}

// 处理日期变化
const handleDateChange = (val) => {
  console.log('日期变化:', val)
  // TODO: 根据日期范围重新获取数据
}

// 查看帖子
const viewPost = (post) => {
  router.push({
    path: `/forum/post/${post.id}`,
    query: { title: post.title }
  })
}

onMounted(() => {
  nextTick(() => {
    initPostsChart()
    initRepliesChart()
    initCategoriesChart()
  })
})
</script>

<style scoped>
.container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.mt-20 {
  margin-top: 20px;
}
.ml-10 {
  margin-left: 10px;
}
.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  background-color: #f9f9f9;
  border-radius: 5px;
}
.stat-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}
.stat-trend {
  display: flex;
  align-items: center;
  font-size: 14px;
}
.stat-trend.up {
  color: #67c23a;
}
.stat-trend.down {
  color: #f56c6c;
}
.chart-container {
  height: 350px;
}
.user-info {
  display: flex;
  align-items: center;
}
</style> 