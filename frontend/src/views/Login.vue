<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: '123456'
})

async function submit() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await login(form)
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    ElMessage.success('登录成功')
    router.push('/devices')
  } catch (e) {
    // 错误提示已由 axios 响应拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2>能源场站设备运维管理平台</h2>
      <p class="sub">设备台账 · 巡检工单 · 告警处理</p>
      <el-form :model="form" label-position="top" @submit.prevent="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="admin" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="123456" />
        </el-form-item>
        <el-button type="primary" style="width: 100%" :loading="loading" @click="submit">
          登录
        </el-button>
      </el-form>
      <p class="hint">演示账号：admin / 123456</p>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f2d3d 0%, #2b5876 100%);
}
.login-card {
  width: 380px;
  padding: 10px 18px 4px;
}
h2 {
  margin: 6px 0 4px;
  font-size: 20px;
  color: #1f2d3d;
}
.sub {
  margin: 0 0 22px;
  color: #909399;
  font-size: 13px;
}
.hint {
  margin: 14px 0 4px;
  color: #c0c4cc;
  font-size: 12px;
  text-align: center;
}
</style>
