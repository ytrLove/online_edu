<template>
  <div class="course-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>课程列表</span>
          <div class="header-actions">
            <el-button v-permission="'edu:course:add'" type="primary" plain @click="handleAdd">
              <el-icon><Plus /></el-icon>新增课程
            </el-button>
            <el-button type="warning" plain @click="fetchCourseList">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <search-form :form-model="searchQuery" @search="handleSearch" @reset="resetSearch">
        <el-form-item label="课程名称">
          <el-input v-model="searchQuery.keyword" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="课程分类">
          <el-select v-model="searchQuery.subjectId" placeholder="课程分类" clearable>
            <el-option label="全部分类" value=""></el-option>
            <el-option 
              v-for="item in subjectList" 
              :key="item.subjectId" 
              :label="item.subjectName" 
              :value="item.subjectId">
            </el-option>
          </el-select>
        </el-form-item>
      </search-form>

      <!-- 表格区域 -->
      <data-table
        :data="courseList"
        :loading="loading"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        @pagination="handlePagination"
        @update:current-page="val => currentPage = val"
        @update:page-size="val => pageSize = val"
        border
      >
        <el-table-column label="课程信息" min-width="300">
          <template #default="{ row }">
            <div class="course-info-cell">
              <el-image :src="row.coverImage || defaultCover" class="course-cover" fit="cover"></el-image>
              <div class="course-details">
                <div class="course-name">{{ row.courseName }}</div>
                <div class="course-code">课程代码: {{ row.courseCode }}</div>
                <div class="course-category">{{ getSubjectName(row.subjectId) }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="teacherName" label="教师" width="120">
          <template #default="{ row }">
            {{ row.teacherName || '暂无' }}
          </template>
        </el-table-column>
        <el-table-column prop="studentCount" label="学生数" width="100" align="center"></el-table-column>
        <el-table-column label="课程状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'published' ? 'success' : 'info'">
              {{ row.status === 'published' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="选课状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getEnrollmentStatusType(row.enrollmentStatus)">
              {{ getEnrollmentStatusText(row.enrollmentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="选课时间" min-width="240">
          <template #default="{ row }">
            <div v-if="row.enrollmentStartTime || row.enrollmentEndTime">
              <div><el-icon><Calendar /></el-icon> 开始：{{ formatDateTime(row.enrollmentStartTime) }}</div>
              <div><el-icon><Calendar /></el-icon> 结束：{{ formatDateTime(row.enrollmentEndTime) }}</div>
            </div>
            <span v-else>未设置</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="viewCourseDetail(row.courseId)">
              <el-icon><View /></el-icon>查看
            </el-button>
            <el-button v-permission="'edu:course:edit'" type="warning" size="small" text @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button v-permission="'edu:course:remove'" type="danger" size="small" text @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </data-table>
    </el-card>

    <!-- 课程详情对话框 -->
    <el-dialog
      :model-value="showDetailModal"
      @update:model-value="showDetailModal = $event"
      title="课程详情"
      width="700px"
      append-to-body
    >
      <div v-if="courseDetail" class="course-detail">
        <div class="detail-header">
          <el-image :src="courseDetail.coverImage || defaultCover" class="detail-cover" fit="cover"></el-image>
          <div class="detail-info">
            <h2>{{ courseDetail.courseName }}</h2>
            <p class="detail-code">课程代码：{{ courseDetail.courseCode }}</p>
            <p class="detail-teacher">授课教师：
              <span v-if="courseDetail.teachers && courseDetail.teachers.length > 0">
                {{ courseDetail.teachers.map(t => t.teacherName).join(', ') }}
              </span>
              <span v-else>暂无教师信息</span>
            </p>
            <p class="detail-subject">所属分类：{{ getSubjectName(courseDetail.subjectId) }}</p>
            <p class="detail-status">
              课程状态：
              <el-tag :type="courseDetail.status === 'published' ? 'success' : 'info'">
                {{ courseDetail.status === 'published' ? '正常' : '停用' }}
              </el-tag>
            </p>
            <p class="detail-enrollment-status">
              选课状态：
              <el-tag :type="getEnrollmentStatusType(courseDetail.enrollmentStatus)">
                {{ getEnrollmentStatusText(courseDetail.enrollmentStatus) }}
              </el-tag>
            </p>
            <p class="detail-enrollment-time" v-if="courseDetail.enrollmentStartTime || courseDetail.enrollmentEndTime">
              选课时间：{{ formatDateTime(courseDetail.enrollmentStartTime) }} ~ {{ formatDateTime(courseDetail.enrollmentEndTime) }}
            </p>
          </div>
        </div>
        <div class="detail-description">
          <h3>课程简介</h3>
          <p>{{ courseDetail.description || '暂无课程简介' }}</p>
        </div>
        <div class="detail-description">
          <h3>课程目标</h3>
          <p>{{ courseDetail.objectives || '暂无课程目标' }}</p>
        </div>
        <div class="detail-description">
          <h3>课程大纲</h3>
          <p>{{ courseDetail.syllabus || '暂无课程大纲' }}</p>
        </div>
        <div class="detail-meta">
          <div class="meta-item">
            <span class="meta-label">创建时间：</span>
            <span>{{ formatDateTime(courseDetail.createTime) }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">更新时间：</span>
            <span>{{ formatDateTime(courseDetail.updateTime) }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">学生数量：</span>
            <span>{{ courseDetail.studentCount || 0 }}</span>
          </div>
        </div>
        <div class="detail-students" v-if="showStudentList && courseDetail">
          <h3>选课学生名单</h3>
          <el-table
            :data="studentList"
            v-loading="loadingStudents"
            border
            stripe
            style="width: 100%"
          >
            <el-table-column prop="student_no" label="学号" width="120" />
            <el-table-column prop="username" label="姓名" width="120" />
            <el-table-column prop="major" label="专业" width="150" />
            <el-table-column prop="enrollment_date" label="选课时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.enrollment_date) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.student_status === 'active' ? 'success' : 'danger'">
                  {{ row.student_status === 'active' ? '在修' : '已退选' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="studentList.length === 0 && !loadingStudents" class="no-data">
            <el-empty description="暂无选课学生" />
          </div>
        </div>
        <div class="detail-actions">
          <el-button v-permission="'edu:course:students'" type="info" @click="toggleStudentList">
            {{ showStudentList ? '隐藏学生名单' : '查看学生名单' }}
          </el-button>
          <el-button type="primary" @click="handleEnroll(courseDetail)">选课</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 选课确认对话框 -->
    <el-dialog
      :model-value="showEnrollModal"
      @update:model-value="showEnrollModal = $event"
      title="选课确认"
      width="500px"
      append-to-body
    >
      <div v-if="currentCourse" class="enroll-confirm">
        <p>您确定要选择 <strong>{{ currentCourse.courseName }}</strong> 这门课程吗？</p>
        <p>教师: {{ currentCourse.mainTeacherName }}</p>
        <p>课程代码: {{ currentCourse.courseCode }}</p>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showEnrollModal = false">取消</el-button>
          <el-button type="primary" @click="confirmEnroll">确认选课</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑课程对话框 -->
    <el-dialog
      :model-value="showEditModal"
      @update:model-value="showEditModal = $event"
      :title="formTitle"
      width="650px"
      append-to-body
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程代码" />
        </el-form-item>
        <el-form-item label="课程分类" prop="subjectId">
          <el-select v-model="form.subjectId" placeholder="请选择课程分类" style="width: 100%">
            <el-option 
              v-for="item in subjectList" 
              :key="item.subjectId" 
              :label="item.subjectName" 
              :value="item.subjectId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程封面" prop="coverImage">
          <image-uploader
            v-model="form.coverImage"
            :imageUrl="form.coverImage" 
            :max-size="2"
            :tip="'建议上传16:9比例图片，大小不超过2MB'"
            @success="handleUploadSuccess"
            @remove="handleDeleteFile"
          />
        </el-form-item>
        <el-form-item label="课程简介" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="3"
            placeholder="请输入课程简介"
          />
        </el-form-item>
        <el-form-item label="课程目标" prop="objectives">
          <el-input
            v-model="form.objectives"
            type="textarea"
            rows="3"
            placeholder="请输入课程目标"
          />
        </el-form-item>
        <el-form-item label="课程大纲" prop="syllabus">
          <el-input
            v-model="form.syllabus"
            type="textarea"
            rows="4"
            placeholder="请输入课程大纲"
          />
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherIds">
          <el-select v-model="form.teacherIds" placeholder="请选择授课教师" style="width: 100%" multiple>
            <el-option 
              v-for="item in teacherList" 
              :key="item.teacherId" 
              :label="item.nickname" 
              :value="item.teacherId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课程状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="'published'">正常</el-radio>
            <el-radio :label="'draft'">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选课开始时间" prop="enrollmentStartTime">
          <el-date-picker
            v-model="form.enrollmentStartTime"
            type="datetime"
            placeholder="选择选课开始时间"
          />
        </el-form-item>
        <el-form-item label="选课结束时间" prop="enrollmentEndTime">
          <el-date-picker
            v-model="form.enrollmentEndTime"
            type="datetime"
            placeholder="选择选课结束时间"
          />
        </el-form-item>
        <el-form-item label="选课状态" prop="enrollmentStatus">
          <el-select v-model="form.enrollmentStatus" placeholder="选择选课状态">
            <el-option :label="'即将开始'" :value="'upcoming'"></el-option>
            <el-option :label="'开放中'" :value="'open'"></el-option>
            <el-option :label="'已结束'" :value="'closed'"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showEditModal = false">取消</el-button>
          <el-button v-permission="form.courseId ? 'edu:course:edit' : 'edu:course:add'" type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, View, Plus, Edit, Delete, Refresh, Calendar } from '@element-plus/icons-vue';
import { getCourseList, getCourseById, addCourse, updateCourse, deleteCourse, uploadCourseCover, getCourseStudents, getTeacherList } from '@/api';
import { getSubjectList } from '@/api/edu/subject';
import SearchForm from '@/components/SearchForm/index.vue';
import DataTable from '@/components/DataTable/index.vue';
import ImageUploader from '@/components/ImageUploader.vue';
import { deleteFile } from '@/api/common/new_upload';

const router = useRouter();

// 默认封面
const defaultCover = 'https://public.readdy.ai/ai/img_res/8408ffef3a428f32e95f3d72c98bf6d8.jpg';

// 加载状态
const loading = ref(false);
// 课程列表
const courseList = ref([]);
// 分类列表
const subjectList = ref([]);
// 搜索参数
const searchQuery = reactive({
  keyword: '',
  subjectId: '',
  sort: 'createTime' // 默认按创建时间排序
});
// 分页参数
const currentPage = ref(1);
const pageSize = ref(6); // 一页显示10个课程
const total = ref(0);
// 对话框参数
const showDetailModal = ref(false);
const showEnrollModal = ref(false);
const courseDetail = ref(null);
const currentCourse = ref(null);

// 在此后添加新变量
const showEditModal = ref(false);
const formTitle = ref('');
const formRef = ref(null);

// 表单数据
const form = reactive({
  courseId: undefined,
  courseName: '',
  courseCode: '',
  subjectId: '',
  teacherIds: [],
  coverImage: '',
  description: '',
  objectives: '',
  syllabus: '',
  status: 'published',
  enrollmentStartTime: null,
  enrollmentEndTime: null,
  enrollmentStatus: 'upcoming'  // 默认为"即将开始"
});

// 教师列表数据
const teacherList = ref([]);

// 表单验证规则
const rules = {
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  courseCode: [
    { required: true, message: '请输入课程代码', trigger: 'blur' }
  ],
  subjectId: [
    { required: true, message: '请选择课程分类', trigger: 'change' }
  ],
  teacherIds: [
    { required: true, message: '请选择授课教师', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择课程状态', trigger: 'change' }
  ],
  enrollmentEndTime: [
    { 
      validator: (rule, value, callback) => {
        if (form.enrollmentStartTime && value && new Date(value) <= new Date(form.enrollmentStartTime)) {
          callback(new Error('结束时间必须晚于开始时间'));
        } else {
          callback();
        }
      }, 
      trigger: 'change' 
    }
  ]
};

// 添加学生名单相关变量
const showStudentList = ref(false);
const loadingStudents = ref(false);
const studentList = ref([]);

/**
 * 获取选课状态文本
 */
const getEnrollmentStatusText = (status) => {
  if (!status) return '未设置';
  
  const statusMap = {
    'upcoming': '即将开始',
    'open': '开放中',
    'closed': '已结束'
  };
  
  return statusMap[status] || '未知';
};

/**
 * 获取选课状态标签类型
 */
const getEnrollmentStatusType = (status) => {
  if (!status) return 'info';
  
  const typeMap = {
    'upcoming': 'warning',
    'open': 'success',
    'closed': 'danger'
  };
  
  return typeMap[status] || 'info';
};

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return '未设置';
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
 * 获取分类名称
 */
const getSubjectName = (subjectId) => {
  const subject = subjectList.value.find(item => item.subjectId === subjectId);
  return subject ? subject.subjectName : '未分类';
};

/**
 * 获取课程列表
 */
const fetchCourseList = async () => {
  try {
    loading.value = true;
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.keyword,
      subjectId: searchQuery.subjectId
    };
    
    const res = await getCourseList(params);
    courseList.value = res.data.records || [];
    total.value = res.data.total || 0;
  } catch (error) {
    console.error('获取课程列表失败', error);
    ElMessage.error('获取课程列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取老师列表
const fetchTeacherList = async () => {
  try {
    const res = await getTeacherList();
    teacherList.value = res.data.records || [];
  } catch (error) {
    console.error('获取老师列表失败', error);
    ElMessage.error('获取老师列表失败');
  }
};

/**
 * 获取分类列表
 */
const fetchSubjectList = async () => {
  try {
    const res = await getSubjectList();
    subjectList.value = res.data || [];
  } catch (error) {
    console.error('获取课程分类列表失败', error);
    ElMessage.error('获取课程分类列表失败');
  }
};

/**
 * 搜索处理
 */
const handleSearch = () => {
  currentPage.value = 1;
  fetchCourseList();
};

/**
 * 重置搜索
 */
const resetSearch = () => {
  searchQuery.keyword = '';
  searchQuery.subjectId = '';
  currentPage.value = 1;
  fetchCourseList();
};

/**
 * 分页查询
 */
const handlePagination = (params) => {
  currentPage.value = params.page;
  pageSize.value = params.limit;
  fetchCourseList();
};

/**
 * 查看课程详情
 */
const viewCourseDetail = async (courseId) => {
  try {
    const res = await getCourseById(courseId);
    courseDetail.value = res.data;
    showDetailModal.value = true;
  } catch (error) {
    console.error('获取课程详情失败', error);
    ElMessage.error('获取课程详情失败');
  }
};

/**
 * 处理选课
 */
const handleEnroll = (course) => {
  currentCourse.value = course;
  showEnrollModal.value = true;
  // 如果从详情页打开，关闭详情页
  if (showDetailModal.value) {
    showDetailModal.value = false;
  }
};

/**
 * 确认选课
 */
const confirmEnroll = async () => {
  try {
    // 这里应该调用选课API
    // await enrollCourse(currentCourse.value.courseId);
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500));
    
    ElMessage.success('选课成功');
    showEnrollModal.value = false;
  } catch (error) {
    console.error('选课失败', error);
    ElMessage.error('选课失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 新增课程
 */
const handleAdd = () => {
  resetForm();
  formTitle.value = '新增课程';
  showEditModal.value = true;
};

/**
 * 编辑课程
 */
const handleEdit = (row) => {
    resetForm();
    formTitle.value = '编辑课程';
  currentCourse.value = row;
  
  // 将课程数据复制到表单对象
  form.courseId = row.courseId;
  form.courseName = row.courseName;
  form.courseCode = row.courseCode || '';
  form.subjectId = row.subjectId;
  form.coverImage = row.coverImage || '';
  form.description = row.description || '';
  form.objectives = row.objectives || '';
  form.syllabus = row.syllabus || '';
  form.status = row.status || 'published';
  form.enrollmentStartTime = row.enrollmentStartTime || null;
  form.enrollmentEndTime = row.enrollmentEndTime || null;
  form.enrollmentStatus = row.enrollmentStatus || 'upcoming';
  
  // 设置教师数据
  if (row.teachers && row.teachers.length > 0) {
    form.teacherIds = row.teachers.map(t => t.teacherId);
  } else if (row.teacherIds && row.teacherIds.length > 0) {
    // 使用后端返回的teacherIds数组
    form.teacherIds = row.teacherIds;
  } else if (row.teacherId) {
    // 兼容旧数据格式
    form.teacherIds = [Number(row.teacherId)];
  } else {
    form.teacherIds = [];
  }
  
  // 调试日志
  console.log('编辑课程数据:', row);
  console.log('设置的教师IDs:', form.teacherIds);
  
  showEditModal.value = true;
};

/**
 * 删除课程
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除课程 "${row.courseName}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCourse(row.courseId);
      ElMessage.success('删除成功');
      fetchCourseList();
    } catch (error) {
      console.error('删除课程失败', error);
      ElMessage.error('删除课程失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {});
};

/**
 * 重置表单
 */
const resetForm = () => {
  form.courseId = undefined;
  form.courseName = '';
  form.courseCode = '';
  form.subjectId = '';
  form.teacherIds = [];
  form.coverImage = '';
  form.description = '';
  form.objectives = '';
  form.syllabus = '';
  form.status = 'published';
  form.enrollmentStartTime = null;
  form.enrollmentEndTime = null;
  form.enrollmentStatus = 'upcoming';
};

/**
 * 计算选课状态
 * 根据开始时间和结束时间自动计算选课状态
 */
const updateEnrollmentStatus = () => {
  const now = new Date();
  const startTime = form.enrollmentStartTime ? new Date(form.enrollmentStartTime) : null;
  const endTime = form.enrollmentEndTime ? new Date(form.enrollmentEndTime) : null;
  
  if (!startTime || !endTime) {
    form.enrollmentStatus = 'upcoming';
    return;
  }
  
  if (now < startTime) {
    form.enrollmentStatus = 'upcoming';  // 未开始
  } else if (now >= startTime && now <= endTime) {
    form.enrollmentStatus = 'open';  // 开放中
  } else {
    form.enrollmentStatus = 'closed';  // 已结束
  }
};

/**
 * 提交表单
 */
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true;
        
        // 更新选课状态
        updateEnrollmentStatus();
        
        const isEdit = form.courseId !== undefined;
        
        // 构建提交数据
        const submitData = { ...form };
        
        if (isEdit) {
          await updateCourse(submitData);
          ElMessage.success('课程更新成功');
        } else {
          await addCourse(submitData);
          ElMessage.success('课程添加成功');
        }
        
        showEditModal.value = false;
        fetchCourseList();
      } catch (error) {
        console.error('保存课程失败', error);
        ElMessage.error('保存课程失败：' + (error.message || '未知错误'));
      } finally {
        loading.value = false;
      }
    }
  });
};

/**
 * 封面上传成功
 */
const handleUploadSuccess = (result) => {
  form.coverImage = result.url || result.response?.url || '';
  ElMessage.success('上传成功');
};

const handleDeleteFile =  (data) => { 
  console.log(form.coverImage);

};

/**
 * 切换学生名单显示
 */
const toggleStudentList = async () => {
  showStudentList.value = !showStudentList.value;
  
  if (showStudentList.value && courseDetail.value && studentList.value.length === 0) {
    await fetchStudentList(courseDetail.value.courseId);
  }
};

/**
 * 获取课程学生名单
 */
const fetchStudentList = async (courseId) => {
  if (!courseId) return;
  
  try {
    loadingStudents.value = true;
    
    // 调用获取学生名单API
    const res = await getCourseStudents(courseId);
    studentList.value = res.data || [];
    
    loadingStudents.value = false;
  } catch (error) {
    console.error('获取学生名单失败', error);
    ElMessage.error('获取学生名单失败: ' + (error.message || '未知错误'));
    loadingStudents.value = false;
  }
};

// 页面初始化
onMounted(() => {
  fetchTeacherList();
  fetchSubjectList();
  fetchCourseList();
});
</script>

<style scoped>
.course-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-info-cell {
  display: flex;
  align-items: center;
  gap: 16px;
}

.course-cover {
  width: 80px;
  height: 45px;
  border-radius: 4px;
  object-fit: cover;
  border: 1px solid #eee;
}

.course-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}

.course-code, .course-category {
  font-size: 12px;
  color: #666;
}

.rating-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.detail-header {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.detail-cover {
  width: 240px;
  height: 135px;
  border-radius: 6px;
  object-fit: cover;
  border: 1px solid #eee;
}

.detail-info {
  flex: 1;
}

.detail-info h2 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #303133;
}

.detail-code, .detail-teacher, .detail-subject, .detail-status {
  margin: 8px 0;
  color: #606266;
}

.detail-description {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.detail-description h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}

.detail-description p {
  color: #606266;
  line-height: 1.6;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 30px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-label {
  font-weight: 600;
  color: #606266;
  margin-right: 5px;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.enroll-confirm {
  padding: 10px 0;
}

.enroll-confirm p {
  margin: 10px 0;
  color: #606266;
}

.enroll-confirm strong {
  color: #303133;
}

.detail-students {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.detail-students h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #303133;
}

.no-data {
  padding: 20px 0;
}
</style>
