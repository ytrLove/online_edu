<template>
  <div class="mycourse-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="进行中" value="published" />
            <el-option label="草稿" value="draft" />
            <el-option label="结束" value="closed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 课程卡片展示区域 -->
      <div v-loading="loading" class="course-list">
        <el-empty v-if="courseList.length === 0" description="暂无课程" />
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in courseList" :key="course.courseId">
            <el-card class="course-card" shadow="hover">
              <div class="course-cover">
                <el-image 
                  :src="course.coverImage || defaultCover" 
                  fit="cover"
                  :preview-src-list="[course.coverImage || defaultCover]"
                >
                  <template #error>
                    <div class="image-placeholder">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div class="course-status">
                  <el-tag :type="getStatusTag(course.status)" size="small">
                    {{ getStatusLabel(course.status) }}
                  </el-tag>
                </div>
              </div>
              <div class="course-info">
                <h3 class="course-name">{{ course.courseName }}</h3>
                <div class="course-meta">
                  <span>学生数: {{ course.studentCount || 0 }}</span>
                  <span>{{ formatDate(course.enrollmentStartTime) }} ~ {{ formatDate(course.enrollmentEndTime) }}</span>
                </div>
                <div class="course-desc">{{ course.description || '暂无描述' }}</div>
              </div>
              <div class="course-actions">
                <el-button type="primary" @click="openCourseDetail(course)">
                  课程管理
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          :current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :page-sizes="[8, 12, 24, 36]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 课程详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      :title="currentCourse.courseName || '课程详情'" 
      width="80%" 
      :before-close="handleCloseDialog"
      class="course-detail-dialog"
    >
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="课程信息" name="info">
          <div class="course-detail-info">
            <div class="detail-item">
              <h4>课程名称：</h4>
              <p>{{ currentCourse.courseName }}</p>
            </div>
            <div class="detail-item">
              <h4>课程描述：</h4>
              <p>{{ currentCourse.description || '暂无描述' }}</p>
            </div>
            <div class="detail-item">
              <h4>开始时间：</h4>
              <p>{{ formatDate(currentCourse.enrollmentStartTime) }}</p>
            </div>
            <div class="detail-item">
              <h4>结束时间：</h4>
              <p>{{ formatDate(currentCourse.enrollmentEndTime) }}</p>
            </div>
            <div class="detail-item">
              <h4>学生数量：</h4>
              <p>{{ currentCourse.studentCount || 0 }}</p>
            </div>
            <div class="detail-item">
              <h4>课程状态：</h4>
              <p>
                <el-tag :type="getStatusTag(currentCourse.status)">
                  {{ getStatusLabel(currentCourse.status) }}
                </el-tag>
              </p>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="资源管理" name="resource">
          <div class="tab-container">
            <!-- 资源管理组件 -->
            <div class="resource-section">
              <div class="section-header">
                <h3>课程资源列表</h3>
                <el-button type="primary" @click="openCreateResourceDialog">上传资源</el-button>
              </div>
              <el-table :data="resourceList" border stripe style="width: 100%" v-loading="resourceLoading">
                <el-table-column label="资源信息" min-width="280">
                  <template #default="scope">
                    <div class="resource-info">
                      <div class="resource-icon">
                        <el-icon v-if="isDocumentType(scope.row.resourceTypeId)"><Document /></el-icon>
                        <el-icon v-else-if="isVideoType(scope.row.resourceTypeId)"><VideoPlay /></el-icon>
                        <el-icon v-else-if="isImageType(scope.row.resourceTypeId)"><Picture /></el-icon>
                        <el-icon v-else-if="isAudioType(scope.row.resourceTypeId)"><Headset /></el-icon>
                        <el-icon v-else><Files /></el-icon>
                      </div>
                      <div class="resource-detail">
                        <div class="resource-name">{{ scope.row.resourceName }}</div>
                        <div class="resource-description">{{ scope.row.description || '暂无描述' }}</div>
                        <div class="resource-meta">
                          <span class="meta-item">
                            <el-tag size="small" :type="getResourceTypeTagById(scope.row.resourceTypeId)">
                              {{ getResourceTypeLabelById(scope.row.resourceTypeId) }}
                            </el-tag>
                          </span>
                          <span class="meta-item">大小: {{ formatFileSize(scope.row.fileSize) }}</span>
                          <span class="meta-item">下载: {{ scope.row.downloadCount || 0 }} 次</span>
                        </div>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="上传时间" width="180">
                  <template #default="scope">
                    {{ formatResourceDateTime(scope.row.createTime) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="250" fixed="right">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="handleDownloadResource(scope.row)">下载</el-button>
                    <el-button size="small" type="info" @click="handlePreviewResource(scope.row)">
                      {{ canDirectPreview(scope.row) ? '预览' : '查看' }}
                    </el-button>
                    <el-button size="small" type="warning" @click="handleEditResource(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteResource(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="题库管理" name="question">
          <div class="tab-container">
            <!-- 题库管理组件 -->
            <div class="question-section">
              <div class="section-header">
                <h3>课程题库列表</h3>
                <el-button type="primary" @click="handleQuestionAction">添加试题</el-button>
              </div>
              <el-form :inline="true" :model="questionQueryParams" class="question-search-form">
                <el-form-item label="试题内容">
                  <el-input v-model="questionQueryParams.questionContent" placeholder="请输入试题内容关键字" clearable @keyup.enter="searchQuestions" />
                </el-form-item>
                <el-form-item label="题目类型">
                  <el-select v-model="questionQueryParams.questionTypeId" placeholder="请选择题目类型" clearable>
                    <el-option 
                      v-for="item in questionTypeOptions" 
                      :key="item.typeId" 
                      :label="item.typeName" 
                      :value="item.typeId"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="searchQuestions">搜索</el-button>
                  <el-button @click="resetQuestionQuery">重置</el-button>
                </el-form-item>
              </el-form>
              <el-table :data="questionList" border stripe style="width: 100%" v-loading="questionLoading">
                <el-table-column type="index" label="#" width="50" align="center" />
                <el-table-column prop="questionContent" label="试题内容" min-width="250" show-overflow-tooltip>
                  <template #default="scope">
                    <div v-html="formatQuestionContent(scope.row.questionContent)"></div>
                  </template>
                </el-table-column>
                <el-table-column prop="typeName" label="题目类型" width="100" align="center" />
                <el-table-column prop="difficulty" label="难度" width="80" align="center">
                  <template #default="scope">
                    <el-tag :type="getDifficultyTag(scope.row.difficulty)">
                      {{ getDifficultyLabel(scope.row.difficulty) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="170" />
                <el-table-column label="操作" width="220" align="center" fixed="right">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="viewQuestion(scope.row)">预览</el-button>
                    <el-button size="small" type="warning" @click="handleEditQuestion(scope.row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="deleteQuestion(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <!-- 分页 -->
              <div class="pagination-container">
                <el-pagination
                  :current-page="questionQueryParams.pageNum"
                  :page-size="questionQueryParams.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  :total="questionTotal"
                  layout="total, sizes, prev, pager, next, jumper"
                  @size-change="handleQuestionSizeChange"
                  @current-change="handleQuestionCurrentChange"
                />
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="考试管理" name="exam">
          <div class="tab-container">
            <!-- 考试管理组件 -->
            <div class="exam-section">
              <div class="section-header">
                <h3>考试列表</h3>
                <el-button type="primary" @click="handleExamAction">创建考试</el-button>
              </div>
              
              <!-- 搜索区域 -->
              <el-form :inline="true" :model="examQueryParams" class="exam-search-form">
                <el-form-item label="考试名称">
                  <el-input v-model="examQueryParams.title" placeholder="请输入考试名称" clearable @keyup.enter="loadExamList" />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="examQueryParams.status" placeholder="请选择状态" clearable>
                    <el-option label="草稿" :value="0" />
                    <el-option label="已发布" :value="1" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadExamList">搜索</el-button>
                  <el-button @click="() => { examQueryParams.title = ''; examQueryParams.status = undefined; loadExamList(); }">重置</el-button>
                </el-form-item>
              </el-form>
              
              <!-- 考试列表 -->
              <el-table :data="examList" border stripe style="width: 100%" v-loading="examLoading">
                <el-table-column type="index" label="#" width="50" align="center" />
                <el-table-column prop="title" label="考试名称" min-width="150" show-overflow-tooltip />
                <el-table-column prop="totalPoints" label="总分" width="80" align="center" />
                <el-table-column prop="duration" label="答题时长" width="100" align="center">
                  <template #default="scope">
                    {{ scope.row.duration }} 分钟
                  </template>
                </el-table-column>
                <el-table-column label="考试时间" width="200" align="center">
                  <template #default="scope">
                    {{ scope.row.startTime }} 至 {{ scope.row.endTime }}
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                      {{ scope.row.status === 1 ? '已发布' : '草稿' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="280" align="center">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="viewExam(scope.row)">预览</el-button>
                    <el-button size="small" type="warning" @click="editExam(scope.row)">编辑</el-button>
                    <el-button size="small" type="info" @click="associateExamQuestions(scope.row)">关联题目</el-button>
                    <el-button
                      size="small"
                      v-if="scope.row.status === 0"
                      type="success"
                      @click="publishExam(scope.row)"
                    >发布</el-button>
                    <el-button
                      size="small"
                      v-if="scope.row.status === 1"
                      type="info"
                      @click="unpublishExam(scope.row)"
                    >下架</el-button>
                    <el-button size="small" type="danger" @click="deleteExam(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              
              <!-- 分页 -->
              <div class="pagination-container">
                <el-pagination
                  :current-page="examQueryParams.pageNum"
                  :page-size="examQueryParams.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  :total="examTotal"
                  layout="total, sizes, prev, pager, next, jumper"
                  @size-change="handleExamSizeChange"
                  @current-change="handleExamCurrentChange"
                />
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="作业管理" name="assignment">
          <div class="tab-container">
            <!-- 作业管理组件 -->
            <div class="assignment-section">
              <div class="section-header">
                <h3>作业列表</h3>
                <el-button type="primary" @click="openCreateAssignmentDialog">发布作业</el-button>
              </div>
              
              <!-- 作业列表 -->
              <el-table :data="assignmentList" border stripe style="width: 100%" v-loading="assignmentLoading">
                <el-table-column type="index" label="#" width="50" align="center" />
                <el-table-column prop="title" label="作业标题" min-width="100" show-overflow-tooltip />
                <el-table-column prop="totalPoints" label="总分" width="80" align="center" />
                <el-table-column label="状态" width="100" align="center">
                  <template #default="scope">
                    <el-tag :type="getAssignmentStatusTag(scope.row.status)">
                      {{ getAssignmentStatusLabel(scope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="startTime" label="发布时间" width="160" align="center" show-overflow-tooltip />
                <el-table-column prop="dueTime" label="截止时间" width="160" align="center" show-overflow-tooltip />
                <el-table-column label="操作" width="250" align="center">
                  <template #default="scope">
                    <el-button size="small" type="primary" @click="viewAssignment(scope.row)">详情</el-button>
                    <el-button size="small" type="warning" @click="editAssignment(scope.row)">编辑</el-button>
                    <el-button size="small" type="info" @click="assignQuestions(scope.row)">关联题目</el-button>
                    <el-button size="small" type="danger" @click="deleteAssignment(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              
              <!-- 分页 -->
              <div class="pagination-container">
                <el-pagination
                  :current-page="assignmentQueryParams.pageNum"
                  :page-size="assignmentQueryParams.pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  :total="assignmentTotal"
                  layout="total, sizes, prev, pager, next, jumper"
                  @size-change="handleAssignmentSizeChange"
                  @current-change="handleAssignmentCurrentChange"
                />
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="讨论区管理" name="discussion">
          <div class="tab-container">
            <!-- 讨论区管理 -->
            <div class="discussion-section">
              <div class="section-header">
                <h3>讨论区列表</h3>
                <el-button type="primary" @click="openCreateDiscussionDialog">创建讨论区</el-button>
              </div>
              <el-table :data="discussionList" border stripe style="width: 100%">
                <el-table-column prop="forumName" label="讨论区标题" min-width="150"></el-table-column>
                <el-table-column prop="description" label="讨论区描述" min-width="200"></el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
                <el-table-column label="操作" width="200">
                  <template #default="scope">
                    <el-button size="small" @click="handleViewDiscussion(scope.row)">查看</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteDiscussion(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- 创建讨论区对话框 -->
    <el-dialog 
      v-model="createDiscussionDialogVisible" 
      title="创建讨论区" 
      width="50%"
      :before-close="handleCloseDiscussionDialog"
    >
      <el-form :model="discussionForm" label-width="100px" :rules="discussionRules" ref="discussionFormRef">
        <el-form-item label="讨论区标题" prop="forumName">
          <el-input v-model="discussionForm.forumName" placeholder="请输入讨论区标题"></el-input>
        </el-form-item>
        <el-form-item label="讨论区描述" prop="description">
          <el-input 
            v-model="discussionForm.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入讨论区描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="是否允许学生创建主题">
          <el-switch v-model="discussionForm.allowStudentTopic"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseDiscussionDialog">取消</el-button>
          <el-button type="primary" @click="handleCreateDiscussion">确认</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 上传/编辑资源对话框 -->
    <el-dialog 
      v-model="createResourceDialogVisible" 
      :title="isEditMode ? '编辑资源' : '上传资源'" 
      width="50%"
      :before-close="handleCloseResourceDialog"
    >
      <el-form :model="resourceForm" label-width="100px" :rules="resourceRules" ref="resourceFormRef">
        <el-form-item label="资源名称" prop="resourceName">
          <el-input v-model="resourceForm.resourceName" placeholder="请输入资源名称"></el-input>
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input 
            v-model="resourceForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入资源描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="资源类型" prop="resourceType">
          <el-select v-model="resourceForm.resourceType" placeholder="请选择资源类型">
            <el-option 
              v-for="type in resourceTypeList" 
              :key="type.typeId" 
              :label="type.typeName" 
              :value="type.typeId"
            ></el-option>
            <!-- 后备选项，当API加载失败时显示 -->
            <el-option v-if="resourceTypeList.length === 0" label="文档" value="document"></el-option>
            <el-option v-if="resourceTypeList.length === 0" label="视频" value="video"></el-option>
            <el-option v-if="resourceTypeList.length === 0" label="音频" value="audio"></el-option>
            <el-option v-if="resourceTypeList.length === 0" label="图片" value="image"></el-option>
            <el-option v-if="resourceTypeList.length === 0" label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上传文件" prop="fileUrl" v-if="!isEditMode">
          <el-upload
            class="upload-container"
            drag
            action="#"
            :http-request="handleFileUpload"
            :before-upload="beforeUpload"
            :limit="1"
            :on-exceed="handleExceed"
            :file-list="fileList"
            :disabled="uploading"
          >
            <template #trigger>
              <el-button :loading="uploading" type="primary">
                {{ uploading ? '上传中...' : '选择文件' }}
              </el-button>
            </template>
            <el-icon class="el-icon--upload"><Upload /></el-icon>
            <div class="el-upload__text">拖拽文件到此处或 <em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">
                支持任意类型文件，单文件大小不超过100MB，文件将直接上传到云服务器
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="文件信息" v-if="isEditMode && resourceForm.fileUrl">
          <div class="file-info">
            <div>文件名称: {{ resourceForm.fileName }}</div>
            <div>文件URL: <el-link type="primary" :href="resourceForm.fileUrl" target="_blank" :underline="false">{{ resourceForm.fileUrl }}</el-link></div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseResourceDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmitResource">{{ isEditMode ? '保存修改' : '确认上传' }}</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 题目预览对话框 -->
    <el-dialog 
      v-model="questionPreviewDialog.visible" 
      :title="questionPreviewDialog.title" 
      width="60%"
      :before-close="handleCloseQuestionPreview"
    >
      <div class="question-preview-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="题目内容">
            <div v-html="currentQuestion.questionContent"></div>
          </el-descriptions-item>
          <el-descriptions-item label="题目类型">
            {{ currentQuestion.typeName }}
          </el-descriptions-item>
          <el-descriptions-item label="难度">
            <el-tag :type="getDifficultyTag(currentQuestion.difficulty)">
              {{ getDifficultyLabel(currentQuestion.difficulty) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="选项" v-if="currentQuestion.options && currentQuestion.options.length > 0">
            <div class="question-options">
              <div v-for="(option, index) in currentQuestion.options" :key="index" class="option-item">
                <div class="option-label">{{ option.label || String.fromCharCode(65 + index) }}:</div>
                <div class="option-content" v-html="option.content"></div>
              </div>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="正确答案">
            {{ formatCorrectAnswer(currentQuestion.correctAnswer) }}
          </el-descriptions-item>
          <el-descriptions-item label="解析" v-if="currentQuestion.explanation">
            <div v-html="currentQuestion.explanation"></div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseQuestionPreview">关闭</el-button>
          <el-button type="warning" @click="handleEditQuestion(currentQuestion)">编辑</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 题目添加/编辑对话框 -->
    <el-dialog 
      v-model="questionEditDialog.visible" 
      :title="questionEditDialog.title" 
      width="60%" 
      destroy-on-close
      @close="resetQuestionForm"
    >
      <el-form ref="questionFormRef" :model="questionForm" :rules="questionRules" label-width="100px">
        <!-- 试题类型 -->
        <el-form-item label="试题类型" prop="questionTypeId">
          <el-select v-model="questionForm.questionTypeId" placeholder="请选择试题类型" style="width: 100%" @change="handleQuestionTypeChange">
            <el-option
              v-for="item in questionTypeOptions"
              :key="item.typeId"
              :label="item.typeName"
              :value="item.typeId"
            />
          </el-select>
        </el-form-item>
        
        <!-- 试题内容 -->
        <el-form-item label="试题内容" prop="questionContent">
          <el-input
            v-model="questionForm.questionContent"
            type="textarea"
            :rows="4"
            placeholder="请输入试题内容"
          />
        </el-form-item>
        
        <!-- 选项 (单选题/多选题) -->
        <div v-if="questionForm.questionTypeId === 1 || questionForm.questionTypeId === 2">
          <el-divider content-position="left">选项</el-divider>
          <div v-for="(option, index) in questionForm._options" :key="index" class="option-form-item">
            <el-form-item :label="`选项 ${String.fromCharCode(65 + index)}`" :prop="`_options.${index}.content`" :rules="{ required: true, message: '选项内容不能为空', trigger: 'blur' }">
              <div class="option-form-content">
                <el-input v-model="option.content" placeholder="请输入选项内容" />
                <el-button type="danger" icon="Delete" circle @click="removeOption(index)" v-if="questionForm._options.length > 2"></el-button>
              </div>
              <div class="option-error" v-if="option.content === ''">选项内容不能为空</div>
            </el-form-item>
          </div>
          <el-button type="primary" plain @click="addOption" :disabled="questionForm._options.length >= 6">添加选项</el-button>
        </div>
        
        <!-- 答案 -->
        <el-form-item label="答案" prop="correctAnswer">
          <!-- 单选题 -->
          <template v-if="questionForm.questionTypeId === 1">
            <el-radio-group v-model="questionForm.correctAnswer">
              <el-radio v-for="(option, index) in questionForm._options" :key="index" :label="String.fromCharCode(65 + index)">
                {{ String.fromCharCode(65 + index) }}
              </el-radio>
            </el-radio-group>
          </template>
          
          <!-- 多选题 -->
          <template v-else-if="questionForm.questionTypeId === 2">
            <el-checkbox-group v-model="questionForm._answerArray">
              <el-checkbox v-for="(option, index) in questionForm._options" :key="index" :label="String.fromCharCode(65 + index)">
                {{ String.fromCharCode(65 + index) }}
              </el-checkbox>
            </el-checkbox-group>
          </template>
          
          <!-- 判断题 -->
          <template v-else-if="questionForm.questionTypeId === 3">
            <el-radio-group v-model="questionForm.correctAnswer">
              <el-radio label="true">正确</el-radio>
              <el-radio label="false">错误</el-radio>
            </el-radio-group>
          </template>
          
          <!-- 填空题/简答题/其他 -->
          <template v-else>
            <el-input
              v-model="questionForm.correctAnswer"
              type="textarea"
              :rows="3"
              placeholder="请输入答案"
            />
          </template>
        </el-form-item>
        
        <!-- 解析 -->
        <el-form-item label="解析" prop="explanation">
          <el-input
            v-model="questionForm.explanation"
            type="textarea"
            :rows="3"
            placeholder="请输入试题解析（选填）"
          />
        </el-form-item>
        
        <!-- 难度 -->
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="questionForm.difficulty" placeholder="请选择难度" style="width: 100%">
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="questionEditDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitQuestionForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 作业表单对话框 -->
    <el-dialog 
      v-model="assignmentDialog.visible" 
      :title="assignmentDialog.title" 
      width="700px" 
      destroy-on-close
      @closed="resetAssignmentForm"
    >
      <el-form ref="assignmentFormRef" :model="assignmentForm" :rules="assignmentRules" label-width="100px">
        <el-form-item label="所属课程" prop="courseId">
          <el-input v-model="currentCourse.courseName" disabled />
          <!-- 隐藏域不能正确传递数据，改为使用el-input -->
          <el-input v-model="assignmentForm.courseId" type="hidden" />
        </el-form-item>
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="assignmentForm.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="作业内容" prop="description">
          <el-input v-model="assignmentForm.description" type="textarea" :rows="5" placeholder="请输入作业内容" />
        </el-form-item>
        <el-form-item label="发布时间" prop="startTime">
          <el-date-picker
            v-model="assignmentForm.startTime"
            type="datetime"
            placeholder="选择发布时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="截止时间" prop="dueTime">
          <el-date-picker
            v-model="assignmentForm.dueTime"
            type="datetime"
            placeholder="选择截止时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="上传要求" prop="fileRequirements">
          <el-input v-model="assignmentForm.fileRequirements" type="textarea" :rows="3" placeholder="请输入上传文件要求（可选）" />
        </el-form-item>
        <el-form-item label="是否公开评分">
          <el-switch v-model="assignmentForm.publicScore" />
          <div class="form-help-text">开启后，学生可以查看自己的评分和教师评语</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignmentDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignmentForm" :loading="assignmentSubmitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加关联题目对话框 -->
    <AssignQuestionDialog
      :visible="assignQuestionDialog.visible"
      @update:visible="(val) => assignQuestionDialog.visible = val"
      :title="assignQuestionDialog.title"
      :assignment="assignQuestionDialog.assignment"
      :questionTypeOptions="questionTypeOptions"
      @submit="handleAssignQuestionsSubmit"
      @cancel="handleAssignQuestionsCancel"
    />

    <!-- 预览试卷对话框 -->
    <el-dialog v-model="examPreviewDialog.visible" :title="examPreviewDialog.title" width="800px" fullscreen destroy-on-close append-to-body>
      <div class="paper-preview" v-loading="previewLoading">
        <div class="paper-header">
          <h1>{{ previewExam.title }}</h1>
          <div class="paper-info">
            <span>总分：{{ previewExam.totalPoints }}分</span>
            <span>时长：{{ previewExam.duration }}分钟</span>
            <span>考试时间：{{ previewExam.startTime }} 至 {{ previewExam.endTime }}</span>
          </div>
          <div class="paper-info">
            <span>所属课程：{{ previewExam.courseName }}</span>
            <span>出题教师：{{ previewExam.teacherName || '系统管理员' }}</span>
          </div>
          <div class="paper-description" v-if="previewExam.description">{{ previewExam.description }}</div>
        </div>
        
        <!-- 试卷题目区域 -->
        <div class="paper-questions" v-if="previewExam.questions && previewExam.questions.length > 0">
          <div v-for="(question, index) in previewExam.questions" :key="question.questionId" class="question-item">
            <div class="question-header">
              <span class="question-number">{{ index + 1 }}.</span>
              <span class="question-title">{{ question.content }}</span>
              <span class="question-points">{{ question.points }}分</span>
            </div>
            
            <!-- 选择题选项 -->
            <div v-if="['单选题', '多选题'].includes(question.typeName) && question.options" class="question-options">
              <div v-for="(optionValue, optionKey) in parseOptions(question.options)" :key="optionKey" class="option-item">
                <span class="option-label">{{ optionKey }}.</span>
                <span class="option-content">{{ optionValue }}</span>
              </div>
            </div>
            
            <!-- 判断题选项 -->
            <div v-if="question.typeName === '判断题'" class="question-options">
              <div class="option-item">
                <span class="option-label">A.</span>
                <span class="option-content">正确</span>
              </div>
              <div class="option-item">
                <span class="option-label">B.</span>
                <span class="option-content">错误</span>
              </div>
            </div>
            
            <!-- 简答题 -->
            <div v-if="question.typeName === '简答题'" class="question-answer-area">
              <div class="answer-area-placeholder">答题区域</div>
            </div>
            
            <!-- 答案和解析 -->
            <div v-if="question.answer" class="question-answer">
              <div class="answer-label">参考答案：</div>
              <div class="answer-content">{{ question.answer }}</div>
            </div>
            <div v-if="question.analysis" class="question-analysis">
              <div class="analysis-label">解析：</div>
              <div class="analysis-content">{{ question.analysis }}</div>
            </div>
          </div>
        </div>
        
        <!-- 无题目提示 -->
        <div v-else class="no-questions">
          <el-empty description="当前试卷暂无题目" />
        </div>
      </div>
    </el-dialog>

    <!-- 考试对话框引用和控制 -->
    <el-dialog 
      v-model="examDialog.visible" 
      :title="examDialog.title" 
      width="50%"
      :before-close="handleCloseExamDialog"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form :model="examForm" label-width="100px" :rules="examRules" ref="examFormRef">
        <el-form-item label="考试标题" prop="title">
          <el-input v-model="examForm.title" placeholder="请输入考试标题"></el-input>
        </el-form-item>
        <el-form-item label="所属课程" prop="courseId">
          <el-select v-model="examForm.courseId" placeholder="请选择所属课程" style="width: 100%" @change="handleCourseChange">
            <el-option
              v-for="item in courseQuestionOptions"
              :key="item.questionId"
              :label="item.questionTitle"
              :value="item.questionId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考试描述" prop="description">
          <el-input v-model="examForm.description" type="textarea" :rows="4" placeholder="请输入考试描述"></el-input>
        </el-form-item>
        <el-form-item label="答题时长" prop="duration">
          <el-input v-model="examForm.duration" placeholder="请设置答题时长"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="examForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="examForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="examForm.status" placeholder="请选择状态">
            <el-option label="草稿" :value="0"></el-option>
            <el-option label="已发布" :value="1"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseExamDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmitExam" :loading="examDialog.loading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 关联考试题目对话框 -->
    <el-dialog 
      v-model="examQuestionDialog.visible" 
      :title="examQuestionDialog.title" 
      width="700px" 
      destroy-on-close
    >
      <div class="assign-questions-container">
        <div class="assignment-info mb-20">
          <h3>考试信息</h3>
          <p><strong>标题:</strong> {{ examQuestionDialog.exam.title }}</p>
          <p v-if="examQuestionDialog.exam.questions && examQuestionDialog.exam.questions.length > 0">
            <strong>已关联题目数:</strong> {{ examQuestionDialog.exam.questions.length }}
          </p>
          <p v-if="examQuestionDialog.exam.totalPoints">
            <strong>当前总分:</strong> {{ examQuestionDialog.exam.totalPoints }}
          </p>
        </div>
        
        <el-form :inline="true" :model="examQuestionQueryParams" class="question-search-form">
          <el-form-item label="试题内容">
            <el-input v-model="examQuestionQueryParams.questionContent" placeholder="请输入试题内容关键字" clearable @keyup.enter="searchExamQuestions" />
          </el-form-item>
          <el-form-item label="题目类型">
            <el-select v-model="examQuestionQueryParams.questionTypeId" placeholder="请选择题目类型" clearable>
              <el-option 
                v-for="item in questionTypeOptions" 
                :key="item.typeId" 
                :label="item.typeName" 
                :value="item.typeId"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchExamQuestions">搜索</el-button>
            <el-button @click="resetExamQuestionQuery">重置</el-button>
          </el-form-item>
        </el-form>
        
        <el-table 
          ref="examQuestionTableRef"
          v-loading="examQuestionLoading"
          :data="examQuestionList" 
          border 
          stripe 
          style="width: 100%"
          @selection-change="handleExamQuestionSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="questionContent" label="题目内容" min-width="200" show-overflow-tooltip>
            <template #default="scope">
              <div v-html="formatQuestionContent(scope.row.questionContent)"></div>
            </template>
          </el-table-column>
          <el-table-column prop="typeName" label="题目类型" width="100" align="center" />
          <el-table-column prop="difficulty" label="难度" width="80" align="center">
            <template #default="scope">
              <el-tag :type="getDifficultyTag(scope.row.difficulty)">
                {{ getDifficultyLabel(scope.row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="分值" width="120">
            <template #default="scope">
              <el-input-number v-model="scope.row.points" :min="0" :max="100" :precision="2" :step="0.5" size="small" />
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <el-pagination
            :current-page="examQuestionQueryParams.pageNum"
            :page-size="examQuestionQueryParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="examQuestionTotal"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleExamQuestionSizeChange"
            @current-change="handleExamQuestionCurrentChange"
          />
        </div>
        
        <div class="assign-question-footer">
          <div class="total-points">
            选中：<span>{{ selectedExamQuestionsCount }}</span> 题 / 
            总分：<span>{{ calculateSelectedExamQuestionsTotalPoints }}</span> 分
          </div>
          <div>
            <el-button @click="cancelExamQuestionDialog">取消</el-button>
            <el-button type="primary" @click="confirmExamQuestionDialog" :disabled="selectedExamQuestions.length === 0">确定</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <!-- 考试题目关联对话框 -->
    <AssignExamQuestionDialog
      :visible="examQuestionDialog.visible"
      :exam="examQuestionDialog.exam"
      :question-type-options="questionTypeOptions"
      @update:visible="(val) => examQuestionDialog.visible = val"
      @submit="handleExamQuestionSubmit"
      @cancel="examQuestionDialog.visible = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue';
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus';
import { Search, Refresh, View, Upload, Edit, Document, Collection, Picture, ArrowDown, Delete, VideoPlay, Headset, Files } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

// 导入API
import { getTeacherCourseList } from '@/api/edu/course';
// 导入讨论区相关的API
import { getForumList, createForum, deleteForum } from '@/api/edu/discussion';
// 导入资源相关的API
import { 
  getResourceList, 
  uploadResource, 
  deleteResource, 
  downloadResource,
  getCourseResourceList,
  getResourceTypeList,
  uploadResourceFile,
  getAllEnabledResourceTypes,
  updateResource
} from '@/api/edu/resource';
// 导入题库相关的API
import { 
  getQuestionList, 
  getQuestionInfo, 
  addQuestion, 
  updateQuestion, 
  deleteQuestion as deleteQuestionApi 
} from '@/api/exam/question';
import { getQuestionTypeList } from '@/api/exam/questionType';
import { 
  getAssignmentList, 
  getAssignmentInfo, 
  addAssignment, 
  updateAssignment, 
  deleteAssignment as deleteAssignmentApi,
  associateQuestions
} from '@/api/exam/assignment'
import { getExamPaperList, getExamPaperInfo, addExamPaper, updateExamPaper, publishExamPaper, unpublishExamPaper, deleteExamPaper } from '@/api/exam/paper';
import { getExamPaperQuestions, addExamPaperQuestions, deleteExamPaperQuestion } from '@/api/exam/paperQuestion';

import AssignQuestionDialog from '@/components/AssignQuestionDialog.vue';
import AssignExamQuestionDialog from '@/components/AssignExamQuestionDialog.vue';

const router = useRouter();

// 默认课程封面图
const defaultCover = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png';

// 加载状态
const loading = ref(false);
// 课程列表
const courseList = ref([]);
// 总记录数
const total = ref(0);
// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 8,
  courseName: '',
  status: ""
});

// 课程详情对话框
const detailDialogVisible = ref(false);
const currentCourse = ref({});
const activeTab = ref('info');

// 讨论区相关
const discussionList = ref([]);
const createDiscussionDialogVisible = ref(false);
const discussionForm = reactive({
  courseId: '',
  forumName: '',
  description: '',
  allowStudentTopic: true
});
const discussionFormRef = ref(null);
const discussionRules = {
  forumName: [{ required: true, message: '请输入讨论区标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入讨论区描述', trigger: 'blur' }]
};

// 资源管理相关
const resourceList = ref([]);
const createResourceDialogVisible = ref(false);
const fileList = ref([]);
const uploading = ref(false); // 添加上传状态标记
const resourceFormRef = ref(null);
const isEditMode = ref(false); // 添加编辑模式标记
const currentResourceId = ref(null); // 添加当前编辑的资源ID
const resourceForm = reactive({
  courseId: '',
  resourceName: '',
  description: '',
  resourceType: 'document',
  fileUrl: '', // 将file替换为fileUrl
  fileName: '' // 添加文件名字段
});
const resourceRules = {
  resourceName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
  resourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
  fileUrl: [{ required: true, message: '请上传文件', trigger: 'change' }] // 更改验证字段
};

// 资源类型相关
const resourceTypeList = ref([]);

// 题库管理相关
const questionList = ref([]);
const questionLoading = ref(false);
const questionTotal = ref(0);
const questionTypeOptions = ref([]);
const questionQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  questionContent: '',
  questionTypeId: undefined,
  courseId: undefined
});

// 题目预览对话框
const questionPreviewDialog = reactive({
  visible: false,
  title: '试题预览'
});

// 当前操作的试题
const currentQuestion = reactive({
  questionId: undefined,
  questionTypeId: undefined,
  typeName: '',
  questionContent: '',
  options: [],
  correctAnswer: '',
  explanation: '',
  difficulty: ''
});

// 题目添加/编辑对话框
const questionEditDialog = reactive({
  visible: false,
  title: '编辑试题'
});

// 题目编辑/添加相关
const questionFormRef = ref(null);
const questionForm = reactive({
  questionId: undefined, // 题目ID（修改时必填）
  questionTypeId: undefined, // 题目类型ID
  questionContent: '', // 题目内容
  options: '[]', // 选项（JSON格式）
  correctAnswer: '', // 正确答案
  explanation: '', // 解析
  difficulty: 'medium', // 难度（easy简单 medium中等 hard困难）
  courseId: undefined, // 课程ID
  status: 1, // 状态（1正常 0停用）
  // 用于UI展示的临时字段
  _options: [
    { content: '' },
    { content: '' },
    { content: '' },
    { content: '' }
  ],
  _answerArray: [] // 用于多选题的临时答案数组
});

// 题目校验规则
const questionRules = {
  questionTypeId: [
    { required: true, message: '请选择题目类型', trigger: 'change' }
  ],
  questionContent: [
    { required: true, message: '请输入题目内容', trigger: 'blur' }
  ],
  correctAnswer: [
    { required: true, message: '请选择/输入正确答案', trigger: 'blur' }
  ],
  difficulty: [
    { required: true, message: '请选择难度', trigger: 'change' }
  ]
};

// 监听多选题答案数组变化，自动更新答案字段
watch(() => questionForm._answerArray, (val) => {
  if (questionForm.questionTypeId === 2) {
    questionForm.correctAnswer = val.sort().join('');
  }
}, { deep: true });

// 作业管理相关
const assignmentList = ref([])
const assignmentTotal = ref(0)
const assignmentLoading = ref(false)
const assignmentSubmitLoading = ref(false)
const assignmentQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  courseId: undefined
})
const assignmentFormRef = ref(null)
const assignmentDialog = reactive({
  visible: false,
  title: ''
})
const assignmentForm = reactive({
  assignmentId: undefined,
  courseId: undefined,
  title: '',
  description: '',
  startTime: '',
  dueTime: '',
  fileRequirements: '',
  publicScore: true
})
const assignmentRules = {
  title: [
    { required: true, message: '请输入作业标题', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入作业内容', trigger: 'blur' }
  ],
  totalPoints: [
    { required: true, message: '请设置总分', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择发布时间', trigger: 'change' }
  ],
  dueTime: [
    { required: true, message: '请选择截止时间', trigger: 'change' }
  ]
}

// 题库选择相关
const courseQuestionOptions = ref([])
const questionSelectLoading = ref(false)

/**
 * 查询课程列表
 */
const getList = async () => {
  loading.value = true;
  try {
    const response = await getTeacherCourseList(queryParams);
    courseList.value = response.data.records || [];
    total.value = response.data.total || 0;
  } catch (error) {
    console.error('获取课程列表失败', error);
    ElMessage.error('获取课程列表失败: ' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

/**
 * 搜索按钮操作
 */
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

/**
 * 重置搜索表单
 */
const resetQuery = () => {
  queryParams.courseName = '';
  queryParams.status = undefined;
  handleQuery();
};

/**
 * 格式化日期
 */
const formatDate = (dateStr) => {
  if (!dateStr) return '未设置';
  return dateStr.split('T')[0];
};

/**
 * 获取状态标签类型
 */
const getStatusTag = (status) => {
  if (status === "draft") return 'info';
  if (status === "published") return 'success';
  if (status === "closed") return 'danger';
  return 'info';
};

/**
 * 获取状态标签文本
 */
const getStatusLabel = (status) => {
  if (status === "draft") return '草稿';
  if (status === "published") return '进行中';
  if (status === "closed") return '已结束';
  return '未知';
};

/**
 * 处理分页大小改变
 */
const handleSizeChange = (size) => {
  queryParams.pageSize = size;
  getList();
};

/**
 * 处理页码改变
 */
const handleCurrentChange = (num) => {
  queryParams.pageNum = num;
  getList();
};

/**
 * 打开课程详情对话框
 */
const openCourseDetail = (course) => {
  currentCourse.value = course;
  detailDialogVisible.value = true;
  activeTab.value = 'info';
};

/**
 * 关闭课程详情对话框
 */
const handleCloseDialog = () => {
  detailDialogVisible.value = false;
  currentCourse.value = {};
};

/**
 * 处理资源管理操作
 */
const handleResourceAction = () => {
  // 打开资源上传对话框
  openCreateResourceDialog();
};

/**
 * 处理题库管理操作
 */
const handleQuestionAction = () => {
  // 打开添加试题对话框
  openQuestionAddDialog();
};

/**
 * 打开添加试题对话框
 */
const openQuestionAddDialog = () => {
  questionEditDialog.title = '添加试题';
  questionEditDialog.visible = true;
  resetQuestionForm();
  
  // 设置当前课程ID
  questionForm.courseId = currentCourse.value.courseId;
  
  // 默认选择第一个题目类型
  if (questionTypeOptions.value.length > 0) {
    questionForm.questionTypeId = questionTypeOptions.value[0].typeId;
  }
};

/**
 * 处理题库预览
 */
const viewQuestion = (question) => {
  // 填充当前操作的试题数据
  currentQuestion.questionId = question.questionId;
  currentQuestion.questionTypeId = question.questionTypeId;
  currentQuestion.typeName = question.typeName;
  currentQuestion.questionContent = question.questionContent;
  
  // 处理选项数据，将JSON字符串转换为数组
  if (question.options) {
    try {
      // 对于后端返回的选项格式 [{key: "A", value: "选项内容"}, ...] 进行特殊处理
      const optionsArray = typeof question.options === 'string' 
        ? JSON.parse(question.options) 
        : question.options;
      
      // 将选项转换为统一的格式
      currentQuestion.options = optionsArray.map(opt => ({
        label: opt.key || '',
        content: opt.value || opt.content || ''
      }));
    } catch (error) {
      console.error('解析选项数据失败', error);
      currentQuestion.options = [];
    }
  } else {
    currentQuestion.options = [];
  }
  
  currentQuestion.correctAnswer = question.correctAnswer || '';
  currentQuestion.explanation = question.explanation || '';
  currentQuestion.difficulty = question.difficulty || '';
  
  // 打开题目预览对话框
  questionPreviewDialog.visible = true;
  questionPreviewDialog.title = `试题预览 - ${question.typeName}`;
};

/**
 * 处理考试管理操作
 */
const handleExamAction = () => {
  createExam();
};

/**
 * 处理作业管理操作
 */
const handleAssignmentAction = () => {
  // 这里可以直接处理发布作业逻辑，或者跳转到作业发布页面
  router.push({
    path: '/exam/assignment',
    query: { 
      action: 'create', 
      courseId: currentCourse.value.courseId, 
      courseName: currentCourse.value.courseName 
    }
  });
};

/**
 * 加载讨论区列表
 */
const loadDiscussionList = async () => {
  try {
    const response = await getForumList({ 
      courseId: currentCourse.value.courseId,
      pageNum: 1,
      pageSize: 50
    });
    discussionList.value = response.data.records || [];
  } catch (error) {
    console.error('获取讨论区列表失败', error);
    ElMessage.error('获取讨论区列表失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 打开创建讨论区对话框
 */
const openCreateDiscussionDialog = () => {
  discussionForm.courseId = currentCourse.value.courseId;
  discussionForm.forumName = '';
  discussionForm.description = '';
  discussionForm.allowStudentTopic = true;
  createDiscussionDialogVisible.value = true;
};

/**
 * 关闭创建讨论区对话框
 */
const handleCloseDiscussionDialog = () => {
  createDiscussionDialogVisible.value = false;
  if (discussionFormRef.value) {
    discussionFormRef.value.resetFields();
  }
};

/**
 * 创建讨论区
 */
const handleCreateDiscussion = () => {
  if (discussionFormRef.value) {
    discussionFormRef.value.validate(async (valid) => {
      if (valid) {
        try {
          // 准备请求数据
          const forumData = {
            courseId: currentCourse.value.courseId,
            forumName: discussionForm.forumName,
            description: discussionForm.description,
            allowStudentTopic: discussionForm.allowStudentTopic
          };
          
          await createForum(forumData);
          ElMessage.success('讨论区创建成功');
          createDiscussionDialogVisible.value = false;
          
          // 刷新讨论区列表
          loadDiscussionList();
        } catch (error) {
          console.error('创建讨论区失败', error);
          ElMessage.error('创建讨论区失败: ' + (error.message || '未知错误'));
        }
      }
    });
  }
};

/**
 * 查看讨论区
 */
const handleViewDiscussion = (discussion) => {
  // 跳转到讨论区详情页面
  router.push({
    path: `/discussion/detail/${discussion.forumId}`,
    query: { 
      courseId: currentCourse.value.courseId, 
      courseName: currentCourse.value.courseName 
    }
  });
};

/**
 * 删除讨论区
 */
const handleDeleteDiscussion = (discussion) => {
  ElMessageBox.confirm(
    `确定要删除讨论区"${discussion.title}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteForum(discussion.forumId);
      ElMessage.success('讨论区删除成功');
      
      // 刷新讨论区列表
      loadDiscussionList();
    } catch (error) {
      console.error('删除讨论区失败', error);
      ElMessage.error('删除讨论区失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {
    // 取消删除
  });
};

// 当选择标签页变化时
const handleTabChange = (tab) => {
  if (tab === 'discussion') {
    loadDiscussionList();
  } else if (tab === 'resource') {
    loadResourceList();
  } else if (tab === 'question') {
    // 加载题目类型选项
    loadQuestionTypes();
    
    // 设置当前课程ID到查询参数
    if (currentCourse.value && currentCourse.value.courseId) {
      questionQueryParams.courseId = currentCourse.value.courseId;
      
      // 加载题库列表
      fetchQuestionList();
    } else {
      ElMessage.warning('当前课程数据无效，无法加载题库');
    }
  } else if (tab === 'assignment') {
    // 加载作业列表
    loadAssignmentList();
  } else if (tab === 'exam') {
    // 加载考试列表
    loadExamList();
  }
};

/**
 * 获取资源类型标签类型
 */
const getResourceTypeTag = (type) => {
  switch (type) {
    case 'document': return 'primary';
    case 'video': return 'success';
    case 'audio': return 'warning';
    case 'image': return 'info';
    default: return 'info';
  }
};

/**
 * 获取资源类型标签文本
 */
const getResourceTypeLabel = (type) => {
  // 从后端获取的资源类型列表中查找对应的标签
  const resourceType = resourceTypeList.value.find(item => item.typeCode === type);
  if (resourceType) {
    return resourceType.typeName;
  }
  
  // 后备方案：如果后端没有返回对应的类型，使用默认值
  switch (type) {
    case 'document': return '文档';
    case 'video': return '视频';
    case 'audio': return '音频';
    case 'image': return '图片';
    default: return '其他';
  }
};

/**
 * 格式化文件大小
 */
const formatFileSize = (size) => {
  if (!size) return '0 B';
  
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let index = 0;
  let formattedSize = size;
  
  while (formattedSize >= 1024 && index < units.length - 1) {
    formattedSize /= 1024;
    index++;
  }
  
  return `${formattedSize.toFixed(2)} ${units[index]}`;
};

/**
 * 判断是否是文档类型资源
 */
const isDocumentType = (typeId) => {
  return typeId === 1 || typeId === 2;
};

/**
 * 判断是否是视频类型资源
 */
const isVideoType = (typeId) => {
  return typeId === 3 || typeId === 4;
};

/**
 * 判断是否是图片类型资源
 */
const isImageType = (typeId) => {
  return typeId === 5;
};

/**
 * 判断是否是音频类型资源
 */
const isAudioType = (typeId) => {
  return typeId === 6;
};

/**
 * 判断资源是否可以直接在浏览器中预览
 */
const canDirectPreview = (resource) => {
  if (!resource.fileUrl) return false;
  
  // 视频或图片可以直接预览
  return isVideoType(resource.resourceTypeId) || isImageType(resource.resourceTypeId);
};

/**
 * 根据资源类型ID获取标签类型
 */
const getResourceTypeTagById = (typeId) => {
  switch (typeId) {
    case 1: return 'primary'; // PPT
    case 2: return 'success'; // 文档
    case 3: return 'warning'; // 视频
    case 4: return 'warning'; // 视频
    case 5: return 'info';    // 图片
    case 6: return 'danger';  // 音频
    default: return 'info';
  }
};

/**
 * 根据资源类型ID获取标签文本
 */
const getResourceTypeLabelById = (typeId) => {
  switch (typeId) {
    case 1: return 'PPT';
    case 2: return '文档';
    case 3: return '视频';
    case 4: return '视频';
    case 5: return '图片';
    case 6: return '音频';
    default: return '其他';
  }
};

/**
 * 格式化日期时间（适用于资源列表）
 */
const formatResourceDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '';
  
  // 解析日期时间字符串
  const dateObj = new Date(dateTimeStr);
  
  // 格式化为 YYYY-MM-DD HH:MM 格式
  const year = dateObj.getFullYear();
  const month = String(dateObj.getMonth() + 1).padStart(2, '0');
  const day = String(dateObj.getDate()).padStart(2, '0');
  const hours = String(dateObj.getHours()).padStart(2, '0');
  const minutes = String(dateObj.getMinutes()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// 资源加载状态
const resourceLoading = ref(false);

/**
 * 加载课程资源列表
 */
const loadResourceList = async () => {
  if (!currentCourse.value || !currentCourse.value.courseId) {
    ElMessage.warning('未选择课程或课程ID无效');
    resourceList.value = [];
    return;
  }

  resourceLoading.value = true;
  try {
    console.log('正在加载课程ID为', currentCourse.value.courseId, '的资源');
    
    const response = await getCourseResourceList({
      courseId: currentCourse.value.courseId
    });
    
    // 后端直接返回资源列表数组，不是分页对象
    if (Array.isArray(response.data)) {
      resourceList.value = response.data;
    } else if (response.data && response.data.records) {
      // 兼容处理，如果后端返回分页对象
      resourceList.value = response.data.records;
    } else {
      resourceList.value = [];
    }
    
    console.log('加载到资源:', resourceList.value.length, '条');
  } catch (error) {
    console.error('获取资源列表失败', error);
    ElMessage.error('获取资源列表失败: ' + (error.message || '未知错误'));
    resourceList.value = [];
  } finally {
    resourceLoading.value = false;
  }
};

/**
 * 打开创建资源对话框
 */
const openCreateResourceDialog = () => {
  isEditMode.value = false; // 设置为创建模式
  currentResourceId.value = null;
  resourceForm.courseId = currentCourse.value.courseId;
  resourceForm.resourceName = '';
  resourceForm.description = '';
  resourceForm.resourceType = resourceTypeList.value.length > 0 ? resourceTypeList.value[0].typeId : 'document';
  resourceForm.fileUrl = ''; // 重置fileUrl
  resourceForm.fileName = ''; // 重置fileName
  fileList.value = [];
  createResourceDialogVisible.value = true;
};

/**
 * 处理编辑资源
 */
const handleEditResource = (resource) => {
  isEditMode.value = true; // 设置为编辑模式
  currentResourceId.value = resource.resourceId;
  
  // 填充表单数据
  resourceForm.courseId = resource.courseId;
  resourceForm.resourceName = resource.resourceName;
  resourceForm.description = resource.description || '';
  resourceForm.resourceType = resource.resourceTypeId;
  resourceForm.fileUrl = resource.fileUrl || '';
  resourceForm.fileName = resource.fileName || '';
  
  // 如果有文件URL，显示在文件列表中
  if (resource.fileUrl) {
    fileList.value = [{
      name: resource.fileName || '未知文件名',
      url: resource.fileUrl
    }];
  } else {
    fileList.value = [];
  }
  
  createResourceDialogVisible.value = true;
};

/**
 * 关闭创建/编辑资源对话框
 */
const handleCloseResourceDialog = () => {
  createResourceDialogVisible.value = false;
  isEditMode.value = false;
  currentResourceId.value = null;
  if (resourceFormRef.value) {
    resourceFormRef.value.resetFields();
  }
  fileList.value = [];
};

/**
 * 处理资源表单提交 (创建或更新)
 */
const handleSubmitResource = () => {
  if (resourceFormRef.value) {
    resourceFormRef.value.validate(async (valid) => {
      if (valid) {
        if (!isEditMode.value && !resourceForm.fileUrl) {
          ElMessage.error('请先上传文件到云服务器');
          return;
        }
        
        try {
          // 检查资源类型ID是否有效
          const selectedTypeId = Number(resourceForm.resourceType);
          const validType = resourceTypeList.value.find(type => type.typeId === selectedTypeId);
          if (!validType) {
            ElMessage.error('所选资源类型无效，请重新选择');
            return;
          }
          
          // 准备提交到后端的数据
          const resourceData = {
            courseId: Number(resourceForm.courseId),
            resourceName: resourceForm.resourceName,
            description: resourceForm.description,
            resourceTypeId: selectedTypeId,
            fileUrl: resourceForm.fileUrl,
            fileName: resourceForm.fileName,
            fileSize: fileList.value[0]?.size || 0
          };
          
          if (isEditMode.value) {
            // 编辑模式 - 添加资源ID
            resourceData.resourceId = currentResourceId.value;
            
            // 调用更新API
            await updateResource(resourceData);
            ElMessage.success('资源更新成功');
          } else {
            // 创建模式
            await uploadResource(resourceData);
            ElMessage.success('资源添加成功');
          }
          
          createResourceDialogVisible.value = false;
          // 刷新资源列表
          loadResourceList();
        } catch (error) {
          console.error(isEditMode.value ? '更新资源失败' : '添加资源失败', error);
          ElMessage.error((isEditMode.value ? '更新资源失败' : '添加资源失败') + ': ' + (error.message || '未知错误'));
        }
      }
    });
  }
};

/**
 * 处理资源下载
 */
const handleDownloadResource = async (resource) => {
  try {
    // 显示下载中提示
    const loadingInstance = ElLoading.service({
      text: '正在获取下载链接...',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    
    const response = await downloadResource(resource.resourceId);
    console.log('下载响应:', response);
    
    // 关闭下载提示
    loadingInstance.close();
    
    // 检查响应格式并获取文件信息
    if (response.code === 200 && response.data) {
      const fileUrl = response.data.fileUrl;
      const fileName = response.data.fileName || resource.resourceName;
      console.log('文件URL:', fileUrl);
      
      if (!fileUrl) {
        ElMessage.error('文件URL不存在');
        return;
      }
      
      // 使用fetch直接获取文件并下载，避免打开新标签页
      ElMessage.info('正在准备下载文件，请稍候...');
      
      try {
        const fileResponse = await fetch(fileUrl);
        if (!fileResponse.ok) throw new Error('文件下载失败');
        
        const blob = await fileResponse.blob();
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName);
        // 不设置target="_blank"，这样就不会打开新标签页
        document.body.appendChild(link);
        link.click();
        
        // 清理
        window.URL.revokeObjectURL(url);
        document.body.removeChild(link);
        
        ElMessage.success('文件下载成功: ' + fileName);
      } catch (error) {
        console.error('文件下载失败', error);
        ElMessage.error('文件下载失败: ' + (error.message || '未知错误'));
        
        // 如果fetch方式失败，回退到传统下载方式
        ElMessage.info('尝试使用替代方式下载...');
        const link = document.createElement('a');
        link.href = fileUrl;
        link.setAttribute('download', fileName);
        // 不使用target="_blank"
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    } else {
      ElMessage.error('获取下载链接失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    console.error('下载资源失败', error);
    ElMessage.error('下载资源失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 处理资源预览
 */
const handlePreviewResource = (resource) => {
  // 检查资源URL是否存在
  if (!resource.fileUrl) {
    ElMessage.warning('该资源没有可预览的URL');
    return;
  }
  
  // 直接在新标签页打开资源URL
  window.open(resource.fileUrl, '_blank');
};

/**
 * 处理资源删除
 */
const handleDeleteResource = (resource) => {
  ElMessageBox.confirm(
    `确定要删除资源"${resource.resourceName}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteResource(resource.resourceId);
      ElMessage.success('资源删除成功');
      
      // 刷新资源列表
      loadResourceList();
    } catch (error) {
      console.error('删除资源失败', error);
      ElMessage.error('删除资源失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {
    // 取消删除
  });
};

/**
 * 加载资源类型列表
 */
const loadResourceTypeList = async () => {
  try {
    // 使用API函数获取所有启用的资源类型
    const response = await getAllEnabledResourceTypes();
    
    if (response.code === 200 && response.data) {
      resourceTypeList.value = response.data;
      console.log('加载到资源类型:', resourceTypeList.value.length, '条');
    } else {
      throw new Error(response.msg || '获取资源类型列表返回数据格式错误');
    }
  } catch (error) {
    console.error('获取资源类型列表失败', error);
    ElMessage.error('获取资源类型列表失败: ' + (error.message || '未知错误'));
    resourceTypeList.value = [];
  }
};

/**
 * 处理题库查询
 */
const searchQuestions = () => {
  questionQueryParams.pageNum = 1;
  // 确保查询时总是带上当前课程ID
  questionQueryParams.courseId = currentCourse.value.courseId;
  fetchQuestionList();
};

/**
 * 重置题库查询
 */
const resetQuestionQuery = () => {
  questionQueryParams.questionContent = '';
  questionQueryParams.questionTypeId = undefined;
  // 确保重置后依然保留当前课程ID
  questionQueryParams.courseId = currentCourse.value.courseId;
  searchQuestions();
};

/**
 * 获取题库查询结果
 */
const fetchQuestionList = async () => {
  // 确保课程ID已设置
  if (!questionQueryParams.courseId && currentCourse.value && currentCourse.value.courseId) {
    questionQueryParams.courseId = currentCourse.value.courseId;
  }
  
  // 如果没有课程ID，则不执行查询
  if (!questionQueryParams.courseId) {
    ElMessage.warning('未选择课程或课程ID无效');
    questionList.value = [];
    questionTotal.value = 0;
    return;
  }
  
  questionLoading.value = true;
  try {
    const response = await getQuestionList(questionQueryParams);
    questionList.value = response.data.rows || response.data.records || [];
    questionTotal.value = response.data.total || 0;
  } catch (error) {
    console.error('获取题库列表失败', error);
    ElMessage.error('获取题库列表失败: ' + (error.message || '未知错误'));
    questionList.value = [];
  } finally {
    questionLoading.value = false;
  }
};

/**
 * 处理题库分页
 */
const handleQuestionSizeChange = (size) => {
  questionQueryParams.pageSize = size;
  fetchQuestionList();
};

/**
 * 处理题库页码
 */
const handleQuestionCurrentChange = (num) => {
  questionQueryParams.pageNum = num;
  fetchQuestionList();
};

/**
 * 处理编辑试题
 */
const handleEditQuestion = async (question) => {
  try {
    // 获取试题完整信息
    const response = await getQuestionInfo(question.questionId);
    const questionData = response.data;
    
    questionEditDialog.title = '编辑试题';
    questionEditDialog.visible = true;
    
    // 重置表单
    resetQuestionForm();
    
    // 设置表单数据
    questionForm.questionId = questionData.questionId;
    questionForm.questionTypeId = questionData.questionTypeId;
    questionForm.questionContent = questionData.questionContent;
    questionForm.correctAnswer = questionData.correctAnswer;
    questionForm.explanation = questionData.explanation;
    questionForm.difficulty = questionData.difficulty || 'medium';
    questionForm.courseId = questionData.courseId || currentCourse.value.courseId;
    questionForm.status = questionData.status || 1;
    
    // 处理选项
    if (questionData.questionTypeId === 1 || questionData.questionTypeId === 2) {
      try {
        const optionsData = typeof questionData.options === 'string' 
          ? JSON.parse(questionData.options) 
          : questionData.options;
          
        questionForm._options = optionsData.map(option => ({
          content: option.value || option.content || ''
        }));
        
        // 处理多选题答案
        if (questionData.questionTypeId === 2 && questionData.correctAnswer) {
          questionForm._answerArray = questionData.correctAnswer.split('');
        }
      } catch (error) {
        console.error('解析选项数据失败', error);
        questionForm._options = [
          { content: '' },
          { content: '' },
          { content: '' },
          { content: '' }
        ];
      }
    }
  } catch (error) {
    console.error('获取试题详情失败', error);
    ElMessage.error('获取试题详情失败: ' + (error.message || '未知错误'));
  }
};

/**
 * 重置题目表单
 */
const resetQuestionForm = () => {
  if (questionFormRef.value) {
    questionFormRef.value.resetFields();
  }
  
  questionForm.questionId = undefined;
  questionForm.questionTypeId = undefined;
  questionForm.questionContent = '';
  questionForm.options = '[]';
  questionForm.correctAnswer = '';
  questionForm.explanation = '';
  questionForm.difficulty = 'medium';
  questionForm.status = 1;
  
  // 重置UI相关字段
  questionForm._options = [
    { content: '' },
    { content: '' },
    { content: '' },
    { content: '' }
  ];
  questionForm._answerArray = [];
  
  // 保留课程ID
  questionForm.courseId = currentCourse.value.courseId;
};

/**
 * 处理题目类型变化
 */
const handleQuestionTypeChange = (typeId) => {
  // 重置答案相关数据
  questionForm.correctAnswer = '';
  questionForm._answerArray = [];
};

/**
 * 添加选项
 */
const addOption = () => {
  if (questionForm._options.length < 6) {
    questionForm._options.push({ content: '' });
  }
};

/**
 * 移除选项
 */
const removeOption = (index) => {
  if (questionForm._options.length > 2) {
    // 处理答案
    if (questionForm.questionTypeId === 1) {
      const optionLabel = String.fromCharCode(65 + index);
      if (questionForm.correctAnswer === optionLabel) {
        questionForm.correctAnswer = '';
      }
    } else if (questionForm.questionTypeId === 2) {
      const optionLabel = String.fromCharCode(65 + index);
      const answerIndex = questionForm._answerArray.indexOf(optionLabel);
      if (answerIndex !== -1) {
        questionForm._answerArray.splice(answerIndex, 1);
      }
    }
    
    // 删除选项
    questionForm._options.splice(index, 1);
    
    // 更新多选答案中的选项
    if (questionForm.questionTypeId === 2) {
      const newAnswerArray = [];
      questionForm._answerArray.forEach(answer => {
        const answerIndex = answer.charCodeAt(0) - 65;
        if (answerIndex < index) {
          newAnswerArray.push(answer);
        } else if (answerIndex > index) {
          newAnswerArray.push(String.fromCharCode(answer.charCodeAt(0) - 1));
        }
      });
      questionForm._answerArray = newAnswerArray;
    }
  }
};

/**
 * 提交题目表单
 */
const submitQuestionForm = async () => {
  if (!questionFormRef.value) return;
  
  // 检查选项是否为空
  let hasEmptyOption = false;
  if (questionForm.questionTypeId === 1 || questionForm.questionTypeId === 2) {
    questionForm._options.forEach(option => {
      if (!option.content.trim()) {
        hasEmptyOption = true;
      }
    });
    
    if (hasEmptyOption) {
      ElMessage.error('选项内容不能为空');
      return;
    }
  }
  
  // 检查单选/多选题是否选择了答案
  if (questionForm.questionTypeId === 1 && !questionForm.correctAnswer) {
    ElMessage.error('请选择正确答案');
    return;
  }
  
  if (questionForm.questionTypeId === 2 && (!questionForm._answerArray || questionForm._answerArray.length === 0)) {
    ElMessage.error('请至少选择一个正确答案');
    return;
  }
  
  // 确保设置了课程ID
  if (!questionForm.courseId) {
    questionForm.courseId = currentCourse.value.courseId;
  }
  
  // 更新选项JSON数据
  if (questionForm.questionTypeId === 1 || questionForm.questionTypeId === 2) {
    questionForm.options = JSON.stringify(questionForm._options.map((item, index) => ({
      key: String.fromCharCode(65 + index),
      value: item.content
    })));
  }
  
  await questionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (questionForm.questionId) {
          // 编辑试题
          await updateQuestion(questionForm);
          ElMessage.success('更新试题成功');
        } else {
          // 添加试题
          await addQuestion(questionForm);
          ElMessage.success('添加试题成功');
        }
        questionEditDialog.visible = false;
        
        // 刷新题库列表
        fetchQuestionList();
      } catch (error) {
        console.error('保存试题失败', error);
        ElMessage.error('保存试题失败: ' + (error.message || '未知错误'));
      }
    }
  });
};

/**
 * 加载题目类型列表
 */
const loadQuestionTypes = async () => {
  try {
    const response = await getQuestionTypeList();
    if (response.code === 200 && response.data) {
      questionTypeOptions.value = response.data.rows;
    } else {
      throw new Error(response.msg || '获取题目类型列表返回数据格式错误');
    }
  } catch (error) {
    console.error('获取题目类型列表失败', error);
    ElMessage.error('获取题目类型列表失败: ' + (error.message || '未知错误'));
    questionTypeOptions.value = [];
  }
};

/**
 * 关闭题目预览对话框
 */
const handleCloseQuestionPreview = () => {
  questionPreviewDialog.visible = false;
  currentQuestion.questionId = undefined;
  currentQuestion.questionTypeId = undefined;
  currentQuestion.typeName = '';
  currentQuestion.questionContent = '';
  currentQuestion.options = [];
  currentQuestion.correctAnswer = '';
  currentQuestion.explanation = '';
  currentQuestion.difficulty = '';
};

/**
 * 格式化正确答案
 */
const formatCorrectAnswer = (answer) => {
  if (!answer) return '未设置';
  
  try {
    // 如果是多选题答案格式，可能是"ABC"这样的字符串，或者JSON数组
    if (answer.length > 1 && /^[A-Z]+$/.test(answer)) {
      // 如果是"ABC"这样的字符串，直接以逗号分隔显示
      return answer.split('').join(', ');
    } else if (answer.startsWith('[') && answer.endsWith(']')) {
      // 如果是JSON格式的多选题答案，尝试解析并格式化
      const answerArray = JSON.parse(answer);
      if (Array.isArray(answerArray)) {
        return answerArray.map(item => {
          // 如果数组元素是数字，转为字母
          if (!isNaN(item)) {
            return String.fromCharCode(65 + parseInt(item));
          }
          return item;
        }).join(', ');
      }
    }
    
    // 单选题，如果是数字，转为对应字母
    if (!isNaN(answer) && answer.length === 1) {
      return String.fromCharCode(65 + parseInt(answer));
    }
    
    // 如果不是上述情况，直接返回答案
    return answer;
  } catch (error) {
    console.error('格式化答案出错', error);
    return answer;
  }
};

/**
 * 处理题库删除
 */
const deleteQuestion = (question) => {
  ElMessageBox.confirm(
    `确定要删除试题"${formatQuestionContent(question.questionContent)}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteQuestionApi(question.questionId);
      ElMessage.success('试题删除成功');
      
      // 确保查询参数中包含课程ID
      questionQueryParams.courseId = currentCourse.value.courseId;
      
      // 刷新题库列表
      fetchQuestionList();
    } catch (error) {
      console.error('删除试题失败', error);
      ElMessage.error('删除试题失败: ' + (error.message || '未知错误'));
    }
  }).catch(() => {
    // 取消删除
  });
};

/**
 * 处理题库格式化
 */
const formatQuestionContent = (content) => {
  if (!content) return '';
  
  // 移除HTML标签并截取部分内容
  const plainText = content.replace(/<[^>]+>/g, '');
  
  // 如果内容过长，截取前100个字符并添加省略号
  if (plainText.length > 100) {
    return plainText.substring(0, 100) + '...';
  }
  
  return plainText;
};

/**
 * 获取题目难度标签类型
 */
const getDifficultyTag = (difficulty) => {
  switch (difficulty) {
    case 'easy': return 'success';
    case 'medium': return 'warning';
    case 'hard': return 'danger';
    default: return 'info';
  }
};

/**
 * 获取题目难度标签文本
 */
const getDifficultyLabel = (difficulty) => {
  switch (difficulty) {
    case 'easy': return '简单';
    case 'medium': return '中等';
    case 'hard': return '困难';
    default: return '未知';
  }
};

// 打开创建作业对话框
const openCreateAssignmentDialog = () => {
  assignmentDialog.visible = true
  assignmentDialog.title = '发布作业'
  resetAssignmentForm()
  
  // 确保在重置表单后设置课程ID
  if (currentCourse.value && currentCourse.value.courseId) {
    assignmentForm.courseId = currentCourse.value.courseId
    console.log('设置courseId:', assignmentForm.courseId) // 添加日志便于调试
  }
  
  // 设置默认发布时间为当前时间
  const now = new Date()
  assignmentForm.startTime = now.getFullYear() + '-' + 
    String(now.getMonth() + 1).padStart(2, '0') + '-' + 
    String(now.getDate()).padStart(2, '0') + 'T' + 
    String(now.getHours()).padStart(2, '0') + ':' + 
    String(now.getMinutes()).padStart(2, '0') + ':' + 
    String(now.getSeconds()).padStart(2, '0')
  
  // 设置默认截止时间为7天后
  const endDate = new Date()
  endDate.setDate(endDate.getDate() + 7)
  assignmentForm.dueTime = endDate.getFullYear() + '-' + 
    String(endDate.getMonth() + 1).padStart(2, '0') + '-' + 
    String(endDate.getDate()).padStart(2, '0') + 'T' + 
    String(endDate.getHours()).padStart(2, '0') + ':' + 
    String(endDate.getMinutes()).padStart(2, '0') + ':' + 
    String(endDate.getSeconds()).padStart(2, '0')
    
  // 加载课程题库选项
  loadCourseQuestionOptions()
}

// 重置作业表单
const resetAssignmentForm = () => {
  assignmentForm.assignmentId = undefined
  assignmentForm.title = ''
  assignmentForm.description = ''
  assignmentForm.startTime = ''
  assignmentForm.dueTime = ''
  assignmentForm.fileRequirements = ''
  assignmentForm.publicScore = true
  
  if (assignmentFormRef.value) {
    assignmentFormRef.value.resetFields()
  }
}

// 加载作业列表
const loadAssignmentList = async () => {
  if (!currentCourse.value || !currentCourse.value.courseId) return
  
  assignmentLoading.value = true
  assignmentQueryParams.courseId = currentCourse.value.courseId
  
  try {
    console.log('正在加载课程ID为', currentCourse.value.courseId, '的作业列表')
    const response = await getAssignmentList(assignmentQueryParams)
    assignmentList.value = response.data.rows || []
    assignmentTotal.value = response.data.total || 0
    console.log('加载到作业:', assignmentList.value.length, '条')
  } catch (error) {
    console.error('获取作业列表失败', error)
    ElMessage.error('获取作业列表失败')
  } finally {
    assignmentLoading.value = false
  }
}

// 加载课程题库选项
const loadCourseQuestionOptions = async () => {
  if (!currentCourse.value || !currentCourse.value.courseId) return
  
  questionSelectLoading.value = true
  try {
    const params = {
      pageNum: 1,
      pageSize: 100, // 获取更多题目供选择
      courseId: currentCourse.value.courseId
    }
    console.log('正在加载课程ID为', currentCourse.value.courseId, '的题库')
    const response = await getQuestionList(params)
    courseQuestionOptions.value = (response.data.rows || []).map(item => ({
      questionId: item.questionId,
      questionTitle: item.questionContent.replace(/<[^>]+>/g, '').substring(0, 50) + (item.questionContent.length > 50 ? '...' : ''),
      typeName: item.typeName,
      difficulty: item.difficulty
    }))
    console.log('加载到题目:', courseQuestionOptions.value.length, '条')
  } catch (error) {
    console.error('获取课程题库失败', error)
    ElMessage.error('获取课程题库失败')
  } finally {
    questionSelectLoading.value = false
  }
}

// 提交作业表单
const submitAssignmentForm = async () => {
  if (!assignmentFormRef.value) return
  
  // 确保 courseId 正确设置
  if (!assignmentForm.courseId && currentCourse.value && currentCourse.value.courseId) {
    assignmentForm.courseId = currentCourse.value.courseId
  }
  
  await assignmentFormRef.value.validate(async (valid) => {
    if (valid) {
      // 校验截止时间必须大于发布时间
      const startTime = new Date(assignmentForm.startTime).getTime()
      const dueTime = new Date(assignmentForm.dueTime).getTime()
      
      if (dueTime <= startTime) {
        ElMessage.error('截止时间必须晚于发布时间')
        return
      }
      
      // 确保提交前 courseId 已设置
      if (!assignmentForm.courseId) {
        ElMessage.error('课程ID不能为空')
        return
      }
      
      assignmentSubmitLoading.value = true
      try {
        let response
        if (assignmentForm.assignmentId) {
          // 修改
          response = await updateAssignment(assignmentForm)
          ElMessage.success('修改成功')
        } else {
          // 新增
          response = await addAssignment(assignmentForm)
          ElMessage.success('发布成功')
          // 新增成功后，打开关联题目对话框
          if (response && response.code === 200) {
            openAssignQuestionDialog(response.data)
          }
        }
        assignmentDialog.visible = false
        loadAssignmentList()
      } catch (error) {
        console.error('保存作业失败', error)
        ElMessage.error('保存作业失败')
      } finally {
        assignmentSubmitLoading.value = false
      }
    }
  })
}

// 分页大小变化
const handleAssignmentSizeChange = (size) => {
  assignmentQueryParams.pageSize = size
  loadAssignmentList()
}

// 页码变化
const handleAssignmentCurrentChange = (current) => {
  assignmentQueryParams.pageNum = current
  loadAssignmentList()
}

// 查看作业详情
const viewAssignment = (row) => {
  router.push(`/exam/assignment/detail/${row.assignmentId}`)
}

// 编辑作业
const editAssignment = async (row) => {
  assignmentDialog.visible = true
  assignmentDialog.title = '修改作业'
  resetAssignmentForm()
  
  try {
    assignmentLoading.value = true
    const response = await getAssignmentInfo(row.assignmentId)
    Object.assign(assignmentForm, response.data)
    // 加载课程题库选项
    await loadCourseQuestionOptions()
  } catch (error) {
    console.error('获取作业详情失败', error)
    ElMessage.error('获取作业详情失败')
  } finally {
    assignmentLoading.value = false
  }
}

// 批阅作业
const reviewAssignment = (row) => {
  // 跳转到作业提交列表页面，用于批阅
  router.push('/course/mycourse/index');
}

// 删除作业
const deleteAssignment = (row) => {
  ElMessageBox.confirm(`确定要删除作业"${row.title}"吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteAssignmentApi(row.assignmentId)
        ElMessage.success('删除成功')
        loadAssignmentList()
      } catch (error) {
        console.error('删除作业失败', error)
        ElMessage.error('删除作业失败')
      }
    })
    .catch(() => {})
}

// 状态标签样式
const getAssignmentStatusTag = (status) => {
  switch (status) {
    case 1: return 'success'
    case 2: return 'danger'
    default: return 'info'
  }
}

// 状态标签文本
const getAssignmentStatusLabel = (status) => {
  switch (status) {
    case 1: return '进行中'
    case 2: return '已截止'
    default: return '未知'
  }
}

// 页面初始化
onMounted(() => {
  getList();
  loadResourceTypeList(); // 加载资源类型列表
});

// 添加关联题目对话框
const assignQuestionDialog = reactive({
  visible: false,
  title: '添加关联题目',
  assignment: {}
});

// 打开关联题目对话框
const openAssignQuestionDialog = (assignmentId) => {
  // 获取作业详情
  getAssignmentInfo(assignmentId).then(response => {
    if (response.code === 200) {
      // 使用一个临时变量存储响应数据
      const assignmentData = response.data;
      
      // 加载已关联的题目
      if (!assignmentData.questions) {
        assignmentData.questions = [];
      }
      
      // 将更新后的对象传递给对话框
      assignQuestionDialog.assignment = assignmentData;
      assignQuestionDialog.visible = true;
    }
  }).catch(error => {
    console.error('获取作业详情失败', error);
    ElMessage.error('获取作业详情失败');
  });
};

// 关联题目
const assignQuestions = (row) => {
  openAssignQuestionDialog(row.assignmentId);
};

// 处理关联题目提交
const handleAssignQuestionsSubmit = async (data) => {
  try {
    const response = await associateQuestions(data);
    if (response.code === 200) {
      ElMessage.success('关联题目成功');
      // 刷新作业列表
      loadAssignmentList();
    } else {
      ElMessage.error(response.msg || '关联题目失败');
    }
  } catch (error) {
    console.error('关联题目失败', error);
    ElMessage.error('关联题目失败: ' + (error.message || '未知错误'));
  }
};

// 取消关联题目
const handleAssignQuestionsCancel = () => {
  assignQuestionDialog.visible = false;
};

// 考试管理相关
const examList = ref([]);
const examTotal = ref(0);
const examLoading = ref(false);
const examQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  courseId: undefined,
  title: '',
  status: undefined
});

// 考试预览相关
const examPreviewDialog = reactive({
  visible: false,
  title: '考试预览'
});
const previewExam = reactive({
  examId: undefined,
  title: '',
  description: '',
  totalPoints: 0,
  duration: 0,
  startTime: '',
  endTime: '',
  courseName: '',
  teacherName: '',
  questions: []
});
const previewLoading = ref(false);

// 罗马数字映射
const romanNumerals = ['I', 'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'IX', 'X'];

// 分组后的试题（按题型分组）
const groupedQuestions = computed(() => {
  if (!previewExam.questions || previewExam.questions.length === 0) {
    return [];
  }
  
  // 按题型分组
  const groupByType = {};
  previewExam.questions.forEach(question => {
    const typeName = question.typeName || '其他';
    if (!groupByType[typeName]) {
      groupByType[typeName] = {
        typeName: typeName,
        questions: []
      };
    }
    groupByType[typeName].questions.push(question);
  });
  
  // 转换为数组
  return Object.values(groupByType);
});

// 计算题型组的总分
const calculateGroupTotalPoints = (questions) => {
  return questions.reduce((total, question) => {
    return total + (question.points || 0);
  }, 0);
};

// 解析选项JSON字符串
const parseOptions = (optionsStr) => {
  if (!optionsStr) return {};
  try {
    return JSON.parse(optionsStr);
  } catch (e) {
    console.error('解析选项失败:', e);
    return {};
  }
};

// 加载考试列表
const loadExamList = async () => {
  if (!currentCourse.value || !currentCourse.value.courseId) return;
  
  examLoading.value = true;
  examQueryParams.courseId = currentCourse.value.courseId;
  
  try {
    const response = await getExamPaperList(examQueryParams);
    examList.value = response.data.rows || [];
    examTotal.value = response.data.total || 0;
  } catch (error) {
    console.error('获取考试列表失败', error);
    ElMessage.error('获取考试列表失败');
  } finally {
    examLoading.value = false;
  }
};

// 处理分页大小变化
const handleExamSizeChange = (size) => {
  examQueryParams.pageSize = size;
  loadExamList();
};

// 处理页码变化
const handleExamCurrentChange = (current) => {
  examQueryParams.pageNum = current;
  loadExamList();
};

// 预览考试
const viewExam = async (row) => {
  previewLoading.value = true;
  examPreviewDialog.visible = true;
  examPreviewDialog.title = `预览考试：${row.title}`;
  
  try {
    const response = await getExamPaperInfo(row.examId);
    const examData = response.data;
    
    // 更新预览数据
    Object.assign(previewExam, {
      examId: examData.examId,
      title: examData.title,
      description: examData.description,
      totalPoints: examData.totalPoints,
      duration: examData.duration,
      startTime: examData.startTime,
      endTime: examData.endTime,
      courseName: examData.courseName,
      teacherName: examData.teacherName,
      questions: examData.questions || []
    });
  } catch (error) {
    console.error('获取考试详情失败', error);
    ElMessage.error('获取考试详情失败');
  } finally {
    previewLoading.value = false;
  }
};

// 修改创建考试函数
const createExam = () => {
  // 打开创建考试对话框
  openCreateExamDialog();
};

// 编辑考试
const editExam = async (row) => {
  examDialog.visible = true;
  examDialog.title = '编辑考试';
  resetExamForm();
  
  try {
    examDialog.loading = true;
    const response = await getExamPaperInfo(row.examId);
    const examData = response.data;
    
    // 填充表单数据
    examForm.examId = examData.examId;
    examForm.courseId = examData.courseId;
    examForm.title = examData.title;
    examForm.description = examData.description;
    examForm.duration = examData.duration;
    examForm.startTime = examData.startTime;
    examForm.endTime = examData.endTime;
    examForm.status = examData.status;
  } catch (error) {
    console.error('获取考试详情失败', error);
    ElMessage.error('获取考试详情失败');
  } finally {
    examDialog.loading = false;
  }
};

// 发布考试
const publishExam = async (row) => {
  try {
    await publishExamPaper(row.examId);
    ElMessage.success('发布成功');
    loadExamList();
  } catch (error) {
    console.error('发布考试失败', error);
    ElMessage.error('发布考试失败');
  }
};

// 下架考试
const unpublishExam = async (row) => {
  try {
    await unpublishExamPaper(row.examId);
    ElMessage.success('下架成功');
    loadExamList();
  } catch (error) {
    console.error('下架考试失败', error);
    ElMessage.error('下架考试失败');
  }
};

// 删除考试
const deleteExam = (row) => {
  ElMessageBox.confirm(`确定要删除考试"${row.title}"吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteExamPaper(row.examId);
        ElMessage.success('删除成功');
        loadExamList();
      } catch (error) {
        console.error('删除考试失败', error);
        ElMessage.error('删除考试失败');
      }
    })
    .catch(() => {});
};

// 考试对话框引用和控制
const examFormRef = ref(null);
const examDialog = reactive({
  visible: false,
  title: '创建考试',
  loading: false
});

// 考试表单数据
const examForm = reactive({
  examId: undefined,
  courseId: undefined,
  title: '',
  description: '',
  duration: 60,
  startTime: '',
  endTime: '',
  status: 0
});

// 考试表单校验规则
const examRules = {
  title: [
    { required: true, message: '请输入考试标题', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择所属课程', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请设置答题时长', trigger: 'blur' },
    { type: 'number', min: 1, message: '时长不能小于1分钟', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
};

// 创建考试对话框
const openCreateExamDialog = () => {
  examDialog.visible = true;
  examDialog.title = '创建考试';
  resetExamForm();
  
  // 设置当前课程ID
  if (currentCourse.value && currentCourse.value.courseId) {
    examForm.courseId = currentCourse.value.courseId;
  }
  
  // 设置默认开始和结束时间
  const now = new Date();
  const startDate = new Date(now);
  startDate.setHours(now.getHours() + 1, 0, 0); // 设置为下一个整点
  
  const endDate = new Date(startDate);
  endDate.setHours(startDate.getHours() + 2); // 默认考试时长2小时
  
  examForm.startTime = formatDateTime(startDate);
  examForm.endTime = formatDateTime(endDate);
};

// 重置考试表单
const resetExamForm = () => {
  examForm.examId = undefined;
  examForm.courseId = undefined;
  examForm.title = '';
  examForm.description = '';
  examForm.duration = 60;
  examForm.startTime = '';
  examForm.endTime = '';
  examForm.status = 0;
  
  if (examFormRef.value) {
    examFormRef.value.resetFields();
  }
};

// 格式化日期时间
const formatDateTime = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  
  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
};

// 提交考试表单
const submitExamForm = async () => {
  if (!examFormRef.value) return;
  
  await examFormRef.value.validate(async (valid) => {
    if (valid) {
      // 校验结束时间必须晚于开始时间
      const startTime = new Date(examForm.startTime).getTime();
      const endTime = new Date(examForm.endTime).getTime();
      
      if (endTime <= startTime) {
        ElMessage.error('结束时间必须晚于开始时间');
        return;
      }
      
      examDialog.loading = true;
      try {
        // 准备提交的数据
        const examData = {
          examId: examForm.examId,
          courseId: examForm.courseId,
          title: examForm.title,
          description: examForm.description,
          duration: examForm.duration,
          startTime: examForm.startTime,
          endTime: examForm.endTime,
          status: examForm.status
        };
        
        let response;
        if (examForm.examId) {
          // 更新考试
          response = await updateExamPaper(examData);
          ElMessage.success('更新考试成功');
        } else {
          // 创建考试
          response = await addExamPaper(examData);
          ElMessage.success('创建考试成功');
        }
        
        examDialog.visible = false;
        // 刷新考试列表
        loadExamList();
      } catch (error) {
        console.error('保存考试失败', error);
        ElMessage.error('保存考试失败: ' + (error.message || '未知错误'));
      } finally {
        examDialog.loading = false;
      }
    }
  });
};

// 添加考试对话框组件

// 处理关闭考试对话框
const handleCloseExamDialog = () => {
  examDialog.visible = false;
  resetExamForm();
};

// 处理课程选择变化
const handleCourseChange = () => {
  // 这里可以添加课程变更后的相关逻辑
};

// 处理提交考试表单
const handleSubmitExam = () => {
  submitExamForm();
};

// 关联考试题目对话框
const examQuestionDialog = reactive({
  visible: false,
  exam: {}
});

// 打开关联考试题目对话框
const openExamQuestionDialog = (examId) => {
  // 获取考试详情
  getExamPaperInfo(examId).then(response => {
    if (response.code === 200) {
      // 使用一个临时变量存储响应数据
      const examData = response.data;
      
      // 加载已关联的题目
      if (!examData.questions) {
        examData.questions = [];
      }
      
      // 将更新后的对象传递给对话框
      examQuestionDialog.exam = examData;
      examQuestionDialog.visible = true;
    }
  }).catch(error => {
    console.error('获取考试详情失败', error);
    ElMessage.error('获取考试详情失败');
  });
};

// 处理考试题目选择变化
const handleExamQuestionSelectionChange = (selection) => {
  selectedExamQuestions.value = selection.map(item => item.questionId);
  selectedExamQuestionsCount.value = selectedExamQuestions.value.length;
  calculateSelectedExamQuestionsTotalPoints.value = selectedExamQuestions.value.reduce((total, questionId) => {
    const question = examQuestionList.value.find(q => q.questionId === questionId);
    return total + (question ? question.points : 0);
  }, 0);
};

// 取消关联考试题目对话框
const cancelExamQuestionDialog = () => {
  examQuestionDialog.visible = false;
};

// 关联考试题目
const associateExamQuestions = (exam) => {
  examQuestionDialog.exam = exam;
  examQuestionDialog.visible = true;
};

// 处理考试题目关联提交
const handleExamQuestionSubmit = async (data) => {
  try {
    await addExamPaperQuestions(data.questions); // 直接传入questions数组
    ElMessage.success('关联题目成功');
    loadExamList(); // 刷新考试列表
  } catch (error) {
    console.error('关联题目失败', error);
    ElMessage.error('关联题目失败: ' + (error.message || '未知错误'));
  }
};

// 确认关联考试题目对话框
const confirmExamQuestionDialog = async () => {
  if (selectedExamQuestions.value.length === 0) {
    ElMessage.warning('请选择要关联的题目');
    return;
  }
  
  examQuestionLoading.value = true;
  try {
    // 准备要提交的数据
    const questions = examQuestionList.value
      .filter(q => selectedExamQuestions.value.includes(q.questionId))
      .map((q, index) => ({
        examId: examQuestionDialog.exam.examId,
        questionId: q.questionId,
        questionOrder: index,
        points: q.points || 5 // 默认5分
      }));
    
    // 直接提交题目数组，不包装在对象中
    await addExamPaperQuestions(questions);
    
    ElMessage.success('关联题目成功');
    examQuestionDialog.visible = false;
    
    // 刷新考试列表
    loadExamList();
  } catch (error) {
    console.error('关联题目失败', error);
    ElMessage.error('关联题目失败: ' + (error.message || '未知错误'));
  } finally {
    examQuestionLoading.value = false;
  }
};

// 考试题目列表
const examQuestionList = ref([]);
const examQuestionLoading = ref(false);
const examQuestionTotal = ref(0);
const examQuestionQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  questionContent: '',
  questionTypeId: undefined,
  courseId: undefined
});

// 处理考试题目查询
const searchExamQuestions = () => {
  examQuestionQueryParams.pageNum = 1;
  // 确保查询时总是带上当前课程ID
  examQuestionQueryParams.courseId = currentCourse.value.courseId;
  fetchExamQuestionList();
};

// 重置考试题目查询
const resetExamQuestionQuery = () => {
  examQuestionQueryParams.questionContent = '';
  examQuestionQueryParams.questionTypeId = undefined;
  // 确保重置后依然保留当前课程ID
  examQuestionQueryParams.courseId = currentCourse.value.courseId;
  searchExamQuestions();
};

// 获取考试题目列表
const fetchExamQuestionList = async () => {
  // 确保课程ID已设置
  if (!examQuestionQueryParams.courseId && currentCourse.value && currentCourse.value.courseId) {
    examQuestionQueryParams.courseId = currentCourse.value.courseId;
  }
  
  // 如果没有课程ID，则不执行查询
  if (!examQuestionQueryParams.courseId) {
    ElMessage.warning('未选择课程或课程ID无效');
    examQuestionList.value = [];
    examQuestionTotal.value = 0;
    return;
  }
  
  examQuestionLoading.value = true;
  try {
    const response = await getQuestionList(examQuestionQueryParams);
    examQuestionList.value = response.data.rows || response.data.records || [];
    examQuestionTotal.value = response.data.total || 0;
  } catch (error) {
    console.error('获取考试题目列表失败', error);
    ElMessage.error('获取考试题目列表失败: ' + (error.message || '未知错误'));
    examQuestionList.value = [];
  } finally {
    examQuestionLoading.value = false;
  }
};

// 处理考试题目分页
const handleExamQuestionSizeChange = (size) => {
  examQuestionQueryParams.pageSize = size;
  fetchExamQuestionList();
};

// 处理考试题目页码
const handleExamQuestionCurrentChange = (num) => {
  examQuestionQueryParams.pageNum = num;
  fetchExamQuestionList();
};

// 计算选中题目总分
const selectedExamQuestionsCount = ref(0);
const selectedExamQuestions = ref([]);
const calculateSelectedExamQuestionsTotalPoints = ref(0);

/**
 * 批阅考试列表
 */
const gradeExamList = (row) => {
  // 跳转到考试记录列表页面，用于批阅
  router.push(`/course/assignment/index/${row.examId}`);
};

/**
 * 处理文件上传
 */
const handleFileUpload = async (options) => {
  const { file, onProgress, onSuccess, onError } = options;
  
  uploading.value = true;
  
  try {
    // 创建FormData对象
    const formData = new FormData();
    formData.append('file', file);
    
    // 使用fetch API直接上传到云服务器
    const response = await fetch('http://110.41.15.40:4000/api/upload', {
      method: 'POST',
      body: formData,
      // 如果API需要认证，可能需要添加headers
      // headers: { Authorization: `Bearer ${token}` }
    });
    
    if (!response.ok) {
      throw new Error(`上传失败: ${response.status} ${response.statusText}`);
    }
    
    const result = await response.json();
    
    // 设置文件URL和文件名
    resourceForm.fileUrl = result.fileUrl;
    resourceForm.fileName = file.name;
    
    // 添加到文件列表以显示
    fileList.value = [{
      name: file.name,
      url: result.fileUrl
    }];
    
    ElMessage.success('文件上传成功');
    onSuccess(result);
  } catch (error) {
    console.error('文件上传失败', error);
    ElMessage.error('文件上传失败: ' + (error.message || '未知错误'));
    onError(error);
  } finally {
    uploading.value = false;
  }
};

/**
 * 验证上传前的文件
 */
const beforeUpload = (file) => {
  // 这里可以添加文件大小、类型等验证
  const isLt100M = file.size / 1024 / 1024 < 100;
  
  if (!isLt100M) {
    ElMessage.error('上传文件大小不能超过100MB!');
    return false;
  }
  
  return true;
};

/**
 * 处理超出文件数量限制
 */
const handleExceed = () => {
  ElMessage.warning('最多只能上传1个文件');
};

// 创建作业
const handleCreateAssignment = async (row) => {
  try {
    // 先创建考试
    const examData = {
      courseId: row.courseId,
      examName: '课程作业',
      examType: '1', // 1表示作业类型
      startTime: new Date().toISOString(),
      endTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(), // 默认7天后截止
      duration: 0, // 作业不限时
      totalScore: 100,
      status: '1' // 1表示正常状态
    }
    
    const examResult = await createExam(examData)
    if (examResult.code === 200) {
      const examId = examResult.data.examId
      
      // 创建作业
      const assignmentData = {
        courseId: row.courseId,
        examId: examId,
        title: '课程作业',
        description: '请完成本次课程作业',
        startTime: examData.startTime,
        dueTime: examData.endTime,
        status: '1'
      }
      
      const result = await createAssignment(assignmentData)
      if (result.code === 200) {
        ElMessage.success('创建作业成功')
        // 刷新列表
        getList()
      } else {
        ElMessage.error(result.message || '创建作业失败')
      }
    } else {
      ElMessage.error(examResult.message || '创建考试失败')
    }
  } catch (error) {
    console.error('创建作业失败:', error)
    ElMessage.error('创建作业失败，请重试')
  }
}
</script>

<style scoped>
.mycourse-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.card-header span {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.search-form {
  margin-bottom: 24px;
  padding: 18px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.course-list {
  margin-bottom: 24px;
}

.course-card {
  height: 100%;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  background: #fff;
  border: none;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.course-cover {
  position: relative;
  height: 160px;
  overflow: hidden;
  margin-bottom: 0;
}

.course-cover .el-image {
  width: 100%;
  height: 100%;
  transition: transform 0.6s ease;
}

.course-card:hover .course-cover .el-image {
  transform: scale(1.05);
}

.image-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f0f2f5;
  color: #909399;
  font-size: 24px;
}

.course-status {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 1;
}

.course-status .el-tag {
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.course-info {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.course-name {
  margin: 0 0 12px 0;
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  height: 48px;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 13px;
  color: #606266;
}

.course-desc {
  font-size: 14px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  height: 42px;
  margin-bottom: 16px;
  line-height: 1.5;
}

.course-actions {
  padding: 12px 16px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
  background-color: #fafafa;
}

.course-actions .el-button {
  width: 100%;
  border-radius: 4px;
  font-weight: 500;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

/* 课程详情对话框样式 */
:deep(.course-detail-dialog .el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.course-detail-dialog .el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  margin-right: 0;
}

:deep(.course-detail-dialog .el-dialog__body) {
  padding: 0;
}

:deep(.course-detail-dialog .el-tabs__header) {
  margin: 0;
  background-color: #fafafa;
}

:deep(.course-detail-dialog .el-tabs__nav) {
  padding: 0 24px;
}

:deep(.course-detail-dialog .el-tabs__item) {
  height: 56px;
  line-height: 56px;
  font-size: 15px;
}

:deep(.course-detail-dialog .el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
}

.course-detail-info {
  padding: 24px;
}

.detail-item {
  margin-bottom: 20px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-item h4 {
  margin: 0 0 8px 0;
  font-size: 15px;
  color: #606266;
  font-weight: 500;
}

.detail-item p {
  margin: 0;
  font-size: 15px;
  color: #303133;
  line-height: 1.5;
}

.tab-container {
  padding: 24px;
  min-height: 300px;
}

.placeholder-text {
  color: #909399;
  font-style: italic;
  margin-bottom: 20px;
}

/* 讨论区管理样式 */
.discussion-section {
  margin-bottom: 28px;
}

/* 题库管理样式 */
.question-section {
  margin-bottom: 28px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.question-search-form {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fb;
  border-radius: 6px;
}

.question-search-form .el-form-item {
  margin-bottom: 0;
}

.question-search-form .el-form-item:last-child {
  margin-left: 10px;
}

/* 题目预览样式 */
.question-preview-content {
  padding: 16px;
}

:deep(.question-preview-content .el-descriptions) {
  --el-descriptions-table-border: 1px solid #ebeef5;
  border-radius: 6px;
  overflow: hidden;
}

:deep(.question-preview-content .el-descriptions__label) {
  font-weight: 500;
  background-color: #f8f9fb;
}

.question-options {
  margin-top: 12px;
}

.option-item {
  display: flex;
  margin-bottom: 10px;
  padding: 8px 12px;
  background-color: #f8f9fb;
  border-radius: 4px;
}

.option-label {
  flex: 0 0 30px;
  font-weight: bold;
}

.option-content {
  flex: 1;
}

/* 增强预览内容样式 */
:deep(.el-descriptions-item__content) {
  word-break: break-word;
}

.question-preview-content img {
  max-width: 100%;
  height: auto;
}

.question-preview-content pre {
  background-color: #f6f8fa;
  border-radius: 4px;
  padding: 12px;
  overflow-x: auto;
}

.question-preview-content code {
  background-color: rgba(175, 184, 193, 0.2);
  border-radius: 4px;
  padding: 0.2em 0.4em;
  font-family: ui-monospace, SFMono-Regular, SF Mono, Menlo, Consolas, "Liberation Mono", monospace;
}

.form-help-text {
  color: #909399;
  font-size: 13px;
  margin-top: 5px;
}

/* 选择题目样式 */
.question-select-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.question-select-info {
  display: flex;
  gap: 5px;
}

:deep(.el-select-dropdown__item) {
  padding: 8px 12px;
}

:deep(.el-table) {
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-table th) {
  background-color: #f8f9fb;
  font-weight: 600;
  color: #606266;
}

:deep(.el-table--border, .el-table--group) {
  border: 1px solid #ebeef5;
}

:deep(.el-table--border th, .el-table--border td) {
  border-right: 1px solid #ebeef5;
}

:deep(.el-table--border::after, .el-table--group::after) {
  width: 0;
}

/* 改进选择题目选项样式 */
.option-form-item {
  display: flex;
  margin-bottom: 15px;
}

.option-form-content {
  display: flex;
  align-items: center;
  width: 100%;
}

.option-form-content .el-input {
  flex: 1;
  margin-right: 10px;
}

.option-error {
  color: #F56C6C;
  font-size: 12px;
  margin-top: 5px;
}

/* 作业表单样式优化 */
:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input.is-disabled .el-input__wrapper) {
  background-color: #f8f9fb;
}

:deep(.el-form--label-top .el-form-item__label) {
  margin-bottom: 8px;
}

:deep(.el-select .el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}

:deep(.el-button--primary) {
  --el-button-hover-bg-color: #409eff;
  --el-button-hover-border-color: #409eff;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
}

.file-info {
  padding: 12px;
  background-color: #f8f9fb;
  border-radius: 4px;
  margin-bottom: 12px;
  border: 1px solid #ebeef5;
}

.file-info div {
  margin-bottom: 6px;
}

.file-info div:last-child {
  margin-bottom: 0;
}

/* 资源管理样式优化 */
.resource-section,
.question-section,
.assignment-section,
.discussion-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

.question-options {
  background-color: #f8f9fb;
  border-radius: 4px;
  padding: 12px;
  border: 1px solid #ebeef5;
}

/* 按钮样式增强 */
:deep(.el-button) {
  font-weight: 500;
}

:deep(.el-button--primary) {
  background-color: #409eff;
}

:deep(.el-button--success) {
  background-color: #67c23a;
}

:deep(.el-button--warning) {
  background-color: #e6a23c;
}

:deep(.el-button--danger) {
  background-color: #f56c6c;
}

:deep(.el-button.is-round) {
  border-radius: 20px;
}

/* 表格增强 */
:deep(.el-table__row) {
  transition: background-color 0.3s;
}

:deep(.el-table__row:hover > td) {
  background-color: #f0f7ff !important;
}

/* 关联题目对话框样式 */
.assignment-info {
  background-color: #f8f9fb;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.assignment-info h3 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
  color: #303133;
}

.assignment-info p {
  margin: 5px 0;
  color: #606266;
}

.assign-question-footer {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.total-points {
  font-size: 14px;
  color: #606266;
}

.total-points span {
  font-weight: bold;
  color: #409eff;
}

.mb-20 {
  margin-bottom: 20px;
}

/* 考试管理样式 */
.exam-section {
  margin-bottom: 28px;
}

.exam-search-form {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fb;
  border-radius: 6px;
}

.paper-preview {
  padding: 20px;
}

.paper-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #ebeef5;
}

.paper-header h1 {
  font-size: 24px;
  margin-bottom: 15px;
}

.paper-info {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 10px;
  color: #606266;
}

.paper-description {
  margin-top: 15px;
  color: #606266;
  font-size: 14px;
}

.question-section {
  margin-bottom: 30px;
}

.question-section .section-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.question-section h2 {
  font-size: 18px;
  margin: 0;
  margin-right: 10px;
}

.section-info {
  color: #606266;
}

.question-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background-color: #fff;
}

.question-header {
  display: flex;
  margin-bottom: 10px;
}

.question-number {
  margin-right: 10px;
  font-weight: bold;
}

.question-title {
  flex: 1;
}

.question-score {
  margin-left: 10px;
  color: #f56c6c;
  font-weight: bold;
}

.question-options {
  margin-left: 25px;
}

.option-item {
  display: flex;
  margin-bottom: 10px;
}

.option-label {
  min-width: 25px;
  font-weight: bold;
}

.option-content {
  flex: 1;
}

.question-blank, .question-answer-area {
  margin-left: 25px;
  margin-top: 15px;
  padding: 10px;
  background-color: #f8f9fb;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
}

.question-answer, .question-analysis {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #ebeef5;
}

.answer-label, .analysis-label {
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

/* 资源列表样式 */
.resource-info {
  display: flex;
  padding: 10px 0;
}

.resource-icon {
  flex: 0 0 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  color: #409eff;
  font-size: 24px;
}

.resource-detail {
  flex: 1;
  overflow: hidden;
}

.resource-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.resource-description {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  height: 36px;
}

.resource-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 12px;
  color: #606266;
}

.meta-item {
  display: inline-flex;
  align-items: center;
}
</style> 