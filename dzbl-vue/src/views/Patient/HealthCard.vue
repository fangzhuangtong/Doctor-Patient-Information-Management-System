<template>
  <div class="health-card">
    <el-tabs v-model="activeTab" class="main-tabs">
      <!-- 健康卡标签页 -->
      <el-tab-pane label="健康卡" name="health">
        <div class="tab-content">
          <div class="left-section">
            <!-- 健康档案展示区域 -->
            <el-card class="record-card" v-loading="loading">
              <template #header>
                <div class="card-header">
                  <span>健康卡</span>
                  <div class="header-buttons">
                    <el-button type="primary" @click="showAnalysis">查看历史健康数据分析</el-button>
                    <el-button type="primary" @click="handleAdd">编辑档案</el-button>
                  </div>
                </div>
              </template>
              
              <!-- 档案内容 -->
              <div v-if="currentRecord" class="record-content">
                <el-descriptions :column="3" border>
                  <!-- 基础信息 -->
                  <el-descriptions-item label="血型">{{ currentRecord.bloodType }}</el-descriptions-item>
                  <el-descriptions-item label="身高">{{ currentRecord.height }}cm</el-descriptions-item>
                  <el-descriptions-item label="体重">{{ currentRecord.weight }}kg</el-descriptions-item>
                  <el-descriptions-item label="BMI">{{ currentRecord.bmi }}</el-descriptions-item>
                  <el-descriptions-item label="风险评分">{{ currentRecord.riskScore }}</el-descriptions-item>
                  <el-descriptions-item label="创建时间">
                    {{ dayjs(currentRecord.createTime).format('YYYY-MM-DD HH:mm:ss') }}
                  </el-descriptions-item>

                  <!-- 身体指标 -->
                  <el-descriptions-item label="血压">{{ currentRecord.bloodPressure }}</el-descriptions-item>
                  <el-descriptions-item label="血脂">{{ currentRecord.bloodLipid }}mmol/L</el-descriptions-item>
                  <el-descriptions-item label="血糖">{{ currentRecord.bloodGlucose }}mmol/L</el-descriptions-item>
                  <el-descriptions-item label="睡眠质量">
                    {{ currentRecord.sleepQuality }}
                  </el-descriptions-item>
                  <el-descriptions-item label="运动频率">
                    {{ currentRecord.exerciseFrequency }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
              <el-empty v-else description="暂无健康档案" />
            </el-card>

            <!-- 添加简要分析卡片 -->
            <el-card class="analysis-summary-card">
              <template #header>
                <div class="card-header">
                  <span>健康状况概要</span>
                </div>
              </template>
              
              <!-- 简要分析区域 -->
              <div class="summary-section">
                <h4>指标分析</h4>
                <div class="indicators-grid">
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">血压</span>
                    <span :class="['value', getStatusClass(healthSummary.bloodPressureStatus)]">
                      {{ healthSummary.bloodPressureStatus }}
                    </span>
                  </div>
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">BMI</span>
                    <span :class="['value', getStatusClass(healthSummary.bmiStatus)]">
                      {{ healthSummary.bmiStatus }}
                    </span>
                  </div>
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">血糖</span>
                    <span :class="['value', getStatusClass(healthSummary.bloodGlucoseStatus)]">
                      {{ healthSummary.bloodGlucoseStatus }}
                    </span>
                  </div>
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">血脂</span>
                    <span :class="['value', getStatusClass(healthSummary.bloodLipidStatus)]">
                      {{ healthSummary.bloodLipidStatus }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 健康状态区域 -->
              <div class="summary-section">
                <h4>健康状态</h4>
                <p class="summary-text">{{ healthSummary.overallStatus || '暂无分析结果' }}</p>
              </div>

              <!-- 健康建议区域 -->
              <div class="summary-section">
                <h4>健康建议</h4>
                <p class="summary-text">{{ healthSummary.recommendation || '暂无建议' }}</p>
              </div>
            </el-card>
          </div>
          
          <div class="right-section">
            <!-- 健康卡 AI 评估 -->
            <el-card class="ai-assessment-card">
              <template #header>
                <div class="card-header">
                  <span>健康卡 AI 评估</span>
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="handleHealthAnalysis"
                    :loading="healthLoading"
                  >
                    开始分析
                  </el-button>
                </div>
              </template>
              <div class="ai-content">
                <div class="chat-container" ref="healthChatContainer">
                  <div v-for="(message, index) in healthChatHistory" :key="index" 
                       :class="['message', message.role]">
                    <div class="message-content" v-html="message.content"></div>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>

      <!-- 生活习惯标签页 -->
      <el-tab-pane label="生活习惯" name="habits">
        <div class="tab-content">
          <div class="left-section">
            <!-- 生活习惯信息 -->
            <el-card class="habit-card">
              <template #header>
                <div class="card-header">
                  <span>生活习惯</span>
                </div>
              </template>
              
              <div v-if="currentRecord">
                <el-descriptions :column="1" border>
                  <el-descriptions-item label="吸烟史">{{ currentRecord.smokingHistory }}</el-descriptions-item>
                  <el-descriptions-item label="饮酒史">{{ currentRecord.alcoholHistory }}</el-descriptions-item>
                  <el-descriptions-item label="睡眠质量">{{ currentRecord.sleepQuality }}</el-descriptions-item>
                  <el-descriptions-item label="运动频率">{{ currentRecord.exerciseFrequency }}</el-descriptions-item>
                  <el-descriptions-item label="心理状态">{{ currentRecord.psychologicalStatus }}</el-descriptions-item>
                  <el-descriptions-item label="饮食习惯">{{ currentRecord.dietaryHabits || '无' }}</el-descriptions-item>
                </el-descriptions>
              </div>
              <el-empty v-else description="暂无生活习惯信息" />
            </el-card>

            <!-- 添加生活习惯概要分析卡片 -->
            <el-card class="analysis-summary-card">
              <template #header>
                <div class="card-header">
                  <span>生活习惯评估概要</span>
                </div>
              </template>
              
              <!-- 习惯评估区域 -->
              <div class="summary-section">
                <h4>习惯评估</h4>
                <div class="indicators-grid">
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">吸烟情况</span>
                    <span :class="['value', getHabitStatusClass(habitSummary.smokingStatus)]">
                      {{ habitSummary.smokingStatus }}
                    </span>
                  </div>
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">饮酒情况</span>
                    <span :class="['value', getHabitStatusClass(habitSummary.alcoholStatus)]">
                      {{ habitSummary.alcoholStatus }}
                    </span>
                  </div>
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">睡眠质量</span>
                    <span :class="['value', getHabitStatusClass(habitSummary.sleepStatus)]">
                      {{ habitSummary.sleepStatus }}
                    </span>
                  </div>
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">运动频率</span>
                    <span :class="['value', getHabitStatusClass(habitSummary.exerciseStatus)]">
                      {{ habitSummary.exerciseStatus }}
                    </span>
                  </div>
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">心理状态</span>
                    <span :class="['value', getHabitStatusClass(habitSummary.psychologicalStatus)]">
                      {{ habitSummary.psychologicalStatus }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 生活方式评价 -->
              <div class="summary-section">
                <h4>生活方式评价</h4>
                <p class="summary-text">{{ habitSummary.overallStatus || '暂无分析结果' }}</p>
              </div>

              <!-- 改善建议 -->
              <div class="summary-section">
                <h4>改善建议</h4>
                <p class="summary-text">{{ habitSummary.recommendation || '暂无建议' }}</p>
              </div>
            </el-card>
          </div>
          
          <div class="right-section">
            <!-- 生活习惯 AI 评估 -->
            <el-card class="ai-assessment-card">
              <template #header>
                <div class="card-header">
                  <span>生活习惯 AI 评估</span>
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="handleHabitAnalysis"
                    :loading="habitLoading"
                  >
                    开始分析
                  </el-button>
                </div>
              </template>
              <div class="ai-content">
                <div class="chat-container" ref="habitChatContainer">
                  <div v-for="(message, index) in habitChatHistory" :key="index" 
                       :class="['message', message.role]">
                    <div class="message-content" v-html="message.content"></div>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>

      <!-- 病史信息标签页 -->
      <el-tab-pane label="病史信息" name="medical">
        <div class="tab-content">
          <div class="left-section">
            <!-- 病史信息 -->
            <el-card class="medical-history-card">
              <template #header>
                <div class="card-header">
                  <span>病史信息</span>
                </div>
              </template>
              
              <div v-if="currentRecord">
                <el-descriptions :column="1" border>
                  <el-descriptions-item label="过敏史">{{ currentRecord.allergyHistory || '无' }}</el-descriptions-item>
                  <el-descriptions-item label="既往史">{{ currentRecord.pastMedicalHistory || '无' }}</el-descriptions-item>
                  <el-descriptions-item label="家族病史">{{ currentRecord.familyHistory || '无' }}</el-descriptions-item>
                </el-descriptions>
              </div>
              <el-empty v-else description="暂无病史信息" />
            </el-card>

            <!-- 添加病史概要分析卡片 -->
            <el-card class="analysis-summary-card">
              <template #header>
                <div class="card-header">
                  <span>病史评估概要</span>
                </div>
              </template>
              
              <!-- 病史评估区域 -->
              <div class="summary-section">
                <h4>风险评估</h4>
                <div class="indicators-grid">
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">过敏风险</span>
                    <span :class="['value', getMedicalStatusClass(medicalSummary.allergyStatus)]">
                      {{ medicalSummary.allergyStatus }}
                    </span>
                  </div>
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">既往病史</span>
                    <span :class="['value', getMedicalStatusClass(medicalSummary.pastMedicalStatus)]">
                      {{ medicalSummary.pastMedicalStatus }}
                    </span>
                  </div>
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">家族病史</span>
                    <span :class="['value', getMedicalStatusClass(medicalSummary.familyHistoryStatus)]">
                      {{ medicalSummary.familyHistoryStatus }}
                    </span>
                  </div>
                  <div class="indicator-item" v-if="currentRecord">
                    <span class="label">综合风险</span>
                    <span :class="['value', getMedicalStatusClass(medicalSummary.overallRiskStatus)]">
                      {{ medicalSummary.overallRiskStatus }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 风险评估结论 -->
              <div class="summary-section">
                <h4>评估结论</h4>
                <p class="summary-text">{{ medicalSummary.conclusion || '暂无分析结果' }}</p>
              </div>

              <!-- 预防建议 -->
              <div class="summary-section">
                <h4>预防建议</h4>
                <p class="summary-text">{{ medicalSummary.recommendation || '暂无建议' }}</p>
              </div>
            </el-card>
          </div>
          
          <div class="right-section">
            <!-- 病史信息 AI 评估 -->
            <el-card class="ai-assessment-card">
              <template #header>
                <div class="card-header">
                  <span>病史信息 AI 评估</span>
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="handleMedicalAnalysis"
                    :loading="medicalLoading"
                  >
                    开始分析
                  </el-button>
                </div>
              </template>
              <div class="ai-content">
                <div class="chat-container" ref="medicalChatContainer">
                  <div v-for="(message, index) in medicalChatHistory" :key="index" 
                       :class="['message', message.role]">
                    <div class="message-content" v-html="message.content"></div>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 健康分析对话框 -->
    <el-dialog
      title="健康数据分析"
      v-model="analysisVisible"
      width="80%"
      class="analysis-dialog"
    >
      <div class="analysis-content">
        <!-- BMI 进度条 -->
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>BMI 指数</span>
              <el-tooltip 
                content="BMI≤18.4：偏瘦；18.5~23.9：正常；≥24：超重"
                placement="top"
              >
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="progress-container">
            <div class="progress-bar">
              <!-- 背景条 -->
              <div class="progress-background">
                <div class="section" style="background-color: #E6A23C"></div>
                <div class="section" style="background-color: #67C23A"></div>
                <div class="section" style="background-color: #F56C6C"></div>
                <div class="section" style="background-color: #CC0000"></div>
              </div>
              <!-- 指针 -->
              <div 
                class="pointer"
                :style="{
                  left: getPointerPosition(currentRecord?.bmi)
                }"
              ></div>
            </div>
            <div class="progress-value">
              {{ currentRecord?.bmi || 0 }}
              <div class="status">{{ getBmiStatus(currentRecord?.bmi) }}</div>
            </div>
          </div>
        </el-card>

        <!-- 风险评分进度条 -->
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>健康风险评分</span>
              <el-tooltip 
                content="≤25：安全；25~50：低风险；50~75：中风险；≥75：高风险"
                placement="top"
              >
                <el-icon><QuestionFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="progress-container">
            <div class="progress-bar">
              <!-- 背景条 -->
              <div class="progress-background">
                <div class="section" style="background-color: #67C23A"></div>
                <div class="section" style="background-color: #E6A23C"></div>
                <div class="section" style="background-color: #F56C6C"></div>
                <div class="section" style="background-color: #CC0000"></div>
              </div>
              <!-- 指针 -->
              <div 
                class="pointer"
                :style="{
                  left: getRiskScorePosition(currentRecord?.riskScore)
                }"
              ></div>
            </div>
            <div class="progress-value">
              {{ currentRecord?.riskScore || 0 }}
              <div class="status">{{ getRiskStatus(currentRecord?.riskScore) }}</div>
            </div>
          </div>
        </el-card>

        <!-- BMI 趋势图 -->
        <el-card class="chart-card trend-chart">
          <template #header>
            <div class="chart-header">
              <span>BMI 趋势</span>
            </div>
          </template>
          <div ref="bmiChartRef" class="chart-content" style="width: 100%; height: calc(100% - 50px);"></div>
        </el-card>

        <!-- 风险评分趋势图 -->
        <el-card class="chart-card trend-chart">
          <template #header>
            <div class="chart-header">
              <span>风险评分趋势</span>
            </div>
          </template>
          <div ref="riskChartRef" class="chart-content" style="width: 100%; height: calc(100% - 50px);"></div>
        </el-card>

        <!-- 添加AI健康评估区域 -->
        <el-card class="chart-card ai-analysis-card">
          <template #header>
            <div class="chart-header">
              <span>AI健康评估</span>
              <el-button 
                type="primary" 
                size="small" 
                @click="handleAnalysisAI"
                :loading="analysisAILoading"
              >
                生成评估
              </el-button>
            </div>
          </template>
          <div class="ai-analysis-content">
            <div v-if="analysisAIResult" class="ai-result">
              <div class="ai-result-header">
                <h4>健康状况综合评估</h4>
              </div>
              <div class="ai-result-body" v-html="analysisAIResult"></div>
            </div>
            <el-empty v-else :description="'点击「生成评估」按钮获取AI分析结果'"></el-empty>
          </div>
        </el-card>
      </div>
    </el-dialog>

    <!-- 新增档案对话框 -->
    <el-dialog
      title="新增健康档案"
      v-model="dialogVisible"
      :fullscreen="true"
      :destroy-on-close="false"
      :close-on-click-modal="false"
    >
      <template #header>
        <div class="dialog-header">
          <span class="dialog-title">编辑健康档案</span>
          <div class="dialog-buttons">
            <el-button @click="handleSubmit" type="primary">确定</el-button>
            <el-button @click="dialogVisible = false">取消</el-button>
          </div>
        </div>
      </template>
      <div class="dialog-content">
        <!-- 左侧手动输入区域 -->
        <div class="manual-input-section">
          <h3>手动输入</h3>
          <el-form :model="form" label-width="120px">
            <el-tabs>
              <!-- 基础信息 -->
              <el-tab-pane label="基础信息">
                <el-form-item label="血型">
                  <el-select v-model="form.bloodType" placeholder="请选择血型">
                    <el-option label="A" value="A" />
                    <el-option label="B" value="B" />
                    <el-option label="O" value="O" />
                    <el-option label="AB" value="AB" />
                    <el-option label="未知" value="未知" />
                  </el-select>
                </el-form-item>
                <el-form-item label="身高">
                  <el-input-number v-model="form.height" :precision="2" :step="0.1" />
                  <span class="ml-2">cm</span>
                </el-form-item>
                <el-form-item label="体重">
                  <el-input-number v-model="form.weight" :precision="2" :step="0.1" />
                  <span class="ml-2">kg</span>
                </el-form-item>
                <el-form-item label="血压">
                  <el-input v-model="form.bloodPressure" placeholder="例如：120/80" />
                </el-form-item>
                <el-form-item label="血脂">
                  <el-input-number v-model="form.bloodLipid" :precision="2" :step="0.1" />
                  <span class="ml-2">mmol/L</span>
                </el-form-item>
                <el-form-item label="血糖">
                  <el-input-number v-model="form.bloodGlucose" :precision="2" :step="0.1" />
                  <span class="ml-2">mmol/L</span>
                </el-form-item>
              </el-tab-pane>

              <!-- 生活习惯 -->
              <el-tab-pane label="生活习惯">
                <el-form-item label="吸烟史">
                  <el-select v-model="form.smokingHistory">
                    <el-option label="无" value="无" />
                    <el-option label="偶尔" value="偶尔" />
                    <el-option label="经常" value="经常" />
                    <el-option label="已戒" value="已戒" />
                  </el-select>
                </el-form-item>
                <el-form-item label="饮酒史">
                  <el-select v-model="form.alcoholHistory">
                    <el-option label="无" value="无" />
                    <el-option label="偶尔" value="偶尔" />
                    <el-option label="经常" value="经常" />
                    <el-option label="已戒" value="已戒" />
                  </el-select>
                </el-form-item>
                <el-form-item label="运动频率">
                  <el-select v-model="form.exerciseFrequency">
                    <el-option label="无" value="无" />
                    <el-option label="每周1-2次" value="每周1-2次" />
                    <el-option label="每周3-4次" value="每周3-4次" />
                    <el-option label="每天" value="每天" />
                  </el-select>
                </el-form-item>
                <el-form-item label="睡眠质量">
                  <el-select v-model="form.sleepQuality">
                    <el-option label="良好" value="良好" />
                    <el-option label="一般" value="一般" />
                    <el-option label="差" value="差" />
                  </el-select>
                </el-form-item>
                <el-form-item label="心理状态">
                  <el-select v-model="form.psychologicalStatus">
                    <el-option label="正常" value="正常" />
                    <el-option label="焦虑" value="焦虑" />
                    <el-option label="抑郁" value="抑郁" />
                  </el-select>
                </el-form-item>
                <el-form-item label="饮食习惯">
                  <el-input v-model="form.dietaryHabits" type="textarea" />
                </el-form-item>
              </el-tab-pane>

              <!-- 病史信息 -->
              <el-tab-pane label="病史信息">
                <el-form-item label="过敏史">
                  <el-input v-model="form.allergyHistory" type="textarea" />
                </el-form-item>
                <el-form-item label="既往史">
                  <el-input v-model="form.pastMedicalHistory" type="textarea" />
                </el-form-item>
                <el-form-item label="家族病史">
                  <el-input v-model="form.familyHistory" type="textarea" />
                </el-form-item>
              </el-tab-pane>
            </el-tabs>
          </el-form>
        </div>

        <!-- 右侧文件提取区域 -->
        <div class="file-extraction-section">
          <h3>文件提取</h3>
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
        </div>
      </div>
    </el-dialog>

    <!-- 完整文本对话框 -->
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
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { QuestionFilled, InfoFilled, Close } from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'
import * as echarts from 'echarts'
import { getUserId } from '@/utils/auth'

// 当前健康档案数据
const currentRecord = ref(null)
const dialogVisible = ref(false)
const analysisVisible = ref(false)
const userId = 2 // 暂时固定为1，后续从登录信息中获取
const form = ref({
  userId,
  bloodType: '',
  height: null,
  weight: null,
  bloodPressure: '',
  bloodLipid: null,
  bloodGlucose: null,
  allergyHistory: '',
  pastMedicalHistory: '',
  familyHistory: '',
  smokingHistory: '无',
  alcoholHistory: '无',
  dietaryHabits: '',
  exerciseFrequency: '无',
  sleepQuality: '良好',
  psychologicalStatus: '正常'
})

// 添加文件提取相关的响应式变量
const activeDialogTab = ref('manual')
const extractedContents = ref({})
const rawExtractedText = ref('')
const showFullText = ref(false)

// 判断是否有提取的内容
const hasExtractedContent = computed(() => {
  return Object.keys(extractedContents.value).length > 0 && 
         Object.values(extractedContents.value).some(value => value && value.trim())
})

// 获取字段对应的标签文本
const getFieldLabel = (field) => {
  const fieldMap = {
    bloodType: '血型',
    height: '身高',
    weight: '体重',
    bloodPressure: '血压',
    bloodLipid: '血脂',
    bloodGlucose: '血糖',
    smokingHistory: '吸烟史',
    alcoholHistory: '饮酒史',
    exerciseFrequency: '运动频率',
    sleepQuality: '睡眠质量',
    psychologicalStatus: '心理状态',
    dietaryHabits: '饮食习惯',
    allergyHistory: '过敏史',
    pastMedicalHistory: '既往史',
    familyHistory: '家族病史'
  }
  return fieldMap[field] || field
}

// 获取字段对应的文本框行数
const getFieldRows = (field) => {
  const longTextFields = ['dietaryHabits', 'allergyHistory', 'pastMedicalHistory', 'familyHistory']
  return longTextFields.includes(field) ? 3 : 1
}

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

// 处理文件上传成功
const handleUploadSuccess = (response) => {
  console.log('文件上传响应:', response) // 添加日志输出

  // 检查响应格式
  if (!response) {
    ElMessage.error('上传失败：服务器响应为空')
    return
  }

  // 检查响应状态
  if (!response.success) {
    ElMessage.error('上传失败：' + (response.message || '未知错误'))
    return
  }

  // 检查数据
  if (!Array.isArray(response.words_result)) {
    ElMessage.error('上传失败：数据格式错误')
    return
  }

  try {
    // 创建一个映射来存储提取的信息
    const extractedInfo = {
      bloodType: '',
      height: '',
      weight: '',
      bloodPressure: '',
      bloodLipid: '',
      bloodGlucose: '',
      smokingHistory: '',
      alcoholHistory: '',
      exerciseFrequency: '',
      sleepQuality: '',
      psychologicalStatus: '',
      dietaryHabits: '',
      allergyHistory: '',
      pastMedicalHistory: '',
      familyHistory: ''
    }

    // 用于存储原始文本
    const allExtractedText = []

    // 创建一个辅助函数来获取下一个值
    const getNextValue = (currentIndex) => {
      if (currentIndex + 1 < response.words_result.length) {
        return response.words_result[currentIndex + 1].words
      }
      return ''
    }

    // 遍历OCR结果
    response.words_result.forEach((item, index) => {
      const text = item.words.trim()
      const nextValue = getNextValue(index)

      // 将每一行文本添加到原始文本中
      allExtractedText.push(text + (nextValue ? ': ' + nextValue : ''))

      // 根据关键字匹配字段
      switch (text) {
        case '血型':
          extractedInfo.bloodType = nextValue
          break
        case '身高':
          extractedInfo.height = nextValue.replace('cm', '')
          break
        case '体重':
          extractedInfo.weight = nextValue.replace('kg', '')
          break
        case '血压':
          extractedInfo.bloodPressure = nextValue
          break
        case '血脂':
          extractedInfo.bloodLipid = nextValue.replace('mmol/L', '')
          break
        case '血糖':
          extractedInfo.bloodGlucose = nextValue.replace('mmol/L', '')
          break
        case '睡眠质量':
          // 处理可能的特殊情况，如"一般"被识别为"般"
          let sleepQuality = nextValue
          if (nextValue === '般') {
            sleepQuality = '一般'
          }
          extractedInfo.sleepQuality = sleepQuality
          break
        case '运动频率':
          extractedInfo.exerciseFrequency = nextValue
          break
        case '过敏史':
          extractedInfo.allergyHistory = nextValue
          break
        case '既往史':
          extractedInfo.pastMedicalHistory = nextValue
          break
        case '家族病史':
          extractedInfo.familyHistory = nextValue
          break
        case '吸烟史':
          extractedInfo.smokingHistory = nextValue
          break
        case '饮酒史':
          extractedInfo.alcoholHistory = nextValue
          break
        case '心理状态':
          extractedInfo.psychologicalStatus = nextValue
          break
        case '饮食习惯':
          extractedInfo.dietaryHabits = nextValue
          break
      }
    })

    // 更新提取内容前过滤掉空值
    const filteredInfo = {}
    Object.entries(extractedInfo).forEach(([key, value]) => {
      const trimmedValue = value.trim()
      // 保留所有非空值，包括"无"，因为这也是有效的信息
      if (trimmedValue) {
        filteredInfo[key] = trimmedValue
      }
    })

    // 检查是否有提取到内容
    if (Object.keys(filteredInfo).length === 0) {
      ElMessage.warning('未能从文件中提取到有效内容')
      return
    }

    // 更新提取内容
    extractedContents.value = filteredInfo
    
    // 更新原始文本内容用于总览显示
    rawExtractedText.value = allExtractedText.join('\n')
    
    ElMessage.success('文本提取成功，请检查内容后点击"应用提取内容"按钮进行应用')
  } catch (error) {
    console.error('处理提取内容时出错:', error)
    ElMessage.error('处理提取内容时出错：' + error.message)
  }
}

// 处理文件上传错误
const handleUploadError = (error) => {
  console.error('文件上传失败:', error)
  ElMessage.error('文件上传失败，请稍后重试')
}

// 应用提取的内容到表单
const handleExtractedContent = () => {
  let appliedCount = 0
  Object.keys(extractedContents.value).forEach(key => {
    if (extractedContents.value[key] && form.value.hasOwnProperty(key)) {
      form.value[key] = extractedContents.value[key]
      appliedCount++
    }
  })
  ElMessage.success(`成功应用 ${appliedCount} 项内容到表单`)
  activeDialogTab.value = 'manual'
}

// 应用单个字段
const handleApplySingleField = (field) => {
  if (extractedContents.value[field] && form.value.hasOwnProperty(field)) {
    form.value[field] = extractedContents.value[field]
    ElMessage.success(`${getFieldLabel(field)}已应用到表单`)
  }
}

// 复制完整文本
const copyFullText = async () => {
  try {
    await navigator.clipboard.writeText(rawExtractedText.value)
    ElMessage.success('文本已复制到剪贴板')
  } catch (err) {
    ElMessage.error('复制失败，请手动复制')
  }
}

const bmiChartRef = ref(null)
const riskChartRef = ref(null)
let bmiChart = null
let riskChart = null
const historyRecords = ref([])
const loading = ref(false)

// 添加健康卡 AI 对话相关的响应式变量
const healthChatHistory = ref([])
const healthInput = ref('')
const healthLoading = ref(false)
const healthChatContainer = ref(null)

// 添加生活习惯 AI 对话相关的响应式变量
const habitChatHistory = ref([])
const habitLoading = ref(false)
const habitChatContainer = ref(null)

// 添加病史信息 AI 对话相关的响应式变量
const medicalChatHistory = ref([])
const medicalLoading = ref(false)
const medicalChatContainer = ref(null)

// 添加标签页激活状态
const activeTab = ref('health')

// 添加健康概要的响应式对象
const healthSummary = ref({
  bloodPressureStatus: '未分析',
  bmiStatus: '未分析',
  bloodGlucoseStatus: '未分析',
  bloodLipidStatus: '未分析',
  overallStatus: '',
  recommendation: ''
})

// 添加生活习惯概要的响应式对象
const habitSummary = ref({
  smokingStatus: '未分析',
  alcoholStatus: '未分析',
  sleepStatus: '未分析',
  exerciseStatus: '未分析', // 添加运动频率状态
  psychologicalStatus: '未分析',
  overallStatus: '',
  recommendation: ''
})

// 添加病史概要的响应式对象
const medicalSummary = ref({
  allergyStatus: '未分析',
  pastMedicalStatus: '未分析',
  familyHistoryStatus: '未分析',
  overallRiskStatus: '未分析',
  conclusion: '',
  recommendation: ''
})

// 添加分析AI相关的响应式变量
const analysisAIResult = ref('')
const analysisAILoading = ref(false)

// 获取最新的健康档案
const getLatestRecord = async () => {
  loading.value = true
  try {
    const allRecords = await request.get('/health-records')
    if (allRecords.data && allRecords.data.length > 0) {
      // 筛选当前用户的记录并按时间排序
      const userRecords = allRecords.data
        .filter(record => record.userId === userId)
        .sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      
      if (userRecords.length > 0) {
        // 获取最新记录的详细信息
        const latestRecord = userRecords[0]
        const detailRes = await request.get(`/health-records/${latestRecord.recordId}`)
        currentRecord.value = detailRes.data
        historyRecords.value = userRecords
        // 等待 DOM 更新后初始化图表
        await nextTick()
        // 确保容器有尺寸后再初始化
        setTimeout(() => {
          initCharts()
        }, 100)
      }
    }
  } catch (error) {
    console.error('获取健康档案失败:', error)
    ElMessage.error('获取健康档案失败')
  } finally {
    loading.value = false
  }
}

// 获取健康档案
const getHealthRecord = async () => {
  try {
    // 修改接口路径
    const { data } = await request.get(`/health-records/user/${getUserId()}`)
    currentRecord.value = data
  } catch (error) {
    ElMessage.error('获取健康档案失败')
  }
}

// 新增档案
const handleAdd = () => {
  // 如果有现有记录，使用现有记录的数据作为默认值
  if (currentRecord.value) {
    const {
      bloodType = '',
      height = null,
      weight = null,
      bloodPressure = '',
      bloodLipid = null,
      bloodGlucose = null,
      allergyHistory = '',
      pastMedicalHistory = '',
      familyHistory = '',
      smokingHistory = '无',
      alcoholHistory = '无',
      dietaryHabits = '',
      exerciseFrequency = '无',
      sleepQuality = '良好',
      psychologicalStatus = '正常'
    } = currentRecord.value

    form.value = {
      userId,
      bloodType,
      height,
      weight,
      bloodPressure,
      bloodLipid,
      bloodGlucose,
      allergyHistory,
      pastMedicalHistory,
      familyHistory,
      smokingHistory,
      alcoholHistory,
      dietaryHabits,
      exerciseFrequency,
      sleepQuality,
      psychologicalStatus
    }
  } else {
    form.value = {
      userId,
      bloodType: '',
      height: null,
      weight: null,
      bloodPressure: '',
      bloodLipid: null,
      bloodGlucose: null,
      allergyHistory: '',
      pastMedicalHistory: '',
      familyHistory: '',
      smokingHistory: '无',
      alcoholHistory: '无',
      dietaryHabits: '',
      exerciseFrequency: '无',
      sleepQuality: '良好',
      psychologicalStatus: '正常'
    }
  }
  dialogVisible.value = true
}

// 提交新增
const handleSubmit = async () => {
  try {
    await request.post('/health-records', form.value)
    ElMessage.success('添加成功')
    dialogVisible.value = false
    getLatestRecord() // 刷新数据
  } catch (error) {
    console.error('添加健康档案失败:', error)
    ElMessage.error('添加健康档案失败')
  }
}

// 添加一个计算 BMI 状态的函数
const getBmiStatus = (bmi) => {
  if (!bmi) return '未知'
  if (bmi <= 18.4) return '偏瘦'
  if (bmi <= 23.9) return '正常'
  if (bmi <= 27.9) return '超重'
  return '肥胖'
}

// 计算指针位置
const getPointerPosition = (bmi) => {
  if (!bmi) return '12.5%'
  if (bmi <= 18.4) return '12.5%'  // 第一区间中点
  if (bmi <= 23.9) return '37.5%'  // 第二区间中点
  if (bmi <= 27.9) return '62.5%'  // 第三区间中点
  return '87.5%'  // 第四区间中点
}

// 添加风险评分状态计算函数
const getRiskStatus = (score) => {
  if (!score) return '未知'
  if (score <= 25) return '安全'
  if (score <= 50) return '低风险'
  if (score <= 75) return '中风险'
  return '高风险'
}

// 添加风险评分指针位置计算函数
const getRiskScorePosition = (score) => {
  if (!score) return '12.5%'
  if (score <= 25) return '12.5%'  // 第一区间中点
  if (score <= 50) return '37.5%'  // 第二区间中点
  if (score <= 75) return '62.5%'  // 第三区间中点
  return '87.5%'  // 第四区间中点
}

// 修改初始化图表的方法
const initCharts = () => {
  // 确保容器存在且有尺寸
  const bmiContainer = bmiChartRef.value
  const riskContainer = riskChartRef.value
  
  if (!bmiContainer || !riskContainer) {
    console.warn('Chart containers not found')
    return
  }
  
  if (bmiContainer.clientWidth === 0 || bmiContainer.clientHeight === 0 ||
      riskContainer.clientWidth === 0 || riskContainer.clientHeight === 0) {
    console.warn('Chart containers have no size')
    return
  }

  // 处理数据
  const sortedRecords = [...historyRecords.value].sort((a, b) => 
    new Date(a.createTime) - new Date(b.createTime)
  )
  const times = sortedRecords.map((_, index) => index)
  const bmiData = sortedRecords.map(record => record.bmi)
  const riskData = sortedRecords.map(record => record.riskScore)
  
  // 初始化 BMI 趋势图
  if (bmiChartRef.value) {
    if (bmiChart) {
      bmiChart.dispose()
    }
    bmiChart = echarts.init(bmiChartRef.value)
    bmiChart.setOption({
      grid: {
        top: 30,
        right: 30,
        bottom: 30,
        left: 50,
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: times,
        boundaryGap: false,
        axisLabel: {
          formatter: (value) => `记录${parseInt(value) + 1}`
        }
      },
      yAxis: {
        type: 'value',
        max: 35,
        min: 15,
        splitLine: {
          lineStyle: {
            type: 'dashed'
          }
        }
      },
      series: [{
        data: bmiData,
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: {
          width: 3
        },
        areaStyle: {
          opacity: 0.2,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(58,77,233,0.8)' },
            { offset: 1, color: 'rgba(58,77,233,0.1)' }
          ])
        }
      }],
      tooltip: {
        trigger: 'axis',
        formatter: function(params) {
          const index = params[0].dataIndex
          const record = sortedRecords[index]
          return `时间：${dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss')}<br/>
                 BMI：${record.bmi}`
        }
      }
    })
  }
  
  // 初始化风险评分趋势图
  if (riskChartRef.value) {
    if (riskChart) {
      riskChart.dispose()
    }
    riskChart = echarts.init(riskChartRef.value)
    riskChart.setOption({
      grid: {
        top: 30,
        right: 30,
        bottom: 30,
        left: 50,
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: times,
        boundaryGap: false,
        axisLabel: {
          formatter: (value) => `记录${parseInt(value) + 1}`
        }
      },
      yAxis: {
        type: 'value',
        max: 80,
        min: 0,
        splitLine: {
          lineStyle: {
            type: 'dashed'
          }
        }
      },
      series: [{
        data: riskData,
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: {
          width: 3
        },
        areaStyle: {
          opacity: 0.2,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255,70,70,0.8)' },
            { offset: 1, color: 'rgba(255,70,70,0.1)' }
          ])
        }
      }],
      tooltip: {
        trigger: 'axis',
        formatter: function(params) {
          const index = params[0].dataIndex
          const record = sortedRecords[index]
          return `时间：${dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss')}<br/>
                  风险评分：${record.riskScore}`
        }
      }
    })
  }
}

// 处理窗口大小变化
const handleResize = () => {
  bmiChart?.resize()
  riskChart?.resize()
}

// 添加显示分析对话框的方法
const showAnalysis = () => {
  analysisVisible.value = true
  // 等待对话框显示后初始化图表
  nextTick(() => {
    setTimeout(() => {
      initCharts()
    }, 100)
  })
}

// 在 setup 中添加以下代码
const loadChatHistoryFromStorage = () => {
  try {
    const storedHealthChat = localStorage.getItem('healthChatHistory')
    const storedHabitChat = localStorage.getItem('habitChatHistory')
    const storedMedicalChat = localStorage.getItem('medicalChatHistory')
    
    if (storedHealthChat) {
      healthChatHistory.value = JSON.parse(storedHealthChat)
    }
    if (storedHabitChat) {
      habitChatHistory.value = JSON.parse(storedHabitChat)
    }
    if (storedMedicalChat) {
      medicalChatHistory.value = JSON.parse(storedMedicalChat)
    }
  } catch (error) {
    console.error('加载聊天历史失败:', error)
  }
}

// 修改健康分析处理函数
const handleHealthAnalysis = async () => {
  if (healthLoading.value) return
  
  // 构建健康数据上下文
  const healthContext = currentRecord.value ? {
    basicInfo: {
      bloodType: currentRecord.value.bloodType,
      height: currentRecord.value.height,
      weight: currentRecord.value.weight,
      bmi: currentRecord.value.bmi,
      riskScore: currentRecord.value.riskScore,
      bloodPressure: currentRecord.value.bloodPressure,
      bloodLipid: currentRecord.value.bloodLipid,
      bloodGlucose: currentRecord.value.bloodGlucose
    }
  } : null

  if (!healthContext) {
    ElMessage.warning('暂无健康数据可供分析')
    return
  }

  healthLoading.value = true
  
  try {
    const assistantMessage = {
      role: 'assistant',
      content: ''
    }
    healthChatHistory.value = [assistantMessage]
    
    const response = await fetch('/api/deepseek/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        text: "请分析我的各项健康指标是否正常，并给出专业的健康建议。",
        context: {
          role: "system",
          content: `你是一个专业的健康顾问。请对以下健康数据进行全面分析：
            ${JSON.stringify(healthContext, null, 2)}
            
            请按照以下格式进行分析：
            1. 各项指标分析（分析每个指标是否在正常范围内）
            2. 健康风险评估（根据异常指标评估可能存在的健康风险）
            3. 改善建议（针对异常指标给出具体的改善建议）
            
            分析要专业、客观，使用适当的医学术语，并给出详细的解释。
            如果发现异常指标，请着重提醒并说明原因。
            回答要分点列出，使用换行和适当的文字强调来增加可读性。`
        }
      })
    })

    const reader = response.body.getReader()
    const decoder = new TextDecoder()

    while (true) {
      const { value, done } = await reader.read()
      if (done) break
      
      const chunk = decoder.decode(value)
      const lines = chunk.split('\n')
      
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const content = line.slice(5).trim()
          if (content) {
            const cleanContent = content
              .replace(/#{3,}/g, '')
              .replace(/\*{2,}/g, '')
              .replace(/\n/g, '<br>')
            
            assistantMessage.content += cleanContent
            healthChatHistory.value = [...healthChatHistory.value]
            // 保存到 localStorage
            localStorage.setItem('healthChatHistory', JSON.stringify(healthChatHistory.value))
          }
        }
      }
      
      await nextTick()
      if (healthChatContainer.value) {
        healthChatContainer.value.scrollTop = healthChatContainer.value.scrollHeight
      }
    }

    // 在分析完成后，解析 AI 响应并更新概要信息
    const analysisContent = assistantMessage.content
    
    // 更新健康概要
    healthSummary.value = parseAIResponse(analysisContent)
    
    // 保存概要到 localStorage
    localStorage.setItem('healthSummary', JSON.stringify(healthSummary.value))
    
  } catch (error) {
    console.error('健康分析失败:', error)
    healthChatHistory.value = [{
      role: 'assistant',
      content: '分析失败，请稍后重试'
    }]
    localStorage.setItem('healthChatHistory', JSON.stringify(healthChatHistory.value))
  } finally {
    healthLoading.value = false
  }
}

// 添加 AI 响应解析函数
const parseAIResponse = (content) => {
  const summary = {
    bloodPressureStatus: '未分析',
    bmiStatus: '未分析',
    bloodGlucoseStatus: '未分析',
    bloodLipidStatus: '未分析',
    overallStatus: '',
    recommendation: ''
  }

  try {
    // 解析血压状态
    if (content.includes('血压')) {
      const bpMatch = content.match(/血压[^。]+/)
      if (bpMatch) {
        summary.bloodPressureStatus = bpMatch[0].includes('正常') ? '正常' :
          bpMatch[0].includes('偏高') ? '偏高' : '偏低'
      }
    }

    // 解析 BMI 状态
    if (content.includes('BMI')) {
      const bmiMatch = content.match(/BMI[^。]+/)
      if (bmiMatch) {
        summary.bmiStatus = bmiMatch[0].includes('正常') ? '正常' :
          bmiMatch[0].includes('超重') ? '偏高' : '偏低'
      }
    }

    // 解析血糖状态
    if (content.includes('血糖')) {
      const glucoseMatch = content.match(/血糖[^。]+/)
      if (glucoseMatch) {
        summary.bloodGlucoseStatus = glucoseMatch[0].includes('正常') ? '正常' :
          glucoseMatch[0].includes('偏高') ? '偏高' : '偏低'
      }
    }

    // 解析血脂状态
    if (content.includes('血脂')) {
      const lipidMatch = content.match(/血脂[^。]+/)
      if (lipidMatch) {
        summary.bloodLipidStatus = lipidMatch[0].includes('正常') ? '正常' :
          lipidMatch[0].includes('偏高') ? '偏高' : '偏低'
      }
    }

    // 提取整体健康状态（取第一个总结性的句子）
    const statusMatch = content.match(/总体[^。]+。/)
    if (statusMatch) {
      summary.overallStatus = statusMatch[0]
    }

    // 提取主要建议（取第一个建议性的句子）
    const recommendMatch = content.match(/建议[^。]+。/)
    if (recommendMatch) {
      summary.recommendation = recommendMatch[0]
    }

  } catch (error) {
    console.error('解析 AI 响应失败:', error)
  }

  return summary
}

// 修改生活习惯 AI 响应解析函数
const parseHabitResponse = (content) => {
  const summary = {
    smokingStatus: '未分析',
    alcoholStatus: '未分析',
    sleepStatus: '未分析',
    exerciseStatus: '未分析', // 添加运动频率状态
    psychologicalStatus: '未分析',
    overallStatus: '',
    recommendation: ''
  }

  try {
    // 解析吸烟状态
    if (content.includes('吸烟')) {
      const smokingMatch = content.match(/吸烟[^。]+/)
      if (smokingMatch) {
        summary.smokingStatus = smokingMatch[0].includes('无') ? '良好' :
          smokingMatch[0].includes('偶尔') ? '偶尔' : '经常'
      }
    }

    // 解析饮酒状态
    if (content.includes('饮酒')) {
      const alcoholMatch = content.match(/饮酒[^。]+/)
      if (alcoholMatch) {
        summary.alcoholStatus = alcoholMatch[0].includes('无') ? '良好' :
          alcoholMatch[0].includes('偶尔') ? '偶尔' : '经常'
      }
    }

    // 解析睡眠状态
    if (content.includes('睡眠')) {
      const sleepMatch = content.match(/睡眠[^。]+/)
      if (sleepMatch) {
        summary.sleepStatus = sleepMatch[0].includes('良好') ? '良好' :
          sleepMatch[0].includes('一般') ? '一般' : '差'
      }
    }

    // 解析心理状态
    if (content.includes('心理')) {
      const psychMatch = content.match(/心理[^。]+/)
      if (psychMatch) {
        summary.psychologicalStatus = psychMatch[0].includes('正常') ? '正常' :
          psychMatch[0].includes('轻度') ? '轻度异常' : '需关注'
      }
    }

    // 添加运动频率状态解析
    if (content.includes('运动')) {
      const exerciseMatch = content.match(/运动[^。]+/)
      if (exerciseMatch) {
        summary.exerciseStatus = exerciseMatch[0].includes('规律') ? '良好' :
          exerciseMatch[0].includes('偶尔') ? '一般' : 
          exerciseMatch[0].includes('无') ? '需改善' : '未分析'
      }
    }

    // 修改整体评价提取逻辑
    const statusMatch = content.match(/生活方式[^。]+。|整体来看[^。]+。|总体[^。]+。/)
    if (statusMatch) {
      summary.overallStatus = statusMatch[0]
    } else {
      // 如果没有找到明确的生活方式评价，尝试提取其他总结性语句
      const altMatch = content.match(/([^。]+生活习惯[^。]+。|[^。]+生活方式[^。]+。)/)
      if (altMatch) {
        summary.overallStatus = altMatch[0]
      }
    }

    // 提取建议（尝试多种可能的表述）
    const recommendMatch = content.match(/建议[^。]+。|改善措施[^。]+。|需要[^。]+改善[^。]+。/)
    if (recommendMatch) {
      summary.recommendation = recommendMatch[0]
    }

  } catch (error) {
    console.error('解析生活习惯 AI 响应失败:', error)
  }

  return summary
}

// 修改生活习惯分析的系统提示
const handleHabitAnalysis = async () => {
  if (habitLoading.value) return
  
  // 构建生活习惯数据上下文
  const habitContext = currentRecord.value ? {
    habits: {
      smokingHistory: currentRecord.value.smokingHistory,
      alcoholHistory: currentRecord.value.alcoholHistory,
      sleepQuality: currentRecord.value.sleepQuality,
      psychologicalStatus: currentRecord.value.psychologicalStatus,
      dietaryHabits: currentRecord.value.dietaryHabits,
      exerciseFrequency: currentRecord.value.exerciseFrequency
    }
  } : null

  if (!habitContext) {
    ElMessage.warning('暂无生活习惯数据可供分析')
    return
  }

  habitLoading.value = true
  
  try {
    const assistantMessage = {
      role: 'assistant',
      content: ''
    }
    habitChatHistory.value = [assistantMessage]
    
    const response = await fetch('/api/deepseek/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        text: "请分析我的生活习惯是否健康，并给出改善建议。",
        context: {
          role: "system",
          content: `你是一个专业的健康生活顾问。请对以下生活习惯数据进行全面分析：
            ${JSON.stringify(habitContext, null, 2)}
            
            请按照以下格式进行分析：
            1. 生活习惯评估
              - 吸烟饮酒情况分析
              - 运动习惯分析（频率和强度）
              - 作息规律性分析
              - 心理健康状况分析
              - 饮食习惯分析
            
            2. 生活方式总体评价
              - 明确指出"生活方式总体评价：xxx"
            
            3. 改善建议
              - 以"建议："开头，给出具体的改善措施
              - 建议要循序渐进，易于执行
            
            分析要专业、客观，注重实用性。
            如果发现不健康的习惯，要说明其危害并提供改善方案。
            回答要分点列出，使用换行和适当的文字强调来增加可读性。`
        }
      })
    })

    const reader = response.body.getReader()
    const decoder = new TextDecoder()

    while (true) {
      const { value, done } = await reader.read()
      if (done) break
      
      const chunk = decoder.decode(value)
      const lines = chunk.split('\n')
      
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const content = line.slice(5).trim()
          if (content) {
            const cleanContent = content
              .replace(/#{3,}/g, '')
              .replace(/\*{2,}/g, '')
              .replace(/\n/g, '<br>')
            
            assistantMessage.content += cleanContent
            habitChatHistory.value = [...habitChatHistory.value]
            // 保存到 localStorage
            localStorage.setItem('habitChatHistory', JSON.stringify(habitChatHistory.value))
          }
        }
      }
      
      await nextTick()
      if (habitChatContainer.value) {
        habitChatContainer.value.scrollTop = habitChatContainer.value.scrollHeight
      }
    }

    // 在分析完成后，解析 AI 响应并更新概要信息
    const analysisContent = assistantMessage.content
    
    // 更新生活习惯概要
    habitSummary.value = parseHabitResponse(analysisContent)
    
    // 保存概要到 localStorage
    localStorage.setItem('habitSummary', JSON.stringify(habitSummary.value))
    
  } catch (error) {
    console.error('生活习惯分析失败:', error)
    habitChatHistory.value = [{
      role: 'assistant',
      content: '分析失败，请稍后重试'
    }]
    localStorage.setItem('habitChatHistory', JSON.stringify(habitChatHistory.value))
  } finally {
    habitLoading.value = false
  }
}

// 修改病史 AI 响应解析函数
const parseMedicalResponse = (content) => {
  const summary = {
    allergyStatus: '未分析',
    pastMedicalStatus: '未分析',
    familyHistoryStatus: '未分析',
    overallRiskStatus: '未分析',
    conclusion: '',
    recommendation: ''
  }

  try {
    // 解析过敏状态
    if (content.includes('过敏')) {
      const allergyMatch = content.match(/过敏[^。]+/)
      if (allergyMatch) {
        summary.allergyStatus = allergyMatch[0].includes('无') ? '无风险' :
          allergyMatch[0].includes('轻度') ? '低风险' : '需关注'
      }
    }

    // 解析既往病史状态
    if (content.includes('既往')) {
      const pastMatch = content.match(/既往[^。]+/)
      if (pastMatch) {
        summary.pastMedicalStatus = pastMatch[0].includes('无') ? '无风险' :
          pastMatch[0].includes('轻微') ? '低风险' : '需关注'
      }
    }

    // 解析家族病史状态
    if (content.includes('家族')) {
      const familyMatch = content.match(/家族[^。]+/)
      if (familyMatch) {
        summary.familyHistoryStatus = familyMatch[0].includes('无') ? '无风险' :
          familyMatch[0].includes('中等') ? '中等风险' : '高风险'
      }
    }

    // 解析综合风险状态
    if (content.includes('风险')) {
      const riskMatch = content.match(/综合[^。]+风险[^。]+/)
      if (riskMatch) {
        summary.overallRiskStatus = riskMatch[0].includes('低') ? '低风险' :
          riskMatch[0].includes('中等') ? '中等风险' : '高风险'
      }
    }

    // 改进评估结论提取逻辑
    const conclusionPatterns = [
      /综合评估[^。]+。/,
      /总体评估[^。]+。/,
      /综合来看[^。]+。/,
      /整体分析[^。]+。/,
      /评估结论[^：]+：[^。]+。/
    ]

    for (const pattern of conclusionPatterns) {
      const match = content.match(pattern)
      if (match) {
        summary.conclusion = match[0]
        break
      }
    }

    // 如果上述模式都没匹配到，尝试提取包含"评估"或"分析"的句子
    if (!summary.conclusion) {
      const fallbackMatch = content.match(/([^。]+评估[^。]+。|[^。]+分析结果[^。]+。)/)
      if (fallbackMatch) {
        summary.conclusion = fallbackMatch[0]
      }
    }

    // 改进建议提取逻辑
    const recommendPatterns = [
      /建议[^。]+。/,
      /预防措施[^。]+。/,
      /需要注意[^。]+。/,
      /应该[^。]+。/,
      /建议采取以下措施：[^。]+。/
    ]

    for (const pattern of recommendPatterns) {
      const match = content.match(pattern)
      if (match) {
        summary.recommendation = match[0]
        break
      }
    }

  } catch (error) {
    console.error('解析病史 AI 响应失败:', error)
  }

  return summary
}

// 处理病史信息分析
const handleMedicalAnalysis = async () => {
  if (medicalLoading.value) return
  
  // 构建病史数据上下文
  const medicalContext = currentRecord.value ? {
    medicalHistory: {
      allergyHistory: currentRecord.value.allergyHistory,
      pastMedicalHistory: currentRecord.value.pastMedicalHistory,
      familyHistory: currentRecord.value.familyHistory,
      // 添加基础健康指标作为参考
      bloodPressure: currentRecord.value.bloodPressure,
      bloodLipid: currentRecord.value.bloodLipid,
      bloodGlucose: currentRecord.value.bloodGlucose,
      bmi: currentRecord.value.bmi
    }
  } : null

  if (!medicalContext) {
    ElMessage.warning('暂无病史信息可供分析')
    return
  }

  medicalLoading.value = true
  
  try {
    const assistantMessage = {
      role: 'assistant',
      content: ''
    }
    medicalChatHistory.value = [assistantMessage]
    
    const response = await fetch('/api/deepseek/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        text: "请分析我的病史信息，评估健康风险并给出建议。",
        context: {
          role: "system",
          content: `你是一个专业的医疗顾问。请对以下病史信息进行全面分析：
            ${JSON.stringify(medicalContext, null, 2)}
            
            请按照以下格式进行分析：
            1. 病史综合评估
               - 过敏史分析及注意事项
               - 既往病史分析及其影响
               - 家族病史风险评估
               - 结合当前健康指标进行综合分析
            
            2. 评估结论
               - 请明确以"评估结论："开头，总结整体健康状况
            
            3. 健康风险评估
               - 基于病史的潜在健康风险分析
               - 家族遗传风险分析
               - 与当前健康状况的关联性分析
            
            4. 预防和管理建议
               - 以"建议："开头，给出具体的预防措施
               - 针对性的健康监测建议
               - 生活方式调整建议
               - 需要重点关注的健康指标
            
            分析要专业、严谨，使用医学术语的同时要确保患者易于理解。
            如果发现重要的健康风险，要特别强调并提供具体的预防措施。
            回答要分点列出，使用换行和适当的文字强调来增加可读性。`
        }
      })
    })

    const reader = response.body.getReader()
    const decoder = new TextDecoder()

    while (true) {
      const { value, done } = await reader.read()
      if (done) break
      
      const chunk = decoder.decode(value)
      const lines = chunk.split('\n')
      
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const content = line.slice(5).trim()
          if (content) {
            const cleanContent = content
              .replace(/#{3,}/g, '')
              .replace(/\*{2,}/g, '')
              .replace(/\n/g, '<br>')
            
            assistantMessage.content += cleanContent
            medicalChatHistory.value = [...medicalChatHistory.value]
            // 保存到 localStorage
            localStorage.setItem('medicalChatHistory', JSON.stringify(medicalChatHistory.value))
          }
        }
      }
      
      await nextTick()
      if (medicalChatContainer.value) {
        medicalChatContainer.value.scrollTop = medicalChatContainer.value.scrollHeight
      }
    }

    // 在分析完成后，解析 AI 响应并更新概要信息
    const analysisContent = assistantMessage.content
    
    // 更新病史概要
    medicalSummary.value = parseMedicalResponse(analysisContent)
    
    // 保存概要到 localStorage
    localStorage.setItem('medicalSummary', JSON.stringify(medicalSummary.value))
    
  } catch (error) {
    console.error('病史分析失败:', error)
    medicalChatHistory.value = [{
      role: 'assistant',
      content: '分析失败，请稍后重试'
    }]
    localStorage.setItem('medicalChatHistory', JSON.stringify(medicalChatHistory.value))
  } finally {
    medicalLoading.value = false
  }
}

// 添加状态样式辅助函数
const getStatusClass = (status) => {
  if (!status) return 'status-unknown'
  if (status.includes('正常')) return 'status-normal'
  if (status.includes('偏高') || status.includes('超重')) return 'status-high'
  if (status.includes('偏低')) return 'status-low'
  return 'status-unknown'
}

// 添加生活习惯状态样式辅助函数
const getHabitStatusClass = (status) => {
  if (!status) return 'status-unknown'
  if (status.includes('良好') || status.includes('正常')) return 'status-normal'
  if (status.includes('偶尔') || status.includes('一般')) return 'status-medium'
  if (status.includes('经常') || status.includes('差')) return 'status-high'
  return 'status-unknown'
}

// 添加病史状态样式辅助函数
const getMedicalStatusClass = (status) => {
  if (!status) return 'status-unknown'
  if (status.includes('无') || status.includes('低风险')) return 'status-normal'
  if (status.includes('中等') || status.includes('需关注')) return 'status-medium'
  if (status.includes('高') || status.includes('严重')) return 'status-high'
  return 'status-unknown'
}

// 修改 handleAnalysisAI 函数
const handleAnalysisAI = async () => {
  if (analysisAILoading.value) return
  
  // 构建图表数据上下文
  const analysisContext = currentRecord.value ? {
    currentData: {
      bmi: currentRecord.value.bmi,
      riskScore: currentRecord.value.riskScore,
    },
    trends: {
      hasTrends: historyRecords.value.length > 1,
      recordCount: historyRecords.value.length,
      bmiTrend: historyRecords.value.map(record => ({
        value: record.bmi,
        time: dayjs(record.createTime).format('YYYY-MM-DD')
      })),
      riskScoreTrend: historyRecords.value.map(record => ({
        value: record.riskScore,
        time: dayjs(record.createTime).format('YYYY-MM-DD')
      }))
    }
  } : null

  if (!analysisContext) {
    ElMessage.warning('暂无数据可供分析')
    return
  }

  analysisAILoading.value = true
  analysisAIResult.value = ''
  
  try {
    const response = await fetch('/api/deepseek/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        text: "请分析这些健康指标数据的变化趋势和当前状态。",
        context: {
          role: "system",
          content: `你是一个专业的健康数据分析师。请对以下四个图表的数据进行分析：
            ${JSON.stringify(analysisContext, null, 2)}
            
            请提供以下分析：
            1. BMI指数评估
               - 当前BMI值的健康状态
               - BMI变化趋势分析（如果有历史数据）
               - 对体重管理的建议
            
            2. 健康风险评估
               - 当前风险评分的等级判定
               - 风险变化趋势分析（如果有历史数据）
               - 风险管理建议
            
            3. 总体趋势评估
               - 健康状况的整体变化趋势
               - 需要重点关注的指标
               - 改善建议
            
            请用简洁专业的语言描述，避免使用标题符号（如#），直接使用分段和换行来组织内容。
            对于异常或需要关注的指标，请用加粗标记。
            对于改善建议，请用斜体标记。`
        }
      })
    })

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let result = ''

    while (true) {
      const { value, done } = await reader.read()
      if (done) break
      
      const chunk = decoder.decode(value)
      const lines = chunk.split('\n')
      
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const content = line.slice(5).trim()
          if (content) {
            // 清理格式并转换为HTML
            const formattedContent = content
              .replace(/#+\s/g, '') // 移除标题符号
              .replace(/\n/g, '<br>') // 转换换行
            result += formattedContent
            analysisAIResult.value = result
              .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // 处理加粗
              .replace(/\*(.*?)\*/g, '<em>$1</em>') // 处理斜体
          }
        }
      }
    }
    
    localStorage.setItem('analysisAIResult', analysisAIResult.value)
    
  } catch (error) {
    console.error('数据分析失败:', error)
    analysisAIResult.value = '<span style="color: #F56C6C">分析失败，请稍后重试</span>'
  } finally {
    analysisAILoading.value = false
  }
}

onMounted(() => {
  getLatestRecord()
  loadChatHistoryFromStorage()
  window.addEventListener('resize', handleResize)
  
  // 加载保存的健康概要
  const storedSummary = localStorage.getItem('healthSummary')
  if (storedSummary) {
    healthSummary.value = JSON.parse(storedSummary)
  }

  // 加载保存的生活习惯概要
  const storedHabitSummary = localStorage.getItem('habitSummary')
  if (storedHabitSummary) {
    habitSummary.value = JSON.parse(storedHabitSummary)
  }

  // 加载保存的病史概要
  const storedMedicalSummary = localStorage.getItem('medicalSummary')
  if (storedMedicalSummary) {
    medicalSummary.value = JSON.parse(storedMedicalSummary)
  }
  
  // 加载保存的分析AI结果
  const storedAnalysisResult = localStorage.getItem('analysisAIResult')
  if (storedAnalysisResult) {
    analysisAIResult.value = storedAnalysisResult
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  bmiChart?.dispose()
  riskChart?.dispose()
})
</script>

<style scoped>
.health-card {
  padding: 20px;
  height: 100%;
}

/* 标签页容器样式 */
.main-tabs {
  height: 100%;
}

:deep(.el-tabs__content) {
  height: calc(100% - 55px);  /* 减去标签头的高度 */
  overflow: hidden;
}

/* 标签页内容样式 */
.tab-content {
  height: 100%;
  display: flex;
  gap: 20px;
}

/* 左侧区域样式 */
.left-section {
  width: 1000px;  /* 保持原有宽度 */
  overflow-y: auto;
}

/* 右侧区域样式 */
.right-section {
  flex: 1;
  min-width: 400px;
  overflow-y: auto;
}

/* AI 评估卡片样式 */
.ai-assessment-card {
  height: 100%;
}

.record-card,
.habit-card,
.medical-history-card {
  flex-shrink: 0;
  max-width: 1000px;
}

.record-content {
  padding: 20px 0;
}

:deep(.el-descriptions) {
  padding: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

:deep(.el-tabs__content) {
  padding: 20px 0;
}

.ml-2 {
  margin-left: 8px;
}

.chart-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;  /* 添加底部间距 */
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;  /* 让容器填充剩余空间 */
}

.progress-bar {
  flex: 1;
  height: 20px;
  background-color: #EBEEF5;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
}

.progress-background {
  display: flex;
  height: 100%;
}

.section {
  flex: 1;
  height: 100%;
}

/* 指针样式 */
.pointer {
  position: absolute;
  top: -4px;
  width: 4px;
  height: 28px;
  background-color: #303133;
  transform: translateX(-50%);
  transition: left 0.3s ease;
}

/* 调整三角形指示器位置 */
.pointer::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  border-top: 4px solid #303133;
}

.progress-value {
  min-width: 48px;
  text-align: right;
  font-weight: bold;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.status {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 修改 tooltip 内容 */
:deep(.el-tooltip__popper) {
  max-width: 300px;
  line-height: 1.5;
}

/* 移除 el-card 默认的内边距 */
:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #EBEEF5;
}

:deep(.el-card__body) {
  padding: 20px;
  height: calc(100% - 60px);
  min-height: 240px;
  display: flex;
  flex-direction: column;
}

.trend-chart {
  /* 修复嵌套语法 */
}
.trend-chart .chart-content {
  height: calc(100% - 50px);
}

.info-section {
  margin-bottom: 20px;
}

.info-section h3 {
  margin-bottom: 15px;
  color: #303133;
  font-size: 16px;
  font-weight: 500;
}

:deep(.el-descriptions__cell) {
  padding: 12px 20px;
}

:deep(.el-descriptions__label) {
  width: 120px;
  color: #606266;
}

/* 修改头部按钮样式 */
.header-buttons {
  display: flex;
  gap: 10px;
}

/* 分析对话框样式 */
.analysis-dialog {
  /* 修复嵌套语法 */
}
.analysis-dialog :deep(.el-dialog__body) {
  padding: 20px;
  max-height: 80vh;
  overflow-y: auto;
}

.analysis-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

/* 图表卡片样式 */
.chart-card {
  min-height: 300px;
}

/* 添加聊天相关样式 */
.chat-container {
  height: 300px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 20px;
}

.message {
  margin-bottom: 12px;
  padding: 8px 12px;
  border-radius: 4px;
}

.message.user {
  background-color: #ecf5ff;
  margin-left: 20%;
}

.message.assistant {
  background-color: #f5f7fa;
  margin-right: 20%;
}

.input-area {
  display: none;
}

/* 修改 AI 评估卡片内容样式 */
.ai-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 修改卡片头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.analysis-summary-card {
  margin-top: 20px;
}

.summary-section {
  margin-bottom: 20px;
}

.summary-section:last-child {
  margin-bottom: 0;
}

.summary-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 500;
}

.indicators-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
}

.indicator-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.indicator-item .label {
  color: #606266;
  font-size: 14px;
}

.indicator-item .value {
  font-weight: 500;
}

.status-normal {
  color: #67C23A;
}

.status-high {
  color: #F56C6C;
}

.status-low {
  color: #E6A23C;
}

.status-unknown {
  color: #909399;
}

.summary-text {
  margin: 0;
  line-height: 1.6;
  color: #606266;
  font-size: 14px;
}

/* 添加生活习惯特定的状态样式 */
.status-medium {
  color: #E6A23C;
}

/* 新增档案对话框样式 */
.extraction-content {
  padding: 20px;
}

.file-upload-area {
  margin-bottom: 20px;
}

.extraction-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.extraction-body {
  margin-bottom: 20px;
}

.extraction-item {
  margin-bottom: 10px;
}

.extraction-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.extraction-field {
  font-weight: 500;
}

.extraction-value {
  flex: 1;
}

.extraction-summary {
  margin-bottom: 20px;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.summary-actions {
  display: flex;
  gap: 10px;
}

.full-text-dialog {
  /* 修复嵌套语法 */
}
.full-text-dialog :deep(.el-dialog__body) {
  padding: 20px;
  max-height: 80vh;
  overflow-y: auto;
}

.full-text-content {
  padding: 20px;
}

.custom-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-btn {
  background: none;
  border: none;
  padding: 0;
  margin: 0;
  font: inherit;
  cursor: pointer;
  outline: inherit;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.copy-btn {
  margin-left: 10px;
}

.dialog-content {
  display: flex;
  gap: 20px;
}

.manual-input-section {
  flex: 1;
  min-width: 400px;
}

.file-extraction-section {
  flex: 1;
  min-width: 400px;
}

/* 新增档案对话框样式 */
.el-dialog.el-dialog--center {
  display: flex;
  flex-direction: column;
  margin: 0 !important;
  height: 100%;
  max-width: 100%;
}

.el-dialog__header {
  padding: 20px;
  margin: 0;
  border-bottom: 1px solid #dcdfe6;
}

.el-dialog__body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  margin: 0;
}

.el-dialog__footer {
  padding: 20px;
  margin: 0;
  border-top: 1px solid #dcdfe6;
}

.dialog-content {
  display: flex;
  gap: 20px;
  height: 100%;
  min-height: 600px;
}

.manual-input-section {
  flex: 1;
  min-width: 500px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.file-extraction-section {
  flex: 1;
  min-width: 500px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.manual-input-section h3,
.file-extraction-section h3 {
  margin-top: 0;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  color: #303133;
  font-size: 18px;
  font-weight: 500;
}

.extraction-content {
  height: calc(100% - 60px);
  display: flex;
  flex-direction: column;
}

.extraction-body {
  flex: 1;
  overflow-y: auto;
  padding-right: 10px;
}

.file-upload-area {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  text-align: center;
}

.extraction-header {
  margin-bottom: 20px;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.extraction-header h3 {
  margin: 0;
  border-bottom: none;
}

.extraction-item {
  margin-bottom: 15px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

/* 滚动条样式优化 */
.manual-input-section,
.file-extraction-section,
.extraction-body {
  scrollbar-width: thin;
  scrollbar-color: #909399 #f4f4f5;
}

.manual-input-section::-webkit-scrollbar,
.file-extraction-section::-webkit-scrollbar,
.extraction-body::-webkit-scrollbar {
  width: 6px;
}

.manual-input-section::-webkit-scrollbar-track,
.file-extraction-section::-webkit-scrollbar-track,
.extraction-body::-webkit-scrollbar-track {
  background: #f4f4f5;
}

.manual-input-section::-webkit-scrollbar-thumb,
.file-extraction-section::-webkit-scrollbar-thumb,
.extraction-body::-webkit-scrollbar-thumb {
  background-color: #909399;
  border-radius: 3px;
}

/* 表单样式优化 */
.el-form {
  height: 100%;
}

.el-tabs {
  height: 100%;
}

:deep(.el-tabs__content) {
  height: calc(100% - 55px);
  overflow-y: auto;
}

:deep(.el-tab-pane) {
  padding: 20px 0;
}

/* 修复嵌套滚动问题 */
:deep(.el-tabs__content)::-webkit-scrollbar {
  width: 6px;
}

:deep(.el-tabs__content)::-webkit-scrollbar-track {
  background: #f4f4f5;
}

:deep(.el-tabs__content)::-webkit-scrollbar-thumb {
  background-color: #909399;
  border-radius: 3px;
}

/* 确保表单项之间有足够间距 */
:deep(.el-form-item) {
  margin-bottom: 22px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

/* 优化文本域样式 */
:deep(.el-textarea__inner) {
  min-height: 100px;
  font-family: inherit;
}

/* 优化按钮样式 */
.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button + .el-button {
  margin-left: 12px;
}

/* 添加对话框标题栏样式 */
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.dialog-title {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.dialog-buttons {
  display: flex;
  gap: 8px;
}

/* 修改对话框内容区域样式 */
.dialog-content {
  height: calc(100% - 20px);
  display: flex;
  gap: 20px;
  padding: 20px;
}

/* 添加AI分析卡片样式 */
.ai-analysis-card {
  grid-column: span 2;  /* 让AI分析卡片占据两列 */
}

.ai-analysis-content {
  height: 100%;
  overflow-y: auto;
}

.ai-result {
  padding: 10px;
}

.ai-result-header {
  margin-bottom: 15px;
}

.ai-result-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 500;
}

.ai-result-body {
  line-height: 1.6;
  color: #606266;
}

.ai-result-body :deep(strong) {
  color: #303133;
}

.ai-result-body :deep(em) {
  font-style: italic;
  color: #409EFF;
}
</style> 