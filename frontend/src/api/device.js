import request from './request'

// 设备相关接口。统一走 request 实例，令牌和错误处理都由拦截器兜住。

/** 分页查询：params 会被拼成 ?page=1&size=10&keyword=... */
export function getDevices(params) {
  return request.get('/devices', { params })
}

export function getDevice(id) {
  return request.get(`/devices/${id}`)
}

export function createDevice(data) {
  return request.post('/devices', data)
}

export function updateDevice(id, data) {
  return request.put(`/devices/${id}`, data)
}

export function deleteDevice(id) {
  return request.delete(`/devices/${id}`)
}
