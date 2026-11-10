import request from './request'

// 设备类型字典接口。读接口走后端 Redis 缓存，前端下拉框每次打开都调它。

export function getDeviceTypes() {
  return request.get('/device-types')
}

export function createDeviceType(data) {
  return request.post('/device-types', data)
}

export function updateDeviceType(id, data) {
  return request.put(`/device-types/${id}`, data)
}

export function deleteDeviceType(id) {
  return request.delete(`/device-types/${id}`)
}
