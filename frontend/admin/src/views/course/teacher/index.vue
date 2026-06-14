<template>
  <div class="teacher-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>教师管理</span>
          <div class="header-actions">
            <el-button type="primary" plain v-permission="'edu:teacher:add'" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增教师
            </el-button>
            <el-button type="warning" plain @click="fetchTeacherList">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="searchQuery" @search="handleSearch" @reset="resetSearch">
        <el-form-item label="教师姓名">
          <el-input v-model="searchQuery.teacherName" placeholder="请输入教师姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchQuery.teacherStatus" placeholder="教师状态" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="在职" value="active"></el-option>
            <el-option label="离职" value="inactive"></el-option>
          </el-select>
        </el-form-item>
      </search-form>

      <!-- 表格区域 -->
      <data-table
        :data="teacherList"
        :loading="loading"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @pagination="handlePagination"
        @update:current-page="val => currentPage = val"
        @update:page-size="val => pageSize = val"
        border
      >
        <el-table-column label="教师信息" min-width="250">
          <template #default="{ row }">
            <div class="teacher-info-cell">
              <el-avatar :size="50" :src="row.avatar">{{ row.teacherName ? row.teacherName.substring(0, 1) : 'T' }}</el-avatar>
              <div class="teacher-details">
                <div class="teacher-name">{{ row.teacherName }}</div>
                <div class="teacher-code">教师工号: {{ row.teacherCode }}</div>
                <div class="teacher-title">{{ row.title || '普通教师' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="所属院系" width="150"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="150"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.teacherStatus === 'active' ? 'success' : 'info'">
              {{ row.teacherStatus === 'active' ? '在职' : '离职' }}
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
            <el-button type="primary" size="small" text @click="viewTeacherDetail(row)">
              <el-icon><View /></el-icon>查看
            </el-button>
            <el-button v-permission="'edu:teacher:edit'" type="warning" size="small" text @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button v-permission="'edu:teacher:remove'" type="danger" size="small" text @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>

      <!-- 教师详情对话框 -->
      <el-dialog
        v-model="showDetailModal"
        title="教师详情"
        width="700px"
        append-to-body
      >
        <div v-if="teacherDetail" class="teacher-detail">
          <div class="detail-header">
            <el-avatar :size="100" :src="teacherDetail.avatar">{{ teacherDetail.teacherName ? teacherDetail.teacherName.substring(0, 1) : 'T' }}</el-avatar>
            <div class="detail-info">
              <h2>{{ teacherDetail.teacherName }}</h2>
              <p class="detail-code">教师工号：{{ teacherDetail.teacherCode }}</p>
              <p class="detail-title">职称：{{ teacherDetail.title || '普通教师' }}</p>
              <p class="detail-department">所属院系：{{ teacherDetail.department }}</p>
              <p class="detail-status">
                状态：
                <el-tag :type="teacherDetail.teacherStatus === 'active' ? 'success' : 'info'">
                  {{ teacherDetail.teacherStatus === 'active' ? '在职' : '离职' }}
                </el-tag>
              </p>
            </div>
          </div>
          <div class="detail-contact">
            <h3>联系方式</h3>
            <p><strong>手机：</strong>{{ teacherDetail.phone }}</p>
            <p><strong>邮箱：</strong>{{ teacherDetail.email }}</p>
            <p v-if="teacherDetail.address"><strong>地址：</strong>{{ teacherDetail.address }}</p>
          </div>
          <div class="detail-bio" v-if="teacherDetail.bio">
            <h3>个人简介</h3>
            <p>{{ teacherDetail.bio }}</p>
          </div>
          <div class="detail-courses" v-if="showCourses && teacherDetail">
            <h3>授课列表</h3>
            <el-table
              :data="courseList"
              v-loading="loadingCourses"
              border
              stripe
              style="width: 100%"
            >
              <el-table-column prop="courseName" label="课程名称" width="200" />
              <el-table-column prop="courseCode" label="课程代码" width="120" />
              <el-table-column prop="subjectName" label="所属分类" width="120" />
              <el-table-column prop="studentCount" label="学生数" width="100" align="center" />
              <el-table-column label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'published' ? 'success' : 'info'">
                    {{ row.status === 'published' ? '正常' : '草稿' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="courseList.length === 0 && !loadingCourses" class="no-data">
              <el-empty description="暂无授课记录" />
            </div>
          </div>
<!--          <div class="detail-actions">-->
<!--            <el-button v-permission="'edu:teacher:query'" type="info" @click="toggleCourses">-->
<!--              {{ showCourses ? '隐藏授课列表' : '查看授课列表' }}-->
<!--            </el-button>-->
<!--          </div>-->
        </div>
      </el-dialog>

      <!-- 教师编辑对话框 -->
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
          <el-form-item label="教师姓名" prop="username">
            <el-input v-model="form.username" placeholder="请输入教师姓名" />
          </el-form-item>
          <el-form-item label="教师工号" prop="teacherCode">
            <el-input v-model="form.teacherCode" placeholder="请输入教师工号" :disabled="form.teacherId !== undefined" />
          </el-form-item>
          <el-form-item label="所属院系" prop="department">
            <el-input v-model="form.department" placeholder="请输入所属院系" />
          </el-form-item>
          <el-form-item label="职称" prop="title">
            <el-select v-model="form.title" placeholder="请选择职称" style="width: 100%">
              <el-option label="助教" value="助教" />
              <el-option label="讲师" value="讲师" />
              <el-option label="副教授" value="副教授" />
              <el-option label="教授" value="教授" />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号码" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="账号信息" v-if="!form.teacherId">
            <el-alert
              title="系统将使用教师工号作为登录账号，默认密码为123456"
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
<!--          <el-form-item label="个人简介" prop="bio">-->
<!--            <el-input-->
<!--              v-model="form.bio"-->
<!--              type="textarea"-->
<!--              rows="3"-->
<!--              placeholder="请输入个人简介"-->
<!--            />-->
<!--          </el-form-item>-->
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio :label="'active'">在职</el-radio>
              <el-radio :label="'inactive'">离职</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="showEditModal = false">取消</el-button>
            <el-button v-permission="form.teacherId ? 'edu:teacher:edit' : 'edu:teacher:add'" type="primary" @click="submitForm">确定</el-button>
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
import { getTeacherList, getTeacherById, addTeacher, updateTeacher, deleteTeacher, getTeacherCourses } from '@/api/edu/teacher';
import { uploadImage } from '@/api/common/upload';

// 加载状态
const loading = ref(false);
// 教师列表
const teacherList = ref([]);
// 搜索参数
const searchQuery = reactive({
  teacherName: '',
  teacherStatus: ''
});
// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
// 详情对话框
const showDetailModal = ref(false);
const teacherDetail = ref(null);
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
  teacherId: undefined,
  username: '',
  teacherCode: '',
  department: '',
  title: '',
  phone: '',
  email: '',
  avatar: '',
  address: '',
  bio: '',
  status: 'active'
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入教师姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  teacherCode: [
    { required: true, message: '请输入教师工号', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]+$/, message: '工号只能包含字母和数字', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入所属院系', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择教师状态', trigger: 'change' }
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
 * 获取教师列表
 */
const fetchTeacherList = async () => {
  try {
    loading.value = true;
    
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      teacherName: searchQuery.teacherName,
      status: searchQuery.status
    };
    
    const res = await getTeacherList(params);
    teacherList.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (error) {
    console.error('获取教师列表失败', error);
    ElMessage.error('获取教师列表失败: ' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

/**
 * 搜索处理
 */
const handleSearch = () => {
  currentPage.value = 1;
  fetchTeacherList();
};

/**
 * 重置搜索
 */
const resetSearch = () => {
  searchQuery.teacherName = '';
  searchQuery.status = '';
  currentPage.value = 1;
  fetchTeacherList();
};

/**
 * 分页查询
 */
const handlePagination = (params) => {
  currentPage.value = params.page;
  pageSize.value = params.limit;
  fetchTeacherList();
};

/**
 * 查看教师详情
 */
const viewTeacherDetail = async (row) => {
  try {
    const res = await getTeacherById(row.teacherId);
    teacherDetail.value = res.data;
    showDetailModal.value = true;
    
    // 重置课程列表显示状态
    showCourses.value = false;
    courseList.value = [];
  } catch (error) {
    console.error('获取教师详情失败', error);
    ElMessage.error('获取教师详情失败');
  }
};

/**
 * 切换课程列表显示
 */
const toggleCourses = async () => {
  showCourses.value = !showCourses.value;
  
  if (showCourses.value && teacherDetail.value && courseList.value.length === 0) {
    await fetchTeacherCourses(teacherDetail.value.teacherId);
  }
};

/**
 * 获取教师课程列表
 */
const fetchTeacherCourses = async (teacherId) => {
  if (!teacherId) return;
  
  try {
    loadingCourses.value = true;
    
    const res = await getTeacherCourses(teacherId);
    courseList.value = res.data || [];
  } catch (error) {
    console.error('获取教师课程列表失败', error);
    ElMessage.error('获取教师课程列表失败: ' + (error.message || '未知错误'));
  } finally {
    loadingCourses.value = false;
  }
};

/**
 * 新增教师
 */
const handleAdd = () => {
  resetForm();
  formTitle.value = '新增教师';
  showEditModal.value = true;
};

/**
 * 编辑教师
 */
const handleEdit = async (row) => {
  try {
    resetForm();
    formTitle.value = '编辑教师';
    
    // 获取完整的教师信息
    const res = await getTeacherById(row.teacherId);
    Object.assign(form, res.data);
    
    showEditModal.value = true;
  } catch (error) {
    console.error('获取教师详情失败', error);
    ElMessage.error('获取教师详情失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 删除教师
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除教师 "${row.teacherName}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTeacher(row.teacherId);
      ElMessage.success('删除成功');
      fetchTeacherList();
    } catch (error) {
      console.error('删除教师失败', error);
      ElMessage.error('删除教师失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {});
};

/**
 * 重置表单
 */
const resetForm = () => {
  form.teacherId = undefined;
  form.username = '';
  form.teacherCode = '';
  form.department = '';
  form.title = '';
  form.phone = '';
  form.email = '';
  form.avatar = '';
  form.address = '';
  form.bio = '';
  form.status = 'active';
};

/**
 * 提交表单
 */
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const isEdit = form.teacherId !== undefined;
        
        if (isEdit) {
          await updateTeacher(form);
        } else {
          await addTeacher(form);
        }
        
        ElMessage.success(isEdit ? '修改成功' : '新增成功');
        showEditModal.value = false;
        fetchTeacherList();
      } catch (error) {
        console.error('保存教师失败', error);
        ElMessage.error('保存教师失败: ' + (error.message || '未知错误'));
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
  fetchTeacherList();
});
</script>

<style scoped>
.teacher-container {
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

.teacher-info-cell {
  display: flex;
  align-items: center;
  gap: 15px;
}

.teacher-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.teacher-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}

.teacher-code, .teacher-title {
  font-size: 12px;
  color: #666;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.teacher-detail {
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

.detail-code, .detail-title, .detail-department, .detail-status {
  margin: 8px 0;
  color: #606266;
}

.detail-contact, .detail-bio {
  margin-top: 10px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.detail-contact h3, .detail-bio h3, .detail-courses h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #303133;
}

.detail-contact p, .detail-bio p {
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
