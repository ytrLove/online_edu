<template>
    <div class="file-uploader">
      <el-upload
        :action="null"
        :http-request="handleUpload"
        :on-success="handleSuccess"
        :on-error="handleError"
        :on-progress="handleProgress"
        :before-upload="beforeUpload"
        :on-remove="handleRemove"
        :accept="accept"
        :multiple="multiple"
        :limit="limit"
        :disabled="disabled"
        :show-file-list="showFileList"
        :drag="drag"
        :list-type="listType"
      >
        <template #default>
          <div v-if="drag" class="upload-drag-area">
            <el-icon class="upload-icon"><upload-filled /></el-icon>
            <div class="upload-text">拖拽文件到此处或 <em>点击上传</em></div>
            <div class="upload-tip" v-if="tip">{{ tip }}</div>
          </div>
          <el-button v-else type="primary">{{ buttonText }}</el-button>
        </template>
        
        <template #tip>
          <div class="upload-tip" v-if="tip && !drag">{{ tip }}</div>
        </template>
        
        <template #file="{ file }" v-if="listType === 'picture-card'">
          <img
            v-if="file.url && isImage(file)"
            :src="file.url"
            class="upload-image"
            alt=""
          />
          <video
            v-else-if="file.url && isVideo(file)"
            :src="file.url"
            class="upload-video"
            controls
          ></video>
          <div v-else class="upload-file-name">{{ file.name }}</div>
        </template>
      </el-upload>
      
      <!-- 上传进度条 -->
      <el-progress
        v-if="showProgress && uploadProgress > 0 && uploadProgress < 100"
        :percentage="uploadProgress"
        :format="progressFormat"
        :stroke-width="8"
      />
    </div>
  </template>
  
  <script>
  import { ref, computed } from 'vue'
  import { ElMessage } from 'element-plus'
  import { UploadFilled } from '@element-plus/icons-vue'
  import { uploadFile, deleteFile } from '@/api/common/new_upload'
  
  export default {
    name: 'FileUploader',
    components: {
      UploadFilled
    },
    props: {
      // 上传类型：image, video, file
      uploadType: {
        type: String,
        default: 'file',
        validator: (value) => ['image', 'video', 'file'].includes(value)
      },
      // 接受的文件类型
      fileTypes: {
        type: Array,
        default: () => []
      },
      // 文件大小限制（MB）
      maxSize: {
        type: Number,
        default: 100
      },
      // 是否允许多文件上传
      multiple: {
        type: Boolean,
        default: false
      },
      // 最大文件数量
      limit: {
        type: Number,
        default: 5
      },
      // 是否禁用
      disabled: {
        type: Boolean,
        default: false
      },
      // 显示文件列表
      showFileList: {
        type: Boolean,
        default: true
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
        default: 'text',
        validator: (value) => ['text', 'picture', 'picture-card'].includes(value)
      },
      // 上传按钮文本
      buttonText: {
        type: String,
        default: '上传文件'
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
    setup(props, { emit }) {
      const uploadProgress = ref(0)
      const isUploading = ref(false)
      const upRes = ref()
      
      // 计算接受的文件类型
      const accept = computed(() => {
        if (props.fileTypes && props.fileTypes.length > 0) {
          return props.fileTypes.join(',')
        }
        
        switch (props.uploadType) {
          case 'image':
            return '.jpg,.jpeg,.png,.gif,.webp'
          case 'video':
            return '.mp4,.webm,.ogg,.mov'
          default:
            return ''
        }
      })
      
      // 上传前验证
      const beforeUpload = (file) => {
        // 检查文件类型
        if (props.fileTypes && props.fileTypes.length > 0) {
          const fileExt = file.name.split('.').pop().toLowerCase()
          const isValidType = props.fileTypes.some(type => {
            if (type.startsWith('.')) {
              return type.substring(1).toLowerCase() === fileExt
            } else {
              return type.toLowerCase() === file.type
            }
          })
          
          if (!isValidType) {
            ElMessage.error(`文件类型不支持，请上传 ${props.fileTypes.join(', ')} 格式的文件`)
            return false
          }
        }
        
        // 检查文件大小
        const isLtMaxSize = file.size / 1024 / 1024 < props.maxSize
        if (!isLtMaxSize) {
          ElMessage.error(`文件大小不能超过 ${props.maxSize}MB!`)
          return false
        }
        
        return true
      }
      
      // 处理上传
      const handleUpload = async (options) => {
        isUploading.value = true
        uploadProgress.value = 0
        
        try {
          let response
          
          // 根据上传类型调用不同的上传函数
          switch (props.uploadType) {
            case 'image':
              response = await uploadImage(options.file, (percent) => {
                uploadProgress.value = percent
                options.onProgress({ percent })
              })
              break
            case 'video':
              response = await uploadVideo(options.file, (percent) => {
                uploadProgress.value = percent
                options.onProgress({ percent })
              })
              break
            default:
              response = await uploadFile(options.file, (percent) => {
                uploadProgress.value = percent
                options.onProgress({ percent })
              })
              break
          }
          
          uploadProgress.value = 100
          isUploading.value = false
          
          // 响应数据格式可能会有所不同，根据实际情况调整
          const result = response.data || response
          
          options.onSuccess(result)
          return result
        } catch (error) {
          isUploading.value = false
          options.onError(error)
          throw error
        }
      }
      
      // 上传成功处理
      const handleSuccess = (response, file, fileList) => {
        upRes.value = response
        uploadProgress.value = 100
        emit('success', { response, file, fileList })
      }
      
      // 上传错误处理
      const handleError = (error, file, fileList) => {
        uploadProgress.value = 0
        ElMessage.error(`文件 ${file.name} 上传失败: ${error.message || '未知错误'}`)
        emit('error', { error, file, fileList })
      }
      
      // 上传进度处理
      const handleProgress = (event, file, fileList) => {
        uploadProgress.value = Math.round(event.percent)
        emit('progress', { percent: event.percent, file, fileList })
      }
      
      // 进度条格式化
      const progressFormat = (percentage) => {
        return percentage === 100 ? '完成' : `${percentage}%`
      }
      
      // 判断是否为图片文件
      const isImage = (file) => {
        return file.type?.startsWith('image/') || /\.(jpg|jpeg|png|gif|webp)$/i.test(file.name)
      }

    // 删除文件
    const handleRemove = () => {
        deleteFile(upRes.value.fileName).then(() => {
            ElMessage({
                message: '取消上传',
                type: 'success'
            })
        }).catch(() => {
            ElMessage({
                message: '取消上传失败',
                type: 'error'
            })
        })
    }
        
      // 判断是否为视频文件
      const isVideo = (file) => {
        return file.type?.startsWith('video/') || /\.(mp4|webm|ogg|mov)$/i.test(file.name)
      }
      
      return {
        uploadProgress,
        isUploading,
        accept,
        beforeUpload,
        handleUpload,
        handleSuccess,
        handleRemove,
        handleError,
        handleProgress,
        progressFormat,
        isImage,
        isVideo
      }
    }
  }
  </script>
  
  <style scoped>
  .file-uploader {
    width: 100%;
  }
  
  .upload-drag-area {
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
  
  .upload-icon {
    font-size: 28px;
    color: #409EFF;
    margin-bottom: 8px;
  }
  
  .upload-text {
    font-size: 14px;
    color: #606266;
    margin-bottom: 8px;
  }
  
  .upload-text em {
    font-style: normal;
    color: #409EFF;
  }
  
  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 8px;
  }
  
  .upload-image, .upload-video {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .upload-file-name {
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 12px;
    text-align: center;
  }
  </style> 