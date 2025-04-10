<template>
  <div class="account-binding">
    <!-- 头部搜索和操作区 -->
    <div class="header-actions">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="查询类型">
          <el-select v-model="queryForm.type" placeholder="请选择查询类型" style="width: 160px;">
            <el-option label="关系状态" value="isValid" />
            <el-option label="查询子账号" value="patientId" />
            <el-option label="查询父账号" value="relativeId" />
          </el-select>
        </el-form-item>
        
        <!-- 根据查询类型显示不同的输入框 -->
        <el-form-item label="查询条件">
          <template v-if="queryForm.type === 'isValid'">
            <el-select v-model="queryForm.isValid" placeholder="请选择" clearable style="width: 120px;">
              <el-option label="有效" :value="1" />
              <el-option label="无效" :value="0" />
            </el-select>
          </template>
          <template v-else>
            <el-input 
              v-model="queryForm.queryId" 
              :placeholder="queryForm.type === 'patientId' ? '请输入病人ID' : '请输入亲属ID'"
              style="width: 160px;"
            />
          </template>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="primary" @click="handleAdd">新增关联</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <el-table 
      :data="tableData" 
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="relationId" label="关联ID" width="100" />
      <el-table-column prop="relativeId" label="亲属ID" width="100" />
      <el-table-column prop="relationType" label="关系类型" width="100" />
      <el-table-column prop="bindTime" label="绑定时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.bindTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="isValid" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isValid ? 'success' : 'danger'">
            {{ scope.row.isValid ? '有效' : '无效' }}
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
            type="danger" 
            link
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增关联' : '编辑关联'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form 
        :model="form" 
        :rules="rules"
        ref="formRef"
        label-width="100px"
      >
        <el-form-item label="亲属ID" prop="relativeId" v-if="dialogType === 'add'">
          <el-input v-model.number="form.relativeId" type="number" />
        </el-form-item>
        <el-form-item label="关系类型" prop="relationType">
          <el-select v-model="form.relationType" placeholder="请选择">
            <el-option label="子女" value="子女" />
            <el-option label="父母" value="父母" />
            <el-option label="配偶" value="配偶" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="isValid" v-if="dialogType === 'edit'">
          <el-switch
            v-model="form.isValid"
            :active-value="1"
            :inactive-value="0"
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
import { getUserId } from '@/utils/auth'
import dayjs from 'dayjs'

// 查询表单
const queryForm = ref({
  type: 'isValid',  // 默认按状态查询
  isValid: '',
  queryId: ''  // 用于存储 patientId 或 relativeId
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = ref({
  relativeId: null,
  relationType: '',
  isValid: 1
})

// 表单校验规则
const rules = {
  relativeId: [
    { required: true, message: '请输入亲属ID', trigger: 'blur' },
    { type: 'number', message: '亲属ID必须为数字', trigger: 'blur' }
  ],
  relationType: [
    { required: true, message: '请选择关系类型', trigger: 'change' }
  ]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    let res
    switch (queryForm.value.type) {
      case 'patientId':
        // 查询子账号
        res = await request.get(`/family-relations/patient/${queryForm.value.queryId}`)
        break
      case 'relativeId':
        // 查询父账号
        res = await request.get(`/family-relations/relative/${queryForm.value.queryId}`)
        break
      default:
        // 默认查询（按状态查询所有关联关系）
        res = await request.get(`/family-relations/patient/${getUserId()}`, {
          params: {
            isValid: queryForm.value.isValid
          }
        })
    }

    if (res.code === 1) {
      // 确保数据是数组形式
      tableData.value = Array.isArray(res.data) ? res.data : [res.data].filter(Boolean)
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  // 验证输入
  if (queryForm.value.type !== 'isValid' && !queryForm.value.queryId) {
    ElMessage.warning('请输入查询ID')
    return
  }
  getList()
}

// 新增关联
const handleAdd = async () => {
  if (!form.value.relativeId) {
    ElMessage.warning('请输入亲属ID')
    return
  }

  try {
    await request.post('/account-relations', {
      // 修改为使用当前用户ID
      patientId: getUserId(),
      relativeId: form.value.relativeId,
      isValid: 1
    })
    ElMessage.success('添加成功')
    dialogVisible.value = false
    getList()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

// 编辑
const handleEdit = async (row) => {
  try {
    const res = await request.get(`/family-relations/${row.relationId}`)
    if (res.code === 1) {
      dialogType.value = 'edit'
      form.value = {
        relationId: res.data.relationId,
        relationType: res.data.relationType,
        isValid: res.data.isValid
      }
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该关联关系吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/family-relations/${row.relationId}`)
      if (res.code === 1) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (dialogType.value === 'add') {
          res = await request.post('/family-relations', {
            patientId: getUserId(),
            ...form.value
          })
        } else {
          res = await request.put(`/family-relations/${form.value.relationId}`, {
            relationType: form.value.relationType,
            isValid: form.value.isValid
          })
        }
        
        if (res.code === 1) {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
          dialogVisible.value = false
          getList()
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
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
.account-binding {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header-actions {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-table) {
  flex: 1;
}

/* 添加新样式 */
.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 20px;
}

.search-form :deep(.el-select),
.search-form :deep(.el-input) {
  width: 160px;
}
</style> 