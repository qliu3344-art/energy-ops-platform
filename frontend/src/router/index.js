import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'
import DeviceList from '../views/DeviceList.vue'
import WorkOrderBoard from '../views/WorkOrderBoard.vue'
import AlarmList from '../views/AlarmList.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login },
    {
      path: '/',
      component: Layout,
      redirect: '/devices',
      children: [
        { path: 'devices', component: DeviceList, meta: { title: '设备台账' } },
        { path: 'work-orders', component: WorkOrderBoard, meta: { title: '巡检工单' } },
        { path: 'alarms', component: AlarmList, meta: { title: '设备告警' } }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    return '/login'
  }
  if (to.path === '/login' && token) {
    return '/devices'
  }
  return true
})

export default router
