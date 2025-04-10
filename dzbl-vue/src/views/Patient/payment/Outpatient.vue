<template>
  <div class="payment">
    <el-card class="payment-card">
      <template #header>
        <div class="card-header">
          <span>门诊缴费</span>
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
        <el-table-column prop="relatedId" label="关联ID" width="100" />
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
      title="订单支付"
      v-model="dialogVisible"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
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
        <!-- 添加二维码显示区域 -->
        <div v-if="showQrCode" class="qr-code-container">
          <el-image 
            style="width: 200px; height: 200px"
            :src="qrCodeUrl"
            fit="contain"
          />
          <div class="payment-status">
            <div class="timer" v-if="countdown > 0">
              支付倒计时：{{ countdown }}秒
            </div>
            <div v-else>二维码已过期，请重新支付</div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelPay">取消</el-button>
          <el-button type="primary" @click="handlePaySubmit" :disabled="showQrCode">
            确认支付
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
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

// 在 script setup 中添加以下代码
const showQrCode = ref(false)
const countdown = ref(0)
const qrCodeUrl = ref('https://example.com/fake-qr-code.png') // 这里替换为实际的二维码图片
let timer = null

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
      // 过滤数据，只保留门诊类型的订单
      const outpatientOrders = res.data.list.filter(order => order.orderType === '门诊')
      tableData.value = outpatientOrders
      // 更新总数为过滤后的数量
      total.value = outpatientOrders.length
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

// 修改支付提交方法
const handlePaySubmit = async () => {
  showQrCode.value = true
  countdown.value = 15 // 15秒倒计时
  
  // 开始倒计时
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value === 10) { // 当倒计时到10秒时模拟支付成功
      clearInterval(timer)
      simulatePaymentSuccess()
    } else if (countdown.value <= 0) {
      clearInterval(timer)
      showQrCode.value = false
    }
  }, 1000)
}

// 修改模拟支付成功方法
const simulatePaymentSuccess = async () => {
  try {
    const res = await request.put(`/payment-orders/${currentOrder.value.orderId}`, {
      payStatus: '已支付',
      payTime: dayjs().format('YYYY-MM-DD[T]HH:mm:ss'), // 使用带T的ISO格式
      payMethod: payForm.value.payMethod
    })
    
    if (res.code === 1) {
      ElMessage.success('支付成功')
      dialogVisible.value = false
      showQrCode.value = false
      getList()
    } else {
      ElMessage.error(res.msg || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请重试')
  }
}

// 添加取消支付方法
const cancelPay = () => {
  if (timer) {
    clearInterval(timer)
  }
  showQrCode.value = false
  countdown.value = 0
  dialogVisible.value = false
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

// 在组件卸载时清理定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
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

.qr-code-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
}

.payment-status {
  margin-top: 15px;
  text-align: center;
}

.timer {
  color: #409EFF;
  font-size: 14px;
}
</style> 