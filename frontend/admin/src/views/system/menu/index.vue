<template>
  <div class="menu-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <div class="header-actions">
            <el-button type="primary" plain v-permission="'system:menu:add'" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增菜单
            </el-button>
            <el-button type="info" plain @click="expandAll" :disabled="isAllExpanded">展开全部</el-button>
            <el-button type="info" plain @click="collapseAll" :disabled="isAllCollapsed">折叠全部</el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="queryParams" @search="handleQuery" @reset="resetQuery">
        <el-form-item label="菜单名称">
          <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="菜单状态" clearable>
            <el-option label="正常" value="1" />
            <el-option label="停用" value="0" />
          </el-select>
        </el-form-item>
      </search-form>

      <!-- 表格区域 -->
      <data-table
        ref="menuTableRef"
        :data="menuList"
        :loading="loading"
        :show-pagination="false"
        row-key="menuId"
        :default-expand-all="defaultExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="220" :show-overflow-tooltip="true" align="left">
          <template #default="scope">
            <span class="menu-name">
              <el-icon v-if="scope.row.icon && isValidIcon(scope.row.icon)" class="menu-icon">
                <component :is="scope.row.icon" />
              </el-icon>
              <span v-else-if="scope.row.icon" class="menu-icon">
                <i :class="scope.row.icon"></i>
              </span>
              {{ scope.row.menuName }}
              <el-tag v-if="scope.row.menuType === 'M'" type="success" size="small" effect="plain" class="menu-type-tag">目录</el-tag>
              <el-tag v-else-if="scope.row.menuType === 'C'" type="primary" size="small" effect="plain" class="menu-type-tag">菜单</el-tag>
              <el-tag v-else-if="scope.row.menuType === 'F'" type="warning" size="small" effect="plain" class="menu-type-tag">按钮</el-tag>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" align="center" width="70">
          <template #default="scope">
            <el-icon v-if="scope.row.icon && isValidIcon(scope.row.icon)" class="table-icon">
              <component :is="scope.row.icon" />
            </el-icon>
            <span v-else-if="scope.row.icon" class="menu-icon">
              <i :class="scope.row.icon"></i>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="60" align="center" />
        <el-table-column prop="perms" label="权限标识" min-width="130" :show-overflow-tooltip="true" align="left" />
        <el-table-column prop="path" label="路由地址" min-width="130" :show-overflow-tooltip="true" align="left" />
        <el-table-column prop="component" label="组件路径" min-width="150" :show-overflow-tooltip="true" align="left" />
        <el-table-column prop="status" label="状态" width="70" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="230" fixed="right">
          <template #default="scope">
            <el-button v-permission="'system:menu:edit'" type="primary" size="small" text @click="handleUpdate(scope.row)">
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button v-permission="'system:menu:add'" type="success" size="small" text @click="handleAdd(scope.row)">
              <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button v-permission="'system:menu:remove'" type="danger" size="small" text @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>
    </el-card>

    <!-- 添加或修改菜单对话框 -->
    <el-dialog :model-value="open" @update:model-value="open = $event" :title="title" width="680px" append-to-body>
      <el-form ref="menuFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select
                v-model="form.parentId"
                :data="menuOptions"
                :props="{ label: 'menuName', value: 'menuId', children: 'children' }"
                placeholder="选择上级菜单"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio :label="'M'">目录</el-radio>
                <el-radio :label="'C'">菜单</el-radio>
                <el-radio :label="'F'">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.menuType !== 'F'">
            <el-form-item label="图标" prop="icon">
              <div class="icon-input-container">
                <el-input v-model="form.icon" placeholder="请输入图标名称">
                  <template #prepend v-if="form.icon">
                    <div class="icon-preview-container">
                      <el-icon v-if="isValidIcon(form.icon)" class="form-icon-preview">
                        <component :is="form.icon" />
                      </el-icon>
                      <i v-else :class="form.icon" class="form-icon-preview"></i>
                    </div>
                  </template>
                  <template #append>
                    <el-button @click="showIconSelect">选择图标</el-button>
                  </template>
                </el-input>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label= 1>正常</el-radio>
                <el-radio :label= 0>停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24" >
            <el-form-item label="权限标识" prop="perms">
              <el-input v-model="form.perms" placeholder="请输入权限标识" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.menuType !== 'F'">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.menuType === 'C'">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="form.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm" v-permission="form.menuId ? 'system:menu:edit' : 'system:menu:add'">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 图标选择对话框 -->
    <el-dialog 
      title="选择图标" 
      :model-value="iconSelectVisible" 
      @update:model-value="iconSelectVisible = $event" 
      width="800px" 
      append-to-body
    >
      <div class="search-container">
        <el-input 
          v-model="iconSearchText" 
          placeholder="搜索图标" 
          clearable 
          prefix-icon="Search"
          @input="handleIconSearch"
        />
      </div>
      <div class="icon-list">
        <div 
          v-for="icon in filteredIconList" 
          :key="icon" 
          class="icon-item" 
          @click="selectIcon(icon)"
        >
          <el-icon class="icon-preview">
            <component :is="icon" />
          </el-icon>
          <span class="icon-name">{{ icon }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { parseTime } from '@/utils/ruoyi'
import { getMenuList, getMenu, addMenu, updateMenu, delMenu } from '@/api/system/menu'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import SearchForm from '@/components/SearchForm/index.vue'
import DataTable from '@/components/DataTable/index.vue'
import router from '@/router'

const { Search, Edit, Delete, Plus, Menu, Document, Setting, User, Lock, Key, Bell, Star, DataLine, View, Hide, Upload, Download, Refresh, Check, Close, Warning, Info, QuestionFilled, ArrowDown, ArrowUp, ArrowLeft, ArrowRight, House, Calendar, ChatLineRound, List, Grid, Operation } = ElementPlusIconsVue

// 遮罩层
const loading = ref(true)
// 显示图标选择
const iconSelectVisible = ref(false)
// 弹出层标题
const title = ref('')
// 是否显示弹出层
const open = ref(false)
// 菜单表格数据
const menuList = ref([])
// 菜单树选项
const menuOptions = ref([])

// 有效的Element Plus图标
const validElementIcons = computed(() => Object.keys(ElementPlusIconsVue))

// 图标列表
const iconList = ref([
  'Search', 'Edit', 'Delete', 'Plus', 'Menu', 'Document', 'Setting',
  'User', 'Lock', 'Key', 'Bell', 'Star', 'DataLine', 'View', 'Hide', 
  'Upload', 'Download', 'Refresh', 'Check', 'Close', 'Warning', 
  'Info', 'QuestionFilled', 'ArrowDown', 'ArrowUp', 'ArrowLeft', 
  'ArrowRight', 'House', 'Calendar', 'ChatLineRound', 'List', 'Grid', 'Operation'
])

// 查询参数
const queryParams = reactive({
  menuName: undefined,
  status: undefined
})

// 表单参数
const form = reactive({
  menuId: undefined,
  parentId: 0,
  menuName: undefined,
  icon: undefined,
  menuType: 'M',
  orderNum: 0,
  perms: undefined,
  component: undefined,
  path: undefined,
  status: '0'
})

// 根据菜单类型获取不同的表单校验规则
const rules = computed(() => {
  const baseRules = {
    menuName: [
      { required: true, message: '菜单名称不能为空', trigger: 'blur' }
    ],
    orderNum: [
      { required: true, message: '显示排序不能为空', trigger: 'blur' }
    ]
  }

  // 根据不同菜单类型添加不同的校验规则
  if (form.menuType === 'M') {
    // 目录类型
    baseRules.path = [
      { required: true, message: '路由地址不能为空', trigger: 'blur' }
    ]
  } else if (form.menuType === 'C') {
    // 菜单类型
    baseRules.path = [
      { required: true, message: '路由地址不能为空', trigger: 'blur' }
    ]
    baseRules.component = [
      { required: true, message: '组件路径不能为空', trigger: 'blur' }
    ]
  } else if (form.menuType === 'F') {
    // 按钮类型
    baseRules.perms = [
      { required: true, message: '权限标识不能为空', trigger: 'blur' }
    ]
  }

  return baseRules
})

/**
 * 检查图标是否有效的Element Plus组件
 */
const isValidIcon = (iconName) => {
  if (!iconName) return false
  if (typeof iconName !== 'string') return false
  if (iconName.includes('#')) return false
  return validElementIcons.value.includes(iconName)
}

/**
 * 折叠所有行
 */
const collapseAllRows = () => {
  setTimeout(() => {
    if (menuTableRef.value && menuTableRef.value.$refs && menuTableRef.value.$refs.table) {
      const table = menuTableRef.value.$refs.table
      if (menuList.value && menuList.value.length > 0) {
        menuList.value.forEach(row => {
          table.toggleRowExpansion(row, false)
        })
      }
    }
  }, 100)
}

/** 查询菜单列表 */
const getList = async () => {
  loading.value = true
  try {
    // 确保查询参数的数据类型正确
    const params = { ...queryParams };
    if (params.status !== undefined && params.status !== null && params.status !== '') {
      params.status = Number(params.status);
    }
    
    const res = await getMenuList(params)
    if (res.code === 200) {
      // 将列表数据转换为树形结构
      menuList.value = convertToTreeData(res.data.rows)
      // 处理菜单选项
      menuOptions.value = [{ menuId: 0, menuName: '主类目', children: convertToTreeData(res.data.rows) }]
    } else {
      menuList.value = []
      menuOptions.value = [{ menuId: 0, menuName: '主类目', children: [] }]
    }
  } finally {
    loading.value = false
  }
}

/**
 * 将扁平数据转换为树状结构
 */
const convertToTreeData = (data) => {
  if (!Array.isArray(data) || data.length === 0) return []
  
  // 创建节点映射
  const nodeMap = new Map()
  
  // 第一步：创建所有节点的映射
  data.forEach(item => {
    // 创建节点的副本，确保不修改原始数据
    nodeMap.set(item.menuId, { ...item, children: [] })
  })
  
  const treeData = []
  
  // 第二步：建立父子关系
  data.forEach(item => {
    const node = nodeMap.get(item.menuId)
    const parentId = item.parentId
    
    if (parentId === 0) {
      // 根节点直接添加到结果数组
      treeData.push(node)
    } else {
      // 非根节点，添加到父节点的children数组
      const parentNode = nodeMap.get(parentId)
      if (parentNode) {
        parentNode.children.push(node)
      } else {
        // 父节点不存在，作为顶级节点处理
        treeData.push(node)
      }
    }
  })
  
  // 递归排序函数
  const sortTree = (nodes) => {
    if (!nodes || nodes.length === 0) return nodes
    
    // 按orderNum排序
    nodes.sort((a, b) => (a.orderNum || 0) - (b.orderNum || 0))
    
    // 递归排序子节点
    nodes.forEach(node => {
      if (node.children && node.children.length > 0) {
        sortTree(node.children)
      }
    })
    
    return nodes
  }
  
  // 返回排序后的树形数据
  return sortTree(treeData)
}

/** 取消按钮 */
const cancel = () => {
  open.value = false
  reset()
}

/** 表单重置 */
const reset = () => {
  form.menuId = undefined
  form.parentId = 0
  form.menuName = undefined
  form.icon = undefined
  form.menuType = 'M'
  form.orderNum = 0
  form.perms = undefined
  form.component = undefined
  form.path = undefined
  form.status = '0'
}

/** 搜索按钮操作 */
const handleQuery = () => {
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryParams.menuName = undefined
  queryParams.status = undefined
  handleQuery()
}

/** 新增按钮操作 */
const handleAdd = (row) => {
  reset()
  getMenuOptions()
  if (row != null && row.menuId) {
    form.parentId = row.menuId
  } else {
    form.parentId = 0
  }
  open.value = true
  title.value = '添加菜单'
}

/** 修改按钮操作 */
const handleUpdate = async (row) => {
  reset()
  const menuId = row.menuId || row.id
  const res = await getMenu(menuId)
  Object.assign(form, res.data)
  getMenuOptions()
  open.value = true
  title.value = '修改菜单'
}

/** 表单提交 */
const menuFormRef = ref(null)

const submitForm = async () => {
  // 表单验证
  await menuFormRef.value.validate()

  // 转换数据类型，确保数字类型正确
  const formData = { ...form };

  // 确保数字类型
  formData.status = Number(formData.status);
  formData.orderNum = Number(formData.orderNum);
  if (formData.parentId !== null && formData.parentId !== undefined) {
    formData.parentId = Number(formData.parentId);
  }
  if (formData.menuId) {
    formData.menuId = Number(formData.menuId);
  }

  const isEdit = formData.menuId !== undefined;
  const apiRequest = isEdit ? updateMenu(formData) : addMenu(formData);

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
  const menuIds = row.menuId || row.id
  ElMessageBox.confirm('是否确认删除名称为"' + row.menuName + '"的数据项?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await delMenu(menuIds)
    getList()
    ElMessage.success('删除成功')
  })
}

/** 获取菜单下拉树列表 */
const getMenuOptions = async () => {
  try {
    const res = await getMenuList()
    let menuData = [];
    
    if (res && res.data) {
      if (Array.isArray(res.data)) {
        menuData = res.data;
      } else if (res.data.rows) {
        menuData = res.data.rows;
      }
    }
    
    // 添加根节点选项
    menuOptions.value = [{ menuId: 0, menuName: '主类目', children: convertToTreeData(menuData) }];
  } catch (error) {
    console.error('获取菜单选项失败:', error);
    menuOptions.value = [{ menuId: 0, menuName: '主类目', children: [] }];
  }
}

/** 显示图标选择 */
const showIconSelect = () => {
  iconSelectVisible.value = true
}

/** 选择图标 */
const selectIcon = (icon) => {
  form.icon = icon
  iconSelectVisible.value = false
}

// 图标搜索
const iconSearchText = ref('')
const filteredIconList = computed(() => {
  if (!iconSearchText.value) {
    return iconList.value
  }
  return iconList.value.filter(icon => 
    icon.toLowerCase().includes(iconSearchText.value.toLowerCase())
  )
})

const handleIconSearch = () => {
  // This function is triggered when the user types in the search field
  // The filtering is handled by the computed property
}

const menuTableRef = ref(null)
const defaultExpandAll = ref(false)

// 展开所有节点
const expandAll = () => {
  defaultExpandAll.value = true
  // 使用 nextTick 确保 DOM 已更新
  nextTick(() => {
    if (menuTableRef.value && menuTableRef.value.$refs && menuTableRef.value.$refs.table) {
      // 获取表格实例并展开所有行
      const table = menuTableRef.value.$refs.table
      menuList.value.forEach(row => {
        table.toggleRowExpansion(row, true)
      })
    }
  })
}

// 折叠所有节点
const collapseAll = () => {
  defaultExpandAll.value = false
  collapseAllRows()
}

const isAllExpanded = computed(() => {
  if (!menuTableRef.value || !menuTableRef.value.$refs || !menuTableRef.value.$refs.table) return false
  const table = menuTableRef.value.$refs.table
  return menuList.value.every(row => table.isRowExpanded(row))
})

const isAllCollapsed = computed(() => {
  if (!menuTableRef.value || !menuTableRef.value.$refs || !menuTableRef.value.$refs.table) return false
  const table = menuTableRef.value.$refs.table
  return menuList.value.every(row => !table.isRowExpanded(row))
})

onMounted(() => {
  getList()
  // 确保初始状态为折叠
  collapseAllRows()
})
</script>

<style scoped>
.menu-container {
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

.search-container {
  margin-bottom: 15px;
  padding: 0 10px;
}

.icon-list {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 10px;
  padding: 10px;
  max-height: 400px;
  overflow-y: auto;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  cursor: pointer;
  border: 1px solid #eee;
  border-radius: 4px;
  transition: all 0.3s;
}

.icon-item:hover {
  background-color: #f5f7fa;
  border-color: #409EFF;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.icon-preview {
  font-size: 24px;
  margin-bottom: 8px;
  color: #333;
}

.icon-name {
  font-size: 12px;
  color: #666;
  word-break: break-all;
  text-align: center;
}

.menu-name {
  display: flex;
  align-items: center;
  padding: 6px 0;
}

.menu-icon {
  margin-right: 8px;
  font-size: 16px;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-type-tag {
  margin-left: 8px;
  font-weight: normal;
}

.table-icon {
  font-size: 18px;
}

/* 表格整体样式 */
:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

:deep(.el-table__header-wrapper th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
  padding: 10px 0;
}

:deep(.el-table__header-wrapper th.el-table__cell) {
  background-color: #f5f7fa;
}

:deep(.el-table--border) {
  border: 1px solid #EBEEF5;
}

/* 美化菜单类型标签 */
.menu-type-tag.el-tag--success {
  background-color: rgba(103, 194, 58, 0.1);
  border-color: rgba(103, 194, 58, 0.2);
  color: #67C23A;
}

.menu-type-tag.el-tag--primary {
  background-color: rgba(64, 158, 255, 0.1);
  border-color: rgba(64, 158, 255, 0.2);
  color: #409EFF;
}

.menu-type-tag.el-tag--warning {
  background-color: rgba(230, 162, 60, 0.1);
  border-color: rgba(230, 162, 60, 0.2);
  color: #E6A23C;
}

/* 表格层次样式 */
:deep(.el-table .el-table__row--level-0) {
  font-weight: 600;
  font-size: 14px;
  background-color: #f5f7fa;
  border-left: 3px solid #409EFF;
}

:deep(.el-table .el-table__row--level-1) {
  background-color: #fafbfc;
  border-left: 2px solid #67C23A;
}

:deep(.el-table .el-table__row--level-2) {
  background-color: #ffffff;
  border-left: 2px solid #E6A23C;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #ecf5ff !important;
}

:deep(.el-table__indent) {
  padding-left: 20px !important;
}

:deep(.el-table__expand-icon) {
  font-size: 16px;
  color: #409EFF;
  transition: transform 0.15s ease-in-out;
  margin-right: 5px;
}

:deep(.el-table__expand-icon--expanded) {
  transform: rotate(90deg);
}

:deep(.el-table__row) {
  transition: background-color 0.3s ease;
  border-bottom: 1px solid #EBEEF5;
  height: 50px;
}

:deep(.el-table__row.expanded) {
  background-color: #f0f9ff;
}

/* 不同级别菜单的名称样式 */
:deep(.el-table__row--level-0) .menu-name {
  font-weight: 600;
  font-size: 14px;
}

:deep(.el-table__row--level-1) .menu-name {
  font-weight: 500;
  font-size: 13px;
}

:deep(.el-table__row--level-2) .menu-name {
  font-weight: normal;
  font-size: 13px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.icon-input-container {
  display: flex;
  align-items: center;
  width: 100%;
}

.icon-preview-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
}

.form-icon-preview {
  font-size: 18px;
  color: #333;
}

/* 调整表格单元格内边距 */
:deep(.el-table .el-table__cell) {
  padding: 8px 0;
}

/* 增加第一列边距 */
:deep(.el-table .el-table__cell:first-child) {
  padding-left: 5px;
}

/* 美化操作按钮区域 */
:deep(.el-button--text) {
  padding: 4px 8px;
  margin-right: 2px;
  border-radius: 4px;
}

:deep(.el-button--text:hover) {
  background-color: rgba(64, 158, 255, 0.1);
}
</style>
