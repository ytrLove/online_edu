<template>
  <div class="myresource-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的资源</span>
          <!-- <div class="header-actions">
            <el-button v-permission="'edu:resource:add'" type="primary" @click="handleAdd">
              <el-icon><upload /></el-icon>上传资源
            </el-button>
          </div> -->
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
      </search-form>

      <!-- 统计卡片 -->
      <div class="resource-stats">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon><files /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ totalCount }}</div>
              <div class="stats-label">资源总数</div>
            </div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon downloads">
              <el-icon><download /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ totalDownloads }}</div>
              <div class="stats-label">总下载次数</div>
            </div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon storage">
              <el-icon><folder /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ formatFileSize(totalSize) }}</div>
              <div class="stats-label">总存储空间</div>
            </div>
          </div>
        </el-card>
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
                <div class="resource-size">{{ formatFileSize(row.fileSize) }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="typeName" label="资源类型" width="120"></el-table-column>
        <el-table-column label="资源描述" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.description || '暂无描述' }}
          </template>
        </el-table-column>
        <el-table-column label="下载次数" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info">{{ row.downloadCount }}</el-tag>
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
          <el-form-item label="资源状态" prop="isPublic">
            <el-radio-group v-model="form.isPublic">
              <el-radio :label="1">公开</el-radio>
              <el-radio :label="0">私有</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="!form.resourceId" label="资源文件" required>
            <FileUploader
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
        v-model="showDownloadModal"
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
              </div>
              <div class="download-description">
                {{ currentResource.description || '暂无描述' }}
              </div>
            </div>
          </div>
          
          <div class="download-notice">
            <el-alert
              title="要选择下载位置，请点击'另存为'按钮"
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
              @click="startDirectDownload" 
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
                <el-icon><document /></el-icon> 另存为
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
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Document, Upload, Download, Edit, Delete, Plus, Files, Folder } from '@element-plus/icons-vue';
import SearchForm from '@/components/SearchForm/index.vue';
import DataTable from '@/components/DataTable/index.vue';
import FileUploader from '@/components/FileUploader.vue';
import { 
  getMyResourceList, 
  getResourceById, 
  addResource, 
  updateResource, 
  deleteResource, 
  uploadResourceFile, 
  downloadResourceFile,
  getResourceTypeList
} from '@/api/edu/resource';

// 加载状态
const loading = ref(false);
// 资源列表
const resourceList = ref([]);
// 资源类型列表
const typeList = ref([]);
// 搜索参数
const searchQuery = reactive({
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
const fileUploaded = ref(false);
// 统计数据
const totalCount = ref(0);
const totalDownloads = ref(0);
const totalSize = ref(0);

// 表单数据
const form = reactive({
  resourceId: undefined,
  resourceName: '',
  typeId: '',
  description: '',
  fileUrl: null,
  fileSize: 0,
  fileType: '',
  status: 1,
  isPublic: 1
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
  typeId: [
    { required: true, message: '请选择资源类型', trigger: 'change' }
  ]
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
 * 获取我的资源列表
 */
const fetchMyResourceList = async () => {
  try {
    loading.value = true;
    
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      resourceName: searchQuery.resourceName,
      resourceTypeId: searchQuery.resourceTypeId
    };
    
    const res = await getMyResourceList(params);
    resourceList.value = res.data?.records || [];
    total.value = res.data?.total || 0;
    
    // 更新统计数据
    if (res.data) {
      totalCount.value = res.data.total || 0;
      totalDownloads.value = res.data.records.reduce((sum, item) => sum += (item.downloadCount || 0), 0);
      
      // 计算所有资源的总内存量（文件大小总和）
      totalSize.value = res.data.records.reduce((sum, item) => sum += (item.fileSize || 0), 0);
    }
  } catch (error) {
    console.error('获取我的资源列表失败', error);
    ElMessage.error('获取我的资源列表失败: ' + (error.message || '未知错误'));
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
 * 搜索处理
 */
const handleSearch = () => {
  currentPage.value = 1;
  fetchMyResourceList();
};

/**
 * 重置搜索
 */
const resetSearch = () => {
  searchQuery.resourceName = '';
  searchQuery.typeId = '';
  currentPage.value = 1;
  fetchMyResourceList();
};

/**
 * 分页查询
 */
const handlePagination = (params) => {
  currentPage.value = params.page;
  pageSize.value = params.limit;
  fetchMyResourceList();
};

/**
 * 新增资源
 */
const handleAdd = () => {
  resetForm();
  formTitle.value = '上传资源';
  showEditModal.value = true;
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
      fetchMyResourceList();
    } catch (error) {
      console.error('删除资源失败', error);
      ElMessage.error('删除资源失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {});
};

/**
 * 处理下载按钮点击
 */
const handleDownload = async (row) => {
  try {
    // 设置当前资源
    currentResource.value = row;
    // 重置状态
    fileDownloadUrl.value = '';
    downloadProgress.value = 0;
    isDownloading.value = false;
    
    // 显示下载模态框
    showDownloadModal.value = true;
    
  } catch (error) {
    console.error('准备下载资源失败', error);
    ElMessage.error('准备下载失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 开始直接下载
 */
const startDirectDownload = async () => {
  if (!currentResource.value || isDownloading.value) return;
  
  try {
    isDownloading.value = true;
    ElMessage.info('正在准备下载，请稍候...');
    
    // 模拟下载进度
    const progressTimer = setInterval(() => {
      if (downloadProgress.value < 90) {
        downloadProgress.value += 10;
      }
    }, 300);
    
    const res = await downloadResourceFile(currentResource.value.resourceId);
    
    // 创建下载链接
    const blob = new Blob([res]);
    const downloadElement = document.createElement('a');
    const href = window.URL.createObjectURL(blob);
    downloadElement.href = href;
    downloadElement.download = currentResource.value.resourceName;
    document.body.appendChild(downloadElement);
    downloadElement.click();
    document.body.removeChild(downloadElement);
    window.URL.revokeObjectURL(href);
    
    // 完成下载
    downloadProgress.value = 100;
    clearInterval(progressTimer);
    
    setTimeout(() => {
      ElMessage.success('下载成功');
      isDownloading.value = false;
      showDownloadModal.value = false;
      
      // 刷新资源列表，更新下载次数
      fetchMyResourceList();
    }, 500);
    
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
    
    const res = await downloadResourceFile(currentResource.value.resourceId);
    
    // 创建Blob和URL
    const blob = new Blob([res]);
    const url = window.URL.createObjectURL(blob);
    
    // 保存文件URL
    fileDownloadUrl.value = url;
    
    // 完成准备
    isPreparingLink.value = false;
    ElMessage.success('下载链接准备完成，请点击"另存为"按钮');
  } catch (error) {
    console.error('准备下载链接失败', error);
    isPreparingLink.value = false;
    ElMessage.error('准备下载链接失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 处理"另存为"下载
 */
const handleSaveAs = () => {
  // 更新下载计数
  setTimeout(() => {
    fetchMyResourceList();
  }, 1000);
  
  // 关闭对话框
  setTimeout(() => {
    showDownloadModal.value = false;
  }, 500);
};

/**
 * 文件上传成功事件
 */
const handleUploadSuccess = (data) => {
  // 更新文件URL
  form.fileUrl = data.fileUrl || data.response?.fileUrl || '';
  
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
  form.fileUrl = null;
  fileUploaded.value = false;
  ElMessage.error('文件上传失败: ' + (error.error?.message || '未知错误'));
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
  form.status = 1;
  form.isPublic = 1;
  fileList.value = [];
  fileUploaded.value = false;
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
          resourceTypeId: form.resourceTypeId,
          description: form.description || '',
          status: form.status,
          isPublic: form.isPublic
        });
        ElMessage.success('修改成功');
      } else {
        // 新增时，提交所有必要字段
        const formData = {
          resourceName: form.resourceName,
          resourceTypeId: form.resourceTypeId,
          description: form.description || '',
          fileUrl: form.fileUrl,
          fileSize: form.fileSize,
          fileType: form.fileType,
          status: form.status,
          isPublic: form.isPublic
        };
        
        await uploadResourceFile(formData);
        ElMessage.success('上传成功');
      }
      
      showEditModal.value = false;
      fetchMyResourceList();
    } catch (error) {
      console.error('保存资源失败', error);
      ElMessage.error('保存资源失败: ' + (error.message || '未知错误'));
    }
  }
};

// 页面初始化
onMounted(() => {
  fetchResourceTypeList();
  fetchMyResourceList();
});
</script>

<style scoped>
.myresource-container {
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

.resource-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stats-card {
  flex: 1;
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stats-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: rgba(64, 158, 255, 0.1);
  color: #409EFF;
  font-size: 28px;
}

.stats-icon.downloads {
  background-color: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.stats-icon.storage {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.stats-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.stats-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stats-label {
  font-size: 14px;
  color: #606266;
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

.download-dialog-content {
  padding: 20px;
}

.download-resource-info {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.download-detail {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.download-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.download-meta {
  font-size: 14px;
  color: #666;
}

.download-description {
  font-size: 14px;
  color: #999;
}

.download-notice {
  margin-bottom: 20px;
}

.download-progress {
  margin-bottom: 20px;
}

.download-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.download-save-link {
  color: #409EFF;
  text-decoration: none;
}

.upload-success-tip {
  margin-top: 10px;
}

.upload-tip {
  margin-top: 10px;
}
</style>
