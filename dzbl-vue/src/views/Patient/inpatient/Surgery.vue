<template>
  <div class="surgery">
    <el-card class="surgery-card">
      <template #header>
        <div class="card-header">
          <span>手术信息查询</span>
        </div>
      </template>

      <!-- 手术信息查询表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="未进行" value="未进行" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 手术信息表格 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="surgeryId" label="手术ID" width="100" />
        <el-table-column prop="surgeryName" label="手术名称" width="150" />
        <el-table-column prop="surgeonId" label="主刀医生ID" width="120" />
        <el-table-column prop="surgeryTime" label="手术时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.surgeryTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="postoperativeNotes" label="术后注意事项" min-width="200" show-overflow-tooltip />
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
import { ElMessage } from 'element-plus'
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

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/surgeries', {
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
    console.error('获取手术信息失败:', error)
  } finally {
    loading.value = false
  }
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
    '未进行': 'info',
    '进行中': 'warning',
    '已完成': 'success',
    '已取消': 'danger'
  }
  return map[status] || 'info'
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.surgery {
  padding: 20px;
  height: 100%;
}

.surgery-card {
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