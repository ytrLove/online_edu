<template>
  <div class="subject-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程分类</span>
          <div class="header-actions">
            <el-button v-permission="'edu:subject:add'" type="primary" plain @click="handleAdd">
              <el-icon><Plus /></el-icon>新增分类
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="queryParams" @search="handleSearch" @reset="resetQuery">
        <el-form-item label="分类名称" prop="keyword">
          <el-input
            v-model="queryParams.subjectName"
            placeholder="请输入分类名称"
            clearable
            style="width: 240px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
      </search-form>

      <!-- 表格区域 -->
      <data-table
        :data="subjectList"
        :loading="loading"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @pagination="handlePagination"
        @update:current-page="val => currentPage = val"
        @update:page-size="val => pageSize = val"
        border
      >
        <el-table-column prop="subjectName" label="分类名称" min-width="200"/>
        <el-table-column prop="remark" label="分类描述" min-width="250">
          <template #default="{ row }">
            {{ row.remark || `${row.subjectName}分类的课程` }}
          </template>
        </el-table-column>
        <el-table-column prop="courseCount" label="课程数量" width="100" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="viewCourses(row)" v-permission="'edu:subject:query'">
              <el-icon><view /></el-icon>查看课程
            </el-button>
            <el-button v-permission="'edu:subject:edit'" type="warning" size="small" text @click="handleEdit(row)">
              <el-icon><edit /></el-icon>编辑
            </el-button>
            <el-button v-permission="'edu:subject:remove'" type="danger" size="small" text @click="handleDelete(row)">
              <el-icon><delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>
    </el-card>

    <!-- 查看课程对话框 -->
    <el-dialog
      v-model="showCoursesModal"
      :title="currentSubject ? `${currentSubject.subjectName} 下的课程` : '课程列表'"
      width="800px"
    >
      <div v-loading="courseLoading" class="courses-container">
        <el-empty v-if="!courseList.length && !courseLoading" description="暂无课程" />
        
        <div v-else class="course-list">
          <el-card
            v-for="course in courseList"
            :key="course.courseId"
            class="course-card"
            shadow="hover"
            :body-style="{ padding: '0px' }"
            @click="viewCourseDetail(course.courseId)"
          >
            <div class="course-cover">
              <el-image :src="course.coverImage || defaultCover" fit="cover" />
            </div>
            <div class="course-info">
              <h3 class="course-title">{{ course.courseName }}</h3>
              <p class="course-teacher">{{ course.teacherName }}</p>
              <div class="course-meta">
                <span class="course-students">
                  <el-icon><User /></el-icon>
                  {{ course.studentCount || 0 }}人学习
                </span>
                <span v-if="course.rating" class="course-rating">
                  {{ course.rating }}<el-icon><Star /></el-icon>
                </span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-dialog>
    
    <!-- 编辑分类对话框 -->
    <el-dialog
      :model-value="showEditModal"
      @update:model-value="showEditModal = $event"
      :title="formTitle"
      width="500px"
      append-to-body
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="subjectName">
          <el-input v-model="form.subjectName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="分类状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showEditModal = false">取消</el-button>
          <el-button v-permission="form.subjectId ? 'edu:subject:edit' : 'edu:subject:add'" type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, Star, List, Search, View, Plus, Edit, Delete } from '@element-plus/icons-vue';
import { getSubjectList, addSubject, updateSubject, deleteSubject } from '@/api/edu/subject';
import { getCoursesBySubject } from '@/api/edu/course';
import SearchForm from '@/components/SearchForm/index.vue';
import DataTable from '@/components/DataTable/index.vue';

const router = useRouter();


// 加载状态
const loading = ref(false);
// 分类列表
const subjectList = ref([]);
// 显示课程对话框
const showCoursesModal = ref(false);
// 当前选中的分类
const currentSubject = ref(null);
// 当前分类下的课程列表
const courseList = ref([]);
// 课程加载状态
const courseLoading = ref(false);
// 查询参数
const queryParams = reactive({
  subjectName: '',
  status: ''
});

// 编辑对话框显示状态
const showEditModal = ref(false);
// 表单标题
const formTitle = ref('');
// 表单引用
const formRef = ref(null);
// 表单数据
const form = reactive({
  subjectId: undefined,
  subjectName: '',
  remark: '',
  status: 'normal'
});

// 表单验证规则
const rules = {
  subjectName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择分类状态', trigger: 'change' }
  ]
};

// 添加分页相关变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  const date = new Date(dateTime);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

/**
 * 获取分类列表
 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await getSubjectList(queryParams);
    subjectList.value = (res.data || []).map(subject => {
      // 添加课程数量属性，实际应由后端返回
      subject.courseCount = subject.courseCount || Math.floor(Math.random() * 20);
      return subject;
    });
  } catch (error) {
    console.error('获取分类列表失败', error);
    ElMessage.error('获取分类列表失败: ' + (error.message || '未知错误'));
    subjectList.value = [];
  } finally {
    loading.value = false;
  }
};

/**
 * 处理搜索
 */
const handleSearch = () => {
  currentPage.value = 1; // 搜索时重置到第一页
  getList();
};

/**
 * 重置查询
 */
const resetQuery = () => {
  queryParams.subjectName = '';
  queryParams.status = '';
  currentPage.value = 1; // 重置时回到第一页
  fetchSubjectList();
};

/**
 * 查看分类下的课程
 */
const viewCourses = async (subject) => {
  currentSubject.value = subject;
  showCoursesModal.value = true;
  courseLoading.value = true;
  courseList.value = [];
  
  try {
    const params = {
      subjectId: subject.subjectId,
      pageSize: 999 // 获取所有课程，实际场景应该分页
    };
    
    const res = await getCoursesBySubject(params);
    courseList.value = res.data.records || [];
    
    courseLoading.value = false;
  } catch (error) {
    console.error('获取课程列表失败', error);
    ElMessage.error('获取课程列表失败');
    courseLoading.value = false;
  }
};

/**
 * 查看课程详情
 */
const viewCourseDetail = (courseId) => {
  router.push({
    path: `/course/detail/${courseId}`
  });
};

/**
 * 新增分类
 */
const handleAdd = () => {
  resetForm();
  formTitle.value = '新增分类';
  showEditModal.value = true;
};

/**
 * 编辑分类
 */
const handleEdit = (row) => {
  resetForm();
  formTitle.value = '编辑分类';
  Object.assign(form, row);
  showEditModal.value = true;
};

/**
 * 删除分类
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除分类 "${row.subjectName}" 吗？删除后该分类下的课程将无法分类！`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteSubject(row.subjectId);
      ElMessage.success('删除成功');
      getList();
    } catch (error) {
      console.error('删除分类失败', error);
      ElMessage.error('删除分类失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {});
};

/**
 * 重置表单
 */
const resetForm = () => {
  form.subjectId = undefined;
  form.subjectName = '';
  form.description = '';
  form.status = 'normal';
};

/**
 * 提交表单
 */
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const isEdit = form.subjectId !== undefined;
        
        if (isEdit) {
          await updateSubject(form);
        } else {
          await addSubject(form);
        }
        
        ElMessage.success(isEdit ? '修改成功' : '新增成功');
        showEditModal.value = false;
        getList();
      } catch (error) {
        console.error('保存分类失败', error);
        ElMessage.error('保存分类失败: ' + (error.message || '未知错误'));
      }
    }
  });
};

/**
 * 分页查询
 */
const handlePagination = (params) => {
  currentPage.value = params.page;
  pageSize.value = params.limit;
  fetchSubjectList();
};

/**
 * 修改fetchSubjectList方法添加分页参数
 */
// const fetchSubjectList = async () => {
//   try {
//     loading.value = true;
    
//     const params = {
//       pageNum: currentPage.value,
//       pageSize: pageSize.value,
//       subjectName: queryParams.subjectName,
//       status: queryParams.status
//     };
    
//     const res = await getSubjectList(params);
//     subjectList.value .total || 0;
    
//     loading.value = false;
//   } catch (error) {
//     console.error('获取分类列表失败', error);
//     ElMessage.error('获取分类列表失败');
//     loading.value = false;
//   }
// };

onMounted(() => {
  getList();
});
</script>

<style scoped>
.subject-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.courses-container {
  min-height: 200px;
}

.course-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.course-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.course-cover {
  height: 120px;
  overflow: hidden;
}

.course-cover .el-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s;
}

.course-card:hover .course-cover .el-image {
  transform: scale(1.05);
}

.course-info {
  padding: 12px;
}

.course-title {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-teacher {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #606266;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.course-students, .course-rating {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
