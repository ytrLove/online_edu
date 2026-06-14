import Modal from './Modal.vue'
import ImageUploader from './ImageUploader.vue'
import FileUploader from './FileUploader.vue'

// 导出所有自定义组件
export {
  FileUploader,
  ImageUploader,
 
}

// 全局注册方法
export default {
  install(app) {
    app.component('Modal', Modal)
    app.component('ImageUploader', ImageUploader)
    app.component('FileUploader', FileUploader)
  }
} 