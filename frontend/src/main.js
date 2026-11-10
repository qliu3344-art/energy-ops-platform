import { createApp } from 'vue'
// 全量引入 Element Plus（方便，但会把所有组件打进包里，体积偏大）
// 优化方向：改成按需引入，用 unplugin-vue-components 自动导入用到的组件
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import App from './App.vue'
import router from './router'

// 创建应用实例，挂上 UI 库和路由，然后挂载到 index.html 里的 #app
const app = createApp(App)
app.use(ElementPlus, { locale: zhCn })   // locale 让分页、日期等组件显示中文
app.use(router)
app.mount('#app')
