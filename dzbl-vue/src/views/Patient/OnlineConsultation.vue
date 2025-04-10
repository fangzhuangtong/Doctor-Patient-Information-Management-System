<template>
  <div class="consultation">
    <!-- 左侧聊天列表 -->
    <div class="chat-list">
      <div class="list-header">
        <h3>咨询记录</h3>
        <el-button type="primary" size="small" @click="dialogVisible = true">
          新建咨询
        </el-button>
      </div>
      <div class="list-content">
        <div 
          v-for="item in chatList" 
          :key="item.doctorId"
          class="chat-item"
          :class="{ active: currentDoctor === item.doctorId }"
          @click="selectDoctor(item.doctorId)"
        >
          <el-avatar :size="40" icon="UserFilled" />
          <div class="chat-info">
            <div class="top-line">
              <span class="doctor-name">医生 {{ item.doctorId }}</span>
              <span class="time">{{ formatTime(item.lastTime) }}</span>
            </div>
            <div class="bottom-line">
              <span class="last-message">{{ item.lastMessage }}</span>
              <span v-if="item.unread" class="unread-count">{{ item.unread }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-area">
      <template v-if="currentDoctor">
        <div class="chat-header">
          <span>医生 {{ currentDoctor }}</span>
        </div>
        
        <div class="message-container" ref="messageContainer">
          <!-- 聊天消息 -->
          <div class="message-content">
            <template v-for="(msg, index) in currentMessages" :key="index">
              <div :class="['message', msg.type]">
                <el-avatar :size="30" icon="UserFilled" />
                <div class="bubble">
                  {{ msg.content }}
                </div>
                <div class="message-info">
                  <span class="time">{{ formatTime(msg.time) }}</span>
                  <span v-if="msg.type === 'patient'" class="status" :class="msg.status">
                    {{ msg.status }}
                  </span>
                </div>
              </div>
            </template>
          </div>
        </div>

        <div class="input-area">
          <el-input
            v-model="newMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入咨询内容..."
            @keyup.enter.ctrl="sendMessage"
          />
          <div class="button-group">
            <span class="tip">Ctrl + Enter 发送</span>
            <el-button 
              type="primary" 
              @click="sendMessage"
            >
              发送
            </el-button>
          </div>
        </div>
      </template>
      
      <el-empty v-else description="请选择聊天对象" />
    </div>

    <!-- 新增咨询对话框 -->
    <el-dialog
      title="新建咨询"
      v-model="dialogVisible"
      width="400px"
    >
      <el-form :model="consultForm" label-width="80px">
        <el-form-item label="医生ID">
          <el-input 
            v-model.number="consultForm.doctorId" 
            placeholder="请输入医生ID"
            type="number"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreateConsult">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import request from '@/utils/request'
import { getUserId } from '@/utils/auth'

const chatList = ref([])  // 聊天列表
const currentDoctor = ref(null)  // 当前选中的医生
const currentMessages = ref([])  // 当前聊天消息
const newMessage = ref('')
const messageContainer = ref(null)
const dialogVisible = ref(false)
const consultForm = ref({
  doctorId: null
})

// 修改初始化聊天列表的方法
const initChatList = async () => {
  try {
    const res = await request.get(`/consultations/user/${getUserId()}`)
    if (res.code === 1) {
      // 确保数据是数组
      const records = Array.isArray(res.data) ? res.data : [res.data]
      
      // 首先按时间排序
      records.sort((a, b) => new Date(a.consultTime) - new Date(b.consultTime))
      
      // 按医生ID分组聊天记录
      const groupedMessages = {}
      records.forEach(record => {
        if (!groupedMessages[record.doctorId]) {
          groupedMessages[record.doctorId] = []
        }
        // 添加患者消息
        groupedMessages[record.doctorId].push({
          type: 'patient',
          content: record.content,
          time: record.consultTime,
          status: record.status,
          consultId: record.consultId
        })
        // 如果有回复，添加医生消息
        if (record.replyContent) {
          groupedMessages[record.doctorId].push({
            type: 'doctor',
            content: record.replyContent,
            time: record.consultTime
          })
        }
      })
      
      // 生成聊天列表
      chatList.value = Object.keys(groupedMessages).map(doctorId => {
        const messages = groupedMessages[doctorId]
        const lastMsg = messages[messages.length - 1]
        return {
          doctorId: parseInt(doctorId),
          messages: messages,
          lastMessage: lastMsg.content,
          lastTime: lastMsg.time,
          status: lastMsg.type === 'patient' ? lastMsg.status : '已回复'
        }
      })
    }
  } catch (error) {
    console.error('获取聊天记录失败:', error)
    ElMessage.error('获取聊天记录失败')
  }
}

// 修改选择医生的方法
const selectDoctor = (doctorId) => {
  currentDoctor.value = doctorId
  const chat = chatList.value.find(item => item.doctorId === doctorId)
  if (chat) {
    currentMessages.value = chat.messages
  }
  nextTick(() => {
    scrollToBottom()
  })
}

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.trim()) {
    return
  }

  // 检查是否有未回复的消息
  const hasUnrepliedMessage = currentMessages.value.some(msg => 
    msg.type === 'patient' && msg.status === '未回复'
  )
  
  if (hasUnrepliedMessage) {
    ElMessage.warning('请等待医生回复后再发送新消息')
    return
  }
  
  try {
    // 发送消息到服务器
    const res = await request.post('/consultations', {
      userId: getUserId(),
      doctorId: currentDoctor.value,
      consultType: '文字',
      content: newMessage.value,
      status: '未回复'  // 新消息默认未回复
    })

    if (res.code === 1) {
      const message = {
        type: 'patient',
        content: newMessage.value,
        time: dayjs().format('YYYY-MM-DD HH:mm:ss'),
        status: '未回复'
      }
      
      // 添加到当前消息列表
      currentMessages.value.push(message)
      
      // 更新聊天列表的最后消息
      const chat = chatList.value.find(item => item.doctorId === currentDoctor.value)
      if (chat) {
        chat.lastMessage = message.content
        chat.lastTime = message.time
        chat.status = '未回复'
      }
      
      // 清空输入框
      newMessage.value = ''
      
      nextTick(() => {
        scrollToBottom()
      })
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}

// 新建咨询
const handleCreateConsult = async () => {
  if (!consultForm.value.doctorId) {
    ElMessage.warning('请输入医生ID')
    return
  }
  
  try {
    const res = await request.post('/consultations', {
      userId: getUserId(),
      doctorId: consultForm.value.doctorId,
      consultType: '文字',
      content: '您好，医生',
      status: '未回复'
    })
    
    if (res.code === 1) {
      ElMessage.success('创建咨询成功')
      dialogVisible.value = false
      // 重新获取聊天列表
      await initChatList()
      // 自动选中新建的聊天
      selectDoctor(consultForm.value.doctorId)
      // 重置表单
      consultForm.value.doctorId = null
    }
  } catch (error) {
    console.error('创建咨询失败:', error)
    ElMessage.error('创建咨询失败')
  }
}

onMounted(() => {
  initChatList()
})
</script>

<style scoped>
.consultation {
  height: 100%;
  display: flex;
  background-color: #f5f7fa;
  padding: 20px;
  gap: 20px;
}

.chat-list {
  width: 300px;
  background-color: #fff;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.list-header {
  padding: 15px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-content {
  flex: 1;
  overflow-y: auto;
}

.chat-item {
  padding: 15px;
  display: flex;
  gap: 10px;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
}

.chat-item:hover {
  background-color: #f5f7fa;
}

.chat-item.active {
  background-color: #e6f1fc;
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.top-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.doctor-name {
  font-weight: bold;
}

.time {
  font-size: 12px;
  color: #999;
}

.bottom-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.last-message {
  color: #666;
  font-size: 13px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 10px;
}

.chat-area {
  flex: 1;
  background-color: #fff;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.chat-header {
  padding: 15px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  margin-bottom: 30px;  /* 增加整体间距 */
  position: relative;  /* 为信息栏提供定位上下文 */
}

.message.doctor {
  flex-direction: row;
}

.message.patient {
  flex-direction: row-reverse;
}

.bubble {
  position: relative;
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 4px;
  word-break: break-all;
}

.message-info {
  position: absolute;
  bottom: -25px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  width: 100%;  /* 确保宽度与气泡一致 */
}

.doctor .message-info {
  justify-content: flex-start;
  margin-left: 40px;  /* 为头像留出空间 */
}

.patient .message-info {
  justify-content: flex-end;
  margin-right: 40px;  /* 为头像留出空间 */
}

.time {
  color: #999;
  white-space: nowrap;
}

.status {
  margin: 0 10px;
  white-space: nowrap;
}

.unread-count {
  background-color: #f56c6c;
  color: white;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 12px;
}

.tip {
  color: #909399;
  font-size: 12px;
  margin-right: 10px;
}

.input-area {
  padding: 20px;
  border-top: 1px solid #eee;
}

.button-group {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

/* 滚动条样式 */
.list-content::-webkit-scrollbar,
.message-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.list-content::-webkit-scrollbar-thumb,
.message-container::-webkit-scrollbar-thumb {
  background-color: #ddd;
  border-radius: 3px;
}

.list-content::-webkit-scrollbar-track,
.message-container::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}

/* 状态样式 */
.status.已回复 {
  color: #67C23A;
}

.status.未回复 {
  color: #E6A23C;
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-dialog__body) {
  padding-bottom: 0;
}

/* 添加输入框禁用样式 */
.el-input.is-disabled .el-input__inner {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #c0c4cc;
  cursor: not-allowed;
}
</style> 