<template>
  <div class="courseresource-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程资源</span>
          <div class="header-actions">
            <el-button v-permission="'edu:resource:add'" type="primary" @click="handleAdd">
              <el-icon><upload /></el-icon>上传资源
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="searchQuery" @search="handleSearch" @reset="resetSearch">
        <el-form-item label="课程名称">
          <el-select v-model="searchQuery.courseId" placeholder="请选择课程" clearable filterable>
            <el-option 
              v-for="item in courseList" 
              :key="item.courseId" 
              :label="item.courseName" 
              :value="item.courseId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资源名称">
          <el-input v-model="searchQuery.resourceName" placeholder="请输入资源名称" clearable />
        </el-form-item>
        <el-form-item label="资源类型">
          <el-select v-model="searchQuery.typeId" placeholder="资源类型" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option 
              v-for="item in typeList" 
              :key="item.typeId" 
              :label="item.typeName" 
              :value="item.typeId"
            ></el-option>
          </el-select>
        </el-form-item>
      </search-form>

      <!-- 课程资源展示区域 -->
      <div v-if="selectedCourse" class="course-info">
        <div class="course-header">
          <el-avatar :size="60" :src="selectedCourse.cover">
            {{ selectedCourse.courseName ? selectedCourse.courseName.substring(0, 1) : 'C' }}
          </el-avatar>
          <div class="course-details">
            <h2 class="course-name">{{ selectedCourse.courseName }}</h2>
            <p class="course-teacher">教师：{{ selectedCourse.teacherName }}</p>
            <p class="course-desc">{{ selectedCourse.description || '暂无描述' }}</p>
          </div>
        </div>
      </div>

      <!-- 表格区域 -->
      <data-table
        :data="resourceList"
        :loading="loading"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @pagination="handlePagination"
        @update:current-page="val => currentPage = val"
        @update:page-size="val => pageSize = val"
        border
      >
        <el-table-column label="资源名称" min-width="200">
          <template #default="{ row }">
            <div class="resource-info">
              <el-icon class="file-icon" :class="getFileIconClass(row.fileType)"><document /></el-icon>
              <div class="resource-detail">
                <div class="resource-name">{{ row.resourceName }}</div>
                <div class="resource-meta">
                  <span class="resource-size">{{ formatFileSize(row.fileSize) }}</span>
                  <span class="resource-date">{{ formatDateTime(row.createTime) }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="资源类型" width="120"></el-table-column>
        <el-table-column prop="courseName" label="所属课程" min-width="150"></el-table-column>
        <el-table-column label="资源描述" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.description || '暂无描述' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdBy" label="上传者" width="120"></el-table-column>
        <el-table-column label="下载次数" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info">{{ row.downloadCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleDownload(row)">
              <el-icon><download /></el-icon>下载
            </el-button>
            <el-button 
              v-if="canManageResource(row)" 
              v-permission="'edu:resource:edit'" 
              type="warning" 
              size="small" 
              text 
              @click="handleEdit(row)"
            >
              <el-icon><edit /></el-icon>编辑
            </el-button>
            <el-button 
              v-if="canManageResource(row)" 
              v-permission="'edu:resource:remove'" 
              type="danger" 
              size="small" 
              text 
              @click="handleDelete(row)"
            >
              <el-icon><delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>

      <!-- 资源上传/编辑对话框 -->
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
          <el-form-item label="所属课程" prop="courseId">
            <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%" filterable>
              <el-option 
                v-for="item in courseList" 
                :key="item.courseId" 
                :label="item.courseName" 
                :value="item.courseId"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="资源名称" prop="resourceName">
            <el-input v-model="form.resourceName" placeholder="请输入资源名称" />
          </el-form-item>
          <el-form-item label="资源类型" prop="typeId">
            <el-select v-model="form.typeId" placeholder="请选择资源类型" style="width: 100%">
              <el-option 
                v-for="item in typeList" 
                :key="item.typeId" 
                :label="item.typeName" 
                :value="item.typeId"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="资源描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入资源描述" :rows="3" />
          </el-form-item>
          <el-form-item v-if="!form.resourceId" label="资源文件" prop="file">
            <el-upload
              class="resource-upload"
              :action="''"
              :auto-upload="false"
              :limit="1"
              :on-change="handleFileChange"
              :on-remove="handleFileRemove"
              :file-list="fileList"
            >
              <el-button type="primary">
                <el-icon><plus /></el-icon>选择文件
              </el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持任意类型文件，单个文件不超过50MB
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="showEditModal = false">取消</el-button>
            <el-button v-permission="form.resourceId ? 'edu:resource:edit' : 'edu:resource:add'" type="primary" @click="submitForm">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Document, Upload, Download, Edit, Delete, Plus } from '@element-plus/icons-vue';
import SearchForm from '@/components/SearchForm/index.vue';
import DataTable from '@/components/DataTable/index.vue';
import { 
  getCourseResourceList, 
  getResourceById, 
  addResource, 
  updateResource, 
  deleteResource, 
  uploadResourceFile, 
  downloadResourceFile,
  getResourceTypeList
} from '@/api/edu/resource';
import { getCourseList } from '@/api/edu/course';
import { useUserStore } from '@/stores/user';

// 用户信息
const userStore = useUserStore();
const currentUser = computed(() => userStore.user);

// 加载状态
const loading = ref(false);
// 资源列表
const resourceList = ref([]);
// 资源类型列表
const typeList = ref([]);
// 课程列表
const courseList = ref([]);
// 搜索参数
const searchQuery = reactive({
  courseId: '',
  resourceName: '',
  typeId: ''
});
// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
// 编辑对话框
const showEditModal = ref(false);
const formTitle = ref('');
const formRef = ref(null);
// 文件上传
const fileList = ref([]);

// 当前选中的课程
const selectedCourse = computed(() => {
  if (!searchQuery.courseId) return null;
  return courseList.value.find(course => course.courseId === searchQuery.courseId);
});

// 表单数据
const form = reactive({
  resourceId: undefined,
  courseId: '',
  resourceName: '',
  typeId: '',
  description: '',
  file: null
});

// 表单验证规则
const rules = {
  courseId: [
    { required: true, message: '请选择所属课程', trigger: 'change' }
  ],
  resourceName: [
    { required: true, message: '请输入资源名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  typeId: [
    { required: true, message: '请选择资源类型', trigger: 'change' }
  ],
  file: [
    { required: true, message: '请上传资源文件', trigger: 'change' }
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
 * 格式化文件大小
 */
const formatFileSize = (size) => {
  if (!size) return '0 B';
  
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let unitIndex = 0;
  let fileSize = size;
  
  while (fileSize >= 1024 && unitIndex < units.length - 1) {
    fileSize /= 1024;
    unitIndex++;
  }
  
  return `${fileSize.toFixed(2)} ${units[unitIndex]}`;
};

/**
 * 根据文件类型获取图标类名
 */
const getFileIconClass = (fileType) => {
  if (!fileType) return 'file-default';
  
  const fileTypeMap = {
    'pdf': 'file-pdf',
    'doc': 'file-word',
    'docx': 'file-word',
    'xls': 'file-excel',
    'xlsx': 'file-excel',
    'ppt': 'file-ppt',
    'pptx': 'file-ppt',
    'jpg': 'file-image',
    'jpeg': 'file-image',
    'png': 'file-image',
    'gif': 'file-image',
    'zip': 'file-archive',
    'rar': 'file-archive',
    'mp4': 'file-video',
    'avi': 'file-video',
    'mp3': 'file-audio',
    'wav': 'file-audio'
  };
  
  return fileTypeMap[fileType.toLowerCase()] || 'file-default';
};

/**
 * 获取课程资源列表
 */
const fetchCourseResourceList = async () => {
  try {
    loading.value = true;
    
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      courseId: searchQuery.courseId,
      resourceName: searchQuery.resourceName,
      typeId: searchQuery.typeId
    };
    
    const res = await getCourseResourceList(params);
    resourceList.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (error) {
    console.error('获取课程资源列表失败', error);
    ElMessage.error('获取课程资源列表失败: ' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

/**
 * 获取资源类型列表
 */
const fetchResourceTypeList = async () => {
  try {
    const res = await getResourceTypeList({ status: '1' });
    typeList.value = res.data?.records || [];
  } catch (error) {
    console.error('获取资源类型列表失败', error);
    ElMessage.error('获取资源类型列表失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 获取课程列表
 */
const fetchCourseList = async () => {
  try {
    const res = await getCourseList();
    courseList.value = res.data?.records || [];
  } catch (error) {
    console.error('获取课程列表失败', error);
    ElMessage.error('获取课程列表失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 判断当前用户是否可以管理该资源
 */
const canManageResource = (resource) => {
  if (!resource || !currentUser.value) return false;
  
  // 系统管理员或资源上传者可以管理资源
  return currentUser.value.roles.includes('admin') || 
         resource.createdBy === currentUser.value.username;
};

/**
 * 搜索处理
 */
const handleSearch = () => {
  currentPage.value = 1;
  fetchCourseResourceList();
};

/**
 * 重置搜索
 */
const resetSearch = () => {
  searchQuery.courseId = '';
  searchQuery.resourceName = '';
  searchQuery.typeId = '';
  currentPage.value = 1;
  fetchCourseResourceList();
};

/**
 * 分页查询
 */
const handlePagination = (params) => {
  currentPage.value = params.page;
  pageSize.value = params.limit;
  fetchCourseResourceList();
};

/**
 * 新增资源
 */
const handleAdd = () => {
  resetForm();
  formTitle.value = '上传课程资源';
  
  // 预设当前选中的课程
  if (searchQuery.courseId) {
    form.courseId = searchQuery.courseId;
  }
  
  showEditModal.value = true;
};

/**
 * 编辑资源
 */
const handleEdit = async (row) => {
  try {
    resetForm();
    formTitle.value = '编辑课程资源';
    
    // 获取完整的资源信息
    const res = await getResourceById(row.resourceId);
    Object.assign(form, res.data);
    
    showEditModal.value = true;
  } catch (error) {
    console.error('获取资源详情失败', error);
    ElMessage.error('获取资源详情失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 删除资源
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除资源 "${row.resourceName}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteResource(row.resourceId);
      ElMessage.success('删除成功');
      fetchCourseResourceList();
    } catch (error) {
      console.error('删除资源失败', error);
      ElMessage.error('删除资源失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {});
};

/**
 * 下载资源
 */
const handleDownload = async (row) => {
  try {
    ElMessage.info('正在准备下载，请稍候...');
    
    const res = await downloadResourceFile(row.resourceId);
    
    // 创建下载链接
    const blob = new Blob([res]);
    const downloadElement = document.createElement('a');
    const href = window.URL.createObjectURL(blob);
    downloadElement.href = href;
    downloadElement.download = row.resourceName;
    document.body.appendChild(downloadElement);
    downloadElement.click();
    document.body.removeChild(downloadElement);
    window.URL.revokeObjectURL(href);
    
    ElMessage.success('下载成功');
    
    // 刷新资源列表，更新下载次数
    fetchCourseResourceList();
  } catch (error) {
    console.error('下载资源失败', error);
    ElMessage.error('下载资源失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 文件选择变更事件
 */
const handleFileChange = (file) => {
  if (file) {
    form.file = file.raw;
    
    // 自动设置资源名称为文件名(不含扩展名)
    if (!form.resourceName && file.name) {
      const lastDotIndex = file.name.lastIndexOf('.');
      form.resourceName = lastDotIndex > 0 ? file.name.substring(0, lastDotIndex) : file.name;
    }
  }
};

/**
 * 文件移除事件
 */
const handleFileRemove = () => {
  form.file = null;
  fileList.value = [];
};

/**
 * 重置表单
 */
const resetForm = () => {
  form.resourceId = undefined;
  form.courseId = '';
  form.resourceName = '';
  form.typeId = '';
  form.description = '';
  form.file = null;
  fileList.value = [];
};

/**
 * 提交表单
 */
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const isEdit = form.resourceId !== undefined;
        
        if (isEdit) {
          await updateResource(form);
          ElMessage.success('修改成功');
        } else {
          // 上传文件
          if (!form.file) {
            ElMessage.warning('请选择上传文件');
            return;
          }
          
          const formData = new FormData();
          formData.append('file', form.file);
          formData.append('resourceName', form.resourceName);
          formData.append('typeId', form.typeId);
          formData.append('courseId', form.courseId);
          formData.append('description', form.description || '');
          
          await uploadResourceFile(formData);
          ElMessage.success('上传成功');
        }
        
        showEditModal.value = false;
        fetchCourseResourceList();
      } catch (error) {
        console.error('保存资源失败', error);
        ElMessage.error('保存资源失败: ' + (error.message || '未知错误'));
      }
    }
  });
};

// 页面初始化
onMounted(() => {
  fetchResourceTypeList();
  fetchCourseList();
  fetchCourseResourceList();
});
</script>

<style scoped>
.courseresource-container {
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

.course-info {
  margin-bottom: 20px;
  background-color: #f8f9fa;
  border-radius: 6px;
  padding: 20px;
}

.course-header {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.course-details {
  flex: 1;
}

.course-name {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}

.course-teacher {
  margin: 5px 0;
  color: #606266;
}

.course-desc {
  margin: 10px 0 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.resource-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-icon {
  font-size: 24px;
  color: #409EFF;
}

.file-pdf {
  color: #F56C6C;
}

.file-word {
  color: #409EFF;
}

.file-excel {
  color: #67C23A;
}

.file-ppt {
  color: #E6A23C;
}

.file-image {
  color: #909399;
}

.file-archive {
  color: #6B7280;
}

.file-video {
  color: #8957e5;
}

.file-audio {
  color: #8957e5;
}

.resource-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.resource-name {
  font-weight: 500;
  font-size: 14px;
  color: #333;
}

.resource-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #666;
}

.resource-upload {
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
