<template>
  <div class="orders">
    <el-card class="orders-card">
      <template #header>
        <div class="card-header">
          <span>费用订单管理</span>
          <el-button type="primary" @click="handleAdd">新增订单</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="病人ID">
          <el-input v-model="queryForm.userId" placeholder="请输入病人ID" clearable />
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="queryForm.payStatus" placeholder="请选择状态" clearable>
            <el-option label="未支付" value="未支付" />
            <el-option label="已支付" value="已支付" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 订单列表 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="userId" label="病人ID" width="100" />
        <el-table-column prop="orderType" label="订单类型" width="100" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.payStatus)">
              {{ scope.row.payStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="支付时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.payTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
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
      :title="dialogType === 'add' ? '新增订单' : '编辑订单'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <template v-if="dialogType === 'add'">
          <el-form-item label="病人ID" prop="userId">
            <el-input v-model="form.userId" placeholder="请输入病人ID" />
          </el-form-item>
          <el-form-item label="订单类型" prop="orderType">
            <el-select v-model="form.orderType" placeholder="请选择类型" style="width: 100%">
              <el-option label="门诊" value="门诊" />
              <el-option label="住院" value="住院" />
              <el-option label="体检" value="体检" />
            </el-select>
          </el-form-item>
          <el-form-item label="关联ID" prop="relatedId">
            <el-input v-model="form.relatedId" placeholder="请输入关联ID" />
          </el-form-item>
          <el-form-item label="金额" prop="amount">
            <el-input-number v-model="form.amount" :precision="2" :step="0.01" :min="0" style="width: 100%" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="支付状态" prop="payStatus">
            <el-select v-model="form.payStatus" placeholder="请选择状态" style="width: 100%">
              <el-option label="未支付" value="未支付" />
              <el-option label="已支付" value="已支付" />
            </el-select>
          </el-form-item>
          <el-form-item label="支付时间" prop="payTime" v-if="form.payStatus === '已支付'">
            <el-date-picker
              v-model="form.payTime"
              type="datetime"
              placeholder="请选择支付时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="支付方式" prop="payMethod" v-if="form.payStatus === '已支付'">
            <el-select v-model="form.payMethod" placeholder="请选择支付方式" style="width: 100%">
              <el-option label="微信" value="微信" />
              <el-option label="支付宝" value="支付宝" />
              <el-option label="银行卡" value="银行卡" />
            </el-select>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </div>
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
  userId: '',
  payStatus: ''
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
const formRef = ref(null)
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  userId: '',
  orderType: '',
  relatedId: '',
  amount: 0,
  payStatus: '未支付',
  payTime: '',
  payMethod: ''
})

// 表单验证规则
const rules = {
  userId: [
    { required: true, message: '请输入病人ID', trigger: 'blur' }
  ],
  orderType: [
    { required: true, message: '请选择订单类型', trigger: 'change' }
  ],
  relatedId: [
    { required: true, message: '请输入关联ID', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' }
  ],
  payStatus: [
    { required: true, message: '请选择支付状态', trigger: 'change' }
  ],
  payMethod: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      userId: queryForm.userId || undefined,
      payStatus: queryForm.payStatus || undefined
    }
    const { data } = await request.get('/payment-orders', { params })
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
  queryForm.payStatus = ''
  currentPage.value = 1
  getList()
}

// 格式化日期时间
const formatDateTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 获取状态标签类型
const getStatusType = (status) => {
  return status === '已支付' ? 'success' : 'warning'
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  Object.assign(form, {
    userId: '',
    orderType: '',
    relatedId: '',
    amount: 0
  })
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = async (row) => {
  dialogType.value = 'edit'
  try {
    const { data } = await request.get(`/payment-orders/${row.orderId}`)
    Object.assign(form, {
      payStatus: data.payStatus,
      payTime: data.payTime,
      payMethod: data.payMethod
    })
    form.orderId = row.orderId
    dialogVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败:', error)
  }
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该订单吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/payment-orders/${row.orderId}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (dialogType.value === 'add') {
      await request.post('/payment-orders', form)
      ElMessage.success('添加成功')
    } else {
      const { payStatus, payTime, payMethod } = form
      await request.put(`/payment-orders/${form.orderId}`, {
        payStatus,
        payTime,
        payMethod
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
.orders {
  height: 100%;
}

.orders-card {
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