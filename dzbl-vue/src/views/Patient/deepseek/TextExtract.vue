<template>
  <div class="text-extract">
    <h2>文字提取</h2>
    <div class="content">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>图片文字提取</span>
          </div>
        </template>
        <div class="card-content">
          <!-- 上传区域 -->
          <div class="upload-area">
            <el-upload
              class="upload-demo"
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*,.pdf"
              :on-change="handleFileChange"
            >
              <template #trigger>
                <el-button type="primary">选择文件</el-button>
              </template>
              <template #tip>
                <div class="el-upload__tip">
                  支持jpg/png格式图片和PDF文件
                </div>
              </template>
            </el-upload>
          </div>

          <!-- 预览区域 -->
          <div v-if="currentFile" class="preview-area">
            <!-- PDF文件显示 -->
            <div v-if="isPdf" class="pdf-preview">
              <el-icon><Document /></el-icon>
              <span class="filename">{{ currentFile.name }}</span>
            </div>
            <!-- 图片预览 -->
            <div v-else class="image-preview">
              <img :src="imageUrl" alt="预览图片" />
            </div>
            <div class="preview-actions">
              <el-button type="primary" @click="handleExtract" :loading="loading">
                提取文字
              </el-button>
            </div>
          </div>

          <!-- 提取结果显示区域 -->
          <div v-if="extractedText.length" class="result-area">
            <h3>提取结果：</h3>
            <div class="text-content">
              <p v-for="(item, index) in extractedText" :key="index">
                {{ item.words }}
              </p>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Document } from '@element-plus/icons-vue';

const imageUrl = ref('');
const extractedText = ref([]);
const loading = ref(false);
const currentFile = ref(null);

// 添加计算属性判断是否为PDF
const isPdf = computed(() => {
  return currentFile.value?.raw.type === 'application/pdf';
});

// 修改文件选择处理函数
const handleFileChange = (uploadFile) => {
  if (!beforeUpload(uploadFile.raw)) {
    return;
  }
  currentFile.value = uploadFile;
  
  // 只为图片创建预览URL
  if (!isPdf.value) {
    imageUrl.value = URL.createObjectURL(uploadFile.raw);
  } else {
    imageUrl.value = ''; // 清空图片预览URL
  }
  
  // 清空之前的提取结果
  extractedText.value = [];
};

// 上传前的验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isPdf = file.type === 'application/pdf';
  
  if (!isImage && !isPdf) {
    ElMessage.error('只能上传图片或PDF文件！');
    return false;
  }
  
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB！');
    return false;
  }
  return true;
};

// 提取文字
const handleExtract = async () => {
  if (!currentFile.value) {
    ElMessage.warning('请先选择文件！');
    return;
  }

  loading.value = true;
  try {
    const formData = new FormData();
    formData.append('file', currentFile.value.raw);

    const response = await fetch('/api/ocr/extract', {
      method: 'POST',
      body: formData
    });
    
    const result = await response.json();
    console.log('API返回数据:', result);
    
    if (result.success) {
      const isPdf = currentFile.value.raw.type === 'application/pdf';
      
      if (isPdf) {
        // 处理PDF的返回格式
        if (result.page_results && Array.isArray(result.page_results)) {
          const allResults = result.page_results.reduce((acc, page) => {
            if (page.words_result && Array.isArray(page.words_result)) {
              return acc.concat(page.words_result);
            }
            return acc;
          }, []);
          extractedText.value = allResults;
        } else {
          throw new Error('PDF返回数据格式错误');
        }
      } else {
        // 处理图片的返回格式
        if (Array.isArray(result.words_result)) {
          extractedText.value = result.words_result;
        } else {
          throw new Error('图片返回数据格式错误');
        }
      }
      
      ElMessage.success('文字提取成功！');
    } else {
      throw new Error(result.message || '提取失败');
    }
  } catch (error) {
    console.error('提取文字失败:', error);
    ElMessage.error(error.message || '提取文字失败，请重试！');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.text-extract {
  padding: 20px;
}

.content {
  margin-top: 20px;
}

.card-header {
  font-weight: bold;
}

.card-content {
  padding: 20px;
}

.upload-area {
  text-align: center;
  margin-bottom: 20px;
}

.preview-area {
  margin: 20px 0;
  text-align: center;
}

.pdf-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 20px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  background-color: #f8f8f8;
}

.pdf-preview .el-icon {
  font-size: 24px;
  color: #409EFF;
}

.filename {
  font-size: 16px;
  color: #606266;
}

.image-preview {
  margin: 20px 0;
  text-align: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
}

.preview-actions {
  margin-top: 10px;
}

.result-area {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.text-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.text-content p {
  margin: 5px 0;
  line-height: 1.5;
}

.el-upload__tip {
  margin-top: 8px;
  color: #909399;
}
</style> 