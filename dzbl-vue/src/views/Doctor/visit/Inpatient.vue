<template>
  <div class="visit">
    <el-card class="visit-card">
      <template #header>
        <div class="card-header">
          <span>在院病人管理</span>
          <el-button type="primary" @click="handleAdd">新增住院记录</el-button>
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

      <!-- 住院列表 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="hosId" label="住院ID" width="100" />
        <el-table-column prop="userId" label="病人ID" width="100" />
        <el-table-column prop="bedNumber" label="床位号" width="100" />
        <el-table-column prop="admitTime" label="入院时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.admitTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="dischargeTime" label="出院时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.dischargeTime) || '未出院' }}
          </template>
        </el-table-column>
        <el-table-column prop="summary" label="病情摘要" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '正在住院' ? 'success' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="250">
          <template #default="scope">
            <el-button 
              type="primary" 
              link
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              type="success" 
              link
              @click="handleDischarge(scope.row)"
              :disabled="scope.row.status !== '正在住院'"
            >
              办理出院
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
      :title="dialogType === 'add' ? '新增住院' : '编辑住院'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="病人ID" prop="userId" v-if="dialogType === 'add'">
          <el-input v-model.number="form.userId" type="number" />
        </el-form-item>
        <el-form-item label="床位号" prop="bedNumber">
          <el-input v-model="form.bedNumber" />
        </el-form-item>
        <el-form-item label="入院时间" prop="admitTime">
          <el-date-picker
            v-model="form.admitTime"
            type="datetime"
            placeholder="选择日期时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="病情摘要" prop="summary">
          <el-input 
            v-model="form.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入病情摘要"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 出院对话框 -->
    <el-dialog
      title="办理出院"
      v-model="dischargeDialogVisible"
      width="500px"
    >
      <el-form :model="dischargeForm" ref="dischargeFormRef" label-width="100px">
        <el-form-item label="出院时间" prop="dischargeTime">
          <el-date-picker
            v-model="dischargeForm.dischargeTime"
            type="datetime"
            placeholder="选择日期时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="出院小结" prop="summary">
          <el-input 
            v-model="dischargeForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入出院小结"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dischargeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleDischargeSubmit">确定</el-button>
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

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/hospitalizations', {
      params: {
        ...queryForm.value,
        pageNum: queryForm.value.page,
        pageSize: queryForm.value.pageSize,
        doctorId,
        status: '正在住院'
      }
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取住院记录列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref()
const form = ref({
  userId: '',
  bedNumber: '',
  admitTime: '',
  summary: '',
  doctorId,
  status: '正在住院'
})

const rules = {
  userId: [{ required: true, message: '请输入病人ID', trigger: 'blur' }],
  bedNumber: [{ required: true, message: '请输入床位号', trigger: 'blur' }],
  admitTime: [{ required: true, message: '请选择入院时间', trigger: 'change' }],
  summary: [{ required: true, message: '请输入病情摘要', trigger: 'blur' }]
}

// 出院对话框相关
const dischargeDialogVisible = ref(false)
const dischargeFormRef = ref()
const dischargeForm = ref({
  hosId: '',
  dischargeTime: '',
  summary: ''
})

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    userId: '',
    bedNumber: '',
    admitTime: '',
    summary: '',
    doctorId,
    status: '正在住院'
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该住院记录吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/hospitalizations/${row.hosId}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 办理出院
const handleDischarge = (row) => {
  dischargeForm.value = {
    hosId: row.hosId,
    dischargeTime: '',
    summary: ''
  }
  dischargeDialogVisible.value = true
}

// 提交出院
const handleDischargeSubmit = () => {
  dischargeFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        await request.put(`/hospitalizations/${dischargeForm.value.hosId}`, {
          dischargeTime: dischargeForm.value.dischargeTime,
          summary: dischargeForm.value.summary,
          status: '已出院'
        })
        ElMessage.success('办理出院成功')
        dischargeDialogVisible.value = false
        getList()
      } catch (error) {
        console.error('办理出院失败:', error)
      }
    }
  })
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await request.post('/hospitalizations', {
            ...form.value,
            status: '正在住院'
          })
          ElMessage.success('添加成功')
        } else {
          const { hosId } = form.value
          await request.put(`/hospitalizations/${hosId}`, {
            bedNumber: form.value.bedNumber,
            summary: form.value.summary
          })
          ElMessage.success('修改成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('操作失败:', error)
      }
    }
  })
}

// 分页相关方法
const handleSizeChange = (val) => {
  queryForm.value.pageSize = val
  queryForm.value.page = 1
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

onMounted(() => {
  getList()
})
</script>

<style scoped>
.visit {
  padding: 20px;
  height: 100%;
}

.visit-card {
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