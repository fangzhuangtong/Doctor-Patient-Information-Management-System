<template>
  <el-container class="admin-container">
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <el-menu
        :default-active="activeIndex"
        :default-openeds="openedMenus"
        class="menu"
        @select="handleSelect"
        @open="handleOpen"
        @close="handleClose"
        router
      >
        <el-menu-item index="/admin" :icon="House">
          首页
        </el-menu-item>
        
        <el-menu-item index="/admin/doctor-accounts" :icon="User">
          医生账号管理
        </el-menu-item>
        
        <el-menu-item index="/admin/patient-accounts" :icon="User">
          病人账号管理
        </el-menu-item>
        
        <el-menu-item index="/admin/schedules" :icon="Calendar">
          出诊排班管理
        </el-menu-item>
        
        <el-menu-item index="/admin/surgeries" :icon="FirstAidKit">
          手术信息管理
        </el-menu-item>
        
        <el-menu-item index="/admin/orders" :icon="Wallet">
          费用订单管理
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主要内容区 -->
    <el-container>
      <el-header>
        <div class="header-title">
          {{ currentPageTitle }}
        </div>
        <div class="user-info">
          <el-dropdown>
            <span class="user-name">
              管理员
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  House,
  User,
  Calendar,
  FirstAidKit,
  Wallet,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const activeIndex = ref(localStorage.getItem('adminActiveMenu') || '/admin')

// 添加展开菜单状态追踪
const openedMenus = ref([])

// 监听菜单展开状态变化
const handleOpen = (index) => {
  const opened = openedMenus.value
  if (!opened.includes(index)) {
    opened.push(index)
  }
  localStorage.setItem('adminOpenedMenus', JSON.stringify(opened))
}

const handleClose = (index) => {
  const opened = openedMenus.value
  const i = opened.indexOf(index)
  if (i !== -1) {
    opened.splice(i, 1)
  }
  localStorage.setItem('adminOpenedMenus', JSON.stringify(opened))
}

// 在组件挂载时恢复展开状态
const initOpenedMenus = () => {
  const saved = localStorage.getItem('adminOpenedMenus')
  if (saved) {
    openedMenus.value = JSON.parse(saved)
  }
}

// 在组件挂载时初始化
initOpenedMenus()

// 添加 handleSelect 方法
const handleSelect = (key) => {
  activeIndex.value = key
  localStorage.setItem('adminActiveMenu', key)
}

// 添加路由监听
router.afterEach((to) => {
  activeIndex.value = to.path
  localStorage.setItem('adminActiveMenu', to.path)
})

// 获取当前页面标题
const currentPageTitle = computed(() => {
  const routeMap = {
    '/admin': '首页',
    '/admin/doctor-accounts': '医生账号管理',
    '/admin/patient-accounts': '病人账号管理',
    '/admin/schedules': '出诊排班管理',
    '/admin/surgeries': '手术信息管理',
    '/admin/orders': '费用订单管理'
  }
  return routeMap[route.path] || ''
})

// 处理退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  router.push('/login')
}
</script>

<style scoped>
.admin-container {
  width: 100%;
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  height: 100vh;
}

.menu {
  height: 100%;
  border-right: none;
}

.el-header {
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  padding: 0 20px;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
}

/* 菜单样式 */
:deep(.el-menu) {
  background-color: #304156;
}

:deep(.el-menu-item) {
  color: #bfcbd9;
}

:deep(.el-menu-item.is-active) {
  color: #409EFF;
  background-color: #263445;
}

:deep(.el-menu-item:hover) {
  color: #fff;
  background-color: #263445;
}
</style> 