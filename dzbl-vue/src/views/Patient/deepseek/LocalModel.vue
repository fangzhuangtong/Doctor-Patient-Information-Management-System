<template>
  <div class="basic-model">
    <h2>本地模型</h2>
    <div class="content">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>DeepSeek本地模型对话</span>
          </div>
        </template>
        <div class="card-content">
          <!-- 聊天记录显示区域 -->
          <div class="chat-container" ref="chatContainer">
            <div v-for="(message, index) in chatHistory" :key="index" 
                 :class="['message', message.role]">
              <div class="message-content">{{ message.content }}</div>
            </div>
          </div>
          
          <!-- 输入区域 -->
          <div class="input-area">
            <el-input
              v-model="userInput"
              type="textarea"
              :rows="3"
              placeholder="请输入您的问题..."
              @keyup.enter="handleSend"
            />
            <el-button type="primary" @click="handleSend" 
                       :loading="loading">发送</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import axios from 'axios'; // 确保已经安装了 axios

const chatHistory = ref([]);
const userInput = ref('');
const loading = ref(false);
const chatContainer = ref(null);

// 发送消息处理函数
const handleSend = async () => {
  if (!userInput.value.trim() || loading.value) return;
  
  // 添加用户消息
  chatHistory.value.push({
    role: 'user',
    content: userInput.value
  });
  
  loading.value = true;
  
  try {
    // 创建消息对象用于累积响应
    const assistantMessage = {
      role: 'assistant',
      content: ''
    };
    chatHistory.value.push(assistantMessage);
    
    const response = await fetch('/api/deepseek/local/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ text: userInput.value })
    });

    const reader = response.body.getReader();
    const decoder = new TextDecoder();

    while (true) {
      const { value, done } = await reader.read();
      if (done) break;
      
      const chunk = decoder.decode(value);
      const lines = chunk.split('\n');
      
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const content = line.slice(5).trim();
          if (content) {
            assistantMessage.content += content;
            // 强制更新视图
            chatHistory.value = [...chatHistory.value];
          }
        }
      }
      
      // 滚动到底部
      await nextTick();
      if (chatContainer.value) {
        chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
      }
    }
  } catch (error) {
    console.error('发送消息失败:', error);
    chatHistory.value[chatHistory.value.length - 1].content = '发送消息失败，请稍后重试';
  } finally {
    loading.value = false;
    userInput.value = '';
  }
};
</script>

<style scoped>
.basic-model {
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
.chat-container {
  height: 400px;
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
  display: flex;
  gap: 10px;
}
.input-area .el-button {
  align-self: flex-end;
}
</style> 