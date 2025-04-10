<template>
  <div class="surgeries">
    <el-card class="surgeries-card">
      <template #header>
        <div class="card-header">
          <span>手术信息管理</span>
          <el-button type="primary" @click="handleAdd">新增手术</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="病人ID">
          <el-input v-model="queryForm.userId" placeholder="请输入病人ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="未进行" value="未进行" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 手术列表 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="surgeryId" label="手术ID" width="100" />
        <el-table-column prop="userId" label="病人ID" width="100" />
        <el-table-column prop="surgeryName" label="手术名称" width="150" />
        <el-table-column prop="surgeonId" label="主刀医生ID" width="120" />
        <el-table-column prop="surgeryTime" label="手术时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.surgeryTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
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
      :title="dialogType === 'add' ? '新增手术' : '编辑手术'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="病人ID" prop="userId" v-if="dialogType === 'add'">
          <el-input v-model="form.userId" placeholder="请输入病人ID" />
        </el-form-item>
        <el-form-item label="手术名称" prop="surgeryName" v-if="dialogType === 'add'">
          <el-input v-model="form.surgeryName" placeholder="请输入手术名称" />
        </el-form-item>
        <el-form-item label="主刀医生ID" prop="surgeonId" v-if="dialogType === 'add'">
          <el-input v-model="form.surgeonId" placeholder="请输入主刀医生ID" />
        </el-form-item>
        <el-form-item label="手术时间" prop="surgeryTime">
          <el-date-picker
            v-model="form.surgeryTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="dialogType === 'edit'">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="未进行" value="未进行" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="术后记录" prop="postoperativeNotes">
          <el-input
            v-model="form.postoperativeNotes"
            type="textarea"
            :rows="3"
            placeholder="请输入术后记录"
          />
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
import dayjs from 'dayjs'

// 查询表单
const queryForm = reactive({
  user_id: '',
  status: ''
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
  userId: '',
  surgeryName: '',
  surgeonId: '',
  surgeryTime: '',
  status: '',
  postoperativeNotes: ''
})

// 表单验证规则
const rules = {
  userId: [
    { required: true, message: '请输入病人ID', trigger: 'blur' }
  ],
  surgeryName: [
    { required: true, message: '请输入手术名称', trigger: 'blur' }
  ],
  surgeonId: [
    { required: true, message: '请输入主刀医生ID', trigger: 'blur' }
  ],
  surgeryTime: [
    { required: true, message: '请选择手术时间', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  postoperativeNotes: [
    { required: true, message: '请输入术后记录', trigger: 'blur' }
  ]
}

// 格式化日期时间
const formatDateTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    '未进行': 'info',
    '进行中': 'warning',
    '已完成': 'success'
  }
  return typeMap[status] || 'info'
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      user_id: queryForm.user_id || undefined,
      status: queryForm.status || undefined
    }
    const { data } = await request.get('/surgeries', { params })
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
  queryForm.userId = ''
  queryForm.status = ''
  currentPage.value = 1
  getList()
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  Object.assign(form, {
    userId: '',
    surgeryName: '',
    surgeonId: '',
    surgeryTime: '',
    status: '未进行',
    postoperativeNotes: ''
  })
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = async (row) => {
  dialogType.value = 'edit'
  try {
    const { data } = await request.get(`/surgeries/${row.surgery_id}`)
    Object.assign(form, {
      surgeryTime: data.surgery_time,
      status: data.status,
      postoperativeNotes: data.postoperative_notes
    })
    form.surgery_id = row.surgery_id
    dialogVisible.value = true
  } catch (error) {
    console.error('获取手术详情失败:', error)
  }
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (dialogType.value === 'add') {
      await request.post('/surgeries', form)
      ElMessage.success('添加成功')
    } else {
      const { surgeryTime, status, postoperativeNotes } = form
      await request.put(`/surgeries/${form.surgery_id}`, {
        surgeryTime,
        status,
        postoperativeNotes
      })
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该手术信息吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/surgeries/${row.surgery_id}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
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
.surgeries {
  height: 100%;
}

.surgeries-card {
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