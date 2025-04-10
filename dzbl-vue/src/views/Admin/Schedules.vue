<template>
  <div class="schedules">
    <el-card class="schedules-card">
      <template #header>
        <div class="card-header">
          <span>出诊排班管理</span>
          <el-button type="primary" @click="handleAdd">新增排班</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="医生ID">
          <el-input v-model="queryForm.doctorId" placeholder="请输入医生ID" clearable />
        </el-form-item>
        <el-form-item label="排班类型">
          <el-select v-model="queryForm.scheduleType" placeholder="请选择类型" clearable>
            <el-option label="门诊" value="门诊" />
            <el-option label="急诊" value="急诊" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 排班列表 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="scheduleId" label="排班ID" width="100" />
        <el-table-column prop="doctorId" label="医生ID" width="100" />
        <el-table-column prop="scheduleType" label="排班类型" width="100" />
        <el-table-column prop="scheduleDate" label="排班日期" width="120" />
        <el-table-column prop="timeRange" label="时间段" width="120" />
        <el-table-column prop="maxPatient" label="最大接诊数" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增排班' : '编辑排班'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="医生ID" prop="doctorId" v-if="dialogType === 'add'">
          <el-input v-model="form.doctorId" placeholder="请输入医生ID" />
        </el-form-item>
        <el-form-item label="排班类型" prop="schedule_type" v-if="dialogType === 'add'">
          <el-select v-model="form.scheduleType" placeholder="请选择类型" style="width: 100%">
            <el-option label="门诊" value="门诊" />
            <el-option label="急诊" value="急诊" />
          </el-select>
        </el-form-item>
        <el-form-item label="排班日期" prop="scheduleDate" v-if="dialogType === 'add'">
          <el-date-picker
            v-model="form.scheduleDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="时间段" prop="timeRange">
          <el-select v-model="form.timeRange" placeholder="请选择时间段">
            <el-option label="上午(8:00-12:00)" value="上午(8:00-12:00)" />
            <el-option label="下午(14:00-18:00)" value="下午(14:00-18:00)" />
            <el-option label="晚上(18:00-21:00)" value="晚上(18:00-21:00)" />
          </el-select>
        </el-form-item>
        <el-form-item label="最大接诊数" prop="max_patient">
          <el-input-number v-model="form.max_patient" :min="1" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 查询表单
const queryForm = reactive({
  doctorId: '',
  scheduleType: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref()
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  doctorId: '',
  scheduleType: '',
  scheduleDate: '',
  timeRange: '',
  maxPatient: 30
})

// 表单验证规则
const rules = {
  doctor_id: [
    { required: true, message: '请输入医生ID', trigger: 'blur' }
  ],
  schedule_type: [
    { required: true, message: '请选择排班类型', trigger: 'change' }
  ],
  schedule_date: [
    { required: true, message: '请选择排班日期', trigger: 'change' }
  ],
  time_range: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ],
  max_patient: [
    { required: true, message: '请输入最大接诊数', trigger: 'blur' }
  ]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      doctorId: queryForm.doctorId || undefined,
      scheduleType: queryForm.scheduleType || undefined
    }
    const { data } = await request.get('/doctor-schedules', { params })
    tableData.value = data.list
    total.value = data.total
  } catch (error) {
    console.error('获取列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置查询
const resetQuery = () => {
  queryForm.doctorId = ''
  queryForm.scheduleType = ''
  currentPage.value = 1
  getList()
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  Object.assign(form, {
    doctorId: '',
    scheduleType: '',
    scheduleDate: '',
    timeRange: '',
    maxPatient: 30
  })
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = async (row) => {
  dialogType.value = 'edit'
  try {
    const { data } = await request.get(`/doctor-schedules/${row.scheduleId}`)
    // 转换后端数据为前端所需格式
    Object.assign(form, {
      scheduleId: data.schedule_id,
      doctorId: data.doctor_id,
      scheduleType: data.schedule_type,
      scheduleDate: data.schedule_date,
      timeRange: data.time_range,
      maxPatient: data.max_patient
    })
    dialogVisible.value = true
  } catch (error) {
    console.error('获取排班详情失败:', error)
    ElMessage.error('获取排班详情失败')
  }
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (dialogType.value === 'add') {
      // 转换前端数据为后端所需格式
      const submitData = {
        doctor_id: form.doctorId,
        schedule_type: form.scheduleType,
        schedule_date: form.scheduleDate,
        time_range: form.timeRange,
        max_patient: form.maxPatient
      }
      await request.post('/doctor-schedules', submitData)
      ElMessage.success('添加成功')
    } else {
      // 编辑时只提交允许修改的字段
      const submitData = {
        time_range: form.timeRange,
        max_patient: form.maxPatient
      }
      await request.put(`/doctor-schedules/${form.scheduleId}`, submitData)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该排班吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/doctor-schedules/${row.scheduleId}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 处理分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  getList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.schedules {
  height: 100%;
}

.schedules-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

/* 表单项间距 */
:deep(.el-form-item) {
  margin-bottom: 22px;
}

/* 对话框按钮间距 */
:deep(.dialog-footer) {
  padding-top: 20px;
  text-align: right;
}

/* 表格内容居中 */
:deep(.el-table .cell) {
  text-align: center;
}
</style> 