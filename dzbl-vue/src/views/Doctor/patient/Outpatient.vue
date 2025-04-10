<template>
  <div class="outpatient">
    <el-card class="patient-card">
      <template #header>
        <div class="card-header">
          <span>已就诊病人管理</span>
          <el-button type="primary" @click="handleAdd">新增病人</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="病人ID">
          <el-input v-model="queryForm.userId" placeholder="请输入病人ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 病人列表 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="regId" label="挂号ID" width="100" />
        <el-table-column prop="userId" label="病人ID" width="100" />
        <el-table-column prop="departmentId" label="科室ID" width="100" />
        <el-table-column prop="regTime" label="就诊时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.regTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fee" label="费用" width="100">
          <template #default="scope">
            ¥{{ scope.row.fee?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="scope">
            <el-button 
              type="primary" 
              link
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              type="danger" 
              link
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增挂号' : '编辑挂号'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="病人ID" prop="userId">
          <el-input v-model.number="form.userId" type="number" />
        </el-form-item>
        <el-form-item label="科室ID" prop="departmentId">
          <el-input v-model.number="form.departmentId" type="number" />
        </el-form-item>
        <el-form-item label="就诊时间" prop="regTime">
          <el-date-picker
            v-model="form.regTime"
            type="datetime"
            placeholder="选择日期时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="挂号类型" prop="registrationType">
          <el-select v-model="form.registrationType" placeholder="请选择">
            <el-option label="普通门诊" value="普通门诊" />
            <el-option label="专家门诊" value="专家门诊" />
            <el-option label="急诊" value="急诊" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'
import { getUserId } from '@/utils/auth'

const doctorId = getUserId()

// 查询表单
const queryForm = ref({
  userId: '',
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = ref({
  userId: '',
  departmentId: '',
  doctorId: doctorId,
  regTime: '',
  registrationType: '普通门诊',
  status: '待就诊'
})

// 表单验证规则
const rules = {
  userId: [{ required: true, message: '请输入病人ID', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请输入科室ID', trigger: 'blur' }],
  regTime: [{ required: true, message: '请选择就诊时间', trigger: 'change' }],
  registrationType: [{ required: true, message: '请选择挂号类型', trigger: 'change' }]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/registrations', {
      params: {
        ...queryForm.value,
        doctorId,
        status: '已完成' // 只获取已完成的记录
      }
    })
    if (res.code === 1 && res.data) {
      tableData.value = res.data.list || []
      total.value = res.data.total || 0
      queryForm.value.page = res.data.pageNum
      queryForm.value.pageSize = res.data.pageSize
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    userId: '',
    departmentId: '',
    doctorId: doctorId,
    regTime: '',
    registrationType: '普通门诊',
    status: '待就诊'
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  dialogType.value = 'edit'
  const res = await request.get(`/registrations/${row.regId}`)
  if (res.code === 1) {
    form.value = { ...res.data }
    dialogVisible.value = true
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该挂号记录吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/registrations/${row.regId}`)
      if (res.code === 1) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = dialogType.value === 'add' ? 
          request.post('/registrations', form.value) :
          request.put(`/registrations/${form.value.regId}`, form.value)
        
        const res = await api
        if (res.code === 1) {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
          dialogVisible.value = false
          getList()
        }
      } catch (error) {
        console.error('操作失败:', error)
      }
    }
  })
}

// 分页相关
const handleSizeChange = (val) => {
  queryForm.value.pageSize = val
  queryForm.value.page = 1 // 切换每页显示数量时重置为第一页
  getList()
}

const handleCurrentChange = (val) => {
  queryForm.value.page = val
  getList()
}

// 格式化时间
const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 获取状态对应的标签类型
const getStatusType = (status) => {
  const map = {
    '待就诊': 'warning',
    '已完成': 'success',
    '已取消': 'info'
  }
  return map[status] || 'info'
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.outpatient {
  padding: 20px;
  height: 100%;
}

.patient-card {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

/* 美化滚动条 */
:deep(.el-card__body)::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

:deep(.el-card__body)::-webkit-scrollbar-thumb {
  background-color: #ddd;
  border-radius: 3px;
}

:deep(.el-card__body)::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}
</style> 