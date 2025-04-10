<template>
  <div class="records">
    <el-card class="records-card">
      <template #header>
        <div class="card-header">
          <span>挂号记录</span>
        </div>
      </template>

      <!-- 挂号记录查询表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="待就诊" value="待就诊" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 挂号记录表格 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="regId" label="挂号ID" width="100" />
        <el-table-column prop="departmentId" label="科室ID" width="100" />
        <el-table-column prop="doctorId" label="医生ID" width="100" />
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
        <el-table-column prop="fee" label="费用" width="100" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button 
              type="danger" 
              link
              @click="handleCancel(scope.row)"
              :disabled="scope.row.status !== '待就诊'"
            >
              取消挂号
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'
import { getUserId } from '@/utils/auth'

const userId = getUserId() // 从本地存储获取当前用户ID

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
const form = ref({})
const rules = {}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/registrations', {
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
    console.error('获取挂号记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 取消挂号
const handleCancel = (row) => {
  ElMessageBox.confirm('确认取消该挂号记录吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.put(`/registrations/${row.regId}`, {
        ...row,
        status: '已取消'
      })
      if (res.code === 1) {
        ElMessage.success('取消成功')
        getList()
      }
    } catch (error) {
      console.error('取消挂号失败:', error)
      ElMessage.error('取消挂号失败')
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
.records {
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