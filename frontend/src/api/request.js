import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * 全局唯一的 axios 实例。
 *
 * baseURL 是 /api，开发环境下由 Vite 的代理转发到 8080 端口，
 * 浏览器看来请求还是发给 5173，所以不会触发跨域。
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 10000        // 超过 10 秒按失败处理，避免请求一直挂着
})

/**
 * 请求拦截器：每次请求发出前自动带上令牌。
 * 业务代码里就不用每个接口都手动加一次 Authorization 头。
 */
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

/**
 * 响应拦截器：统一拆包、统一报错。
 *
 * 后端固定返回 {code, msg, data}，这里把 data 直接返回给页面，
 * 页面里 await getDevices() 拿到的就是业务数据，不用层层 .data.data。
 */
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data       // 成功：只把 data 交出去
    }
    // 业务失败（比如编码重复）：弹提示，并让调用方的 catch 能接住
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    // HTTP 层失败：401 表示令牌失效，清掉本地令牌回登录页
    if (error.response && error.response.status === 401) {
      // 记住当前页面，重新登录后回到这里（表单草稿由各页面自己保存）
      const path = window.location.pathname + window.location.search
      if (path !== '/login') {
        sessionStorage.setItem('redirectAfterLogin', path)
      }
      localStorage.removeItem('token')
      ElMessage.error('登录已过期，请重新登录')
      window.location.href = '/login'
    } else {
      // 其余错误尽量把后端给的提示透出来，没有就用兜底文案
      const msg = error.response?.data?.msg || error.message || '网络异常'
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default request
