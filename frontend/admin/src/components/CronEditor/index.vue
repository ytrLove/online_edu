<template>
  <div class="cron-editor">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="秒" name="second">
        <el-radio-group v-model="cron.second">
          <el-radio label="*">每秒</el-radio>
          <el-radio label="0">每分钟第0秒</el-radio>
          <el-radio label="custom">自定义</el-radio>
        </el-radio-group>
        <div v-if="cron.second === 'custom'" class="custom-options">
          <el-input-number v-model="cron.secondCustom" :min="0" :max="59" />
          <span class="tip">秒 (0-59)</span>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="分" name="minute">
        <el-radio-group v-model="cron.minute">
          <el-radio label="*">每分钟</el-radio>
          <el-radio label="0">每小时第0分</el-radio>
          <el-radio label="custom">自定义</el-radio>
        </el-radio-group>
        <div v-if="cron.minute === 'custom'" class="custom-options">
          <el-input-number v-model="cron.minuteCustom" :min="0" :max="59" />
          <span class="tip">分 (0-59)</span>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="时" name="hour">
        <el-radio-group v-model="cron.hour">
          <el-radio label="*">每小时</el-radio>
          <el-radio label="0">每天0点</el-radio>
          <el-radio label="custom">自定义</el-radio>
        </el-radio-group>
        <div v-if="cron.hour === 'custom'" class="custom-options">
          <el-input-number v-model="cron.hourCustom" :min="0" :max="23" />
          <span class="tip">时 (0-23)</span>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="日" name="day">
        <el-radio-group v-model="cron.day">
          <el-radio label="*">每日</el-radio>
          <el-radio label="?">不指定</el-radio>
          <el-radio label="1">每月1日</el-radio>
          <el-radio label="custom">自定义</el-radio>
        </el-radio-group>
        <div v-if="cron.day === 'custom'" class="custom-options">
          <el-input-number v-model="cron.dayCustom" :min="1" :max="31" />
          <span class="tip">日 (1-31)</span>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="月" name="month">
        <el-radio-group v-model="cron.month">
          <el-radio label="*">每月</el-radio>
          <el-radio label="1">每年1月</el-radio>
          <el-radio label="custom">自定义</el-radio>
        </el-radio-group>
        <div v-if="cron.month === 'custom'" class="custom-options">
          <el-input-number v-model="cron.monthCustom" :min="1" :max="12" />
          <span class="tip">月 (1-12)</span>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="周" name="week">
        <el-radio-group v-model="cron.week">
          <el-radio label="*">每周</el-radio>
          <el-radio label="?">不指定</el-radio>
          <el-radio label="1">周日</el-radio>
          <el-radio label="2">周一</el-radio>
          <el-radio label="3">周二</el-radio>
          <el-radio label="4">周三</el-radio>
          <el-radio label="5">周四</el-radio>
          <el-radio label="6">周五</el-radio>
          <el-radio label="7">周六</el-radio>
          <el-radio label="custom">自定义</el-radio>
        </el-radio-group>
        <div v-if="cron.week === 'custom'" class="custom-options">
          <el-select v-model="cron.weekCustom" placeholder="请选择">
            <el-option label="周日" value="1" />
            <el-option label="周一" value="2" />
            <el-option label="周二" value="3" />
            <el-option label="周三" value="4" />
            <el-option label="周四" value="5" />
            <el-option label="周五" value="6" />
            <el-option label="周六" value="7" />
          </el-select>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="年" name="year">
        <el-radio-group v-model="cron.year">
          <el-radio label="">不指定</el-radio>
          <el-radio label="*">每年</el-radio>
          <el-radio label="custom">自定义</el-radio>
        </el-radio-group>
        <div v-if="cron.year === 'custom'" class="custom-options">
          <el-input-number v-model="cron.yearCustom" :min="1970" :max="2099" />
          <span class="tip">年 (1970-2099)</span>
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <div class="cron-preview">
      <h4>Cron表达式:</h4>
      <el-input v-model="cronExpression" readonly />
      <h4>下次执行时间:</h4>
      <el-input v-model="nextRunTime" readonly />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const activeTab = ref('second')

const cron = ref({
  second: '*',
  secondCustom: 0,
  minute: '*',
  minuteCustom: 0,
  hour: '*',
  hourCustom: 0,
  day: '*',
  dayCustom: 1,
  month: '*',
  monthCustom: 1,
  week: '?',
  weekCustom: '1',
  year: '',
  yearCustom: new Date().getFullYear()
})

const cronExpression = computed(() => {
  const parts = [
    cron.value.second === 'custom' ? cron.value.secondCustom : cron.value.second,
    cron.value.minute === 'custom' ? cron.value.minuteCustom : cron.value.minute,
    cron.value.hour === 'custom' ? cron.value.hourCustom : cron.value.hour,
    cron.value.day === 'custom' ? cron.value.dayCustom : cron.value.day,
    cron.value.month === 'custom' ? cron.value.monthCustom : cron.value.month,
    cron.value.week === 'custom' ? cron.value.weekCustom : cron.value.week,
    cron.value.year === 'custom' ? cron.value.yearCustom : cron.value.year
  ]
  return parts.join(' ').trim()
})

const nextRunTime = computed(() => {
  // 这里可以添加计算下次执行时间的逻辑
  // 实际项目中可以使用cron-parser等库来计算
  return '需要实现计算逻辑'
})

watch(cronExpression, (newVal) => {
  emit('update:modelValue', newVal)
})

watch(() => props.modelValue, (newVal) => {
  if (newVal && newVal !== cronExpression.value) {
    // 解析传入的cron表达式并更新表单
    // 实际项目中需要实现解析逻辑
  }
}, { immediate: true })
</script>

<style scoped>
.cron-editor {
  padding: 10px;
}

.custom-options {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.tip {
  color: #999;
  font-size: 12px;
}

.cron-preview {
  margin-top: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.cron-preview h4 {
  margin: 10px 0 5px;
  font-size: 14px;
  font-weight: normal;
  color: #666;
}
</style>