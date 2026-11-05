import request from './request'

export function getWorkOrders(params) {
  return request.get('/work-orders', { params })
}

export function createWorkOrder(data) {
  return request.post('/work-orders', data)
}

export function transitionWorkOrder(id, data) {
  return request.put(`/work-orders/${id}/status`, data)
}
