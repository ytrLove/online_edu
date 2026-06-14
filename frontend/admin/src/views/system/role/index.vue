<template>
  <div class="role-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <div class="header-actions">
            <el-button type="primary" plain v-permission="'system:role:add'" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增角色
            </el-button>
            <el-button type="warning" plain @click="getList">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="queryParams" @search="handleQuery" @reset="resetQuery">
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="queryParams.roleKey" placeholder="请输入角色编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="角色状态" clearable>
            <el-option label="正常" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
      </search-form>

      <!-- 表格区域 -->
      <data-table
        :data="roleList"
        :loading="loading"
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :total="total"
        @pagination="handlePagination"
        @update:current-page="val => queryParams.pageNum = val"
        @update:page-size="val => queryParams.pageSize = val"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column type="index" label="序号" width="50" align="center" />
        <el-table-column label="角色名称" prop="roleName" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column label="角色编码" prop="roleKey" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column label="排序" prop="roleSort" width="60" align="center" />
        <el-table-column label="状态" align="center" prop="status" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="150" :show-overflow-tooltip="true">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="120" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center" width="230" fixed="right">
          <template #default="scope">
            <el-button v-permission="'system:role:edit'" type="primary" size="small" text @click="handleUpdate(scope.row)">
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button v-permission="'system:role:edit'" type="success" size="small" text @click="handleAuthMenu(scope.row)">
              <el-icon><Menu /></el-icon>菜单
            </el-button>
            <el-button v-permission="'system:role:remove'" type="danger" size="small" text @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>
    </el-card>

    <!-- 添加或修改角色对话框 -->
    <el-dialog :model-value="open" @update:model-value="open = $event" :title="title" width="500px" append-to-body>
      <el-form ref="roleFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="排序" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label=1>正常</el-radio>
            <el-radio :label=0>禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-permission="form.roleId ? 'system:role:edit' : 'system:role:add'">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配菜单对话框 -->
    <el-dialog :model-value="menuOpen" @update:model-value="menuOpen = $event" title="分配菜单" width="500px" append-to-body>
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="form.roleName" disabled />
        </el-form-item>
        <el-form-item label="菜单列表">
          <el-tree
            ref="menuTree"
            :data="menuOptions"
            :props="{ label: 'menuName', children: 'children', id: 'menuId' }"
            :default-checked-keys="form.menuIds"
            show-checkbox
            check-strictly
            node-key="menuId"
            default-expand-all
            @check="handleTreeCheck"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAuthMenu" v-permission="'system:role:edit'">确 定</el-button>
          <el-button @click="cancelAuthMenu">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { parseTime } from '@/utils/ruoyi'
import { getRoleList, getRoleDetail, addRole, updateRole, delRole, assignMenus, getRoleMenus } from '@/api/system/role'
import { getMenuTree } from '@/api/system/menu'
import { Edit, Delete, Menu, Plus, Refresh } from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm/index.vue'
import DataTable from '@/components/DataTable/index.vue'

// 遮罩层
const loading = ref(true)
// 显示弹出层
const open = ref(false)
const menuOpen = ref(false)
// 弹出层标题
const title = ref('')
// 角色表格数据
const roleList = ref([])
// 菜单树选项
const menuOptions = ref([])
// 菜单树引用
const menuTree = ref(null)
// 表单引用
const roleFormRef = ref(null)
// 总条数
const total = ref(0)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 6,
  roleName: undefined,
  roleKey: undefined,
  status: undefined
})

// 表单参数
const form = reactive({
  roleId: undefined,
  roleName: undefined,
  roleKey: undefined,
  roleSort: 0,
  status: 1,
  remark: undefined,
  menuIds: []
})

// 表单校验
const rules = {
  roleName: [
    { required: true, message: '角色名称不能为空', trigger: 'blur' },
    { min: 2, max: 20, message: '角色名称长度必须介于 2 和 20 之间', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '角色编码不能为空', trigger: 'blur' },
    { min: 2, max: 20, message: '角色编码长度必须介于 2 和 20 之间', trigger: 'blur' }
  ]
}

/** 查询角色列表 */
const getList = async () => {
  loading.value = true
  try {
    // 确保查询参数的数据类型正确
    const params = { ...queryParams };
    if (params.status !== undefined && params.status !== null && params.status !== '') {
      params.status = Number(params.status);
    }
    
    const res = await getRoleList(params)
    roleList.value = res.data.rows
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

/** 分页查询 */
const handlePagination = (params) => {
  queryParams.pageNum = params.page
  queryParams.pageSize = params.limit
  getList()
}

/** 取消按钮 */
const cancel = () => {
  open.value = false
  reset()
}

/** 表单重置 */
const reset = () => {
  form.roleId = undefined
  form.roleName = undefined
  form.roleKey = undefined
  form.roleSort = 0
  form.status = 1
  form.remark = undefined
  form.menuIds = []
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryParams.roleName = undefined
  queryParams.roleKey = undefined
  queryParams.status = undefined
  handleQuery()
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '添加角色'
}

/** 修改按钮操作 */
const handleUpdate = async (row) => {
  reset()
  const res = await getRoleDetail(row.roleId)
  Object.assign(form, res.data)
  open.value = true
  title.value = '修改角色'
}

/** 表单提交 */
const submitForm = async () => {
  // 表单验证
  await roleFormRef.value.validate()

  // 转换数据类型，确保数字类型正确
  const formData = { ...form };

  // 确保数字类型
  formData.status = Number(formData.status);
  formData.roleSort = Number(formData.roleSort);
  if (formData.roleId) {
    formData.roleId = Number(formData.roleId);
  }

  const isEdit = formData.roleId !== undefined;
  const apiRequest = isEdit ? updateRole(formData) : addRole(formData);

  try {
    const res = await apiRequest;
    ElMessage.success(res.msg || (isEdit ? '修改成功' : '新增成功'));
    open.value = false;
    getList();
  } catch (error) {
    console.error(isEdit ? '修改失败' : '新增失败', error);
  }
}

/** 删除按钮操作 */
const handleDelete = (row) => {
  ElMessageBox.confirm('是否确认删除名称为"' + row.roleName + '"的数据项?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await delRole(row.roleId)
    getList()
    ElMessage.success('删除成功')
  })
}

/** 分配菜单按钮操作 */
const handleAuthMenu = async (row) => {
  try {
    // 清空之前的数据
    form.menuIds = []
    menuOptions.value = []
    
    // 获取角色信息
    const res = await getRoleDetail(row.roleId)
    
    // 填充表单数据（角色基本信息）
    Object.assign(form, res.data)
    
    // 获取全部菜单树数据(用于显示)
    const menuRes = await getMenuTree()
    if (menuRes && menuRes.data) {
      menuOptions.value = menuRes.data
    }
    
    // 获取角色已有菜单数据
    const roleMenusRes = await getRoleMenus(row.roleId)
    if (roleMenusRes && roleMenusRes.data) {
      console.log('角色菜单:', roleMenusRes.data)
      
      // 后端现在返回平铺的菜单列表，直接提取ID即可
      form.menuIds = roleMenusRes.data.map(menu => Number(menu.menuId));
      console.log('提取的菜单ID:', form.menuIds);
    }
    
    // 打开对话框
    menuOpen.value = true
    
    // 等待DOM更新后再尝试设置选中状态
    await nextTick()
  } catch (error) {
    console.error('分配菜单出错:', error)
    ElMessage.error('获取菜单数据失败')
  }
}

/** 取消分配菜单 */
const cancelAuthMenu = () => {
  menuOpen.value = false
}

/** 分配菜单提交 */
const submitAuthMenu = async () => {
  // 获取选中的菜单ID - 使用el-tree的getCheckedKeys方法获取所有选中的节点
  const checkedKeys = menuTree.value.getCheckedKeys();
  // 半选中的节点也需要包含，对于需要特定权限但不需要显示菜单的情况很重要
  const halfCheckedKeys = menuTree.value.getHalfCheckedKeys();
  
  console.log('选中的节点:', checkedKeys);
  console.log('半选中的节点:', halfCheckedKeys);
  
  // 合并所有需要的菜单ID
  const allKeys = [...checkedKeys, ...halfCheckedKeys];
  
  // 构建请求数据
  const data = {
    roleId: form.roleId,
    menuIds: allKeys.map(id => Number(id)) // 确保ID是数字类型
  };
  
  console.log('提交的数据:', data);

  try {
    const res = await assignMenus(data);
    ElMessage.success(res.msg || '分配成功');
    menuOpen.value = false;
  } catch (error) {
    console.error('分配失败', error);
  }
}

/** 处理树节点选中 */
const handleTreeCheck = (data, { checkedKeys, checkedNodes, halfCheckedKeys, halfCheckedNodes }) => {
  console.log('节点选中变化:', data.menuId);
  console.log('当前选中节点:', checkedKeys);
  console.log('当前半选中节点:', halfCheckedKeys);
}

// 初始化数据
onMounted(() => {
  getList()
})
</script>

<style scoped>
.role-container {
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

.toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
