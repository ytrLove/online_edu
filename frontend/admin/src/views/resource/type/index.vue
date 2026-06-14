<template>
  <div class="resource-type-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>资源类型管理</span>
          <div class="header-actions">
            <el-button v-permission="'edu:resource:type:add'" type="primary" @click="handleAdd">
              <el-icon><plus /></el-icon>新增类型
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="searchQuery" @search="handleSearch" @reset="resetSearch">
        <el-form-item label="类型名称">
          <el-input v-model="searchQuery.typeName" placeholder="请输入资源类型名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchQuery.status" placeholder="类型状态" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="启用" value="1"></el-option>
            <el-option label="禁用" value="0"></el-option>
          </el-select>
        </el-form-item>
      </search-form>

      <!-- 表格区域 -->
      <data-table
        :data="typeList"
        :loading="loading"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @pagination="handlePagination"
        @update:current-page="val => currentPage = val"
        @update:page-size="val => pageSize = val"
        border
      >
        <el-table-column prop="typeId" label="类型ID" width="80" align="center" />
        <el-table-column prop="typeName" label="类型名称" min-width="150" />
        <el-table-column prop="fileExtensions" label="文件拓展名" width="150" align="center" show-overflow-tooltip />
        <el-table-column prop="remark" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button v-permission="'edu:resource:type:edit'" type="primary" size="small" text @click="handleEdit(row)">
              <el-icon><edit /></el-icon>编辑
            </el-button>
            <el-button v-permission="'edu:resource:type:remove'" type="danger" size="small" text @click="handleDelete(row)">
              <el-icon><delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>

      <!-- 资源类型编辑对话框 -->
      <el-dialog
        v-model="showEditModal"
        :title="formTitle"
        width="600px"
        append-to-body
        destroy-on-close
      >
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="类型名称" prop="typeName">
            <el-input v-model="form.typeName" placeholder="请输入资源类型名称" />
          </el-form-item>
          <el-form-item label="类型编码" prop="fileExtensions">
            <el-input v-model="form.fileExtensions" placeholder="请输入资源类型编码" :disabled="form.typeId !== undefined" />
          </el-form-item>
          <el-form-item label="描述" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入资源类型描述" :rows="3" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio :value= 1>启用</el-radio>
              <el-radio :value= 0>禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="showEditModal = false">取消</el-button>
            <el-button v-permission="form.typeId ? 'edu:resource:type:edit' : 'edu:resource:type:add'" type="primary" @click="submitForm">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';
import SearchForm from '@/components/SearchForm/index.vue';
import DataTable from '@/components/DataTable/index.vue';
import { getResourceTypeList, getResourceTypeById, addResourceType, updateResourceType, deleteResourceType } from '@/api/edu/resource';

// 加载状态
const loading = ref(false);
// 资源类型列表
const typeList = ref([]);
// 搜索参数
const searchQuery = reactive({
  typeName: '',
  status: ''
});
// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
// 编辑对话框
const showEditModal = ref(false);
const formTitle = ref('');
const formRef = ref(null);

// 表单数据
const form = reactive({
  typeId: undefined,
  typeName: '',
  fileExtensions: '',
  remark: '',
  status: 1
});

// 表单验证规则
const rules = {
  typeName: [
    { required: true, message: '请输入资源类型名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  typeCode: [
    { required: true, message: '请输入资源类型编码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '编码格式为大写字母和下划线', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入显示顺序', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择资源类型状态', trigger: 'change' }
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
 * 获取资源类型列表
 */
const fetchResourceTypeList = async () => {
  try {
    loading.value = true;
    
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      typeName: searchQuery.typeName,
      status: searchQuery.status
    };
    
    const res = await getResourceTypeList(params);
    typeList.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (error) {
    console.error('获取资源类型列表失败', error);
    ElMessage.error('获取资源类型列表失败: ' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

/**
 * 搜索处理
 */
const handleSearch = () => {
  currentPage.value = 1;
  fetchResourceTypeList();
};

/**
 * 重置搜索
 */
const resetSearch = () => {
  searchQuery.typeName = '';
  searchQuery.status = '';
  currentPage.value = 1;
  fetchResourceTypeList();
};

/**
 * 分页查询
 */
const handlePagination = (params) => {
  currentPage.value = params.page;
  pageSize.value = params.limit;
  fetchResourceTypeList();
};

/**
 * 新增资源类型
 */
const handleAdd = () => {
  resetForm();
  formTitle.value = '新增资源类型';
  showEditModal.value = true;
};

/**
 * 编辑资源类型
 */
const handleEdit = async (row) => {
  try {
    resetForm();
    formTitle.value = '编辑资源类型';
    
    // 获取完整的资源类型信息
    const res = await getResourceTypeById(row.typeId);
    Object.assign(form, res.data);
    showEditModal.value = true;
  } catch (error) {
    console.error('获取资源类型详情失败', error);
    ElMessage.error('获取资源类型详情失败: ' + (error.message || '未知错误'));
  } 
};

/**
 * 删除资源类型
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除资源类型 "${row.typeName}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteResourceType(row.typeId);
      ElMessage.success('删除成功');
      fetchResourceTypeList();
    } catch (error) {
      console.error('删除资源类型失败', error);
      ElMessage.error('删除资源类型失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {});
};

/**
 * 重置表单
 */
const resetForm = () => {
  form.typeId = undefined;
  form.typeName = '';
  form.remark = '';
  form.fileExtensions = '';
  form.status = 1;
};

/**
 * 提交表单
 */
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const isEdit = form.typeId !== undefined;
        
        if (isEdit) {
          await updateResourceType(form);
        } else {
          await addResourceType(form);
        }
        
        ElMessage.success(isEdit ? '修改成功' : '新增成功');
        showEditModal.value = false;
        fetchResourceTypeList();
      } catch (error) {
        console.error('保存资源类型失败', error);
        ElMessage.error('保存资源类型失败: ' + (error.message || '未知错误'));
      }
    }
  });
};

// 页面初始化
onMounted(() => {
  fetchResourceTypeList();
});
</script>

<style scoped>
.resource-type-container {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
