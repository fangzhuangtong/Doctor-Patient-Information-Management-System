<template>
  <div class="personal-info">
    <!-- 左侧区域 -->
    <div class="left-section">
      <!-- 上方：个人信息 -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>个人基本信息</span>
            <el-button type="primary" @click="handleEdit">编辑</el-button>
          </div>
        </template>
        <div class="info-content">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="医生ID">{{ doctorInfo.doctorId }}</el-descriptions-item>
            <el-descriptions-item label="科室ID">{{ doctorInfo.departmentId }}</el-descriptions-item>
            <el-descriptions-item label="职称">{{ doctorInfo.title }}</el-descriptions-item>
            <el-descriptions-item label="执业证号">{{ doctorInfo.licenseNo }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatTime(doctorInfo.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>

      <!-- 下方：周日历 -->
      <div class="calendar-container">
        <el-table 
          :data="calendarData" 
          style="width: 100%" 
          border
          :height="tableHeight"
        >
          <!-- 时间列 -->
          <el-table-column prop="time" label="时间" width="80" fixed>
            <template #default="scope">
              {{ scope.row.time }}:00
            </template>
          </el-table-column>
          
          <!-- 周日至周六的列 -->
          <el-table-column 
            v-for="(day, index) in weekDays" 
            :key="index"
            :label="day.format('ddd')"
            :prop="'day' + index"
            class="schedule-column"
          >
            <template #header>
              <div class="day-header">
                <div>{{ day.format('ddd') }}</div>
                <div :class="{ 'current-day': day.isSame(new Date(), 'day') }">
                  {{ day.format('DD') }}
                </div>
              </div>
            </template>
            <template #default="scope">
              <div 
                class="schedule-cell"
                @click.stop="handleCellClick(scope.row.time, index, scope.row['day' + index])"
              >
                <el-tag 
                  v-if="scope.row['day' + index]"
                  :type="getScheduleType(scope.row['day' + index])"
                  size="small"
                  @click.stop="handleCellClick(scope.row.time, index, scope.row['day' + index])"
                >
                  {{ scope.row['day' + index] }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 右侧：工作统计 -->
    <div class="right-section">
      <el-card class="stats-container">
        <template #header>
          <div class="card-header">
            <span>工作统计</span>
          </div>
        </template>
        <div class="stats-content">
          <div class="stats-item">
            <div class="stats-title">今日门诊</div>
            <div class="stats-value">{{ stats.todayOutpatient }}</div>
          </div>
          <div class="stats-item">
            <div class="stats-title">今日住院</div>
            <div class="stats-value">{{ stats.todayInpatient }}</div>
          </div>
          <div class="stats-item">
            <div class="stats-title">待处理检查</div>
            <div class="stats-value">{{ stats.pendingChecks }}</div>
          </div>
          <div class="stats-item">
            <div class="stats-title">待回复咨询</div>
            <div class="stats-value">{{ stats.pendingConsults }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      title="编辑个人信息"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="科室ID" prop="departmentId">
          <el-input v-model.number="form.departmentId" type="number" />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-select v-model="form.title" placeholder="请选择职称">
            <el-option label="主任医师" value="主任医师" />
            <el-option label="副主任医师" value="副主任医师" />
            <el-option label="主治医师" value="主治医师" />
            <el-option label="住院医师" value="住院医师" />
          </el-select>
        </el-form-item>
        <el-form-item label="执业证号" prop="licenseNo">
          <el-input v-model="form.licenseNo" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加待办事项对话框 -->
    <el-dialog
      v-model="scheduleDialogVisible"
      :title="scheduleDialogTitle"
      width="30%"
    >
      <el-form :model="scheduleForm" label-width="80px">
        <el-form-item label="类型">
          <el-select v-model="scheduleForm.type" placeholder="请选择类型">
            <el-option label="门诊" value="门诊" />
            <el-option label="手术" value="手术" />
            <el-option label="查房" value="查房" />
            <el-option label="会诊" value="会诊" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="scheduleForm.note" 
            type="textarea" 
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scheduleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleScheduleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'
import { getUserId, getRole } from '@/utils/auth'

// 检查用户角色和ID
const role = getRole()
const doctorId = getUserId()

if (!doctorId || role !== 'doctor') {
  ElMessage.error('无法获取医生信息，请重新登录')
}

// 医生信息
const doctorInfo = ref({})
const dialogVisible = ref(false)
const formRef = ref(null)

// 编辑表单
const form = ref({
  departmentId: '',
  title: '',
  licenseNo: ''
})

// 表单验证规则
const rules = {
  departmentId: [
    { required: true, message: '请输入科室ID', trigger: 'blur' },
    { type: 'number', message: '科室ID必须为数字', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请选择职称', trigger: 'change' }
  ],
  licenseNo: [
    { required: true, message: '请输入执业证号', trigger: 'blur' }
  ]
}

// 工作统计数据
const stats = ref({
  todayOutpatient: 0,
  todayInpatient: 0,
  pendingChecks: 0,
  pendingConsults: 0
})

// 添加周日历相关数据
const weekDays = ref([])
const timeSlots = ref([])

// 日历数据
const calendarData = ref([])

// 添加日程相关的响应式变量
const scheduleDialogVisible = ref(false)
const scheduleDialogTitle = ref('')
const scheduleForm = ref({
  type: '',
  note: ''
})
const currentCell = ref({
  time: null,
  dayIndex: null
})

// 添加表格高度计算
const tableHeight = ref(0)

// 计算表格高度
const calculateTableHeight = () => {
  // 获取日历容器
  const container = document.querySelector('.calendar-container')
  if (container) {
    // 设置表格高度为容器高度减去内边距
    tableHeight.value = container.clientHeight - 40 // 40是calendar-container的上下padding总和
  }
}

// 获取本地存储的日程数据
const getLocalSchedule = () => {
  const key = `doctor_schedule_${doctorId}`
  const savedSchedule = localStorage.getItem(key)
  if (savedSchedule) {
    const scheduleData = JSON.parse(savedSchedule)
    // 更新日历数据
    calendarData.value = scheduleData
  }
}

// 保存日程到本地存储
const saveLocalSchedule = () => {
  const key = `doctor_schedule_${doctorId}`
  localStorage.setItem(key, JSON.stringify(calendarData.value))
}

// 初始化周日历数据
const initWeekCalendar = () => {
  // 生成本周的日期
  const today = dayjs()
  weekDays.value = Array.from({ length: 7 }, (_, i) => {
    return today.startOf('week').add(i, 'day')
  })

  // 先检查本地存储是否有数据
  const key = `doctor_schedule_${doctorId}`
  const savedSchedule = localStorage.getItem(key)
  
  if (savedSchedule) {
    // 如果有保存的数据，直接使用
    calendarData.value = JSON.parse(savedSchedule)
  } else {
    // 如果没有保存的数据，生成空的日历数据
    calendarData.value = Array.from({ length: 10 }, (_, i) => {
      const hour = i + 8
      const rowData = {
        time: hour,
        day0: '',
        day1: '',
        day2: '',
        day3: '',
        day4: '',
        day5: '',
        day6: ''
      }
      return rowData
    })
  }
}

// 获取医生信息
const getDoctorInfo = async () => {
  if (!doctorId) return
  
  try {
    const res = await request.get(`/doctors/${doctorId}`)
    if (res.code === 1) {
      doctorInfo.value = res.data
    }
  } catch (error) {
    console.error('获取医生信息失败:', error)
    ElMessage.error('获取医生信息失败')
  }
}

// 打开编辑对话框
const handleEdit = () => {
  form.value = {
    departmentId: doctorInfo.value.departmentId,
    title: doctorInfo.value.title,
    licenseNo: doctorInfo.value.licenseNo
  }
  dialogVisible.value = true
}

// 提交编辑
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await request.put(`/doctors/${doctorId}`, form.value)
        if (res.code === 1) {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getDoctorInfo()
        }
      } catch (error) {
        console.error('修改失败:', error)
      }
    }
  })
}

// 格式化时间
const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 监听窗口大小变化
const handleResize = () => {
  // 这里不需要处理窗口大小变化，因为日历部分是固定的
}

// 处理单元格点击
const handleCellClick = (time, dayIndex, currentValue) => {
  // 如果当前单元格不可编辑，则直接返回
  if (!time || dayIndex === undefined) return
  
  currentCell.value = { time, dayIndex }
  scheduleDialogTitle.value = `添加${weekDays.value[dayIndex].format('MM-DD')} ${time}:00的日程`
  
  if (currentValue) {
    // 如果已有日程，显示当前日程信息
    const [type, note] = currentValue.split(':')
    scheduleForm.value = { type, note: note || '' }
  } else {
    // 如果是空单元格，重置表单
    scheduleForm.value = { type: '', note: '' }
  }
  
  scheduleDialogVisible.value = true
}

// 处理日程提交
const handleScheduleSubmit = () => {
  const { time, dayIndex } = currentCell.value
  const scheduleText = scheduleForm.value.note 
    ? `${scheduleForm.value.type}:${scheduleForm.value.note}`
    : scheduleForm.value.type

  // 更新对应单元格的数据
  const rowIndex = time - 8 // 因为从8点开始
  calendarData.value[rowIndex][`day${dayIndex}`] = scheduleText
  
  // 保存到本地存储
  saveLocalSchedule()
  
  scheduleDialogVisible.value = false
}

// 获取日程类型对应的标签类型
const getScheduleType = (schedule) => {
  if (!schedule) return ''
  const type = schedule.split(':')[0]
  const typeMap = {
    '门诊': 'success',
    '手术': 'danger',
    '查房': 'warning',
    '会诊': 'info'
  }
  return typeMap[type] || ''
}

onMounted(async () => {
  getDoctorInfo()
  initWeekCalendar()
  // 等待 DOM 更新后计算高度
  nextTick(() => {
    calculateTableHeight()
  })
  window.addEventListener('resize', calculateTableHeight)
})

onUnmounted(() => {
  window.removeEventListener('resize', calculateTableHeight)
})
</script>

<style scoped>
.personal-info {
  padding: 20px;
  height: 100%;
  display: grid;
  grid-template-columns: 2fr 1fr; /* 调整左右比例 */
  gap: 20px;
}

.left-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100%; /* 添加高度100% */
}

.info-card {
  flex-shrink: 0; /* 防止卡片被压缩 */
}

.right-section {
  height: 100%;
}

.stats-container {
  height: 100%;
}

.stats-content {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 20px;
  padding: 20px;
}

.stats-item {
  text-align: center;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.stats-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

/* 日历表格样式 */
.calendar-container {
  flex: 1; /* 占用剩余空间 */
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  min-height: 0; /* 允许容器缩小 */
}

.day-header {
  text-align: center;
}

.day-header div:first-child {
  font-size: 12px;
  color: #606266;
}

.day-header div:last-child {
  font-size: 16px;
  font-weight: bold;
  margin-top: 2px;
}

.current-day {
  color: #409EFF;
}

.schedule-cell {
  height: 100%;
  width: 100%;
  padding: 4px;
  cursor: pointer;  /* 添加鼠标指针样式 */
  display: flex;    /* 使用 flex 布局 */
  align-items: center;
  justify-content: center;
}

/* 添加悬停效果 */
.schedule-cell:hover {
  background-color: #f5f7fa;
}

/* 修改 el-tag 的样式 */
:deep(.el-tag) {
  margin: 0;
  text-align: center;
  width: 100%;     /* 让标签占满整个单元格 */
  cursor: pointer;  /* 标签也可以点击 */
}

/* 确保表格单元格可以正常点击 */
:deep(.el-table__cell) {
  padding: 0 !important;
  height: 100%;
}

:deep(.el-table__cell .cell) {
  height: 100%;    /* 让单元格内容区域占满高度 */
  padding: 0;      /* 移除内边距 */
}

/* 自定义表格样式 */
:deep(.el-table) {
  height: 100% !important;
}

:deep(.el-table__body-wrapper) {
  overflow-y: auto;
}

/* 调整单元格高度，使其均匀分布 */
:deep(.el-table__row) {
  height: 40px; /* 设置固定行高 */
}
</style>