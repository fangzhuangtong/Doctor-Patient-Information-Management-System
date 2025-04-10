<template>
  <div class="report-maker">
    <h2>住院病历制作</h2>
    
    <div class="report-content">
      <div class="form-header">
        <h2>住院病历制作</h2>
        <el-button 
          type="danger" 
          link
          class="clear-all-btn"
          @click="confirmClearAll"
          v-if="hasContent"
        >
          <el-icon><Delete /></el-icon>
          清空所有
        </el-button>
      </div>
      <el-form :model="reportForm" label-width="120px">
        <draggable 
          v-model="formItems" 
          item-key="id"
          handle=".drag-handle"
          ghost-class="ghost"
          @start="isDragging = true"
          @end="isDragging = false"
        >
          <template #item="{ element }">
            <el-form-item class="form-item-draggable" :class="{ 'basic-info': element.isBasicInfo }">
              <div class="drag-handle" v-if="!element.isFixed && !element.isBasicInfo">
                <el-icon><Rank /></el-icon>
              </div>
              <template v-if="element.isDivider">
                <div :class="['form-divider', element.dividerClass]">
                  <div class="divider-line"></div>
                  <span>分割线</span>
                  <div class="divider-line"></div>
                </div>
              </template>
              <div v-else class="textarea-container">
                <div class="label-row">
                  <span class="custom-label">{{ element.label }}</span>
                  <div v-if="element.isBasicInfo" class="basic-info-input">
                    <template v-if="element.key === 'patientId'">
                      <div class="patient-id-input">
                        <el-input 
                          v-model="reportForm[element.key]" 
                          :placeholder="element.placeholder"
                        />
                        <el-button type="primary" @click="searchPatient" :loading="loading">
                          查询
                        </el-button>
                      </div>
                    </template>
                    <template v-else-if="element.type === 'select'">
                      <el-select v-model="reportForm[element.key]" :placeholder="element.placeholder">
                        <el-option 
                          v-for="option in element.options" 
                          :key="option.value" 
                          :label="option.label" 
                          :value="option.value" 
                        />
                      </el-select>
                    </template>
                    <template v-else-if="element.type === 'date'">
                      <el-date-picker
                        v-model="reportForm[element.key]"
                        type="date"
                        :placeholder="element.placeholder"
                        format="YYYY/MM/DD"
                        value-format="YYYY/MM/DD"
                      />
                    </template>
                    <template v-else>
                      <el-input 
                        v-model="reportForm[element.key]" 
                        :placeholder="element.placeholder" 
                      />
                    </template>
                  </div>
                  <div class="button-group" v-if="!element.isBasicInfo">
                    <el-button 
                      v-if="element.showConsultButton"
                      type="primary" 
                      class="consult-btn"
                      @click="getFromConsultation(element.showConsultButton)"
                      :loading="element.showConsultButton === 'chiefComplaint' ? chiefComplaintLoading : presentIllnessLoading"
                    >
                      从咨询记录获取{{ element.showConsultButton === 'chiefComplaint' ? '主诉' : '现病史' }}
                    </el-button>
                    <el-button 
                      v-if="element.showAIButton"
                      type="primary" 
                      class="ai-assist-btn"
                      @click="handleAIAssist(element.key)"
                      :loading="getLoadingState(element.key)"
                    >
                      AI辅助
                    </el-button>
                  </div>
                </div>
                <div v-if="!element.isBasicInfo" class="input-row">
                  <div class="input-wrapper">
                    <el-button 
                      class="clear-btn" 
                      type="text" 
                      @click="clearField(element.key)"
                      v-if="reportForm[element.key]"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                    <template v-if="element.type === 'select'">
                      <el-select v-model="reportForm[element.key]" :placeholder="element.placeholder">
                        <el-option 
                          v-for="option in element.options" 
                          :key="option.value" 
                          :label="option.label" 
                          :value="option.value" 
                        />
                      </el-select>
                    </template>
                    <template v-else-if="element.type === 'date'">
                      <el-date-picker
                        v-model="reportForm[element.key]"
                        type="date"
                        :placeholder="element.placeholder"
                        format="YYYY/MM/DD"
                        value-format="YYYY/MM/DD"
                      />
                    </template>
                    <template v-else-if="element.type === 'textarea'">
                      <el-input
                        v-model="reportForm[element.key]"
                        type="textarea"
                        :rows="element.rows"
                        :placeholder="element.placeholder"
                      />
                    </template>
                    <template v-else>
                      <el-input 
                        v-model="reportForm[element.key]" 
                        :placeholder="element.placeholder" 
                      />
                    </template>
                  </div>
                </div>
                <div v-if="element.key === 'chiefComplaint' && aiSuggestions.chiefComplaint" class="ai-suggestion-area">
                  <div class="ai-suggestion-header">咨询记录中提取的主诉：</div>
                  <div class="ai-suggestion-content">
                    <el-input
                      v-model="aiSuggestions.chiefComplaint"
                      type="textarea"
                      :rows="3"
                      readonly
                    />
                    <el-button type="success" @click="useAISuggestion('chiefComplaint')">使用</el-button>
                  </div>
                </div>
                <div v-if="element.key === 'presentIllness' && aiSuggestions.presentIllness" class="ai-suggestion-area">
                  <div class="ai-suggestion-header">咨询记录中提取的现病史：</div>
                  <div class="ai-suggestion-content">
                    <el-input
                      v-model="aiSuggestions.presentIllness"
                      type="textarea"
                      :rows="3"
                      readonly
                    />
                    <el-button type="success" @click="useAISuggestion('presentIllness')">使用</el-button>
                  </div>
                </div>
                <div v-if="element.key === 'diagnosis' && aiSuggestions.diagnosis" class="ai-suggestion-area">
                  <div class="ai-suggestion-header">AI 建议的初步诊断：</div>
                  <div class="ai-suggestion-content">
                    <el-input
                      v-model="aiSuggestions.diagnosis"
                      type="textarea"
                      :rows="3"
                      readonly
                    />
                    <el-button type="success" @click="useAISuggestion('diagnosis')">使用</el-button>
                  </div>
                </div>
                <div v-if="element.key === 'treatment' && aiSuggestions.treatment" class="ai-suggestion-area">
                  <div class="ai-suggestion-header">AI 建议的处理意见：</div>
                  <div class="ai-suggestion-content">
                    <el-input
                      v-model="aiSuggestions.treatment"
                      type="textarea"
                      :rows="3"
                      readonly
                    />
                    <el-button type="success" @click="useAISuggestion('treatment')">使用</el-button>
                  </div>
                </div>
                <div v-if="element.key === 'pastHistory' && aiSuggestions.pastHistory" class="ai-suggestion-area">
                  <div class="ai-suggestion-header">AI 分析的病史要点：</div>
                  <div class="ai-suggestion-content">
                    <el-input
                      v-model="aiSuggestions.pastHistory"
                      type="textarea"
                      :rows="3"
                      readonly
                    />
                    <el-button type="success" @click="useAISuggestion('pastHistory')">使用</el-button>
                  </div>
                </div>
                <div v-if="element.key === 'physicalExam' && aiSuggestions.physicalExam" class="ai-suggestion-area">
                  <div class="ai-suggestion-header">AI 分析的体格检查要点：</div>
                  <div class="ai-suggestion-content">
                    <el-input
                      v-model="aiSuggestions.physicalExam"
                      type="textarea"
                      :rows="3"
                      readonly
                    />
                    <el-button type="success" @click="useAISuggestion('physicalExam')">使用</el-button>
                  </div>
                </div>
                <div v-if="element.key === 'auxiliaryExam' && aiSuggestions.auxiliaryExam" class="ai-suggestion-area">
                  <div class="ai-suggestion-header">AI 分析的辅助检查要点：</div>
                  <div class="ai-suggestion-content">
                    <el-input
                      v-model="aiSuggestions.auxiliaryExam"
                      type="textarea"
                      :rows="3"
                      readonly
                    />
                    <el-button type="success" @click="useAISuggestion('auxiliaryExam')">使用</el-button>
                  </div>
                </div>
              </div>
            </el-form-item>
          </template>
        </draggable>

        <el-form-item>
          <el-button type="primary" @click="submitReport">提交报告</el-button>
          <el-button type="success" @click="handleGeneratePDF">生成PDF</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="preview-area">
      <el-tabs v-model="activeTab" class="preview-tabs">
        <el-tab-pane label="PDF预览" name="preview">
          <div class="preview-paper" ref="pdfContent">
            <div class="report-header">
              <div class="report-top">
                <span class="patient-id">
                  患者ID: {{ reportForm.patientId }}
                </span>
                <h1>{{ reportForm.title || '医疗报告' }}</h1>
              </div>
              <div class="title-divider"></div>
              <div class="report-info">
                <div class="info-row">
                  <p>姓名: {{ reportForm.name }}</p>
                  <p>性别: {{ reportForm.gender }}</p>
                  <p>出生日期: {{ reportForm.birthDate }}</p>
                  <p>手机号: {{ reportForm.phone }}</p>
                </div>
                <div class="info-row">
                  <p>身份证号: {{ reportForm.idCard }}</p>
                  <p>地址: {{ reportForm.address }}</p>
                </div>
              </div>
              <div class="info-divider"></div>
            </div>
            <div class="report-body">
              <template v-for="item in formItems" :key="item.id">
                <template v-if="!item.showInHeader && !item.isDivider && item.key !== 'title' && item.showInPdf !== false">
                  <div class="medical-section">
                    <div class="medical-label">{{ item.label }}：</div>
                    <div class="medical-content">{{ reportForm[item.key] }}</div>
                  </div>
                </template>
                <div v-if="item.isDivider && !item.isFixed" :class="item.dividerClass"></div>
              </template>
              <div class="report-footer">
                <p>生成日期: {{ currentDate }}</p>
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="文件文字提取" name="extraction">
          <div class="extraction-content">
            <div class="file-upload-area">
              <el-upload
                class="upload-demo"
                :action="'/api/ocr/extract'"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeUpload"
                :multiple="true"
                accept=".pdf,.jpg,.jpeg,.png"
              >
                <el-button type="primary">上传文件</el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    支持 PDF、JPG 格式文件，用于自动提取文本内容
                  </div>
                </template>
              </el-upload>
            </div>

            <div class="extraction-header">
              <h3>文件文字提取结果</h3>
              <el-button type="primary" @click="handleExtractedContent" :disabled="!hasExtractedContent">
                应用提取内容
              </el-button>
            </div>
            <div class="extraction-body">
              <template v-if="hasExtractedContent">
                <div v-for="(content, field) in extractedContents" :key="field" class="extraction-item">
                  <div class="extraction-field-row">
                    <div class="extraction-field">{{ getFieldLabel(field) }}：</div>
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click="handleApplySingleField(field)"
                      :disabled="!extractedContents[field]"
                    >
                      应用
                    </el-button>
                  </div>
                  <div class="extraction-value">
                    <el-input
                      v-model="extractedContents[field]"
                      type="textarea"
                      :rows="getFieldRows(field)"
                      :placeholder="'提取的' + getFieldLabel(field)"
                    />
                  </div>
                </div>
              </template>
              <div v-if="hasExtractedContent">
                <div class="extraction-summary">
                  <div class="summary-header">
                    <h4>提取结果总览</h4>
                    <div class="summary-actions">
                      <el-tooltip content="显示所有从文件中提取的原始文本内容" placement="top">
                        <el-icon><InfoFilled /></el-icon>
                      </el-tooltip>
                      <el-button 
                        type="primary" 
                        link 
                        @click="showFullText = true"
                      >
                        查看完整文本
                      </el-button>
                    </div>
                  </div>
                  <el-input
                    v-model="rawExtractedText"
                    type="textarea"
                    :rows="6"
                    readonly
                    placeholder="这里显示文件中提取的原始文本内容"
                  />
                </div>
              </div>
              <el-empty v-else description="暂无提取内容，请先上传文件"></el-empty>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 修改对话框组件 -->
    <el-dialog
      v-model="showFullText"
      title="完整提取文本"
      :fullscreen="false"
      :show-close="false"
      :close-on-click-modal="false"
      class="full-text-dialog"
      :modal="false"
      :append-to-body="true"
      custom-class="right-side-dialog"
      draggable
      :destroy-on-close="false"
    >
      <!-- 添加自定义标题栏，用于拖动 -->
      <template #header>
        <div class="custom-dialog-header">
          <span>完整提取文本</span>
          <el-button
            class="close-btn"
            type="text"
            @click="showFullText = false"
          >
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </template>
      <div class="full-text-content">
        <el-input
          v-model="rawExtractedText"
          type="textarea"
          :rows="20"
          readonly
          resize="none"
        />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showFullText = false">关闭</el-button>
          <el-button type="primary" @click="copyFullText">
            复制文本
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import html2pdf from 'html2pdf.js'
import draggable from 'vuedraggable'
import { Rank, InfoFilled, Close, Delete } from '@element-plus/icons-vue'
import axios from 'axios'
import { getUserId } from '@/utils/auth'  // 引入获取用户ID的方法

const pdfContent = ref(null)
const isDragging = ref(false)
const loading = ref(false)
const aiLoading = ref(false)
const diagnosisLoading = ref(false)
const treatmentLoading = ref(false)
const chiefComplaintLoading = ref(false)
const presentIllnessLoading = ref(false)
const pastHistoryLoading = ref(false)
const physicalExamLoading = ref(false)
const auxiliaryExamLoading = ref(false)

const reportForm = ref({
  patientId: '',
  name: '',
  gender: '',
  birthDate: '',
  phone: '',
  idCard: '',
  address: '',
  title: '住院病历', // 设置默认值
  reportId: '',
  reportType: '住院病历', // 设置默认值
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  physicalExam: '',
  auxiliaryExam: '',
  diagnosis: '',
  treatment: ''
})

// 表单项配置
const formItems = ref([
  {
    id: 8,
    key: 'title',
    label: '报告标题',
    type: 'input',
    placeholder: '请输入报告标题',
    showInHeader: false,
    isBasicInfo: true
  },
  {
    id: 'reportId',
    key: 'reportId',
    label: '报告ID',
    type: 'input',
    placeholder: '请输入报告ID',
    showInHeader: false,
    isBasicInfo: true,
    showInPdf: false
  },
  {
    id: 'reportType',
    key: 'reportType',
    label: '报告类型',
    type: 'input',
    placeholder: '请输入报告类型',
    showInHeader: false,
    isBasicInfo: true,
    showInPdf: false
  },
  {
    id: 1,
    key: 'patientId',
    label: '患者ID',
    type: 'input',
    placeholder: '请输入患者ID',
    showInHeader: true,
    isBasicInfo: true
  },
  {
    id: 2,
    key: 'name',
    label: '姓名',
    type: 'input',
    placeholder: '请输入姓名',
    showInHeader: true,
    isBasicInfo: true
  },
  {
    id: 3,
    key: 'gender',
    label: '性别',
    type: 'select',
    placeholder: '请选择性别',
    showInHeader: true,
    isBasicInfo: true,
    options: [
      { label: '男', value: '男' },
      { label: '女', value: '女' }
    ]
  },
  {
    id: 4,
    key: 'birthDate',
    label: '出生日期',
    type: 'date',
    placeholder: '请选择出生日期',
    showInHeader: true,
    isBasicInfo: true
  },
  {
    id: 5,
    key: 'phone',
    label: '手机号',
    type: 'input',
    placeholder: '请输入手机号',
    showInHeader: true,
    isBasicInfo: true
  },
  {
    id: 6,
    key: 'idCard',
    label: '身份证号',
    type: 'input',
    placeholder: '请输入身份证号',
    showInHeader: true,
    isBasicInfo: true
  },
  {
    id: 7,
    key: 'address',
    label: '地址',
    type: 'input',
    placeholder: '请输入地址',
    showInHeader: true,
    isBasicInfo: true
  },
  {
    id: 'fixed-divider',  // 使用特殊的 id
    type: 'divider',
    isDivider: true,
    dividerClass: 'fixed-divider',
    isFixed: true  // 添加标识表示不可拖动
  },
  {
    id: 9,
    key: 'chiefComplaint',
    label: '主诉',
    type: 'textarea',
    rows: 3,
    placeholder: '请输入主诉',
    showInHeader: false,
    showConsultButton: 'chiefComplaint'
  },
  {
    id: 10,
    key: 'presentIllness',
    label: '现病史',
    type: 'textarea',
    rows: 4,
    placeholder: '请输入现病史',
    showInHeader: false,
    showConsultButton: 'presentIllness'
  },
  {
    id: 'divider1',
    type: 'divider',
    isDivider: true,
    dividerClass: 'present-illness-divider'
  },
  {
    id: 11,
    key: 'pastHistory',
    label: '病史',
    type: 'textarea',
    rows: 3,
    placeholder: '请输入病史',
    showInHeader: false,
    showAIButton: true
  },
  {
    id: 12,
    key: 'physicalExam',
    label: '体格检查',
    type: 'textarea',
    rows: 4,
    placeholder: '请输入体格检查结果',
    showInHeader: false,
    showAIButton: true
  },
  {
    id: 13,
    key: 'auxiliaryExam',
    label: '辅助检查',
    type: 'textarea',
    rows: 3,
    placeholder: '请输入辅助检查结果',
    showInHeader: false,
    showAIButton: true
  },
  {
    id: 'divider2',
    type: 'divider',
    isDivider: true,
    dividerClass: 'diagnosis-divider'
  },
  {
    id: 14,
    key: 'diagnosis',
    label: '初步诊断',
    type: 'textarea',
    rows: 3,
    placeholder: '请输入初步诊断',
    showInHeader: false,
    showAIButton: true
  },
  {
    id: 15,
    key: 'treatment',
    label: '处理意见',
    type: 'textarea',
    rows: 4,
    placeholder: '请输入处理意见',
    showInHeader: false,
    showAIButton: true
  }
])

const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN')
})

const getReportTypeName = (type) => {
  const typeMap = {
    outpatient: '门诊报告',
    check: '检查报告',
    inpatient: '住院报告',
    physical: '体检报告'
  }
  return typeMap[type] || '未知类型'
}

const generatePDF = async () => {
  if (!reportForm.value.title) {
    ElMessage.warning('请填写报告标题')
    return null
  }

  const element = pdfContent.value
  const opt = {
    margin: [10, 10, 10, 10],
    filename: `${reportForm.value.title}-${new Date().getTime()}.pdf`,
    image: { type: 'jpeg', quality: 0.98 },
    html2canvas: { 
      scale: 2,
      useCORS: true,
      logging: false,
      windowHeight: element.scrollHeight
    },
    jsPDF: { 
      unit: 'mm', 
      format: 'a4', 
      orientation: 'portrait',
      putTotalPages: false,
      compress: true
    },
    pagebreak: { mode: 'avoid-all' }
  }

  try {
    // 使用 output 方法返回 blob 而不是直接保存
    const pdf = await html2pdf().from(element).set(opt).output('blob')
    return pdf
  } catch (error) {
    console.error('PDF生成错误:', error)
    ElMessage.error('PDF生成失败')
    return null
  }
}

const submitReport = async () => {
  if (!reportForm.value.reportId) {
    ElMessage.warning('请输入报告ID')
    return
  }
  
  if (!reportForm.value.patientId) {
    ElMessage.warning('请输入患者ID')
    return
  }

  if (!reportForm.value.reportType) {
    ElMessage.warning('请输入报告类型')
    return
  }

  try {
    ElMessage.info('正在生成PDF...')
    const pdfBlob = await generatePDF()
    if (!pdfBlob) {
      return
    }

    // 创建 FormData 对象
    const formData = new FormData()
    
    // 将 PDF blob 转换为文件并添加到 FormData
    const fileName = `${reportForm.value.title || '医疗报告'}-${new Date().getTime()}.pdf`
    const pdfFile = new File([pdfBlob], fileName, { type: 'application/pdf' })
    
    // 直接使用用户输入的报告类型作为 moduleType
    formData.append('file', pdfFile)
    formData.append('userId', reportForm.value.patientId)
    formData.append('reportId', reportForm.value.reportId)
    formData.append('moduleType', reportForm.value.reportType)

    ElMessage.info('正在上传文件...')
    
    // 发送请求到后端
    const response = await axios.post('/api/medical-files', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response.data.code === 1) {
      ElMessage.success('报告提交成功')
    } else {
      throw new Error(response.data.msg || '提交失败')
    }
  } catch (error) {
    console.error('提交报告失败:', error)
    ElMessage.error(error.message || '提交报告失败，请稍后重试')
  }
}

const handleGeneratePDF = async () => {
  const pdf = await generatePDF()
  if (pdf) {
    // 创建一个临时的URL并触发下载
    const url = URL.createObjectURL(pdf)
    const link = document.createElement('a')
    link.href = url
    link.download = `${reportForm.value.title || '医疗报告'}-${new Date().getTime()}.pdf`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    ElMessage.success('PDF生成成功')
  }
}

const resetForm = () => {
  reportForm.value = {
    patientId: '',
    name: '',
    gender: '',
    birthDate: '',
    phone: '',
    idCard: '',
    address: '',
    title: '住院病历', // 保持默认值
    reportId: '',
    reportType: '住院病历', // 保持默认值
    chiefComplaint: '',
    presentIllness: '',
    pastHistory: '',
    physicalExam: '',
    auxiliaryExam: '',
    diagnosis: '',
    treatment: ''
  }
}

// 在模板中添加日期选择器的处理
const handleDateInput = (element, value) => {
  if (element.type === 'date') {
    reportForm.value[element.key] = value
  }
}

// 添加一个清理文本的工具函数
const cleanTextContent = (text) => {
  if (!text) return ''
  // 移除 *-# 符号，但保留换行符
  return text.replace(/[*#\-]/g, '').trim()
}

// 查询患者信息和咨询记录
const searchPatient = async () => {
  if (!reportForm.value.patientId) {
    ElMessage.warning('请输入患者ID')
    return
  }

  loading.value = true
  try {
    // 只获取用户信息和健康记录
    const [userResponse, healthResponse] = await Promise.all([
      axios.get(`/api/users/${reportForm.value.patientId}`),
      axios.get(`/api/health-records/user/${reportForm.value.patientId}`)
    ])

    // 处理用户基本信息
    if (userResponse.data.code === 1) {
      const userData = userResponse.data.data
      // 更新表单数据
      reportForm.value = {
        ...reportForm.value,
        name: userData.name,
        gender: userData.gender,
        birthDate: userData.birthdate,
        phone: userData.phone,
        idCard: userData.idCard,
        address: userData.address
      }
    }

    // 处理健康信息
    if (healthResponse.data.code === 1) {
      const healthRecords = healthResponse.data.data
      if (healthRecords && healthRecords.length > 0) {
        // 获取最新的健康记录（数组中的第一条记录）
        const latestRecord = healthRecords[0]

        // 组装体格检查信息
        const physicalInfo = [
          `血型：${latestRecord.bloodType}`,
          `身高：${latestRecord.height}cm`,
          `体重：${latestRecord.weight}kg`,
          `血压：${latestRecord.bloodPressure}mmHg`,
          `血脂：${latestRecord.bloodLipid}mmol/L`,
          `血糖：${latestRecord.bloodGlucose}mmol/L`,
          `BMI：${latestRecord.bmi}`
        ].join('\n')

        // 组装病史信息
        const historyInfo = [
          `过敏史：${latestRecord.allergyHistory}`,
          `既往病史：${latestRecord.pastMedicalHistory}`,
          `家族病史：${latestRecord.familyHistory}`
        ].join('\n')

        // 组装辅助检查信息（生活习惯）
        const lifestyleInfo = [
          `吸烟情况：${latestRecord.smokingHistory}`,
          `饮酒情况：${latestRecord.alcoholHistory}`,
          `饮食习惯：${latestRecord.dietaryHabits}`,
          `运动频率：${latestRecord.exerciseFrequency}`,
          `睡眠质量：${latestRecord.sleepQuality}`,
          `心理状态：${latestRecord.psychologicalStatus}`
        ].join('\n')

        // 更新表单数据
        reportForm.value = {
          ...reportForm.value,
          physicalExam: physicalInfo,
          pastHistory: historyInfo,
          auxiliaryExam: lifestyleInfo
        }
      }
    }

    ElMessage.success('查询成功')
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 在 script setup 中添加新的响应式变量
const aiSuggestions = ref({
  diagnosis: '',
  treatment: '',
  chiefComplaint: '',
  presentIllness: '',
  pastHistory: '',
  physicalExam: '',
  auxiliaryExam: ''
})

// 修改 generateDiagnosis 方法，只生成初步诊断
const generateDiagnosis = async () => {
  if (!reportForm.value.chiefComplaint && !reportForm.value.presentIllness) {
    ElMessage.warning('请先填写主诉和现病史')
    return
  }

  diagnosisLoading.value = true
  try {
    const prompt = `
      请你作为一名专业的医生，根据以下病例信息进行分析并给出初步诊断：

      主诉：${reportForm.value.chiefComplaint || '无'}
      现病史：${reportForm.value.presentIllness || '无'}
      既往病史：${reportForm.value.pastHistory || '无'}
      体格检查：${reportForm.value.physicalExam || '无'}
      辅助检查：${reportForm.value.auxiliaryExam || '无'}

      请按以下格式输出：
      初步诊断：（请列出诊断依据和诊断结果）
    `

    const response = await axios.post('/api/deepseek/chat/stream', prompt, {
      responseType: 'text'
    })

    if (response.data) {
      const fullResponse = response.data
        .split('\n')
        .filter(line => line.startsWith('data:'))
        .map(line => line.slice(5).trim())
        .filter(content => content && content !== '[DONE]')
        .join('')

      console.log('AI 诊断建议:', fullResponse)

      const diagnosisMatch = fullResponse.match(/初步诊断[:：]([\s\S]*?)$/)

      if (diagnosisMatch?.[1]) {
        aiSuggestions.value.diagnosis = cleanTextContent(diagnosisMatch[1])
        ElMessage.success('AI 分析完成')
      } else {
        ElMessage.warning('AI 返回的内容格式异常')
      }
    }
  } catch (error) {
    console.error('AI 分析失败:', error)
    ElMessage.error('AI 分析失败，请稍后重试')
  } finally {
    diagnosisLoading.value = false
  }
}

// 添加新的 generateTreatment 方法，只生成处理意见
const generateTreatment = async () => {
  if (!reportForm.value.diagnosis) {
    ElMessage.warning('请先填写初步诊断')
    return
  }

  treatmentLoading.value = true
  try {
    const prompt = `
      请你作为一名专业的医生，根据以下病例信息和初步诊断，给出具体的处理意见：

      主诉：${reportForm.value.chiefComplaint || '无'}
      现病史：${reportForm.value.presentIllness || '无'}
      既往病史：${reportForm.value.pastHistory || '无'}
      体格检查：${reportForm.value.physicalExam || '无'}
      辅助检查：${reportForm.value.auxiliaryExam || '无'}
      初步诊断：${reportForm.value.diagnosis || '无'}

      请按以下格式输出：
      处理意见：（根据诊断给出具体的治疗方案和建议）
    `

    const response = await axios.post('/api/deepseek/chat/stream', prompt, {
      responseType: 'text'
    })

    if (response.data) {
      const fullResponse = response.data
        .split('\n')
        .filter(line => line.startsWith('data:'))
        .map(line => line.slice(5).trim())
        .filter(content => content && content !== '[DONE]')
        .join('')

      console.log('AI 处理建议:', fullResponse)

      const treatmentMatch = fullResponse.match(/处理意见[:：]([\s\S]*?)$/)

      if (treatmentMatch?.[1]) {
        aiSuggestions.value.treatment = cleanTextContent(treatmentMatch[1])
        ElMessage.success('AI 分析完成')
      } else {
        ElMessage.warning('AI 返回的内容格式异常')
      }
    }
  } catch (error) {
    console.error('AI 分析失败:', error)
    ElMessage.error('AI 分析失败，请稍后重试')
  } finally {
    treatmentLoading.value = false
  }
}

// 修改 getFromConsultation 方法
const getFromConsultation = async (type) => {
  if (!reportForm.value.patientId) {
    ElMessage.warning('请先输入患者ID并查询基本信息')
    return
  }

  // 根据类型设置对应的 loading 状态
  if (type === 'chiefComplaint') {
    chiefComplaintLoading.value = true
  } else {
    presentIllnessLoading.value = true
  }

  try {
    const doctorId = getUserId()
    const consultResponse = await axios.get(`/api/consultations/search`, {
      params: {
        userId: reportForm.value.patientId,
        doctorId: doctorId
      }
    })

    if (consultResponse.data.code === 1 && consultResponse.data.data.list.length > 0) {
      const consultData = consultResponse.data.data.list[0]
      const prompt = type === 'chiefComplaint' 
        ? `
          请你作为一名专业的医生，根据以下咨询记录分析并生成规范的主诉：
          
          咨询时间：${consultData.consultTime}
          患者描述：${consultData.content}
          医生回复：${consultData.replyContent}
          
          请直接输出主诉内容，简要描述患者主要症状和持续时间，不要包含"主诉："等前缀，不要包含其他内容。
        `
        : `
          请你作为一名专业的医生，根据以下咨询记录分析并生成规范的现病史：
          
          咨询时间：${consultData.consultTime}
          患者描述：${consultData.content}
          医生回复：${consultData.replyContent}
          
          请直接输出现病史内容，详细描述发病经过、症状演变和治疗经历。注意：
          1. 不要包含"现病史："等前缀
          2. 不要包含主诉相关内容
          3. 只输出现病史部分
          4. 按时间顺序描述病情发展过程
        `

      const response = await axios.post('/api/deepseek/chat/stream', prompt, {
        responseType: 'text'
      })

      if (response.data) {
        const fullResponse = response.data
          .split('\n')
          .filter(line => line.startsWith('data:'))
          .map(line => line.slice(5).trim())
          .filter(content => content && content !== '[DONE]')
          .join('')

        // 直接使用清理后的文本
        const cleanedText = cleanTextContent(fullResponse)
        
        // 根据类型更新对应的建议内容
        if (type === 'chiefComplaint') {
          aiSuggestions.value.chiefComplaint = cleanedText
        } else {
          aiSuggestions.value.presentIllness = cleanedText
        }

        ElMessage.success(`已从咨询记录提取${type === 'chiefComplaint' ? '主诉' : '现病史'}`)
      }
    } else {
      ElMessage.warning('未找到相关咨询记录')
    }
  } catch (error) {
    console.error('获取咨询记录失败:', error)
    ElMessage.error('获取咨询记录失败，请稍后重试')
  } finally {
    // 根据类型重置对应的 loading 状态
    if (type === 'chiefComplaint') {
      chiefComplaintLoading.value = false
    } else {
      presentIllnessLoading.value = false
    }
  }
}

// 修改 useAISuggestion 方法，添加对主诉和现病史的支持
const useAISuggestion = (type) => {
  if (aiSuggestions.value[type]) {
    reportForm.value[type] = aiSuggestions.value[type]
    aiSuggestions.value[type] = ''
  }
}

// 获取对应的 loading 状态
const getLoadingState = (key) => {
  const loadingMap = {
    diagnosis: diagnosisLoading,
    treatment: treatmentLoading,
    pastHistory: pastHistoryLoading,
    physicalExam: physicalExamLoading,
    auxiliaryExam: auxiliaryExamLoading
  }
  return loadingMap[key]?.value || false
}

// 处理 AI 辅助按钮点击
const handleAIAssist = (key) => {
  switch (key) {
    case 'diagnosis':
      generateDiagnosis()
      break
    case 'treatment':
      generateTreatment()
      break
    case 'pastHistory':
      generatePastHistoryAnalysis()
      break
    case 'physicalExam':
      generatePhysicalExamAnalysis()
      break
    case 'auxiliaryExam':
      generateAuxiliaryExamAnalysis()
      break
  }
}

// 生成病史分析
const generatePastHistoryAnalysis = async () => {
  if (!reportForm.value.pastHistory) {
    ElMessage.warning('请先填写病史信息')
    return
  }

  pastHistoryLoading.value = true
  try {
    const prompt = `
      请你作为一名专业的医生，对以下病史信息进行分析和概括：

      ${reportForm.value.pastHistory}

      请从以下几个方面进行分析：
      1. 重要的既往病史
      2. 过敏史和不良反应
      3. 家族病史中的遗传倾向
      4. 对当前诊疗的影响

      请直接输出分析结果，不要包含"分析："等前缀。
    `

    const response = await axios.post('/api/deepseek/chat/stream', prompt, {
      responseType: 'text'
    })

    if (response.data) {
      const fullResponse = response.data
        .split('\n')
        .filter(line => line.startsWith('data:'))
        .map(line => line.slice(5).trim())
        .filter(content => content && content !== '[DONE]')
        .join('')

      aiSuggestions.value.pastHistory = cleanTextContent(fullResponse)
      ElMessage.success('病史分析完成')
    }
  } catch (error) {
    console.error('AI 分析失败:', error)
    ElMessage.error('AI 分析失败，请稍后重试')
  } finally {
    pastHistoryLoading.value = false
  }
}

// 生成体格检查分析
const generatePhysicalExamAnalysis = async () => {
  if (!reportForm.value.physicalExam) {
    ElMessage.warning('请先填写体格检查信息')
    return
  }

  physicalExamLoading.value = true
  try {
    const prompt = `
      请你作为一名专业的医生，对以下体格检查结果进行分析和概括：

      ${reportForm.value.physicalExam}

      请从以下几个方面进行分析：
      1. 关键体征指标的异常情况
      2. 各项检查结果的临床意义
      3. 需要重点关注的异常发现
      4. 对诊断的提示意义

      请直接输出分析结果，不要包含"分析："等前缀。
    `

    const response = await axios.post('/api/deepseek/chat/stream', prompt, {
      responseType: 'text'
    })

    if (response.data) {
      const fullResponse = response.data
        .split('\n')
        .filter(line => line.startsWith('data:'))
        .map(line => line.slice(5).trim())
        .filter(content => content && content !== '[DONE]')
        .join('')

      aiSuggestions.value.physicalExam = cleanTextContent(fullResponse)
      ElMessage.success('体格检查分析完成')
    }
  } catch (error) {
    console.error('AI 分析失败:', error)
    ElMessage.error('AI 分析失败，请稍后重试')
  } finally {
    physicalExamLoading.value = false
  }
}

// 生成辅助检查分析
const generateAuxiliaryExamAnalysis = async () => {
  if (!reportForm.value.auxiliaryExam) {
    ElMessage.warning('请先填写辅助检查信息')
    return
  }

  auxiliaryExamLoading.value = true
  try {
    const prompt = `
      请你作为一名专业的医生，对以下辅助检查结果进行分析和概括：

      ${reportForm.value.auxiliaryExam}

      请从以下几个方面进行分析：
      1. 异常检查指标的临床意义
      2. 各项检查结果的相互关系
      3. 对诊断和治疗的指导意义
      4. 是否需要进一步检查的建议

      请直接输出分析结果，不要包含"分析："等前缀。
    `

    const response = await axios.post('/api/deepseek/chat/stream', prompt, {
      responseType: 'text'
    })

    if (response.data) {
      const fullResponse = response.data
        .split('\n')
        .filter(line => line.startsWith('data:'))
        .map(line => line.slice(5).trim())
        .filter(content => content && content !== '[DONE]')
        .join('')

      aiSuggestions.value.auxiliaryExam = cleanTextContent(fullResponse)
      ElMessage.success('辅助检查分析完成')
    }
  } catch (error) {
    console.error('AI 分析失败:', error)
    ElMessage.error('AI 分析失败，请稍后重试')
  } finally {
    auxiliaryExamLoading.value = false
  }
}

// 添加文本提取相关的响应式变量
const textExtractionDialog = ref({
  visible: false,
  extractedText: '',
  selectedField: ''
})

// 定义可用于填写的字段列表
const availableFields = computed(() => {
  return formItems.value
    .filter(item => !item.isDivider && !item.isFixed)
    .map(item => ({
      key: item.key,
      label: item.label
    }))
})

// 文件上传前的验证
const beforeUpload = (file) => {
  const isValidFormat = ['application/pdf', 'image/jpeg', 'image/jpg', 'image/png'].includes(file.type)
  if (!isValidFormat) {
    ElMessage.error('只支持 PDF 和 JPG 格式文件！')
    return false
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB！')
    return false
  }
  return true
}

// 添加标签页相关的响应式变量
const activeTab = ref('preview')
const extractedContents = ref({})

// 判断是否有提取的内容
const hasExtractedContent = computed(() => {
  return Object.keys(extractedContents.value).length > 0 && 
         Object.values(extractedContents.value).some(value => value && value.trim())
})

// 获取字段对应的标签文本
const getFieldLabel = (field) => {
  const fieldMap = {
    patientId: '患者ID',
    name: '姓名',
    gender: '性别',
    birthDate: '出生日期',
    phone: '手机号',
    idCard: '身份证号',
    address: '地址',
    chiefComplaint: '主诉',
    presentIllness: '现病史',
    pastHistory: '既往史',
    physicalExam: '体格检查',
    auxiliaryExam: '辅助检查',
    diagnosis: '初步诊断',
    treatment: '处理意见'
  }
  return fieldMap[field] || field
}

// 获取字段对应的文本框行数
const getFieldRows = (field) => {
  const longTextFields = ['chiefComplaint', 'presentIllness', 'pastHistory', 'physicalExam', 'auxiliaryExam', 'diagnosis', 'treatment']
  return longTextFields.includes(field) ? 3 : 1
}

// 添加新的响应式变量
const rawExtractedText = ref('')

// 修改 handleUploadSuccess 方法
const handleUploadSuccess = (response) => {
  if (response.success && response.page_results) {
    // 创建一个数组来存储所有提取的文本
    const allExtractedText = []
    
    // 创建一个映射来存储提取的信息
    const extractedInfo = {
      patientId: '',
      name: '',
      gender: '',
      birthDate: '',
      phone: '',
      idCard: '',
      address: '',
      chiefComplaint: '',
      presentIllness: '',
      pastHistory: '',
      physicalExam: '',
      auxiliaryExam: '',
      diagnosis: '',
      treatment: ''
    }

    // 遍历所有页面的结果
    response.page_results.forEach(page => {
      if (page.success && page.words_result) {
        // 收集所有文本内容用于总览显示
        const pageText = page.words_result.map(item => item.words).join('\n')
        allExtractedText.push(pageText)
        
        page.words_result.forEach(item => {
          const text = item.words
          
          // 特别处理患者ID - 使用更宽松的匹配
          if (text.match(/患者ID[:|：]?/)) {
            const value = text.replace(/患者ID[:|：]?\s*/, '').trim()
            if (value) {
              extractedInfo.patientId = value
            }
          } else if (text.includes('姓名：')) {
            extractedInfo.name = text.split('：')[1].trim()
          } else if (text.includes('性别：')) {
            extractedInfo.gender = text.split('：')[1].trim()
          } else if (text.includes('出生日期：')) {
            extractedInfo.birthDate = text.split('：')[1].trim()
          } else if (text.includes('手机号：')) {
            extractedInfo.phone = text.split('：')[1].trim()
          } else if (text.includes('身份证号：')) {
            extractedInfo.idCard = text.split('：')[1].trim()
          } else if (text.includes('地址：')) {
            extractedInfo.address = text.split('：')[1].trim()
          }
        })

        // 提取主诉、现病史等较长的内容
        let currentField = ''
        let currentContent = []

        page.words_result.forEach(item => {
          const text = item.words
          
          if (text.includes('主诉：')) {
            currentField = 'chiefComplaint'
            currentContent = []
          } else if (text.includes('现病史：')) {
            if (currentField === 'chiefComplaint') {
              extractedInfo.chiefComplaint = currentContent.join('')
            }
            currentField = 'presentIllness'
            currentContent = []
          } else if (text.includes('病史：')) {
            if (currentField === 'presentIllness') {
              extractedInfo.presentIllness = currentContent.join('')
            }
            currentField = 'pastHistory'
            currentContent = []
          } else if (text.includes('体格检查：')) {
            if (currentField === 'pastHistory') {
              extractedInfo.pastHistory = currentContent.join('')
            }
            currentField = 'physicalExam'
            currentContent = []
          } else if (text.includes('辅助检查')) {
            if (currentField === 'physicalExam') {
              extractedInfo.physicalExam = currentContent.join('')
            }
            currentField = 'auxiliaryExam'
            currentContent = []
          } else if (text.match(/初步诊断[:|：]?/)) {  // 特别处理初步诊断
            if (currentField === 'auxiliaryExam') {
              extractedInfo.auxiliaryExam = currentContent.join('')
            }
            currentField = 'diagnosis'
            currentContent = []
          } else if (text.includes('处理意见：')) {
            if (currentField === 'diagnosis') {
              extractedInfo.diagnosis = currentContent.join('')
            }
            currentField = 'treatment'
            currentContent = []
          } else if (text.includes('生成日期：')) {
            if (currentField === 'treatment') {
              extractedInfo.treatment = currentContent.join('')
            }
            currentField = ''
          } else if (currentField) {
            currentContent.push(text)
          }
        })

        // 在循环结束后，确保最后一个字段也被处理
        if (currentField && currentContent.length > 0) {
          extractedInfo[currentField] = currentContent.join('')
        }
      }
    })

    // 更新提取内容前过滤掉空值
    const filteredInfo = {}
    Object.entries(extractedInfo).forEach(([key, value]) => {
      if (value && value.trim()) {  // 只保留有内容的字段
        filteredInfo[key] = value.trim()
      }
    })

    // 更新提取内容
    extractedContents.value = filteredInfo
    
    // 更新原始文本内容用于总览显示
    rawExtractedText.value = allExtractedText.join('\n\n')
    
    // 自动切换到文字提取标签页
    activeTab.value = 'extraction'
    ElMessage.success('文本提取成功，请检查内容后点击"应用提取内容"按钮进行应用')
  } else {
    ElMessage.error('文本提取失败：' + (response.message || '未知错误'))
  }
}

// 修改 handleExtractedContent 方法
const handleExtractedContent = () => {
  let appliedCount = 0
  Object.keys(extractedContents.value).forEach(key => {
    if (extractedContents.value[key] && reportForm.value.hasOwnProperty(key)) {
      reportForm.value[key] = extractedContents.value[key]
      appliedCount++
    }
  })
  ElMessage.success(`成功应用 ${appliedCount} 项内容到表单`)
  activeTab.value = 'preview'
}

// 添加单个字段应用方法
const handleApplySingleField = (field) => {
  if (extractedContents.value[field] && reportForm.value.hasOwnProperty(field)) {
    reportForm.value[field] = extractedContents.value[field]
    ElMessage.success(`${getFieldLabel(field)}已应用到表单`)
  }
}

// 添加新的响应式变量
const showFullText = ref(false)

// 添加复制文本的方法
const copyFullText = async () => {
  try {
    await navigator.clipboard.writeText(rawExtractedText.value)
    ElMessage.success('文本已复制到剪贴板')
  } catch (err) {
    ElMessage.error('复制失败，请手动复制')
  }
}

// 添加清空字段的方法
const clearField = (key) => {
  reportForm.value[key] = ''
}

// 添加清空所有字段的方法
const confirmClearAll = () => {
  ElMessageBox.confirm(
    '确定要清空所有内容吗？此操作不可恢复',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      // 清空所有表单内容
      Object.keys(reportForm.value).forEach(key => {
        reportForm.value[key] = ''
      })
      // 同时清空 AI 建议
      Object.keys(aiSuggestions.value).forEach(key => {
        aiSuggestions.value[key] = ''
      })
      ElMessage.success('已清空所有内容')
    })
    .catch(() => {
      // 用户取消操作
    })
}

// 添加一个新的计算属性来判断是否有内容
const hasContent = computed(() => {
  return Object.values(reportForm.value).some(value => value !== '')
})
</script>

<style scoped>
.report-maker {
  padding: 20px;
  display: flex;
  gap: 20px;
  height: calc(100vh - 40px);
}

.report-content {
  flex: 0 0 40%;
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  overflow-y: auto;
}

.preview-area {
  flex: 0 0 60%;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  justify-content: center;
  background-color: #f0f2f5;
}

.preview-tabs {
  width: 100%;
  height: 100%;
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.preview-tabs :deep(.el-tabs__content) {
  height: calc(100% - 55px);
  overflow-y: auto;
}

.extraction-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.extraction-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.extraction-header h3 {
  margin: 0;
  color: #303133;
}

.extraction-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}

.extraction-item {
  margin-bottom: 20px;
  padding: 12px;
  border-radius: 4px;
  background-color: #f8f9fa;
}

.extraction-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.extraction-field {
  font-size: 14px;
  color: #606266;
}

.extraction-value {
  width: 100%;
}

.extraction-value :deep(.el-textarea__inner) {
  font-size: 14px;
  background-color: #fff;
}

.preview-paper {
  margin: 0;
  padding: 40px;
  max-width: 210mm;
  margin: 0 auto;
  background: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-tab-pane {
  height: 100%;
  overflow-y: auto;
}

.report-header {
  text-align: center;
  margin-bottom: 20px;
}

.report-header h1 {
  font-size: 24px;
  margin-bottom: 20px;
}

.report-info {
  margin-top: 15px;
  text-align: left;
}

.info-row {
  display: flex;
  gap: 20px;
  margin-bottom: 8px;
}

.info-row p {
  margin: 0;
  flex: 1;
  white-space: nowrap;
}

/* 第二行的地址字段占更多空间 */
.info-row:nth-child(2) p:last-child {
  flex: 3;
}

/* 最后一行(生成日期)靠左对齐 */
.info-row:last-child {
  justify-content: flex-start;
}

.info-row:last-child p {
  flex: none;
}

.report-body {
  text-align: left;
  line-height: 1.8;
  white-space: pre-wrap;
  min-height: 100px; /* 确保内容区域有最小高度 */
}

h2 {
  position: static;  /* 改为静态定位 */
  margin: 0;
  color: #409EFF;
}

.form-item-draggable {
  position: relative;
  padding-left: 20px;
  padding-right: 20px;
}

.drag-handle {
  position: absolute;
  right: 25px;
  top: 50%;
  transform: translateY(-50%);
  cursor: move;
  color: #909399;
  padding: 8px;
  transition: color 0.2s;
  background-color: rgba(255, 255, 255, 0.9);
  opacity: 0.4;
  z-index: 1;
}

.drag-handle:hover {
  color: #409EFF;
  opacity: 1;
}

.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}

/* 确保拖动时表单项样式正确 */
:deep(.el-form-item) {
  margin-bottom: 18px;
  background: white;
}

:deep(.el-form-item.is-error) {
  margin-bottom: 18px;
}

.report-top {
  position: relative;
  margin-bottom: 20px;
  min-height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.patient-id {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  font-size: 14px;
}

.report-top h1 {
  margin: 0;
  padding: 0;
  font-size: 24px;
}

.title-divider {
  border-top: 2px solid #000;
  margin: 10px auto;
  width: 100%;
}

.info-divider {
  border-top: 1px solid #000;
  margin: 10px auto;
  width: 100%;
}

.medical-section {
  margin-bottom: 15px;
  text-align: left;
  line-height: 1.6;
}

.medical-label {
  font-weight: normal;
  font-size: 14px;
  margin-bottom: 4px;
}

.medical-content {
  font-size: 14px;
  white-space: pre-wrap;
  padding-left: 20px;
}

.report-footer {
  margin-top: 20px;
  text-align: right;
  border-top: 1px solid #000;
  padding-top: 10px;
}

.report-footer p {
  margin: 0;
}

.diagnosis-divider {
  border-top: 1px solid #000;
  margin: 10px auto;
  width: 100%;
}

.present-illness-divider {
  border-top: 1px solid #000;
  margin: 10px auto;
  width: 100%;
}

.patient-id-input {
  display: flex;
  gap: 10px;
  width: 100%;
}

.patient-id-input .el-input {
  flex: 1;
}

.textarea-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.custom-label {
  font-size: 14px;
  color: #606266;
}

.input-row {
  margin-top: 24px;
  width: 100%;
}

.input-row .el-input,
.input-row .el-select,
.input-row .el-date-picker {
  width: 100%;
}

/* 隐藏原始的表单标签 */
:deep(.el-form-item__label) {
  display: none;
}

/* 确保表单项内容从最左边开始 */
:deep(.el-form-item__content) {
  margin-left: 0 !important;
}

.ai-assist-btn {
  flex-shrink: 0;
}

.ai-suggestion-area {
  width: 100%;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.ai-suggestion-header {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.ai-suggestion-content {
  width: 100%;
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.ai-suggestion-content .el-input {
  flex: 1;
}

.ai-suggestion-content .el-button {
  flex-shrink: 0;
}

.label-row.basic-info {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 0;
}

.label-row.basic-info .custom-label {
  min-width: 80px;
  flex-shrink: 0;
  margin-right: 10px;
  margin-left: 20px;
}

.basic-info-input {
  flex: 1;
  width: auto;
  margin-right: 30px;
  margin-left: 20px;
}

.label-row.basic-info:has([placeholder*="地址"]) .basic-info-input {
  width: auto;
}

.label-row.basic-info .patient-id-input {
  width: auto;
  flex: 1;
  margin-left: 20px;
}

/* 修改拖动区域相关的样式 */
.form-item-draggable {
  position: relative;
  padding-left: 20px;
  padding-right: 20px;
}

.drag-handle {
  position: absolute;
  right: 25px;
  top: 50%;
  transform: translateY(-50%);
  cursor: move;
  color: #909399;
  padding: 8px;
  transition: color 0.2s;
  background-color: rgba(255, 255, 255, 0.9);
  opacity: 0.4;
  z-index: 1;
}

/* 鼠标悬停时显示完全不透明 */
.drag-handle:hover {
  color: #409EFF;
  opacity: 1;
}

/* 调整输入框右侧的内边距，为拖动图标留出空间 */
.basic-info-input .el-input :deep(.el-input__inner),
.basic-info-input .el-select :deep(.el-input__inner),
.basic-info-input .el-date-picker :deep(.el-input__inner) {
  padding-right: 30px;
}

/* 调整文本域的右侧内边距 */
.input-row .el-textarea :deep(.el-textarea__inner) {
  padding-right: 30px;
}

.form-divider {
  width: 100%;
  padding: 10px 0;
  cursor: move;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #909399;
  font-size: 14px;
  border: none !important;  /* 确保分割线容器没有边框 */
}

.divider-line {
  flex: 1;
  height: 1px;
  background-color: #dcdfe6;
}

/* 拖动时的样式 */
.ghost .form-divider {
  background-color: #409EFF;
  color: #409EFF;
}

.ghost .form-divider .divider-line {
  background-color: #409EFF;
}

/* 为分割线表单项添加特殊样式，移除所有边框和边距 */
.form-item-draggable:has(.form-divider) {
  margin: 0;  /* 移除所有外边距 */
  padding: 0; /* 移除所有内边距 */
  border: none !important; /* 移除所有边框 */
}

.form-item-draggable:has(.form-divider) :deep(.el-form-item__content) {
  border: none !important;  /* 移除内容区域的边框 */
  margin: 0 !important;    /* 移除内容区域的外边距 */
  padding: 0 !important;   /* 移除内容区域的内边距 */
}

/* 确保分割线本身的样式正确 */
.form-divider {
  width: 100%;
  padding: 10px 0;
  cursor: move;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #909399;
  font-size: 14px;
  border: none !important;  /* 确保分割线容器没有边框 */
}

/* 移除 Element Plus 的默认边框样式 */
:deep(.el-form-item.is-without-title) {
  margin: 0;
  border: none !important;
}

/* 固定分割线的样式 */
.fixed-divider {
  border-top: 2px solid #000 !important;  /* 使用更粗的实线 */
  margin: 20px 0 !important;
  padding: 0 !important;
}

/* 隐藏固定分割线的文字和装饰线 */
.fixed-divider .divider-line,
.fixed-divider span {
  display: none;
}

/* 移除固定分割线的鼠标手型 */
.form-item-draggable:has(.fixed-divider) {
  cursor: default;
}

.fixed-divider {
  cursor: default;
}

.button-group {
  display: flex;
  gap: 10px;
}

.consult-btn,
.ai-assist-btn {
  flex-shrink: 0;
}

/* 添加新的样式 */
.file-upload-area {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 20px;
}

.upload-demo {
  display: flex;
  justify-content: center;
}

.el-upload__tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}

/* 调整提取内容区域的样式 */
.extraction-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.extraction-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
}

/* 确保文件上传区域和提取结果之间有适当间距 */
.extraction-header {
  margin-top: 20px;
}

/* 添加新的样式 */
.extraction-summary {
  margin-top: 30px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.summary-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.summary-header h4 {
  margin: 0;
  color: #303133;
}

.summary-header .el-icon {
  color: #909399;
  cursor: help;
}

.extraction-summary :deep(.el-textarea__inner) {
  font-family: monospace;
  font-size: 13px;
  line-height: 1.6;
  background-color: #fff;
}

.summary-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.full-text-dialog {
  /* 不需要 pointer-events 相关样式 */
}

.full-text-dialog :deep(.el-dialog) {
  position: fixed !important;
  top: 20px !important;
  right: 20px !important;
  margin: 0 !important;
  height: calc(100% - 40px) !important;
  width: calc(60% - 40px) !important;
  border-radius: 4px;
  /* 不需要 pointer-events 相关样式 */
  z-index: 2000;
}

/* 修改遮罩层样式 */
.full-text-dialog :deep(.el-overlay) {
  background: transparent !important;
  /* 不需要 pointer-events 相关样式 */
}

/* 自定义标题栏样式 */
.custom-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  cursor: move;
  user-select: none;
}

.close-btn {
  padding: 0;
  font-size: 20px;
}

.close-btn:hover {
  color: #409EFF;
}

/* 调整内容区域样式 */
.full-text-content {
  height: 100%;
  width: 100%;
}

.full-text-content :deep(.el-textarea) {
  height: 100%;
}

.full-text-content :deep(.el-textarea__inner) {
  height: 100% !important;
  font-family: monospace;
  font-size: 14px;
  line-height: 1.6;
  padding: 16px;
  resize: none;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.clear-btn {
  position: absolute;
  right: 5px;
  top: -24px;
  padding: 2px;
  color: #909399;
  z-index: 1;
}

.clear-btn:hover {
  color: #f56c6c;
}

.form-header {
  position: relative;
  height: 40px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.clear-all-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.clear-all-btn:hover {
  opacity: 0.8;
}
</style> 