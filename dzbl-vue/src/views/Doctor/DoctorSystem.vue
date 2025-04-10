<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  House,
  User, 
  ChatLineRound,
  Document,
  FirstAidKit,
  Notebook,
  DocumentCopy
} from '@element-plus/icons-vue'
import { removeToken, getUserId, getUsername } from '@/utils/auth'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const activeIndex = ref(localStorage.getItem('doctorActiveMenu') || '/')
// 添加新的 ref 来跟踪展开的子菜单
const openedMenus = ref([])

// 监听菜单展开状态变化
const handleOpen = (index) => {
  const opened = openedMenus.value
  if (!opened.includes(index)) {
    opened.push(index)
  }
  localStorage.setItem('doctorOpenedMenus', JSON.stringify(opened))
}

const handleClose = (index) => {
  const opened = openedMenus.value
  const i = opened.indexOf(index)
  if (i !== -1) {
    opened.splice(i, 1)
  }
  localStorage.setItem('doctorOpenedMenus', JSON.stringify(opened))
}

// 在组件挂载时恢复展开状态
const initOpenedMenus = () => {
  const saved = localStorage.getItem('doctorOpenedMenus')
  if (saved) {
    openedMenus.value = JSON.parse(saved)
  }
}

// 在组件挂载时初始化
initOpenedMenus()

// 获取当前页面标题
const currentPageTitle = computed(() => {
  const routeMap = {
    '/doctor': '首页',
    '/doctor/personal-info': '个人信息',
    '/doctor/consultation': '咨询服务',
    '/doctor/patient/outpatient': '门诊病人',
    '/doctor/patient/inpatient': '住院病人',
    '/doctor/medical-record/outpatient': '门诊病历管理',
    '/doctor/medical-record/inpatient': '住院病历管理',
    '/doctor/visit/outpatient': '门诊出诊',
    '/doctor/visit/check': '检查出诊',
    '/doctor/visit/inpatient': '住院出诊',
    '/doctor/visit/physical': '体检出诊',
    '/doctor/progress/outpatient': '门诊病程',
    '/doctor/progress/inpatient': '住院病程',
    '/doctor/report/outpatient': '门诊报告管理',
    '/doctor/report/check': '检查报告管理',
    '/doctor/report/inpatient': '住院报告管理',
    '/doctor/report/physical': '体检报告管理',
  }
  return routeMap[route.path] || '首页'
})

// 获取当前用户信息
const currentUser = computed(() => {
  const userId = getUserId()
  const username = getUsername()
  return `${userId}：${username}`
})

const handleSelect = (key) => {
  activeIndex.value = key
  localStorage.setItem('doctorActiveMenu', key)
}

// 退出登录处理
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    router.push('/login')
  }).catch(() => {})
}

// 添加路由监听，确保路由变化时也更新 activeIndex
router.afterEach((to) => {
  activeIndex.value = to.path
  localStorage.setItem('doctorActiveMenu', to.path)
})
</script>

<template>
  <div class="main-container">
    <!-- 顶部标题栏 -->
    <div class="header">
      <div class="title">电子病历管理系统</div>
      <div class="user-info">
        <!-- 退出到/login -->
        <el-link type="primary" @click="handleLogout">退出登录【{{ currentUser }}】</el-link>
      </div>
    </div>

    <!-- 导航栏 -->
    <div class="nav-bar">
      <div class="page-title">{{ currentPageTitle }}</div>
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>医生端</el-breadcrumb-item>
          <template v-if="route.matched && route.matched.length > 1">
            <el-breadcrumb-item v-for="(item, index) in route.matched.slice(1)" 
                               :key="index">
              {{ item.meta.title || item.name }}
            </el-breadcrumb-item>
          </template>
        </el-breadcrumb>
      </div>
    </div>

    <div class="content-container">
      <!-- 左侧菜单 -->
      <div class="left-menu">
        <el-menu
          :default-active="activeIndex"
          :default-openeds="openedMenus"
          class="el-menu-vertical"
          @select="handleSelect"
          @open="handleOpen"
          @close="handleClose"
          :router="true"
          background-color="#409EFF"
          text-color="#fff"
          active-text-color="#ffd04b"
        >
          <el-menu-item index="/doctor">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          
          <el-menu-item index="/doctor/personal-info">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          
          <el-menu-item index="/doctor/consultation">
            <el-icon><ChatLineRound /></el-icon>
            <span>咨询服务</span>
          </el-menu-item>
          
          <el-sub-menu index="4">
            <template #title>
              <el-icon><User /></el-icon>
              <span>病人管理</span>
            </template>
            <el-menu-item index="/doctor/patient/outpatient">门诊病人</el-menu-item>
            <el-menu-item index="/doctor/patient/inpatient">住院病人</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="5">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>病历管理</span>
            </template>
            <el-menu-item index="/doctor/medical-record/outpatient">门诊病历管理</el-menu-item>
            <el-menu-item index="/doctor/medical-record/inpatient">住院病历管理</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="6">
            <template #title>
              <el-icon><FirstAidKit /></el-icon>
              <span>出诊管理</span>
            </template>
            <el-menu-item index="/doctor/visit/outpatient">门诊出诊</el-menu-item>
            <el-menu-item index="/doctor/visit/check">检查出诊</el-menu-item>
            <el-menu-item index="/doctor/visit/inpatient">住院出诊</el-menu-item>
            <el-menu-item index="/doctor/visit/physical">体检出诊</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="7">
            <template #title>
              <el-icon><Notebook /></el-icon>
              <span>病程记录</span>
            </template>
            <el-menu-item index="/doctor/progress/outpatient">门诊病程</el-menu-item>
            <el-menu-item index="/doctor/progress/inpatient">住院病程</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="8">
            <template #title>
              <el-icon><DocumentCopy /></el-icon>
              <span>报告管理</span>
            </template>
            <el-menu-item index="/doctor/report/outpatient">门诊报告管理</el-menu-item>
            <el-menu-item index="/doctor/report/check">检查报告管理</el-menu-item>
            <el-menu-item index="/doctor/report/inpatient">住院报告管理</el-menu-item>
            <el-menu-item index="/doctor/report/physical">体检报告管理</el-menu-item>
          </el-sub-menu>

          <!-- 添加新的报告制作菜单项 -->
          <el-sub-menu index="9">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>病历生成助手</span>
            </template>
            <el-menu-item-group title="报告制作">
              <el-menu-item index="/doctor/file-make/outpatient-report">门诊报告制作</el-menu-item>
              <el-menu-item index="/doctor/file-make/inpatient-report">住院报告制作</el-menu-item>
              <el-menu-item index="/doctor/file-make/check-report">检查报告制作</el-menu-item>
              <el-menu-item index="/doctor/file-make/physical-report">体检报告制作</el-menu-item>
            </el-menu-item-group>
            <el-menu-item-group title="病程制作">
              <el-menu-item index="/doctor/file-make/outpatient-progress">门诊病程制作</el-menu-item>
              <el-menu-item index="/doctor/file-make/inpatient-progress">住院病程制作</el-menu-item>
            </el-menu-item-group>
            <el-menu-item-group title="病历制作">
              <el-menu-item index="/doctor/file-make/outpatient-record">门诊病历制作</el-menu-item>
              <el-menu-item index="/doctor/file-make/inpatient-record">住院病历制作</el-menu-item>
            </el-menu-item-group>
          </el-sub-menu>
        </el-menu>
      </div>

      <!-- 主要内容区域 -->
      <div class="main-content">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 重置默认边距和背景色 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 设置全局背景色 */
:root {
  background-color: #f5f7fa;
}

.main-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.header {
  background-color: #1e4c9c;
  width: 100%;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: white;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.title {
  font-family: KaiTi, "楷体";
  font-size: 30px;
  color: #ffffff;
}

.user-info :deep(.el-link) {
  color: #ffffff;
}

.nav-bar {
  position: fixed;
  top: 60px;
  left: 0;
  right: 0;
  height: 50px;
  background-color: #409EFF;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: white;
  z-index: 999;
}

.page-title {
  font-size: 16px;
  font-weight: bold;
}

.breadcrumb {
  :deep(.el-breadcrumb) {
    color: white;
  }
  
  :deep(.el-breadcrumb__item) {
    color: white;
  }

  :deep(.el-breadcrumb__inner) {
    color: white;
  }

  :deep(.el-breadcrumb__separator) {
    color: white;
  }
}

.content-container {
  display: flex;
  margin-top: 110px;
  height: calc(100vh - 110px);
  background-color: #f5f7fa;
  width: 100%;
}

.left-menu {
  width: 200px;
  background-color: #409EFF;
  height: calc(100vh - 110px);
  position: fixed;
  left: 0;
  top: 110px;
  bottom: 0;
  overflow-y: auto;
}

.main-content {
  margin-left: 200px;
  width: calc(100% - 200px);
  height: calc(100vh - 110px);
  background-color: #f5f7fa;
  overflow: auto;
  box-sizing: border-box;
}

/* 自定义滚动条样式 */
.left-menu::-webkit-scrollbar {
  width: 6px;
}

.left-menu::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.left-menu::-webkit-scrollbar-track {
  background-color: transparent;
}

.el-menu-vertical {
  border-right: none;
  height: auto;
  min-height: 100%;
  width: 200px;
}

/* 确保菜单项不会被压缩 */
:deep(.el-menu-item), :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
}

/* 移除菜单hover时的背景色 */
:deep(.el-menu-item:hover), :deep(.el-sub-menu__title:hover) {
  background-color: #66b1ff !important;
}

/* 设置选中菜单项的背景色 */
:deep(.el-menu-item.is-active) {
  background-color: #66b1ff !important;
}

/* 重要：移除所有可能引入白色背景的样式 */
:deep(.el-main),
:deep(.el-container),
:deep(.router-view),
:deep(.el-menu),
:deep(.el-menu-item),
:deep(.el-sub-menu),
:deep(.el-sub-menu__title) {
  background-color: transparent !important;
}

/* 添加全局样式覆盖 */
:global(body) {
  background-color: #f5f7fa !important;
}

:global(#app) {
  max-width: none !important;
  margin: 0 !important;
  padding: 0 !important;
  background-color: #f5f7fa !important;
}
</style> 