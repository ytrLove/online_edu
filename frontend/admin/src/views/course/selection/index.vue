<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程选择</span>
          <div class="search-box">
            <el-form :inline="true" :model="queryParams" @submit.prevent>
              <el-form-item label="课程名称">
                <el-input
                  v-model="queryParams.courseName"
                  placeholder="请输入课程名称"
                  clearable
                  @keyup.enter="handleQuery"
                />
              </el-form-item>
              <el-form-item label="教师姓名">
                <el-input
                  v-model="queryParams.teacherName"
                  placeholder="请输入教师姓名"
                  clearable
                  @keyup.enter="handleQuery"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleQuery">搜索</el-button>
                <el-button @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="courseList"
        style="width: 100%"
        border
      >
        <el-table-column
          prop="courseName"
          label="课程名称"
          min-width="200"
        />
        <el-table-column
          prop="courseCode"
          label="课程代码"
          width="120"
        />
        <el-table-column
          prop="teacherName"
          label="授课教师"
          width="100"
        />
        <el-table-column
          prop="creditHours"
          label="学时"
          width="80"
        />
        <el-table-column
          prop="credits"
          label="学分"
          width="80"
        />
        <el-table-column
          prop="capacity"
          label="容量"
          width="80"
        >
          <template #default="scope">
            <span>{{ scope.row.selected }}/{{ scope.row.capacity }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="semester"
          label="学期"
          width="100"
        />
        <el-table-column
          prop="status"
          label="状态"
          width="100"
        >
          <template #default="scope">
            <el-tag
              :type="scope.row.isSelected ? 'success' : scope.row.selected >= scope.row.capacity ? 'danger' : ''"
            >
              {{ scope.row.isSelected ? '已选' : scope.row.selected >= scope.row.capacity ? '已满' : '可选' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="200"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              type="primary"
              link
              @click="viewCourseDetail(scope.row)"
            >
              查看详情
            </el-button>
            <el-button
              v-if="!scope.row.isSelected && scope.row.selected < scope.row.capacity"
              type="success"
              link
              @click="selectCourse(scope.row)"
            >
              选课
            </el-button>
            <el-button
              v-if="scope.row.isSelected"
              type="danger"
              link
              @click="unselectCourse(scope.row)"
            >
              退选
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          :current-page="queryParams.pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const courseList = ref([])
const total = ref(0)

// 查询参数
const queryParams = ref({
  courseName: '',
  teacherName: '',
  pageNum: 1,
  pageSize: 10
})

// 获取可选课程列表
const getCourseList = async () => {
  loading.value = true
  try {
    // TODO: 调用后端接口获取可选课程列表
    // const res = await getAvailableCourses(queryParams.value)
    // 模拟数据
    setTimeout(() => {
      courseList.value = [
        {
          courseId: 1,
          courseName: 'Web前端开发技术',
          courseCode: 'CS-001',
          teacherName: '张三',
          creditHours: 48,
          credits: 3,
          capacity: 50,
          selected: 30,
          semester: '2023-2024-2',
          isSelected: true
        },
        {
          courseId: 2,
          courseName: 'Java程序设计',
          courseCode: 'CS-002',
          teacherName: '李四',
          creditHours: 64,
          credits: 4,
          capacity: 60,
          selected: 45,
          semester: '2023-2024-2',
          isSelected: false
        },
        {
          courseId: 3,
          courseName: '数据库原理与应用',
          courseCode: 'CS-003',
          teacherName: '王五',
          creditHours: 48,
          credits: 3,
          capacity: 40,
          selected: 40,
          semester: '2023-2024-2',
          isSelected: false
        },
        {
          courseId: 4,
          courseName: '计算机网络',
          courseCode: 'CS-004',
          teacherName: '赵六',
          creditHours: 48,
          credits: 3,
          capacity: 45,
          selected: 20,
          semester: '2023-2024-2',
          isSelected: false
        }
      ]
      total.value = 4
      loading.value = false
    }, 300)
  } catch (error) {
    console.error('获取课程列表失败', error)
    ElMessage.error('获取课程列表失败')
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getCourseList()
}

// 重置查询
const resetQuery = () => {
  queryParams.value = {
    courseName: '',
    teacherName: '',
    pageNum: 1,
    pageSize: 10
  }
  getCourseList()
}

// 查看课程详情
const viewCourseDetail = (row) => {
  router.push({
    path: `/course/detail/${row.courseId}`,
    query: { name: row.courseName }
  })
}

// 选课
const selectCourse = async (row) => {
  try {
    // TODO: 调用后端接口执行选课操作
    // await selectStudentCourse(row.courseId)
    // 模拟选课成功
    ElMessageBox.confirm(`确认选择课程：${row.courseName}？`, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 模拟选课成功
      row.isSelected = true
      row.selected += 1
      ElMessage.success('选课成功')
    }).catch(() => {
      // 用户取消选课
    })
  } catch (error) {
    console.error('选课失败', error)
    ElMessage.error('选课失败，请稍后重试')
  }
}

// 退选
const unselectCourse = async (row) => {
  try {
    // TODO: 调用后端接口执行退选操作
    // await unselectStudentCourse(row.courseId)
    ElMessageBox.confirm(`确认退选课程：${row.courseName}？退选后可能无法再次选择。`, '警告', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 模拟退选成功
      row.isSelected = false
      row.selected -= 1
      ElMessage.success('退选成功')
    }).catch(() => {
      // 用户取消退选
    })
  } catch (error) {
    console.error('退选失败', error)
    ElMessage.error('退选失败，请稍后重试')
  }
}

// 处理每页数量变化
const handleSizeChange = (size) => {
  queryParams.value.pageSize = size
  getCourseList()
}

// 处理页码变化
const handleCurrentChange = (page) => {
  queryParams.value.pageNum = page
  getCourseList()
}

onMounted(() => {
  getCourseList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.card-header {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.search-box {
  margin-top: 15px;
}
.pagination-container {
  margin-top: 15px;
  text-align: right;
}
</style> 