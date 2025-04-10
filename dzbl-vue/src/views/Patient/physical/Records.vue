<template>
  <div class="physical-records">
    <el-card class="records-card">
      <template #header>
        <div class="card-header">
          <span>体检记录</span>
        </div>
      </template>

      <!-- 体检记录查询表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="待体检" value="待体检" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 体检记录表格 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="examId" label="体检ID" width="100" />
        <el-table-column prop="packageType" label="体检套餐" width="100" />
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
              :disabled="scope.row.status !== '待体检'"
            >
              修改
            </el-button>
            <el-button 
              type="danger" 
              link
              @click="handleCancel(scope.row)"
              :disabled="scope.row.status !== '待体检'"
            >
              取消
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

    <!-- 编辑对话框 -->
    <el-dialog
      title="修改体检预约"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="体检时间" prop="examTime">
          <el-date-picker
            v-model="form.examTime"
            type="datetime"
            placeholder="选择日期时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'
import { getUserId } from '@/utils/auth'

const userId = getUserId()

// 查询表单
const queryForm = ref({
  status: '',
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({
  examId: null,
  examTime: ''
})

// 表单校验规则
const rules = {
  examTime: [
    { required: true, message: '请选择体检时间', trigger: 'change' }
  ]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/physical-exam-bookings', {
      params: {
        userId,
        ...queryForm.value
      }
    })
    if (res.code === 1) {
      tableData.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取体检记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 修改体检预约
const handleEdit = async (row) => {
  try {
    const res = await request.get(`/physical-exam-bookings/${row.examId}`)
    if (res.code === 1) {
      form.value = {
        examId: res.data.examId,
        examTime: dayjs(res.data.examTime).format('YYYY-MM-DD HH:mm:ss')
      }
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取体检详情失败:', error)
  }
}

// 取消体检预约
const handleCancel = (row) => {
  ElMessageBox.confirm('确认取消该体检预约吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.put(`/physical-exam-bookings/${row.examId}`, {
        ...row,
        status: '已取消'
      })
      if (res.code === 1) {
        ElMessage.success('取消成功')
        getList()
      }
    } catch (error) {
      console.error('取消体检失败:', error)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await request.put(`/physical-exam-bookings/${form.value.examId}`, {
          examTime: form.value.examTime
        })
        
        if (res.code === 1) {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getList()
        }
      } catch (error) {
        console.error('修改失败:', error)
      }
    }
  })
}

// 分页相关方法
const handleSizeChange = (val) => {
  queryForm.value.pageSize = val
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
    '待体检': 'warning',
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
.physical-records {
  padding: 20px;
  height: 100%;
}

.records-card {
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