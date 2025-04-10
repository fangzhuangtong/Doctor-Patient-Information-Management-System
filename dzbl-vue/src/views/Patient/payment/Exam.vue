<template>
  <div class="payment">
    <el-card class="payment-card">
      <template #header>
        <div class="card-header">
          <span>检查缴费</span>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="支付状态">
          <el-select v-model="queryForm.payStatus" placeholder="请选择" clearable>
            <el-option label="未支付" value="未支付" />
            <el-option label="已支付" value="已支付" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 订单列表 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="relatedId" label="检查ID" width="100" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="scope">
            ¥{{ scope.row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.payStatus === '已支付' ? 'success' : 'warning'">
              {{ scope.row.payStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="100" />
        <el-table-column prop="payTime" label="支付时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.payTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="scope">
            <el-button 
              type="primary" 
              link
              @click="handlePay(scope.row)"
              :disabled="scope.row.payStatus === '已支付'"
            >
              支付
            </el-button>
            <el-button 
              type="danger" 
              link
              @click="handleDelete(scope.row)"
              :disabled="scope.row.payStatus === '已支付'"
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

    <!-- 支付对话框 -->
    <el-dialog
      title="检查费用支付"
      v-model="dialogVisible"
      width="400px"
    >
      <div class="pay-dialog">
        <div class="amount">
          支付金额：<span class="price">¥{{ currentOrder?.amount?.toFixed(2) }}</span>
        </div>
        <el-form :model="payForm" label-width="80px">
          <el-form-item label="支付方式">
            <el-radio-group v-model="payForm.payMethod">
              <el-radio label="支付宝">支付宝</el-radio>
              <el-radio label="微信">微信</el-radio>
              <el-radio label="银行卡">银行卡</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePaySubmit">确认支付</el-button>
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
  payStatus: '',
  page: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 支付相关
const dialogVisible = ref(false)
const currentOrder = ref(null)
const payForm = ref({
  payMethod: '支付宝'
})

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await request.get('/payment-orders', {
      params: {
        userId,
        ...queryForm.value
      }
    })
    if (res.code === 1) {
      // 过滤数据，只保留检查类型的订单
      const examOrders = res.data.list.filter(order => order.orderType === '检查')
      tableData.value = examOrders
      // 更新总数为过滤后的数量
      total.value = examOrders.length
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 删除订单
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该订单?', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/payment-orders/${row.orderId}`)
      if (res.code === 1) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 打开支付对话框
const handlePay = (row) => {
  currentOrder.value = row
  dialogVisible.value = true
}

// 提交支付
const handlePaySubmit = async () => {
  try {
    const res = await request.put(`/payment-orders/${currentOrder.value.orderId}`, {
      payStatus: '已支付',
      payTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
      payMethod: payForm.value.payMethod
    })
    
    if (res.code === 1) {
      ElMessage.success('支付成功')
      dialogVisible.value = false
      getList()
    }
  } catch (error) {
    console.error('支付失败:', error)
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

onMounted(() => {
  getList()
})
</script>

<style scoped>
.payment {
  padding: 20px;
  height: 100%;
}

.payment-card {
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

.pay-dialog {
  padding: 20px;
}

.amount {
  text-align: center;
  margin-bottom: 20px;
  font-size: 16px;
}

.price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
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