<template>
  <div class="patient-accounts">
    <el-card class="accounts-card">
      <template #header>
        <div class="card-header">
          <span>病人账号管理</span>
          <el-button type="primary" @click="handleAdd">新增账号</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 账号列表 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="userId" label="病人ID" width="120" />
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">修改密码</el-button>
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

    <!-- 新增账号对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="新增病人账号"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="addFormRef"
        :model="addForm"
        :rules="addRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="addForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="修改密码"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="editForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="editForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="submitLoading">
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
  username: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 新增表单
const addDialogVisible = ref(false)
const addFormRef = ref()
const addForm = reactive({
  username: '',
  password: ''
})
const addRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 修改密码表单
const editDialogVisible = ref(false)
const editFormRef = ref()
const editForm = reactive({
  userId: '',
  oldPassword: '',
  newPassword: ''
})
const editRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' }
  ]
}

const submitLoading = ref(false)

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      username: queryForm.username
    }
    const { data } = await request.get('/patient-accounts', { params })
    tableData.value = data.rows
    total.value = data.total
  } catch (error) {
    console.error('获取列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置查询
const resetQuery = () => {
  queryForm.username = ''
  currentPage.value = 1
  getList()
}

// 处理新增
const handleAdd = () => {
  addForm.username = ''
  addForm.password = ''
  addDialogVisible.value = true
}

// 提交新增
const submitAdd = async () => {
  await addFormRef.value.validate()
  submitLoading.value = true
  try {
    await request.post('/patient-accounts', addForm)
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('添加失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 处理修改密码
const handleEdit = (row) => {
  editForm.userId = row.userId
  editForm.oldPassword = ''
  editForm.newPassword = ''
  editDialogVisible.value = true
}

// 提交修改密码
const submitEdit = async () => {
  await editFormRef.value.validate()
  submitLoading.value = true
  try {
    await request.put(`/patient-accounts/${editForm.userId}/password`, {
      oldPassword: editForm.oldPassword,
      newPassword: editForm.newPassword
    })
    ElMessage.success('密码修改成功')
    editDialogVisible.value = false
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该账号吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/patient-accounts/${row.userId}`)
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
.patient-accounts {
  height: 100%;
}

.accounts-card {
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