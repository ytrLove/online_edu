<template>
  <div class="image-uploader">
    <el-upload
      :auto-upload="true"
      :http-request="customUpload"
      :accept="acceptTypes"
      :multiple="multiple"
      :limit="limit"
      :disabled="disabled"
      :show-file-list="showFileList"
      :list-type="listType"
      :drag="drag"
      :before-upload="beforeUpload"
      :on-exceed="handleExceed"
      :on-remove="handleRemove"
      class="uploader"
    >
      <!-- 预览图 -->
      <div v-if="imageUrl && !drag && !showFileList" class="image-preview">
        <img :src="imageUrl" class="image" alt="预览图">
        <div class="image-actions">
          <el-icon class="action-icon" @click.stop="handleRemove"><delete /></el-icon>
        </div>
      </div>
      
      <!-- 上传占位符 -->
      <template v-else-if="!drag">
        <el-icon class="upload-icon"><plus /></el-icon>
        <div class="upload-text">{{ buttonText }}</div>
      </template>
      
      <!-- 拖拽提示 -->
      <template v-if="drag" #tip>
        <div class="el-upload__tip">
          {{ tip || '请上传图片文件，支持jpg、jpeg、png、gif格式' }}
        </div>
      </template>
    </el-upload>
    
    <!-- 上传提示 -->
    <div v-if="!drag && tip" class="el-upload__tip">
      {{ tip }}
    </div>
    
    <!-- 上传进度条 -->
    <el-progress 
      v-if="showProgress && uploading" 
      :percentage="uploadProgress" 
      :format="percentageFormat"
      :status="uploadStatus"
    />
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { Delete, Plus } from '@element-plus/icons-vue'
import { getFileUrl, uploadFile,deleteFile } from '@/api/common/new_upload'
import { ElMessage } from 'element-plus'

export default {
  name: 'ImageUploader',
  components: {
    Delete,
    Plus
  },
  props: {
    // 上传成功后的值（可用于v-model）
    modelValue: {
      type: String,
      default: ''
    },
    imageUrl: {
      type: String,
      default: ''
    },
    // 接受的文件类型
    fileTypes: {
      type: Array,
      default: () => ['.jpg', '.jpeg', '.png', '.gif', '.webp']
    },
    // 文件大小限制（MB）
    maxSize: {
      type: Number,
      default: 5
    },
    // 是否允许多文件上传
    multiple: {
      type: Boolean,
      default: false
    },
    // 最大文件数量
    limit: {
      type: Number,
      default: 1
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      default: false
    },
    // 显示文件列表
    showFileList: {
      type: Boolean,
      default: false
    },
    // 是否显示进度条
    showProgress: {
      type: Boolean,
      default: true
    },
    // 是否启用拖拽上传
    drag: {
      type: Boolean,
      default: false
    },
    // 列表类型：text, picture, picture-card
    listType: {
      type: String,
      default: 'picture-card'
    },
    // 上传按钮文本
    buttonText: {
      type: String,
      default: '上传图片'
    },
    // 提示文本
    tip: {
      type: String,
      default: ''
    },
    // 其他上传参数
    params: {
      type: Object,
      default: () => ({})
    }
  },
  emits: ['update:modelValue', 'success', 'error', 'remove', 'exceed'],
  setup(props, { emit }) {
    // 上传状态
    const uploading = ref(false);
    const uploadProgress = ref(0);
    const uploadStatus = ref('');
    
    // 格式化百分比
    const percentageFormat = (percentage) => {
      return percentage === 100 ? '上传完成' : `${percentage}%`;
    };
    
    // 接受的文件类型字符串
    const acceptTypes = computed(() => {
      return props.fileTypes.join(',');
    });
    
    // 图片URL
    const imageUrl = computed(() => {
      if (!props.modelValue) return '';
      return getFileUrl(props.modelValue);
    });
    
    // 上传前验证
    const beforeUpload = (file) => {
      // 检查文件类型
      const isValidType = props.fileTypes.some(type => {
        const extension = type.startsWith('.') ? type.substring(1) : type;
        return file.name.toLowerCase().endsWith(extension.toLowerCase());
      });
      
      if (!isValidType) {
        ElMessage.error(`文件类型不支持，请上传${props.fileTypes.join('、')}格式的文件`);
        return false;
      }
      
      // 检查文件大小
      const isLessThanMaxSize = file.size / 1024 / 1024 < props.maxSize;
      if (!isLessThanMaxSize) {
        ElMessage.error(`文件大小不能超过 ${props.maxSize}MB!`);
        return false;
      }
      
      return true;
    };
    
    // 自定义上传方法
    const customUpload = async (options) => {
      try {
        const { file, onProgress } = options;
        
        // 重置状态
        uploading.value = true;
        uploadProgress.value = 0;
        uploadStatus.value = '';
        
        // 使用uploadImage方法上传到云服务器
        // 注意: uploadImage方法期望直接接收File对象和进度回调函数
        const progressCallback = (percentage) => {
          uploadProgress.value = percentage;
          onProgress({ percent: percentage });
        };
        
        // 调用uploadImage API，直接传入文件对象而不是FormData
        const response = await uploadFile(file, progressCallback);
        console.log(response);
        
        // 处理成功
        uploadProgress.value = 100;
        uploadStatus.value = 'success';
        
        setTimeout(() => {
          uploading.value = false;
        }, 1000);
        const isfalse = (response && response.data && (response.data.code === 200 || response.data.code === 201 ) ? true : false);
        console.log(isfalse);
        
        if (response) {
          // 获取文件URL - 根据实际返回结构调整
          console.log("进入获取文件URL逻辑");
          
          const result = response.data;
          const fileUrl = result.url || result.path || result.url || '';
          
          // 调用成功回调
          options.onSuccess(response.data);
          
          // 更新v-model
          emit('update:modelValue', fileUrl);
          
          // 触发成功事件
          emit('success', { response: response.data, fileUrl, file });
        } else {
          // 处理业务错误
          uploadStatus.value = 'exception';
          const error = new Error('上传失败，返回数据异常');
          options.onError(error);
          emit('error', { error, file });
        }
      } catch (error) {
        console.error('上传图片错误:', error);
        // 处理异常
        uploadProgress.value = 100;
        uploadStatus.value = 'exception';
        options.onError(error);
        emit('error', { error, file: options.file });
        
        setTimeout(() => {
          uploading.value = false;
        }, 1000);
      }
    };
    
    // 处理超出限制
    const handleExceed = (files, uploadFiles) => {
      ElMessage.warning(`最多只能上传 ${props.limit} 个文件!`);
      emit('exceed', { files, uploadFiles });
    };
    
    // 删除图片
    const handleRemove = (uploadFile, uploadFiles) => {
      deleteFile(props.modelValue).then(() => {
        ElMessage({
          message: '删除成功',
          type: 'success'
        })
      });
      emit('update:modelValue', '');
      emit('remove', { uploadFile, uploadFiles });
    };
    
    return {
      acceptTypes,
      imageUrl,
      uploading,
      uploadProgress,
      uploadStatus,
      percentageFormat,
      beforeUpload,
      customUpload,
      handleExceed,
      handleRemove
    };
  }
}
</script>

<style scoped>
.image-uploader {
  width: 100%;
}

.uploader {
  width: 100%;
}

.image-preview {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 120px;
  max-height: 150px;
  max-width: 300px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.image-preview:hover .image-actions {
  opacity: 1;
}

.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.action-icon {
  font-size: 20px;
  color: #fff;
  cursor: pointer;
}

.upload-icon {
  font-size: 24px;
  color: #8c939d;
  margin-bottom: 6px;
}

.upload-text {
  font-size: 12px;
  color: #606266;
  margin-top: 6px;
}

:deep(.el-upload) {
  width: 100%;
  max-width: 300px;
}

:deep(.el-upload-dragger) {
  width: 100%;
  max-width: 300px;
}

:deep(.el-upload--picture-card) {
  width: 100%;
  max-width: 300px;
  height: auto;
  min-height: 120px;
  max-height: 150px;
}

.el-upload__tip {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
}
</style> 