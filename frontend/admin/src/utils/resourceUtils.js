/**
 * 资源管理相关工具函数
 */

// 格式化文件大小
export const formatFileSize = (size) => {
  if (!size) return '未知';
  
  if (size < 1024) {
    return size + ' B';
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB';
  } else if (size < 1024 * 1024 * 1024) {
    return (size / 1024 / 1024).toFixed(2) + ' MB';
  } else {
    return (size / 1024 / 1024 / 1024).toFixed(2) + ' GB';
  }
};

// 获取文件类型
export const getFileType = (fileName) => {
  if (!fileName) return 'other';
  
  if (/\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(fileName)) {
    return 'image';
  } else if (/\.(mp4|webm|ogg|mov|avi|wmv|flv|mkv)$/i.test(fileName)) {
    return 'video';
  } else if (/\.(mp3|wav|ogg|aac|flac)$/i.test(fileName)) {
    return 'audio';
  } else if (/\.(pdf|doc|docx|xls|xlsx|ppt|pptx|txt)$/i.test(fileName)) {
    return 'document';
  } else {
    return 'other';
  }
};

// 格式化日期时间
export const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  const date = new Date(dateTime);
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
};



// 获取资源图标
export const getResourceIcon = (type) => {
  switch (type) {
    case 'document': return 'Document';
    case 'image': return 'Picture';
    case 'video': return 'VideoPlay';
    case 'audio': return 'Headset';
    default: return 'Files';
  }
};

// 获取资源标签类型
export const getResourceTagType = (type) => {
  switch (type) {
    case 'document': return 'primary';
    case 'image': return 'success';
    case 'video': return 'danger';
    case 'audio': return 'warning';
    default: return 'info';
  }
};

// 获取资源状态标签
export const getStatusLabel = (status) => {
  switch (parseInt(status)) {
    case 0: return '待审核';
    case 1: return '已通过';
    case 2: return '已拒绝';
    default: return '未知';
  }
};

// 获取资源状态标签类型
export const getStatusType = (status) => {
  switch (parseInt(status)) {
    case 0: return 'warning';
    case 1: return 'success';
    case 2: return 'danger';
    default: return 'info';
  }
};

// 检查文件是否可以在浏览器中预览
export const canPreviewInBrowser = (fileName) => {
  if (!fileName) return false;
  
  // 增加对Office文档的在线预览支持
  if (/\.(pdf|txt)$/i.test(fileName)) {
    return 'native'; // 浏览器原生支持
  } else if (/\.(doc|docx|xls|xlsx|ppt|pptx)$/i.test(fileName)) {
    return 'office'; // 使用Office在线预览
  } else {
    return false;
  }
}; 