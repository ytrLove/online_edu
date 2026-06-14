<template>
    <div class="upload-container">
        <NewFileUpload
            upload-type="file"
            :max-size="50"
            :multiple="false"
            :show-progress="true"
            tip="支持任意类型文件，单个文件不超过50MB"
            button-text="选择文件"
            @success="handleUploadSuccess"
            @error="handleUploadError"
        ></NewFileUpload>
    </div>
  
</template>

<script setup>
import NewFileUpload from '@/components/newFileUpload.vue';

const handleUploadSuccess = (res, file) => {
    console.log(res, file);
}


</script>

<style>
.upload-container { 
    width: 200px;
    height: 150px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f5f5f5;
    font-size: 20px;
}

</style>