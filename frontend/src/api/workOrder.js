import request from './request'

// 工单相关接口

/** 分页查询工单，可按状态、类型、关键字筛选 */
export function getWorkOrders(params) {
  return request.get('/work-orders', { params })
}

export function createWorkOrder(data) {
  return request.post('/work-orders', data)
}

/**
 * 工单状态流转。data 形如 { toStatus: 1, operator: 'admin', remark: '', assignee: '张工' }
 * 其中 assignee 在派发（0→1）时必填。
 * 能不能流转由后端状态机判断，前端只负责发起。
 */
export function transitionWorkOrder(id, data) {
  return request.put(`/work-orders/${id}/status`, data)
}
