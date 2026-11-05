<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout } from '../api/auth'

const route = useRoute()
const router = useRouter()
const username = computed(() => localStorage.getItem('username') || 'admin')

async function handleLogout() {
  try {
    await logout()
  } catch (e) {
    // 令牌可能已过期，忽略
  }
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="210px" class="aside">
      <div class="logo">能源运维平台</div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#1f2d3d"
        text-color="#c0c4cc"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/devices">设备台账</el-menu-item>
        <el-menu-item index="/work-orders">巡检工单</el-menu-item>
        <el-menu-item index="/alarms">设备告警</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span class="title">{{ route.meta.title || '' }}</span>
        <span class="user">
          {{ username }}
          <el-button link type="primary" @click="handleLogout">退出</el-button>
        </span>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100%;
}
.aside {
  background: #1f2d3d;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 1px;
}
.aside :deep(.el-menu) {
  border-right: none;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}
.title {
  font-size: 16px;
  font-weight: 600;
}
.user {
  color: #606266;
  font-size: 14px;
}
.main {
  background: #f5f7fa;
}
</style>
