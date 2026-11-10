<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout } from '../api/auth'

const route = useRoute()    // 当前路由信息，用来做菜单高亮和标题
const router = useRouter()  // 用来跳转
// computed：有缓存，只有 localStorage 里的用户名变化时才重新计算
const username = computed(() => localStorage.getItem('username') || 'admin')

async function handleLogout() {
  try {
    await logout()   // 通知后端把令牌从 Redis 删掉
  } catch (e) {
    // 令牌可能已过期，接口报错也不影响本地退出
  }
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<template>
  <!-- 整体布局：左侧菜单 + 右侧头部与内容区 -->
  <el-container class="layout">
    <el-aside width="210px" class="aside">
      <div class="logo">能源运维平台</div>
      <!-- router 模式：点菜单项相当于 router.push(index)；default-active 让当前页高亮 -->
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
        <!-- meta.title 在路由表里配置，切换页面时标题自动跟着变 -->
        <span class="title">{{ route.meta.title || '' }}</span>
        <span class="user">
          {{ username }}
          <el-button link type="primary" @click="handleLogout">退出</el-button>
        </span>
      </el-header>
      <el-main class="main">
        <!-- 子路由在这里渲染：/devices、/work-orders、/alarms 共用这个外壳 -->
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
/* :deep() 用于穿透 scoped，改 Element Plus 内部元素的样式 */
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
