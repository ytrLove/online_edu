<template>
  <div class="resource-manage-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>资源管理</span>
          <div class="header-actions">
            <el-button v-permission="'edu:resource:add'" type="primary" @click="handleAdd">
              <el-icon><upload /></el-icon>上传资源
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="searchQuery" @search="handleSearch" @reset="resetSearch">
        <el-form-item label="资源名称">
          <el-input v-model="searchQuery.resourceName" placeholder="请输入资源名称" clearable />
        </el-form-item>
        <el-form-item label="资源类型">
          <el-select v-model="searchQuery.resourceTypeId" placeholder="资源类型" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option 
              v-for="item in typeList" 
              :key="item.typeId" 
              :label="item.typeName" 
              :value="item.typeId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上传者">
          <el-input v-model="searchQuery.teacherId" placeholder="请输入上传者" clearable />
        </el-form-item>
      </search-form>

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
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="资源类型" width="120"></el-table-column>
        <el-table-column label="文件大小" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ formatFileSize(row.fileSize) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="资源描述" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.description || '暂无描述' }}
          </template>
        </el-table-column>
        <!-- <el-table-column label="文件类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ (row.fileType || '').toUpperCase() || '未知' }}</el-tag>
          </template>
        </el-table-column> -->
        <el-table-column label="下载次数" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info">{{ row.downloadCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="所属课程" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.courseName || '不属于任何课程' }}
          </template>
        </el-table-column>
        <el-table-column label="资源状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isPublic === 1 ? 'success' : 'warning'">
              {{ row.isPublic === 1 ? '公开' : '私有' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="uploadByName" label="上传者" width="120">
          <template #default="{ row }">
            {{ row.uploadByName || 'admin' }}
          </template>
        </el-table-column>
        <el-table-column label="上传时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleDownload(row)">
              <el-icon><download /></el-icon>下载
            </el-button>
            <el-button v-permission="'edu:resource:edit'" type="warning" size="small" text @click="handleEdit(row)">
              <el-icon><edit /></el-icon>编辑
            </el-button>
            <el-button v-permission="'edu:resource:remove'" type="danger" size="small" text @click="handleDelete(row)">
              <el-icon><delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>

      <!-- 资源上传/编辑对话框 -->
      <el-dialog
        :model-value="showEditModal"
        @update:model-value="showEditModal = $event"
        :title="formTitle"
        width="650px"
        append-to-body
        destroy-on-close
        @open="handleDialogOpen"
      >
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="资源名称" prop="resourceName">
            <el-input v-model="form.resourceName" placeholder="请输入资源名称" />
          </el-form-item>
          <el-form-item label="资源类型" prop="resourceTypeId">
            <el-select v-model="form.resourceTypeId" placeholder="请选择资源类型" style="width: 100%">
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
          <el-form-item label="所属课程" prop="courseId">
            <el-select v-model="form.courseId" placeholder="请选择所属课程" style="width: 100%" clearable>
              <el-option label="不关联课程" :value="undefined"></el-option>
              <el-option 
                v-for="item in courseList" 
                :key="item.courseId" 
                :label="item.courseName" 
                :value="item.courseId"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="资源状态" prop="isPublic">
            <el-radio-group v-model="form.isPublic">
              <el-radio :label="1">公开</el-radio>
              <el-radio :label="0">私有</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="!form.resourceId" label="资源文件" required>
            <NewFileUpload
              upload-type="file"
              :max-size="50"
              :multiple="false"
              :show-progress="true"
              tip="支持任意类型文件，单个文件不超过50MB"
              button-text="选择文件"
              @success="handleUploadSuccess"
              @error="handleUploadError"
            />
            <div v-if="fileUploaded" class="upload-success-tip">
              <el-tag type="success">文件已上传: {{ form.fileUrl }}</el-tag>
            </div>
            <div v-else-if="!form.resourceId" class="upload-tip">
              <el-tag type="warning">请上传资源文件</el-tag>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="showEditModal = false">取消</el-button>
            <el-button v-permission="form.resourceId ? 'edu:resource:edit' : 'edu:resource:add'" type="primary" @click="submitForm">确定</el-button>
          </div>
        </template>
      </el-dialog>
      
      <!-- 下载模态框 -->
      <el-dialog
        :model-value="showDownloadModal"
        @update:model-value="showDownloadModal = $event"
        title="资源下载"
        width="500px"
        :destroy-on-close="true"
      >
        <div v-if="currentResource" class="download-dialog-content">
          <div class="download-resource-info">
            <el-icon class="file-icon" :class="getFileIconClass(currentResource.fileType)" :size="48"><document /></el-icon>
            <div class="download-detail">
              <h3 class="download-title">{{ currentResource.resourceName }}</h3>
              <div class="download-meta">
                <span>{{ formatFileSize(currentResource.fileSize) }}</span>
                <span>·</span>
                <span>{{ currentResource.typeName }}</span>
                <span v-if="currentResource.fileType">·</span>
                <span v-if="currentResource.fileType">{{ currentResource.fileType.toUpperCase() }}</span>
              </div>
              <div class="download-description">
                {{ currentResource.description || '暂无描述' }}
              </div>
            </div>
          </div>
          
          <div class="download-notice">
            <el-alert
              title="要选择下载位置，请右键点击下方的'保存文件'按钮，选择'另存为'选项"
              type="info"
              :closable="false"
              show-icon
            />
          </div>
          
          <div class="download-progress" v-if="downloadProgress > 0">
            <el-progress :percentage="downloadProgress" :format="progressFormat" />
          </div>
          
          <div class="download-buttons">
            <el-button 
              type="primary" 
              @click="startDownload" 
              :loading="isDownloading"
            >
              <el-icon><download /></el-icon> 直接下载
            </el-button>
            
            <a 
              v-if="fileDownloadUrl" 
              :href="fileDownloadUrl" 
              class="download-save-link" 
              download 
              target="_blank"
              @click="handleSaveAs"
            >
              <el-button>
                <el-icon><document /></el-icon> 保存文件
              </el-button>
            </a>
            
            <el-button 
              v-else
              @click="prepareDownloadLink" 
              :disabled="isDownloading || isPreparingLink"
              :loading="isPreparingLink"
            >
              <el-icon><document /></el-icon> 准备下载链接
            </el-button>
          </div>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Document, Upload, Download, Edit, Delete, Plus } from '@element-plus/icons-vue';
import SearchForm from '@/components/SearchForm/index.vue';
import DataTable from '@/components/DataTable/index.vue';
import FileUploader from '@/components/FileUploader.vue';
import { 
  getResourceList, 
  getResourceById, 
  addResource, 
  updateResource, 
  deleteResource, 
  uploadResourceFile, 
  downloadResourceFile,
  getResourceTypeList
} from '@/api/edu/resource';
import { getCourseList } from '@/api/edu/course';
import NewFileUpload from '@/components/newFileUpload.vue';

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
  resourceName: '',
  resourceTypeId: '',
  teacherId: ''
});
// 分页参数
const currentPage = ref(1);
const pageSize = ref(6);
const total = ref(0);
// 编辑对话框
const showEditModal = ref(false);
const formTitle = ref('');
const formRef = ref(null);
// 文件上传
const fileList = ref([]);
const fileUploaded = ref(false);

// 表单数据
const form = reactive({
  resourceId: undefined,
  resourceName: '',
  resourceTypeId: '',
  description: '',
  fileUrl: null,
  fileSize: 0,
  fileType: '',
  courseId: undefined,
  isPublic: 1, // 默认公开
  status: 1 // 默认启用状态
});

// 用于表单验证的计算属性
const formFile = computed(() => {
  return fileUploaded.value ? { name: form.fileUrl } : null;
});

// 表单验证规则
const rules = {
  resourceName: [
    { required: true, message: '请输入资源名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  resourceTypeId: [
    { required: true, message: '请选择资源类型', trigger: 'change' }
  ],
  isPublic: [
    { required: true, message: '请选择资源状态', trigger: 'change' }
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
 * 获取资源列表
 */
const fetchResourceList = async () => {
  try {
    loading.value = true;
    
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      resourceName: searchQuery.resourceName,
      resourceTypeId: searchQuery.resourceTypeId,
      createdBy: searchQuery.teacherId
    };
    console.log(params);
    
    const res = await getResourceList(params);
    resourceList.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (error) {
    console.error('获取资源列表失败', error);
    ElMessage.error('获取资源列表失败: ' + (error.message || '未知错误'));
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
    const res = await getCourseList({ pageSize: 100 }); // 获取更多课程
    courseList.value = res.data?.records || [];
  } catch (error) {
    console.error('获取课程列表失败', error);
    ElMessage.error('获取课程列表失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 搜索处理
 */
const handleSearch = () => {
  currentPage.value = 1;
  fetchResourceList();
};

/**
 * 重置搜索
 */
const resetSearch = () => {
  searchQuery.resourceName = '';
  searchQuery.resourceTypeId = '';
  searchQuery.teacherId = '';
  currentPage.value = 1;
  fetchResourceList();
};

/**
 * 分页查询
 */
const handlePagination = (params) => {
  currentPage.value = params.page;
  pageSize.value = params.limit;
  fetchResourceList();
};

/**
 * 新增资源
 */
const handleAdd = () => {
  console.log('点击了新增按钮');
  resetForm();
  formTitle.value = '上传资源';
  showEditModal.value = true;
  console.log('对话框状态:', showEditModal.value);
  nextTick(() => {
    console.log('nextTick后对话框状态:', showEditModal.value);
  });
};

/**
 * 编辑资源
 */
const handleEdit = async (row) => {
  try {
    resetForm();
    formTitle.value = '编辑资源';
    
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
      fetchResourceList();
    } catch (error) {
      console.error('删除资源失败', error);
      ElMessage.error('删除资源失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {});
};

// 下载相关
const showDownloadModal = ref(false);
const currentResource = ref(null);
const fileDownloadUrl = ref('');
const isDownloading = ref(false);
const downloadProgress = ref(0);
const isPreparingLink = ref(false);

// 进度条格式化
const progressFormat = (percentage) => {
  return percentage === 100 ? '完成' : `${percentage}%`;
};

/**
 * 下载资源
 */
const handleDownload = async (row) => {
  try {
    // 设置当前资源
    currentResource.value = row;
    // 重置所有状态
    fileDownloadUrl.value = '';
    downloadProgress.value = 0;
    isDownloading.value = false;
    isPreparingLink.value = false;
    
    // 显示下载模态框
    showDownloadModal.value = true;
    
  } catch (error) {
    console.error('准备下载资源失败', error);
    ElMessage.error('准备下载失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 开始下载
 */
const startDownload = async () => {
  if (!currentResource.value || isDownloading.value) return;
  
  try {
    isDownloading.value = true;
    ElMessage.info('正在准备下载，请稍候...');
    
    // 重置下载进度
    downloadProgress.value = 0;
    
    // 模拟下载初始进度
    const progressTimer = setInterval(() => {
      if (downloadProgress.value < 85) {
        downloadProgress.value += 5;
      }
    }, 300);
    
    // 获取文件链接
    const res = await downloadResourceFile(currentResource.value.resourceId);
    
    // 获取文件URL
    const fileUrl = res.data?.fileUrl;
    
    if (!fileUrl) {
      clearInterval(progressTimer);
      downloadProgress.value = 0;
      ElMessage.error('下载失败：未获取到文件链接');
      isDownloading.value = false;
      return;
    }
    
    // 进度更新到90%，表示已获取到链接，准备下载
    downloadProgress.value = 90;
    
    // 保存文件URL
    fileDownloadUrl.value = fileUrl;
    
    // 使用原生fetch API下载文件
    try {
      // 文件下载开始
      const response = await fetch(fileUrl);
      
      // 文件下载中，进度增加到95%
      downloadProgress.value = 95;
      
      const blob = await response.blob();
      
      // 文件已下载，设置进度为100%
      downloadProgress.value = 100;
      
      // 清除进度定时器
      clearInterval(progressTimer);
      
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = currentResource.value.resourceName || '未命名文件';
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      
      setTimeout(() => {
        ElMessage.success('下载成功');
        isDownloading.value = false;
        // 关闭下载模态框
        showDownloadModal.value = false;
      }, 500);
      
      // 刷新资源列表，更新下载次数
      fetchResourceList();
    } catch (fetchError) {
      console.error('文件下载失败', fetchError);
      clearInterval(progressTimer);
      downloadProgress.value = 0;
      isDownloading.value = false;
      ElMessage.error('文件下载失败: ' + (fetchError.message || '未知错误'));
    }
    
  } catch (error) {
    console.error('下载资源失败', error);
    isDownloading.value = false;
    downloadProgress.value = 0;
    ElMessage.error('下载资源失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 准备下载链接
 */
const prepareDownloadLink = async () => {
  if (!currentResource.value || isDownloading.value || isPreparingLink.value) return;
  
  try {
    isPreparingLink.value = true;
    ElMessage.info('正在准备下载链接，请稍候...');
    
    // 重置并开始显示进度
    downloadProgress.value = 0;
    const progressTimer = setInterval(() => {
      if (downloadProgress.value < 85) {
        downloadProgress.value += 10;
      }
    }, 300);
    
    const res = await downloadResourceFile(currentResource.value.resourceId);
    
    // 进度提升到90%，表示链接获取中
    downloadProgress.value = 90;
    
    // 获取文件URL
    const fileUrl = res.data?.fileUrl;
    
    if (!fileUrl) {
      clearInterval(progressTimer);
      downloadProgress.value = 0;
      ElMessage.error('准备下载链接失败：未获取到文件链接');
      isPreparingLink.value = false;
      return;
    }
    
    // 保存文件URL
    fileDownloadUrl.value = fileUrl;
    
    // 完成准备，进度100%
    downloadProgress.value = 100;
    clearInterval(progressTimer);
    
    // 完成准备
    isPreparingLink.value = false;
    ElMessage.success('下载链接准备完成');
  } catch (error) {
    console.error('准备下载链接失败', error);
    downloadProgress.value = 0;
    isPreparingLink.value = false;
    ElMessage.error('准备下载链接失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 文件上传成功事件
 */
const handleUploadSuccess = (data) => {
  // 更新文件URL
  form.fileUrl = data.fileUrl || data.response?.url || '';
  
  // 更新文件信息
  if (data.file) {
    // 设置文件大小(字节)
    form.fileSize = data.file.size || 0;
    
    // 获取文件类型(扩展名)
    const fileName = data.file.name || '';
    const lastDotIndex = fileName.lastIndexOf('.');
    form.fileType = lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : '';
    
    // 自动设置资源名称为文件名(不含扩展名)
    if (!form.resourceName && fileName) {
      form.resourceName = lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }
  }
  
  fileUploaded.value = true;
  ElMessage.success('上传成功');
};

/**
 * 文件上传失败事件
 */
const handleUploadError = (error) => {
  console.error('文件上传失败', error);
  form.file = null;
  fileUploaded.value = false;
  ElMessage.error('文件上传失败: ' + (error.error?.message || '未知错误'));
};

/**
 * 重置表单
 */
const resetForm = () => {
  form.resourceId = undefined;
  form.resourceName = '';
  form.resourceTypeId = '';
  form.description = '';
  form.fileUrl = null;
  form.fileSize = 0;
  form.fileType = '';
  form.courseId = undefined;
  form.isPublic = 1;
  form.status = 1;
  fileList.value = [];
  fileUploaded.value = false;
};

// 提交表单前的验证
const validateForm = async () => {
  if (!formRef.value) return false;
  
  // 先进行表单验证
  let isValid = false;
  await formRef.value.validate((valid) => {
    isValid = valid;
  });
  
  // 检查文件是否已上传(对于新增操作)
  if (isValid && !form.resourceId && (!form.fileUrl || !fileUploaded.value)) {
    ElMessage.warning('请先上传文件');
    return false;
  }
  
  return isValid;
};

/**
 * 提交表单
 */
const submitForm = async () => {
  const isValid = await validateForm();
  
  if (isValid) {
    try {
      const isEdit = form.resourceId !== undefined;
      
      if (isEdit) {
        // 修改时，需要保留资源ID和更新相关字段
        await updateResource({
          resourceId: form.resourceId,
          resourceName: form.resourceName,
          resourceTypeId: form.resourceTypeId, // 注意这里是resourceresourceTypeId，与表单中的resourceTypeId对应
          description: form.description || '',
          isPublic: form.isPublic,
          status: form.status
        });
        ElMessage.success('修改成功');
      } else {
        // 新增时，提交所有必要字段
        const formData = {
          resourceName: form.resourceName,
          resourceTypeId: form.resourceTypeId, // 注意这里是resourceTypeId，与表单中的resourceTypeId对应
          description: form.description || '',
          fileUrl: form.fileUrl,
          fileSize: form.fileSize,
          fileType: form.fileType,
          courseId: form.courseId,
          isPublic: form.isPublic,
          status: form.status
        };
        
        await uploadResourceFile(formData);
        ElMessage.success('上传成功');
      }
      
      showEditModal.value = false;
      fetchResourceList();
    } catch (error) {
      console.error('保存资源失败', error);
      ElMessage.error('保存资源失败: ' + (error.message || '未知错误'));
    }
  }
};

/**
 * 对话框打开事件处理
 */
const handleDialogOpen = () => {
  console.log('对话框已打开');
};

/**
 * 处理"另存为"下载
 */
const handleSaveAs = () => {
  // 更新下载计数
  setTimeout(() => {
    fetchResourceList();
  }, 1000);
};

// 页面初始化
onMounted(() => {
  fetchResourceTypeList();
  fetchCourseList();
  fetchResourceList();
});
</script>

<style scoped>
.resource-manage-container {
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

.resource-size {
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

.upload-success-tip {
  margin-top: 10px;
  margin-left: 10px;
}

.upload-tip {
  margin-top: 10px;
  margin-left: 10px;
}

.download-dialog-content {
  padding: 10px;
}

.download-resource-info {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.download-detail {
  flex: 1;
}

.download-title {
  margin-top: 0;
  margin-bottom: 8px;
  font-size: 18px;
  color: #303133;
}

.download-meta {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
  display: flex;
  gap: 8px;
}

.download-description {
  color: #606266;
  font-size: 14px;
  margin-bottom: 10px;
}

.download-notice {
  margin-bottom: 20px;
}

.download-progress {
  margin: 20px 0;
}

.download-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.download-save-link {
  text-decoration: none;
  color: inherit;
}
</style>
