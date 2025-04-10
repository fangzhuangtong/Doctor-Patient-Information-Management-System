<template>
  <div class="visit">
    <el-card class="visit-card">
      <template #header>
        <div class="card-header">
          <span>体检出诊管理</span>
          <el-button type="primary" @click="handleAdd">新增预约</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="待体检" name="pending">
          <!-- 搜索表单 -->
          <el-form :inline="true" :model="queryForm" class="search-form">
            <el-form-item label="病人ID">
              <el-input v-model="queryForm.userId" placeholder="请输入病人ID" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getList">查询</el-button>
            </el-form-item>
          </el-form>

          <!-- 预约列表 -->
          <el-table :data="tableData" style="width: 100%" v-loading="loading">
            <el-table-column prop="examId" label="体检ID" width="100" />
            <el-table-column prop="userId" label="病人ID" width="100" />
            <el-table-column prop="packageType" label="体检套餐" width="120" />
            <el-table-column prop="examTime" label="体检时间" width="180">
              <template #default="scope">
                {{ formatTime(scope.row.examTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="institution" label="体检机构" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="200">
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
                  @click="handleComplete(scope.row)"
                >
                  完成体检
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
        </el-tab-pane>

        <el-tab-pane label="已体检" name="completed">
          <!-- 搜索表单 -->
          <el-form :inline="true" :model="queryForm" class="search-form">
            <el-form-item label="病人ID">
              <el-input v-model="queryForm.userId" placeholder="请输入病人ID" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getList">查询</el-button>
            </el-form-item>
          </el-form>

          <!-- 预约列表 -->
          <el-table :data="tableData" style="width: 100%" v-loading="loading">
            <el-table-column prop="examId" label="体检ID" width="100" />
            <el-table-column prop="userId" label="病人ID" width="100" />
            <el-table-column prop="packageType" label="体检套餐" width="120" />
            <el-table-column prop="examTime" label="体检时间" width="180">
              <template #default="scope">
                {{ formatTime(scope.row.examTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="institution" label="体检机构" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="120">
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
        </el-tab-pane>
      </el-tabs>

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
      :title="dialogType === 'add' ? '新增体检预约' : '编辑体检预约'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="病人ID" prop="userId" v-if="dialogType === 'add'">
          <el-input v-model.number="form.userId" type="number" />
        </el-form-item>
        <el-form-item label="体检套餐" prop="packageType">
          <el-select v-model="form.packageType" placeholder="请选择">
            <el-option label="基础" value="基础" />
            <el-option label="全面" value="全面" />
            <el-option label="高级" value="高级" />
          </el-select>
        </el-form-item>
        <el-form-item label="体检时间" prop="examTime">
          <el-date-picker
            v-model="form.examTime"
            type="datetime"
            placeholder="选择日期时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="体检机构" prop="institution">
          <el-input v-model="form.institution" />
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

const activeTab = ref('pending')

// 查询表单
const queryForm = ref({
  userId: '',
  doctorId,
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
    const status = activeTab.value === 'pending' ? '待体检' : '已完成'
    const res = await request.get('/physical-exam-bookings', {
      params: {
        ...queryForm.value,
        pageNum: queryForm.value.page,
        pageSize: queryForm.value.pageSize,
        doctorId,
        status
      }
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取体检预约列表失败:', error)
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
  doctorId,
  packageType: '',
  examTime: '',
  institution: ''
})

const rules = {
  userId: [{ required: true, message: '请输入病人ID', trigger: 'blur' }],
  packageType: [{ required: true, message: '请选择体检套餐', trigger: 'change' }],
  examTime: [{ required: true, message: '请选择体检时间', trigger: 'change' }],
  institution: [{ required: true, message: '请输入体检机构', trigger: 'blur' }]
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    userId: '',
    doctorId,
    packageType: '',
    examTime: '',
    institution: ''
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
  ElMessageBox.confirm('确认删除该体检预约吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/physical-exam-bookings/${row.examId}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 完成体检
const handleComplete = (row) => {
  ElMessageBox.confirm('确认完成该体检预约吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.put(`/physical-exam-bookings/${row.examId}`, {
        status: '已完成'
      })
      ElMessage.success('操作成功')
      getList()
    } catch (error) {
      console.error('操作失败:', error)
    }
  })
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await request.post('/physical-exam-bookings', form.value)
          ElMessage.success('添加成功')
        } else {
          const { examId } = form.value
          await request.put(`/physical-exam-bookings/${examId}`, {
            examTime: form.value.examTime,
            packageType: form.value.packageType,
            institution: form.value.institution
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

// 获取状态对应的标签类型
const getStatusType = (status) => {
  const map = {
    '待体检': 'warning',
    '已完成': 'success',
    '已取消': 'info'
  }
  return map[status] || 'info'
}

// 格式化时间
const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 添加标签页切换处理函数
const handleTabClick = () => {
  queryForm.value.page = 1
  getList()
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

/* 添加标签页样式 */
:deep(.el-tabs__content) {
  padding: 20px 0;
}
</style> 