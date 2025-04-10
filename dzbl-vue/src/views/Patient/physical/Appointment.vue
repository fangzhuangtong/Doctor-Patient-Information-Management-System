<template>
  <div class="appointment">
    <!-- 医生列表 -->
    <div class="doctors-section">
      <!-- 科室筛选 -->
      <div class="filter-bar">
        <div class="department-tags">
          <el-button 
            :class="['dept-btn', activeFilter === 'ALL' ? 'active' : '']" 
            @click="filterByDepartment('ALL')"
          >
            全部
          </el-button>
          <el-button 
            v-for="dept in departments" 
            :key="dept.id"
            :class="['dept-btn', activeFilter === dept.name ? 'active' : '']"
            @click="filterByDepartment(dept.name)"
          >
            {{ dept.name }}
          </el-button>
        </div>
        <div class="view-toggle">
          <el-button-group>
            <el-button :type="viewMode === 'grid' ? 'primary' : ''" @click="viewMode = 'grid'">
              <el-icon><Grid /></el-icon>
            </el-button>
            <el-button :type="viewMode === 'list' ? 'primary' : ''" @click="viewMode = 'list'">
              <el-icon><List /></el-icon>
            </el-button>
          </el-button-group>
        </div>
      </div>

      <!-- 医生卡片网格 -->
      <div class="doctors-grid" v-if="viewMode === 'grid'" v-loading="scheduleLoading">
        <el-card 
          v-for="schedule in scheduleData" 
          :key="schedule.scheduleId" 
          class="doctor-card"
          shadow="hover"
        >
          <div class="doctor-avatar">
            <el-avatar :size="80" :src="schedule.doctorInfo?.avatar || '/default-avatar.png'" />
            <div class="rating">
              <el-rate
                :model-value="schedule.doctorInfo?.rating || 4.5"
                disabled
                text-color="#ff9900"
                score-template="{value}"
              />
              <span class="rating-score">{{ schedule.doctorInfo?.rating || 4.5 }}</span>
            </div>
          </div>
          <div class="doctor-info">
            <h3 class="doctor-name">{{ schedule.doctorName }}</h3>
            <p class="doctor-title">{{ schedule.doctorInfo?.title }}</p>
            <p class="doctor-dept">{{ schedule.department }}</p>
            <p class="schedule-info">
              <el-tag type="warning" size="small">体检</el-tag>
              <span class="schedule-date">{{ schedule.scheduleDate }}</span>
            </p>
            <p class="doctor-fee">￥{{ getDoctorFee(schedule.doctorInfo?.title) }}</p>
          </div>
          <div class="doctor-actions">
            <el-button type="primary" @click="handleAddRegistration(schedule)">
              预约体检
            </el-button>
            <el-button plain @click="showDoctorDetail(schedule)">查看详情</el-button>
          </div>
        </el-card>
      </div>

      <!-- 列表视图 -->
      <div v-else class="doctors-list" v-loading="scheduleLoading">
        <el-table :data="scheduleData" style="width: 100%">
          <el-table-column prop="doctorName" label="医生姓名" />
          <el-table-column prop="department" label="科室" />
          <el-table-column prop="scheduleDate" label="出诊日期" />
          <el-table-column label="出诊类型">
            <template #default>
              <el-tag type="warning" size="small">体检</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="体检费用">
            <template #default="scope">
              <span class="doctor-fee">￥{{ getDoctorFee(scope.row.doctorInfo?.title) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleAddRegistration(scope.row)">
                预约体检
              </el-button>
              <el-button size="small" @click="showDoctorDetail(scope.row)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页器 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="scheduleQuery.page"
          v-model:page-size="scheduleQuery.pageSize"
          :page-sizes="[8, 16, 24, 32]"
          :total="scheduleTotal"
          layout="total, sizes, prev, pager, next"
          @size-change="handleScheduleSizeChange"
          @current-change="handleScheduleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增体检预约对话框 -->
    <el-dialog
      title="新增体检预约"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="医生ID" prop="doctorId">
          <el-input v-model.number="form.doctorId" type="number" disabled />
        </el-form-item>
        <el-form-item label="体检套餐" prop="examType">
          <el-select v-model="form.examType" placeholder="请选择">
            <el-option label="基础体检" value="基础体检" />
            <el-option label="全面体检" value="全面体检" />
            <el-option label="老年体检" value="老年体检" />
            <el-option label="入职体检" value="入职体检" />
          </el-select>
        </el-form-item>
        <el-form-item label="体检时间" prop="regTime">
          <el-date-picker
            v-model="form.regTime"
            type="datetime"
            placeholder="选择日期时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="体检地点" prop="location">
          <el-select v-model="form.location" placeholder="请选择">
            <el-option label="3楼体检中心" value="体检中心" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 医生详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="医生详情"
      width="600px"
    >
      <div v-if="selectedDoctor" class="doctor-detail">
        <div class="detail-header">
          <el-avatar :size="100" :src="selectedDoctor.avatar || '/default-avatar.png'" />
          <div class="detail-info">
            <h2>{{ selectedDoctor.name }}</h2>
            <p>{{ selectedDoctor.title }}</p>
            <p>{{ selectedDoctor.departmentName }}</p>
          </div>
        </div>
        <div class="detail-content">
          <h3>执业信息</h3>
          <p>执业证号：{{ selectedDoctor.licenseNo }}</p>
          <p>专业特长：{{ selectedDoctor.expertise || '暂无' }}</p>
          <h3>简介</h3>
          <p>{{ selectedDoctor.introduction || '暂无简介' }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'
import { getUserId } from '@/utils/auth'
import { Grid, List } from '@element-plus/icons-vue'

const userId = getUserId()

// 视图相关
const viewMode = ref('grid')
const activeFilter = ref('ALL')
const departments = ref([
  { id: 1, name: '内科' },
  { id: 2, name: '外科' },
  { id: 3, name: '骨科' },
  { id: 4, name: '消化科' }
])

// 排班相关
const scheduleQuery = ref({
  page: 1,
  pageSize: 10
})
const scheduleData = ref([])
const scheduleTotal = ref(0)
const scheduleLoading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedDoctor = ref(null)
const formRef = ref(null)
const form = ref({
  doctorId: null,
  examType: '',
  regTime: '',
  location: '体检中心',
  fee: 0
})

// 表单校验规则
const rules = {
  examType: [
    { required: true, message: '请选择体检套餐', trigger: 'change' }
  ],
  regTime: [
    { required: true, message: '请选择体检时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请选择体检机构', trigger: 'change' }
  ]
}

// 科室筛选方法
const filterByDepartment = (dept) => {
  activeFilter.value = dept
  getScheduleList()
}

// 获取排班列表
const getScheduleList = async () => {
  scheduleLoading.value = true
  try {
    const res = await request.get('/doctor-schedules', {
      params: {
        page: scheduleQuery.value.page,
        pageSize: scheduleQuery.value.pageSize,
        scheduleType: '体检',
        department: activeFilter.value === 'ALL' ? undefined : activeFilter.value
      }
    })
    if (res.code === 1) {
      scheduleData.value = res.data.list
      scheduleTotal.value = res.data.total
      await Promise.all(scheduleData.value.map(schedule => getDoctorInfo(schedule)))
    }
  } catch (error) {
    console.error('获取排班列表失败:', error)
    ElMessage.error('获取排班列表失败')
  } finally {
    scheduleLoading.value = false
  }
}

// 获取医生信息
const getDoctorInfo = async (schedule) => {
  if (!schedule.doctorId) return
  
  try {
    const res = await request.get(`/doctors/${schedule.doctorId}`)
    if (res.code === 1) {
      schedule.doctorInfo = res.data
    }
  } catch (error) {
    console.error('获取医生信息失败:', error)
    ElMessage.error('获取医生信息失败')
  }
}

// 查看医生详情
const showDoctorDetail = async (schedule) => {
  if (!schedule.doctorId) return
  
  try {
    const res = await request.get(`/doctors/${schedule.doctorId}`)
    if (res.code === 1) {
      selectedDoctor.value = res.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取医生详情失败:', error)
    ElMessage.error('获取医生详情失败')
  }
}

// 新增体检预约
const handleAddRegistration = (row) => {
  if (!row.doctorInfo) {
    ElMessage.warning('请先加载医生信息')
    return
  }
  
  form.value = {
    doctorId: row.doctorId,
    regTime: row.scheduleDate,
    examType: '',
    location: '体检中心',
    fee: getDoctorFee(row.doctorInfo.title)
  }
  dialogVisible.value = true
}

// 获取医生费用
const getDoctorFee = (title) => {
  if (!title) return '--'
  switch (title) {
    case '主任医师':
      return 30
    case '副主任医师':
      return 25
    default:
      return 20
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await request.post('/physical-exam-bookings', {
          userId,
          doctorId: form.value.doctorId,
          examTime: dayjs(form.value.regTime).format('YYYY-MM-DDTHH:mm:ss'),
          packageType: form.value.examType,
          institution: form.value.location,
          fee: form.value.fee
        })
        
        if (res.code === 1) {
          ElMessage.success('预约成功')
          dialogVisible.value = false
          getScheduleList() // 刷新列表
        }
      } catch (error) {
        console.error('预约失败:', error)
        ElMessage.error('预约失败')
      }
    }
  })
}

// 分页相关方法
const handleScheduleSizeChange = (val) => {
  scheduleQuery.value.pageSize = val
  getScheduleList()
}

const handleScheduleCurrentChange = (val) => {
  scheduleQuery.value.page = val
  getScheduleList()
}

onMounted(() => {
  getScheduleList()
})
</script>

<style scoped>
/* 复用 Appointment.vue 的所有样式 */
.appointment {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.doctors-section {
  max-width: 1200px;
  margin: 0 auto;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.department-tags {
  display: flex;
  gap: 10px;
}

.dept-btn {
  padding: 8px 16px;
  border-radius: 20px;
}

.dept-btn.active {
  background-color: var(--el-color-primary);
  color: white;
}

.doctors-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.doctor-card {
  border-radius: 12px;
  transition: transform 0.3s;
}

.doctor-card:hover {
  transform: translateY(-5px);
}

.doctor-avatar {
  text-align: center;
  margin-bottom: 15px;
}

.rating {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.rating-score {
  color: #ff9900;
  font-weight: bold;
}

.doctor-info {
  text-align: center;
  margin-bottom: 15px;
}

.doctor-name {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 5px;
}

.doctor-title {
  color: var(--el-color-primary);
  margin: 5px 0;
}

.doctor-dept {
  color: #666;
  margin: 5px 0;
}

.doctor-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.schedule-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin: 5px 0;
}

.schedule-date {
  color: #666;
  font-size: 14px;
}

.doctor-fee {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
  margin: 5px 0;
}

.doctor-detail {
  padding: 20px;
}

.detail-header {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.detail-info {
  flex: 1;
}

.detail-info h2 {
  margin: 0 0 10px;
}

.detail-info p {
  margin: 5px 0;
  color: #666;
}

.detail-content {
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.detail-content h3 {
  margin: 15px 0 10px;
  color: #333;
}

.detail-content p {
  margin: 5px 0;
  color: #666;
  line-height: 1.6;
}

.doctors-list {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.doctors-list .el-table {
  margin-bottom: 20px;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .doctors-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}
</style> 