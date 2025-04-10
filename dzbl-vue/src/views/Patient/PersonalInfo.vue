<template>
  <div class="personal-info">
    <div class="left-section">
      <!-- 左上：用户信息 -->
      <div class="user-info">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>个人基本信息</span>
              <el-button type="primary" @click="handleEdit">编辑</el-button>
            </div>
          </template>
          <div class="info-content">
            <el-descriptions :column="3" border>
              <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ userInfo.gender }}</el-descriptions-item>
              <el-descriptions-item label="出生日期">{{ userInfo.birthdate }}</el-descriptions-item>
              <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
              <el-descriptions-item label="身份证号" :span="2">{{ userInfo.idCard }}</el-descriptions-item>
              <el-descriptions-item label="地址" :span="3">{{ userInfo.address }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </div>

      <!-- 左下：周日历 -->
      <div class="week-calendar-container">
        <div class="week-view">
          <!-- 左上角空白区域 -->
          <div class="corner-space"></div>
          
          <!-- 日期头部 -->
          <div class="days-header">
            <div v-for="(day, index) in weekDays" :key="index" class="day-header">
              <div class="day-name">{{ day.format('ddd') }}</div>
              <div class="day-date" :class="{ 'current-day': day.isSame(new Date(), 'day') }">
                {{ day.format('DD') }}
              </div>
            </div>
          </div>

          <!-- 时间轴和内容区域的容器 -->
          <div class="calendar-body">
            <!-- 时间轴 -->
            <div class="time-axis">
              <div v-for="hour in hours" :key="hour" class="time-slot">
                {{ hour.toString().padStart(2, '0') }}:00
              </div>
            </div>
            
            <!-- 时间格子 -->
            <div class="time-grid">
              <div v-for="hour in hours" :key="hour" class="hour-row">
                <div v-for="(day, index) in weekDays" 
                     :key="index" 
                     class="time-cell"
                     @click="handleCellClick(day, hour)">
                  <!-- 显示该时间段的事项 -->
                  <div v-for="event in getEventsForTimeSlot(day, hour)" 
                       :key="event.id"
                       class="event-item"
                       :style="{ backgroundColor: event.type ? typeColors[event.type] : '#64B5F6' }">
                    <div class="event-content">{{ event.title }}</div>
                    <el-button
                      class="delete-btn"
                      type="text"
                      size="small"
                      @click.stop="handleDeleteEvent(event.id)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 添加事项对话框 -->
        <el-dialog
          title="添加待办事项"
          v-model="eventDialogVisible"
          width="30%">
          <el-form :model="newEvent" label-width="80px">
            <el-form-item label="标题">
              <el-input v-model="newEvent.title" />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="newEvent.type" placeholder="请选择类型">
                <el-option label="咨询" value="consultation" />
                <el-option label="手术" value="operation" />
                <el-option label="检查" value="analysis" />
                <el-option label="康复" value="rehabilitation" />
              </el-select>
            </el-form-item>
            <el-form-item label="时长">
              <el-input-number v-model="newEvent.duration" :min="30" :step="30" step-strictly />
              <span class="ml-2">分钟</span>
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="eventDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="handleEventSubmit">确定</el-button>
            </span>
          </template>
        </el-dialog>
      </div>
    </div>

    <!-- 右侧：月日历 -->
    <div class="right-section">
      <div class="calendar-container">
        <!-- 农历日历 -->
        <div class="lunar-calendar" ref="lunarChartRef"></div>
        
        <!-- 其他内容可以放在这里 -->
        <div class="calendar-content">
          <!-- 可以添加其他内容 -->
        </div>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      title="编辑个人信息"
      v-model="dialogVisible"
      width="50%"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="form.birthdate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { Calendar, ArrowLeft, ArrowRight, Delete } from '@element-plus/icons-vue'
import { solarToLunar } from 'lunar-calendar'
import { getUserId } from '@/utils/auth'

const userInfo = ref({})
const dialogVisible = ref(false)
let weekChart = null
let lunarChart = null

const form = ref({
  name: '',
  gender: '',
  birthdate: '',
  phone: '',
  idCard: '',
  address: ''
})

// 生成小时数组（8:00 - 15:00）
const hours = Array.from({ length: 8 }, (_, i) => i + 8)

// 获取本周的日期
const weekDays = computed(() => {
  const today = dayjs()
  const weekStart = today.startOf('week')
  return Array.from({ length: 7 }, (_, i) => weekStart.add(i, 'day'))
})

// 事项类型颜色
const typeColors = {
  consultation: '#81C784', // 咨询-绿色
  operation: '#FFB74D',    // 手术-橙色
  analysis: '#64B5F6',     // 检查-蓝色
  rehabilitation: '#9575CD' // 康复-紫色
}

// 使用 localStorage 的替代方案
const events = ref([])

// 获取日程数据
const getEvents = () => {
  const savedEvents = localStorage.getItem(`events_${getUserId()}`)
  if (savedEvents) {
    events.value = JSON.parse(savedEvents)
  } else {
    events.value = [] // 如果没有存储的数据，初始化为空数组
  }
}

// 待办事项数据
const eventDialogVisible = ref(false)
const newEvent = ref({
  title: '',
  type: '',
  duration: 30,
  date: null,
  hour: null
})

// 当前选中的日期
const currentDate = ref(dayjs())
const selectedDate = ref(null)

// 计算月份中的所有日期
const monthDays = computed(() => {
  const firstDay = currentDate.value.startOf('month')
  const lastDay = currentDate.value.endOf('month')
  const days = []
  
  // 填充月初空白日期
  const firstWeekday = firstDay.day()
  for (let i = 0; i < firstWeekday; i++) {
    const date = firstDay.subtract(firstWeekday - i, 'day')
    days.push({
      date,
      currentMonth: false,
      isToday: date.isSame(dayjs(), 'day'),
      lunar: getLunarDate(date)
    })
  }
  
  // 填充当月日期
  for (let i = 0; i < lastDay.date(); i++) {
    const date = firstDay.add(i, 'day')
    days.push({
      date,
      currentMonth: true,
      isToday: date.isSame(dayjs(), 'day'),
      lunar: getLunarDate(date)
    })
  }
  
  // 填充月末空白日期
  const remainingDays = 42 - days.length // 保持6行
  for (let i = 0; i < remainingDays; i++) {
    const date = lastDay.add(i + 1, 'day')
    days.push({
      date,
      currentMonth: false,
      isToday: date.isSame(dayjs(), 'day'),
      lunar: getLunarDate(date)
    })
  }
  
  return days
})

// 获取农历日期
const getLunarDate = (date) => {
  const year = date.year()
  const month = date.month() + 1
  const day = date.date()
  const lunar = solarToLunar(year, month, day)
  
  // 处理农历节日和特殊日期
  if (lunar.lunarFestival) return lunar.lunarFestival
  if (lunar.solarFestival) return lunar.solarFestival
  if (lunar.term) return lunar.term
  
  // 初一显示月份
  if (lunar.lunarDay === 1) return `${lunar.lunarMonthName}月`
  
  return lunar.lunarDayName
}

// 月份切换
const prevMonth = () => {
  currentDate.value = currentDate.value.subtract(1, 'month')
}

const nextMonth = () => {
  currentDate.value = currentDate.value.add(1, 'month')
}

// 日期选择
const isSelected = (date) => {
  return selectedDate.value && date.isSame(selectedDate.value, 'day')
}

const selectDate = (date) => {
  selectedDate.value = date
}

// 获取用户信息
const getUserInfo = async () => {
  try {
    const res = await request.get(`/users/${getUserId()}`) // 暂时固定userId为1
    userInfo.value = res.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 编辑用户信息
const handleEdit = () => {
  form.value = { ...userInfo.value }
  dialogVisible.value = true
}

// 提交编辑
const handleSubmit = async () => {
  try {
    await request.put(`/users/${getUserId()}`, form.value)
    ElMessage.success('修改成功')
    dialogVisible.value = false
    getUserInfo()
  } catch (error) {
    console.error('修改用户信息失败:', error)
  }
}

// 点击时间格子
const handleCellClick = (day, hour) => {
  newEvent.value = {
    title: '',
    type: '',
    duration: 30,
    date: day.format('YYYY-MM-DD'),
    hour
  }
  eventDialogVisible.value = true
}

// 提交新事项
const handleEventSubmit = () => {
  const newEventData = {
    id: Date.now(),
    userId: getUserId(),
    ...newEvent.value
  }
  
  events.value.push(newEventData)
  localStorage.setItem(`events_${getUserId()}`, JSON.stringify(events.value))
  eventDialogVisible.value = false
  ElMessage.success('添加日程成功')
}

// 获取指定时间段的事项
const getEventsForTimeSlot = (day, hour) => {
  return events.value.filter(event => 
    event.date === day.format('YYYY-MM-DD') && 
    event.hour === hour
  )
}

// 添加删除事项方法
const handleDeleteEvent = (eventId) => {
  ElMessageBox.confirm(
    '确定要删除这个日程吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      events.value = events.value.filter(event => event.id !== eventId)
      localStorage.setItem(`events_${getUserId()}`, JSON.stringify(events.value))
      ElMessage.success('删除成功')
    })
    .catch(() => {
      // 取消删除操作
    })
}

// 初始化周日历
const initWeekChart = () => {
  const weekChartRef = document.querySelector('.week-calendar')
  if (!weekChartRef) return

  weekChart = echarts.init(weekChartRef)
  const now = new Date()
  const weekStart = new Date(now)
  weekStart.setDate(now.getDate() - now.getDay())

  const option = {
    title: {
      text: '本周日程',
      left: 'center'
    },
    tooltip: {
      formatter: '{c}'
    },
    calendar: {
      range: [weekStart, new Date(weekStart.getTime() + 6 * 24 * 3600 * 1000)],
      dayLabel: {
        firstDay: 0,
        nameMap: ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      },
      monthLabel: { show: false },
      yearLabel: { show: false }
    },
    series: {
      type: 'scatter',
      coordinateSystem: 'calendar',
      symbolSize: 0,
      label: {
        show: true,
        formatter: (params) => {
          const date = new Date(params.value[0])
          return date.getDate()
        }
      }
    }
  }

  weekChart.setOption(option)
}

// 获取农历数据
const getYearData = () => {
  const year = dayjs().year()
  const data = []
  
  // 生成全年的数据
  for (let month = 0; month < 12; month++) {
    const date = dayjs().year(year).month(month).startOf('month')
    const daysInMonth = date.daysInMonth()
    
    for (let day = 1; day <= daysInMonth; day++) {
      const currentDate = date.date(day)
      data.push([
        currentDate.format('YYYY-MM-DD'),
        day
      ])
    }
  }
  
  return data
}

// 初始化农历日历
const initLunarCalendar = () => {
  const chartDom = document.querySelector('.lunar-calendar')
  if (!chartDom) return
  
  lunarChart = echarts.init(chartDom)
  const width = chartDom.offsetWidth
  const cellSize = [width / 7, width / 7 * 0.8] // 调整单元格宽高比
  
  const option = {
    title: {
      text: dayjs().format('YYYY年'),
      left: 5,
      top: 5,
    },
    tooltip: {
      position: 'top',
      formatter: (params) => {
        const date = dayjs(params.value[0])
        return date.format('YYYY-MM-DD')
      }
    },
    calendar: [{
      left: 'center',
      top: 40,
      orient: 'vertical',
      cellSize: cellSize,
      range: dayjs().format('YYYY'),
      itemStyle: {
        borderWidth: 1,
        borderColor: '#eee'
      },
      yearLabel: { show: false },
      dayLabel: {
        firstDay: 1,
        nameMap: ['日', '一', '二', '三', '四', '五', '六'],
        margin: 2,
        fontSize: 10
      },
      monthLabel: {
        show: true,
        margin: 5,
        fontSize: 12,
        formatter: '{MM}月'
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: '#eee',
          width: 1,
          type: 'solid'
        }
      }
    }],
    series: {
      type: 'scatter',
      coordinateSystem: 'calendar',
      symbolSize: function (val) {
        return val[1] === dayjs().date() && 
               dayjs(val[0]).month() === dayjs().month() ? 6 : 0;
      },
      itemStyle: {
        color: '#409EFF'
      },
      label: {
        show: true,
        formatter: (params) => {
          return params.value[1]
        },
        fontSize: 10,
        fontWeight: function (params) {
          return params.value[1] === dayjs().date() && 
                 dayjs(params.value[0]).month() === dayjs().month() ? 'bold' : 'normal';
        }
      },
      data: getYearData()
    }
  }
  
  lunarChart.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  if (lunarChart) {
    const chartDom = document.querySelector('.lunar-calendar')
    const cellSize = [chartDom.offsetWidth / 7, chartDom.offsetWidth / 7]
    lunarChart.setOption({
      calendar: {
        cellSize: cellSize
      }
    })
    lunarChart.resize()
  }
}

onMounted(async () => {
  await getUserInfo()
  getEvents()
  initWeekChart()
  initLunarCalendar()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  weekChart?.dispose()
  lunarChart?.dispose()
})
</script>

<style scoped>
.personal-info {
  height: 100%;
  padding: 20px;
  background-color: #f5f7fa;
  box-sizing: border-box;
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
}

.left-section {
  display: grid;
  grid-template-rows: auto 1fr;
  gap: 20px;
}

.user-info {
  min-height: 360px;
}

.week-calendar-container {
  height: 100%;
  min-height: 300px;
}

.right-section {
  height: 100%;
}

.box-card,
.week-view,
.calendar-container {
  height: 100%;
  background-color: white;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-content {
  height: calc(100% - 60px);
  overflow: auto;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 确保内容不会溢出 */
:deep(.el-card__body) {
  height: calc(100% - 55px);
  overflow: auto;
}

:deep(.el-descriptions) {
  padding: 0;
  .el-descriptions__body {
    padding: 12px 0;
  }
  .el-descriptions__cell {
    padding: 12px;
  }
}

/* 修改周视图相关样式 */
.week-view {
  display: grid;
  grid-template-rows: auto 1fr;
  grid-template-columns: 60px 1fr;
  height: 100%;
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.corner-space {
  background: #f8f9fa;
  border-right: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.days-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.day-header {
  padding: 8px;
  text-align: center;
  border-right: 1px solid #eee;
}

.calendar-body {
  display: flex;
  overflow: hidden;
  grid-column: 1 / -1;  /* 确保calendar-body跨越所有列 */
}

.time-axis {
  width: 60px;
  flex-shrink: 0;
  border-right: 1px solid #eee;
  background: #f8f9fa;
}

.time-slot {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #666;
}

.time-grid {
  flex: 1;
  overflow-y: auto;
  display: grid;
  grid-template-rows: repeat(8, 60px);  /* 8个小时的格子 */
}

.hour-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-bottom: 1px solid #eee;
}

.time-cell {
  border-right: 1px solid #eee;
  border-bottom: 1px solid #eee;
  padding: 2px;
  position: relative;
  cursor: pointer;
  min-height: 60px;
}

.time-cell:hover {
  background-color: rgba(24, 144, 255, 0.1);
}

.event-item {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
  margin-bottom: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-height: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.event-content {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.delete-btn {
  color: white;
  opacity: 0;
  transition: opacity 0.2s;
  padding: 2px;
}

.event-item:hover .delete-btn {
  opacity: 1;
}

/* 修改删除按钮的图标大小 */
:deep(.delete-btn .el-icon) {
  font-size: 14px;
}

/* 滚动条样式 */
.time-grid::-webkit-scrollbar {
  width: 6px;
}

.time-grid::-webkit-scrollbar-thumb {
  background-color: #ddd;
  border-radius: 3px;
}

.time-grid::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}

.calendar-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.lunar-calendar {
  width: 100%;
  height: 100%;
  padding: 10px;
}

.calendar-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
}
</style> 