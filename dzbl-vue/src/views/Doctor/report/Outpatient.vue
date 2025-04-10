<template>
  <div class="report">
    <el-card class="report-card">
      <template #header>
        <div class="card-header">
          <span>门诊报告管理</span>
          <el-button type="primary" @click="handleAdd">新增报告</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="病人ID">
          <el-input v-model="queryForm.userId" placeholder="请输入病人ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 报告列表 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="reportId" label="报告ID" width="100" />
        <el-table-column prop="userId" label="病人ID" width="100" />
        <el-table-column prop="doctorId" label="医生ID" width="100" />
        <el-table-column prop="diagnosis" label="诊断结果" min-width="200" show-overflow-tooltip />
        <el-table-column prop="medicationAdvice" label="用药建议" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="scope">
            <el-button 
              type="primary" 
              link
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              type="primary" 
              link
              @click="handleViewFiles(scope.row)"
            >
              查看文件
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
      :title="dialogType === 'add' ? '新增报告' : '编辑报告'"
      v-model="dialogVisible"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="病人ID" prop="userId" v-if="dialogType === 'add'">
          <el-input v-model.number="form.userId" type="number" />
        </el-form-item>
        <el-form-item label="诊断结果" prop="diagnosis">
          <el-input 
            v-model="form.diagnosis" 
            type="textarea" 
            :rows="3"
            placeholder="请输入诊断结果"
          />
        </el-form-item>
        <el-form-item label="用药建议" prop="medicationAdvice">
          <el-input 
            v-model="form.medicationAdvice" 
            type="textarea" 
            :rows="3"
            placeholder="请输入用药建议"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加文件查看对话框 -->
    <el-dialog
      title="文件列表"
      v-model="fileDialogVisible"
      width="800px"
    >
      <el-table :data="fileList" v-loading="fileLoading">
        <el-table-column prop="fileId" label="文件ID" width="80" />
        <el-table-column prop="moduleType" label="模块类型" width="100" />
        <el-table-column prop="uploadTime" label="上传时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button 
              type="primary" 
              link
              @click="handleDownload(scope.row.fileUrl)"
            >
              下载
            </el-button>
            <el-button 
              type="info" 
              link
              @click="handlePreview(scope.row.fileId)"
            >
              预览
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加文件预览对话框 -->
    <el-dialog
      title="文件预览"
      v-model="previewDialogVisible"
      :modal="true"
      :close-on-click-modal="false"
      :destroy-on-close="true"
      class="preview-dialog"
      :draggable="true"
    >
      <div v-loading="previewLoading" class="preview-container">
        <div v-if="previewUrl" class="pdf-viewer">
          <iframe 
            :src="previewUrl" 
            class="preview-iframe" 
            frameborder="0"
          ></iframe>
        </div>
        <div v-else class="no-preview">
          <el-empty description="无法预览该文件"></el-empty>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="previewDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleDownloadCurrent">下载</el-button>
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

// 查询表单
const queryForm = ref({
  userId: '',
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
    const res = await request.get('/outpatient-reports', {
      params: {
        ...queryForm.value,
        pageNum: queryForm.value.page,
        pageSize: queryForm.value.pageSize,
        doctorId
      }
    })
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取报告列表失败:', error)
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
  diagnosis: '',
  medicationAdvice: ''
})

const rules = {
  userId: [{ required: true, message: '请输入病人ID', trigger: 'blur' }],
  diagnosis: [{ required: true, message: '请输入诊断结果', trigger: 'blur' }],
  medicationAdvice: [{ required: true, message: '请输入用药建议', trigger: 'blur' }]
}

// 新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    userId: '',
    doctorId,
    diagnosis: '',
    medicationAdvice: ''
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
  ElMessageBox.confirm('确认删除该报告吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/outpatient-reports/${row.reportId}`)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await request.post('/outpatient-reports', form.value)
          ElMessage.success('添加成功')
        } else {
          const { reportId } = form.value
          await request.put(`/outpatient-reports/${reportId}`, {
            diagnosis: form.value.diagnosis,
            medicationAdvice: form.value.medicationAdvice
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

// 格式化时间
const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'
}

// 文件相关
const fileDialogVisible = ref(false)
const fileList = ref([])
const fileLoading = ref(false)

// 查看文件
const handleViewFiles = async (row) => {
  fileDialogVisible.value = true
  fileLoading.value = true
  try {
    const res = await request.get('/medical-files', {
      params: {
        userId: row.userId,
        moduleType: '门诊报告',
        reportId: row.reportId
      }
    })
    fileList.value = res.data.list
  } catch (error) {
    console.error('获取文件列表失败:', error)
    ElMessage.error('获取文件列表失败')
  } finally {
    fileLoading.value = false
  }
}

// 下载文件
const handleDownload = (fileUrl) => {
  if (!fileUrl) {
    ElMessage.warning('文件地址不存在')
    return
  }
  window.open(fileUrl)
}

// 预览相关
const previewDialogVisible = ref(false)
const previewUrl = ref('')
const previewLoading = ref(false)
const currentFileUrl = ref('')

// 预览文件
const handlePreview = async (fileId) => {
  if (!fileId) {
    ElMessage.warning('文件ID不存在')
    return
  }
  
  previewLoading.value = true
  previewDialogVisible.value = true
  previewUrl.value = ''
  
  try {
    // 先调用预览接口获取文件URL
    const res = await request.get(`/medical-files/preview/${fileId}`)
    if (res && res.code === 1) {
      // 将OSS的URL转换为代理URL
      const pdfUrl = res.data.replace('https://web-fangzhuangtong.oss-cn-beijing.aliyuncs.com', '/oss')
      // 使用完整的viewer.html路径，并添加必要的参数
      const viewerPath = `${window.location.origin}/pdfjs/web/viewer.html`
      previewUrl.value = `${viewerPath}?file=${encodeURIComponent(pdfUrl)}`
      currentFileUrl.value = res.data // 保存原始URL用于下载
    } else {
      ElMessage.warning('获取预览文件失败')
    }
  } catch (error) {
    console.error('预览文件失败:', error)
    ElMessage.error('预览文件失败')
  } finally {
    previewLoading.value = false
  }
}

// 下载当前预览的文件
const handleDownloadCurrent = () => {
  if (currentFileUrl.value) {
    window.open(currentFileUrl.value)
  } else {
    ElMessage.warning('文件地址不存在')
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.report {
  padding: 20px;
  height: 100%;
}

.report-card {
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

/* 文件预览对话框样式 */
:deep(.preview-dialog) {
  --dialog-width: 60vw;  /* 增加宽度到60% */
}

:deep(.preview-dialog .el-dialog) {
  width: var(--dialog-width) !important;
  height: 90vh !important;  /* 增加高度到90% */
  margin: 0 !important;
  position: fixed !important;
  left: 50% !important;
  top: 50% !important;
  transform: translate(-50%, -50%) !important;
  display: flex !important;
  flex-direction: column !important;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.preview-dialog .el-dialog__header) {
  padding: 8px 20px;  /* 减小头部高度 */
  margin: 0;
  cursor: move;
  background-color: #f5f5f5;
  border-radius: 8px 8px 0 0;
  border-bottom: 1px solid #dcdfe6;
  flex-shrink: 0;
  height: 40px;  /* 固定头部高度 */
  display: flex;
  align-items: center;
}

:deep(.preview-dialog .el-dialog__body) {
  flex: 1;
  padding: 0;
  overflow: hidden;
  background-color: #fff;
  min-height: 0;
  height: calc(90vh - 90px); /* 90vh减去头部和底部的高度 */
}

:deep(.preview-dialog .el-dialog__footer) {
  padding: 8px 20px;  /* 减小底部高度 */
  margin: 0;
  background-color: #f5f5f5;
  border-radius: 0 0 8px 8px;
  border-top: 1px solid #dcdfe6;
  flex-shrink: 0;
  height: 42px;  /* 固定底部高度 */
  display: flex;
  align-items: center;
}

/* 文件预览容器样式 */
.preview-container {
  width: 100%;
  height: 100%;
  position: relative;
  background-color: #fff;
}

.pdf-viewer {
  width: 100%;
  height: 100%;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
  display: block;
}

.no-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
}
</style> 