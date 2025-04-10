<template>
  <div class="consultation">
    <!-- 左侧会话列表 -->
    <div class="chat-list">
      <div class="list-header">
        <h3>咨询会话</h3>
      </div>
      <div class="list-content">
        <div 
          v-for="chat in chatList" 
          :key="chat.userId"
          class="chat-item"
          :class="{ active: currentChat?.userId === chat.userId }"
          @click="selectChat(chat)"
        >
          <div class="chat-info">
            <div class="patient-info">
              <span class="patient-id">患者ID: {{ chat.userId }}</span>
              <el-tag 
                size="small" 
                :type="chat.hasUnread ? 'warning' : 'success'"
              >
                {{ chat.hasUnread ? '未回复' : '已回复' }}
              </el-tag>
            </div>
            <div class="last-message">{{ chat.lastMessage || '暂无内容' }}</div>
          </div>
          <div class="chat-time">{{ formatTime(chat.lastTime) }}</div>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-area">
      <template v-if="currentChat">
        <div class="chat-header">
          <span>患者ID: {{ currentChat.userId }} 的咨询记录</span>
        </div>
        <div class="message-list" ref="messageListRef">
          <div 
            v-for="msg in messageList" 
            :key="msg.consultId"
            class="message-group"
          >
            <!-- 患者咨询内容 -->
            <div class="message-item patient-message">
              <div class="message-info">
                <span class="message-sender">患者ID: {{ msg.userId }}</span>
                <span class="message-time">{{ formatTime(msg.consultTime) }}</span>
              </div>
              <div class="message-content">{{ msg.content }}</div>
            </div>
            
            <!-- 医生回复内容 -->
            <div v-if="msg.replyContent" class="message-item doctor-message">
              <div class="message-info">
                <span class="message-sender">医生回复</span>
                <span class="message-time">{{ formatTime(msg.replyTime) }}</span>
              </div>
              <div class="message-content">{{ msg.replyContent }}</div>
            </div>
          </div>
        </div>
        <div class="message-input">
          <el-input
            v-model="replyText"
            type="textarea"
            :rows="3"
            placeholder="请输入回复内容"
          />
          <el-button type="primary" @click="handleReply">回复</el-button>
        </div>
      </template>
      <div v-else class="no-chat">
        请选择一个会话
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'
import { getUserId, getRole } from '@/utils/auth'

// 检查用户角色和ID
const role = getRole()
const doctorId = getUserId()

// 添加权限检查
if (!doctorId || role !== 'doctor') {
  ElMessage.error('无法获取医生信息，请重新登录')
}

// 会话列表
const chatList = ref([])
const currentChat = ref(null)
const messageList = ref([])
const replyText = ref('')
const messageListRef = ref(null)

// 获取会话列表
const getChatList = async () => {
  if (!doctorId) return
  
  try {
    const res = await request.get(`/consultations/doctor/${doctorId}`)
    if (res.code === 1) {
      // 按病人ID分组整理会话列表
      const groupedChats = res.data.reduce((acc, curr) => {
        if (!acc[curr.userId]) {
          acc[curr.userId] = {
            userId: curr.userId,
            lastMessage: curr.content,
            lastTime: curr.consultTime,
            hasUnread: curr.status !== '已回复'
          }
        } else {
          // 更新最后消息时间
          if (dayjs(curr.consultTime).isAfter(acc[curr.userId].lastTime)) {
            acc[curr.userId].lastMessage = curr.content
            acc[curr.userId].lastTime = curr.consultTime
          }
          // 更新未读状态
          if (curr.status !== '已回复') {
            acc[curr.userId].hasUnread = true
          }
        }
        return acc
      }, {})
      
      // 转换为数组并按最后消息时间排序
      chatList.value = Object.values(groupedChats).sort((a, b) => 
        dayjs(b.lastTime).valueOf() - dayjs(a.lastTime).valueOf()
      )
    }
  } catch (error) {
    console.error('获取会话列表失败:', error)
  }
}

// 选择会话
const selectChat = async (chat) => {
  currentChat.value = chat
  replyText.value = ''
  await getMessages(chat.userId)
}

// 获取消息记录
const getMessages = async (userId) => {
  if (!doctorId) return
  
  try {
    const res = await request.get(`/consultations/user/${userId}`, {
      params: { doctorId }
    })
    if (res.code === 1) {
      // 只过滤出当前医生的对话或未分配医生的新咨询
      const filteredMessages = res.data.filter(msg => 
        !msg.doctorId || // 未分配医生的新咨询
        msg.doctorId === parseInt(doctorId) // 当前医生的对话
      )
      
      // 按时间升序排列
      messageList.value = filteredMessages.sort((a, b) => {
        const timeA = dayjs(a.consultTime)
        const timeB = dayjs(b.consultTime)
        return timeA.isBefore(timeB) ? -1 : 1
      })
      
      // 等待DOM更新后滚动到底部
      await nextTick()
      if (messageListRef.value) {
        messageListRef.value.scrollTop = messageListRef.value.scrollHeight
      }
    }
  } catch (error) {
    console.error('获取消息记录失败:', error)
    ElMessage.error('获取消息记录失败')
  }
}

// 回复消息
const handleReply = async () => {
  if (!replyText.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    // 获取当前未回复的咨询
    const unreadConsult = messageList.value.find(msg => msg.status !== '已回复')
    if (!unreadConsult) {
      ElMessage.warning('没有待回复的咨询')
      return
    }

    const res = await request.put(`/consultations/${unreadConsult.consultId}`, {
      status: '已回复',
      replyContent: replyText.value
    })

    if (res.code === 1) {
      ElMessage.success('回复成功')
      replyText.value = ''
      // 更新消息列表和会话列表
      await getMessages(currentChat.value.userId)
      await getChatList()
    }
  } catch (error) {
    console.error('回复失败:', error)
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  getChatList()
})
</script>

<style scoped>
.consultation {
  height: 100%;
  padding: 20px;
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 20px;
}

.chat-list {
  background: white;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.list-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.list-header h3 {
  margin: 0;
}

.list-content {
  flex: 1;
  overflow-y: auto;
}

.chat-item {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
}

.chat-item:hover {
  background-color: #f5f7fa;
}

.chat-item.active {
  background-color: #ecf5ff;
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.patient-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.patient-id {
  font-weight: bold;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.last-message {
  color: #999;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-time {
  font-size: 12px;
  color: #999;
  margin-left: 10px;
}

.chat-area {
  background: white;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.message-list {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 15px;
  background: #f7f7f7;
  border-radius: 8px;
  width: 100%;
}

.message-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.message-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.message-sender {
  font-weight: bold;
}

.message-time {
  color: #999;
}

.patient-message .message-content {
  background: #fff;
  padding: 10px 15px;
  border-radius: 4px;
  word-break: break-all;
}

.doctor-message .message-content {
  background: #ecf5ff;
  color: #409EFF;
  padding: 10px 15px;
  border-radius: 4px;
  word-break: break-all;
}

.message-input {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.message-input .el-button {
  align-self: flex-end;
}

.no-chat {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
}

.message-mine,
.message-reply {
  display: none;
}
</style> 

