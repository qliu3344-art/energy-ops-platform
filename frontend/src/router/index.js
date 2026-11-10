import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'
import DeviceList from '../views/DeviceList.vue'
import WorkOrderBoard from '../views/WorkOrderBoard.vue'
import AlarmList from '../views/AlarmList.vue'

const router = createRouter({
  // HTML5 History 模式：URL 里没有 #，需要后端把所有路径回退到 index.html
  history: createWebHistory(),
  routes: [
    // 登录页独立，不带侧边栏
    { path: '/login', component: Login },
    {
      // 业务页面统一挂在 Layout 下：Layout 提供侧边栏，内容区由 <router-view> 渲染子路由
      path: '/',
      component: Layout,
      redirect: '/devices',        // 访问根路径时直接跳到设备台账
      children: [
        // 子路由的 path 不带开头的斜杠，浏览器地址栏实际是 /devices
        { path: 'devices', component: DeviceList, meta: { title: '设备台账' } },
        { path: 'work-orders', component: WorkOrderBoard, meta: { title: '巡检工单' } },
        { path: 'alarms', component: AlarmList, meta: { title: '设备告警' } }
      ]
    }
  ]
})

/**
 * 全局前置守卫：每次跳转前都会执行。
 * 返回路径字符串 = 改道；返回 true = 放行。
 */
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  // 没登录却想进业务页 → 踢回登录页，并记住原本要去的页面
  if (to.path !== '/login' && !token) {
    sessionStorage.setItem('redirectAfterLogin', to.fullPath)
    return '/login'
  }
  // 已经登录还去登录页 → 直接进首页
  if (to.path === '/login' && token) {
    return '/devices'
  }
  return true
})

export default router
