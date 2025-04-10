<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserId, getUsername, removeToken } from '@/utils/auth'
import { 
  House,
  User, 
  Document, 
  ChatLineRound, 
  Link, 
  FirstAidKit,
  OfficeBuilding, 
  List, 
  Wallet,
  ArrowDown 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
// 修改 activeIndex 的初始化
const activeIndex = ref(localStorage.getItem('patientActiveMenu') || '/')

// 添加展开菜单状态追踪
const openedMenus = ref([])

// 监听菜单展开状态变化
const handleOpen = (index) => {
  const opened = openedMenus.value
  if (!opened.includes(index)) {
    opened.push(index)
  }
  localStorage.setItem('patientOpenedMenus', JSON.stringify(opened))
}

const handleClose = (index) => {
  const opened = openedMenus.value
  const i = opened.indexOf(index)
  if (i !== -1) {
    opened.splice(i, 1)
  }
  localStorage.setItem('patientOpenedMenus', JSON.stringify(opened))
}

// 在组件挂载时恢复展开状态
const initOpenedMenus = () => {
  const saved = localStorage.getItem('patientOpenedMenus')
  if (saved) {
    openedMenus.value = JSON.parse(saved)
  }
}

// 在组件挂载时初始化
initOpenedMenus()

// 修改 handleSelect 方法
const handleSelect = (key) => {
  activeIndex.value = key
  localStorage.setItem('patientActiveMenu', key)
}

// 添加路由监听
router.afterEach((to) => {
  activeIndex.value = to.path
  localStorage.setItem('patientActiveMenu', to.path)
})

// 获取当前页面标题
const currentPageTitle = computed(() => {
  const routeMap = {
    '/patient': '首页',
    '/patient/personal-info': '个人信息',
    '/patient/health-card': '健康卡',
    '/patient/online-consultation': '在线咨询',
    '/patient/account-binding': '账号关联',
    '/patient/outpatient/appointment': '预约挂号',
    '/patient/outpatient/records': '挂号记录',
    '/patient/outpatient/reports': '门诊报告查询',
    '/patient/deepseek/basic': '基础模型',
    '/patient/deepseek/r1': 'R1模型',
    '/patient/deepseek/text': '文字提取',
    '/patient/deepseek/local': '本地模型',
    // ... 其他路由映射
  }
  return routeMap[route.path] || '首页'
})

// 处理退出登录
const handleLogout = () => {
  removeToken()
  router.push('/login')
}
</script>

<template>
  <div class="main-container">
    <!-- 顶部标题栏 -->
    <div class="header">
      <div class="title">电子病历管理系统</div>
      <div class="user-info">
        <el-dropdown>
          <span class="user-name">
            <span>ID {{ getUserId() }}: {{ getUsername() }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 导航栏 -->
    <div class="nav-bar">
      <div class="page-title">{{ currentPageTitle }}</div>
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>病人端</el-breadcrumb-item>
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
          <el-menu-item index="/patient">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          
          <el-menu-item index="/patient/personal-info">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          
          <el-menu-item index="/patient/health-card">
            <el-icon><Document /></el-icon>
            <span>健康卡</span>
          </el-menu-item>
          
          <el-menu-item index="/patient/online-consultation">
            <el-icon><ChatLineRound /></el-icon>
            <span>在线咨询</span>
          </el-menu-item>
          
          <el-menu-item index="/patient/account-binding">
            <el-icon><Link /></el-icon>
            <span>账号关联</span>
          </el-menu-item>
          
          <el-sub-menu index="6">
            <template #title>
              <el-icon><FirstAidKit /></el-icon>
              <span>门诊服务</span>
            </template>
            <el-menu-item index="/patient/outpatient/appointment">预约挂号</el-menu-item>
            <el-menu-item index="/patient/outpatient/records">挂号记录</el-menu-item>
            <el-menu-item index="/patient/outpatient/reports">门诊报告查询</el-menu-item>
            <el-menu-item index="/patient/outpatient/check-appointment">检查预约</el-menu-item>
            <el-menu-item index="/patient/outpatient/check-records">检查记录</el-menu-item>
            <el-menu-item index="/patient/outpatient/check-reports">检查报告查询</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="7">
            <template #title>
              <el-icon><OfficeBuilding /></el-icon>
              <span>住院服务</span>
            </template>
            <el-menu-item index="/patient/inpatient/records">住院记录</el-menu-item>
            <el-menu-item index="/patient/inpatient/reports">住院报告查询</el-menu-item>
            <el-menu-item index="/patient/inpatient/surgery">手术信息查询</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="8">
            <template #title>
              <el-icon><List /></el-icon>
              <span>体检服务</span>
            </template>
            <el-menu-item index="/patient/physical/appointment">体检预约</el-menu-item>
            <el-menu-item index="/patient/physical/records">体检记录</el-menu-item>
            <el-menu-item index="/patient/physical/reports">体检报告查询</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="9">
            <template #title>
              <el-icon><Wallet /></el-icon>
              <span>缴费窗口</span>
            </template>
            <el-menu-item index="/patient/payment/outpatient">门诊缴费</el-menu-item>
            <el-menu-item index="/patient/payment/inpatient">住院缴费</el-menu-item>
            <el-menu-item index="/patient/payment/physical">体检缴费</el-menu-item>
            <el-menu-item index="/patient/payment/exam">检查缴费</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="10">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>Deepseek医疗助手</span>
            </template>
            <el-menu-item index="/patient/deepseek/basic">基础模型</el-menu-item>
            <el-menu-item index="/patient/deepseek/r1">R1模型</el-menu-item>
            <el-menu-item index="/patient/deepseek/text">文字提取</el-menu-item>
            <el-menu-item index="/patient/deepseek/local">本地模型</el-menu-item>
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

.user-name {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}
</style> 