<template>
  <div class="student-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <div class="header-actions">
            <el-button type="primary" plain v-permission="'edu:student:add'" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增学生
            </el-button>
            <el-button type="warning" plain @click="fetchStudentList">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="searchQuery" @search="handleSearch" @reset="resetSearch">
        <el-form-item label="学生姓名">
          <el-input v-model="searchQuery.username" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="searchQuery.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchQuery.status" placeholder="学生状态" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="在读" value="active"></el-option>
            <el-option label="休学" value="leave"></el-option>
            <el-option label="毕业" value="graduated"></el-option>
          </el-select>
        </el-form-item>
      </search-form>

      <!-- 表格区域 -->
      <data-table
        :data="studentList"
        :loading="loading"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @pagination="handlePagination"
        @update:current-page="val => currentPage = val"
        @update:page-size="val => pageSize = val"
        border
      >
        <el-table-column label="学生信息" min-width="250">
          <template #default="{ row }">
            <div class="student-info-cell">
              <el-avatar :size="50" :src="row.avatar">{{ row.username ? row.username.substring(0, 1) : 'S' }}</el-avatar>
              <div class="student-details">
                <div class="student-name">{{ row.username }}</div>
                <div class="student-code">学号: {{ row.studentNo }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="major" label="专业" width="150"></el-table-column>
        <el-table-column prop="admissionDate" label="入学年份" width="100" align="center"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="150"></el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="row.studentStatus === 'active' ? 'success' : row.studentStatus === 'leave' ? 'warning' : 'info'"
            >
              {{ getStatusText(row.studentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="viewStudentDetail(row)">
              <el-icon><View /></el-icon>查看
            </el-button>
            <el-button v-permission="'edu:student:edit'" type="warning" size="small" text @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button v-permission="'edu:student:remove'" type="danger" size="small" text @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>

      <!-- 学生详情对话框 -->
      <el-dialog
        v-model="showDetailModal"
        title="学生详情"
        width="700px"
        append-to-body
      >
        <div v-if="studentDetail" class="student-detail">
          <div class="detail-header">
            <el-avatar :size="100" :src="studentDetail.avatar">{{ studentDetail.studentName ? studentDetail.studentName.substring(0, 1) : 'S' }}</el-avatar>
            <div class="detail-info">
              <h2>{{ studentDetail.username }}</h2>
              <p class="detail-code">学号：{{ studentDetail.studentNo }}</p>
              <p class="detail-department">院系/专业：{{ studentDetail.department }} / {{ studentDetail.major }}</p>
              <p class="detail-admission">入学年份：{{ studentDetail.admissionDate }}</p>
              <p class="detail-status">
                状态：
                <el-tag :type="getStatusType(studentDetail.studentStatus)">
                  {{ getStatusText(studentDetail.studentStatus) }}
                </el-tag>
              </p>
            </div>
          </div>
          <div class="detail-contact">
            <h3>联系方式</h3>
            <p><strong>手机：</strong>{{ studentDetail.phone }}</p>
            <p><strong>邮箱：</strong>{{ studentDetail.email }}</p>
            <p v-if="studentDetail.address"><strong>地址：</strong>{{ studentDetail.address }}</p>
          </div>
<!--          <div class="detail-courses" v-if="showCourses && studentDetail">-->
<!--            <h3>选课列表</h3>-->
<!--            <el-table-->
<!--              :data="courseList"-->
<!--              v-loading="loadingCourses"-->
<!--              border-->
<!--              stripe-->
<!--              style="width: 100%"-->
<!--            >-->
<!--              <el-table-column prop="courseName" label="课程名称" width="200" />-->
<!--              <el-table-column prop="courseCode" label="课程代码" width="120" />-->
<!--              <el-table-column prop="teacherName" label="授课教师" width="120" />-->
<!--              <el-table-column prop="score" label="成绩" width="80" align="center">-->
<!--                <template #default="{ row }">-->
<!--                  {{ row.score !== undefined && row.score !== null ? row.score : '-' }}-->
<!--                </template>-->
<!--              </el-table-column>-->
<!--              <el-table-column label="选课时间" width="180">-->
<!--                <template #default="{ row }">-->
<!--                  {{ formatDateTime(row.enrollTime) }}-->
<!--                </template>-->
<!--              </el-table-column>-->
<!--              <el-table-column label="状态" width="100" align="center">-->
<!--                <template #default="{ row }">-->
<!--                  <el-tag :type="row.enrollStatus === 'active' ? 'success' : 'info'">-->
<!--                    {{ row.enrollStatus === 'active' ? '在修' : '已修完' }}-->
<!--                  </el-tag>-->
<!--                </template>-->
<!--              </el-table-column>-->
<!--            </el-table>-->
<!--            <div v-if="courseList.length === 0 && !loadingCourses" class="no-data">-->
<!--              <el-empty description="暂无选课记录" />-->
<!--            </div>-->
<!--          </div>-->
<!--          <div class="detail-actions">-->
<!--            <el-button v-permission="'edu:student:query'" type="info" @click="toggleCourses">-->
<!--              {{ showCourses ? '隐藏选课列表' : '查看选课列表' }}-->
<!--            </el-button>-->
<!--          </div>-->
        </div>
      </el-dialog>

      <!-- 学生编辑对话框 -->
      <el-dialog
        v-model="showEditModal"
        :title="formTitle"
        width="650px"
        append-to-body
        destroy-on-close
      >
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="学生姓名" prop="username">
            <el-input v-model="form.username" placeholder="请输入学生姓名" />
          </el-form-item>
          <el-form-item label="学号" prop="studentNo">
            <el-input v-model="form.studentNo" placeholder="请输入学号" :disabled="form.studentId !== undefined" />
          </el-form-item>
          <el-form-item label="专业" prop="major">
            <el-input v-model="form.major" placeholder="请输入专业" />
          </el-form-item>
          <el-form-item label="入学年份" prop="admissionDate">
            <el-date-picker 
              v-model="form.admissionDate" 
              type="date" 
              placeholder="选择入学年份"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号码" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="账号信息" v-if="!form.studentId">
            <el-alert
              title="系统将使用学号作为登录账号，默认密码为123456"
              type="info"
              :closable="false"
              show-icon
            />
          </el-form-item>
          <el-form-item label="头像" prop="avatar">
            <image-uploader
              v-model="form.avatar"
              :max-size="2"
              :tip="'支持jpg、png格式，大小不超过2MB'"
              @success="handleUploadSuccess"
            />
          </el-form-item>
<!--          <el-form-item label="地址" prop="address">-->
<!--            <el-input v-model="form.address" placeholder="请输入地址信息" />-->
<!--          </el-form-item>-->
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.studentStatus" placeholder="请选择学生状态" style="width: 100%">
              <el-option label="在读" value="active" />
              <el-option label="休学" value="leave" />
              <el-option label="毕业" value="graduated" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="showEditModal = false">取消</el-button>
            <el-button v-permission="form.studentId ? 'edu:student:edit' : 'edu:student:add'" type="primary" @click="submitForm">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, View, Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue';
import SearchForm from '@/components/SearchForm/index.vue';
import DataTable from '@/components/DataTable/index.vue';
import ImageUploader from '@/components/ImageUploader.vue';
import { getStudentList, getStudentById, addStudent, updateStudent, deleteStudent, getStudentCourses } from '@/api/edu/student';
import { uploadImage } from '@/api/common/upload';

// 加载状态
const loading = ref(false);
// 学生列表
const studentList = ref([]);
// 搜索参数
const searchQuery = reactive({
  studentName: '',
  studentNo: '',
  status: ''
});
// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
// 详情对话框
const showDetailModal = ref(false);
const studentDetail = ref(null);
// 编辑对话框
const showEditModal = ref(false);
const formTitle = ref('');
const formRef = ref(null);
// 课程列表相关
const showCourses = ref(false);
const loadingCourses = ref(false);
const courseList = ref([]);

// 表单数据
const form = reactive({
  studentId: undefined,
  username: '',
  studentNo: '',
  major: '',
  admissionDate: '',
  phone: '',
  email: '',
  avatar: '',
  address: '',
  studentStatus: 'active'
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入学生姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: '学号只能包含字母和数字', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  admissionYear: [
    { required: true, message: '请选择入学年份', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  studentStatus: [
    { required: true, message: '请选择学生状态', trigger: 'change' }
  ]
};

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
 * 获取状态文本
 */
const getStatusText = (status) => {
  switch (status) {
    case 'active': return '在读';
    case 'leave': return '休学';
    case 'graduated': return '毕业';
    default: return '未知';
  }
};

/**
 * 获取状态类型
 */
const getStatusType = (status) => {
  switch (status) {
    case 'active': return 'success';
    case 'leave': return 'warning';
    case 'graduated': return 'info';
    default: return '';
  }
};

/**
 * 获取学生列表
 */
const fetchStudentList = async () => {
  try {
    loading.value = true;
    
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      studentName: searchQuery.studentName,
      studentNo: searchQuery.studentNo,
      status: searchQuery.status
    };
    
    const res = await getStudentList(params);
    studentList.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (error) {
    console.error('获取学生列表失败', error);
    ElMessage.error('获取学生列表失败: ' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

/**
 * 搜索处理
 */
const handleSearch = () => {
  currentPage.value = 1;
  fetchStudentList();
};

/**
 * 重置搜索
 */
const resetSearch = () => {
  searchQuery.studentName = '';
  searchQuery.studentNo = '';
  searchQuery.status = '';
  currentPage.value = 1;
  fetchStudentList();
};

/**
 * 分页查询
 */
const handlePagination = (params) => {
  currentPage.value = params.page;
  pageSize.value = params.limit;
  fetchStudentList();
};

/**
 * 查看学生详情
 */
const viewStudentDetail = async (row) => {
  try {
    const res = await getStudentById(row.studentId);
    studentDetail.value = res.data;
    showDetailModal.value = true;
    
    // 重置课程列表显示状态
    showCourses.value = false;
    courseList.value = [];
  } catch (error) {
    console.error('获取学生详情失败', error);
    ElMessage.error('获取学生详情失败');
  }
};

/**
 * 切换课程列表显示
 */
const toggleCourses = async () => {
  showCourses.value = !showCourses.value;
  
  if (showCourses.value && studentDetail.value && courseList.value.length === 0) {
    await fetchStudentCourses(studentDetail.value.studentId);
  }
};

/**
 * 获取学生选课列表
 */
const fetchStudentCourses = async (studentId) => {
  if (!studentId) return;
  
  try {
    loadingCourses.value = true;
    
    const res = await getStudentCourses(studentId);
    courseList.value = res.data || [];
  } catch (error) {
    console.error('获取学生选课列表失败', error);
    ElMessage.error('获取学生选课列表失败: ' + (error.message || '未知错误'));
  } finally {
    loadingCourses.value = false;
  }
};

/**
 * 新增学生
 */
const handleAdd = () => {
  resetForm();
  formTitle.value = '新增学生';
  showEditModal.value = true;
};

/**
 * 编辑学生
 */
const handleEdit = async (row) => {
  try {
    resetForm();
    formTitle.value = '编辑学生';
    
    // 获取完整的学生信息
    const res = await getStudentById(row.studentId);
    Object.assign(form, res.data);
    
    showEditModal.value = true;
  } catch (error) {
    console.error('获取学生详情失败', error);
    ElMessage.error('获取学生详情失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 删除学生
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学生 "${row.studentName}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteStudent(row.studentId);
      ElMessage.success('删除成功');
      fetchStudentList();
    } catch (error) {
      console.error('删除学生失败', error);
      ElMessage.error('删除学生失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {});
};

/**
 * 重置表单
 */
const resetForm = () => {
  form.studentId = undefined;
  form.username = '';
  form.studentNo = '';
  form.major = '';
  form.admissionDate = '';
  form.phone = '';
  form.email = '';
  form.avatar = '';
  form.address = '';
  form.studentStatus = 'active';
};

/**
 * 提交表单
 */
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const isEdit = form.studentId !== undefined;
        
        if (isEdit) {
          await updateStudent(form);
        } else {
          await addStudent(form);
        }
        
        ElMessage.success(isEdit ? '修改成功' : '新增成功');
        showEditModal.value = false;
        fetchStudentList();
      } catch (error) {
        console.error('保存学生失败', error);
        ElMessage.error('保存学生失败: ' + (error.message || '未知错误'));
      }
    }
  });
};

/**
 * 头像上传成功
 */
const handleUploadSuccess = (result) => {
  form.avatar = result.fileUrl || result.response?.fileUrl || '';
  ElMessage.success('上传成功');
};

// 页面初始化
onMounted(() => {
  fetchStudentList();
});
</script>

<style scoped>
.student-container {
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

.student-info-cell {
  display: flex;
  align-items: center;
  gap: 15px;
}

.student-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.student-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}

.student-code, .student-class {
  font-size: 12px;
  color: #666;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.student-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-header {
  display: flex;
  gap: 20px;
}

.detail-info {
  flex: 1;
}

.detail-info h2 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #303133;
}

.detail-code, .detail-class, .detail-department, .detail-admission, .detail-status {
  margin: 8px 0;
  color: #606266;
}

.detail-contact {
  margin-top: 10px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.detail-contact h3, .detail-courses h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #303133;
}

.detail-contact p {
  margin: 8px 0;
  color: #606266;
  line-height: 1.6;
}

.detail-courses {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
  margin-top: 10px;
}

.no-data {
  padding: 20px 0;
}
</style>
