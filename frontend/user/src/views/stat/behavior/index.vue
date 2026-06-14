<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>学习行为记录</span>
      </div>
      
      <!-- 行为记录表单 -->
      <el-form :model="behaviorForm" :rules="rules" ref="behaviorForm" label-width="100px">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="behaviorForm.courseId" placeholder="请选择课程">
            <el-option
              v-for="item in courseOptions"
              :key="item.courseId"
              :label="item.courseName"
              :value="item.courseId">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="行为类型" prop="behaviorType">
          <el-select v-model="behaviorForm.behaviorType" placeholder="请选择行为类型">
            <el-option label="查看资源" value="view_resource"></el-option>
            <el-option label="提交作业" value="submit_assignment"></el-option>
            <el-option label="参加考试" value="take_exam"></el-option>
            <el-option label="发表讨论" value="post_discussion"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="资源ID" prop="resourceId" v-if="behaviorForm.behaviorType === 'view_resource'">
          <el-input v-model="behaviorForm.resourceId" placeholder="请输入资源ID"></el-input>
        </el-form-item>
        
        <el-form-item label="持续时间" prop="duration">
          <el-input-number v-model="behaviorForm.duration" :min="0" :step="60" placeholder="请输入持续时间(秒)"></el-input-number>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitBehavior">记录行为</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 学习行为列表 -->
    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>学习行为记录列表</span>
      </div>
      
      <el-table
        v-loading="loading"
        :data="behaviorList"
        style="width: 100%">
        <el-table-column
          prop="behaviorTime"
          label="行为时间"
          width="180">
        </el-table-column>
        <el-table-column
          prop="courseName"
          label="课程名称"
          width="180">
        </el-table-column>
        <el-table-column
          prop="behaviorType"
          label="行为类型">
          <template slot-scope="scope">
            {{ getBehaviorTypeName(scope.row.behaviorType) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="duration"
          label="持续时间(秒)">
        </el-table-column>
        <el-table-column
          prop="clientInfo"
          label="客户端信息">
        </el-table-column>
      </el-table>
      
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script>
import { recordBehavior, getBehaviorList } from '@/api/behavior'
import { getStudentCourses } from '@/api/course'

export default {
  name: 'LearningBehavior',
  data() {
    return {
      // 行为记录表单
      behaviorForm: {
        courseId: undefined,
        behaviorType: undefined,
        resourceId: undefined,
        duration: 0
      },
      // 表单校验规则
      rules: {
        courseId: [
          { required: true, message: '请选择课程', trigger: 'change' }
        ],
        behaviorType: [
          { required: true, message: '请选择行为类型', trigger: 'change' }
        ],
        duration: [
          { required: true, message: '请输入持续时间', trigger: 'blur' }
        ]
      },
      // 课程选项
      courseOptions: [],
      // 行为列表数据
      behaviorList: [],
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      // 加载状态
      loading: false
    }
  },
  created() {
    this.getCourses()
    this.getList()
  },
  methods: {
    // 获取课程列表
    async getCourses() {
      try {
        const response = await getStudentCourses()
        this.courseOptions = response.data
      } catch (error) {
        console.error('获取课程列表失败:', error)
      }
    },
    // 获取行为列表
    async getList() {
      this.loading = true
      try {
        const response = await getBehaviorList(this.queryParams)
        this.behaviorList = response.data.rows
        this.total = response.data.total
      } catch (error) {
        console.error('获取行为列表失败:', error)
      }
      this.loading = false
    },
    // 提交行为记录
    submitBehavior() {
      this.$refs.behaviorForm.validate(async valid => {
        if (valid) {
          try {
            const data = {
              ...this.behaviorForm,
              studentId: this.$store.state.user.userId
            }
            await recordBehavior(data)
            this.$message.success('记录成功')
            this.resetForm()
            this.getList()
          } catch (error) {
            console.error('记录行为失败:', error)
          }
        }
      })
    },
    // 重置表单
    resetForm() {
      this.$refs.behaviorForm.resetFields()
    },
    // 获取行为类型名称
    getBehaviorTypeName(type) {
      const typeMap = {
        'view_resource': '查看资源',
        'submit_assignment': '提交作业',
        'take_exam': '参加考试',
        'post_discussion': '发表讨论'
      }
      return typeMap[type] || type
    }
  }
}
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}
</style> 