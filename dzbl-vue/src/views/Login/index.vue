<template>
  <div class="login-container">
    <div class="login-box">
      <div class="title">电子病历管理系统</div>
      
      <!-- 切换按钮 -->
      <div class="switch-buttons">
        <el-button 
          :type="isLogin ? 'primary' : 'default'"
          @click="isLogin = true"
        >
          登录
        </el-button>
        <el-button 
          :type="!isLogin ? 'primary' : 'default'"
          @click="isLogin = false"
        >
          注册
        </el-button>
      </div>
      
      <el-form 
        ref="formRef"
        :model="formData"
        :rules="formRules"
        class="form"
      >
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input
            v-model="formData.username"
            :placeholder="isLogin ? '请输入用户名' : '请输入注册用户名'"
            prefix-icon="User"
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            :placeholder="isLogin ? '请输入密码' : '请输入注册密码'"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <!-- 确认密码(注册时显示) -->
        <el-form-item 
          v-if="!isLogin"
          prop="confirmPassword"
        >
          <el-input
            v-model="formData.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <!-- 身份选择 -->
        <el-form-item prop="role">
          <el-select
            v-model="formData.role"
            :placeholder="isLogin ? '请选择身份' : '请选择注册身份'"
            style="width: 100%"
          >
            <el-option label="医生" value="doctor" />
            <el-option label="病人" value="patient" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>

        <!-- 按钮 -->
        <el-form-item>
          <el-button 
            type="primary" 
            :loading="loading"
            class="submit-button"
            @click="handleSubmit"
          >
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { setToken } from '@/utils/auth'

const router = useRouter()
const loading = ref(false)
const formRef = ref()
const isLogin = ref(true)

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  role: ''
})


// 表单验证规则
const formRules = computed(() => ({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应在3-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  confirmPassword: !isLogin.value ? [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== formData.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ] : [],
  role: [
    { required: true, message: '请选择身份', trigger: 'change' }
  ]
}))

// 处理提交
const handleSubmit = async () => {
  await formRef.value.validate()
  
  loading.value = true
  try {
    if (isLogin.value) {
      // 登录逻辑
      const apiMap = {
        doctor: '/doctor-accounts/login',
        patient: '/patient-accounts/login',
        admin: '/admin/login'
      }
      
      const { data } = await request.post(apiMap[formData.role], {
        username: formData.username,
        password: formData.password
      })
      
      setToken(data.token)
      
      ElMessage.success('登录成功')
      
      const routeMap = {
        doctor: '/doctor',
        patient: '/patient',
        admin: '/admin'
      }
      router.push(routeMap[formData.role])
    } else {
      // 注册逻辑
      const apiMap = {
        doctor: '/doctor-accounts',
        patient: '/patient-accounts',
        admin: '/admin-accounts'
      }
      
      await request.post(apiMap[formData.role], {
        username: formData.username,
        password: formData.password
      })
      
      ElMessage.success('注册成功')
      isLogin.value = true // 切换到登录
    }
  } catch (error) {
    console.error(isLogin.value ? '登录失败:' : '注册失败:', error)
    ElMessage.error(error.response?.data?.msg || '操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title {
  margin-bottom: 20px;
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.switch-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.form {
  margin-top: 20px;
}

.submit-button {
  width: 100%;
}

/* 输入框样式优化 */
:deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.8);
}

/* 按钮悬停效果 */
.submit-button:hover {
  opacity: 0.9;
  transform: translateY(-1px);
  transition: all 0.3s;
}

/* 添加响应式设计 */
@media screen and (max-width: 576px) {
  .login-box {
    width: 90%;
    padding: 20px;
  }
}
</style> 